package com.wishzixing.lib.able;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;

import com.wishzixing.lib.config.AutoFocusConfig;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.listener.AutoFocusCallback;
import com.wishzixing.lib.manager.CameraManager;
import com.wishzixing.lib.manager.PixsValuesCus;
import com.wishzixing.lib.manager.SensorManager;

/***
 *  Created by SWY
 *  DATE 2019/6/8
 *
 */
public class AutoFocusAble implements PixsValuesCus {

    private Handler timeHandler;

    private boolean isFrist = false;

    @Override
    public void cusAction(byte[] data, Camera camera) {

        if (CameraConfig.getInstance().getAutoFocusModel() == AutoFocusConfig.PIXVALUES) {
            setPixvaluesAutoFocus(data, camera);
        } else {
            if (!isFrist) {
                startAutoFocus();
                isFrist = true;
            }
        }
    }

    private void startAutoFocus() {
        if (CameraConfig.getInstance().getAutoFocusModel() == AutoFocusConfig.TIME)
            setTimeAutoFocus();
        if (CameraConfig.getInstance().getAutoFocusModel() == AutoFocusConfig.SENSOR)
            setSensorAutoFocus();
    }

    @Override
    public void stop() {
        timeHandler.removeCallbacksAndMessages(null);
    }

    private final long TIMEINTERVAL = 1500L;
    private int model = 0;

    private AutoFocusAble() {
        HandlerThread handlerThread = new HandlerThread("time");
        handlerThread.start();
        timeHandler = new Handler(handlerThread.getLooper());
    }

    private static class Holder {
        static AutoFocusAble INSTANCE = new AutoFocusAble();
    }

    public static AutoFocusAble getInstance() {
        return AutoFocusAble.Holder.INSTANCE;
    }

    private void setFocus() {
        if (CameraManager.get().getCamera() == null)
            return;

        Camera camera = CameraManager.get().getCamera();
        camera.startPreview();
        camera.autoFocus(AutoFocusCallback.getInstance());
    }

    private void setTimeAutoFocus() {
        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setFocus();
                timeHandler.postDelayed(this, TIMEINTERVAL);
            }
        }, TIMEINTERVAL);
    }

    private void setSensorAutoFocus() {

        SensorManager.getInstance().registerListener(new SensorManager.SensorChange() {
            @Override
            public void change() {
                setFocus();
            }
        })
                .startListener();


    }

    private void setPixvaluesAutoFocus(byte[] data, Camera camera) {

    }
}