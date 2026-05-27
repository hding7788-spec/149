---
name: wtpart-development
description: 在 149 Windchill 定制工程中编写 WTPart（零部件）相关代码时的开发规范。涉及 WTPart 的查询/创建/修改、版本与视图（Design/Manufacturing）、BOM 结构遍历（uses/usedBy/UsageLink）、关联文档（DescribeLink/ReferenceLink）、软属性（IBA）、以及 PersistenceHelper / WTPartHelper / VersionControlHelper 等 OOTB API 的用法时使用。参考实现：ext.casc.part.CSCPart 与 ext.casc.common.PartCommonHelper。
---

# WTPart 开发规范（149 工程）

本规范从 `PDM/src/ext/casc/part/CSCPart.java` 与 `PDM/src/ext/casc/common/PartCommonHelper.java`
提炼。两者都是**全 static 的面向过程 Helper**——`CSCPart` 负责 WTPart 的增删改查与文档关联，
`PartCommonHelper` 负责结构关系与关联文档查询。新增 WTPart 逻辑时**优先复用这两个类的已有方法**，
不要重复造轮子。

> 约定：本文“现状”= 工程里既有写法（保持一致即可），“建议”= 新代码应遵循的改进点。

---

## 1. 查询：QuerySpec + SearchCondition

**现状（标准写法）**

```java
QuerySpec qs = new QuerySpec(WTPartMaster.class);
SearchCondition sc = new SearchCondition(
        WTPartMaster.class, WTPartMaster.NUMBER, SearchCondition.EQUAL, partNumber, false);
qs.appendSearchCondition(sc);
QueryResult qr = PersistenceHelper.manager.find(qs);
while (qr.hasMoreElements()) {
    WTPartMaster m = (WTPartMaster) qr.nextElement();
}
```

要点：
- 多条件用 `qs.appendAnd()` 连接；按内部属性路径过滤时用字符串列名，例如
  `"view.key.id"`、`"containerReference.key.id"`、`"versionInfo.identifier.versionId"`、
  `"iterationInfo.identifier.iterationId"`、`"roleAObjectRef.key.id"`。
- 跨视图/高级查询需 `qs.setAdvancedQueryEnabled(true)`，并把 `qs` 转 `StatementSpec` 传入 `find`。
- 取最新版本：对 `QueryResult` 套 `new LatestConfigSpec().process(qr)`。

**建议**
- 用 `WTPartMaster.NUMBER` 这类常量代替魔法字符串列名（无常量时再用字符串路径）。
- 不要 `catch(Exception)` 后返回 `null` 吞掉异常（见 §7）。

---

## 2. Master / Iteration / 版本视图

WTPart 是版本受控对象，区分 **Master（主对象）** 与 **Iteration（具体版次）**：

| 目的 | 方法 |
| --- | --- |
| 按编号取 Master | `CSCPart.getPartMasterByNumber(number)` → `WTPartMaster` |
| 按编号取某个版本 | `CSCPart.getPartByNumber(number)` |
| 按编号取**最新版（管理员权限）** | `CSCPart.getPart(number)` |
| 按编号 + 视图取最新版 | `CSCPart.getPartByNumberAndViewName(number, viewName)` |
| 取 Master 最新版次 | `CSCPart.getLatestObject(master)` |
| 取检出工作副本 | `CSCPart.getWorkingCopyOfPart(part)` |

底层 API：`VersionControlHelper.service.allVersionsOf / allIterationsOf / getLatestIteration`。

**视图约定**：本工程主要用 `Design`（设计）与 `Manufacturing`（制造）两个视图。遍历 BOM 时常需把
Design 件切到 Manufacturing 件（见 `PartCommonHelper.getChildMParts`）。新建零件默认视图为 `Design`。

---

## 3. 管理员权限提升（重要约定）

后台/集成代码在查询或写库时，需临时提权到管理员，并在 `finally` 中**还原**原用户，避免污染会话：

```java
String user = "";
try {
    user = wt.session.SessionHelper.manager.getPrincipal().getName();
    wt.session.SessionHelper.manager.setAdministrator();
    // ... 业务逻辑 ...
} finally {
    if (user != null && !user.equals("")) {
        try { wt.session.SessionHelper.manager.setPrincipal(user); } catch (Exception e) {}
    }
}
```

参见 `CSCPart.getPart / getPartsLikeNumber / getEndItemPartByContext`。
**建议**：务必把还原放在 `finally`，不要漏掉还原（现状中部分方法的还原不在 finally，属待改进）。

---

## 4. 创建 WTPart

标准流程（参见 `CSCPart.createPart` 的几个重载）：

```java
WTPart part = WTPart.newWTPart();
part.setName(name);
part.setNumber(number);
part.setSource(Source.toSource(source));        // make / buy ...
part.setPartType(PartType.toPartType(type));    // separable / assembled ...
part.setDefaultUnit(QuantityUnit.toQuantityUnit(unit));
part.setContainerReference(containerRef);

if (partView != null)  ViewHelper.assignToView(part, partView);          // 视图
if (folder != null)    FolderHelper.assignLocation(part, folder);        // 文件夹
if (lifecycleTemplate != null)
    part = (WTPart) LifeCycleHelper.setLifeCycle((LifeCycleManaged) part, lifecycleTemplate);

part = (WTPart) PersistenceHelper.manager.save(part);
```

配套 Helper：
- 视图：`ViewHelper.service.getView(name)`（取不到回退 `"Design"`）。
- 文件夹：`FolderHelper.service.getFolder(path, ref)`，不存在则 `saveFolderPath(path, ref)`；默认 `/Default`。
- 生命周期：`LifeCycleHelper.service.getLifeCycleTemplate(name, containerRef)`。

**幂等约定**：`createPart` 开头先 `getPart(number)`，已存在则直接返回，避免重复创建。
**建议**：用枚举/常量包装 `attributes` 这类 `HashMap`，新代码用泛型 `HashMap<String,String>`。

---

## 5. 软属性（IBA）

通过 `ext.casc.util.IBAUtility` 读写软属性，写完必须 `refresh`：

```java
IBAUtility ibaUtility = new IBAUtility(part);
ibaUtility.setIBAValue("DEPT", "xxx");           // 逐个赋值
part = (WTPart) ibaUtility.updateAttributeContainer(part);
ibaUtility.updateIBAHolder(part);
part = (WTPart) PersistenceHelper.manager.refresh(part);   // 必须刷新拿到最新值
```

参见 `CSCPart.createPart(... ibaattributes ...)`。常见软属性内部名见 `PDM/readMeZYK.txt`、
`readMe_cjh.txt`（如 `DEPT`/`SECRET`/`STANDARDNUMBER` 等）。

---

## 6. 关联关系（Link）

| 关系 | Link 类 | 创建 / 删除 | 查询 |
| --- | --- | --- | --- |
| 零件↔描述文档 | `WTPartDescribeLink` | `PersistenceServerHelper.manager.insert/remove` | `PersistenceHelper.manager.find(WTPartDescribeLink.class, part, DESCRIBES_ROLE, doc)` |
| 零件↔参考文档 | `WTPartReferenceLink` | 同上（参考用 `WTDocumentMaster`） | `find(..., REFERENCES_ROLE, docMaster)` |
| BOM 父↔子 | `WTPartUsageLink` | OOTB 结构 API | 按 `"roleAObjectRef.key.id"` 查父件下挂的 link |

注意 Describe 链接对象用 **WTDocument**，Reference 链接对象用 **WTDocumentMaster**（见 `createPartReferenceDoc`）。
创建前先查重（`getPartDescribeLink/getPartReferenceLink` 返回非 null 则跳过）。

`PersistenceServerHelper.manager`（insert/remove 关系链接） vs `PersistenceHelper.manager`（save/refresh/find 业务对象）——按现状区分使用。

---

## 7. 结构与关联文档遍历（PartCommonHelper / WTPartHelper.service）

```java
WTPartHelper.service.getDescribedByWTDocuments(part, true);   // 描述文档（含子件）
WTPartHelper.service.getDescribedByDocuments(part);            // 描述文档/EPM
WTPartHelper.service.getReferencesWTDocumentMasters(part);     // 参考文档主对象
WTPartHelper.service.getUsesWTParts(part, configSpec);         // 子件（BOM 下钻）
WTPartHelper.service.getUsedByWTParts(partMaster);             // 父件（被谁使用）
```

- `getUsesWTParts` 返回 `Persistable[]`，`per[1]` 才是子件对象，可能是 `WTPart` 或 `WTPartMaster`，需分别处理（见 `getChildMParts`）。
- ConfigSpec 用 `com.glaway.mpm.util.WTPartUtil.getConfigSpec()`。
- 文档类型过滤用 `TypedUtility.getTypeIdentifier(doc).getTypename().contains(typeCode)`。
- 取兄弟节点：先 `getUsedByWTParts` 取父，再 `getChildMParts` 取同级（仅 Manufacturing 视图，见 `getSiblings`）。

---

## 8. 反面模式（现状存在，新代码请避免）

这两个文件里有以下不良写法，**复用其方法可以，但新增代码请改进**：

- ❌ `catch (Exception e) {}` / `catch 后返回 null`：吞掉异常导致问题难定位。
  ✅ 用 `GLLogger`（`com.glaway.mpm.util.GLLogger`）记录，必要时抛 `WTException`。
- ❌ `System.out.println("---装配模式：...")` 散落在业务逻辑里。✅ 统一走 `GLLogger.debug/info`。
- ❌ `e.printStackTrace()`。✅ 用日志框架。
- ❌ 裸 `HashMap` / `ArrayList`（无泛型）。✅ 用泛型集合。
- ❌ `strFolder.trim();`（未接收返回值，等于无效操作）这类 bug。✅ 注意 String 不可变，要 `strFolder = strFolder.trim();`。

---

## 9. 编码与构建提醒

- 源码 UTF-8，含中文注释/字符串，编辑时**保留原编码**（详见根目录 `CLAUDE.md`）。
- 新增类放在 `ext.casc.*` / `ext.ases.*` 等既有命名空间下；若新增顶层包，需同步更新
  `PDM/build_149.xml` 的 `i_javac` 的 `<include>` 列表，否则不会被编译。
- 无单元测试；验证靠部署到 Windchill MethodServer。改完按 `CLAUDE.md` 的部署流程走。
