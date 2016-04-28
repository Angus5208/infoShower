package com.tingken.infoshower.util;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/29.
 */
public class EquipmentInfo {
    private List<String> items = null;//存放名称
    private List<String> paths = null;//存放路径

    //可用SD内存(KB)
    public String getAvailaleSize() {

        File path = Environment.getExternalStorageDirectory();//取得sdcard文件路径

        StatFs stat = new StatFs(path.getPath());


        long blockSize = stat.getBlockSize();


        long availableBlocks = stat.getAvailableBlocks();


        return String.valueOf((availableBlocks * blockSize) / 1024);

    }

    //总手机SD内存(KB)
    public String getAllSize() {

        File path = Environment.getExternalStorageDirectory();

        StatFs stat = new StatFs(path.getPath());


        long blockSize = stat.getBlockSize();


        long availableBlocks = stat.getBlockCount();


        return String.valueOf((availableBlocks * blockSize) / 1024);

    }

    //获取指定文件大小
    public long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    //获取指定文件夹下的所有文件名
    public List<String> getFileDir(String filePath) {
        items = new ArrayList<String>();
        paths = new ArrayList<String>();
        File f = new File(filePath);
        File[] files = f.listFiles();
        if (files != null) {
            int count = files.length;// 文件个数
            for (int i = 0; i < count; i++) {
                File file = files[i];
                items.add(file.getName());
                paths.add(file.getPath());
            }
        }
        return items;
    }

    //新建文件夹
    public void createFile(String LocalFile) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(LocalFile);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    //删除指定文件
    public boolean execCommand(String command) {
        File file = new File(command);
        if (file.delete()) {
            return true;
        } else {
            return false;
        }
    }

    //判断指定文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
