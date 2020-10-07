<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="meeting-tool-borad" id="boardTools">
    <div class="blue showon">
        <dl>
            <dd><img src="images/board/first-level/page.png"></dd>
            <dd>页面操作</dd>
        </dl>
        <div class="son close-li">
            <ul>
                <li id="addBoard"><img src="images/board/second-level/page/add.png"> 增加一页</li>
                <li id="deleteBoard"><img src="images/board/second-level/page/del.png">删除一页</li>
                <li id="prevBoard"><img src="images/board/second-level/page/previous.png">前翻一页</li>
                <li id="nextBoard"><img src="images/board/second-level/page/next.png">后翻一页</li>
            </ul>
        </div>
    </div>
    <div class="green setcolor showon set-color">
        <dl>
            <dd><img src="images/board/first-level/setting.png"></dd>
            <dd>设置</dd>
        </dl>
        <div class="son">
            <ul>
                <li>
                    <p>
                        <input id="color_a" type="text" class="input_cxcolor" readonly>
                    </p>
                    设置当前背景颜色
                </li>
                <li>
                    <p>
                        <input id="color_b" type="text" class="input_cxcolor" readonly>
                    </p>
                    设置全局背景颜色
                </li>
                <li>
                    <p>
                        <input id="color_c" type="text" class="input_cxcolor" readonly>
                    </p>
                    设置画笔颜色
                </li>
                <li>
                    <p class="choiceline">
											<span class="ss">
												<input type="radio" name="lines" id="lines1" value="1" checked="checked">
												<label for="lines1" style="cursor:pointer;"></label>
											</span>
                        <span class="dd">
												<input type="radio" name="lines" id="lines2" value="2" >
												<label for="lines2" style="cursor:pointer"></label>
											</span>
                        <span class="bb">
												<input type="radio" name="lines" id="lines3" value="3">
												<label for="lines3" style="cursor:pointer"></label>
											</span>
                    </p>

                    <input type="hidden" name="linevalue" id="linevalue" />
                    设置线条粗细
                </li>
            </ul>
        </div>
    </div>
    <div class="orange kk showon">
        <dl>
            <dd><img src="images/board/first-level/graphics.png"></dd>
            <dd>图形</dd>
        </dl>
        <div class="son close-li">
            <ul>
                <li id="brush"><img src="images/board/second-level/graph/brush.png"> 默认普通画笔</li>
                <li id="line"><img src="images/board/second-level/graph/line.png">直线</li>
                <%--<li id="h-circle"><img src="images/board/second-level/graph/h-circle.png">空心圆</li>
                <li id="s-circle"><img src="images/board/second-level/graph/s-circle.png">实心圆</li>--%>
                <li id="h-rectangle"><img src="images/board/second-level/graph/h-rectangle.png">空心距形</li>
                <li id="s-rectangle"><img src="images/board/second-level/graph/s-rectangle.png">实心距形</li>
                <li id="h-oval"><img src="images/board/second-level/graph/h-oval.png">空心椭圆</li>
                <li id="s-oval"><img src="images/board/second-level/graph/s-oval.png">实心椭圆</li>
            </ul>
        </div>
    </div>
    <div class="orange showon">
        <dl>
            <dd><img src="images/board/first-level/clean.png"></dd>
            <dd>清空操作</dd>
        </dl>
        <div class="son close-li graffiti-li">
            <ul>
                <li id="c-graffiti" class="sk"><img src="images/board/second-level/clean/c-graffiti.png"> 清空当前白板涂鸦</li>
                <li id="c-graffiti-bg" class="sk"><img src="images/board/second-level/clean/c-Graffiti-bg.png">清空当前白板涂鸦+背景色</li>
                <li id="c-graffiti-img" class="sk"><img src="images/board/second-level/clean/c-Graffiti-bg.png">清空当前白板涂鸦+背景图片</li>
                <li id="c-bg" class="sk"><img src="images/board/second-level/clean/c-bg.png">清空全局背景色</li>
            </ul>
        </div>
    </div>
    <div class="blue showon">
        <dl>
            <dd><img src="images/board/first-level/file.png"></dd>
            <dd>文件操作</dd>
        </dl>
        <div class="son close-li">
            <ul>
                <li id="addFile"><img src="images/board/second-level/file/upd.png"/>上传文件</li>
                <input style="display: none;" id="file_input" class="custom-file-input" type="file" onchange="uploadFile();" accept="image/*, application/pdf, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/vnd.openxmlformats-officedocument.presentationml.presentation, application/vnd.ms-works, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.openxmlformats-officedocument.presentationml.slideshow, application/vnd.openxmlformats-officedocument.wordprocessingml.document">
                <li id="delFile"><img src="images/board//second-level/file/del.png">删除文件</li>
                <li id="switchFile"><img src="images/board/second-level/file/cutover.png">切换文件</li>
            </ul>
        </div>
    </div>
    <div class="green textop showon set-color">
        <dl>
            <dd><img src="images/board/first-level/T.png"></dd>
            <dd>文本操作</dd>
        </dl>
        <div class="son ">
            <ul>
                <li id="textIn"><img src="images/board/second-level/text/T.png">文字输入</li>
                <li>
                    <p>
                        <input id="color_d" type="text" class="input_cxcolor" readonly>
                    </p>
                    设置文本颜色
                </li>
                <li>
                    <div class="font">
                        <select class="new set" id="font_size">
                            <option value="8">8</option>
                            <option value="10">10</option>
                            <option value="12" selected>12</option>
                            <option value="14">14</option>
                            <option value="16">16</option>
                            <option value="18">18</option>
                            <option value="20">20</option>
                            <option value="24">24</option>
                        </select>
                    </div>
                </li>
                <li>
                    <div class="font" id="font">
                        <select class="new set" id="fontval">
                            <option>请选择字体</option>
                            <option style="font-family: SimHei, SIMHEI;" value="SimHei">黑体</option>
                            <option style="font-family: Microsoft Yahei, WRYHC;" value="Microsoft YaHei">微软雅黑</option>
                            <option style="font-family: SimSun,SONGTI;" value="SimSun">宋体</option>
                        </select>
                    </div>
                </li>
            </ul>
        </div>

    </div>

    <div class="blue showdown">
        <dl>
            <dd><img src="images/board/first-level/magnifier.png"></dd>
            <dd>缩放</dd>
        </dl>
        <div class="son">
            <ul>
                <li id="zoomIn"><img src="images/board/second-level/zoom/zoomIn.png"> 放大</li>
                <li id="zoomOut"><img src="images/board/second-level/zoom/zoomOut.png">缩小</li>
            </ul>
        </div>
    </div>
    <div class="orange kk showon">
        <dl>
            <dd><img src="images/board/first-level/c&r.png"></dd>
            <dd>撤销与恢复</dd>
        </dl>
        <div class="son close-li">
            <ul>
                <li id="previous"><img src="images/board/second-level/c&r/cancel.png">当前白板页撤销</li>
                <li id="nextStep"><img src="images/board/second-level/c&r/restore.png">当前白板页恢复</li>
            </ul>
        </div>
    </div>
    <div class="blue showon">
        <dl>
            <dd><img src="images/board/first-level/mouse.png"></dd>
            <dd>选择</dd>
        </dl>
        <div class="son close-li">
            <ul>
                <li id="click-s"><img src="images/board/second-level/chioce/click-s.png">点选</li>
                <li id="frame-s"><img src="images/board/second-level/chioce/frame-s.png">框选</li>
            </ul>
        </div>
    </div>
    <div class="green showon">
        <dl>
            <dd><img src="images/board/first-level/erase.png"></dd>
            <dd>擦除</dd>
        </dl>
        <div class="son close-li">
            <ul>
                <li id="eraser"><img src="images/board/second-level/erase/erase.png"> 橡皮擦</li>
            </ul>
        </div>
    </div>

</div>