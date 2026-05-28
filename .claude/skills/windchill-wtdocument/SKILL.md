---
name: windchill-wtdocument
description: 在 PTC Windchill 11 PDM 定制工程中生成 WTDocument（文档对象）相关代码的模式参考——涵盖文档的创建、查询、软属性 (IBA) 读写、主/次内容与附件、版本修订、文档与零部件关联、MVC 表格/DataUtility、表单处理器、StandardManager 服务以及 .xconf 注册。当任务涉及 Windchill WTDocument 的创建、修改、检索、关联或 UI 时使用本 skill。
---

# Windchill WTDocument 业务处理代码生成指南

本 skill 沉淀了本仓库（149 PDM 定制工程）多年累积的 WTDocument 处理"套路"，供其他 Windchill 定制项目和 AI 编码助手参考。**第一原则：优先复用本仓库已有工具类，不要重复造轮子。** 每个模式都标注了权威实现文件路径，直接调用或拷贝改造即可。

---

## 0. 概览与命名空间约定

| 命名空间 | 用途 |
|---|---|
| `ext.casc.doc.*` | 文档业务主目录（创建、修订、附件、签审、技术分类等） |
| `ext.casc.doc.mvc.builder.*` | 文档相关的 MVC 表格/信息页组件构建器 |
| `ext.casc.doc.dataUtility.*` | 文档表格列的 DataUtility |
| `ext.casc.util.*` | IBA、查询、引用等通用工具（`IBAUtility`、`CSCIBA`、`IBAHelper`、`CSCUtil`、`WTUtil`） |
| `ext.casc.workflow.util.*` | 文档相关的 StandardManager 服务 |
| `com.ptc.extend.ixb.*` | WTDocument / 各类 Link 的导入导出处理器 |

**软类型标识符约定**：`WCTYPE|wt.doc.WTDocument|casc.sast.149.<TYPE>`，例如 `casc.sast.149.PROCESS_PLAN`、`casc.sast.149.GONGYIJIANDING`。通过 `ClientTypedUtility.getTypeDefinitionReference(typeName)` 拿到 `TypeDefinitionReference`，赋给 `document.setTypeDefinitionReference(tdr)`。

**编码**：源码 UTF-8 编译，Ant 构建文件 GBK；保留中文注释、保留原始编码。

**异常处理风格**：本仓库历史代码绝大多数为 `e.printStackTrace()`，保持风格一致即可；新增重要服务建议同时用 `wt.log4j.LogR.getLogger(...)` 记录关键节点。

---

## 1. 文档创建（Create）

**权威实现**：`ext.casc.doc.CSCDoc.createDoc(...)`（`PDM/src/ext/casc/doc/CSCDoc.java`，两个重载 line 215、line 310）。生成新文档代码时，**首选直接调用 `CSCDoc.createDoc(...)`**；只有当现有重载无法满足时，再仿照其内部模式自行构造。

核心五步：

```java
// 1) 新建文档骨架
WTDocument doc = WTDocument.newWTDocument(number, name, DocumentType.getDocumentTypeDefault());

// 2) 设置软类型（WCTYPE|wt.doc.WTDocument|casc.sast.149.<TYPE>）
TypeDefinitionReference tdr =
    ClientTypedUtility.getTypeDefinitionReference("casc.sast.149.GONGYIJIANDING");
doc.setTypeDefinitionReference(tdr);

// 3) 落容器与文件夹
doc.setContainerReference(containerRef);
Folder folder = FolderHelper.service.getFolder("/Default/02工艺文件/15工艺鉴定", containerRef);
if (folder == null) {
    folder = FolderHelper.service.saveFolderPath("/Default/02工艺文件/15工艺鉴定", containerRef);
}
WTValuedHashMap map = new WTValuedHashMap();
map.put(doc, folder);
FolderHelper.assignLocations(map);

// 4) 持久化
doc = (WTDocument) PersistenceHelper.manager.save(doc);

// 5) 初始化 IBA（见 §2）
IBAUtility iba = new IBAUtility(doc);
iba.setIBAValue("SECRET", "公开");
doc = (WTDocument) iba.updateAttributeContainer(doc);
IBAUtility.updateIBAHolder(doc);
doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
```

**可直接发布并跳过生命周期流程**（如批量导入场景，参考 `CSCDoc.createDoc` line 257-259）：

```java
LifeCycleState st = LifeCycleState.newLifeCycleState();
st.setState(State.toState("APPROVED"));
doc.setState(st);
```

**编号自动生成**：`CSCDoc.getDefaultDocSeqNumber()` 调 `PersistenceHelper.manager.getNextSequence("WTDOCUMENTID_SEQ")`，必要时用 `DecimalFormat` 补零（见 `getDocSeqNumber(seqName, bits)`）。

---

## 2. 软属性 (IBA) 读写

**权威工具**：
- `ext.casc.util.IBAUtility`（实例化版，构造函数自动加载现有 IBA 容器）
- `ext.casc.util.CSCIBA`（项目内静态封装）
- `ext.casc.util.IBAHelper`（静态 getter 简化调用）

### 写入（标准三步走）

```java
IBAUtility iba = new IBAUtility(doc);              // 加载已有 IBA 容器
iba.setIBAValue("PHASE_CODE", "P1");               // 单值
iba.setIBAValues("DEPT", Arrays.asList("D1","D2")); // 多值
// 带依赖关系（参考值）：
iba.setIBAValue(sourceName, sourceValue, businessName, businessValue);

doc = (WTDocument) iba.updateAttributeContainer(doc); // 把 hashtable 回写到 AttributeContainer
IBAUtility.updateIBAHolder(doc);                      // 落库（含 update without checkout/checkin）
doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
```

支持类型（在 `IBAUtility.internalCreateValue` 处分派）：`StringDefView`、`IntegerDefView`、`FloatDefView`、`BooleanDefView`、`TimestampDefView`、`URLDefView`、`ReferenceDefView`、`UnitDefView`、`RatioDefView`。

### 读取（推荐用静态封装）

```java
String secret = IBAHelper.getIBAStringValue(doc, "SECRET");
String phase  = new IBAUtility(doc).getIBAValue("PHASE_CODE");
List<String> values = new IBAUtility(doc).getIBAValues("DEPT");
```

### 批量按 IBA 查询（避免逐对象查询）

`IBAUtility.getBulkIBAString(List, String ibaName)` 一次拉取多文档的字符串 IBA 值，返回 `Map<objectId, value>`。

### 常见坑

- 写完必须 `updateAttributeContainer` + `updateIBAHolder`，**单独 `setIBAValue` 不落库**。
- 同一对象多次写 IBA 时复用同一个 `IBAUtility` 实例；每次 `new IBAUtility(doc)` 会重新拉容器。
- CSM 分类约束自动被 `suppressCSMConstraint`/`removeCSMConstraint` 临时屏蔽，无需手动处理。

---

## 3. 主内容与次内容/附件 (Content)

### 主内容（PRIMARY）

权威实现：`CSCDoc.updateDocContent(WTDocument doc, String filepath)`（`CSCDoc.java:446`）。流程：检出 → 新建 `ApplicationData` → `ContentServerHelper.updateContent` → 设格式 → 检入。

```java
doc = CSCDoc.getWorkingCopyOfDoc(doc);                          // 自动 checkout 或返回 workingCopy

ApplicationData ad = ApplicationData.newApplicationData(doc);
ad.setRole(ContentRoleType.PRIMARY);
ad = ContentServerHelper.service.updateContent(doc, ad, filepath);

doc = (WTDocument) PersistenceServerHelper.manager.restore(doc);

DataFormat df = ContentHelper.service.getFormatByName("PDF");   // 见 CSCDoc.getFileFormat
doc.setFormat(DataFormatReference.newDataFormatReference(df));

if (WorkInProgressHelper.isCheckedOut(doc, SessionHelper.manager.getPrincipal())) {
    doc = (WTDocument) WorkInProgressHelper.service.checkin(doc, "add primary file");
}
```

### 次内容/附件（SECONDARY）

权威实现：`ext.casc.doc.AddAttachment4SKProcessor.uploadFile`（`AddAttachment4SKProcessor.java:67`）。

```java
ContentHolder holder = ContentHelper.service.getContents(document);
ApplicationData data = ApplicationData.newApplicationData(holder);
data.setFileName(file.getName());
data.setUploadedFromPath(file.getAbsolutePath());
data.setRole(ContentRoleType.SECONDARY);
data.setFileSize(file.length());
data.setDescription("数控程序");                                  // 用 description 区分不同类附件
holder = (ContentHolder) PersistenceHelper.manager.refresh(holder);
ContentServerHelper.service.updateContent(holder, data, file.getPath());
```

### 读取附件（按角色）

权威工具：`ext.casc.doc.DocumentUtil.getAttachmentsFromDocument(WTDocument, ContentRoleType)`（`DocumentUtil.java:21`），返回 `Map<fileName, downloadURL>`。WTDocument 与 EPMDocument 两个重载。

```java
Map<String,String> primaryUrls   = DocumentUtil.getAttachmentsFromDocument(doc, ContentRoleType.PRIMARY);
Map<String,String> secondaryUrls = DocumentUtil.getAttachmentsFromDocument(doc, ContentRoleType.SECONDARY);
```

---

## 4. 查询与检索 (Query)

### 简单查询（按编号、名称）

直接复用 `CSCDoc` 静态方法：

```java
WTDocument doc = CSCDoc.getDoc(number);                            // 最新迭代
WTDocument v   = CSCDoc.getLatestDocByNumberAndVersion(num, "B");  // 按版本
ArrayList all  = CSCDoc.getDocVersions(number);                    // 所有迭代
ArrayList like = CSCDoc.getDocumentsLikeNumber(numberFragment);    // 模糊编号
```

这些方法内置 `setAdministrator` / `setPrincipal` 会话切换以越权查询。**在工具类里仿写时务必把还原放进 finally。**

### 高级查询（QuerySpec）

```java
QuerySpec qs = new QuerySpec(WTDocument.class);
qs.appendSearchCondition(
    new SearchCondition(WTDocument.class, WTDocument.NUMBER,
        SearchCondition.LIKE, "%" + num + "%", false));
qs.appendAnd();
qs.appendSearchCondition(
    new SearchCondition(WTDocument.class, "iterationInfo.latest", "TRUE"));
QueryResult qr = PersistenceHelper.manager.find(qs);
```

### 按软类型过滤（常用于 MVC builder）

权威示例：`ext.casc.doc.mvc.builder.SearchProcessPlanBuilder.getQueryRefDoc`（`SearchProcessPlanBuilder.java:48`）。

```java
ArrayList<TypeIdentifier> types = SoftTypeUtil.getChildTypes(
    "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);

QuerySpec qs = new QuerySpec();
qs.setAdvancedQueryEnabled(true);
qs.appendClassList(WTDocument.class, true);

long[] branchIds = /* 把每个 TypeIdentifier 转 TypeDefinitionReference 取 branchId 收集 */ ;
qs.appendWhere(
    new SearchCondition(
        new ClassAttribute(WTDocument.class, "typeDefinitionReference.key.branchId"),
        SearchCondition.IN, new ArrayExpression(branchIds)));

qs = new LatestConfigSpec().appendSearchCriteria(qs);  // 仅最新迭代
```

### 按 IBA 值过滤（子查询）

权威示例：`SearchProcessPlanBuilder.getStringIBAQuery`（line 127）。

```java
private static SubSelectExpression getStringIBAQuery(String ibaName, String ibaValue)
        throws WTException, WTPropertyVetoException, RemoteException {
    AttributeDefDefaultView addv =
        IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
    long ibaDefId = addv.getObjectID().getId();

    QuerySpec qs = new QuerySpec();
    int idx = qs.appendClassList(wt.iba.value.StringValue.class, false);
    qs.appendSelect(
        new ClassAttribute(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id"),
        new int[]{idx}, false);
    qs.appendWhere(new SearchCondition(wt.iba.value.StringValue.class,
        "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[]{idx});
    qs.appendAnd();
    qs.appendWhere(new SearchCondition(wt.iba.value.StringValue.class,
        _StringValue.VALUE2, SearchCondition.EQUAL, ibaValue), new int[]{idx});
    return new SubSelectExpression(qs);
}
```

外层用 `SearchCondition(IN, subSelectExpression)` 把 `WTDocument.persistInfo.objectIdentifier.id` 与子查询对接。

---

## 5. 版本修订 (Revise)

**权威示例**：`ext.casc.doc.ReviseProcessPlanDocProcessor`（`ReviseProcessPlanDocProcessor.java:42`）。新建修订版本的标准流程：

```java
Transaction tx = new Transaction();
try {
    // 1) 拿当前最新版本作为新版本号基础
    QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc);
    Versioned vMax = (Versioned) qr2.nextElement();

    // 2) 找同一大版本下最新的"非一次性"版本作为内容基础
    Versioned vBase = null;
    qr2 = VersionControlHelper.service.allVersionsFrom(doc);
    while (qr2.hasMoreElements()) {
        vBase = (Versioned) qr2.nextElement();
        if (!(vBase instanceof OneOffVersioned)
            || !VersionControlHelper.isAOneOff((OneOffVersioned) vBase)) break;
    }

    // 3) 检出保护
    if (vBase instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) vBase))
        throw new Exception("选中版本的最新非先行更改版本正被检出，不能进行修订!");

    // 4) 创建并存储新版本
    tx.start();
    VersionIdentifier   vi = VersionControlHelper.nextVersionId(vMax);
    IterationIdentifier ii = VersionControlHelper.firstIterationId(vMax);
    Versioned newDoc = VersionControlHelper.service.newVersion(vMax, vi, ii);
    newDoc = (WTDocument) PersistenceHelper.manager.store(newDoc);

    // 5) 复制主体文件 + 关联零件
    WTDocumentUtil.setPrimaryForDocument((WTDocument) newDoc, (WTDocument) vBase);
    WTPartUtil.createWTPartDescribeLink(part, (WTDocument) newDoc);

    tx.commit();
    tx = null;

    // 6) 清掉旧版本的可视化（参考 deleteRepresentation 实现）
    deleteRepresentation((WTDocument) newDoc);
} finally {
    if (tx != null) tx.rollback();
}
```

`com.glaway.mpm.util.WTDocumentUtil.setPrimaryForDocument` 是复制 PRIMARY 内容的快捷方法。删可视化用 `PublishUtils.getRepresentations` + `RepresentationHelper.service.deleteRepresentation`。

---

## 6. 文档关联关系 (Links)

### 文档 ↔ 文档（依赖）

`WTDocumentDependencyLink`。权威封装：`CSCDoc.createDocAssociateDoc` / `removeDocAssociateDoc`（`CSCDoc.java:650-716`）。

```java
WTDocumentDependencyLink existed = CSCDoc.getDocDependencyLink(doc1, doc2);
if (existed == null) {
    WTDocumentDependencyLink link = WTDocumentDependencyLink.newWTDocumentDependencyLink(doc1, doc2);
    PersistenceServerHelper.manager.insert(link);
    PersistenceHelper.manager.refresh(link);
}
```

类似还有 `WTDocumentUsageLink`（用途引用）、技术通知前后关联（见 `CmExpImpTechNoticeBeforeLink/AfterLink`）。

### 零件 ↔ 文档（描述）

`WTPartDescribeLink`。权威示例：`CreateGyjdFormProcessor.saveDocPartLink`（`CreateGyjdFormProcessor.java:141`），通用工具 `WTPartUtil.createWTPartDescribeLink(part, doc)`、`ext.casc.part.CSCPart.createPartAssociateDoc`、`ext.casc.util.RelatedDocPartUtil`。

```java
WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
PersistenceServerHelper.manager.insert(link);
PersistenceHelper.manager.refresh(link);
```

### 查询关联

```java
QueryResult parts = WTPartHelper.service.getDescribesWTParts(doc);            // 文档描述的零件
QueryResult docs  = WTDocumentHelper.service.getDependsOnWTDocuments(doc);    // 文档依赖的文档
QueryResult refs  = StructHelper.service.navigateReferencedBy(
                        (WTDocumentMaster) doc.getMaster(), WTPartReferenceLink.class, true);
```

更复杂的关联清查见 `com.ptc.extend.ixb.CmExpImpSearchHelper`。

---

## 7. 表单处理器 (Form Processor)

两类基类：

- 文档**创建**：继承 `com.ptc.windchill.enterprise.doc.forms.CreateDocFormProcessor`，参考 `CreateGyjdFormProcessor`、`CreateGyffaFormProcessor`、`CreateGydxFormProcessor`、`CreateQtlwdFormProcessor` 等（均在 `PDM/src/ext/casc/doc/`）。
- 文档**动作**（提交、签审、修订、回收、下载、改 IBA 等）：继承 `com.ptc.core.components.forms.DefaultObjectFormProcessor`，参考 `SubmitApprovalProcessor`、`ReviseProcessPlanDocProcessor`、`QuickApprovedProcessor`、`EditPrintFlagProcessor`。

### 统一骨架

```java
public class XxxProcessor extends DefaultObjectFormProcessor {   // 或 CreateDocFormProcessor
    @Override
    public FormResult doOperation(NmCommandBean cb, List<ObjectBean> beans) throws WTException {
        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);   // 提权
        try {
            for (ObjectBean ob : beans) {
                if (ob.getObject() instanceof WTDocument) {
                    WTDocument doc = (WTDocument) ob.getObject();
                    // —— 业务处理 ——
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FeedbackMessage fb = new FeedbackMessage();
            fb.addMessage("操作失败");
            result.addFeedbackMessage(fb);
            result.setStatus(FormProcessingStatus.FAILURE);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);                // 还原
        }
        FeedbackMessage ok = new FeedbackMessage();
        ok.addMessage("操作成功");
        result.addFeedbackMessage(ok);
        result.setNextAction(FormResultAction.NONE);
        return result;
    }
}
```

**关键**：`setAccessEnforced(false)` 与还原必须严格成对出现在 `try/finally`，否则会泄漏到后续请求。

### 取选中对象的另一种方式

```java
Object actionObj = cb.getActionOid().getRefObject();   // 单选场景
String[] oids = cb.getTextParameterValues("oid");      // 多选场景
Persistable p = new ReferenceFactory().getReference(oid).getObject();
```

### 在创建表单里设置类型 + 文件夹（CreateDocFormProcessor 子类）

```java
TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("casc.sast.149.GONGYIJIANDING");
wtdoc.setTypeDefinitionReference(tdr);
String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtdoc);
if (docType.endsWith("casc.sast.149.GONGYIJIANDING")) {
    FolderUtil.setDocFolder("/Default/02工艺文件/15工艺鉴定", wtdoc);
}
wtdoc = (WTDocument) PersistenceHelper.manager.save(wtdoc);
```

---

## 8. MVC 组件构建器（表格 / 信息页）

继承 `com.ptc.mvc.components.AbstractComponentBuilder`，类上加 `@ComponentBuilder("全限定名")` 注解。

**权威示例**：`SearchProcessPlanBuilder`、`SearchDocBuilder`、`UploadAttachmentBuilder`、`TechnicsTechnologyBuilder`（均在 `PDM/src/ext/casc/doc/mvc/builder/`）。

```java
@ComponentBuilder("ext.casc.doc.mvc.builder.MyDocBuilder")
public class MyDocBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        String number = (String) cb.getText().get("number");
        // 调用 §4 中的查询逻辑返回 List<WTDocument>
        return queryDocs(number);
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory f = getComponentConfigFactory();
        TableConfig t = f.newTableConfig();
        t.setLabel("文档列表");
        t.setId("ext.casc.doc.mvc.builder.MyDocBuilder");
        t.setSelectable(true);
        t.addComponent(f.newColumnConfig("type_icon", false));
        t.addComponent(f.newColumnConfig("number",    false));
        t.addComponent(f.newColumnConfig("name",      false));
        t.addComponent(f.newColumnConfig("version",   false));
        t.addComponent(f.newColumnConfig("state.state", true));
        return t;
    }
}
```

常见列键：`type_icon`、`number`、`name`、`version`、`iterationInfo.modifier`、`state.state`、`thePersistInfo.createStamp`。

---

## 9. DataUtility（列数据 / GUI 组件）

继承 `com.ptc.core.components.descriptor.AbstractDataUtility`，覆写 `getDataValue(columnName, obj, mc)`，按列名返回 `String`、`StringInputComponent`、`ComboBox` 或 `GUIComponentArray`。

**权威示例**：`ext.casc.doc.dataUtility.DocDataUtility`、`TechnicsTechnologyDataUtility`、`ext.ases.envelope.datautility.ProcessEnvelopeDataUtility`。

```java
public class MyDocDataUtility extends AbstractDataUtility {
    @Override
    public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
        if (!(obj instanceof WTDocument)) return null;
        WTDocument doc = (WTDocument) obj;
        if ("ppNumber".equals(columnName)) {
            return IBAHelper.getIBAStringValue(doc, "PPNUMBER");
        }
        // 返回输入框
        StringInputComponent input = new StringInputComponent("ppNumber");
        input.setValue("...");
        return input;
    }
}
```

### 注册（**必须**）

`PDM/codebase/ext/casc/conf/casc_datautility.xconf` 增加：

```xml
<Service context="default" name="com.ptc.core.components.descriptor.DataUtility">
    <Option requestor="null"
            serviceClass="ext.casc.doc.dataUtility.MyDocDataUtility"
            selector="MyDocDataUtility"
            cardinality="singleton"/>
</Service>
```

执行 `xconfmanager -i <path>/casc_datautility.xconf -p` 生效。

---

## 10. 自定义服务（StandardManager 模式）

接口 + `Standard*Service extends wt.services.StandardManager`，工厂方法 `newStandard*Service()` 内部调 `initialize()` 完成启动注册。

**权威示例**：`ext.casc.workflow.util.StandardDocService`（`StandardDocService.java:21`）、`ext.casc.service.StandardCascCacheService`。

```java
public class StandardDocService extends StandardManager implements DocService, Serializable {
    public static StandardDocService newStandardDocService() throws WTException {
        StandardDocService instance = new StandardDocService();
        instance.initialize();
        return instance;
    }

    public Object createDoc(String number, String name, String desc,
                            HashMap attributes, HashMap<String,Object> softAttr,
                            WTContainerRef containerRef) throws WTException {
        WTDocument doc = CSCDoc.createDoc(number, name, desc, attributes, containerRef);
        CSCIBA iba = new CSCIBA(doc);
        for (String key : softAttr.keySet()) {
            Object v = softAttr.get(key);
            if (v instanceof String) CSCIBA.setIBAStringValue(doc, key, (String) v);
        }
        new IBAUtility(doc).updateIBAHolder((IBAHolder) doc);
        return doc;
    }
}
```

### 注册（在 `casc_149.xconf` 中）

```xml
<Service context="default" name="ext.casc.workflow.util.DocService">
    <Option serviceClass="ext.casc.workflow.util.StandardDocService"
            requestor="null" selector="java.lang.Object" cardinality="singleton"/>
</Service>
```

需要时把服务加入 `wt.services.StartupParticipant.list`，让 MethodServer 启动时实例化。

---

## 11. 注册、构建与部署

| 内容 | 位置 / 命令 |
|---|---|
| 服务 / Validator / DataUtility | `PDM/codebase/ext/casc/conf/casc_149.xconf`、`casc_datautility.xconf` → `xconfmanager -i <file> -p` |
| UI 动作 / 动作模型 | `PDM/codebase/.../*-actions.xml`、`*-actionModels.xml`（参考 `PDM/ootbActions/DocumentManagement-actions.xml`、`custom-149-actions.xml`） |
| Registry 增量（associationRegistry / descendentRegistry / modelRegistry） | `PDM/codebase/*.properties.addition` —— **构建不自动合并**，需手动并入 `$WT_HOME/codebase` |
| 持久化模型（如新建 Link 类） | 放 `ext.ases.*`，跑 `ant -f build_149.xml jg` 走 JavaGen + modelInstall，再 `sqlgen` 生成 DDL |
| 资源 bundle (`.rbInfo`) | `ant -f build_149.xml rb` |
| 整体构建 | 在 Windchill Shell 内 `ant -f build_149.xml`（输出直接写入 `$WT_HOME`，**等同于一次部署**，无回滚） |

详细分步见 `PDM/readme.txt` 与 `CLAUDE.md`。

---

## 12. 导入 / 导出 (IXB / MQ 同步)

跨域、跨系统同步文档时，**不要重写**，扩展现有处理器即可：

- `com.ptc.extend.ixb.CmExpImpWTDocument` —— 主文档导入导出（属性、版本、生命周期、内容、IBA、关联链路）。
- `com.ptc.extend.ixb.CmExpImpWTDocumentDependencyLink` / `CmExpImpWTDocumentUsageLink` / `CmExpImpWTPartDescribeLink` / `CmExpImpWTPartReferenceLink` —— 各类 Link。
- `com.ptc.extend.ixb.center.MQExpImpWTDocument` + `ext.sast.center.synch.MQDataExportService` —— RabbitMQ 同步链路（依赖 `dc_rabbit-common` 模块）。
- 协助查询：`CmExpImpSearchHelper.searchAllWTDocumentDependencyLink` / `searchAllWTPartDescribeLink` / `searchIteratedByNumberVersionIteration`。

---

## 13. 约定与坑（Conventions & Gotchas）

1. **提权用 `SessionServerHelper.manager.setAccessEnforced(false)`，必须 `finally` 还原** —— 否则越权状态会泄漏到后续请求。
2. **越权查询切换 principal**：工具方法常用 `setAdministrator` 做查询，结束前 `setPrincipal(原用户)` 还原（见 `CSCDoc.getDoc` 模板）。
3. **IBA 写完务必 `updateAttributeContainer` + `updateIBAHolder`** —— 仅 `setIBAValue` 不会落库。
4. **类型标识符前缀** `WCTYPE|`：使用 `TypeIdentifier` API 时若字符串没带前缀需手动拼上（参考 `CSCDoc.createDoc` line 366）。
5. **编号自动序列**：`PersistenceHelper.manager.getNextSequence("WTDOCUMENTID_SEQ")`，可结合 `DecimalFormat` 补零。
6. **修订/删除可视化**：复制内容后用 `RepresentationHelper.service.deleteRepresentation` 清旧 Rep，否则前端会看到错版。
7. **OneOff（一次性）版本要跳过**：找"内容基础"时务必判 `!isAOneOff`。
8. **检入前判 `isCheckedOut(doc, principal)`**：未检出时直接 `checkin` 会抛异常。
9. **编码混用**：Java 源 UTF-8，Ant build 文件 GBK，`.properties` 文件有的是 GBK，**保留原始编码**不要转换。
10. **无自动化测试**：靠部署到 MethodServer 与跑 `ext.casc.test.*` / `ext.test.*` 临时 main 验证。
11. **`build_149.xml` 的 `<include>/<exclude>`**：新增顶层包或覆盖 OOTB 类时记得加进 `i_javac` 的 include / exclude 列表，否则不会被编译。

---

## 14. 决策速查（生成代码前先对照）

| 任务 | 首选已有工具 / 类 | 路径 |
|---|---|---|
| 按编号取文档 | `CSCDoc.getDoc(number)` | `PDM/src/ext/casc/doc/CSCDoc.java:92` |
| 按号 + 版本取最新迭代 | `CSCDoc.getLatestDocByNumberAndVersion` | `CSCDoc.java:126` |
| 创建文档 | `CSCDoc.createDoc(...)` 或 `StandardDocService.createDoc` | `CSCDoc.java:215,310` / `StandardDocService.java:31` |
| 写 IBA | `new IBAUtility(doc).setIBAValue + updateAttributeContainer + updateIBAHolder` | `IBAUtility.java` |
| 读 IBA | `IBAHelper.getIBAStringValue(doc, name)` | `ext/casc/util/IBAHelper.java` |
| 上传主内容 | `CSCDoc.updateDocContent` | `CSCDoc.java:446` |
| 添加附件（SECONDARY） | 参考 `AddAttachment4SKProcessor.uploadFile` | `AddAttachment4SKProcessor.java:67` |
| 取附件 URL | `DocumentUtil.getAttachmentsFromDocument` | `DocumentUtil.java:21` |
| 修订新版本 | 仿 `ReviseProcessPlanDocProcessor` | `ReviseProcessPlanDocProcessor.java` |
| 建零件-文档关联 | `WTPartUtil.createWTPartDescribeLink` 或 `CSCPart.createPartAssociateDoc` | `com.glaway.mpm.util.WTPartUtil` / `ext.casc.part.CSCPart` |
| 建文档-文档关联 | `CSCDoc.createDocAssociateDoc` | `CSCDoc.java:650` |
| 按软类型 + IBA 查询 | 参考 `SearchProcessPlanBuilder.getQueryRefDoc` | `SearchProcessPlanBuilder.java:48` |
| 文档表单创建处理 | 继承 `CreateDocFormProcessor`，仿 `CreateGyjdFormProcessor` | `CreateGyjdFormProcessor.java` |
| 文档动作处理 | 继承 `DefaultObjectFormProcessor`，仿 `SubmitApprovalProcessor` | `ext.casc.doc.*Processor` |
| 文档表格 | 继承 `AbstractComponentBuilder`，仿 `SearchProcessPlanBuilder` | `SearchProcessPlanBuilder.java` |
| 表格列数据 | 继承 `AbstractDataUtility`，仿 `DocDataUtility` | `DocDataUtility.java` |
| 自定义服务 | 继承 `StandardManager`，仿 `StandardDocService` | `StandardDocService.java` |

---

**最后建议**：在生成新代码前，先用 grep 搜本仓库（`PDM/src`）确认是否已有同义实现；本工程历史长、相似类多，直接复用能省去 90% 反复试错。
