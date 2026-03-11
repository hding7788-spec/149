package ext.casc.distribute.vo;

import java.util.List;

public class MesStatusResultVo {
    private String STATE;
    private String MESSAGE;
    private List<MesDataEntryVo> DATA;

    public String getSTATE() {
        return STATE;
    }

    public void setSTATE(String STATE) {
        this.STATE = STATE;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public List<MesDataEntryVo> getDATA() {
        return DATA;
    }

    public void setDATA(List<MesDataEntryVo> DATA) {
        this.DATA = DATA;
    }
}
