package ext.casc.distribute.vo;

import java.util.List;

public class NcResponseDataVo {
    private String Dispatcher;
    private String GGCode;
    private String Partid;
    private String ResType;
    private List<NcYzpDetailEntryVo> YZPDetail;
    private List<NcZZPZZDetailEntryVo> ZZPZZDetail;
    private List<NcZZPWXDetailEntryVo> ZZPWXDetail;

    public String getDispatcher() {
        return Dispatcher;
    }

    public void setDispatcher(String dispatcher) {
        this.Dispatcher = dispatcher;
    }

    public String getGGCode() {
        return GGCode;
    }

    public void setGGCode(String GGCode) {
        this.GGCode = GGCode;
    }

    public String getPartid() {
        return Partid;
    }

    public void setPartid(String partid) {
        this.Partid = partid;
    }

    public String getResType() {
        return ResType;
    }

    public void setResType(String resType) {
        this.ResType = resType;
    }

    public List<NcYzpDetailEntryVo> getYZPDetail() {
        return YZPDetail;
    }

    public void setYZPDetail(List<NcYzpDetailEntryVo> YZPDetail) {
        this.YZPDetail = YZPDetail;
    }

    public List<NcZZPZZDetailEntryVo> getZZPZZDetail() {
        return ZZPZZDetail;
    }

    public void setZZPZZDetail(List<NcZZPZZDetailEntryVo> ZZPZZDetail) {
        this.ZZPZZDetail = ZZPZZDetail;
    }

    public List<NcZZPWXDetailEntryVo> getZZPWXDetail() {
        return ZZPWXDetail;
    }

    public void setZZPWXDetail(List<NcZZPWXDetailEntryVo> ZZPWXDetail) {
        this.ZZPWXDetail = ZZPWXDetail;
    }
}
