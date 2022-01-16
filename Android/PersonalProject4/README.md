# 个人项目4
# 简单音乐播放器

# 第十二周任务
## 简单音乐播放器
---

### 实验目的
1. 学会使用MediaPlayer
2. 学会简单的多线程编程，使用Handler更新UI
3. 学会使用Service进行后台工作
4. 学会使用Service与Activity进行通信

---
### 实验内容
实现一个简单的播放器，要求功能有：  
<table>
    <tr>
        <td ><img src="https://gitee.com/code_sysu/PersonalProject4/raw/master/manual/images/fig1.jpg" >打开程序主页面</td>
        <td ><img src="https://gitee.com/code_sysu/PersonalProject4/raw/master/manual/images/fig2.jpg" >开始播放</td>
    </tr>
    <tr>
        <td ><img src="https://gitee.com/code_sysu/PersonalProject4/raw/master/manual/images/fig3.jpg" >暂停</td>
        <td ><img src="https://gitee.com/code_sysu/PersonalProject4/raw/master/manual/images/fig1.jpg" >停止</td>
    </tr>
</table>

1. 播放、暂停、停止、退出功能，按停止键会重置封面转角，进度条和播放按钮；按退出键将停止播放并退出程序
2. 后台播放功能，按手机的返回键和home键都不会停止播放，而是转入后台进行播放
3. 进度条显示播放进度、拖动进度条改变进度功能
4. 播放时图片旋转，显示当前播放时间功能，圆形图片的实现使用的是一个开源控件CircleImageView


**附加内容（加分项，加分项每项占10分）**

1.选歌

用户可以点击选歌按钮自己选择歌曲进行播放，要求换歌后不仅能正常实现上述的全部功能，还要求选歌成功后不自动播放，重置播放按钮，重置进度条，重置歌曲封面转动角度，最重要的一点：需要解析mp3文件，并更新封面图片。

---
### 验收内容
1. 布局显示是否正常
2. 播放、暂停、停止功能是否可用，界面显示是否正常
3. 是否可以后台播放
4. 播放时是否显示当前播放时间、位置，以及图片是否旋转
5. 代码+实验报告（先在实验课上检查，检查后再pr）

---

**为了正常运行demo，请将manual中的文件:山高水长.mp3放到/data/目录下，并打开应用的文件读取权限**

---
### 完成期限
第十三周各班实验课进行检查，未通过者需在下一周进行修改与重新检查，如再次未通过则扣除这一周任务的分数。
