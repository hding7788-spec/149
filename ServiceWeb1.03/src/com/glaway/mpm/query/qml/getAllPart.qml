<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE qml SYSTEM "/wt/query/qml/qml.dtd">
<qml>
	<parameter name="context" type="java.lang.Object"/>
	<parameter name="name" type="java.lang.Object"/>
	<statement>
		<query>
			<select>
				<object alias="WTPART" heading="Persist Info.Object Identifier" propertyName="thePersistInfo.theObjectIdentifier.classname">
					<property name="thePersistInfo">
						<property name="theObjectIdentifier">
						<property name="classname">
						</property>
						</property>
					</property>
				</object>
			</select>
			<from>
				<table alias="WTPART">
					wt.part.WTPart
				</table>
			</from>	
		</query>
	</statement>
</qml>
