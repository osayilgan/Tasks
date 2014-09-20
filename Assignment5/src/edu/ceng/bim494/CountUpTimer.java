package edu.ceng.bim494;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;

public class CountUpTimer {
	
	private Chronometer chronometer;
	private boolean isStarted;
	private boolean isPaused;
	
	/* Time when the counter is stopped */
	private long pauseTime = 0;
	
	public CountUpTimer(Activity activity) {
		
		chronometer = (Chronometer) activity.findViewById(R.id.avarageBlinkCounter);
		isStarted = false;
	}
	
	public static interface CountUpTickListener {
		void onTick(long time);
	}
	
	public void setTickListener(final CountUpTickListener listener) {
		
		chronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
			
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				if (listener != null) {
					Log.i("Okan", "Time : " + getTime());
					listener.onTick(getTime());
				}
			}
		});
	}
	
	/**
	 * Starts the Counter from given BaseTime (from Constructor).
	 */
	public void start() {
		
		if (!isStarted) {
			chronometer.setBase(SystemClock.elapsedRealtime() + pauseTime);
			chronometer.start();
			isStarted = true;
		}
	}
	
	public void pause() {
		
		if (isStarted) {
			chronometer.stop();
			pauseTime = chronometer.getBase() - SystemClock.elapsedRealtime();
			isStarted = false;
		}
	}
	
	public void resume() {
		chronometer.setBase(SystemClock.elapsedRealtime() + pauseTime);
		chronometer.start();
		isStarted = true;
	}
	
	public long getTime() {
		
		if (isStarted) {
			return (SystemClock.elapsedRealtime() - chronometer.getBase());
		} else {
			return 0;
		}
	}
	
	public boolean isStarted() {
		return isStarted;
	}
}
