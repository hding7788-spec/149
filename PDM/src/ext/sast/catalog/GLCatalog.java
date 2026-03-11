package ext.sast.catalog;

import java.io.Externalizable;

import wt.fc.WTObject;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.IconProperties;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.StringCase;
import com.ptc.windchill.annotations.metadata.SupportedAPI;

@GenAsPersistable(superClass = WTObject.class, interfaces = {Externalizable.class}, properties = {
	// 标准号
	@GeneratedProperty(name = "bzh", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(stringCase=StringCase.UPPER_CASE, required = true), columnProperties = @ColumnProperties(index = true, columnName = "GLCatalogNumber")),
	// 目录名称
	@GeneratedProperty(name = "catalogname", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true, columnName = "GLCatalogName")),
	// 类别
	@GeneratedProperty(name = "catalogtype", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 目录级别
	@GeneratedProperty(name = "grade", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 适用范围
	@GeneratedProperty(name = "scope", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 状况
	@GeneratedProperty(name = "state", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 创建单位
	@GeneratedProperty(name = "createunit", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 创建者
	@GeneratedProperty(name = "creator", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 修改者
	@GeneratedProperty(name = "modifier", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 目录类型
	@GeneratedProperty(name = "cataloglinktype", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 备注
	@GeneratedProperty(name = "remark", type = String.class, supportedAPI = SupportedAPI.PUBLIC)}, iconProperties = @IconProperties(standardIcon = "netmarkets/images/catalog.gif", openIcon = "netmarkets/images/catalog.gif"))
public class GLCatalog extends _GLCatalog{
	public static final long serialVersionUID = 1;

	public static GLCatalog newGLCatalog() throws WTException {
		GLCatalog instance = new GLCatalog();
        instance.initialize();
        return instance;
    }
}

