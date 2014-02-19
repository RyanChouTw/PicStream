package com.happypig.picstream;

import android.content.Context;
import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

/**
 * Created by ryanchou on 2013/6/20.
 */

public class PictureGalleryAdapter extends BaseAdapter {
    private final Context mContext;
    ArrayList<ImageInfo> mImagePoll;

    public PictureGalleryAdapter(Context context, ArrayList<ImageInfo> imgPoll) {
        this.mContext = context;
        mImagePoll = imgPoll;
    }

    @Override 
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;
        if (view == null) {
            view = vi.inflate(R.layout.item_picture_gallery, parent, false);
        }        

        // Get the image URL for the current position.
        ImageInfo imgInfo = getItem(position);   

        ScaleImageView img = (ScaleImageView) view.findViewById(R.id.item_picture_gallery_image);
        img.setTag(imgInfo.getUrl());
        
        Bitmap bmp = Utils.getInstance(mContext).getBitmapFromCache(Integer.toString(imgInfo.getImageId()));
        if (bmp == null) {
        	if (imgInfo.getCategoryId() == CommonDef.IMG_SOURCE_CATEGORY_SD) {
        		new LoadImage(mContext).execute(img);
        		img.setImageResource(R.drawable.placeholder);    
        	}
        	else {
        		Picasso.with(mContext).load(imgInfo.getUrl()).into(img);
        	}                	
        } else {
        	img.setImageBitmap(bmp);
        }
        
        ImageView icon = (ImageView) view.findViewById(R.id.item_picture_gallery_source_icon);
        switch (imgInfo.getCategoryId()) {
            case CommonDef.IMG_SOURCE_CATEGORY_FACEBOOK:
                icon.setImageResource(R.drawable.ic_fb);
                break;
            case CommonDef.IMG_SOURCE_CATEGORY_GOOGLE_PLUS:
                icon.setImageResource(R.drawable.ic_google_plus);
                break;
            case CommonDef.IMG_SOURCE_CATEGORY_SD:
            default:
                icon.setImageResource(R.drawable.ic_sd);
                break;
        }
        
        return view;
    }

    @Override
    public ImageInfo getItem(int position) {
        return (ImageInfo) mImagePoll.get(position);
    }

    @Override public int getCount() {
        return mImagePoll.size();
    }

    @Override public long getItemId(int position) {
        return position;
    }
}
