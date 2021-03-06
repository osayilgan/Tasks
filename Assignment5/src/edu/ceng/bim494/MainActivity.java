package edu.ceng.bim494;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing.PREVIEW_ROTATION_ANGLE;

public class MainActivity extends Activity implements Camera.PreviewCallback {
	
	// Global Variables Required
	Camera cameraObj;
	FrameLayout preview;
	FacialProcessing faceProc;
	
	// Array in which all the face data values will be returned for each face detected.
	FaceData[] faceArray = null;
	
	private CameraSurfacePreview mPreview;
	private int FRONT_CAMERA_INDEX = 1;
	private int BACK_CAMERA_INDEX = 0;
	
	// boolean clicked = false;
	boolean _qcSDKEnabled = false;
	
	// Boolean to check if the "pause" button is pressed or no.
	boolean cameraPause = false;
	
	// Boolean to check if the camera is switched to back camera or no.
	static boolean cameraSwitch = false;
	
	// Boolean to check if the phone orientation is in landscape mode or portrait mode.
	boolean landScapeMode = false;
	
	int cameraIndex; // Integer to keep track of which camera is open.
	int leftEyeBlink = 0;
	int rightEyeBlink = 0;
	
	int surfaceWidth = 0;
	int surfaceHeight = 0;
	
	Display display;
	int displayAngle;
	
	/* UI for blink information */
	private TextView avarageBlinkCount;
	
	/* Counter to keep the number of Blinks */
	private int totalBlinkCount;
	
	/* Count Up Timer to find the Average Blink Count */
	CountUpTimer timer;
	
	/* Date Format to format timer time */
	private SimpleDateFormat format = new SimpleDateFormat("mm:ss");
	
	private boolean isTimerStarted = false;
	private int previousFrameBlinkValue = 0;
	private RelativeLayout wall;
	
	/* Array to Hold the hex colors */
	private String[] colorArray = {"#ff4af224", "#ff07b8ff", "#ffeef435", "#ffff4444"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initializeUI();
		
		/* Initialize Timer */
		// TODO, create a dialog to ask if the user wants to start over or resume from previous one.
		timer = new CountUpTimer(this);
		
		// Create our Preview view and set it as the content of our activity.
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		
		// Check to see if the FacialProc feature is supported in the device or no.
		_qcSDKEnabled = FacialProcessing.isFeatureSupported(FacialProcessing.FEATURE_LIST.FEATURE_FACIAL_PROCESSING);
		
		if (_qcSDKEnabled && faceProc == null) {
			Log.e("TAG", "Feature is supported");
			faceProc = FacialProcessing.getInstance(); // Calling the Facial
														// Processing
														// Constructor.
		} else {
			Log.e("TAG", "Feature is NOT supported");
			return;
		}
		
		cameraIndex = Camera.getNumberOfCameras() - 1; // Start with front Camera
		
		try {
			cameraObj = Camera.open(cameraIndex); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			Log.d("TAG", "Camera Does Not exist");
		}
		
		// Change the sizes according to phone's compatibility.
		mPreview = new CameraSurfacePreview(MainActivity.this, cameraObj, faceProc);
		preview.removeView(mPreview);
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		cameraObj.setPreviewCallback(MainActivity.this);
		
		// Action listener for the Pause Button.
		pauseActionListener();
		
		// Action listener for the Switch Camera Button.
		cameraSwitchActionListener();
		
		display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}
	
	/**
	 * Initializes the UI elements
	 */
	private void initializeUI() {
		
		avarageBlinkCount = (TextView) findViewById(R.id.avarageBlinkCount);
		wall = (RelativeLayout) findViewById(R.id.mContainer);
	}
	
	/**
	 * Sets UI information for Right and Left Blink.
	 * And also for the total value of Blink Duration.
	 */
	private void setUIInformation(int mAvarageBlinkCount) {
		avarageBlinkCount.setText(mAvarageBlinkCount);
	}

	/*
	 * Function for switch camera action listener. Switches camera from front to
	 * back and vice versa.
	 */
	private void cameraSwitchActionListener() {
		
		ImageView switchButton = (ImageView) findViewById(R.id.switchCameraButton);
		
		switchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				// If the camera is facing front then do this
				if (!cameraSwitch) {
					
					stopCamera();
					cameraObj = Camera.open(BACK_CAMERA_INDEX);
					mPreview = new CameraSurfacePreview(MainActivity.this, cameraObj, faceProc);
					preview = (FrameLayout) findViewById(R.id.camera_preview);
					preview.addView(mPreview);
					cameraSwitch = true;
					cameraObj.setPreviewCallback(MainActivity.this);
					
					// If the camera is facing back then do this.
				} else {
					
					stopCamera();
					cameraObj = Camera.open(FRONT_CAMERA_INDEX);
					preview.removeView(mPreview);
					mPreview = new CameraSurfacePreview(MainActivity.this, cameraObj, faceProc);
					preview = (FrameLayout) findViewById(R.id.camera_preview);
					preview.addView(mPreview);
					cameraSwitch = false;
					cameraObj.setPreviewCallback(MainActivity.this);
				}
			}
		});
	}
	
	/*
	 * Function for pause button action listener to pause and resume the
	 * preview.
	 */
	private void pauseActionListener() {
		
		ImageView pause = (ImageView) findViewById(R.id.pauseButton);
		pause.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				if (!cameraPause) {
					cameraObj.stopPreview();
					cameraPause = true;
					
					timer.pause();
					
				} else {
					cameraObj.startPreview();
					cameraObj.setPreviewCallback(MainActivity.this);
					cameraPause = false;
					
					timer.resume();
				}
			}
		});
	}
	
	protected void onPause() {
		super.onPause();
		stopCamera();
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}
	
	protected void onResume() {
		super.onResume();
		
		if (cameraObj != null) {
			stopCamera();
		}
		
		if (!cameraSwitch)
			startCamera(FRONT_CAMERA_INDEX);
		else
			startCamera(BACK_CAMERA_INDEX);
	}
	
	/*
	 * This is a function to stop the camera preview. Release the appropriate
	 * objects for later use.
	 */
	public void stopCamera() {
		
		if (cameraObj != null) {
			cameraObj.stopPreview();
			cameraObj.setPreviewCallback(null);
			preview.removeView(mPreview);
			cameraObj.release();
			faceProc.release();
			faceProc = null;
		}
		
		cameraObj = null;
	}
	
	/*
	 * This is a function to start the camera preview. Call the appropriate
	 * constructors and objects.
	 * 
	 * @param-cameraIndex: Will specify which camera (front/back) to start.
	 */
	public void startCamera(int cameraIndex) {
		
		if (_qcSDKEnabled && faceProc == null) {
			
			Log.e("TAG", "Feature is supported");
			
			// Calling the Facial Processing Constructor.
			faceProc = FacialProcessing.getInstance();
		}
		
		try {
			cameraObj = Camera.open(cameraIndex); // attempt to get a Camera instance
		} catch (Exception e) {
			Log.d("TAG", "Camera Does Not exist"); // Camera is not available (in use or does not exist)
		}
		
		mPreview = new CameraSurfacePreview(MainActivity.this, cameraObj, faceProc);
		preview.removeView(mPreview);
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		cameraObj.setPreviewCallback(MainActivity.this);
	}

	/*
	 * Detecting the face according to the new Snapdragon SDK. Face detection
	 * will now take place in this function.
	 * 
	 * 1) Set the Frame 2) Detect the Number of faces. 3) If(numFaces > 0) then
	 * do the necessary processing.
	 */
	@Override
	public void onPreviewFrame(byte[] data, Camera arg1) {
		
		int dRotation = display.getRotation();
		PREVIEW_ROTATION_ANGLE angleEnum = PREVIEW_ROTATION_ANGLE.ROT_0;
		
		switch (dRotation) {
		case 0:
			displayAngle = 90;
			angleEnum = PREVIEW_ROTATION_ANGLE.ROT_90;
			break;
			
		case 1:
			displayAngle = 0;
			angleEnum = PREVIEW_ROTATION_ANGLE.ROT_0;
			break;
			
		case 2:
			// This case is never reached.
			break;
			
		case 3:
			displayAngle = 180;
			angleEnum = PREVIEW_ROTATION_ANGLE.ROT_180;
			break;
		}
		
		if (faceProc == null) {
			faceProc = FacialProcessing.getInstance();
		}
		
		Parameters params = cameraObj.getParameters();
		Size previewSize = params.getPreviewSize();
		surfaceWidth = mPreview.getWidth();
		surfaceHeight = mPreview.getHeight();
		
		// Landscape mode - front camera
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
				&& !cameraSwitch) {
			faceProc.setFrame(data, previewSize.width, previewSize.height,
					true, angleEnum);
			cameraObj.setDisplayOrientation(displayAngle);
			landScapeMode = true;
		}
		
		// landscape mode - back camera
		else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
				&& cameraSwitch) {
			faceProc.setFrame(data, previewSize.width, previewSize.height,
					false, angleEnum);
			cameraObj.setDisplayOrientation(displayAngle);
			landScapeMode = true;
		}
		
		// Portrait mode - front camera
		else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
				&& !cameraSwitch) {
			faceProc.setFrame(data, previewSize.width, previewSize.height,
					true, angleEnum);
			cameraObj.setDisplayOrientation(displayAngle);
			landScapeMode = false;
		}
		
		// Portrait mode - back camera
		else {
			faceProc.setFrame(data, previewSize.width, previewSize.height, false, angleEnum);
			cameraObj.setDisplayOrientation(displayAngle);
			landScapeMode = false;
		}
		
		/* Get number of detected faces */
		int numFaces = faceProc.getNumFaces();
		
		/* Clean-up with Garbage Collector */
		System.gc();
		
		/* No face detected */
		if (numFaces == 0) {
			
			Log.d("TAG", "No Face Detected");
			timer.pause();
			
		} else if(numFaces > 0) {
			
			Log.d("TAG", "Face Detected");
			faceArray = faceProc.getFaceData();
			
			if (faceArray == null) {
				
				Log.e("TAG", "Face array is null");
				timer.pause();
				
			} else if(faceArray.length > 0) {
				
				/* Start or Resume the Timer when a face is detected */
				if (isTimerStarted) {
					timer.resume();
				} else {
					timer.start();
				}
				
				faceProc.normalizeCoordinates(surfaceWidth, surfaceHeight);
				
				for (int j = 0; j < faceArray.length; j++) {
					leftEyeBlink = faceArray[j].getLeftEyeBlink();
					rightEyeBlink = faceArray[j].getRightEyeBlink();
				}
				
				/* Set UI information and color of the Screen */
				int averageCount = getAverageBlinkCount();
				setAverageText(averageCount);
				setBackgroundColor(averageCount);
				
				if ((leftEyeBlink > 50) && (rightEyeBlink > 50) && (leftEyeBlink < 100) && (rightEyeBlink < 100)) {
					
					if ((Math.max(leftEyeBlink, rightEyeBlink) - previousFrameBlinkValue) > 20) {
						
						totalBlinkCount++;
						previousFrameBlinkValue = Math.max(leftEyeBlink, rightEyeBlink);
					}
					
					Log.i("Okan", "Total Blink Count : " + totalBlinkCount);
					Log.i("Okan", "Left Eye Blink : " + leftEyeBlink);
					Log.i("Okan", "Right Eye Blink : " + rightEyeBlink);
					
				} else {
					previousFrameBlinkValue = 0;
				}
			}
		} else {
			timer.pause();
		}
	}
	
	public int getAverageBlinkCount() {
		
		long seconds = timer.getTime()/1000;
		
		if (seconds == 0) {
			return 0;
		}
		
		return (int) (60*(totalBlinkCount)/seconds);
	}
	
	public void setAverageText(int average) {
		avarageBlinkCount.setText(average + " p/m");
	}
	
	public void setBackgroundColor(int average) {
		
		String color = "";
		
		if (average > 22) {
			color = colorArray[0];
		} else if(average > 15) {
			color = colorArray[1];
		} else if(average > 8) {
			color = colorArray[2];
		} else {
			color = colorArray[3];
		}
		
		wall.setBackgroundColor(Color.parseColor(color));
	}
}
