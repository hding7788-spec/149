package ext.casc.capp;

import java.io.File;

/**
 * CAPP传递的工艺文件
 * 
 * @author Administrator
 */
public class CAPPCraft {
    private String number;
    private String name;
    private String partNumber;
    private String fileSate;// 正样先行工艺、    并行工艺、  模样工艺、  蓝图工艺
    private String type;// 工艺更改单、 工艺技术通知单、   临时工艺文件 工艺规程（包括：正样先行工艺、并行工艺、模样工艺、蓝图工艺）
    private String version;
    private String changedCraftNum;//普通工艺文件为空；如果文件类型为工艺更改单，则需手工填写更改单对应更改后工艺文件编号或由CAPP自动获取
    private String changedCraftVersion;
    private String craftor;//工艺员
    private String department;//工艺文件中填写的编制部门
    private String productName;//所属型号   需要与PDM产品库名称一致，确定存储的产品库位置
    
    private File file;
    
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    public String getFileSate() {
        return fileSate;
    }
    public void setFileSate(String fileSate) {
        this.fileSate = fileSate;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getChangedCraftNum() {
        return changedCraftNum;
    }
    public void setChangedCraftNum(String changedCraftNum) {
        this.changedCraftNum = changedCraftNum;
    }
    public String getChangedCraftVersion() {
        return changedCraftVersion;
    }
    public void setChangedCraftVersion(String changedCraftVersion) {
        this.changedCraftVersion = changedCraftVersion;
    }
    public String getCraftor() {
        return craftor;
    }
    public void setCraftor(String craftor) {
        this.craftor = craftor;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    
}
