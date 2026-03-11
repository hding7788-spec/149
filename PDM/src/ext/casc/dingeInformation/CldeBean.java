package ext.casc.dingeInformation;

public class CldeBean {

	private String dataType;//数据类型：标准件(BZJ),元器件(YQJ),金属材料(JSCL),非金属材料(FJSCL),复合材料(FHCL)
	private String number;// 编号
	private String sjbm;//设计编码
	private String name;// 名称
	private String wzjc;// 物资简称
	private String bmyyjb;// 编码优选级别
	private String bmzt;// 编码状态
	private String bmlx;// 编码类型
	private String xh;// 型号
	private String cllx;// 材料类型
	private String hsl;// 换算率
	private String xs;//系数
	private String ph;// 牌号
	private String bzh;// 标准号
	private String gg;// 规格
	private String gyzt;// 供应状态
	private String cybz;// 采用标准
	private String jd;// 精度
	private String zltz;// 质量特征
	private String pzggbz;// 品种规格标准
	private String cl;// 材料
	private String xhgg;// 型号规格
	private String zldj;// 质量等级
	private String zgf;// 总规范
	private String xxgf;// 详细规范
	private String fzxs;// 封装形式
	private String wxcc;// 外形尺寸
	private String zytj;// 专用条件
	private String fjxy;// 附加协议
	private String jxxndjhyd;// 机械性能等级或硬度
	private String bmcl;// 表面处理
	private String rcl;// 热处理
	private String cpxs;// 产品型式
	private String cpdj;// 产品等级
	private String bnxs;// 板拧形式
	private String tssm;// 特殊说明
	private String sfjk;// 是否进口
	private String jldw;// 计量单位
	private String syfw;//适用范围

	private String gysl;//工艺数量
	private String dw;//单位
	private String sjsl;//设计数量
	private String kzjs;//可制件数
	private String xlcc;//下料尺寸
	private String sl;//数量
	private String sjkzjs;//试件可制件数
	private String sjcc;//试件尺寸
	private String partNumber;
	private String parentPartNumber;
	private String xyml;//选用目录
	private String fl;//分类
	private String gys;//供应商
	private String datafrom;//数据来源
	private String jstj;//技术条件
	private String sccj;//生产厂家
	private String dcstxyq;//电参考特选要求
    private String comment;//备注

	public String getDcstxyq() {
		return dcstxyq;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public void setDcstxyq(String dcstxyq) {
		this.dcstxyq = dcstxyq;
	}
	public String getSccj() {
		return sccj;
	}
	public void setSccj(String sccj) {
		this.sccj = sccj;
	}
	public String getJstj() {
		return jstj;
	}
	public void setJstj(String jstj) {
		this.jstj = jstj;
	}
	public String getDatafrom() {
		return datafrom;
	}
	public void setDatafrom(String datafrom) {
		this.datafrom = datafrom;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getSjbm() {
		return sjbm;
	}
	public void setSjbm(String sjbm) {
		this.sjbm = sjbm;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWzjc() {
		return wzjc;
	}
	public void setWzjc(String wzjc) {
		this.wzjc = wzjc;
	}
	public String getBmyyjb() {
		return bmyyjb;
	}
	public void setBmyyjb(String bmyyjb) {
		this.bmyyjb = bmyyjb;
	}
	public String getBmzt() {
		return bmzt;
	}
	public void setBmzt(String bmzt) {
		this.bmzt = bmzt;
	}
	public String getBmlx() {
		return bmlx;
	}
	public void setBmlx(String bmlx) {
		this.bmlx = bmlx;
	}
	public String getXh() {
		return xh;
	}
	public void setXh(String xh) {
		this.xh = xh;
	}
	public String getCllx() {
		return cllx;
	}
	public void setCllx(String cllx) {
		this.cllx = cllx;
	}
	public String getHsl() {
		return hsl;
	}
	public void setHsl(String hsl) {
		this.hsl = hsl;
	}
	public String getXs() {
		return xs;
	}
	public void setXs(String xs) {
		this.xs = xs;
	}
	public String getPh() {
		return ph;
	}
	public void setPh(String ph) {
		this.ph = ph;
	}
	public String getBzh() {
		return bzh;
	}
	public void setBzh(String bzh) {
		this.bzh = bzh;
	}
	public String getGg() {
		return gg;
	}
	public void setGg(String gg) {
		this.gg = gg;
	}
	public String getGyzt() {
		return gyzt;
	}
	public void setGyzt(String gyzt) {
		this.gyzt = gyzt;
	}
	public String getCybz() {
		return cybz;
	}
	public void setCybz(String cybz) {
		this.cybz = cybz;
	}
	public String getJd() {
		return jd;
	}
	public void setJd(String jd) {
		this.jd = jd;
	}
	public String getZltz() {
		return zltz;
	}
	public void setZltz(String zltz) {
		this.zltz = zltz;
	}
	public String getPzggbz() {
		return pzggbz;
	}
	public void setPzggbz(String pzggbz) {
		this.pzggbz = pzggbz;
	}
	public String getCl() {
		return cl;
	}
	public void setCl(String cl) {
		this.cl = cl;
	}
	public String getXhgg() {
		return xhgg;
	}
	public void setXhgg(String xhgg) {
		this.xhgg = xhgg;
	}
	public String getZldj() {
		return zldj;
	}
	public void setZldj(String zldj) {
		this.zldj = zldj;
	}
	public String getZgf() {
		return zgf;
	}
	public void setZgf(String zgf) {
		this.zgf = zgf;
	}
	public String getXxgf() {
		return xxgf;
	}
	public void setXxgf(String xxgf) {
		this.xxgf = xxgf;
	}
	public String getFzxs() {
		return fzxs;
	}
	public void setFzxs(String fzxs) {
		this.fzxs = fzxs;
	}
	public String getWxcc() {
		return wxcc;
	}
	public void setWxcc(String wxcc) {
		this.wxcc = wxcc;
	}
	public String getZytj() {
		return zytj;
	}
	public void setZytj(String zytj) {
		this.zytj = zytj;
	}
	public String getFjxy() {
		return fjxy;
	}
	public void setFjxy(String fjxy) {
		this.fjxy = fjxy;
	}
	public String getJxxndjhyd() {
		return jxxndjhyd;
	}
	public void setJxxndjhyd(String jxxndjhyd) {
		this.jxxndjhyd = jxxndjhyd;
	}
	public String getBmcl() {
		return bmcl;
	}
	public void setBmcl(String bmcl) {
		this.bmcl = bmcl;
	}
	public String getRcl() {
		return rcl;
	}
	public void setRcl(String rcl) {
		this.rcl = rcl;
	}
	public String getCpxs() {
		return cpxs;
	}
	public void setCpxs(String cpxs) {
		this.cpxs = cpxs;
	}
	public String getCpdj() {
		return cpdj;
	}
	public void setCpdj(String cpdj) {
		this.cpdj = cpdj;
	}
	public String getBnxs() {
		return bnxs;
	}
	public void setBnxs(String bnxs) {
		this.bnxs = bnxs;
	}
	public String getTssm() {
		return tssm;
	}
	public void setTssm(String tssm) {
		this.tssm = tssm;
	}
	public String getSfjk() {
		return sfjk;
	}
	public void setSfjk(String sfjk) {
		this.sfjk = sfjk;
	}
	public String getJldw() {
		return jldw;
	}
	public void setJldw(String jldw) {
		this.jldw = jldw;
	}
	public String getSyfw() {
		return syfw;
	}
	public void setSyfw(String syfw) {
		this.syfw = syfw;
	}
	public String getGysl() {
		return gysl;
	}
	public void setGysl(String gysl) {
		this.gysl = gysl;
	}
	public String getDw() {
		return dw;
	}
	public void setDw(String dw) {
		this.dw = dw;
	}
	public String getSjsl() {
		return sjsl;
	}
	public void setSjsl(String sjsl) {
		this.sjsl = sjsl;
	}
	public String getKzjs() {
		return kzjs;
	}
	public void setKzjs(String kzjs) {
		this.kzjs = kzjs;
	}
	public String getXlcc() {
		return xlcc;
	}
	public void setXlcc(String xlcc) {
		this.xlcc = xlcc;
	}
	public String getSl() {
		return sl;
	}
	public void setSl(String sl) {
		this.sl = sl;
	}
	public String getSjkzjs() {
		return sjkzjs;
	}
	public void setSjkzjs(String sjkzjs) {
		this.sjkzjs = sjkzjs;
	}
	public String getSjcc() {
		return sjcc;
	}
	public void setSjcc(String sjcc) {
		this.sjcc = sjcc;
	}
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public String getParentPartNumber() {
		return parentPartNumber;
	}
	public void setParentPartNumber(String parentPartNumber) {
		this.parentPartNumber = parentPartNumber;
	}
	public String getXyml() {
		return xyml;
	}
	public void setXyml(String xyml) {
		this.xyml = xyml;
	}
	public String getFl() {
		return fl;
	}
	public void setFl(String fl) {
		this.fl = fl;
	}
	public String getGys() {
		return gys;
	}
	public void setGys(String gys) {
		this.gys = gys;
	}


}
