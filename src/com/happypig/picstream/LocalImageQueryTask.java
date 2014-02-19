package com.happypig.picstream;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by ryanchou on 2013/6/21.
 */
public class LocalImageQueryTask extends AsyncTask<Void, Integer, String> {
    Context mContext;
    ArrayList<ImageInfo> mImagePoll;
    PictureGalleryAdapter mAdapter;

    public LocalImageQueryTask(Context context, ArrayList<ImageInfo> imgPoll, PictureGalleryAdapter adapter) {
        this.mContext = context;
        this.mImagePoll = imgPoll;
        this.mAdapter = adapter;
    }

    @Override
    protected String doInBackground(Void... voids) {
        Cursor imgCursor;
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN
        };

        imgCursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC");

        if (imgCursor != null && imgCursor.getCount() > 0) {
            imgCursor.moveToFirst();
            while (imgCursor.moveToNext()) {
                int imgId = imgCursor.getInt(imgCursor.getColumnIndex(MediaStore.Images.Media._ID));
                ImageInfo imgInfo = new ImageInfo(imgId,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI + "/" + imgId,
                        CommonDef.IMG_SOURCE_CATEGORY_SD,
                        imgCursor.getLong(imgCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)));

                mImagePoll.add(imgInfo);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        
        mAdapter.notifyDataSetChanged();
    }
}
