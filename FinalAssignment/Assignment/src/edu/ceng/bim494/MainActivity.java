package edu.ceng.bim494;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity implements CvCameraViewListener2, OnClickListener, OnSeekBarChangeListener {
	
	private static final String TAG = "OCV Native Camera View";
	
	private CameraBridgeViewBase mOpenCvCameraView;
	
	private Mat 				inputFrame;
	private Button				mButton;
	
	/* constants */
	private static final int POLL_INTERVAL = 300;

	/** running state **/
	private boolean mRunning = false;
	private int mHitCount =0;

	/** config state **/
	private int mThreshold = 10;
	
	private Handler mHandler = new Handler();
	
	private SeekBar seekBar;

	/* data source */
	private SoundMeter mSensor;
	
	private Runnable mSleepTask = new Runnable() {
		public void run() {
			start();
		}
	};
	
	private Runnable mPollTask = new Runnable() {
		
		public void run() {
			
			double amp = mSensor.getAmplitude();
			
			Log.i("Okan", "Amplitude : " + amp);
			Log.i("Okan", "Hit Count : " + mHitCount);
			
			if (amp > mThreshold) {
				
				mHitCount++;
				
				if (mHitCount > 5) {
					takeAndSavePhoto();
					mHitCount = 0;
				}
			}
			
			/* Go to the Infinite loop with a Delay */
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);
		}
	};

	@Override
	public void onStop() {
		super.onStop();
		stop();
	}
	
	private void start() {
		mHitCount = 0;
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}
	
	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		mRunning = false;
	}
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				
				mOpenCvCameraView.enableView();
				mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
				
				mButton.setOnClickListener(MainActivity.this);
				
				/* Show Button and Set Text */
				mButton.setVisibility(View.VISIBLE);
				seekBar.setVisibility(View.VISIBLE);
				seekBar.setMax(12);
				seekBar.setOnSeekBarChangeListener(MainActivity.this);
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
	public void onClick(View v) {
		
		if (!mRunning) {
			mRunning = true;
			start();
			
			mButton.setText("Stop");
			
		} else {
			mRunning = false;
			stop();
			
			mButton.setText("Start");
		}
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mButton = (Button) findViewById(R.id.mButton);
		mButton.setVisibility(View.GONE);
		
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setVisibility(View.GONE);
		
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_native_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
		
		mSensor = new SoundMeter();
		mSensor.start();
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
	
	public void takeAndSavePhoto() {
		
		/* Create Bitmap to pass to other Activity */
		Bitmap frame = Bitmap.createBitmap(inputFrame.width(), inputFrame.height(), Config.ARGB_8888);
		
		/* Convert Mat Object to Bitmap */
		Utils.matToBitmap(inputFrame, frame);
		
		Toast.makeText(this, "Photo is Saving...", Toast.LENGTH_SHORT).show();
		
		File imageFileFolder = new File(Environment.getExternalStorageDirectory(), "WorldCup");
		
		imageFileFolder.mkdir();
		FileOutputStream out = null;
		
		Calendar c = Calendar.getInstance();
		String date = fromInt(c.get(Calendar.MONTH))
				+ fromInt(c.get(Calendar.DAY_OF_MONTH))
				+ fromInt(c.get(Calendar.YEAR))
				+ fromInt(c.get(Calendar.HOUR_OF_DAY))
				+ fromInt(c.get(Calendar.MINUTE))
				+ fromInt(c.get(Calendar.SECOND));
		
		File imageFileName = new File(imageFileFolder, date.toString() + ".jpg");
		
		boolean success = true;
		
		try {
			out = new FileOutputStream(imageFileName);
			frame.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			scanMedia(imageFileName.toString());
			out = null;
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		} finally {
			String text = success ? "Successfully saved" : "Error saving file" ;
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		}
	}
	
	public String fromInt(int val) {
		return String.valueOf(val);
	}
	
	private void scanMedia(String path) {
	    File file = new File(path);
	    Uri uri = Uri.fromFile(file);
	    Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
	    sendBroadcast(scanFileIntent);
	}
	
	public void scan(MediaScannerConnection msConn, String imageFileName) {
		msConn.scanFile(imageFileName, null);
	}
	
	public void disconnect(MediaScannerConnection msConn) {
		msConn.disconnect();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
		if (fromUser) {
			mThreshold = progress;
			Log.i("Okan", "Trhreshold : " + mThreshold);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// Do Nothing.
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// Do Nothing.
	}
}