package com.tingken.infoshower.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.tingken.infoshower.R;
import com.tingken.infoshower.R.layout;
import com.tingken.infoshower.core.LocalService;
import com.tingken.infoshower.core.LocalServiceFactory;
import com.tingken.infoshower.outside.ServerCommand;
import com.tingken.infoshower.outside.ShowService;
import com.tingken.infoshower.outside.ShowServiceFactory;
import com.tingken.infoshower.outside.VersionInfo;
import com.tingken.infoshower.util.EquipmentInfo;
import com.tingken.infoshower.util.MyProgressCallBack;
import com.tingken.infoshower.util.NetUtils;
import com.tingken.infoshower.util.SPUtils;
import com.tingken.infoshower.util.ScreenCaptureHelper;
import com.tingken.infoshower.util.SwitchMachineTask;
import com.tingken.infoshower.util.UpgradeHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(layout.activity_main)
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    //::::::::::::::::::::::::::::::::::::::::::::::::
    private String ss = "http://10.0.205.13:214/VideoDisplay.svc/DeviceWeb/Switch/";


    //::::::::::::::::::::::::::::::::::
    //指令播放名字
    private String PLAY_NAME;
    //指令下载地址
    private String DOWNLOAD_URL;
    //指令删除名字
    private String DELETE_NAME;
    //
    private String TaskId;
    //指令下载地址2
    private String DownloadUrl;
    /*::::::::::::::::::::::::::::::::::::::视屏相关::::::::::::::::::::::::::::::::::::::::::::::::::*/
    //本地视屏存放地址
    private String saveAddress = Environment.getExternalStorageDirectory().getPath() + "/scze/video";
    //信息返回接口
    final String urk = "http://192.168.188.16:208/DeviceCmd?action=taskstatus&loginId=";

    private EquipmentInfo ei = new EquipmentInfo();

    //视屏存放名字
    private String LOCALVIDEO="LOCALVIDEO";
    private List<String> videoLists = new ArrayList<String>();

    private WebView webContent;
    private Timer serverListener;
    private ShowService showService = ShowServiceFactory.getSystemShowService();
    private LocalService localService = LocalServiceFactory.getSystemLocalService();
    private boolean connectionNoticeOpened;
    private PopupWindow connectionFailedNotice;
    private PopupWindow popMenu;
    private StringBuffer commandBuffer = new StringBuffer();

    enum MenuFocus {
        NONE, CHANGE_REG_NUM, CHANGE_AUTO_START, EXIT
    }

    MenuFocus focus = MenuFocus.NONE;



    //视屏界面1
    private RelativeLayout videoLay1;

    private VideoView surface_view1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        //Vitamio.isInitialized(this);
        videoLay1 = (RelativeLayout) findViewById(R.id.videoLay1);
        surface_view1 = (VideoView) findViewById(R.id.surface_view1);


        webContent = (WebView) findViewById(R.id.webView1);
        webContent.setWebViewClient(new WebViewClient());
        webContent.getSettings().setSupportZoom(true);
        webContent.getSettings().setJavaScriptEnabled(true);
        webContent.getSettings().setBuiltInZoomControls(true);
        webContent.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                onKeyDown(keyCode, event);
                int disabledKey[] = {KeyEvent.KEYCODE_TAB, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN,
                        KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT};
                for (int key : disabledKey) {
                    if (key == keyCode) {
                        return true;
                    }
                }
                return false;
            }

        });

        String contentPageAddress = getIntent().getStringExtra("content_page_address");
//		webContent.getSettings().setDomStorageEnabled(true);
//		webContent.getSettings().setDatabaseEnabled(true);
//		String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
//		Log.i(TAG, "cacheDirPath=" + cacheDirPath);
//		webContent.getSettings().setDatabasePath(cacheDirPath);
//		webContent.getSettings().setAppCachePath(cacheDirPath);
        webContent.getSettings().setAppCacheEnabled(true);
        webContent.getSettings().setCacheMode(getIntent().getIntExtra("web_cache_setting", WebSettings.LOAD_DEFAULT));
//		HttpServiceWorker httpWorker = new HttpServiceWorker();
//		try {
//			httpWorker.executeGetStream(contentPageAddress);
//		} catch (Exception e) {
//			webContent.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
//		} finally {
//			httpWorker.close();
//		}
        if (contentPageAddress != null) {
            webContent.loadUrl(contentPageAddress);
        }
    }

//    @Event(value = {R.id.but, R.id.button, R.id.button2, R.id.button3, R.id.bo})
//    private void getEvent(View view) {
//        switch (view.getId()) {
//            //删除
//            case R.id.but:
//                Toast.makeText(this, "点击:::::" + ei.fileIsExists(saveAddress), Toast.LENGTH_SHORT).show();
////                if (ei.fileIsExists(urlTest)) {
////                    ei.execCommand(urlTest);
////                    Log.e("删除成功", "ttt");
////                } else {
////                    Log.e("删除失败", "ffff");
////                }
//                break;
//            //发送
//            case R.id.button:
//                PostConfiguration("http://192.168.188.16:208/DeviceCmd?loginId=" + localService.getLoginId());
//                //PostConfiguration("http://192.168.188.16:208/DeviceCmd?action=data&loginId=" + localService.getLoginId());
//                break;
//
//            //播放
//            case R.id.button3:
////                int a1 = url1.lastIndexOf("/");
////                String s1 = url1.substring(a1 + 1);
////                if (ei.fileIsExists(saveAddress + "/" + s1)) {
////                    //videoLay.setVisibility(View.INVISIBLE);
////                    Toast.makeText(MainActivity.this, s1 + "存在", Toast.LENGTH_LONG).show();
////                } else {
////                    Toast.makeText(MainActivity.this, s1 + "不存在", Toast.LENGTH_LONG).show();
////                }
//                Toast.makeText(MainActivity.this, "播放", Toast.LENGTH_SHORT).show();
//                videoLay1.setVisibility(View.VISIBLE);
//                // playfunction("b.mp4");
//                break;
//
//            //下载
//            case R.id.button2:
////                int a = url1.lastIndexOf("/");
////                Log.e("操作字符串", a + ".....");
////                String s = url1.substring(a + 1);
////                Log.e("操作字符串", s + "::::");
//
//                // DownLoadProgress(url1, saveAddress + "/" + s);
////                videoLay1.setVisibility(View.VISIBLE);
////                mVideo();
//                break;
//            case R.id.bo:
//                Toast.makeText(this, "按了", Toast.LENGTH_SHORT).show();
//                DELETE_NAME = saveAddress + "/" + PLAY_NAME;
//                Message msg = OrderHandle.obtainMessage();
//                msg.what = 2;
//                msg.sendToTarget();
//                break;
//        }
//    }


    private class HeartBeatTask extends TimerTask {

        @Override
        public void run() {
            SwitchMachineTask smt = new SwitchMachineTask();
            smt.getReturn();
            PostConfiguration("http://192.168.188.16:208/DeviceCmd?loginId=" + localService.getLoginId());
            // Get Server Command
            // If connection failed, popup connection notice
            ServerCommand command = showService.heartBeat(localService.getLoginId());

            boolean networkAccessable = true;
            switch (command) {
                case SCREEN_CAPTURE:
                    // capture
                    showServiceHandler.sendEmptyMessage(0);
                    if (serverListener != null) {
                        serverListener.schedule(new HeartBeatTask(), 2000);
                    }
                    break;
                case RESTART:
                    // notice restart and prepare to do
                    showServiceHandler.sendEmptyMessage(4);
                    break;
                case CONNECTION_FAILED:
                    // notice connection failed and save status
                    networkAccessable = false;
                    showServiceHandler.sendEmptyMessage(2);
                    // serverListener.schedule(new HeartBeatTask(), 2000);
                    break;
                case UPGRADE:
                    VersionInfo version = showService.getLatestVersion(localService.getLoginId());
                    if (version != null && upgradeHelper.isUpdate(version.getVersionCode())) {
                        Message upgradeMsg = new Message();
                        upgradeMsg.what = 1;
                        upgradeMsg.obj = version;
                        showServiceHandler.sendMessage(upgradeMsg);
                    }
                    break;
                default:
                    if (serverListener != null) {
                        serverListener.schedule(new HeartBeatTask(), 2000);
                    }
                    break;
            }
            if (networkAccessable && connectionNoticeOpened) {
                // close
                showServiceHandler.sendEmptyMessage(3);
            }
        }

    }

    @Override
    protected void onStart() {
        // start timer to monitor server commands
        if (serverListener == null) {
            serverListener = new Timer(true);
            // serverListener.schedule(heartBeatTask, 5000, 1 * 60 * 1000);
            // if schedule with a period, may be call by several threads on the
            // same time, that means need to re-start
            // timer after execute
            serverListener.schedule(new HeartBeatTask(), 5000);
        }
        if (SPUtils.get(MainActivity.this,LOCALVIDEO,"").toString().length()>1) {
            if (!surface_view1.isPlaying()) {
                videoLay1.setVisibility(View.VISIBLE);
                surface_view1.setVisibility(View.VISIBLE);
                playfunction(SPUtils.get(MainActivity.this,LOCALVIDEO,"").toString(), TaskId);
            }else {
                Log.e("执行播放","执行本地播放");
            }
        }else {
            Log.e("没有本地缓存","没有本地缓存");
        }

        super.onStart();
    }

    private Handler upgradeHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UpgradeHelper.DOWNLOAD:
                    break;
                case UpgradeHelper.DOWNLOAD_FINISH:
                    upgradeHelper.installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private UpgradeHelper upgradeHelper = new UpgradeHelper(this, upgradeHandler);

    static int connectionFailedTime = 0;
    private Handler showServiceHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Intent intent;
            switch (msg.what) {
                case 0:
                    // screen capture
                    Bitmap map = ScreenCaptureHelper.takeScreenShot(MainActivity.this);
                    // File file = new File("sc.png");
                    // new FileOutputStream(file);
                    try {
                        ScreenCaptureHelper.savePic(map, openFileOutput("sc.png", MODE_PRIVATE));
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (serverListener != null) {
                        serverListener.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                showService.uploadScreen(localService.getLoginId(), new Date(),
                                        MainActivity.this.getFileStreamPath("sc.png"));
                            }

                        }, 0);
                    }
                    break;
                case 1:
                    // upgrade
                    VersionInfo version = (VersionInfo) msg.obj;
                    if (version != null) {
                        showUpgradeNotice(version.getVersionName());
                        upgradeHelper.downloadApk(version.getDownloadAddress());
                    }
                    break;
                case 2:
                    // open connection failed notice
                    if (++connectionFailedTime > 0) {
                        openConnectionNote();
                    }
                    break;
                case 3:
                    // close connection failed notice
                    connectionFailedTime = 0;
                    closeConnectionNote();
                    break;
                case 4:
                    // restart
                    openRestartNotice();
                    if (serverListener != null) {
                        serverListener.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                restartApp();
                            }

                        }, 5000);
                    }
                    break;
            }
            super.handleMessage(msg);
        }

    };
    private PopupWindow popupNotice;
    private TextView networkDisconnectAlertText;
    private Handler changeNetworkAlertTextHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what < 1) {
                closeConnectionNote();
                if (serverListener != null) {
                    serverListener.schedule(new HeartBeatTask(), 2000);
                }
            } else {
                networkDisconnectAlertText.setText(String.format(
                        getResources().getString(R.string.network_disconnect_alert), msg.what));
            }
            super.handleMessage(msg);
        }
    };

    protected void openConnectionNote() {
        if (!connectionNoticeOpened) {
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            Point outSize = new Point();
            display.getSize(outSize);
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View view = inflater.inflate(R.layout.connection_failed_notice, null);
            connectionFailedNotice = new PopupWindow(view, outSize.x, outSize.y, false);
            networkDisconnectAlertText = (TextView) view.findViewById(R.id.network_disconnect_alert);
            final int originalSecondToConnect = 10;
            networkDisconnectAlertText.setText(String.format(getResources()
                            .getString(R.string.network_disconnect_alert),
                    originalSecondToConnect));
            if (serverListener != null) {
                serverListener.schedule(new TimerTask() {
                    int iSecondToConnect = originalSecondToConnect;

                    @Override
                    public void run() {
                        changeNetworkAlertTextHandler.sendEmptyMessage(--iSecondToConnect);
                        if (iSecondToConnect < 1) {
                            cancel();
                        }
                    }
                }, 1000, 1000);
            }
            connectionFailedNotice.showAtLocation(inflater.inflate(R.layout.activity_main, null), Gravity.BOTTOM, 0, 0);

            connectionNoticeOpened = true;
        }
    }

    protected void closeConnectionNote() {
        if (connectionFailedNotice != null) {
            connectionFailedNotice.dismiss();
            connectionFailedNotice = null;
            connectionNoticeOpened = false;
        }
    }

    @Override
    protected void onStop() {
        // end timer to stop monitoring
        if (serverListener != null) {
            serverListener.cancel();
            serverListener = null;
        }
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // handle the key from controller
        if (KeyEvent.ACTION_UP == event.getAction() && 0 == event.getRepeatCount()) {
            if (popMenu == null) {
                if (KeyEvent.KEYCODE_0 <= keyCode && KeyEvent.KEYCODE_9 >= keyCode) {
                    commandBuffer.append(keyCode - KeyEvent.KEYCODE_0);
                } else if (KeyEvent.KEYCODE_NUMPAD_0 <= keyCode && KeyEvent.KEYCODE_NUMPAD_9 >= keyCode) {
                    commandBuffer.append(keyCode - KeyEvent.KEYCODE_NUMPAD_0);
                }
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_TAB:
                    if (popMenu == null) {
                        if (commandBuffer.length() > 0) {
                            if (LocalService.SET_SERVER_ADDRESS_COMMAND.equals(commandBuffer.toString())) {
                                // open dialog to configure
                                openServerAddressConfig();
                            }
                            commandBuffer = new StringBuffer();
                        }
                    }
                    return true;
                case KeyEvent.KEYCODE_DEL:
                    commandBuffer = new StringBuffer();
                    break;
                case KeyEvent.KEYCODE_NUMPAD_0:
                    openConnectionNote();
                    try {
                        ScreenCaptureHelper.savePic(ScreenCaptureHelper.takeScreenShot(MainActivity.this),
                                openFileOutput("sc.png", MODE_PRIVATE));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //
                    // ScreenCaptureHelper
                    // .savePic(ScreenCaptureHelper.takeScreenShot(MainActivity.this),
                    // "sdcard/Download/sc.png");
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    // close menu if it's open
                    if (popMenu != null) {
                        closeMenu();
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    // execute if menu is open
                    if (popMenu != null) {
                        return true;
                    } else {
                        // open menu
                        openMenu();
                        // showServiceHandler.sendEmptyMessage(2);
                        // openConnectionNote();
                        return true;
                    }
                case KeyEvent.KEYCODE_DPAD_UP:
                    // move focus if menu is open
                    // if (popMenu != null) {
                    // switch (focus) {
                    // case CHANGE_REG_NUM:
                    // break;
                    // case CHANGE_AUTO_START:
                    // focus = MenuFocus.CHANGE_REG_NUM;
                    // break;
                    // case EXIT:
                    // focus = MenuFocus.CHANGE_AUTO_START;
                    // break;
                    // case NONE:
                    // break;
                    // }
                    // moveMenuFocus(focus);
                    // return true;
                    // }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    // move focus if menu is open
                    // if (popMenu != null) {
                    // switch (focus) {
                    // case CHANGE_REG_NUM:
                    // focus = MenuFocus.CHANGE_AUTO_START;
                    // break;
                    // case CHANGE_AUTO_START:
                    // focus = MenuFocus.EXIT;
                    // break;
                    // case EXIT:
                    // break;
                    // case NONE:
                    // break;
                    // }
                    // moveMenuFocus(focus);
                    // return true;
                    // }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    // move focus if menu is open
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    // move focus if menu is open
                    break;
                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void openMenu() {

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View view = inflater.inflate(R.layout.menu_setting, null);
        popMenu = new PopupWindow(view, 1920, 1080, false);
        popMenu.setBackgroundDrawable(new BitmapDrawable());
        popMenu.setOutsideTouchable(true);
        popMenu.setFocusable(true);
        Button btnChangeRegnum = (Button) view.findViewById(R.id.btnChangeRegnum);
        btnChangeRegnum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // open edit dialog
                view.findViewById(R.id.child_menu_change_reg_num).setVisibility(View.VISIBLE);
                view.findViewById(R.id.child_menu_change_auto_start).setVisibility(View.INVISIBLE);
            }
        });
        Button btnApplyNewRegnum = (Button) view.findViewById(R.id.btn_apply_change);
        final EditText editRegnum = (EditText) view.findViewById(R.id.edit_reg_num);
        editRegnum.setText(localService.getAuthCode());
        btnApplyNewRegnum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // apply new register number
                localService.saveAuthCode(editRegnum.getText().toString());
            }
        });
        Button btnChangeAutoStart = (Button) view.findViewById(R.id.btnChangeAutoStart);
        btnChangeAutoStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // open choice dialog
                view.findViewById(R.id.child_menu_change_reg_num).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.child_menu_change_auto_start).setVisibility(View.VISIBLE);
            }
        });
        Button btnSwitchAutoStart = (Button) view.findViewById(R.id.btn_switch_auto_start);
        boolean autoStart = localService.isAutoStart();
        if (!autoStart) {
            btnSwitchAutoStart.setBackgroundResource(R.drawable.off_btn_selector);
        }
        btnSwitchAutoStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // switch auto start configuration
                boolean autoStart = !localService.isAutoStart();
                localService.setAutoStart(autoStart);
                if (autoStart) {
                    v.setBackgroundResource(R.drawable.on_btn_selector);
                } else {
                    v.setBackgroundResource(R.drawable.off_btn_selector);
                }
            }
        });
        Button btnExit = (Button) view.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // open confirm exit dialog
                openExitAlert();
            }
        });
        popMenu.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                popMenu = null;
            }
        });
        view.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.ACTION_UP == event.getAction() && 0 == event.getRepeatCount()) {
                    if (KeyEvent.KEYCODE_BACK == keyCode) {
                        // close menu if it's open
                        if (popMenu != null) {
                            closeMenu();
                            return true;
                        }
                    }
                }
                return false;
            }

        });
        popMenu.showAtLocation(inflater.inflate(R.layout.activity_main, null), Gravity.NO_GRAVITY, 170, 60);
        btnChangeRegnum.requestFocus();
        // focus = MenuFocus.CHANGE_AUTO_START;
        // moveMenuFocus(MenuFocus.CHANGE_AUTO_START);
    }

    private void closeMenu() {
        //
        if (popMenu != null) {
            popMenu.dismiss();
            popMenu = null;
        }
    }

    private void restartApp() {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void openRestartNotice() {
        if (popupNotice == null) {
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            Point outSize = new Point();
            display.getSize(outSize);
            // create view and PopupWindow
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View view = inflater.inflate(R.layout.activity_restart_alert, null);
            popupNotice = new PopupWindow(view, outSize.x, outSize.y, false);
            popupNotice.showAtLocation(inflater.inflate(R.layout.activity_main, null), Gravity.CENTER, 0, 0);
        }
    }

    private void closeRestartNotice() {
        if (popupNotice != null) {
            popupNotice.dismiss();
            popupNotice = null;
        }
    }

    private void openExitAlert() {
        if (popupNotice != null) {
            popupNotice.dismiss();
            popupNotice = null;
        }
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        // create view and PopupWindow
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.exit_alert_dialog, null);
        final PopupWindow exitAlert = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        Button btnExit = (Button) view.findViewById(R.id.exit);
        btnExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // exit
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                System.exit(0);
            }
        });
        Button btnCancel = (Button) view.findViewById(R.id.cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // exit dialog
                exitAlert.dismiss();
            }
        });
        exitAlert.showAtLocation(inflater.inflate(R.layout.activity_main, null), Gravity.CENTER, 0, 0);
    }

    private void openServerAddressConfig() {
        // create view and PopupWindow
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View view = inflater.inflate(R.layout.config_server_address, null);
        final PopupWindow configServerAddressDialog = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, true);

        Button btnConfig = (Button) view.findViewById(R.id.btn_config);
        final EditText editServerAddress = (EditText) view.findViewById(R.id.edit_server_address);
        String serviceAddress = localService.getShowServiceAddress();
        editServerAddress.setText(serviceAddress != null ? serviceAddress : ShowService.DEFAULT_SERVER_ADDRESS);
        btnConfig.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // configure server address
                localService.saveShowServiceAddress(editServerAddress.getText().toString());
                configServerAddressDialog.dismiss();
            }
        });

        Button btnCancel = (Button) view.findViewById(R.id.cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // exit dialog
                configServerAddressDialog.dismiss();
            }
        });
        configServerAddressDialog.showAtLocation(inflater.inflate(R.layout.activity_main, null), Gravity.CENTER, 0, 0);
    }

    /**
     * show upgrade dialog
     */
    private void showUpgradeNotice(String newVersion) {
        if (popupNotice != null) {
            popupNotice.dismiss();
            popupNotice = null;
        }
        // create view and PopupWindow
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.activity_upgrade_notice, null);
        popupNotice = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
        TextView currentVersion = (TextView) view.findViewById(R.id.current_version_info);
        currentVersion.setText(String.format(getResources().getString(R.string.current_version_info),
                UpgradeHelper.getVersionName(this)));
        TextView newVersionAlert = (TextView) view.findViewById(R.id.alert_new_version);
        newVersionAlert.setText(String.format(getResources().getString(R.string.alert_new_version), newVersion));
        popupNotice.showAtLocation(inflater.inflate(R.layout.activity_main, null), Gravity.CENTER, 0, 0);
    }

    //下载视频
    private void DownLoadProgress(final String downUrl, final String localUrl, final String TaskId) {
        NetUtils.DownLoadFile(downUrl, localUrl, new MyProgressCallBack<File>() {

            @Override
            public void onStarted() {
                super.onStarted();
                if (TaskId != null && TaskId.length() != 0) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("EventType", "videofiledown");
                        object.put("flieName", downUrl);
                        object.put("TaskId", TaskId);
                        object.put("Progress", "start");
                        postMsg(urk + TaskId, object);
                        Log.e("开始下载", object + "");
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onSuccess(File result) {
                super.onSuccess(result);
                Log.e("下载完成", "完成");
                videoLists.add(localUrl);
                if (TaskId != null && TaskId.length() != 0) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("EventType", "videofiledown");
                        object.put("TaskId", TaskId);
                        object.put("Progress", "end");
                        postMsg(urk + TaskId, object);
                        Log.e("下载完成", object + "");
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                super.onLoading(total, current, isDownloading);
                Log.e("下载中", total + ":::::" + current);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
            }
        });
    }

    //发送设备信息，获取返回指令
    private void PostConfiguration(String URL) {
        List<String> names = ei.getFileDir(saveAddress + "/");
        JSONObject jsonData = new JSONObject();
        JSONObject jsonHead = new JSONObject();

        try {
            String dateValue = DateFormat.format("yyyy-MM-dd",
                    System.currentTimeMillis()).toString();
            String timeValue = DateFormat.format("kk:mm",
                    System.currentTimeMillis()).toString();
            String time = "请求时间:" + dateValue + "/" + timeValue;

            JSONArray array = new JSONArray();

            for (String name : names) {
                JSONObject jsonName = new JSONObject();
                jsonName.put("VideoName", name);
                array.put(jsonName);
            }
            if (surface_view1.isPlaying()) {
                if (SPUtils.contains(MainActivity.this, LOCALVIDEO)) {
                    jsonData.put("PlayName", PLAY_NAME);
                } else {
                    Log.e("SP错误", "没有");
                }
            } else {
                Log.e("没有播放", "没有播放");
            }
            jsonData.put("AvailaleSize", ei.getAvailaleSize());
            jsonData.put("AllSize", ei.getAllSize());
            if (array.length() > 0) {
                jsonData.put("VideoNames", array);
            }

            jsonHead.put("transferTime", time);
            jsonHead.put("Data", jsonData);
            Log.e("测试", jsonHead + "::::::::::");
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(URL);
        params.addBodyParameter("MSG", jsonHead.toString());
        Callback.Cancelable callable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                MyJson(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("失败", ex + "");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //解析返回信息
    private void MyJson(String result) {

        Message msg = OrderHandle.obtainMessage();
        Log.e("指令返回信息", result);
        try {
            JSONObject objects = new JSONObject(result);
            boolean IsExistsTask = objects.getBoolean("IsExistsTask");
            TaskId = objects.getString("TaskId");
            if (IsExistsTask) {
                JSONObject object = new JSONObject(objects.getString("TaskContent"));
                if (object != null && object.length() != 0) {
                    String Cmd = object.getString("Cmd");

                    //下载
                    if (Cmd.equals("filedownload")) {
                        DownloadUrl = object.getString("DownloadUrl");
                        if (DownloadUrl != null && DownloadUrl.length() != 0) {
                            msg.what = 1;
                            msg.sendToTarget();
                        }
                        //删除
                    } else if (Cmd.equals("filedel")) {
                        DELETE_NAME = object.getString("FileName");
                        if (DELETE_NAME != null && DELETE_NAME.length() != 0) {
                            msg.what = 2;
                            msg.sendToTarget();
                        }
                        Log.e("删除", "::::");
                        //播放
                    } else if (Cmd.equals("play")) {
                        PLAY_NAME = object.getString("FileName");
                        msg.what = 0;
                        msg.sendToTarget();
                        Log.e("播放地址", "：：：" + PLAY_NAME + "::::::" + SPUtils.contains(this, LOCALVIDEO));
                    } else {
                        Log.e("无任务", "::::");
                    }
                }

            } else {
                Log.e("没有视频相关任务", "::::");
            }
        } catch (Exception e) {

        }
    }

    //发送信息
    private void postMsg(String postUrl, JSONObject object) {
        RequestParams params = new RequestParams(postUrl);
        params.setConnectTimeout(8 * 1000);
        params.addBodyParameter("MSG", object.toString());
        Callback.Cancelable callable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("成功", result + ":::::::::" + localService.getLoginId());
                Log.e("id", localService.getLoginId());

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("失败", ex + "");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //视频播放器
    void playfunction(String URL, final String TaskId) {

        if (surface_view1.isPlaying()) {
            surface_view1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.stop();
                    mp.release();
                }
            });
        }
        surface_view1.setVideoPath(saveAddress + "/" + URL);
        surface_view1.setMediaController(new MediaController(this));
        surface_view1.requestFocus();
        surface_view1.start();

        try {
            JSONObject returnObject = new JSONObject();
            returnObject.put("EventType", "play");
            returnObject.put("TaskId", TaskId);
            postMsg(urk + TaskId, returnObject);
            Log.e("开始播放返回", returnObject.toString());
        } catch (Exception e) {
            Log.e("开始播放返回错误", "播放返回错误");
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        surface_view1.setLayoutParams(layoutParams);

        surface_view1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                surface_view1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        try {
//                            surface_view1.setVideoPath(saveAddress + "/" + "a.mp4");
//                            surface_view1.start();
                            mp.setLooping(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("播放错误", "播放错误");
                        }
                    }
                });
            }
        });

    }

    private Handler OrderHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //播放执行
                case 0:
                    if (ei.fileIsExists(saveAddress + "/" + PLAY_NAME)) {
                        if (!SPUtils.contains(MainActivity.this, LOCALVIDEO)) {
                            try {
                                SPUtils.clear(MainActivity.this);
                                SPUtils.put(MainActivity.this, LOCALVIDEO, PLAY_NAME);
                                videoLay1.setVisibility(View.VISIBLE);
                                surface_view1.setVisibility(View.VISIBLE);
                                playfunction(PLAY_NAME, TaskId);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("错误", e + ":::");
                            }
                        } else {
                            Log.e("已经在播放该视频", ":::");
//                            videoLay.setVisibility(View.VISIBLE);
//                            playfunction(PLAY_NAME, TaskId);
                            if (!surface_view1.isPlaying()) {
                                videoLay1.setVisibility(View.VISIBLE);
                                playfunction(PLAY_NAME, TaskId);
                            } else {
                                Log.e("视屏在播放", "ssss");
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "需要播放的视频不存在，请在后台下载", Toast.LENGTH_LONG).show();
                    }
                    break;
                //下载执行
                case 1:
                    int a = DownloadUrl.lastIndexOf("/");
                    String s = DownloadUrl.substring(a + 1);

                    if (ei.fileIsExists(saveAddress + "/" + s)) {
                        if (TaskId != null && TaskId.length() != 0) {
                            try {
                                JSONObject returnObject = new JSONObject();
                                returnObject.put("EventType", "videofiledown");
                                returnObject.put("TaskId", TaskId);
                                returnObject.put("Progress", "end");
                                postMsg(urk + TaskId, returnObject);
                                Log.e("已存在", "视频已经存在::::");

                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    } else {
                        DownLoadProgress(DownloadUrl, saveAddress + "/" + s, TaskId);
                    }
                    break;
                //删除执行
                case 2:
                    if (ei.fileIsExists(saveAddress + "/" + DELETE_NAME)) {

                        //判断删除视频是否正在播放
                        if (SPUtils.get(MainActivity.this, LOCALVIDEO,"").toString() == DELETE_NAME) {
                            SPUtils.clear(MainActivity.this);
                            if (surface_view1.isPlaying()) {
                                surface_view1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.stop();
                                        mp.release();
                                        //Log.e("停止播放", "停止播放");
                                    }
                                });
                            }
                            videoLay1.setVisibility(View.GONE);
                            surface_view1.setVisibility(View.GONE);
                        }

                        if (ei.execCommand(saveAddress + "/" + DELETE_NAME)) {
                            try {
                                JSONObject returnObject = new JSONObject();
                                returnObject.put("EventType", "filedel");
                                returnObject.put("TaskId", TaskId);
                                returnObject.put("Progress", "end");
                                postMsg(urk + TaskId, returnObject);
                                if (SPUtils.get(MainActivity.this, LOCALVIDEO,"").toString() == DELETE_NAME) {
                                    SPUtils.clear(MainActivity.this);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        } else {
                            //删除失败
                            try {
                                Log.e("删除失败", "删除失败");
                                JSONObject returnObject = new JSONObject();
                                returnObject.put("EventType", "filedel");
                                returnObject.put("TaskId", TaskId);
                                returnObject.put("Progress", "end");
                                returnObject.put("Errormess", "删除失败");
                                postMsg(urk + TaskId, returnObject);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    } else {
                        //视频不存在
                        try {
                            Log.e("删除失败，不存在", "删除失败，不存在");
                            JSONObject returnObject = new JSONObject();
                            returnObject.put("EventType", "filedel");
                            returnObject.put("TaskId", TaskId);
                            returnObject.put("Progress", "end");
                            returnObject.put("Errormess", "视频不存在");
                            postMsg(urk + TaskId, returnObject);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    break;
            }
        }
    };
}
