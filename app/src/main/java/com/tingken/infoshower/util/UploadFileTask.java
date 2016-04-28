package com.tingken.infoshower.util;

import java.io.File;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

public class UploadFileTask extends AsyncTask<String, Void, Boolean> {
	public static final String requestURL="http://10.0.205.13:207/IAndroidPrintScreen.aspx";
  
   private  Activity context=null;
    public UploadFileTask(Activity ctx){
    	this.context=ctx;
    }
    @Override
	protected void onPostExecute(Boolean result) {
        if(result){
			// Toast.makeText(context, "上传成功!",Toast.LENGTH_LONG ).show();
        }else{
			Toast.makeText(context, "上传失败!", Toast.LENGTH_LONG).show();
        }
    }

	  @Override
	  protected void onPreExecute() {
	  }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

		@Override
	protected Boolean doInBackground(String... params) {
			File file=new File(params[0]);
			return UploadUtils.uploadFile( file, requestURL);
		}
	       @Override
	       protected void onProgressUpdate(Void... values) {
	       }

	
}