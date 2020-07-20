#  NBZxing
 一个稳定完善的扫码库，几行代码既可接入，完美适配各种分辨率，无拉伸。

### 依赖

[![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9qaXRwYWNrLmlvL3YvYWlsaXdlYW4vTkJaeGluZy5zdmc?x-oss-process=image/format,png)](https://jitpack.io/#ailiwean/NBZxing)


```
	        implementation 'com.github.ailiwean:NBZxing:0.0.11'

```
**注意：库中已经包含zxing源码无需再次依赖**


两步搞定

 **step1.  自定义一个View继承ZxingCameraView**
			
```
class CusZxingView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : ZxingCameraView(context, attributeSet, def) {
	
    /***
     * 扫码结果回调
     */
    override fun resultBack(content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    /***
     * 可扩展顶层View
     */
    override fun provideFloorView(): View? {
        return null
    }

    /***
     * 返回扫码类型
     * 1 ScanTypeConfig.HIGH_FREQUENCY 高频率格式(默认)
     * 2 ScanTypeConfig.ALL  所有格式
     * 3 ScanTypeConfig.ONLY_QR_CODE 仅QR_CODE格式
     * 4 ScanTypeConfig.TWO_DIMENSION 所有二维码格式
     * 5 ScanTypeConfig.ONE_DIMENSION 所有一维码格式
     */
    override fun getScanType(): ScanTypeConfig {
        return ScanTypeConfig.HIGH_FREQUENCY
    }

}
```

 **step2.  同步AppComActivity生命周期**

在onCreate方法中调用

```
   this.<CusZxingView>findViewById(R.id.cusZxing)
                .synchLifeStart(this);
```

-------

#### 下载体验
![在这里插入图片描述](https://imgconvert.csdnimg.cn/aHR0cHM6Ly93d3cucGd5ZXIuY29tL2FwcC9xcmNvZGUvaWlabg?x-oss-process=image/format,png)
 - 安装密码 ： 1234

😊 <如果觉得还凑合不错，强烈请求来上一个star 。 开源不易，多多鼓励，感谢！>  😊
##### 联系我
微信： 17391961576
QQ:  1581209979
