# StreamBGHelper
搭配[StreamCompanion](https://github.com/Piotrekol/StreamCompanion)使用的，实时生成缩略图BG的小程序。

需要环境：JRE1.7以上（有switch String，不支持1.6以下的JRE版本）。
运行方式：命令行java -jar xxx.jar或者直接双击（双击没有控制台界面）。

打图的时候开背景暗化，看直播的人看不到BG，光有歌曲名和链接还是差了点。

StreamCompanion必须安装在默认位置，当然你可以clone下来自己改，我懒得给自定义选项。

文件和图片路径如下：
```java
String beatMapFile = "C:\\Program Files (x86)\\StreamCompanion\\Files\\np_playing_DL.txt";
String thumbFile = "C:\\Program Files (x86)\\StreamCompanion\\Files\\thumb.jpg";
```
在OBS里添加下面那个图片就行了。

建议在StreamCompanion的设置里，Map Formatting里那一项，np_playing_DL.txt右边的saved when项目改成All，点左下角的save保存。

这样不管打图/围观都能显示缩略图。
