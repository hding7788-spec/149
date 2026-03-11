<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="ext.casc.distribute.service.ManufactoryDataService"%>
<%@page import="ext.casc.distribute.controller.DistributeController"%>
<%@page import="java.util.Map"%>
<%
    String changeNumber = request.getParameter("changeNumber");
    String partId = request.getParameter("partId");
    String styleId = request.getParameter("styleId");

    if("0".equals(styleId)==false && "1".equals(styleId)==false)
    {
        %>
            连接中...
            <script>
                var styleId = localStorage.getItem("workflow_graph_style_id");
                if(styleId!="0" &&  styleId!="1" )
                {
                    styleId  = "0";
                }
                localStorage.setItem("workflow_graph_style_id", styleId);
                var url = "./distribute_workflow.jsp?changeNumber=<%=changeNumber%>&partId=<%=partId%>&styleId=" + styleId;
                window.location.href = url;
            </script>
        <%
        return;
    }

    String[] colorArr = {
        "#A5A3A3;##CACBCB;#4A4A4A;#5fc7fc;#3e94f3;#000000;#C8FFB4;#4DD51B;#000000;#FF0000;#FF0000",
        "#FFFFFF;#CCCCCC;#4A4A4A;#43A047;#388E3C;#FFFFFF;#E0E0E0;#B0B0B0;#000000;#000000;#000000"
        };

    String colorStr = "";
    if("0".equals(styleId))
    {
        colorStr = colorArr[0];
    }else if("1".equals(styleId))
    {
        colorStr = colorArr[1];
    }

    String[] colorTemp = colorStr.split(";");
    String COLOR_NOT_START_FILL = colorTemp[0];
    String COLOR_NOT_START_BORDER = colorTemp[1];
    String COLOR_NOT_START_TEXT = colorTemp[2];
    String COLOR_IN_WORK_FILL = colorTemp[3];
    String COLOR_IN_WORK_BORDER = colorTemp[4];
    String COLOR_IN_WORK_TEXT = colorTemp[5];
    String COLOR_FINISHED_FILL = colorTemp[6];
    String COLOR_FINISHED_BORDER = colorTemp[7];
    String COLOR_FINISHED_TEXT = colorTemp[8];
    String COLOR_LINE = colorTemp[9];
    String COLOR_LINE_TEXT = colorTemp[10];

    String ajaxDataUrl = "./ajaxdata.jsp?changeNumber="+ changeNumber + "&partId=" + partId + "&uuid=" + java.util.UUID.randomUUID().toString();

    boolean isNoEffect =ManufactoryDataService.isNoEffect(changeNumber, partId);
    if(isNoEffect)
    {
        out.println("<div style='text-align:center;'>在制品和已制品都无影响</div>");
        return;
    }

    Map<String, String> map = DistributeController.getBasicInfoMap(changeNumber, partId);

%>

<html>
  <head>
      <title>流程图 <%=map.get("change")%>  <%=map.get("part")%>  </title>
       <style>
              /* 原布局样式 */
              .helloworld-app { width: 100%; }
              .app-content { height: 380px; }
              .viewport { position: relative; height: 80vh; overflow: hidden; }

              /* 原自定义节点样式 */
              .uml-wrapper-inwork {
                  box-sizing: border-box;
                  width: 100%;
                  height: 100%;
                  background: <%=COLOR_IN_WORK_FILL%>;
                  border: 1px solid <%=COLOR_IN_WORK_BORDER%>;
                  color: <%=COLOR_IN_WORK_TEXT%>;
                  border-radius: 10px;
              }
              .uml-wrapper-finished {
                    box-sizing: border-box;
                    width: 100%;
                    height: 100%;
                    background: <%=COLOR_FINISHED_FILL%>;
                    border: 1px solid <%=COLOR_FINISHED_BORDER%>;
                    color: <%=COLOR_FINISHED_TEXT%>;
                    border-radius: 10px;
              }
              .uml-wrapper-not-start {
                  box-sizing: border-box;
                  width: 100%;
                  height: 100%;
                  background: <%=COLOR_NOT_START_FILL%>;
                  border: 1px solid <%=COLOR_NOT_START_BORDER%>;
                  color: <%=COLOR_NOT_START_TEXT%>;
                  border-radius: 10px;
              }
              .uml-head {
                  font-size: 12px;
                  line-height: 15px;
                  text-align: left;
              }
              .uml-body {
                  border-top: 1px solid #838382;
                  border-bottom: 1px solid #838382;
              }
              .uml-footer {
                  font-size: 12px;
                  line-height: 20px;
                  text-align: left;
              }
              .uml-text {
                    border-top: 1px solid #838382;
                    border-bottom: 1px solid #838382;
                    white-space: nowrap;
                    font-size: 12px;
                    line-height: 15px;
                    text-align: left;
                }

              /* 按钮样式 */
              .control-buttons {
                  position: fixed;
                  top: 20px;
                  left: 20px;
                  z-index: 1000;
                  display: flex;
                  flex-direction: row;
                  gap: 8px;
              }
              .control-btn {
                  padding: 6px 12px;
                  border: 0;
                  border-radius: 4px;
                  #cursor: pointer;
                  font-size: 14px;
                  transition: background 0.3s;
              }
              .control-btn:hover {
                  background: #45a049s;
              }

              .image-mask {
                  position: fixed;
                  top: 0;
                  left: 0;
                  width: 100%;
                  height: 100%;
                  background: rgba(0,0,0,0.5);
                  display: none;
                  justify-content: center;
                  align-items: center;
                  animation: fadeIn 0.3s ease;
                  z-index: 2000;
              }
              .show-image {
                  max-width: 80%;
                  max-height: 80%;
                  cursor: pointer;
                  box-shadow: 0 0 20px rgba(0,0,0,0.3);
                  animation: floatIn 0.3s ease;
              }
              @keyframes fadeIn {
                  from { opacity: 0; }
                  to { opacity: 1; }
              }
              @keyframes floatIn {
                  from { transform: translateY(50px); opacity: 0; }
                  to { transform: translateY(0); opacity: 1; }
              }
          </style>

          <style>
              .menuContainer { position: relative; display: inline-block; }
              .img-trigger { cursor: pointer; }
              .menu {
                  position: absolute;
                  top: 100%;
                  left: 0;
                  margin-top: 5px;
                  display: none;
                  background: #fff;
                  border: 1px solid #ddd;
                  width: 150px;
                  z-index: 5005;
              }
              .menu-item {
                padding: 3px 6px;
                border: 0;
                border-radius: 4px;
                cursor: pointer;
                font-size:10px;
                transition: background 0.3s;
              }
              .menu-item:hover {  /* 新增悬停亮显样式 */
                  background: #f0f0f0;  /* 浅灰色背景 */
              }
          </style>

          <style>

              .tb_trigger {
                  font-size: 2rem;
                  cursor: pointer;
                  background: none;
                  border: none;
                  color: #2563eb;
              }

              .tb_mask {
                  display: none;
                  position: fixed;
                  top: 0;
                  left: 0;
                  width: 100%;
                  height: 100%;
                  background: rgba(0,0,0,0.5);
                  justify-content: center;
                  align-items: center;
                  z-index: 2000;
              }

              .tb_content {
                  background: white;
                  padding: 1rem;
                  border-radius: 4px;
                  max-width: 90%;
                  max-height: 90%;
                  overflow: auto;
              }

              .tb_close {
                  margin-top: 1rem;
                  padding: 0.3rem 1rem;
                  background: #4444ff;
                  color: white;
                  border: none;
                  border-radius: 3px;
                  cursor: pointer;
              }

              .tb_table { border-collapse: collapse; min-width: 300px; }
              .tb_th, .tb_td { padding: 0.5rem 1rem; border: 1px solid #ddd; }
              .tb_th { background: #f5f5f5; }
          </style>
  </head>
  <body>
    <div class="control-buttons"  style="align-items: center;z-index: 1010;" >
        <table>
        <tr><td style="font-size:14px;color:#0000FF">
            <%
                out.println(map.get("change") + "&nbsp;&nbsp;");
                out.println(map.get("part"));
            %>
        </td></tr>

        <tr><td>
        <!-- <img src="./resource/verticalline.png"  width="20" height="20"  /> -->

        <button class="control-btn" style="padding: 5px 10px;font-size:12px; background: <%=COLOR_NOT_START_FILL%>;color: <%=COLOR_NOT_START_TEXT%>;">未开始</button>
        <button class="control-btn" style="padding: 5px 10px;font-size:12px; background: <%=COLOR_IN_WORK_FILL%>;color:  <%=COLOR_IN_WORK_TEXT%>;">进行中</button>
        <button class="control-btn" style="padding: 5px 10px;font-size:12px; background: <%=COLOR_FINISHED_FILL%>;color: <%=COLOR_FINISHED_TEXT%>;">已完成</button>
        &nbsp;
        <img src="./resource/table.png" width="20" height="20" style="cursor: pointer;" id="tbTrigger" />
        <div class="menuContainer">
            <img src="./resource/setting.png"  width="20" height="20" class="img-trigger"/>
            <div class="menu">
                <%
                    for(int i=0;i<colorArr.length;i++)
                    {
                        String[] strArrTemp = colorArr[i].split(";");
                        %>
                        <div class="menu-item" data-message="<%=i%>">
                            <button class="control-btn" style="padding: 3px 6px;font-size:10px; background: <%=strArrTemp[0]%>;color: <%=strArrTemp[2]%>;" >未开始</button>
                            <button class="control-btn" style="padding: 3px 6px;font-size:10px; background: <%=strArrTemp[3]%>;color:  <%=strArrTemp[5]%>;">进行中</button>
                            <button class="control-btn" style="padding: 3px 6px;font-size:10px; background: <%=strArrTemp[6]%>;color: <%=strArrTemp[8]%>;">已完成</button>
                        </div>
                        <%
                    }
                %>

            </div>
        </div>

        <img src="./resource/help.png" alt="帮助" width="20" height="20" style="cursor: pointer;" onclick="showDemoImage()" />

        </td></tr>
        </table>
    </div>

    <div class="image-mask" id="imageMask" onclick="hideImage()" width="80%" height="80%">
        <img src="./resource/demo1.jpg" class="show-image" id="demoImage">
    </div>

    <script src="./resource/index.min.js"></script>
    <link href="./resource/style/index.css" rel="stylesheet">
    <div id="container"></div>

    <div class="tb_mask" id="tbMask">
        <div class="tb_content">
            <table class="tb_table">
                <thead>
                    <tr>
                        <td class="tb_th" colspan="11"><b>在制品处理详情</b></td>
                    </tr>
                    <tr>
                        <td class="tb_th">图号</td>
                        <td class="tb_th">MES路卡号/离散订单号</td>
                        <td class="tb_th">实际处理结论</td>
                        <td class="tb_th">数量</td>
                        <td class="tb_th">实际处理数量</td>
                        <td class="tb_th">不合格品单号</td>
                        <td class="tb_th">返修工艺编号</td>
                        <td class="tb_th">处理状态</td>
                        <td class="tb_th">责任人</td>
                        <td class="tb_th">流程到达时间</td>
                        <td class="tb_th">实际完成时间</td>
                    </tr>
                </thead>
                <tbody id="tableBodyZZP">
                    <tr>
                        <td class="tb_td">-</td><td class="tb_td">-</td><td class="tb_td">-</td>
                        <td class="tb_td">-</td><td class="tb_td">-</td><td class="tb_td">-</td>
                        <td class="tb_td">-</td><td class="tb_td">-</td><td class="tb_td">-</td>
                        <td class="tb_td">-</td><td class="tb_td">-</td>
                    </tr>
                </tbody>
            </table>
            <br>
            <table class="tb_table">
                <thead>
                    <tr>
                        <td class="tb_th" colspan="11"><b>已制品处理详情</b></td>
                    </tr>
                    <tr>
                        <td class="tb_th">图号</td>
                        <td class="tb_th">库存批次号</td>
                        <td class="tb_th">实际处理结论</td>
                        <td class="tb_th">数量</td>
                        <td class="tb_th">实际处理数量</td>
                        <td class="tb_th">报废单号/返修计划号</td>
                        <td class="tb_th">返修工艺编号</td>
                        <td class="tb_th">处理状态</td>
                        <td class="tb_th">责任人</td>
                        <td class="tb_th">流程到达时间</td>
                        <td class="tb_th">实际完成时间</td>
                    </tr>
                </thead>
                <tbody id="tableBodyYZP">
                    <tr>
                        <td class="tb_td">-</td><td class="tb_td">-</td><td class="tb_td">-</td>
                        <td class="tb_td">-</td><td class="tb_td">-</td><td class="tb_td">-</td>
                        <td class="tb_td">-</td><td class="tb_td">-</td><td class="tb_td">-</td>
                        <td class="tb_td">-</td><td class="tb_td">-</td>
                    </tr>
                </tbody>
            </table>
            <br>
            <table class="tb_table">
                <thead>
                    <tr>
                        <td class="tb_th" colspan="11"><b>整件外协处理详情</b></td>
                    </tr>
                    <tr>
                        <td class="tb_th">图号</td>
                        <td class="tb_th">库存批次号</td>
                        <td class="tb_th">实际处理结论</td>
                        <td class="tb_th">数量</td>
                        <td class="tb_th">实际处理数量</td>
                        <td class="tb_th">计划号</td>
                        <td class="tb_th">处理状态</td>
                        <td class="tb_th">责任人</td>
                        <td class="tb_th">流程到达时间</td>
                        <td class="tb_th">实际完成时间</td>
                    </tr>
                </thead>
                <tbody id="tableBodyZJWX">
                    <tr>
                        <td class="tb_td">-</td><td class="tb_td">-</td><td class="tb_td">-</td>
                        <td class="tb_td">-</td><td class="tb_td">-</td><td class="tb_td">-</td>
                        <td class="tb_td">-</td><td class="tb_td">-</td><td class="tb_td">-</td>
                        <td class="tb_td">-</td>
                    </tr>
                </tbody>
            </table>
            <div width="100%" align="center">
                <button class="tb_close" id="tbClose">关闭</button>
            </div>
        </div>
    </div>

    <script>
        const trigger = document.getElementById('tbTrigger');
        const mask = document.getElementById('tbMask');
        const close = document.getElementById('tbClose');

        trigger.onclick = () => mask.style.display = 'flex';
        close.onclick = () => mask.style.display = 'none';
        mask.onclick = (e) => {
            if(e.target === mask) mask.style.display = 'none';
        };

    </script>

  <script>
  // 显示表格结果
  function displayResultsTable(nodes, type) {
    debugger;

    var tableBody = document.getElementById('tableBody' + type);

    // 清空表格
    tableBody.innerHTML = '';

    // 填充表格
    nodes.forEach(node => {
      //console.log(node.text);

      var properties = node.properties;
      var taskType = properties?cusStr(properties.taskType):"";

        if("ZZP"==type)
        {
            if(node.cusText!='在制品返修工艺编制中' && node.cusText!='在制品报废审批中'
                && node.cusText!='在制品工艺升版' && node.cusText!='外协返修' && node.cusText!='在制品无影响' && node.cusText!='在制品已提前返修')
            {
              return;
            }

            if(taskType!='在制品')
            {
              return;
            }
        }else if("YZP"==type)
        {
            if(node.cusText!='已制品返修工艺编制中' && node.cusText!='已制品报废中' && node.cusText!='已制品无影响' && node.cusText!='已制品已提前返修')
            {
              return;
            }

            if(taskType!='已制品')
            {
              return;
            }
        }else if("ZJWX"==type)
         {
             if(node.cusText!='整件外协执行中')
             {
               return;
             }

             if(taskType!='在制品')
             {
               return;
             }
         }

      var changeNumber = "<%=map.get("change")%> ";
      var taskItemObjectNumber = properties?cusStr(properties.taskItemObjectNumber):"";
      var conditionItemType = properties?cusStr(properties.conditionItemType):"";

      var taskState	 = properties?cusStr(properties.branchFlowCurrentNodeState):"";
      var taskOwner	 = properties?cusStr(properties.branchFlowCurrentNodeOwner):"";
      var taskStartDate	 = properties?cusStr(properties.branchFlowCurrentNodeStartDate):"";
      var taskEndDate	 = properties?cusStr(properties.branchFlowCurrentNodeEndDate):"";

      var quantity	 = properties?cusStr(properties.quantity):"";
      var actualQuantity	 = properties?cusStr(properties.actualQuantity):"";
      var extNumber	 = properties?cusStr(properties.extNumber):"";
      var repairProcessNumber	 = properties?cusStr(properties.repairProcessNumber):"";

      var row = document.createElement('tr');
      //row.className = '';
      row.innerHTML = row.innerHTML + "<td class='tb_td'>" + changeNumber + "</td>";
      row.innerHTML = row.innerHTML + "<td class='tb_td'>" + taskItemObjectNumber + "</td>";
      row.innerHTML = row.innerHTML + "<td class='tb_td'>" + conditionItemType + "</td>";
      row.innerHTML = row.innerHTML + "<td class='tb_td'>" + quantity + "</td>";
      row.innerHTML = row.innerHTML + "<td class='tb_td'>" + actualQuantity + "</td>";
      row.innerHTML = row.innerHTML + "<td class='tb_td'>" + extNumber + "</td>";

      if("ZZP"==type || "YZP"==type)
      {
        row.innerHTML = row.innerHTML + "<td class='tb_td'>" + repairProcessNumber + "</td>";
      }

      row.innerHTML = row.innerHTML + "<td class='tb_td'>" + taskState + "</td>";
      row.innerHTML = row.innerHTML + "<td class='tb_td'>" + taskOwner + "</td>";
      row.innerHTML = row.innerHTML + "<td class='tb_td'>" + taskStartDate + "</td>";
      row.innerHTML = row.innerHTML + "<td class='tb_td'>" + taskEndDate + "</td>";

      tableBody.appendChild(row);
    });

    }

    </script>

    <script>

        function showDemoImage() {
            const mask = document.getElementById('imageMask');
            mask.style.display = 'flex';
        }

        function hideImage() {
            const mask = document.getElementById('imageMask');
            mask.style.display = 'none';
        }

        function getPointsList(parentNode, childNode) {
            var temp1 = 40;
            if("circle"==parentNode.type && parentNode.properties && parentNode.properties.r)
            {
                temp1 = parentNode.properties.r;
            }else if("rect"==parentNode.type && parentNode.properties && parentNode.properties.height)
            {
                temp1 = parentNode.properties.height/2;
            }else if("diamond"==parentNode.type && parentNode.properties && parentNode.properties.height)
            {
                temp1 = parentNode.properties.height/2;
            }

            var temp2 = 40;
            if("circle"==childNode.type && childNode.properties && childNode.properties.r)
            {
                temp2 = childNode.properties.r;
            }else if("rect"==childNode.type && childNode.properties && childNode.properties.height)
            {
                temp2 = childNode.properties.height/2;
            }else if("diamond"==childNode.type && childNode.properties && childNode.properties.height)
            {
                temp2 = childNode.properties.height/2;
            }

            var pointsList =  [
                {x: parentNode.x, y: parentNode.y + temp1},
                {x: parentNode.x, y: (parentNode.y+childNode.y)/2},
                {x: childNode.x, y: (parentNode.y+childNode.y)/2},
                {x: childNode.x, y: childNode.y-temp2-1},
            ];
            return pointsList;
        }

        function cusStr(str) {
            return str?str:"";
        }

        /**
         * 修改流程图节点的样式属性
         * @param {Array} nodes 节点数据数组
         * @returns {Array} 修改后的节点数据数组
         */
        function updateNodeStyles(nodes) {
            // 遍历所有节点
            return nodes.map(node => {
                // 确保节点有properties和style属性
                if (!node.properties) node.properties = {};
                if (!node.properties.style) node.properties.style = {};

                // 根据节点类型进行不同处理
                if (node.type === "circle") {
                    // 处理圆形节点
                    const taskState = node.properties.taskState;
                    console.log(taskState);
                    switch(taskState) {
                        case "未开始":
                            node.properties.style.fill = "<%=COLOR_NOT_START_FILL%>";
                            node.properties.style.stroke = "<%=COLOR_NOT_START_BORDER%>";
                            break;
                        case "进行中":
                            node.properties.style.fill = "<%=COLOR_IN_WORK_FILL%>";
                            node.properties.style.stroke = "<%=COLOR_IN_WORK_BORDER%>";
                            break;
                        case "已完成":
                            node.properties.style.fill = "<%=COLOR_FINISHED_FILL%>";
                            node.properties.style.stroke = "<%=COLOR_FINISHED_BORDER%>";
                            break;
                        case "已闭环":
                            node.properties.style.fill = "<%=COLOR_FINISHED_FILL%>";
                            node.properties.style.stroke = "<%=COLOR_FINISHED_BORDER%>";
                            break;
                        default:
                            node.properties.style.fill = "<%=COLOR_NOT_START_FILL%>";
                            node.properties.style.stroke = "<%=COLOR_NOT_START_BORDER%>";
                    }
                } else if (node.type === "diamond") {
                    // 处理菱形节点
                    node.properties.style.fill = "<%=COLOR_FINISHED_FILL%>";
                    node.properties.style.stroke = "<%=COLOR_FINISHED_BORDER%>";
                }

                // 其他类型节点不做处理
                return node;
            });
        }

        class CustomNodeView extends Core.HtmlNode {
            setHtml(rootEl) {
                const { properties } = this.props.model;
                const wrapper = document.createElement('div');
                if("已完成"==cusStr(properties.taskState) || "已闭环"==cusStr(properties.taskState)) {
                    wrapper.className = 'uml-wrapper-finished';
                }else if("未开始"==cusStr(properties.taskState)) {
                    wrapper.className = 'uml-wrapper-not-start';
                }else if("进行中"==cusStr(properties.taskState)) {
                    wrapper.className = 'uml-wrapper-inwork';
                }else {
                    wrapper.className = 'uml-wrapper-not-start';
                }

                var str = "名称：" + properties.taskName;
                str += "<br>单号：" + cusStr(properties.taskItemObjectNumber);
                str += "<br>责任人：" + cusStr(properties.taskOwner);
                str += "<br>开始时间：" + cusStr(properties.taskStartDate);
                str += "<br>完成时间：" + cusStr(properties.taskEndDate);
                str += "<br>任务状态：" + cusStr(properties.taskState);

                wrapper.innerHTML = " <div class=\"uml-text\">" + properties.head + "</div>";
                var titleStr = properties.head + "\n" + str.replace(/<br>/g, "\n");
                wrapper.innerHTML += " <div class=\"uml-text\" title=\"" + titleStr + "\"  >" + str + "<br><br></div>";

                rootEl.innerHTML = '';
                rootEl.appendChild(wrapper);
            }

        }

        class CustomNodeModel extends Core.HtmlNodeModel {
            setAttributes() {
                this.width = 220;
                this.height = 115;
                this.text.editable = false;
            }
        }

        class Node {
            constructor(id, type, x, y, text, properties) {
                this.id = id;
                this.type = type;
                this.x = x;
                this.y = y;
                this.cusText = text;
                this.text = "CustHtmlNode"==type ? "" : text;
                this.properties = properties;
            }
        }

        class Edge {
            constructor(id, type, sourceNodeId, targetNodeId, sourceAnchorId, targetAnchorId, startPoint, endPoint, text) {
                this.id = id;
                //this.type = type;
                this.type = 'custom-edge'; //改为自定义
                this.sourceNodeId = sourceNodeId;
                this.targetNodeId = targetNodeId;
                this.sourceAnchorId = sourceAnchorId;
                this.targetAnchorId = targetAnchorId;
                this.startPoint = startPoint;
                this.endPoint = endPoint;
                this.text = text;
            }
        }

        class GraphData {
            constructor(nodes, edges) {
                this.nodes = nodes;
                this.edges = edges;
            }
        }

        class CustomEdgeModel extends Core.PolylineEdgeModel {
            customTextPosition = true;

            getTextPosition() {
                const pointsList = this.pointsList;
                const position = super.getTextPosition();
                if (!pointsList || pointsList.length < 4) return position;

                var x;
                var y;
                x = (pointsList[2].x + pointsList[3].x)/2;
                y = (pointsList[2].y + pointsList[3].y)/2;

                x = Math.floor(x);
                y = Math.floor(y);

                position.x = x;
                position.y = y-3;
                return position;

            }

            getEdgeStyle() {
                const style = super.getEdgeStyle();
                const { properties } = this;
                //if (properties.isstrokeDashed) {
                //  style.strokeDasharray = '4, 4';
                //}
                style.strokeWidth = 2;
                style.stroke = '<%=COLOR_LINE%>';
                return style;
              }

            getTextStyle() {
                const style = super.getTextStyle();
                style.color = '<%=COLOR_LINE_TEXT%>';
                style.fontSize = 12;
                return style;
            }
        }

        const xhr = new XMLHttpRequest();
        xhr.open('GET', '<%=ajaxDataUrl%>', true);
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                const responseData = JSON.parse(xhr.responseText);
                if(responseData.success)
                {
                    var nodes = responseData.data.nodes.map(node => new Node(node.id, node.type, node.gridX*230, (node.gridY+1)*165, node.text, node.properties));
                    console.log(nodes);
                    nodes = updateNodeStyles(nodes);

                    const edges = responseData.data.edges.map(edge => {
                        const newEdge = new Edge(edge.id, edge.type, edge.sourceNodeId, edge.targetNodeId, edge.sourceAnchorId, edge.targetAnchorId, edge.startPoint, edge.endPoint, edge.text);
                        const sourceNode = nodes.find(n => n.id === edge.sourceNodeId);
                        const targetNode = nodes.find(n => n.id === edge.targetNodeId);
                        if (sourceNode && targetNode) {
                            newEdge.pointsList = getPointsList(sourceNode, targetNode);
                        }
                        return newEdge;
                    });
                    const data = new GraphData(nodes, edges);

                    const lf = new Core.default({
                        container: document.querySelector('#container'),
                        grid: false,
                        stopZoomGraph: false,
                        stopScrollGraph: false,
                        stopMoveGraph: false,
                        adjustEdge: false,
                        adjustEdgeStartAndEnd: false,
                        adjustNodePosition: false,
                        hideAnchors: true,
                        nodeSelectedOutline: true,
                        nodeTextEdit: false,
                        edgeTextEdit: false,
                        nodeTextDraggable: false,
                        edgeTextDraggable: false,
                        background: {
                            backgroundColor: '#ffffff'
                        }
                    });

                    lf.register({
                        type: 'CustHtmlNode',
                        view: CustomNodeView,
                        model: CustomNodeModel
                    });

                    lf.register({
                        type: 'custom-edge',
                        view: Core.PolylineEdge,
                        model: CustomEdgeModel
                    });

                    lf.render(data);
                    lf.translateCenter();
                    lf.fitView();

                    displayResultsTable(nodes, "ZZP");
                    displayResultsTable(nodes, "YZP");
                    displayResultsTable(nodes, "ZJWX");
                }else
                {
                    alert("运行中出现错误:" + responseData.message);
                }

            }else if(xhr.status != 200) {
                alert("运行中出现错误！" + xhr.status);
            }
        };
        xhr.send();
    </script>

    <script>
        const container = document.querySelector('.menuContainer');  /* 获取容器 */
        const img = document.querySelector('.img-trigger');
        const menu = document.querySelector('.menu');

        // 点击图片显示/隐藏菜单
        img.onclick = () => {
            menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
        };

        // 点击子菜单关闭
        document.querySelectorAll('.menu-item').forEach(item => {
            item.addEventListener('click', function() {
                //alert(this.dataset.message);
                menu.style.display = 'none';
                var styleId = this.dataset.message;
                localStorage.setItem("workflow_graph_style_id", styleId);
                var url = "./distribute_workflow.jsp?changeNumber=<%=changeNumber%>&partId=<%=partId%>&styleId=" + styleId;
                window.location.href = url;
            });
        });

        // 点击页面其他区域关闭菜单（关键新增逻辑）
        document.addEventListener('click', (e) => {
            // 如果点击目标不在容器内，则关闭菜单
            if (!container.contains(e.target)) {
                menu.style.display = 'none';
            }
        });

    </script>



  </body>
</html>
