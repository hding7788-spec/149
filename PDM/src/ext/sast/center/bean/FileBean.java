package ext.sast.center.bean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/18
 * @ Description：跨域文件信息
 * @ Modified By：
 */
public class FileBean {
    /**文件唯一标识*/
    private String file_id;

    /**文件名*/
    private String file_name;

    /**文件大小*/
    private String file_size;

    /**AES加密的key*/
    private String key;

    /**AES加密的ivkey*/
    private String ivkey;

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIvkey() {
        return ivkey;
    }

    public void setIvkey(String ivkey) {
        this.ivkey = ivkey;
    }
}
