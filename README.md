# Java项目日志

*Start in 23/11/2024 Made by Tianlang*

23/11/2024 实现了基本的JFrame框架，进行用户交互界面的设计，实现功能一，合并功能选择模块，解决合并两个模块之间的冲突，

1.**设置用户数据库，解决用户名存储冲突。针对重复用户名进行确认读档判定**  ~~(存在保留意见)~~   之后实现用户历史统计，现有用户排名等（只保留累计分数）累计分数可以替换为累计背单词量 <u>（是否设计用户数据可视化？）</u>

2.开始实现设计要求功能，初步美化界面，保留统计思想-计划在开始菜单设计， ~~主客户端非法的修正~~

3.实现选择模块，1跳转，2,3还未设计暂时输出测试


## 2.1 功能1

​	1>基本实现服务器端：**a.读取文件，使用List存储，WordMap分化。随机化发送英语单词  b.Thread开线程,嵌套计分机制**，使用 getHint目前使用头尾控制一单元  ~~（存在保留意见）~~

```
getHint 接收一个 word 作为输入，并返回一个字符串，其中保留了原单词的第一个和最后一个字符，中间的字符被下划线 (_) 替代。
```

2>合并先前客户端设计**，完善数据统计机制，完善界面大量冲突**，处理客户端无法正常开启问题（解决：编写线程函数，等待函数）

```java
//这个Java代码创建一个新的线程，当线程执行时，会启动一个WordGameServer实例。start()方法开始线程的执行，使得服务器可以与主程序并发运行。
 new Thread(() -> {
                new WordGameServer();
            }).start();
```

3>设计存储机制

​		~~考虑附加一个存储线程（已解决）。存在大量冲突（已解决）~~ 。整合先前设计函数Count，实现统计正确背单词个数（嵌入Client返回客户端），修改了错误实现网络编程的NickName传递方法，更改为客户端内实现



### 冲突

​		功能二需要实现与一不同的服务端，考虑创建新的双端。保留基本的设计观念。



## 2.2功能2

​		冲突较多，实现