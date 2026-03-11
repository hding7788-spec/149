aaa149部署   
1.拷贝149文件夹至目标服务器${WTHOME}。

2.运行Windchill Shell 进入目录${WTHOME}/149，执行命令ant -f build_149.xml。

3.用oracle用户登录系统,进入目录${WTHOME}/db/sql3，运行Sqlplus执行SQL @ext/Make_pkg_ext_Table和@ext/Make_pkg_ext_Index。

4.进入目录${WTHOME}/codebase，执行命令ant -f MakeJar.xml。

5.手动拷贝149\codebase\associationRegistry.properties.addition、descendentRegistry.properties.addition、modelRegistry.properties.addition
        到服务器的${WTHOME}/codebase相应文件中

6.重启Windchill MethodServer。

7.进入目录WTHOME/loadFiles/ext/149，执行命令windchill wt.load.LoadFileSet -file loadSet.xml -Unattended -NoServerStop -u wcadmin -p wcadmin    

8.进入目录WTHOME 执行命令xconfmanager -i ${WTHOME}\codebase\ext\casc\conf\casc_149.xconf -p

9.进入目录WTHOME 执行命令xconfmanager -i ${WTHOME}\codebase\ext\casc\conf\casc_datautility.xconf -p

10.重启Windchill MethodServer

11.进入首选项管理器:更改管理->不附有更改请求的更改通告：是
  
12.进入首选项管理器:开启使用用任务模板来显示任务详细信息页面

13.进入首选项管理器:开启不带变更请求的变更通告
  
14.创建批量数据集的软类型:casc.sast.149.APPROVEFORM

15.修改类型批量数据集的生命周期模板和团队模板的中文显示

================================================
发次BOM功能

1. 在custom-149-actionModels.xml
<action name="ebomExport" type="customReport"/>
下一行增加
	   <action name="faciBomExport" type="customReport"/>

2. 在custom-149-actions.xml
<action name="ebomExport" resourceBundle="ext.casc.ui.actionsRB">
			<command url="/netmarkets/jsp/ext/casc/report/downloadBomCompareReport.jsp?type=ebomExport" windowType="popup"/>
			<includeFilter name="DownloadTechnicsReportValidtor" />
		</action>
下增加
		<action name="faciBomExport" resourceBundle="ext.casc.ui.actionsRB">
			<command url="/netmarkets/jsp/ext/casc/report/downloadBomCompareReport.jsp?type=faciExport" windowType="popup"/>
			<includeFilter name="DownloadTechnicsReportValidtor" />
		</action>
=====================================================================================
技术状态流程图
1. 创建数据库表
CREATE TABLE "DW_NODE_CONFIG"
   (	"ID" VARCHAR2(1000) NOT NULL ENABLE,
	"NODE_TYPE" VARCHAR2(100) NOT NULL ENABLE,
	"NODE_TEXT" VARCHAR2(2000) NOT NULL ENABLE,
	"NODE_PROPERTIES" VARCHAR2(2000),
	"NODE_ORDER" NUMBER(38,0),
	"IS_DELETED" VARCHAR2(100)
   );

CREATE UNIQUE INDEX "DW_NODE_CONFIG_ID_IDX" ON "DW_NODE_CONFIG" ("ID");

CREATE TABLE "DW_EDGE_CONFIG"
   (	"ID" VARCHAR2(1000) NOT NULL ENABLE,
	"EDGE_TYPE" VARCHAR2(100) NOT NULL ENABLE,
	"SOURCENODEID" VARCHAR2(1000) NOT NULL ENABLE,
	"TARGETNODEID" VARCHAR2(1000) NOT NULL ENABLE,
	"EDGE_TEXT" VARCHAR2(1000),
	"IS_DELETED" VARCHAR2(100)
   );
CREATE UNIQUE INDEX "DW_EDGE_CONFIG_ID_IDX" ON "DW_EDGE_CONFIG" ("ID");

2. 导入DW_NODE_CONFIG数据
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_在制品报废_在制品报废审批中','CustHtmlNode','在制品报废审批中','{
"head": "MES系统",
"taskName": "在制品报废审批中",
"taskType": "在制品",
"conditionItemType": "报废",
"conditionCount": "",
"instanceCountRange": "0_n"
}',250,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_在制品报废_在制品报废完成','CustHtmlNode','在制品报废完成','{
"head": "MES系统",
"taskName": "在制品报废完成"
}',260,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_在制品报废_结束','circle','结束','{
                "r": 30
            }',270,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_工艺升版_在制品工艺升版意见返回PDM','CustHtmlNode','在制品工艺升版','{
"head": "MES系统",
"taskName": "在制品工艺升版意见返回PDM",
"taskType": "在制品",
"conditionItemType": "工艺升版",
"conditionCount": "",
"instanceCountRange": "0_n"
}',280,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_工艺升版_在制品工艺升版完成','CustHtmlNode','在制品工艺升版完成','{
"head": "MES系统",
"taskName": "在制品工艺升版完成"
}',290,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_工艺升版_结束','circle','结束','{
                "r": 30
            }',300,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_外协返修_外协返修','CustHtmlNode','外协返修','{
"head": "MES系统",
"taskName": "外协返修",
"taskType": "在制品",
"conditionItemType": "外协返修",
"conditionCount": "",
"instanceCountRange": "0_n"
}',310,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_外协返修_结束','circle','结束','{
                "r": 30
            }',320,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_无影响_结束','circle','结束','{
                "r": 30
            }',340,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量=0_无影响_在制品无影响','CustHtmlNode','在制品无影响','{
"head": "MES系统",
"taskName": "在制品无影响",
"taskType": "在制品",
"conditionItemType": "",
"conditionCount": "=0",
"instanceCountRange": "0_1"
}',350,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量=0_无影响_结束','circle','结束','{
                "r": 30
            }',360,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_开始','circle','开始','{
"r": 30,
"style": {
"strokeWidth": "1"
},
"taskState": "已完成"
}',10,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_NC处理影响分析任务','CustHtmlNode','NC处理影响分析任务','{
"head": "NC系统",
"taskName": "NC处理影响分析任务",
"taskType": "在制品或已制品"
}',20,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_整件外协是否有影响','diamond','整件外协是否有影响','{
"width": 160,
"height": 80,
"style": {
"strokeWidth": "1"
},
"rx": 100,
"ry": 40
}',30,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响','diamond','已制品是否有影响','{
"width": 160,
"height": 80,
"style": {
"strokeWidth": "1"
},
"rx": 100,
"ry": 40
}',40,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响','diamond','在制品是否有影响','{
"width": 160,
"height": 80,
"style": {
"strokeWidth": "1"
},
"rx": 100,
"ry": 40
}',50,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_整件外协是否有影响_外协数量>0_NC返回闭环结果','CustHtmlNode','整件外协执行中','{
"head": "NC系统",
"taskName": "整件外协执行中",
"taskType": "在制品",
"conditionItemType": "整件外协",
"conditionCount": ">0",
"instanceCountRange": "0_1"
}',70,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_整件外协是否有影响_外协数量>0_结束','circle','结束','{
                "r": 30
            }',75,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品返修_已制品返修工艺编制中','CustHtmlNode','已制品返修工艺编制中','{
"head": "PDM系统",
"taskName": "已制品返修工艺编制中",
"taskType": "已制品",
"conditionItemType": "返修",
"conditionCount": "",
"instanceCountRange": "0_n"
}',80,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品返修_NC收到返修工艺任务','CustHtmlNode','NC收到返修工艺任务','{
"head": "PDM系统",
"taskName": "NC收到返修工艺任务",
"taskType": "已制品",
"conditionItemType": "返修",
"conditionCount": "",
"instanceCountRange": "1"
}',90,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品返修_已制品返修工艺计划下达','CustHtmlNode','已制品返修工艺计划下达','{
"head": "NC系统",
"taskName": "已制品返修工艺计划下达"
}',100,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品返修_已制品返修执行中','CustHtmlNode','已制品返修执行中','{
"head": "NC系统",
"taskName": "已制品返修执行中"
}',110,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品返修_结束','circle','结束','{
                "r": 30
            }',120,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品报废_已制品报废中','CustHtmlNode','已制品报废中','{
"head": "NC系统",
"taskName": "已制品报废中",
"taskType": "已制品",
"conditionItemType": "报废",
"conditionCount": "",
"instanceCountRange": "0_n"
}',130,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品报废_已制品报废完成','CustHtmlNode','已制品报废完成','{
"head": "NC系统",
"taskName": "已制品报废完成"
}',140,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品报废_结束','circle','结束','{
                "r": 30
            }',150,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品无影响_已制品无影响','CustHtmlNode','已制品无影响','{
"head": "NC系统",
"taskName": "已制品无影响",
"taskType": "已制品",
"conditionItemType": "无影响",
"conditionCount": "",
"instanceCountRange": "0_n"
}',160,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品无影响_结束','circle','结束','{
                "r": 30
            }',170,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_NC推送MES在制品影响分析任务','CustHtmlNode','NC推送MES在制品影响分析任务','{
"head": "NC系统",
"taskName": "NC推送MES在制品影响分析任务",
"taskType": "在制品",
"conditionItemType": "",
"conditionCount": ">0",
"instanceCountRange": "0_1"
}',365,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_工艺员指定处理意见','CustHtmlNode','工艺员指定处理意见','{
"head": "MES系统",
"taskName": "工艺员指定处理意见",
"taskType": "在制品",
"conditionItemType": "",
"conditionCount": "",
"instanceCountRange": "1"
}',190,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_在制品返修_在制品返修工艺编制中','CustHtmlNode','在制品返修工艺编制中','{
"head": "PDM系统",
"taskName": "在制品返修工艺编制中",
"taskType": "在制品",
"conditionItemType": "返修",
"conditionCount": "",
"instanceCountRange": "0_n"
}',200,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_在制品返修_MES收到返修工艺任务','CustHtmlNode','MES收到返修工艺任务','{
"head": "PDM系统",
"taskName": "MES收到返修工艺任务",
"taskType": "在制品",
"conditionItemType": "返修",
"conditionCount": "",
"instanceCountRange": "1"
}',210,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_在制品返修_在制品返修工艺跳转','CustHtmlNode','在制品返修工艺跳转','{
"head": "MES系统",
"taskName": "在制品返修工艺跳转",
"taskType": "在制品",
"conditionItemType": "返修",
"conditionCount": "",
"instanceCountRange": "1"
}',220,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_在制品返修_在制品返修返修执行中','CustHtmlNode','在制品返修返修执行中','{
"head": "MES系统",
"taskName": "在制品返修返修执行中",
"taskType": "在制品",
"conditionItemType": "返修",
"conditionCount": "",
"instanceCountRange": "1"
}',230,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_在制品返修_结束','circle','结束','{
                "r": 30
            }',240,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_无影响_在制品无影响','CustHtmlNode','在制品无影响','{
"head": "MES系统",
"taskName": "在制品无影响",
"taskType": "在制品",
"conditionItemType": "无影响",
"conditionCount": "",
"instanceCountRange": "0_n"
}',330,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_整件外协是否有影响_外协数量=0_结束','circle','结束','{
"r": 30,
"taskState": "已完成",
"conditionItemType": "整件外协",
"conditionCount": "=0",
"instanceCountRange": "0_1"
}',60,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_已提前返修_在制品已提前返修意见返回PDM','CustHtmlNode','在制品已提前返修','{
"head": "MES系统",
"taskName": "在制品已提前返修意见返回PDM",
"taskType": "在制品",
"conditionItemType": "已提前返修",
"conditionCount": "",
"instanceCountRange": "0_n"
}',245,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_已提前返修_在制品已提前返修完成','CustHtmlNode','在制品已提前返修完成','{
"head": "MES系统",
"taskName": "在制品已提前返修完成"
}',246,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_在制品是否有影响_在制品数量>0_已提前返修_结束','circle','结束','{
                "r": 30
            }',247,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品已提前返修_已制品已提前返修意见返回PDM','CustHtmlNode','已制品已提前返修','{
"head": "NC系统",
"taskName": "已制品已提前返修意见返回PDM",
"taskType": "已制品",
"conditionItemType": "已提前返修",
"conditionCount": "",
"instanceCountRange": "0_n"
}',125,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品已提前返修_已制品已提前返修完成','CustHtmlNode','已制品已提前返修完成','{
"head": "NC系统",
"taskName": "已制品已提前返修完成"
}',126,NULL);
INSERT INTO DW_NODE_CONFIG (ID,NODE_TYPE,NODE_TEXT,NODE_PROPERTIES,NODE_ORDER,IS_DELETED) VALUES ('id_已制品是否有影响_已制品已提前返修_结束','circle','结束','{
                "r": 30
            }',127,NULL);

3. 导入DW_EDGE_CONFIG数据
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('18','polyline','id_在制品是否有影响','id_在制品是否有影响_在制品数量>0_NC推送MES在制品影响分析任务','在制品数量>0',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('19','polyline','id_在制品是否有影响_在制品数量>0_NC推送MES在制品影响分析任务','id_在制品是否有影响_在制品数量>0_工艺员指定处理意见',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('20','polyline','id_在制品是否有影响_在制品数量>0_工艺员指定处理意见','id_在制品是否有影响_在制品数量>0_在制品返修_在制品返修工艺编制中','在制品返修',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('21','polyline','id_在制品是否有影响_在制品数量>0_在制品返修_在制品返修工艺编制中','id_在制品是否有影响_在制品数量>0_在制品返修_MES收到返修工艺任务',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('22','polyline','id_在制品是否有影响_在制品数量>0_在制品返修_MES收到返修工艺任务','id_在制品是否有影响_在制品数量>0_在制品返修_在制品返修工艺跳转',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('23','polyline','id_在制品是否有影响_在制品数量>0_在制品返修_在制品返修工艺跳转','id_在制品是否有影响_在制品数量>0_在制品返修_在制品返修返修执行中',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('24','polyline','id_在制品是否有影响_在制品数量>0_在制品返修_在制品返修返修执行中','id_在制品是否有影响_在制品数量>0_在制品返修_结束',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('25','polyline','id_在制品是否有影响_在制品数量>0_工艺员指定处理意见','id_在制品是否有影响_在制品数量>0_在制品报废_在制品报废审批中','在制品报废',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('26','polyline','id_在制品是否有影响_在制品数量>0_在制品报废_在制品报废审批中','id_在制品是否有影响_在制品数量>0_在制品报废_在制品报废完成',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('27','polyline','id_在制品是否有影响_在制品数量>0_在制品报废_在制品报废完成','id_在制品是否有影响_在制品数量>0_在制品报废_结束',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('28','polyline','id_在制品是否有影响_在制品数量>0_工艺员指定处理意见','id_在制品是否有影响_在制品数量>0_工艺升版_在制品工艺升版意见返回PDM','工艺升版',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('29','polyline','id_在制品是否有影响_在制品数量>0_工艺升版_在制品工艺升版意见返回PDM','id_在制品是否有影响_在制品数量>0_工艺升版_在制品工艺升版完成',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('30','polyline','id_在制品是否有影响_在制品数量>0_工艺升版_在制品工艺升版完成','id_在制品是否有影响_在制品数量>0_工艺升版_结束',NULL,NULL);

INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('40','polyline','id_在制品是否有影响_在制品数量>0_工艺员指定处理意见','id_在制品是否有影响_在制品数量>0_已提前返修_在制品已提前返修意见返回PDM','已提前返修',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('41','polyline','id_在制品是否有影响_在制品数量>0_已提前返修_在制品已提前返修意见返回PDM','id_在制品是否有影响_在制品数量>0_已提前返修_在制品已提前返修完成',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('42','polyline','id_在制品是否有影响_在制品数量>0_已提前返修_在制品已提前返修完成','id_在制品是否有影响_在制品数量>0_已提前返修_结束',NULL,NULL);

INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('31','polyline','id_在制品是否有影响_在制品数量>0_工艺员指定处理意见','id_在制品是否有影响_在制品数量>0_外协返修_外协返修','外协返修',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('32','polyline','id_在制品是否有影响_在制品数量>0_外协返修_外协返修','id_在制品是否有影响_在制品数量>0_外协返修_结束',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('33','polyline','id_在制品是否有影响_在制品数量>0_工艺员指定处理意见','id_在制品是否有影响_在制品数量>0_无影响_在制品无影响','无影响',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('34','polyline','id_在制品是否有影响_在制品数量>0_无影响_在制品无影响','id_在制品是否有影响_在制品数量>0_无影响_结束',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('37','polyline','id_在制品是否有影响','id_在制品是否有影响_在制品数量=0_无影响_在制品无影响','在制品数量=0',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('38','polyline','id_在制品是否有影响_在制品数量=0_无影响_在制品无影响','id_在制品是否有影响_在制品数量=0_无影响_结束',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('1','polyline','id_开始','id_NC处理影响分析任务',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('2','polyline','id_NC处理影响分析任务','id_整件外协是否有影响',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('3','polyline','id_NC处理影响分析任务','id_已制品是否有影响',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('4','polyline','id_NC处理影响分析任务','id_在制品是否有影响',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('5','polyline','id_整件外协是否有影响','id_整件外协是否有影响_外协数量>0_NC返回闭环结果','外协数量>0',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('6','polyline','id_整件外协是否有影响_外协数量>0_NC返回闭环结果','id_整件外协是否有影响_外协数量>0_结束',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('8','polyline','id_已制品是否有影响','id_已制品是否有影响_已制品返修_已制品返修工艺编制中','已制品返修',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('9','polyline','id_已制品是否有影响','id_已制品是否有影响_已制品报废_已制品报废中','已制品报废',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('10','polyline','id_已制品是否有影响','id_已制品是否有影响_已制品无影响_已制品无影响','已制品无影响',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('11','polyline','id_已制品是否有影响_已制品返修_已制品返修工艺编制中','id_已制品是否有影响_已制品返修_NC收到返修工艺任务',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('12','polyline','id_已制品是否有影响_已制品返修_NC收到返修工艺任务','id_已制品是否有影响_已制品返修_已制品返修工艺计划下达',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('13','polyline','id_已制品是否有影响_已制品返修_已制品返修工艺计划下达','id_已制品是否有影响_已制品返修_已制品返修执行中',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('14','polyline','id_已制品是否有影响_已制品返修_已制品返修执行中','id_已制品是否有影响_已制品返修_结束',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('15','polyline','id_已制品是否有影响_已制品报废_已制品报废中','id_已制品是否有影响_已制品报废_已制品报废完成',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('16','polyline','id_已制品是否有影响_已制品报废_已制品报废完成','id_已制品是否有影响_已制品报废_结束',NULL,NULL);

INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('43','polyline','id_已制品是否有影响','id_已制品是否有影响_已制品已提前返修_已制品已提前返修意见返回PDM','已制品已提前返修',NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('44','polyline','id_已制品是否有影响_已制品已提前返修_已制品已提前返修意见返回PDM','id_已制品是否有影响_已制品已提前返修_已制品已提前返修完成',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('45','polyline','id_已制品是否有影响_已制品已提前返修_已制品已提前返修完成','id_已制品是否有影响_已制品已提前返修_结束',NULL,NULL);

INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('17','polyline','id_已制品是否有影响_已制品无影响_已制品无影响','id_已制品是否有影响_已制品无影响_结束',NULL,NULL);
INSERT INTO DW_EDGE_CONFIG (ID,EDGE_TYPE,SOURCENODEID,TARGETNODEID,EDGE_TEXT,IS_DELETED) VALUES ('39','polyline','id_整件外协是否有影响','id_整件外协是否有影响_外协数量=0_结束','外协数量=0',NULL);

4. ant jc

5. 重启服务

