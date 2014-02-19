package com.happypig.picstream;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;

public class ImageLayoutDescription {
    // Information got from ImageInfo
    int mImgId;
    String mUrl;
    int mImgCategoryId;

    Bitmap mBmp;
    
    // Layout 
    float mRotateDegree;
    int	mX, mY, mWidth, mHeight;
    Matrix mMatrix;
    float[] mSrcPs, mDstPs;
    RectF mSrcRect, mDstRect;

    int mAllowMoveDirect;
    int mOffset, mOffsetMax;
    
    public ImageLayoutDescription(int x, int y, int width, int height, float rotateDegree) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;	
        mRotateDegree = rotateDegree;

        /* used to keep the layout information described in template file */
        mSrcPs = new float[]{  
                x,y,   
                x+width/2,y,   
                x+width,y,   
                x+width,y+height/2,  
                x+width,y+height,   
                x+width/2,y+height,   
                x,y+height,   
                x,y+height/2,   
                x+width/2,y+height/2  
            };  
        /* used to keep the drawing information which is resulted from transformation of ImageEditorViewer's Matrix */
        mDstPs = mSrcPs.clone(); 

        /* used to keep the layout information described in template file */
        mSrcRect = new RectF(x, y, x+width, y+height);
        if (rotateDegree != 0) {
            Matrix matrix = new Matrix();
        	matrix.postRotate(rotateDegree, x, y);
        	matrix.mapPoints(mSrcPs);
        	matrix.mapRect(mSrcRect);        	
        }
        /* used to keep the drawing information which is resulted from transformation of ImageEditorViewer's Matrix */
        mDstRect = new RectF();
    }    
    
    public void setImgId(int id) {
    	mImgId = id;
    }
    public int getImgId() {
    	return mImgId;
    }
    public void setUrl(String url) {
    	mUrl = url;
    }
    public String getUrl() {
    	return mUrl;
    }
    public void setCategoryId(int id) {
    	mImgCategoryId = id;
    }
    public int getCategoryId() {
    	return mImgCategoryId;
    }
    
    public void setBitmap(Bitmap bmp) {
    	mBmp = bmp;
    }    

    public Bitmap getBitmap() {
        return mBmp;
    }

    public void setX(int x) {
    	mX = x;
    }    
    public int getX() {
        return mX;
    }
    
    public void setY(int y) {
    	mY = y;
    }
    public int getY() {
    	return mY;
    }
    
    public void setOffset(int offset) {
    	mOffset = offset;
    }
    public int getOffset() {
    	return mOffset;
    }
    public boolean contain(float x, float y) {
    	return mDstRect.contains(x, y);
    }

    public float getRotate() {
        return mRotateDegree;
    }
    
    public void setMatrix(Matrix matrix) {
    	mMatrix = matrix;
    }
    
    public Matrix getMatrix() {
    	return mMatrix;
    }    
}
