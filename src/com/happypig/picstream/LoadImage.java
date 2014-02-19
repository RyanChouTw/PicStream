package com.happypig.picstream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

public class LoadImage extends AsyncTask<Object, Void, Bitmap> {
	private Context mContext;
	private ImageView mImgView;
	private String mPath; // or ID
	
	public LoadImage(Context context) {
		mContext = context;
	}
	
	@Override
	protected Bitmap doInBackground(Object... params) {
		mImgView = (ImageView) params[0];
		mPath = mImgView.getTag().toString();

		int scaledBitmapWidth = mImgView.getMeasuredWidth() > 0 ? mImgView.getMeasuredWidth():mContext.getResources().getDisplayMetrics().widthPixels/CommonDef.NUM_COLUMN_IN_GRID;
		Uri imgUri = Uri.parse(mPath);
		
		Bitmap bmp = null;
		try {
	        BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
	        bitmapOption.inJustDecodeBounds = true;	       	        
        	bmp = Utils.getBitmap(mContext, imgUri, scaledBitmapWidth, scaledBitmapWidth);
		} catch (Exception e) {
			// nothing
		}
      
		return bmp;
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		int idx = mPath.lastIndexOf("/");		
		String key = idx >= 0 ? mPath.substring(idx + 1) : mPath;
		
		Utils.getInstance(mContext).addBitmapToCache(key, result);
		if (mImgView.getTag().equals(mPath)) {
			mImgView.setImageBitmap(result);
		}
	}

}
