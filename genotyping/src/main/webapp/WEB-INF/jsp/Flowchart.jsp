<%@ page import="java.io.OutputStream" %>
<%@ page import="java.io.FileOutputStream" %>
<%@ page import="java.security.Principal" %>
<%@ page import="java.io.PrintWriter" %>
<%@page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Flowchart</title>
    <meta name="description" content="Interactive flowchart diagram implemented by GoJS in JavaScript for HTML."/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Copyright 1998-2020 by Northwoods Software Corporation. -->

    <script type="text/javascript" src="/js/go.js"></script>
    <link href='https://fonts.googleapis.com/css?family=Lato:300,400,700' rel='stylesheet' type='text/css'>
    <script id="code">
        function init() {
            
            // make构建模板
            var $ = go.GraphObject.make;  // for conciseness in defining templates
            myDiagram =
                $(go.Diagram, document.getElementById("myDiagramDiv"),  // must name or refer to the DIV HTML element
                    {
                        // 每次画线后调用的事件：为条件连线加上标签
                        "LinkDrawn": showLinkLabel,  // this DiagramEvent listener is defined below
                        // 每次重画线后调用的事件
                        "LinkRelinked": showLinkLabel,
                        // 启用Ctrl-Z和Ctrl-Y撤销重做功能
                        "undoManager.isEnabled": true,  // enable undo & redo
                        // 居中显示内容
                        initialContentAlignment: go.Spot.Center,
                        // 是否允许从Palette面板拖入元素
                        allowDrop: true,
                    });

            // 当图有改动时，在页面标题后加*，且启动保存按钮
            myDiagram.addDiagramListener("Modified", function (e) {
                var button = document.getElementById("SaveButton");
                if (button) button.disabled = !myDiagram.isModified;
                var idx = document.title.indexOf("*");
                if (myDiagram.isModified) {
                    if (idx < 0) document.title += "*";
                } else {
                    if (idx >= 0) document.title = document.title.substr(0, idx);
                }
            });

            // 设置节点位置风格，并与模型"loc"属性绑定，该方法会在初始化各种节点模板时使用
            function nodeStyle() {
                return [
                    // 将节点位置信息 Node.location 同节点模型数据中 "loc" 属性绑定：
                    // 节点位置信息从 节点模型 "loc" 属性获取, 并由静态方法 Point.parse 解析.
                    // 如果节点位置改变了, 会自动更新节点模型中"loc"属性, 并由 Point.stringify 方法转化为字符串
                    new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
                    {
                        // 节点位置 Node.location 定位在节点的中心
                        locationSpot: go.Spot.Center
                    }
                ];
            }
            // 创建"port"方法，"port"是一个透明的长方形细长图块，在每个节点的四个边界上，如果鼠标移到节点某个边界上，它会高亮
            // "name": "port" ID，即GraphObject.portId,
            // "align": 决定"port" 属于节点4条边的哪条
            // "spot": 控制连线连入/连出的位置，如go.Spot.Top指, go.Spot.TopSide
            // "output" / "input": 布尔型，指定是否允许连线从此"port"连入或连出
            function makePort(name, align, spot, output, input) {
                // 表示如果是上，下，边界则是水平的"port"
                var horizontal = align.equals(go.Spot.Top) || align.equals(go.Spot.Bottom);
                return $(go.Shape,
                    {
                        fill: "transparent",  // 默认透明不现实
                        strokeWidth: 0,  // 无边框
                        width: horizontal ? NaN : 8,  // 垂直"port"则8像素宽
                        height: !horizontal ? NaN : 8,  // 水平"port"则8像素
                        alignment: align,  // 同其节点对齐
                        stretch: (horizontal ? go.GraphObject.Horizontal : go.GraphObject.Vertical),//自动同其节点一同伸缩
                        portId: name,  // 声明ID
                        fromSpot: spot,  // 声明连线头连出此"port"的位置
                        fromLinkable: output,  // 布尔型，是否允许连线从此"port"连出
                        toSpot: spot,  // 声明连线尾连入此"port"的位置
                        toLinkable: input,  // 布尔型，是否允许连线从此"port"连出
                        cursor: "pointer",  // 鼠标由指针改为手指，表示此处可点击生成连线
                        mouseEnter: function (e, port) {  //鼠标移到"port"位置后，高亮
                            if (!e.diagram.isReadOnly) port.fill = "rgba(255,0,255,0.5)";
                        },
                        mouseLeave: function (e, port) {// 鼠标移出"port"位置后，透明
                            port.fill = "transparent";
                        }
                    });
            }
            // 定义图形上的文字风格
            function textStyle() {
                return {
                    font: "bold 11pt Lato, Helvetica, Arial, sans-serif",
                    stroke: "#F8F8F8"
                }
            }

            // 定义步骤（默认类型）节点的模板
            myDiagram.nodeTemplateMap.add("",  // the default category
                $(go.Node, "Table", nodeStyle(),
                    // 步骤节点是一个包含可编辑文字块的长方形图块
                    $(go.Panel, "Auto",
                        $(go.Shape, "Rectangle",
                            {fill: "#282c34", stroke: "#00A9C9", strokeWidth: 3.5},
                            new go.Binding("figure", "figure")),
                        $(go.TextBlock, textStyle(),
                            {
                                margin: 8,
                                maxSize: new go.Size(160, NaN),
                                wrap: go.TextBlock.WrapFit,// 尺寸自适应
                                editable: true// 文字可编辑
                            },
                            new go.Binding("text").makeTwoWay())// 双向绑定模型中"text"属性
                    ),
                    // 上、左、右可以入，左、右、下可以出
                    // "Top"表示中心，"TopSide"表示上方任一位置，自动选择
                    makePort("T", go.Spot.Top, go.Spot.TopSide, false, true),
                    makePort("L", go.Spot.Left, go.Spot.LeftSide, true, true),
                    makePort("R", go.Spot.Right, go.Spot.RightSide, true, true),
                    makePort("B", go.Spot.Bottom, go.Spot.BottomSide, true, false)
                ));
            // 定义条件节点的模板
            myDiagram.nodeTemplateMap.add("Conditional",
                $(go.Node, "Table", nodeStyle(),
                    // 条件节点是一个包含可编辑文字块的菱形图块
                    $(go.Panel, "Auto",
                        $(go.Shape, "Diamond",
                            {fill: "#282c34", stroke: "#00A9C9", strokeWidth: 3.5},
                            new go.Binding("figure", "figure")),
                        $(go.TextBlock, textStyle(),
                            {
                                margin: 8,
                                maxSize: new go.Size(160, NaN),
                                wrap: go.TextBlock.WrapFit,
                                editable: true
                            },
                            new go.Binding("text").makeTwoWay())
                    ),
                    // 上、左、右可以入，左、右、下可以出
                    makePort("T", go.Spot.Top, go.Spot.Top, false, true),
                    makePort("L", go.Spot.Left, go.Spot.Left, true, true),
                    makePort("R", go.Spot.Right, go.Spot.Right, true, true),
                    makePort("B", go.Spot.Bottom, go.Spot.Bottom, true, false)
                ));
            //定义算法模板
            myDiagram.nodeTemplateMap.add("algorithm",
                $(go.Node, "Table", nodeStyle(),
                    $(go.Panel, "Auto",
                        $(go.Shape, "RoundedRectangle",
                            {fill: "green", stroke: "#00A9C9", strokeWidth: 3.5},
                            new go.Binding("figure", "figure")),
                        $(go.TextBlock, textStyle(),
                            {
                                margin: 8,
                                maxSize: new go.Size(160, NaN),
                                wrap: go.TextBlock.WrapFit,// 尺寸自适应
                                editable: false// 文字不可编辑
                            },
                            new go.Binding("text").makeTwoWay())// 双向绑定模型中"text"属性
                    ),
                    // 上、左、右可以入，左、右、下可以出
                    // "Top"表示中心，"TopSide"表示上方任一位置，自动选择
                    makePort("T", go.Spot.Top, go.Spot.TopSide, false, true),
                    makePort("L", go.Spot.Left, go.Spot.LeftSide, true, true),
                    makePort("R", go.Spot.Right, go.Spot.RightSide, true, true),
                    makePort("B", go.Spot.Bottom, go.Spot.BottomSide, true, false)
                ));
            // 定义开始节点的模板
            myDiagram.nodeTemplateMap.add("Start",
                $(go.Node, "Table", nodeStyle(),
                    $(go.Panel, "Spot",
                        $(go.Shape, "Circle",
                            {desiredSize: new go.Size(70, 70), fill: "#282c34", stroke: "#09d3ac", strokeWidth: 3.5}),
                        $(go.TextBlock, "Start", textStyle(),
                            new go.Binding("text"))
                    ),
                    // 左、右、下可以出，但都不可入
                    makePort("L", go.Spot.Left, go.Spot.Left, true, false),
                    makePort("R", go.Spot.Right, go.Spot.Right, true, false),
                    makePort("B", go.Spot.Bottom, go.Spot.Bottom, true, false)
                ));
            // 定义结束节点的模板
            myDiagram.nodeTemplateMap.add("End",
                $(go.Node, "Table", nodeStyle(),
                    // 结束节点是一个圆形图块，文字不可编辑
                    $(go.Panel, "Spot",
                        $(go.Shape, "Circle",
                            {desiredSize: new go.Size(60, 60), fill: "#282c34", stroke: "#DC3C00", strokeWidth: 3.5}),
                        $(go.TextBlock, "End", textStyle(),
                            new go.Binding("text"))
                    ),
                    // 上、左、右可以入，但都不可出
                    makePort("T", go.Spot.Top, go.Spot.Top, false, true),
                    makePort("L", go.Spot.Left, go.Spot.Left, false, true),
                    makePort("R", go.Spot.Right, go.Spot.Right, false, true)
                ));

            // taken from ../extensions/Figures.js:
            go.Shape.defineFigureGenerator("File", function (shape, w, h) {
                var geo = new go.Geometry();
                var fig = new go.PathFigure(0, 0, true); // starting point
                geo.add(fig);
                fig.add(new go.PathSegment(go.PathSegment.Line, .75 * w, 0));
                fig.add(new go.PathSegment(go.PathSegment.Line, w, .25 * h));
                fig.add(new go.PathSegment(go.PathSegment.Line, w, h));
                fig.add(new go.PathSegment(go.PathSegment.Line, 0, h).close());
                var fig2 = new go.PathFigure(.75 * w, 0, false);
                geo.add(fig2);
                // The Fold
                fig2.add(new go.PathSegment(go.PathSegment.Line, .75 * w, .25 * h));
                fig2.add(new go.PathSegment(go.PathSegment.Line, w, .25 * h));
                geo.spot1 = new go.Spot(0, .25);
                geo.spot2 = go.Spot.BottomRight;
                return geo;
            });
             // 定义注释节点的模板
            myDiagram.nodeTemplateMap.add("Comment",
                // 注释节点是一个包含可编辑文字块的文件图块
                $(go.Node, "Auto", nodeStyle(),
                    $(go.Shape, "File",
                        {fill: "#282c34", stroke: "#DEE0A3", strokeWidth: 3}),
                    $(go.TextBlock, textStyle(),
                        {
                            margin: 8,
                            maxSize: new go.Size(200, NaN),
                            wrap: go.TextBlock.WrapFit,// 尺寸自适应
                            textAlign: "center",
                            editable: true// 文字可编辑
                        },
                        new go.Binding("text").makeTwoWay())
                    // 不支持连线入和出
                ));


            // 初始化连接线的模板
            myDiagram.linkTemplate =
                $(go.Link,  // 所有连接线
                    {
                        routing: go.Link.AvoidsNodes,// 连接线避开节点
                        curve: go.Link.JumpOver,
                        corner: 5, toShortLength: 4,// 直角弧度，箭头弧度
                        relinkableFrom: true,// 允许连线头重设
                        relinkableTo: true,// 允许连线尾重设
                        reshapable: true,// 允许线形修改
                        resegmentable: true,// 允许连线分割（折线）修改
                        // 鼠标移到连线上后高亮
                        mouseEnter: function (e, link) {
                            link.findObject("HIGHLIGHT").stroke = "rgba(30,144,255,0.2)";
                        },
                        mouseLeave: function (e, link) {
                            link.findObject("HIGHLIGHT").stroke = "transparent";
                        },
                        selectionAdorned: false
                    },
                    new go.Binding("points").makeTwoWay(), // 双向绑定模型中"points"数组属性
                    $(go.Shape,  // 隐藏的连线形状，8个像素粗细，当鼠标移上后显示
                        {isPanelMain: true, strokeWidth: 8, stroke: "transparent", name: "HIGHLIGHT"}),
                    $(go.Shape,  // 连线规格（颜色，选中/非选中，粗细）
                        {isPanelMain: true, stroke: "gray", strokeWidth: 2},
                        new go.Binding("stroke", "isSelected", function (sel) {
                            return sel ? "dodgerblue" : "gray";
                        }).ofObject()),
                    $(go.Shape,   // 箭头规格
                        {toArrow: "standard", strokeWidth: 0, fill: "gray"}),
                    $(go.Panel, "Auto",  // 连线标签，默认不显示
                        {visible: false, name: "LABEL", segmentIndex: 2, segmentFraction: 0.5},
                        new go.Binding("visible", "visible").makeTwoWay(),// 双向绑定模型中"visible"属性
                        $(go.Shape, "RoundedRectangle",  // 连线中显示的标签形状
                            {fill: "#F8F8F8", strokeWidth: 0}),
                        $(go.TextBlock, "Yes",  // // 连线中显示的默认标签文字
                            {
                                textAlign: "center",
                                font: "10pt helvetica, arial, sans-serif",
                                stroke: "#333333",
                                editable: true
                            },
                            new go.Binding("text").makeTwoWay()) // 双向绑定模型中"text"属性
                    )
                );

            // 此事件方法由整个画板的LinkDrawn和LinkRelinked事件触发
            // 如果连线是从"conditional"条件节点出发，则将连线上的标签显示出来
            function showLinkLabel(e) {
                var label = e.subject.findObject("LABEL");
                if (label !== null) label.visible = (e.subject.fromNode.data.category === "Conditional");
            }

            // 临时的连线（还在画图中），包括重连的连线，都保持直角
            myDiagram.toolManager.linkingTool.temporaryLink.routing = go.Link.Orthogonal;
            myDiagram.toolManager.relinkingTool.temporaryLink.routing = go.Link.Orthogonal;

            load();  // load an initial diagram from some JSON text

            // 在图形页面的左边初始化图例Palette面板
            myPalette =
                $(go.Palette, "myPaletteDiv", // 必须同HTML中Div元素id一致
                    {
                        // Instead of the default animation, use a custom fade-down
                        "animationManager.initialAnimationStyle": go.AnimationManager.None,
                        "InitialAnimationStarting": animateFadeDown, // 使用此函数设置动画

                        nodeTemplateMap: myDiagram.nodeTemplateMap,  // 同myDiagram公用一种node节点模板
                        model: new go.GraphLinksModel([  // 初始化Palette面板里的内容
                            {category: "Start", text: "Start"},
                            {text: "Step"},
                            {category: "Conditional", text: "???"},
                            {category: "End", text: "End"},
                            {category: "Comment", text: "Comment"},
                            {category: "algorithm", text: "sample splitting"},
                            {category: "algorithm", text: "BWA"},
                            {category: "algorithm", text: "SAMTools"},
                            {category: "algorithm", text: "IGVTools"},
                        ])
                    });

            // 动画效果
            function animateFadeDown(e) {
                var diagram = e.diagram;
                var animation = new go.Animation();
                animation.isViewportUnconstrained = true; // So Diagram positioning rules let the animation start off-screen
                animation.easing = go.Animation.EaseOutExpo;
                animation.duration = 900;
                // Fade "down", in other words, fade in from above
                animation.add(diagram, 'position', diagram.position.copy().offset(0, 200), diagram.position);
                animation.add(diagram, 'opacity', 0, 1);
                animation.start();
            }

        } // end init
        // 将go模型以JSon格式保存在文本框内
        function save() {
            document.getElementById("mySavedModel").value = myDiagram.model.toJson();
            myDiagram.isModified = false;
        }
        // 初始化模型范例
        function load() {
            myDiagram.model = go.Model.fromJson(document.getElementById("mySavedModel").value);
        }

    </script>
</head>
    <body onload="init()">
        <div id="sample">
            <div style="width: 100%; display: flex; justify-content: space-between">
                <div id="myPaletteDiv" style="width: 200px; margin-right: 2px; background-color: skyblue;"></div>
                <div id="myDiagramDiv" style="flex-grow: 1; height: 1000px; background-color: #282c34;"></div>
            </div>
            <button id="SaveButton" onclick="save()">保存</button>
            <button onclick="load()">加载</button>


            <form action="/getJson" method="post" enctype="multipart/form-data">
                <button type="submit">运行</button>
                Diagram Model saved in JSON format:
                <textarea id="mySavedModel" name="myJson" style="width:100%;height:300px">
{ "class": "GraphLinksModel",
  "linkFromPortIdProperty": "fromPort",
  "linkToPortIdProperty": "toPort",
  "nodeDataArray": [
{"category":"Comment", "text":"Genotyping", "key":-5, "loc":"-174 -396"},
{"category":"Start", "text":"Start", "key":-1, "loc":"158 -382"},
{"text":"CleanData.fq", "key":-2, "loc":"158 -305"},
{"category":"algorithm", "text":"sample splitting", "key":-6, "loc":"158 -242"},
{"text":"Samples_1.fq\nSamples_2.fq", "key":-7, "loc":"158 -172"},
{"text":"reference.fq", "key":-8, "loc":"-14 -167"},
{"category":"algorithm", "text":"BWA", "key":-9, "loc":"71.94868850708008 -99.75"},
{"text":"samples.sam", "key":-10, "loc":"72 -39"},
{"text":"reference.fa.amb\nreference.fa.ann\nreference.fa.bwt\nreference.fa.pac\nreference.fa.sa", "key":-11, "loc":"-211 -100"},
{"category":"algorithm", "text":"SAMTools", "key":-12, "loc":"72 24"},
{"text":"samples.bam", "key":-13, "loc":"-144 90.44444465637207"},
{"text":"samples.sorted.bam", "key":-14, "loc":"72 90.44444465637207"},
{"text":"samples.sorted.bam.bai", "key":-15, "loc":"301 91.44444465637207"},
{"category":"algorithm", "text":"IGVTools", "key":-16, "loc":"72 164.11111450195312"},
{"text":"samples.wig", "key":-17, "loc":"72 233.77777862548828"},
{"category":"End", "text":"End", "key":-4, "loc":"72 311.2222213745117"}
 ],
  "linkDataArray": [
{"from":-1, "to":-2, "fromPort":"B", "toPort":"T", "points":[158,-345.25,158,-335.25,158,-334.4309814453125,158,-334.4309814453125,158,-333.611962890625,158,-323.611962890625]},
{"from":-2, "to":-6, "fromPort":"B", "toPort":"T", "points":[158,-286.38803710937503,158,-276.38803710937503,158,-274.880711874577,158,-274.880711874577,158,-273.37338663977897,158,-263.37338663977897]},
{"from":-6, "to":-7, "fromPort":"B", "toPort":"T", "points":[158,-220.62661336022103,158,-210.62661336022103,158,-210.0502695707355,158,-210.0502695707355,158,-209.47392578125,158,-199.47392578125]},
{"from":-7, "to":-9, "fromPort":"B", "toPort":"T", "points":[158,-144.52607421874998,158,-134.52607421874998,158,-132.8247304292645,81.85854805920692,-132.8247304292645,81.85854805920692,-131.12338663977897,81.85854805920692,-121.12338663977897]},
{"from":-8, "to":-9, "fromPort":"B", "toPort":"T", "points":[-14,-148.38803710937498,-14,-138.38803710937498,-14,-134.75571187457697,62.03882895495323,-134.75571187457697,62.03882895495323,-131.12338663977897,62.03882895495323,-121.12338663977897]},
{"from":-9, "to":-10, "fromPort":"B", "toPort":"T", "points":[71.94868850708008,-78.37661336022103,71.94868850708008,-68.37661336022103,71.94868850708008,-67.99428812542301,72,-67.99428812542301,72,-67.611962890625,72,-57.611962890625]},
{"from":-9, "to":-11, "fromPort":"L", "toPort":"R", "points":[42.219109850699546,-99.75,32.219109850699546,-99.75,-51.62298901996273,-99.75,-51.62298901996273,-100,-135.465087890625,-100,-145.465087890625,-100]},
{"from":-10, "to":-12, "fromPort":"B", "toPort":"T", "points":[72,-20.388037109375,72,-10.388037109374999,72,-8.880711874576985,72,-8.880711874576985,72,-7.373386639778971,72,2.626613360221029]},
{"from":-12, "to":-14, "fromPort":"B", "toPort":"T", "points":[72,45.37338663977897,72,55.37338663977897,72,58.602934202763024,72,58.602934202763024,72,61.832481765747076,72,71.83248176574708]},
{"from":-12, "to":-13, "fromPort":"B", "toPort":"T", "points":[49.19866434612614,45.37338663977897,49.19866434612614,55.37338663977897,49.19866434612614,58.602934202763024,-144,58.602934202763024,-144,61.832481765747076,-144,71.83248176574708]},
{"from":-12, "to":-15, "fromPort":"B", "toPort":"T", "points":[94.80133565387386,45.37338663977897,94.80133565387386,55.37338663977897,94.80133565387386,59.102934202763024,301,59.102934202763024,301,62.832481765747076,301,72.83248176574708]},
{"from":-14, "to":-16, "fromPort":"B", "toPort":"T", "points":[72,109.05640754699708,72,119.05640754699708,72,125.89706770458562,72,125.89706770458562,72,132.73772786217415,72,142.73772786217415]},
{"from":-16, "to":-17, "fromPort":"B", "toPort":"T", "points":[72,185.4845011417321,72,195.4845011417321,72,200.3251584382977,72,200.3251584382977,72,205.1658157348633,72,215.1658157348633]},
{"from":-17, "to":-4, "fromPort":"B", "toPort":"T", "points":[72,252.3897415161133,72,262.3897415161133,72,265.9309814453125,72,265.9309814453125,72,269.4722213745117,72,279.4722213745117]}
 ]}
                </textarea>

            </form>
        </div>
    </body>
</html>
