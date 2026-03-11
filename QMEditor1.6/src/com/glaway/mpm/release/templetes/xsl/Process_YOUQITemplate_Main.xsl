<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
<xsl:variable name="wrlFile" select="technics/QMFawTechnicsInfo//@wrlFile"/>
<xsl:variable name="technicsNumber" select="technics/QMFawTechnicsInfo//@technicsNumber"/>
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>无标题文档</title>
		<link rel="stylesheet" type="text/css" href="default.css" />
		<meta http-equiv="Cache-Control" content="no-store"></meta>
		<meta http-equiv="Expires" content="-1"></meta>
		<script type="text/javascript" src="jquery-1.7.2.min.js">&#160;</script>
		<script type="text/javascript" src="pv/pvUtils.js">&#160;</script>
		<script type="text/javascript" src="pv/pvlaunch.js">&#160;</script>
		<script type="text/javascript" src="releasetools.js">&#160;</script>
		<script type="text/javascript" src="release_pv.js">&#160;</script>
		<script type="text/javascript" src="pv/procedure_api.js">&#160;</script>
		<script type="text/javascript" src="pv/generic_prc.js">&#160;</script>
		<script language="javascript">
			$(function(){
				<xsl:if test="$wrlFile != ''">
					propSceneName = '<xsl:value-of select='$wrlFile'/>'+'.wrl';
					on_load();
				</xsl:if>
				valTdData();
				setProcessImg();
				imgSrcValData();
				initPageContent();
				showContentByDivName("top_00");
				replaseProcessContent();
				tdvalue="<xsl:value-of select="technics/QMFawTechnicsInfo//@PHASE_CODE"/>";
				funtd(tdvalue);
			});
			<xsl:if test="$wrlFile != ''">
				$(window).unload(function(){on_unload();});
			</xsl:if>

		</script>
		<script type="text/javascript" language="javascript">
			function funtd(tdvalue){
	          if(tdvalue=="M"){
		        document.getElementById("td1").innerHTML="M";
	          }else if(tdvalue=="C"){
	            document.getElementById("td2").innerHTML="C";
	          }else if(tdvalue=="S"){
	            document.getElementById("td3").innerHTML="S";
	          }else if(tdvalue=="D"){
	            document.getElementById("td4").innerHTML="D";
	          }else if(tdvalue=="G"){
	            document.getElementById("td5").innerHTML="G";
	          }else if(tdvalue=="P"){
	            document.getElementById("td6").innerHTML="P";
	          }else if(tdvalue=="Y"){
	            document.getElementById("td1").innerHTML="Y";
	          }
             }
		</script>
		</head>
		<body>
		   <div id="tbody">
		   <div id="newContent"><span></span></div>
		    <div id="floatFrame" style="position: absolute;visibility:hidden;">
		        <div id="floatImage" style="background-color:#FF0000;border:solid 2px #cccccc;">
		        	<script type="text/javascript">
	                    try {
                            myPvApi = ProductView("", "");
                        } catch(e) {
                            alert(e);
                        }
					</script>
		        </div>
		        <div  style="height:20px;background-color:#f0f0f0;">
		        	<span>视图:</span><select id="listView" onchange="listViewAction();"></select>
		        	<span><input type="hidden" value="hidden"/></span>
		        	<span>注释集:</span><select id="listSelection" onchange="listSelected();"></select>
		        	<input id="start_but" type="button" title="Start" value="Start" onClick="startAnimation();"/>
		        	<input id="stop_but" type="button" title="Stop" value="Stop" onClick="stopAnimation();"/>
		        	<span></span>
		        	<input id="prev_but" type="button" title="Previous" align="right" value="Previous" onClick="previous();"/>
		        	<input id="next_but" type="button" title="Next" align="right" value="Next" onClick="next();"/>
		        	<span></span>
		        	<input id="close_but" type="button" title="Close" value="Close" onClick="closeCVWindow();"/>
		        </div>
		    </div>

	   		<div id="message_container" style="top:0; left:0; position: absolute;width: 0px; height: 0px; overflow: hidden;"><span></span></div>
			<div id="menu_container" style="top:0; left:0; position: absolute;width: 0px; height: 0px;"><span></span></div>
			<div id="floatCortona" style="position: absolute;visibility:hidden;">
				<table id="aggregateElement" style="table-layout:fixed;" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="100%" id="cortonaTD" style="overflow: hidden;background-color: white;">
							<table id="cortonaFrame" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td colspan="2" id="cortona_holder" style="height:365px;">
										<div id="cortonaControl"></div>
									</td>
								</tr>
								<tr id="control_bar">
									<td height="10" align="left" valign="middle" nowrap="true">
										<input id="btn_play" type="button" title="Play" value="Play" disabled="true" class="btn_control" onClick="if(isPageLoaded)play();"/>
										<input id="btn_pause" type="button" title="Pause" value="Pause" disabled="true" class="btn_control" onClick="if(isPageLoaded)pause();"/>
										<input id="btn_stop" type="button" title="Stop" value="Stop" disabled="true" class="btn_control" onClick="if(isPageLoaded)stop();"/>
										<input id="btn_close" type="button" title="Close" value="Close" class="btn_control" onClick="closeC3DWindow();"/>
									</td>
									<td height="10" align="right" class="captions">
										<span id="autostopspan" style="">
											<input type="checkbox" id="autostopbox" title="Autostop" name="autostopbox" disabled="true" value="" onClick="if(isPageLoaded)_set_stop_by_step();"/>
											<label id="autostop_label" title="Autostop" for="autostopbox">Autostop</label>
										</span>
										<span id="messagesspan" style="">
											<input type="checkbox" title="Messages" class="FontSz" name="warningbox" id="warningbox" disabled="true" value=""/>
											<label id="warning_label" title="Messages" for="warningbox">Messages</label>
										</span>
										<span id="freezeVPSpan"><nobr>
											<input type="checkbox" class="checkbox_control" name="vpfreeze" id="vpfreeze" checked="false" value="" onClick="if(isPageLoaded)setFreezeVP(this.checked);"/>
											<label id="vpfreeze_label" class="captions" for="vpfreeze">Freeze Viewpoint</label>
											</nobr>
										</span>
										<span id="speedspan" style="">
											<span id="speed_label" title="">Speed:</span>
											<input type="radio" name="speed_ratio_selector" id="speed1" title="" value="0.5" disabled="true" onClick="if(isPageLoaded)api.set_speed_ratio(0.5);"/>
											<label id="speed1_label" title="" for="speed1">x1/2</label>
											<input type="radio" name="speed_ratio_selector" id="speed2" title="" onClick="if(isPageLoaded)api.set_speed_ratio(1);" disabled="true" value="1" checked="true"/>
											<label id="speed2_label" title="" for="speed2">x1</label>
											<input type="radio" name="speed_ratio_selector" title="" onClick="if(isPageLoaded)api.set_speed_ratio(2);" id="speed3" disabled="true" value="2"/>
											<label id="speed3_label" title="" for="speed3">x2</label>
										</span>
										<input id="btn_help" title="Help" type="button" value="?" class="btn_control" onClick="showHelp();"/>
										<!--[if IE]><script for="cortonaControl" event="OnSceneLoaded(success)" language="javascript">on_cortona_scene_loaded(success);</script><script for="cortonaControl" event="MouseDown(Button, Shift, X, Y)" language="javascript">on_cortona_mouse_down(Button, Shift, X, Y);</script><script for="cortonaControl" event="MouseUp(Button, Shift, X, Y)" language="javascript">on_cortona_mouse_up(Button, Shift, X, Y);</script><script for="cortonaControl" event="MouseMove(Button, Shift, X, Y)" language="javascript">on_cortona_mouse_move(Button, Shift, X, Y);</script><script for="cortonaControl" event="OnKeyDown(key, shift)" language="javascript">on_cortona_key_down(key, shift);</script><script for="cortonaControl" event="OnMouseOut()" language="javascript">on_cortona_mouse_out();</script><![endif]-->
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</div>

		   	<div id="right">
		   		<div class="sh_div">
		   		<xsl:attribute name="name">top_00</xsl:attribute>
			   		<div><a><xsl:attribute name="name">top_00</xsl:attribute></a></div>
			        <div class="title" id="share">
			        <!--<div><img class="logo" src="image/logo.png" /></div>-->
			        <div class="title-font" ><xsl:value-of select="technics/QMFawTechnicsInfo//@pplanName" /></div>
			        <div class="childtitle-font"><xsl:value-of select="technics/QMFawTechnicsInfo//@pplanNumber" /></div>
			        </div>

                    <div class="zhongjian">
			          <div class="secret">
			              <p>批次：<xsl:value-of select="technics/QMFawTechnicsInfo//@PCNO" /></p>
			              <p>关键件：<xsl:value-of select="technics/QMFawTechnicsInfo//@KEYCOMPONENT" /></p>
			          </div>
			          <div class="secret1">

                      <table class="table1" width="180"  cellpadding="0" cellspacing="0"  align="right" border="1" rules="cols">
                          <tr>
                           <td width="60" align="center" height="20">阶段标记</td>
                           <td id="td1" width="20" align="center"></td>
                           <td id="td2" width="20" align="center"></td>
                           <td id="td3" width="20" align="center"></td>
                           <td id="td4" width="20" align="center"></td>
                           <td id="td5" width="20" align="center"></td>
                           <td id="td6" width="20" align="center"></td>
                          </tr>
                      </table>
                      </div>
		     </div>
			        <!--工艺路线图开始 -->
			        <div class="route">
			           <div class="order-title">工艺路线图</div>
			           <div class="route-tup"><img src="technics_route.jpg" /></div>
			        </div>
			         <!--工艺路线图结束 -->

                    <!-- 工艺文件属性开始-->
                    <div class="gongyi">
                    <div class="gongyishuxing">
                    <div class="order-title1">工艺文件属性</div>
                     <table>
			                 <tr>
			                   <td class="shuxing">工艺文件编号：</td>
			                   <td class="neirong"><xsl:value-of select="technics/QMFawTechnicsInfo//@pplanNumber" /></td>
			                   <td class="shuxing">工艺文件名称：</td>
			                   <td class="neirong"><xsl:value-of select="technics/QMFawTechnicsInfo//@pplanName" /></td>
			                 </tr>
			                 <tr>
			                   <td>工艺版本：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@version" /></td>
			                   <td>创建者：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@creatorDisplay" /></td>
			                 </tr>
			                 <tr>
			                   <td>修改时间：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@modifyTime" /></td>
			                   <td>工艺状态：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@lifecycle" /></td>
			                 </tr>
			                 <tr>
			                   <td>产品型号代号：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@MINDEX" /></td>
			                   <td>产品代号：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@PINDEX" /></td>
			                 </tr>
			                 <tr>
			                   <td>设计图样代号：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@partNumber" /></td>
			                   <td>设计图样名称：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@partName" /></td>
			                 </tr>
			                 <tr>
			                   <td>关重键标记：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@KEYCOMPONENT" /></td>
			                   <td>产品阶段标记：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@PHASE_CODE" /></td>
			                 </tr>
			                 <tr>
			                   <td>工艺文件类别：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@PPLANTYPE" /></td>
			                   <td class="shuxing">临时工艺顺序号：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@TEMPNO" /></td>
			                 </tr>
			                 <tr>
			                   <td>主辅制类别：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@ZFFLAG" /></td>
			                   <td>工艺型号：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@technicsType" /></td>
			                 </tr>
			                 <tr>
			                   <td>工艺文件形式：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@isTabular" /></td>
			                   <td>工艺特征编号：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@PPLANID" /></td>
			                 </tr>
			                 <tr>
			                   <td>文件密级：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@SECRET" /></td>
			                   <td>批次号：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@PCNO" /></td>
			                 </tr>
			                 <tr>
			                   <td>部门：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@DEPT" /></td>
			                   <td>图号：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@CINDEX" /></td>
			                 </tr>
			                 <tr>
			                   <td>涂覆标记：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@TFBJ" /></td>
			                   <td>涂覆表面积零部件数量：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@TFBMJLBJSL" /></td>
			                 </tr>
			                 <tr>
			                   <td>涂料消耗零部件数量：</td>
			                   <td><xsl:value-of select="technics/QMFawTechnicsInfo//@TLXHLBJSL" /></td>
			                 </tr>
			                  <tr>
			                   <td>每<xsl:value-of select="technics/QMFawTechnicsInfo//@MJJS" /></td>
			                   <td>件面积cr2<xsl:value-of select="technics/QMFawTechnicsInfo//@TOTALAREA" /></td>
			                   <td>每<xsl:value-of select="technics/QMFawTechnicsInfo//@MJTLXHL" /></td>
			                   <td>件涂料消耗量kg<xsl:value-of select="technics/QMFawTechnicsInfo//@TLXHLJS" /></td>
			                 </tr>
			           </table>

                   </div>
                    <!-- 工艺文件属性结束-->
                    <div class="gongyishuoming">
			        <div class="order-title2">工艺说明</div>
			        <div class="procedureContent">
		        	<xsl:value-of select="technics/QMFawTechnicsInfo/TechnicsDescribe" />
		        	</div>
                    </div>
                    </div>
		        </div>

		        <xsl:apply-templates select="technics/QMFawTechnicsInfo/steps" />

		        <xsl:apply-templates select="technics/QMFawTechnicsInfo/technicsStateTables" />

		        <xsl:apply-templates select="technics/QMFawTechnicsInfo/borrowTechnicss" />

		        <xsl:apply-templates select="technics/QMFawTechnicsInfo/FZGY" />

		        <xsl:apply-templates select="technics/QMFawTechnicsInfo/GYDE" />

		        <xsl:apply-templates select="technics/QMFawTechnicsInfo/CLDE" />

				<!--
		      	<div class="sh_div">
		   			<xsl:attribute name="name">technics_desc</xsl:attribute>
			   		<div><a><xsl:attribute name="name">technics_desc</xsl:attribute></a></div>
					<iframe name="technics_desc" src="technics_desc.mht" width="90%" height="800px" style="float:right;border:0;margin:0;padding:0;" frameborder="yes"></iframe>
		       	</div>
 				-->

				<!-- <xsl:apply-templates select="technics/QMFawTechnicsInfo/additiontables" /> -->

		    </div>
		   </div>
		</body>
		</html>
	</xsl:template>

	<!-- 典型/通用工艺 -->
	<xsl:template match="technics/QMFawTechnicsInfo/borrowTechnicss">
	  <div class="sh_div" style="visibility: hidden;">
			<xsl:attribute name="name">borrowTechnics</xsl:attribute>
          	<a>
      			<xsl:attribute name="name">
      				borrowTechnics
      			</xsl:attribute>
     		</a>
     		<div class="order-title">典型/通用工艺信息</div>
       		<table width="100%" border="0" cellspacing="5" cellpadding="0">
       			<tr>
       				<td class="cailiao-title-bg">工艺编号</td>
       				<td class="cailiao-title-bg">工艺名称</td>
       				<td class="cailiao-title-bg">工艺类型</td>
       			</tr>
          		<xsl:for-each select="borrowTechnics">
      				<tr>
      					<th><a target="_blank" >
				<xsl:attribute name="href"><xsl:value-of select="@technicsPath" /></xsl:attribute>

      					<xsl:value-of select="@technicsNumber" />

      					</a>
</th>
      					<th><xsl:value-of select="@technicsName" /></th>
      					<th><xsl:value-of select="@technicsType" /></th>
      				</tr>
          		</xsl:for-each>
          	</table>
	 </div>
	</xsl:template>

	<!-- 辅制工艺 -->
	<xsl:template match="technics/QMFawTechnicsInfo/FZGY">
	  <div class="sh_div" style="visibility: hidden;">
			<xsl:attribute name="name">FZTECHNICS</xsl:attribute>
          	<a>
      			<xsl:attribute name="name">
      				FZTECHNICS
      			</xsl:attribute>
     		</a>
     		<div class="order-title">辅制工艺信息</div>
       		<table width="100%" border="0" cellspacing="5" cellpadding="0">
       			<tr>
       				<td class="cailiao-title-bg">编号</td>
       				<td class="cailiao-title-bg">名称</td>
       			</tr>
          		<xsl:for-each select="FZTECHNICS">
      				<tr>
      					<th><xsl:value-of select="@number" /></th>
      					<th><xsl:value-of select="@name" /></th>
      				</tr>
          		</xsl:for-each>
          	</table>
	 </div>
	</xsl:template>

	<!-- 工艺定额 -->
	<xsl:template match="technics/QMFawTechnicsInfo/GYDE">
	  <div class="sh_div" style="visibility: hidden;">
			<xsl:attribute name="name">GYDE</xsl:attribute>
          	<a>
      			<xsl:attribute name="name">
      				GYDE
      			</xsl:attribute>
     		</a>
     		<div class="order-title">标准件/元器件匹配信息</div>
       		<table width="100%" border="0" cellspacing="5" cellpadding="0">
       			<tr>
       				<td class="cailiao-title-bg">图号</td>
       				<td class="cailiao-title-bg">存货编码</td>
       				<td class="cailiao-title-bg">存货名称</td>
       				<td class="cailiao-title-bg">设计数量</td>
       				<td class="cailiao-title-bg">工艺数量</td>
       			</tr>
          		<xsl:for-each select="MATCHPART/MatchPart">
      				<tr>
      					<th><xsl:value-of select="@number" /></th>
      					<th><xsl:value-of select="@chbm" /></th>
      					<th><xsl:value-of select="@chmc" /></th>
      					<th><xsl:value-of select="@useCount" /></th>
      					<th><xsl:value-of select="@gyCount" /></th>
      				</tr>
          		</xsl:for-each>
          		<xsl:for-each select="SJZYKMATCHPART/SjzykMatchPart">
      				<tr>
      					<th><xsl:value-of select="@partNumber" /></th>
      					<th><xsl:value-of select="@sjbm" /></th>
      					<th><xsl:value-of select="@name" /></th>
      					<th><xsl:value-of select="@sjsl" /></th>
      					<th><xsl:value-of select="@gysl" /></th>
      				</tr>
          		</xsl:for-each>
          	</table>
          	<div class="order-title">添加的标准件/元器件信息</div>
          	<table width="100%" border="0" cellspacing="5" cellpadding="0">
       			<tr>
       				<td class="cailiao-title-bg">上级图号</td>
       				<td class="cailiao-title-bg">图号</td>
       				<td class="cailiao-title-bg">名称</td>
       				<td class="cailiao-title-bg">使用数量</td>
       			</tr>
          		<xsl:for-each select="NEWPART/NewPart">
      				<tr>
      					<th><xsl:value-of select="@parentNumber" /></th>
      					<th><xsl:value-of select="@number" /></th>
      					<th><xsl:value-of select="@chmc" /></th>
      					<th><xsl:value-of select="@gysl" /></th>
      				</tr>
          		</xsl:for-each>
          		<xsl:for-each select="SJZYKNEWPART/SjzykNewPart">
      				<tr>
      					<th><xsl:value-of select="@parentPartNumber" /></th>
      					<th><xsl:value-of select="@sjbm" /></th>
      					<th><xsl:value-of select="@name" /></th>
      					<th><xsl:value-of select="@gysl" /></th>
      				</tr>
          		</xsl:for-each>
          	</table>
	 </div>
	</xsl:template>

	<!-- 材料定额 -->
	<xsl:template match="technics/QMFawTechnicsInfo/CLDE">
	  <div class="sh_div" style="visibility: hidden;">
			<xsl:attribute name="name">CLDE</xsl:attribute>
          	<a>
      			<xsl:attribute name="name">
      				CLDE
      			</xsl:attribute>
     		</a>
     		<div class="order-title">原材料定额信息</div>
       		<table width="100%" border="0" cellspacing="5" cellpadding="0">
       			<tr>
       				<td class="cailiao-title-bg">存货编码</td>
       				<td class="cailiao-title-bg">存货名称</td>
       				<td class="cailiao-title-bg">下料尺寸</td>
       				<td class="cailiao-title-bg">可制件数</td>
       				<td class="cailiao-title-bg">型号牌号</td>
       				<td class="cailiao-title-bg">规格</td>
       				<td class="cailiao-title-bg">技术条件</td>
       			</tr>
          		<xsl:for-each select="YCLDE/ycldeRecord">
      				<tr>
      					<th><xsl:value-of select="@chbm" /></th>
      					<th><xsl:value-of select="@chmc" /></th>
      					<th><xsl:value-of select="@xlcc" /></th>
      					<th><xsl:value-of select="@kzjs" /></th>
      					<th><xsl:value-of select="@xhph" /></th>
      					<th><xsl:value-of select="@gg" /></th>
      					<th><xsl:value-of select="@jstj" /></th>
      				</tr>
          		</xsl:for-each>
          		<xsl:for-each select="SJZYKYCLDE/sjzykycldeRecord">
      				<tr>
      					<th><xsl:value-of select="@sjbm" /></th>
      					<th><xsl:value-of select="@name" /></th>
      					<th><xsl:value-of select="@xlcc" /></th>
      					<th><xsl:value-of select="@kzjs" /></th>
      					<th><xsl:value-of select="@xh" /></th>
      					<th><xsl:value-of select="@gg" /></th>
      					<th><xsl:value-of select="@jstj" /></th>
      				</tr>
          		</xsl:for-each>
          	</table>

          	<div class="order-title">主要材料定额信息</div>
          	<table width="100%" border="0" cellspacing="5" cellpadding="0">
       			<tr>
       				<td class="cailiao-title-bg">存货编码</td>
       				<td class="cailiao-title-bg">存货名称</td>
       				<td class="cailiao-title-bg">数量</td>
       				<td class="cailiao-title-bg">型号牌号</td>
       				<td class="cailiao-title-bg">规格</td>
       				<td class="cailiao-title-bg">技术条件</td>
       			</tr>
          		<xsl:for-each select="ZYCLDE/zycldeRecord">
      				<tr>
      					<th><xsl:value-of select="@chbm" /></th>
      					<th><xsl:value-of select="@chmc" /></th>
      					<th><xsl:value-of select="@sl" /></th>
      					<th><xsl:value-of select="@xhph" /></th>
      					<th><xsl:value-of select="@gg" /></th>
      					<th><xsl:value-of select="@jstj" /></th>
      				</tr>
          		</xsl:for-each>
          		<xsl:for-each select="SJZYKZYCLDE/sjzykzycldeRecord">
      				<tr>
      					<th><xsl:value-of select="@sjbm" /></th>
      					<th><xsl:value-of select="@name" /></th>
      					<th><xsl:value-of select="@sl" /></th>
      					<th><xsl:value-of select="@xh" /></th>
      					<th><xsl:value-of select="@gg" /></th>
      					<th><xsl:value-of select="@jstj" /></th>
      				</tr>
          		</xsl:for-each>
          	</table>

          	<div class="order-title">试件材料定额信息</div>
          	<table width="100%" border="0" cellspacing="5" cellpadding="0">
       			<tr>
       				<td class="cailiao-title-bg">存货编码</td>
       				<td class="cailiao-title-bg">存货名称</td>
       				<td class="cailiao-title-bg">试件尺寸</td>
       				<td class="cailiao-title-bg">试件可制件数</td>
       				<td class="cailiao-title-bg">试件数量</td>
       				<td class="cailiao-title-bg">型号牌号</td>
       				<td class="cailiao-title-bg">规格</td>
       				<td class="cailiao-title-bg">技术条件</td>
       			</tr>
          		<xsl:for-each select="SJYCLDE/sjycldeRecord">
      				<tr>
      					<th><xsl:value-of select="@chbm" /></th>
      					<th><xsl:value-of select="@chmc" /></th>
      					<th><xsl:value-of select="@sjcc" /></th>
      					<th><xsl:value-of select="@sjkzjs" /></th>
      					<th><xsl:value-of select="@sjsl" /></th>
      					<th><xsl:value-of select="@xhph" /></th>
      					<th><xsl:value-of select="@gg" /></th>
      					<th><xsl:value-of select="@jstj" /></th>
      				</tr>
          		</xsl:for-each>
          		<xsl:for-each select="SJZYKSJYCLDE/sjzyksjycldeRecord">
      				<tr>
      					<th><xsl:value-of select="@sjbm" /></th>
      					<th><xsl:value-of select="@name" /></th>
      					<th><xsl:value-of select="@sjcc" /></th>
      					<th><xsl:value-of select="@sjkzjs" /></th>
      					<th><xsl:value-of select="@sl" /></th>
      					<th><xsl:value-of select="@xh" /></th>
      					<th><xsl:value-of select="@gg" /></th>
      					<th><xsl:value-of select="@jstj" /></th>
      				</tr>
          		</xsl:for-each>
          	</table>
	 </div>
	</xsl:template>

	<xsl:template match="technics/QMFawTechnicsInfo/additiontables">
		 <xsl:for-each select="additionaltable">
		      <div class="sh_div">
		   			<xsl:attribute name="name"><xsl:value-of select="@bsoID" /></xsl:attribute>
			   		<div>
			   			<a>
			   				<xsl:attribute name="name"><xsl:value-of select="@bsoID" /></xsl:attribute>
			   			</a>
			   		</div>
					<iframe width="90%" height="800px" style="float:right;border:0;margin:0;padding:0;" frameborder="yes">
						<xsl:attribute name="src"><xsl:value-of select="@absolutePath2" /></xsl:attribute>
					</iframe>
		       </div>
		</xsl:for-each>
	</xsl:template>


	<xsl:template match="technics/QMFawTechnicsInfo/steps">
		<xsl:for-each select="QMProcedureInfo">
			<!--重复开始-->
	       <!--工序开始 -->
	       <xsl:variable name="gxStepNumber" select="@stepNumber"/>
	       <div class="sh_div" style="visibility: hidden;">
	       <xsl:attribute name="name"><xsl:value-of select="@stepNumber"/></xsl:attribute>
		       <div class="circle"></div>
		       <div >
			       <div class="order-title order-title-bold">
			       		<a>
				       		<xsl:attribute name="name">
				       			<xsl:value-of select="@stepNumber"/>
				       		</xsl:attribute>
			       		</a>
			       		工序-<xsl:value-of select="@stepNumber" /><xsl:if test="@isKey='true'">(关键)</xsl:if>
		       		</div>
			       <div class="order-content">
			       		<span><xsl:value-of select="@stepNumber" /></span>
			       		<span class = "congxu_desc"><xsl:value-of select="@stepName" /></span>
			       		<span class = "congxu_desc"><xsl:value-of select="@workShop" /></span>
			       		<span class = "congxu_desc"><xsl:value-of select="@workType" /></span>
			       		<span class = "congxu_desc"><xsl:value-of select="@workType" /></span>
			       		<span class = "congxu_desc"> <xsl:variable name="sopURL" select="@sopURL"/>
							<a style="color:blue" target = "_blank" href="{$sopURL}"><xsl:choose>
								<xsl:when test="sops/SOPInfo/@ppnumber != ''"><xsl:value-of select="sops/SOPInfo//@ppnumber" />_<xsl:value-of select="sops/SOPInfo//@name" />
                           </xsl:when>
                           <xsl:otherwise> </xsl:otherwise>
                         </xsl:choose></a>
			       		</span>
			       </div>
			       <div style="margin-left:10px">
			       		<xsl:choose>
	                 		<xsl:when test="procedureContent != ''">
	                 			<table>
		                 			<tr>
		                 				<td style="border-bottom-style: none;" width="60">工序内容：</td>
		                 				<td style="border-bottom-style: none;">
		                 					<div style="word-break: break-all;text-align:left;font-size: 12px;" class="procedureContent">
			               						<xsl:value-of select="procedureContent" />
			               					</div>
		                 				</td>
		                 			</tr>
		                 		</table>
	                 		</xsl:when>
	                 	    <xsl:otherwise>工序内容：</xsl:otherwise>
	                    </xsl:choose>
			       </div>
			       <xsl:if test="@isKey='true'">
 						<div class="order-content">检查操作指导说明：<xsl:value-of select="@operateInstruction" /></div>
 						<div class="order-content">批次号：<xsl:value-of select="@PCNO" /></div>
 						<div class="order-content">操作者：<xsl:value-of select="@CZZ" /></div>
 						<div class="order-content">使用设备：<xsl:value-of select="@SYSB" /></div>
 						<div class="order-content">环境条件：<xsl:value-of select="@HJTJ" /></div>
 						<div class="order-content">检测工具：<xsl:value-of select="@JCGJ" /></div>
 						<div class="order-content">控制图表：<xsl:value-of select="@KZTB" /></div>
 						<div class="order-content">控制内容：<xsl:value-of select="@KZNR" /></div>
 						<div class="order-content">质量控制程序：<xsl:value-of select="@ZLKZCX" /></div>
			       </xsl:if>
			       <!-- 循环显示工步对象的IBA属性 -->
			   		<xsl:for-each select="IBAAttibutes/attribute">
						<div class="order-content"><xsl:value-of select="@key" />:<xsl:value-of select="@value" /></div>
					</xsl:for-each>
		       </div>
		       <!--工序结束 -->

		       <!--工艺材料信息开始 -->
		       <div class="cailiao">
               	   <!-- 工艺辅料信息 -->
		         <xsl:if test="materials/QMMaterialInfo">
		       		<div class="order-title">工艺辅料信息</div>
		           	<table width="736" border="0" cellspacing="5" cellpadding="0">
			           	<!-- 材料信息 是否存在  不存在则不显示 -->
			           	<tr>
				             <td class="cailiao-title-bg">编号</td>
				             <td class="cailiao-title-bg">名称</td>
				             <td class="cailiao-title-bg">型号</td>
				             <td class="cailiao-title-bg">规格</td>
				             <td class="cailiao-title-bg">技术条件</td>
				             <td class="cailiao-title-bg">计量单位</td>
				             <td class="cailiao-title-bg">附加条件</td>
				             <td class="cailiao-title-bg">使用数量</td>
				             <td class="cailiao-title-bg">使用车间</td>
				             <td class="cailiao-title-bg">备注</td>
			          	</tr>
			           	<xsl:for-each select="materials/QMMaterialInfo">
				           	 <tr>
					             <td><xsl:value-of select="@materialNumber" /></td>
					             <td><xsl:value-of select="@materialName" /></td>
					             <td><xsl:value-of select="@mindex" /></td>
					             <td><xsl:value-of select="@csize" /></td>
					             <td><xsl:value-of select="@jstj" /></td>
					             <td><xsl:value-of select="@jldw" /></td>
					             <td><xsl:value-of select="@fjtj" /></td>
					             <td><xsl:value-of select="@sl" /></td>
					             <td><xsl:value-of select="@sycj" /></td>
					             <td><xsl:value-of select="@bz" /></td>
				             </tr>
			           	</xsl:for-each>
			           	<xsl:for-each select="paces/QMProcedureInfo/materials/QMMaterialInfo">
				           	 <tr>
					             <td><xsl:value-of select="@materialNumber" /></td>
					             <td><xsl:value-of select="@materialName" /></td>
					             <td><xsl:value-of select="@mindex" /></td>
					             <td><xsl:value-of select="@csize" /></td>
					             <td><xsl:value-of select="@jstj" /></td>
					             <td><xsl:value-of select="@jldw" /></td>
					             <td><xsl:value-of select="@fjtj" /></td>
					             <td><xsl:value-of select="@sl" /></td>
					             <td><xsl:value-of select="@sycj" /></td>
					             <td><xsl:value-of select="@bz" /></td>
				             </tr>
			           	</xsl:for-each>
		           </table>
		         </xsl:if>

		           <!-- 首先判断有没有附件  没有则不显示 -->
               	   <xsl:if test="attachs/PAttachInfo">
               	   <table width="736" border="0" cellspacing="5" cellpadding="0">
		           	<tr>
		             <td class="cailiao-title-bg">工艺附件</td>
		             <td colspan="5">
		             	<xsl:for-each select="attachs/PAttachInfo">
			             	<a>
			             		<xsl:attribute name="href">
			             			<xsl:value-of select="@absolutePath"/>
			             		</xsl:attribute>
		             			<xsl:value-of select="@attachName" />.<xsl:value-of select="@attachType" />
			             	</a>
		             		<xsl:if test="position()!=last()">
								<xsl:text>, </xsl:text>
							</xsl:if>
		             	</xsl:for-each>
		             </td>
		             </tr>
		             </table>
		            </xsl:if>

				   <!-- 首先判断有没有视频类大文件  没有则不显示 -->
				   <xsl:if test="LargeFiles/LargeFile">
					   <table width="736" border="0" cellspacing="5" cellpadding="0">
						   <tr>
							   <td class="cailiao-title-bg">视频类大文件</td>
							   <td colspan="5">
								   <xsl:for-each select="LargeFiles/LargeFile">
									   <xsl:variable name="docNumber" select="@docNumber"/>
									   <a target = "_blank" href="/Windchill/netmarkets/jsp/ext/casc/report/downContent.jsp?documentNumber={$docNumber}"><xsl:value-of select="@fileName" />
									   </a>
									   <xsl:if test="position()!=last()">
										   <xsl:text>, </xsl:text>
									   </xsl:if>
								   </xsl:for-each>
							   </td>
						   </tr>
					   </table>
				   </xsl:if>

		          <!-- 工艺设备信息 -->
	         	<xsl:if test="equips/QMEquipmentInfo">
		       	<div class="order-title">工艺设备信息</div>
		           <table width="736" border="0" cellspacing="5" cellpadding="0">
		           <!-- s设备信息 是否存在  不存在则不显示 -->
		           <tr>
		             <td class="cailiao-title-bg">设备编号</td>
		             <td class="cailiao-title-bg">设备名称</td>
		             <td class="cailiao-title-bg">设备型号</td>
		             <td class="cailiao-title-bg">设备类别</td>
		             <td class="cailiao-title-bg">设备规格</td>
		             <td class="cailiao-title-bg">备注</td>
		           </tr>
		           <xsl:for-each select="equips/QMEquipmentInfo">
			           <tr>
			             <td><xsl:value-of select="@number" /></td>
			             <td><xsl:value-of select="@name" /></td>
			             <td><xsl:value-of select="@pindex" /></td>
			             <td><xsl:value-of select="@equipmentType" /></td>
			             <td><xsl:value-of select="@csize" /></td>
			             <td><xsl:value-of select="@bz" /></td>
			           </tr>
		           </xsl:for-each>
		           </table>
		         </xsl:if>

		        <!-- 标准仪器仪表信息 -->
	         	<xsl:if test="sdashboard/QMSDashboardInfo">
		       	<div class="order-title">标准仪器仪表信息</div>
		           <table width="736" border="0" cellspacing="5" cellpadding="0">
		           <!-- s设备信息 是否存在  不存在则不显示 -->
		           <tr>
		             <td class="cailiao-title-bg">编号</td>
		             <td class="cailiao-title-bg">名称</td>
		             <td class="cailiao-title-bg">型号</td>
		             <td class="cailiao-title-bg">类别</td>
		             <td class="cailiao-title-bg">规格</td>
		             <td class="cailiao-title-bg">备注</td>
		           </tr>
		           <xsl:for-each select="sdashboard/QMSDashboardInfo">
			           <tr>
			             <td><xsl:value-of select="@number" /></td>
			             <td><xsl:value-of select="@name" /></td>
			             <td><xsl:value-of select="@pindex" /></td>
			             <td><xsl:value-of select="@equipmentType" /></td>
			             <td><xsl:value-of select="@csize" /></td>
			             <td><xsl:value-of select="@bz" /></td>
			           </tr>
		           </xsl:for-each>
		           </table>
		         </xsl:if>

		        <!-- 非标准仪器仪表信息 -->
	         	<xsl:if test="unsdashboard/QMUnSDashboardInfo">
		       	<div class="order-title">非标准仪器仪表信息</div>
		           <table width="736" border="0" cellspacing="5" cellpadding="0">
		           <!-- s设备信息 是否存在  不存在则不显示 -->
		           <tr>
		             <td class="cailiao-title-bg">编号</td>
		             <td class="cailiao-title-bg">名称</td>
		             <td class="cailiao-title-bg">型号</td>
		             <td class="cailiao-title-bg">类别</td>
		             <td class="cailiao-title-bg">规格</td>
		             <td class="cailiao-title-bg">备注</td>
		           </tr>
		           <xsl:for-each select="unsdashboard/QMUnSDashboardInfo">
			           <tr>
			             <td><xsl:value-of select="@number" /></td>
			             <td><xsl:value-of select="@name" /></td>
			             <td><xsl:value-of select="@pindex" /></td>
			             <td><xsl:value-of select="@equipmentType" /></td>
			             <td><xsl:value-of select="@csize" /></td>
			             <td><xsl:value-of select="@bz" /></td>
			           </tr>
		           </xsl:for-each>
		           </table>
		         </xsl:if>

		         <!-- 工艺工装及工具信息 -->
		         <xsl:if test="tools/QMToolInfo">
		       		<div class="order-title">工艺工装及工具信息</div>
		           	<table width="736" border="0" cellspacing="5" cellpadding="0">
			           	<!-- 材料信息 是否存在  不存在则不显示 -->
			           	<tr>
				             <td class="cailiao-title-bg">编号</td>
				             <td class="cailiao-title-bg">名称</td>
				             <td class="cailiao-title-bg">工装类别</td>
				             <td class="cailiao-title-bg">规格</td>
				             <td class="cailiao-title-bg">型号</td>
				             <td class="cailiao-title-bg">备注</td>
			          	</tr>
			           	<xsl:for-each select="tools/QMToolInfo">
				           	 <tr>
					             <td><xsl:value-of select="@toolNum" /></td>
					             <td><xsl:value-of select="@toolName" /></td>
					             <td><xsl:value-of select="@toolType" /></td>
					             <td><xsl:value-of select="@csize" /></td>
					             <td><xsl:value-of select="@mindex" /></td>
					             <td><xsl:value-of select="@bz" /></td>
				             </tr>
			           	</xsl:for-each>
		           </table>
		         </xsl:if>
		         <!-- 量具信息 -->
		         <xsl:if test="measures/QMMeasureInfo">
		       		<div class="order-title">量具信息</div>
		           	<table width="736" border="0" cellspacing="5" cellpadding="0">
			           	<!-- 材料信息 是否存在  不存在则不显示 -->
			           	<tr>
				             <td class="cailiao-title-bg">编号</td>
				             <td class="cailiao-title-bg">名称</td>
				             <td class="cailiao-title-bg">型号</td>
				             <td class="cailiao-title-bg">规格</td>
				             <td class="cailiao-title-bg">备注</td>
			          	</tr>
			           	<xsl:for-each select="measures/QMMeasureInfo">
				           	 <tr>
					             <td><xsl:value-of select="@number" /></td>
					             <td><xsl:value-of select="@name" /></td>
					             <td><xsl:value-of select="@pindex" /></td>
					             <td><xsl:value-of select="@csize" /></td>
					             <td><xsl:value-of select="@bz" /></td>
				             </tr>
			           	</xsl:for-each>
		           </table>
		         </xsl:if>


		       <!--工艺材料信息结束 -->

		         <!-- 刀具信息 -->
		         <xsl:if test="knifeTools/QMKnifeToolInfo">
		       		<div class="order-title">刀具信息</div>
		           	<table width="736" border="0" cellspacing="5" cellpadding="0">
			           	<tr>
				             <td class="cailiao-title-bg">编号</td>
				             <td class="cailiao-title-bg">名称</td>
				             <td class="cailiao-title-bg">类别</td>
				             <td class="cailiao-title-bg">材料</td>
				             <td class="cailiao-title-bg">夹持直径</td>
				             <td class="cailiao-title-bg">刃口长度</td>
				             <td class="cailiao-title-bg">总长度</td>
				             <td class="cailiao-title-bg">公差</td>
				             <td class="cailiao-title-bg">最小加工尺寸</td>
				             <td class="cailiao-title-bg">最大加工尺寸</td>
				             <td class="cailiao-title-bg">结构形式</td>
				             <td class="cailiao-title-bg">接口类型</td>
				             <td class="cailiao-title-bg">技术备注</td>
				             <td class="cailiao-title-bg">刃口圆角半径</td>
				             <td class="cailiao-title-bg">齿数</td>
				             <td class="cailiao-title-bg">备注</td>
			          	</tr>
			           	<xsl:for-each select="knifeTools/QMKnifeToolInfo">
				           	 <tr>
					             <td><xsl:value-of select="@toolNum" /></td>
					             <td><xsl:value-of select="@toolName" /></td>
					             <td><xsl:value-of select="@knifetype" /></td>
					             <td><xsl:value-of select="@cmat" /></td>
					             <td><xsl:value-of select="@jczj" /></td>
					             <td><xsl:value-of select="@rkcd" /></td>
					             <td><xsl:value-of select="@zcd" /></td>
					             <td><xsl:value-of select="@gc" /></td>
					             <td><xsl:value-of select="@zxjgcc" /></td>
					             <td><xsl:value-of select="@zdjgcc" /></td>
					             <td><xsl:value-of select="@jgxs" /></td>
					             <td><xsl:value-of select="@jklx" /></td>
					             <td><xsl:value-of select="@jsbz" /></td>
					             <td><xsl:value-of select="@rkyjbj" /></td>
					             <td><xsl:value-of select="@cs" /></td>
					             <td><xsl:value-of select="@bz" /></td>
				             </tr>
			           	</xsl:for-each>
		           </table>
		         </xsl:if>
		       <!--刀具信息结束 -->

		       </div>

		       <!--工序参装件信息开始-->
		       <!-- 判断 简图或者 参装件是否存在 不存在不显示 -->
		       <xsl:if test="images/PDrawingInfo or paces/QMProcedureInfo/parts/QMPartInfo or $wrlFile != ''">
		       <div class="canzhuang">
		         <div class="showtup" id="baginFloat">
		         <xsl:choose>
               		<xsl:when test="images/PDrawingInfo or $wrlFile != ''">
               			<xsl:if test="$wrlFile != ''">
               				<div class="showtup-tbody">
				              	<div class="showtup-content">
					              	<img class="imgSrcVal" onclick="showImageModel(this)">
					              		<xsl:attribute name="src"><xsl:value-of select="$wrlFile" />.wrl</xsl:attribute>
					              	</img>
				              	</div>
				                <div class="showtup-title">Cortona3D装配动画</div>
				             </div>
               			</xsl:if>
               			<xsl:for-each select="images/PDrawingInfo">
				          	<div class="showtup-tbody">
				              	<div class="showtup-content">
					              	<img class="imgSrcVal" onclick="showImageModel(this)">
					              		<xsl:attribute name="src">
		                      				<xsl:value-of select="@absolutePath" />
		                      			</xsl:attribute>
		                      			<xsl:if test="@annoName">
		                      				<xsl:attribute name="annoName">
		                      					<xsl:value-of select="@annoName" />
		                      				</xsl:attribute>
		                      			</xsl:if>
		                      			<xsl:if test="@annoPic">
		                      				<xsl:attribute name="annoPic">
		                      					<xsl:value-of select="@annoPic" />
		                      				</xsl:attribute>
		                      			</xsl:if>
					              	</img>
				              	</div>
				                <div class="showtup-title"><xsl:value-of select="@drawingName" /><span></span></div>
				             </div>
	             		</xsl:for-each>
               		</xsl:when>
               		<xsl:otherwise>
               			<div class="showtup-null">
               				<table><tr></tr></table>
			             </div>
               		</xsl:otherwise>
               	</xsl:choose>
		         </div>
		         <div class="canzhuang-data">
		             <div class="canzhuang-header">工序参装件信息</div>
		             <div class="canzhuang-content">
		                 <table width="150" border="0" cellspacing="0" cellpadding="0" class="valData">
		                 <tr><td></td></tr>
		                 	 <!-- 工艺参装见:工具参装见的集合：相同的要合并处理 -->
		                 	<xsl:for-each select="paces/QMProcedureInfo/parts/QMPartInfo">
		                 		<tr>
			                       <td><xsl:value-of select="@partNumber" />*<xsl:value-of select="@useCount" />
			                       </td>
			                     </tr>
		                 	</xsl:for-each>
		               </table>
		           </div>
		         </div>
		       </div>
		       </xsl:if>
		       <!--工序参装件信息结束-->

		       <!--工步内容开始-->
		       <div class="gongbu">
		       	   <!-- 工步信息 是否存在  不存在则不显示 -->
               	   <xsl:if test="paces/QMProcedureInfo">
		       	   <div class="order-title">工步内容</div>
		           <table width="708" border="0" cellspacing="1" cellpadding="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;">
		               <tr>
						   <td width="40"  class="cailiao-title-bg">工步号</td>
						   <td width="100"  class="cailiao-title-bg">工步内容</td>
						   <td width="60"  class="cailiao-title-bg">设备信息</td>
						   <td width="64"  class="cailiao-title-bg">工装及工具</td>
						   <td width="60"  class="cailiao-title-bg">参装件</td>
						   <td width="60"  class="cailiao-title-bg">工艺辅料</td>
						   <td width="50"  class="cailiao-title-bg">刀具</td>
						   <td width="40"  class="cailiao-title-bg">程序号</td>
						   <td width="60"  class="cailiao-title-bg">检验工步</td>
						   <td width="100" class="cailiao-title-bg">引用SOP</td>
						   <td width="60" class="cailiao-title-bg">照片样张</td>
		               </tr>
		               <!--  工步信息循环处理 -->
		               <xsl:for-each select="paces/QMProcedureInfo">
		               		<xsl:variable name="gbStepNumber" select="@stepNumber"/>
			               	<tr class="gongbuTrCss" onclick="gongbuTRClickAction(this);">
			               	<xsl:if test="$wrlFile != ''">
			               		<xsl:attribute name="cortonaid"><xsl:value-of select="@cortonaID"/></xsl:attribute>
			               	</xsl:if>
			               	<!-- 工步编号 -->
			               	<xsl:choose>
			               		<xsl:when test="images/PDrawingInfo">
			               			<td rowspan="2"><xsl:value-of select="@stepNumber" />
			               			<xsl:if test="@isKey='true'">(关键)</xsl:if>
			               			</td>
			               		</xsl:when>
			               		<xsl:otherwise>
			               			<td><xsl:value-of select="@stepNumber" /><xsl:if test="@isKey='true'">(关键)</xsl:if></td>
			               		</xsl:otherwise>
			               	</xsl:choose>
			                 <!-- 工步内容 -->
			                 <xsl:choose>
			                 	<xsl:when test="procedureContent != ''">
			                 		<td>
			                 			<div style="word-break: break-all;text-align:left;font-size: 12px;" class="procedureContent">
                 							<xsl:value-of select="procedureContent" />
                 						</div>
			                 		</td>
			                 	</xsl:when>
			                 	<xsl:otherwise><td></td></xsl:otherwise>
			                 </xsl:choose>
			                 <!-- <td>
			                   	<xsl:value-of select="procedureContent" />
			                   </td> -->
			                 <!-- 工步设备信息 -->
			                 <td>
			                 	<xsl:for-each select="equips/QMEquipmentInfo">
			                 		<xsl:value-of select="@name" />
				                 </xsl:for-each>
			                 </td>
			                 <!-- 工步工装及工具 -->
			                 <td>
			                 	<xsl:for-each select="tools/QMToolInfo">
			                 		<xsl:value-of select="@toolName" />
				                 </xsl:for-each>
			                 </td>
			                 <!-- 工步参装件 -->
			                 <td>
			                 	<xsl:for-each select="parts/QMPartInfo">
			                 		<xsl:value-of select="@partNumber" />
				                 </xsl:for-each>
			                 </td>
		                   	 <!-- 工艺辅料 -->
			                 <td>
			                 	<xsl:for-each select="materials/QMMaterialInfo">
			                 		<xsl:value-of select="@materialName" />
				                 </xsl:for-each>
			                 </td>
			                 <!-- 刀具 -->
			                 <td>
			                 	<xsl:for-each select="knifeTools/QMKnifeToolInfo">
			                 		<xsl:value-of select="@toolName" />
				                 </xsl:for-each>
			                 </td>
			                 <!-- 程序号 -->
			                 <td>
			                 	<xsl:value-of select="@programNo" />
			                 </td>
							 <!-- 检验工步 -->
 							 <xsl:choose>
			               		<xsl:when test="@isCheck='true'">
			               			<td>
			               		 	<a target = "_blank" href="/Windchill/netmarkets/jsp/ext/glaway/mpm/checkTable/showCheckPaceInfo.jsp?technicsNumber={$technicsNumber}&amp;gxStepNumber={$gxStepNumber}&amp;gbStepNumber={$gbStepNumber}">
			               				是
			               			</a>
			               			</td>
			               		</xsl:when>
			               		<xsl:otherwise>
			               			<td>
			               				否
			               			</td>
			               		</xsl:otherwise>
			               	</xsl:choose>
								<!-- 引用SOP -->
								<xsl:choose>
									<xsl:when test="sops/SOPInfo/@ppnumber != ''">
										<td>
											<div style="word-break: break-all;text-align:left;font-size: 12px;" class="paceSopURL">
												<xsl:variable name="paceSopURL" select="@sopURL"/>
												<a style="color:blue" target = "_blank" href="{$paceSopURL}"><xsl:choose>
													<xsl:when test="sops/SOPInfo/@ppnumber != ''"><xsl:value-of select="sops/SOPInfo//@ppnumber" />_<xsl:value-of select="sops/SOPInfo//@name" />
													</xsl:when>
													<xsl:otherwise> </xsl:otherwise>
												</xsl:choose></a>
											</div>
										</td>
									</xsl:when>
									<xsl:otherwise><td></td></xsl:otherwise>
								</xsl:choose>

								<!-- 照片样张 -->
								<xsl:choose>
									<xsl:when test="photoRecords/photoRecord/@photoNumber != ''">
										<td>
											<div style="word-break: break-all;text-align:left;font-size: 12px;" class="pacePhotoURL">
												<xsl:variable name="pacePhotoURL" select="@photoUrl"/>
												<a style="color:blue" target = "_blank" href="{$pacePhotoURL}">下载</a>
											</div>
										</td>
									</xsl:when>
									<xsl:otherwise><td></td></xsl:otherwise>
								</xsl:choose>

			                 <xsl:if test="images/PDrawingInfo">
			                 	 <tr>
				                  <td colspan="6">
				                      <div class="tup-auto">
				                      	<xsl:for-each select="images/PDrawingInfo">
				                      		<img class="imgSrcVal" onclick="showImageModel(this,true)">
				                      			<xsl:attribute name="src">
				                      				<xsl:value-of select="@absolutePath" />
				                      			</xsl:attribute>
				                      			<xsl:if test="@annoName">
				                      				<xsl:attribute name="annoName">
				                      					<xsl:value-of select="@annoName" />
				                      				</xsl:attribute>
				                      			</xsl:if>
				                      			<xsl:if test="@annoPic">
				                      				<xsl:attribute name="annoPic">
				                      					<xsl:value-of select="@annoPic" />
				                      				</xsl:attribute>
				                      			</xsl:if>
			                      			</img>
						                 </xsl:for-each>
				                      </div>
				                  </td>
				                </tr>
			                 </xsl:if>
			               </tr>
		           		</xsl:for-each>
		           </table>
		           </xsl:if>
		       </div>
	       </div>
	       <!--工步内容结束-->
	       <!--重复部分结束-->
       </xsl:for-each>
	</xsl:template>

	<xsl:template match="technics/QMFawTechnicsInfo/technicsStateTables">
		<xsl:for-each select="technicsStateTable">
			<div class="sh_div" style="visibility: hidden;">
				<xsl:attribute name="name"><xsl:value-of select="@zzdw"/></xsl:attribute>
				<div class="order-title">工艺状态表信息</div>
				<table width="736" border="0" cellspacing="3" cellpadding="0">
		           	<tr>
			             <td class="cailiao-title-bg">制造单位</td>
			             <td class="cailiao-title-bg">使用单位</td>
			             <td class="cailiao-title-bg">工艺状态</td>
		          	</tr>
		           	<tr>
				         <td><xsl:value-of select="@zzdw" /></td>
				         <td><xsl:value-of select="@sydw" /></td>
				         <td>
				         	<div style="word-break: break-all;text-align:left;font-size: 12px;" class="procedureContent">
       							<xsl:value-of select="gyzt" />
       						</div>
				         </td>
			        </tr>
		        </table>
		        <img>
         			<xsl:attribute name="src">
         				<xsl:value-of select="@absolutePath" />
         			</xsl:attribute>
         		</img>
	        </div>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>