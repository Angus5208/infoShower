package com.tingken.infoshower.view;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.tingken.infoshower.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;

import com.tingken.infoshower.core.LocalService;
import com.tingken.infoshower.core.LocalServiceFactory;
import com.tingken.infoshower.outside.AuthResult;
import com.tingken.infoshower.outside.ShowService;
import com.tingken.infoshower.outside.ShowServiceFactory;
import com.tingken.infoshower.util.EquipmentInfo;
import com.tingken.infoshower.util.SystemUiHider;
import com.tingken.infoshower.util.SystemUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class WelcomActivity extends Activity {

	private EquipmentInfo ei;
	private String saveAddress = Environment.getExternalStorageDirectory().getPath() + "/scze/video";
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	protected static final String TAG = "effort";

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	// private SystemUiHider mSystemUiHider;

	private LocalService localService;
	private ShowService showService = ShowServiceFactory.getSystemShowService();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// remove title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		LocalServiceFactory.init(this);

		ei = new EquipmentInfo();
		ei.createFile(saveAddress);

		localService = LocalServiceFactory.getSystemLocalService();
		if (localService.getShowServiceAddress() != null) {
			showService.init(localService.getShowServiceAddress());
		}

		setContentView(R.layout.activity_welcom);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		// final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		// mSystemUiHider = SystemUiHider.getInstance(this, contentView,
		// HIDER_FLAGS);
		// mSystemUiHider.setup();
		// mSystemUiHider.setOnVisibilityChangeListener(new
		// SystemUiHider.OnVisibilityChangeListener() {
		// // Cached values.
		// int mControlsHeight;
		// int mShortAnimTime;
		//
		// @Override
		// @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
		// public void onVisibilityChange(boolean visible) {
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
		// // If the ViewPropertyAnimator API is available
		// // (Honeycomb MR2 and later), use it to animate the
		// // in-layout UI controls at the bottom of the
		// // screen.
		// if (mControlsHeight == 0) {
		// mControlsHeight = controlsView.getHeight();
		// }
		// if (mShortAnimTime == 0) {
		// mShortAnimTime =
		// getResources().getInteger(android.R.integer.config_shortAnimTime);
		// }
		// controlsView.animate().translationY(visible ? 0 :
		// mControlsHeight).setDuration(mShortAnimTime);
		// } else {
		// // If the ViewPropertyAnimator APIs aren't
		// // available, simply show or hide the in-layout UI
		// // controls.
		// controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
		// }
		//
		// if (visible && AUTO_HIDE) {
		// // Schedule a hide().
		// delayedHide(AUTO_HIDE_DELAY_MILLIS);
		// }
		// }
		// });
		//
		// // Set up the user interaction to manually show or hide the system
		// UI.
		// contentView.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		// if (TOGGLE_ON_CLICK) {
		// mSystemUiHider.toggle();
		// } else {
		// mSystemUiHider.show();
		// }
		// }
		// });

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(1000);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Timer mLoginTimer;
	TimerTask mLoginTask = new TimerTask() {
		@Override
		public void run() {
			// test screen capture
			// Bitmap map =
			// ScreenCaptureHelper.takeScreenShot(WelcomActivity.this);
			// // File file = new File("sc.png");
			// // new FileOutputStream(file);
			// try {
			// ScreenCaptureHelper.savePic(map, openFileOutput("sc.png",
			// MODE_PRIVATE));
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// UploadUtils.uploadFile(WelcomActivity.this.getFileStreamPath("sc.png"),
			// "http://125.71.200.138:10001/ii/IPrintScreen.aspx?loginId=008,C07ADF6F-63A2-4153-B2D4-3C6F6392505B");
			// check login status
			if (localService.getAuthCode() != null && localService.getAuthCode().trim().length() > 0) {
				// try to login background
				AuthResult authResult = null;
				try {
					authResult = showService.authenticate(localService.getAuthCode(),
							SystemUtils.getDeviceId(WelcomActivity.this),
							SystemUtils.getResolution(WelcomActivity.this));
				} catch (IOException e) {
					// network exception, so go to main page if has a cached
					// page
					Log.e(TAG, "authenticate failed with a network problem", e);
					if (localService.getCachedServerAddress() != null) {
						// go to main page
						Intent intent = new Intent(WelcomActivity.this, MainActivity.class);
						intent.putExtra("content_page_address", localService.getCachedServerAddress());
						intent.putExtra("web_cache_setting", WebSettings.LOAD_CACHE_ONLY);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
						return;
					}
				} catch (Exception e) {
					Log.e(TAG, "authenticate failed", e);
				}
				if (authResult != null && authResult.isAuthSuccess()) {
					localService.saveLoginId(authResult.getLoginId());
					localService.saveCachedServerAddress(authResult.getShowPageAddress());
					// go to main page
					Intent intent = new Intent(WelcomActivity.this, MainActivity.class);
					intent.putExtra("content_page_address", authResult.getShowPageAddress());
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				} else {
					finish();
					// go to Login page
					Intent intent = new Intent(WelcomActivity.this, LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			} else {
				finish();
				// go to Login page
				Intent intent = new Intent(WelcomActivity.this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// intent.putExtra("content_page_address",
				// dataSource.getCachedServerAddress());
				startActivity(intent);
			}
			// mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to mLoginTask in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		if (mLoginTimer != null) {
			mLoginTimer.cancel();
		}
		mLoginTimer = new Timer();
		mLoginTimer.schedule(mLoginTask, delayMillis);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// handle the key from controller
		switch (keyCode) {
		case KeyEvent.KEYCODE_NUMPAD_0:
			//
			break;
		case KeyEvent.KEYCODE_BACK:
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			// open menu
			// Intent intent = new Intent(WelcomActivity.this,
			// RestartAlertActivity.class);
			// startActivity(intent);
		    // test dialog
//			UpgradeNoticeActivity dialog = new UpgradeNoticeActivity(this, R.style.MyDialog);
//			dialog.show();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
}
