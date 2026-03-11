<%@ page import="wt.httpgw.URLFactory" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <%
        URLFactory factory = new URLFactory();
        String oid = request.getParameter("oid");
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
    %>
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/netmarkets/javascript/jointjs/css/joint.min.css">
    <script type="text/javascript" src="<%=basePath%>/netmarkets/javascript/jointjs/js/jquery.js"></script>
    <script type="text/javascript" src="<%=basePath%>/netmarkets/javascript/jointjs/js/lodash.js"></script>
    <script type="text/javascript" src="<%=basePath%>/netmarkets/javascript/jointjs/js/backbone.js"></script>
    <script type="text/javascript" src="<%=basePath%>/netmarkets/javascript/jointjs/js/joint.min.js"></script>

    <title>影响分析流程图</title>

    <style>
        html, body {
            height: 100%;
            margin: 0;
            overflow: hidden; /* 防止出现滚动条 */
        }

        #paper {
            width: 100%;
            height: 100%;
            border: 1px solid #ccc;
        }

        .link-tools {
            display: none !important; /* 隐藏删除工具 */
        }
    </style>
</head>
<body class="x-body">
<div id="paper" style="width: 100%;"></div>
<script>
    var graph = new joint.dia.Graph();
    var paper = new joint.dia.Paper({
        el: document.getElementById('paper'),
        model: graph,
        width: window.innerWidth, // 动态设置宽度
        height: window.innerHeight, // 动态设置高度
        gridSize: 1,
        interactive: function () {
            // 禁用所有交互行为
            return false;
        }
    });

    // 调整画布大小以适应窗口大小
    function resizePaper() {
        paper.setDimensions(window.innerWidth, window.innerHeight);
    }

    // 监听窗口大小变化
    window.addEventListener('resize', resizePaper);

    // 定义框的形状
    function state(id, pre, type, x, y, width, height, title, header, text, status) {
        var stroke = "#4DD51B";
        var fill = "#C8FFB4";
        if (status == '未完成') {
            stroke = "#CACBCB";
            fill = "#A5A3A3";
        }
        if (type == 'RECT') {
            var cell = new joint.shapes.standard.Rectangle({
                id: id
            });
            cell.position(x, y);
            cell.resize(width, height);
            cell.attr({
                body: {
                    fill: '#5fc7fc',
                    stroke: '#3e94f3', // 边框颜色
                    'stroke-width': 2, // 边框粗细
                    rx: 10, // 圆角
                    ry: 10 // 圆角
                },
                label: {
                    text: text,
                    fill: 'black', // 文字颜色
                    'font-size': 24, // 字体大小
                    'text-anchor': 'middle',
                    'y-alignment': 'middle',
                    'x-alignment': 'middle'

                }
            });
            graph.addCell(cell);

            if (pre != null && pre != '') {
                link(pre, id, "", false);
            }

            return cell;
        } else if (type == 'HEAD') {
            var rect = new joint.shapes.standard.HeaderedRectangle({
                id: id
            });
            rect.resize(width, height);
            rect.position(x, y);
            rect.attr('root/title', title);  // 设置 title

            // 设置头部样式和文本
            rect.attr('header/stroke', stroke);  // 头部边框颜色
            rect.attr('header/stroke-width', 2);    // 头部边框宽度
            rect.attr('header/fill', fill);    // 头部背景色
            rect.attr('header/height', 30);         // 头部高度
            rect.attr('headerText/text', header);   // 头部文本

            // 设置主体文本样式
            rect.attr('body/stroke', stroke);     // 主体边框颜色
            rect.attr('body/stroke-width', 2);       // 主体边框宽度
            rect.attr('body/fill', fill);       // 主体背景色
            rect.attr('body/rx', 2);                 // 圆角半径
            rect.attr('body/ry', 2);
            rect.attr('bodyText/text', text);        // 主体文本

            rect.addTo(graph);

            if (pre != null && pre != '') {
                if(header == '工艺更改申请单' || header =='设计更改/偏离签审包'){
                    link(pre, id, "", true);
                }else {
                    link(pre, id, "", false);
                }

            }

            return rect;

        }

    }


    // 连接线
    function link(source, target, label, original) {
        if(original){
            var sources = source.split('@');
            for (let i = 0; i < sources.length; i++) {
                var cell = new joint.dia.Link({
                    source: {id: sources[i]},
                    target: {id: target},
                    labels: [{position: 0.5, attrs: {text: {text: label || '', 'font-weight': 'bold', 'font-size': 12}}}],
                    router: {name: 'manhattan'}, // 设置连线弯曲样式 manhattan直角
                    attrs: {
                        '.connection': {
                            stroke: '#FF5733', // 改变连线颜色
                            'stroke-width': 2 // 改变连线粗细
                        },
                        '.marker-target': {
                            fill: '#FF5733', // 箭头颜色
                            d: 'M 8 0 L 0 4 L 8 8 z' // 箭头样式
                        },
                    },
                    connector: {
                        name: 'rounded',
                        args: {radius: 10}
                    }
                });
                graph.addCell(cell);
            }
        }else {
            var sources = source.split('@');
            for (let i = 0; i < sources.length; i++) {
                // 获取源矩形的边界框
                var sourceCell = graph.getCell(sources[i]);
                var sourceBBox = sourceCell.getBBox();

                // 获取目标矩形的边界框
                var targetCell = graph.getCell(target);
                var targetBBox = targetCell.getBBox();

                // 计算源点：矩形的底部中心
                var sourcePoint = {
                    x: sourceBBox.x + sourceBBox.width / 2,
                    y: sourceBBox.y + sourceBBox.height
                };

                // 计算目标点：矩形的顶部中心
                var targetPoint = {
                    x: targetBBox.x + targetBBox.width / 2,
                    y: targetBBox.y
                };

                var cell = new joint.dia.Link({
                    source: { x: sourcePoint.x, y: sourcePoint.y }, // 使用底部中心作为起始点
                    target: { x: targetPoint.x, y: targetPoint.y }, // 使用顶部中心作为目标点
                    labels: [{position: 0.5, attrs: {text: {text: label || '', 'font-weight': 'bold', 'font-size': 12}}}],
                    router: {name: 'manhattan'}, // 设置连线弯曲样式 manhattan直角
                    attrs: {
                        '.connection': {
                            stroke: '#FF5733', // 改变连线颜色
                            'stroke-width': 2 // 改变连线粗细
                        },
                        '.marker-target': {
                            fill: '#FF5733', // 箭头颜色
                            d: 'M 8 0 L 0 4 L 8 8 z' // 箭头样式
                        }
                    },
                    connector: {
                        name: 'rounded',
                        args: { radius: 10 }
                    }
                });

                graph.addCell(cell);
            }
        }
    }



    $(function () {
        //创建元素
        var setY = 20;

        $.ajax({
            url: "<%=basePath%>/netmarkets/jsp/ext/casc/analysisActivity/generateWorkflowData.jsp?oid=<%=oid%>",
            type: "POST",
            success: function (response) {
                try {
                    // 尝试将响应转换为 JSON 对象
                    var result = JSON.parse(response);

                    // 遍历返回的矩形数据
                    result.forEach(function (rect) {
                        state(rect.id, rect.pre, rect.type, rect.x, rect.y, rect.width, rect.height, rect.title, rect.header, rect.label, rect.status);
                    });
                } catch (e) {
                    console.error("解析 JSON 出错: ", e);
                    alert('数据解析失败');
                }
            },
            error: function () {
                alert('数据加载失败');
            }
        });
    });
</script>

</body>

</html>
