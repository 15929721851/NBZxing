#  NBZxing
 一个稳定完善的扫码库，几行代码既可接入，完美适配各种分辨率无拉伸，可插拔式自定义UI 。
  
  
# 看这里 #  
  发现java的zxing条码有些问题，密集情况的二维码总是扫成条码 ，这两天在研究源码，等这个问题解决后会放出一个版本， 大家有需要可以等那个版本。 
    
  
### 依赖

[![](https://jitpack.io/v/ailiwean/NBZxing.svg)](https://jitpack.io/#ailiwean/NBZxing)    [![](https://jitpack.io/v/ailiwean/NBZxing-Scale.svg)](https://jitpack.io/#ailiwean/NBZxing-Scale)



```
	        implementation 'com.github.ailiwean:NBZxing:0.0.26'
		//若需要使用灰度算法增强库，再次添加以下依赖(纯java超轻量，两个同时依赖,包体积只增大约400kb,混淆后仅200k)
		implementation 'com.github.ailiwean:NBZxing-Scale:0.0.4'
```
[NBZxing-Scale](https://github.com/ailiwean/NBZxing-Scale "NBZxing-Scale")

**注意：库中已经包含zxing源码无需再次依赖**

-------

### WIKI
[如何使用-超简易](https://github.com/ailiwean/NBZxing/wiki)


感谢[@guangming](https://github.com/guangmingxiong9999)提供近百台机型应用测试🙏

#### 下载体验
![在这里插入图片描述](https://imgconvert.csdnimg.cn/aHR0cHM6Ly93d3cucGd5ZXIuY29tL2FwcC9xcmNvZGUvaWlabg?x-oss-process=image/format,png)

- 安装密码1234

###### 😊 <如果觉得还凑合不错，强烈请求来上一个star 。 开源不易，多多鼓励，感谢！>  😊

----

#### 测试二维码

| 标准反色  | ![标准反色](https://github.com/ailiwean/NBZxing/blob/master/qr_test/82984899-9f981600-a025-11ea-9fe6-ad9fead67afa.png "标准反色")  |
| ------------ | ------------ |
| 彩色  | ![彩色](https://github.com/ailiwean/NBZxing/blob/master/qr_test/caise.png "彩色")  |
| 暗色  | ![暗色](https://github.com/ailiwean/NBZxing/blob/master/qr_test/over_dart.png "暗色")  |
|  曝光 |  ![曝光](https://github.com/ailiwean/NBZxing/blob/master/qr_test/over_light.png "曝光") |
|  浅色 | ![浅色](https://github.com/ailiwean/NBZxing/blob/master/qr_test/test_gray.png "浅色")  |
|  间断 | ![间断](https://github.com/ailiwean/NBZxing/blob/master/qr_test/test_inter.png "间断")  |


#### 联系我

`QQ群:  444236054`欢迎进群交流


| 😊  |  😊 |
| ------------ | ------------ |
|  请喝咖啡 | ![pay](https://github.com/ailiwean/NBZxing/blob/master/qr_test/pay.png "pay")  |











