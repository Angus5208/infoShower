package com.tingken.infoshower.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.tingken.infoshower.R;

/**
 * @author tingken.com
 * @date 2015-3-12
 */

public class UpgradeHelper {
	public static final int DOWNLOAD = 1;
	public static final int DOWNLOAD_FINISH = 2;
	private String mSavePath;
	private String apkFileName;
	private int progress;
	private boolean cancelUpdate = false;

	private Activity mParentActivity;
	private Handler upgradeHandler;
	private PopupWindow restartNotice;

	public UpgradeHelper(Activity parent, Handler upgradeHandler) {
		this.mParentActivity = parent;
		this.upgradeHandler = upgradeHandler;
	}

	public boolean checkUpdate(int serviceCode) {
		boolean neadUpgrade = isUpdate(serviceCode);
		if (neadUpgrade) {
			showNoticeDialog();
		}
		return neadUpgrade;
	}

	public boolean isUpdate(int serviceCode) {
		int versionCode = getVersionCode(mParentActivity);
		if (serviceCode > versionCode) {
			return true;
		}
		return false;
	}

	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo("com.tingken.infoshower", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}


	public static String getVersionName(Context context) {
		String versionName = null;
		try {
			versionName = context.getPackageManager().getPackageInfo("com.tingken.infoshower", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	private void showNoticeDialog() {
		if (restartNotice == null) {
			// create view and PopupWindow
			LayoutInflater inflater = LayoutInflater.from(mParentActivity);
			View view = inflater.inflate(R.layout.activity_upgrade_notice, null);
			restartNotice = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
			restartNotice.showAtLocation(inflater.inflate(R.layout.activity_main, null), Gravity.CENTER, 0, 0);
		}
	}


	public void downloadApk(String url) {
		// �������߳��������
		new downloadApkThread(url).start();
	}

	/**
	 * �����ļ��߳�
	 * 
	 * @author tingken.com
	 * @date 2015-3-12
	 */
	private class downloadApkThread extends Thread {
		String downloadUrl;

		public downloadApkThread(String url) {
			downloadUrl = url.trim();
		}

		@Override
		public void run() {
			try {
				// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					// ��ô洢����·��
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(downloadUrl);
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					// ����������
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// �ж��ļ�Ŀ¼�Ƿ����
					if (!file.exists()) {
						file.mkdir();
					}
					int nameIndex = downloadUrl.lastIndexOf('\\') > downloadUrl.lastIndexOf('/') ? downloadUrl
					        .lastIndexOf('\\') : downloadUrl.lastIndexOf('/');
					apkFileName = downloadUrl.substring(nameIndex);
					File apkFile = new File(mSavePath, apkFileName);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// ����
					byte buf[] = new byte[1024];
					// д�뵽�ļ���
					do {
						int numread = is.read(buf);
						count += numread;
						// ��������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½��
						upgradeHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// �������
							upgradeHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// ���ȡ���ֹͣ����.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// ȡ�����ضԻ�����ʾ
			// restartNotice.dismiss();
		}
	};

	/**
	 * ��װAPK�ļ�
	 */
	public void installApk() {
		File apkfile = new File(mSavePath, apkFileName);
		if (!apkfile.exists()) {
			return;
		}
		// ͨ��Intent��װAPK�ļ�
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mParentActivity.startActivity(i);
		mParentActivity.finish();
	}
}
