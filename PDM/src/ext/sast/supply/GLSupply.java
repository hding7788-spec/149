package ext.sast.supply;

import java.io.Externalizable;

import wt.fc.IdentificationObject;
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
	// 编号
	@GeneratedProperty(name = "number", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(stringCase=StringCase.UPPER_CASE, required = true), columnProperties = @ColumnProperties(index = true, unique=true, columnName = "GLSupplyNumber")),
	// 名称
	@GeneratedProperty(name = "name", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true, unique=true, columnName = "GLSupplyName")),
	// 供应商代号
	@GeneratedProperty(name = "code", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 简称
	@GeneratedProperty(name = "jc", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 曾用名
	@GeneratedProperty(name = "cym", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 别称
	@GeneratedProperty(name = "bc", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 物资类别
	@GeneratedProperty(name = "wzlb", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true)),
	// 认定产品
	@GeneratedProperty(name = "rdcp", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 企业性质
	@GeneratedProperty(name = "qyxz", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 联系人
	@GeneratedProperty(name = "lxr", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 联系电话
	@GeneratedProperty(name = "lxdh", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 传真
	@GeneratedProperty(name = "cz", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 电子邮箱
	@GeneratedProperty(name = "email", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 通信地址
	@GeneratedProperty(name = "address", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 邮政编码
	@GeneratedProperty(name = "zipcode", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 分类等级
	@GeneratedProperty(name = "classgrade", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
	// 状态
	@GeneratedProperty(name = "state", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true)),
	// 备注
	@GeneratedProperty(name = "remark", type = String.class, supportedAPI = SupportedAPI.PUBLIC)}, iconProperties = @IconProperties(standardIcon = "netmarkets/images/supply.gif", openIcon = "netmarkets/images/supply.gif"))
public class GLSupply extends _GLSupply{
	public static final long serialVersionUID = 1;

	public static GLSupply newGLSupply() throws WTException {
		GLSupply instance = new GLSupply();
        instance.initialize();
        return instance;
    }

}
