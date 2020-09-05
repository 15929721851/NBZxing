package com.ailiwean.core.view

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.PointF
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.ailiwean.core.Config.*
import com.ailiwean.core.Result
import com.ailiwean.core.able.AbleManager
import com.ailiwean.core.helper.VibrateHelper
import com.ailiwean.core.zxing.BitmapLuminanceSource
import com.ailiwean.core.zxing.CustomMultiFormatReader
import com.ailiwean.core.zxing.ScanTypeConfig
import com.ailiwean.core.zxing.core.BinaryBitmap
import com.ailiwean.core.zxing.core.common.GlobalHistogramBinarizer
import com.ailiwean.core.zxing.core.common.HybridBinarizer
import com.google.android.cameraview.AspectRatio
import com.google.android.cameraview.BaseCameraView
import com.google.android.cameraview.CameraView
import kotlinx.android.synthetic.*
import java.io.File
import java.lang.ref.WeakReference

/**
 * @Package:        com.google.android.cameraview
 * @ClassName:      ZxingCamera
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/8/23 12:38 AM
 */
abstract class FreeZxingView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
        BaseCameraView(context, attributeSet, def), Handler.Callback, FreeInterface {

    private val handleZX = HandleZX(this)

    private var ableCollect: AbleManager? = null

    private var busHandle: Handler? = null

    init {

        //使用后置相机
        facing = FACING_BACK

        //设定相机数据选取比例
        this.setAspectRatio(AspectRatio.of(16, 9))

    }

    /***
     * 自定义扫描条
     */
    private val scanBarView: ScanBarCallBack? get() = provideScanBarView()

    /**
     *自定义收手电筒
     */
    private val lightView: ScanLightViewCallBack? get() = provideLightView()

    /***
     * 自定义定位点
     */
    private val locView: ScanLocViewCallBack? get() = provideLocView()

    /***
     * 自定义解析区域
     */
    private val parseRect: View? get() = provideParseRectView()

    /***
     * Handler结果回调
     */
    override fun handleMessage(it: Message): Boolean {
        when (it.what) {

            //扫码结果回调
            SCAN_RESULT -> {
                scanSucHelper()
                if (it.obj is Result) {
                    showQRLoc((it.obj as Result).pointF, it.obj.toString())
                }
            }

            //环境亮度变换回调
            LIGHT_CHANGE -> {
                it.obj.toString().toBoolean().let {
                    if (it) lightView?.lightDark()
                    else lightView?.lightBrighter()
                }
            }

            //放大回调
            AUTO_ZOOM -> {
                setZoom(it.obj.toString().toFloat())
            }

        }
        return true
    }

    /***
     * 相机采集数据实时回调
     */
    override fun onPreviewByteBack(camera: CameraView, data: ByteArray) {
        super.onPreviewByteBack(camera, data)
        //解析数据
        ableCollect?.cusAction(data, scanRect.dataX, scanRect.dataY)
    }

    /***
     * 扫码成功后的一些动作
     */
    private fun scanSucHelper() {

        //关闭相机
        onCameraPause()

        //清理线程池任务缓存
        ableCollect?.clear()

        //关闭扫码条动画
        scanBarView?.stopScanAnimator()

        //播放音频
        VibrateHelper.playVibrate()

        //震动
        VibrateHelper.playBeep()

    }

    /***
     * Activity或Fragment创建，详见{@link BaseCameraView.synchLifeStart}
     */
    override fun onCreate() {
        super.onCreate()
        ableCollect = AbleManager.createInstance(handleZX)
    }

    /***
     * Activity或Fragment不可视，详见{@link BaseCameraView.synchLifeStart}
     */
    override fun onPause() {
        super.onPause()
        scanBarView?.stopScanAnimator()
    }

    /***
     * Activity或Fragment生命周期结束销毁，详见{@link BaseCameraView.synchLifeStart}
     */
    override fun onDestroy() {
        super.onDestroy()
        ableCollect?.release()
        busHandle?.looper?.quit()
    }

    /***
     * 显示二维码位置, 动画播放完回调扫描结果
     */
    fun showQRLoc(point: PointF, content: String) {
        locView?.toLocation(point) {
            resultBack(content)
        }
    }

    /***
     * 相机启动后的一些配置初始化
     */
    private fun cameraStartLaterConfig() {

        //自定义
        locView?.cameraStartLaterInit()
        //控件
        scanBarView?.cameraStartLaterInit()
        //初始化
        lightView?.cameraStartLaterInit()
        //设定扫码区域
        post {
            defineScanParseRect(parseRect)
        }
        //注册打开手关闭电筒功能
        lightView?.regLightOperator({
            lightOperator(true)
        }, {
            lightOperator(false)
        })
        //扫码条开始播放动画
        scanBarView?.startScanAnimator()
        //重新装填AbleManager
        ableCollect?.init()
        //配置扫码类型
        initScanType()
        //重新接收数据
        handleZX.init()
        //音频资源加载
        VibrateHelper.playInit()

    }

    /***
     * 扫码结果回调
     */
    abstract fun resultBack(content: String)

    /***
     * 图片文件扫码
     * 扫码失败返回空字符串，详见{ @link #parseBitmap}
     */
    protected open fun resultBackFile(content: String) {}

    /***
     * 启动相机后的操作
     */
    override fun onCameraOpenBack(camera: CameraView) {
        super.onCameraOpenBack(camera)
        clearFindViewByIdCache()
        LayoutInflater.from(context).inflate(provideFloorView(), this, true)
        cameraStartLaterConfig()
    }

    abstract fun provideFloorView(): Int

    /***
     * 配置扫码类型
     */
    private fun initScanType() {
        scanTypeConfig = getScanType()
    }

    /***
     * 初始化业务Handler
     */
    private fun initBusHandle() {
        val handlerThread = HandlerThread(System.currentTimeMillis().toString())
        handlerThread.start()
        busHandle = Handler(handlerThread.looper)
    }

    /***
     * File转Bitmap详见{@link #parseBitmap}
     */
    protected fun parseFile(filePath: String) {

        proscribeCamera()

        if (!checkPermissionRW())
            return

        val file = File(filePath)
        if (!file.exists())
            return

        if (busHandle == null)
            initBusHandle()

        busHandle?.post {
            val bitmap: Bitmap = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, getMediaUriFromPath(context, filePath)))
                        .copy(Bitmap.Config.RGB_565, false)
            } else
                BitmapFactory.decodeFile(filePath))
                    ?: return@post
            parseBitmap(bitmap)
        }
    }

    /***
     * 解析Bitmap
     * 解析过程中会关闭相机， 解析失败重新启动
     */
    protected fun parseBitmap(bitmap: Bitmap?) {

        proscribeCamera()

        if (bitmap == null)
            return
        if (busHandle == null)
            initBusHandle()
        busHandle?.post {
            bitmap.apply {
                if (config != Bitmap.Config.RGB_565
                        && config != Bitmap.Config.ARGB_8888) {
                    if (isMutable)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            config = Bitmap.Config.RGB_565
                        } else {
                            copy(Bitmap.Config.RGB_565, false)
                        }
                    else
                        copy(Bitmap.Config.RGB_565, false)
                }
                val source = BitmapLuminanceSource(this)
                var result = CustomMultiFormatReader.getInstance()
                        .decode(BinaryBitmap(GlobalHistogramBinarizer(source)))
                if (result == null)
                    result = CustomMultiFormatReader.getInstance()
                            .decode(BinaryBitmap(HybridBinarizer(source)))
                if (result != null) {
                    mainHand.post {
                        resultBackFile(result.text)
                        scanSucHelper()
                    }
                } else {
                    mainHand.post {
                        resultBackFile("")
                        unProscibeCamera()
                    }
                }
            }
        }
    }

    /***
     * path转Uri兼容Android10
     */
    private fun getMediaUriFromPath(context: Context, path: String): Uri {
        val mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = context.contentResolver.query(mediaUri,
                null,
                MediaStore.Images.Media.DISPLAY_NAME + "= ?", arrayOf(path.substring(path.lastIndexOf("/") + 1)),
                null)
        var uri: Uri? = null
        cursor?.let {
            it.moveToFirst()
            uri = ContentUris.withAppendedId(mediaUri,
                    it.getLong(it.getColumnIndex(MediaStore.Images.Media._ID)))
        }
        cursor?.close()
        return uri ?: Uri.EMPTY
    }


    /***
     * 提供扫码类型
     */
    open fun getScanType(): ScanTypeConfig {
        return ScanTypeConfig.HIGH_FREQUENCY
    }

    /***
     * 全局Handler
     */

    class HandleZX constructor(view: Callback) : Handler() {
        var hasResult = false
        var viewReference: WeakReference<Callback>? = null

        init {
            this@HandleZX.viewReference = WeakReference(view)
        }

        override fun handleMessage(msg: Message?) {
            if (hasResult)
                return
            msg?.apply {
                if (msg.what == SCAN_RESULT)
                    hasResult = true
                this@HandleZX.viewReference?.get()?.let {
                    it.handleMessage(msg)
                }
            }
        }

        fun init() {
            hasResult = false
        }
    }

}