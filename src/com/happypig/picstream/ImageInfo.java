package com.happypig.picstream;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ryanchou on 2013/6/26.
 */
public class ImageInfo implements Parcelable, Comparable<ImageInfo>{
    private int mId;
    private String mUrl;
    private int mCategoryId;
    private long mCreateTime;
    
    public ImageInfo(int imgId, String url, int categoryId) {
        this.mId = imgId;
        this.mUrl = url;
        this.mCategoryId = categoryId;
    }
    
    public ImageInfo(int imgId, String url, int categoryId, long cTime) {
        this.mId = imgId;
        this.mUrl = url;
        this.mCategoryId = categoryId;
        this.mCreateTime = cTime;
    }
    
    public void setImgId(int imgId) {
    	this.mId = imgId;
    }    
    public int getImageId(){
    	return mId;
    }
    public void setUrl(String url) {
    	this.mUrl = url;
    }
    public String getUrl() {
    	return mUrl;
    }
    public void setCategoryId(int catId) {
    	this.mCategoryId = catId;
    }
    public int getCategoryId(){
    	return mCategoryId;
    }
    public void setCreateTime(long date) {
    	this.mCreateTime = date;
    }
    public long getCreateTime() {
    	return mCreateTime;
    }

    public ImageInfo (Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<ImageInfo> CREATOR = new Parcelable.Creator<ImageInfo>() {
        public ImageInfo createFromParcel(Parcel in) {
            return new ImageInfo(in);
        }
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };

    public void readFromParcel(Parcel in) {
        mId = in.readInt();
        mUrl = in.readString();
        mCategoryId = in.readInt();
        mCreateTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mUrl);
        dest.writeInt(mCategoryId);
        dest.writeLong(mCreateTime);
    }

	@Override
	public int compareTo(ImageInfo another) {
		if (mCreateTime > another.getCreateTime()) {
			return -1;
		}
		else if (mCreateTime == another.getCreateTime()) {
			return 0;
		}
		else {
			return 1;
		}
	}
}
