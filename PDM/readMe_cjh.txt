新建子类型：
    工艺参数化模板 - casc.sast.149.TechnicsParamTemplate
    工序参数化模板 - casc.sast.149.StepParamTemplate
    新增软属性
        部门 - DEPT
        专业 - speciality
        产品类型 - productType



新建更改请求子类型
    工艺变更申请单 - casc.sast.149.PROCESS_ECR
    新增软属性
        更改类别 - ECRTYPE  合法值列表 -> I类、设计I类、首飞前II类、首飞后II类、设计II类、III类  必需
        密级 - SECRET  Create New Layout/Edit Layout -> SecretLevelDataUtility
    /config/actions/ChangeManagement-actionModels.xml
        Action Model Name: analysis_activity_row_actions  新增条目: deleteChangeItem
工艺更改单
    新建软属性
        工艺更改申请编号 - ECRNUMBER
            创建和编辑实用程序ID - AddAttriubutesDatautility

分析活动 - 修改显示名称 - 更改影响分析
    新增软属性
        密级 - SECRET
    建模 AnalysisToSourceLink

工艺任务活动
    新增软属性
        关联的更改影响分析编号 - ANALYSISNUMBER

生命周期模版 对象初始化规则
    工艺更改申请单 更改影响分析

流程
    工艺申请单签审流程
    更改影响分析执行流程
    工艺更改单签审流程 - 添加活动 - 完成处理受影响工艺
    149变更签审包工艺会签流程等 - 添加活动 - 自动创建更改影响分析

导出模板 - Windchill\codebase\templates\

    受影响列表.xlsx
    受影响制品列表.xlsx

工时定额
    操作-工序
        新增软属性
            制造单位 - workShop
            工序ID - BIAOSHI (标识)

    批量数据集
        新增子类型 - 工时定额签审单 casc.sast.149.GSDE
        新建对象初始化规则

        新增子类型 - 批量预审单 casc.sast.149.PREVIEWPKG
        新建对象初始化规则

    工艺规程新建软属性
        工时标识 - GONGSHISIGN

变更签审包|批量签审包
    新增软属性
        是否创建影响分析 - hasAnalysis