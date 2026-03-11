<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE qml SYSTEM "/wt/query/qml/qml.dtd">
<qml bypassAccessControl="false">
	<parameter name="isDefaultRep" type="java.lang.Object">
		<parameterDefault isMacro="false">
			1
		</parameterDefault>
	</parameter>
	<parameter name="appDataDesc" type="java.lang.Object">
		<parameterDefault isMacro="false">
			BBOX*
		</parameterDefault>
	</parameter>
	<parameter name="appDataRole" type="wt.content.ContentRoleType">
		<parameterDefault isMacro="false">
			SECONDARY
		</parameterDefault>
	</parameter>
	<parameter name="oidKeys" type="java.lang.Object">
		<parameterDefault isMacro="false">
			1
		</parameterDefault>
	</parameter>
	<statement>
		<query>
			<select distinct="false" group="false">
				<column alias="Part (wt.part.WTPart)" heading="oidKey" isExternal="false" propertyName="persistInfo.objectIdentifier.id" selectOnly="false" type="long">
					thePersistInfo.theObjectIdentifier.id
				</column>
				<object alias="Part (wt.part.WTPart)" heading="objectIdentifier" propertyName="persistInfo.objectIdentifier.stringValue">
					<property name="persistInfo">
						<property name="objectIdentifier">
							<property name="stringValue"/>
						</property>
					</property>
				</object>
				<column alias="Application Data" heading="Description" isExternal="false" propertyName="description" selectOnly="false" type="java.lang.String">
					description
				</column>
				<column alias="Application Data" heading="FileName" isExternal="false" propertyName="fileName" selectOnly="false" type="java.lang.String">
					fileName
				</column>
			</select>
			<from>
				<table alias="wt.viewmarkup.DerivedImage" isExternal="false">
					wt.viewmarkup.DerivedImage
				</table>
				<table alias="Application Data" isExternal="false">
					wt.content.ApplicationData
				</table>
				<table alias="Part (wt.part.WTPart)" isExternal="false">
					wt.part.WTPart
				</table>
			</from>
			<where>
				<compositeCondition type="and">
					<condition>
						<operand>
							<column alias="Application Data" heading="Role" isExternal="false" propertyName="role" selectOnly="false" type="wt.content.ContentRoleType">
								role
							</column>
						</operand>
						<operator type="equal"/>
						<operand>
							<parameterTarget name="appDataRole"/>
						</operand>
					</condition>
					<condition>
						<operand>
							<column alias="Application Data" heading="Description" isExternal="false" propertyName="description" selectOnly="false" type="java.lang.String">
								description
							</column>
						</operand>
						<operator type="like"/>
						<operand>
							<parameterTarget name="appDataDesc"/>
						</operand>
					</condition>
					<condition>
						<operand>
							<column alias="wt.viewmarkup.DerivedImage" heading="Default Representation" isExternal="false" propertyName="defaultRepresentation" selectOnly="false" type="boolean">
								defaultRepresentation
							</column>
						</operand>
						<operator type="equal"/>
						<operand>
							<parameterTarget name="isDefaultRep"/>
						</operand>
					</condition>
					<condition>
						<operand>
							<column alias="Part (wt.part.WTPart)" heading="Persist Info.Object Identifier.Id" isExternal="false" propertyName="persistInfo.objectIdentifier.id" selectOnly="false" type="long">
								thePersistInfo.theObjectIdentifier.id
							</column>
						</operand>
						<inOperator type="in"/>
						<inOperand>
							<delimitedList delimiter=",">
								<parameterTarget name="oidKeys"/>
							</delimitedList>
						</inOperand>
					</condition>
				</compositeCondition>
			</where>
			<linkJoin>
				<join name="wt.content.HolderToContent">
					<aliasTarget alias="wt.viewmarkup.DerivedImage"/>
					<aliasTarget alias="Application Data"/>
				</join>
			</linkJoin>
			<referenceJoin>
				<join name="representableReference">
					<aliasTarget alias="wt.viewmarkup.DerivedImage"/>
					<aliasTarget alias="Part (wt.part.WTPart)"/>
				</join>
			</referenceJoin>
		</query>
	</statement>
</qml>
