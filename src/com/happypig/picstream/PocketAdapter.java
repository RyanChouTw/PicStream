package com.happypig.picstream;

import android.content.Context;
import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.ArrayList;

/**
 * Created by ryanchou on 2013/6/27.
 */
public class PocketAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<ImageInfo> mImgPocket;
    private final Utils mUtils;

    public PocketAdapter(Context context, ArrayList<ImageInfo> imgPocket) {
        mContext = context;
        mImgPocket = imgPocket;

        mUtils = Utils.getInstance(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bitmap bitmap;
        ImageInfo imgInfo;

        if (mImgPocket.size() == 0)
            return null;

        LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;
        if (view == null) {
            view = vi.inflate(R.layout.item_pocket, parent, false);
        }

        // Get the image URL for the current position.
        imgInfo = getItem(position);
        String key = Integer.toString(imgInfo.getImageId());
        bitmap = mUtils.getBitmapFromDiskCache(key);

        ScaleImageView img = (ScaleImageView) view.findViewById(R.id.item_pocket_image);
        img.setImageBitmap(bitmap);

        return view;
    }

    @Override
    public ImageInfo getItem(int position) {
        if (mImgPocket != null && mImgPocket.size() > 0)
            return (ImageInfo) mImgPocket.get(position);
        else
            return null;
    }

    @Override
    public int getCount() {
        if (mImgPocket != null)
            return mImgPocket.size();
        else
            return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
