package ext.sast.common.fc;

public class CommonQueryParams {
    public CommonQueryParams(String key,String fuhao,String value){
        this.key = key;
        this.fuhao = fuhao;
        this.value = value;
    }
    private String key;
    private String fuhao;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFuhao() {
        return fuhao;
    }

    public void setFuhao(String fuhao) {
        this.fuhao = fuhao;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
