package com.glaway.mpm.visual.conf;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;



public interface VaConstants {
	////////////////wanghaoyu add
	String PVIEW_OL = ".ol";
	String PVIEW_PVS = ".pvs";
	//////////////////
	 String         TOP_GROUP                         = "SAMC";
	   String         VAR_TOP_GROUP                     = "topGroup";

	   // 软属性定义
	   String         IBA_SAMC_MANUFACTURY              = "SAMC_manufactury";                                   // 制造单位
	   String         IBA_SAMC_CPI                      = "SAMC_CPI";                                           // CPI
	   String         IBA_SAMC_workspan                 = "SAMC_workspan";                                      //工时
	   String         IBA_SAMC_deptCode                 = "SAMC_deptCode";                                      //工位代码
	   String         IBA_SAMC_effectivity              = "SAMC_effectivity";                                   //有效性
	   String         IBA_SAMC_staCode                  = "SAMC_staCode";                                       //站位代码
	   String         IBA_SAMC_NextAssy                 = "SAMC_nextAssy";                                      //下级工程组件
	   String         IBA_SAMC_AOH                      = "SAMC_AOH";                                           //AO号
	   String         IBA_SAMC_RELATIVEUPPER               = "SAMC_RelativeUpper";                                    //站位

	   String         IBA_Tooling_Version               = "Tooling_Version";                                    //工装T次

	   // 软类型定义
	   String         TYPE_PART_LO                      = "wt.part.WTPart|com.ideal.LO";                        // LO
	   String         TYPE_PART_AO                      = "wt.part.WTPart|com.ideal.AO";                        //AO组件
	   String         TYPE_PART_AAO                     = "wt.part.WTPart|com.ideal.AAO";                       //AO组件
	   String         TYPE_DOC_AO                       = "wt.doc.WTDocument|com.ideal.工艺文档|com.ideal.工艺文档AO";  // AO文档
	   String         TYPE_TOOLING                      = "wt.part.WTPart";                                     // 工装
	   String         TYPE_DOC_GYBZ                     = "wt.doc.WTDocument";                                  // 工艺标准
	   String         TYPE_GY_RES                       = "wt.part.WTPart";                                     // 工艺资料
	   String         Type_DOC_NORMAL                   = "wt.doc.WTDocument";                                  // 一般资料

	   String         TYPE_DOC_DATA_MBOM                = "wt.doc.WTDocument|com.ideal.数据包|com.ideal.数据包MBOM";  // 数据包MBOM
	   String         TYPE_DOC_DATA_SPSBOM              = "wt.doc.WTDocument|com.ideal.数据包|com.ideal.数据包SPSBOM"; // 数据包SPSBOM
	   String         TYPE_DOC_DATA_AO                  = "wt.doc.WTDocument|com.ideal.数据包|com.ideal.数据包AO";    // 数据包AO com.ideal.SPS零件交付清单

	   String         TYPE_DOC_SPS_LISTING              = "wt.doc.WTDocument|com.ideal.SPS零件交付清单";              // SPS零件交付清单

	   String         TYPE_PART_SPS                     = "wt.part.WTPart|com.ideal.SPSPackage";                //SPS工作包
	   String         TYPE_PART_SUBSPS                  = "wt.part.WTPart|com.ideal.SPSSubPackage";              //SPS工作分包
	   String         TYPE_DOC_SPS                      = "wt.doc.WTDocument|com.ideal.工艺文档|com.ideal.工艺文档SPS"; // SPS文档

	   String         TYPE_PAET_GONGWEI                 = "wt.part.WTPart|com.ideal.gongwei";                   //工位
	   String         TYPE_PAET_ZHANWEI                 = "wt.part.WTPart|com.ideal.zhanwei";                   //站位

	   String         TOOLS_FINISHED_PRODUCT            = "wt.part.WTPart|com.ideal.gongzhuangchengpin";         //工装成品

	   String         TOOLS_DOC_GONGZHUANGERWEITUYANG   = "wt.doc.WTDocument|com.ideal.工装文档|com.ideal.gongzhuangerweituyang";   //工装二维图样

	   String         TOOLS_DOC_GONGZHUANGSHUMOJITUYANG = "wt.doc.WTDocument|com.ideal.工装文档|com.ideal.gongzhuangshumojituyang"; //工装数模及图样

	   // 软类型标识定义
	   TypeIdentifier TI_Part_AO                        = TypeHelper.getTypeIdentifier(TYPE_PART_AO);
	   TypeIdentifier TI_Part_SPS                       = TypeHelper.getTypeIdentifier(TYPE_PART_SPS);
	   TypeIdentifier TI_Part_SUBSPS                    = TypeHelper.getTypeIdentifier(TYPE_PART_SUBSPS);
	   TypeIdentifier TI_Part_AAO                       = TypeHelper.getTypeIdentifier(TYPE_PART_AAO);
	   TypeIdentifier TI_DOC_AO                         = TypeHelper.getTypeIdentifier(TYPE_DOC_AO);
	   TypeIdentifier TI_TOOLING                        = TypeHelper.getTypeIdentifier(TYPE_TOOLING);
	   TypeIdentifier TI_DOC_GYBZ                       = TypeHelper.getTypeIdentifier(TYPE_DOC_GYBZ);
	   TypeIdentifier TI_GY_RES                         = TypeHelper.getTypeIdentifier(TYPE_GY_RES);
	   TypeIdentifier TI_DOC_NORMAL                     = TypeHelper.getTypeIdentifier(Type_DOC_NORMAL);
	   TypeIdentifier TI_DOC_DATA_MBOM                  = TypeHelper.getTypeIdentifier(TYPE_DOC_DATA_MBOM);
	   TypeIdentifier TI_DOC_DATA_SPSBOM                = TypeHelper.getTypeIdentifier(TYPE_DOC_DATA_SPSBOM);
	   TypeIdentifier TI_DOC_DATA_AO                    = TypeHelper.getTypeIdentifier(TYPE_DOC_DATA_AO);

	   TypeIdentifier TI_DOC_SPS_LISTING                = TypeHelper.getTypeIdentifier(TYPE_DOC_SPS_LISTING);
	   TypeIdentifier TI_TYPE_PAET_GONGWEI              = TypeHelper.getTypeIdentifier(TYPE_PAET_GONGWEI);
	   TypeIdentifier TI_TYPE_PAET_ZHANWEI              = TypeHelper.getTypeIdentifier(TYPE_PAET_ZHANWEI);

	}
