package ext.casc.report.technics;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.util.WCUtil;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.part.WTPart;
import wt.services.StandardManager;
import wt.util.WTException;

import java.io.File;
import java.io.Serializable;

public class StandardDownloadTechnicsReportService extends StandardManager implements DownloadTechnicsReportService, Serializable {

	private static final long serialVersionUID = 1L;

	public static StandardDownloadTechnicsReportService newStandardDownloadTechnicsReportService() throws WTException {
		StandardDownloadTechnicsReportService service = new StandardDownloadTechnicsReportService();
		service.initialize();
		return service;
	}

	@Override
	public File export(String oid, String type, String batch) throws WTException {
		if(oid == null || "".equals(oid)) {
			System.out.println("-----oid-----"+oid);
			return null;
		}
		if(type == null || "".equals(type)) {
			System.out.println("-----type-----"+type);
			return null;
		}

		Persistable per = WCUtil.getPersistable(oid);
		if(per instanceof WTPart) {
			WTPart part = (WTPart)per;
			if(type.equals("gongxugongshidingehuizong")) {
				return DownloadTechnicsReportUtil.exportGongXuGongShiDingEHuiZong(part,batch);
			} else if(type.equals("gongyiluxianhuizong")) {

			} else if(type.equals("cailiaoxiaihaohuizong")) {
				return DownloadTechnicsReportUtil.exportCaiLiaoXiaiHaoHuiZong(part,batch);
			} else if(type.equals("fuliaoxiaohaohuizong")) {
				return DownloadTechnicsReportUtil.exportFuLiaoXiaiHaoHuiZong(part,batch);
			} else if(type.equals("yuanqijianxiaohaohuizong")) {
				return DownloadTechnicsReportUtil.exportYuanQiJianXiaoHaoHuiZong(part,batch);
			} else if(type.equals("biaozhunjianxiaohaohuizong")) {
				return DownloadTechnicsReportUtil.exportBiaoZhunJianXiaoHaoHuiZong(part,batch);
			} else if(type.equals("waigoujianhuizong")) {
				return DownloadTechnicsReportUtil.exportWaiGouJianHuiZong(part,batch);
			} else if(type.equals("waixiejianhuizong")) {
				return DownloadTechnicsReportUtil.exportWaiXieJianHuiZong(part,batch);
			} else if(type.equals("gongyizhuangbeihuizong")) {
				return DownloadTechnicsReportUtil.exportGongyizhuangbeihuizong(part,batch);
			} else if(type.equals("daoliangjuhuizong")) {
				return DownloadTechnicsReportUtil.exportDaoliangjuhuizong(part,batch);
			} else if(type.equals("yiqiyibiaohuizong")) {
				return DownloadTechnicsReportUtil.exportYiqiyibiaohuizong(part,batch);
			} else if(type.equals("chanpinbutaodingehuizong")) {
				return DownloadTechnicsReportUtil.exportChanpinbutaodingezonghui(part,batch);
			} else if(type.equals("cldewczthz")){
				return DownloadTechnicsReportUtil.exportCldeWcztHz(part,batch);
			}
		}else if(per instanceof WTDocument){
			WTDocument doc = (WTDocument)per;
			if(type.equals("peitaomingxibiao")) {
				return DownloadTechnicsReportUtil.exportPeitaomingxibiao(doc);
			}else if(type.equals("gongxupeitao")){
				return DownloadTechnicsReportUtil.exportGongXuPeiTao(doc);
			}
		}else if(per instanceof ProcessEnvelope){
			ProcessEnvelope envelope = (ProcessEnvelope) per;
			if(type.equals("exportSignatureAdvise")) {
				return DownloadTechnicsReportUtil.exportSignatureAdvise(envelope,"signature");
			}

		} else if (per instanceof ChangePackaged){
			ChangePackaged changePackaged = (ChangePackaged) per;
			if(type.equals("exportSignatureAdvise")) {
				return DownloadTechnicsReportUtil.exportSignatureAdvise(changePackaged,"change");
			}
		}else {
			System.out.println("oid not is a WTPart");
			return null;
		}

		return null;
	}

}
