package edu.ceng.bim494;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity implements CvCameraViewListener2, OnClickListener {
	
	private static final String TAG = "OCV Native Camera View";
	
	private CameraBridgeViewBase mOpenCvCameraView;
	
	private Mat 				inputFrame;
	private Button				mButton;
	private ImageView			captureImage;
	private PhotoViewAttacher	mAttacher;
	
	/**
	 * Enum to define different states of the Capture Button.
	 */
	private static enum CaptureButtonState {
		CAMERA_PREVIEW, IMAGE_PREVIEW
	}
	
	private CaptureButtonState buttonState = CaptureButtonState.CAMERA_PREVIEW;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				
				mOpenCvCameraView.enableView();
				mButton.setOnClickListener(MainActivity.this);
				
				/* Show Button and Set Text */
				mButton.setVisibility(View.VISIBLE);
				mButton.setText("Take Picture");
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};
	
	public MainActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}
	
	@Override
	public void onBackPressed() {
		
		/* If We are in Camera Preview, clicking on back button quits the application */
		if (buttonState == CaptureButtonState.CAMERA_PREVIEW) {
			
			super.onBackPressed();
		
		/* If we are in Image Preview state, then clicking on back button goes to Camera Preview */
		} else if (buttonState == CaptureButtonState.IMAGE_PREVIEW) {
			
			/* Go To OnClick mButton */
			onClick(null);
		}
	}
	
	@Override
	public void onClick(View v) {
		
		/* Check Button's State */
		if (buttonState == CaptureButtonState.CAMERA_PREVIEW) {
			
			/* Change Button's STATE to Image Preview */
			buttonState = CaptureButtonState.IMAGE_PREVIEW;
			
			/* Create Bitmap to pass to other Activity */
			Bitmap frame = Bitmap.createBitmap(inputFrame.width(), inputFrame.height(), Config.ARGB_8888);
			
			/* Convert Mat Object to Bitmap */
			Utils.matToBitmap(inputFrame, frame);
			
			mOpenCvCameraView.disableView();
			mOpenCvCameraView.setVisibility(View.GONE);
			
			/* Draw Mask Here */
			drawMaskOnBitmap(frame);
			
			/* Activate and Set Image Bitmap */
			captureImage.setVisibility(View.VISIBLE);
			captureImage.setImageBitmap(frame);
			
			/* Add Image View to Attacher */
			mAttacher = new PhotoViewAttacher(captureImage);
			
			/* Change Text */
			mButton.setText("Show Preview");
			
		} else {
			
			/* Change Button's STATE to Camera Preview */
			buttonState = CaptureButtonState.CAMERA_PREVIEW;
			
			/* Enable Camera View */
			mOpenCvCameraView.enableView();
			mOpenCvCameraView.setVisibility(View.VISIBLE);
			
			/* Deactivate Image View */
			captureImage.setVisibility(View.GONE);
			
			/* Change Text */
			mButton.setText("Take Picture");
		}
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.activity_main);
		
		mButton = (Button) findViewById(R.id.mButton);
		mButton.setVisibility(View.GONE);
		
		captureImage = (ImageView) findViewById(R.id.captureImage);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_native_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}
	
	/**
	 * Draws a pre-defined Mask onto Captured Image from Camera.
	 * 
	 * @param bitmap
	 */
	private void drawMaskOnBitmap(Bitmap bitmap) {
		
		/* Create Canvas with Bitmap */
		Canvas canvas = new Canvas(bitmap);
		
		/* Load Mask from Resources */
		Bitmap mask = BitmapFactory.decodeResource(this.getResources(), R.drawable.mask); 
		mask = Bitmap.createScaledBitmap(mask, 100, 100, true);
		
		Paint paint = new Paint();
		
		/* This Line creates a Frame around the Mask */
//		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawBitmap(mask, 200, 200, paint);
		
		/* We do not need the mask bitmap anymore. */
		mask.recycle();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}
	
	public void onDestroy() {
		super.onDestroy();
		
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}
	
	public void onCameraViewStarted(int width, int height) {}
	
	public void onCameraViewStopped() {}
	
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		this.inputFrame = inputFrame.rgba();
		return this.inputFrame;
	}
}
