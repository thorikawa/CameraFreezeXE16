package com.example.camerafreezesample;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MainActivity extends Activity {

    protected static final String TAG = "CameraFreezeSample";
    private SurfaceHolder mCameraPreviewSurfaceHolder;
    private Camera mCamera;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        SurfaceView cameraPreview = (SurfaceView) findViewById(R.id.surfaceView);
        mCameraPreviewSurfaceHolder = cameraPreview.getHolder();
        mCameraPreviewSurfaceHolder.addCallback(mCameraPreviewSurfaceCallback);

        mHandler = new Handler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void startCamera() {
        Log.d(TAG, "start camera");
        mCamera = Camera.open();
        try {
            mCamera.setPreviewDisplay(mCameraPreviewSurfaceHolder);
        } catch (IOException e) {
            Log.e(TAG, "setPreviewDisplay error", e);
        }
        mCamera.startPreview();
        takePicture();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            Log.d(TAG, "release camera");
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void takePicture() {
        Log.d(TAG, "call takePicture after 500ms");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCamera != null) {
                    mCamera.takePicture(null, null, mJpgCallback);
                }
            }
        }, 500);
    }

    private PictureCallback mJpgCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            Log.d(TAG, "picture taken");
            if (mCamera != null) {
                mCamera.startPreview();
                takePicture();
            }
        }
    };

    private Callback mCameraPreviewSurfaceCallback = new Callback() {
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "preview surface destroyed");
            releaseCamera();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "preview surface created");
            startCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, final int width, final int height) {
            Log.d(TAG, "preview surface changed");
        }
    };

}
