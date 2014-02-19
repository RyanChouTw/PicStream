package com.happypig.picstream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

public class ImageEditorView extends ImageView {
		
    private Context mContext;
    private int mBgColor;
    private Bitmap mBgBmp, mFgBmp;
    private ArrayList<ImageLayoutDescription> mImagesOnPages;
    //private int mLeft, mTop;
    private int mWidth, mHeight;
    private RectF mBackgroundRect = new RectF(0, 0, CommonDef.IMAGE_EDITOR_WIDTH, CommonDef.IMAGE_EDITOR_HEIGHT);
    private Matrix mMatrix;
    private int mFocusIndex = -1, mSwapIndex = -1;
    
    private boolean mIsSwapMode = false; // In this mode, highlighted image will be swapped
    private Bitmap mCropBmp = null;
    private Point mMoveStartPos = null; 
    private Point mCropBmpPos = null;
    private Matrix mCropBmpMatrix = new Matrix();

    private Paint mPaintBackground = new Paint();
    private Paint mPaintBorder = new Paint();
    private Paint mPaintFrame = new Paint(); 
    private Paint mPaintCropBitmap = new Paint();
    
    public ImageEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;        
    	
        mPaintBackground.setColor(Color.WHITE);
        mPaintBackground.setAntiAlias(true);      

        mPaintBorder.setColor(Color.WHITE);
        mPaintBorder.setAntiAlias(true);
        mPaintBorder.setStyle(Paint.Style.STROKE);
        int strokeWidth = getResources().getDimensionPixelOffset(R.dimen.image_editor_item_border);
        mPaintBorder.setStrokeWidth(strokeWidth);
        
    	mPaintFrame.setColor(Color.GREEN);
    	mPaintFrame.setStrokeWidth(4);
    	mPaintFrame.setAntiAlias(true);
    	
    	mPaintCropBitmap.setAlpha(128);
    }
  
    public void setLayout(Template template) {
    	if (template == null)
    		return;
    	
    	if (mImagesOnPages != null) {
    		freeBitmaps();
    	}
    	mBgColor = template.getBackgroundColor();
    	if (mBgColor >= 0) {
    		mPaintBackground.setColor(mBgColor);
    		mPaintBorder.setColor(mBgColor);
    	}

    	try {
	    	if (template.getBackground() != null) {
				mBgBmp = Utils.getBitmapFromAsset(mContext, CommonDef.PATH_TO_BACKGROUND + "/" + template.getBackground());
	    	}
	    	if (template.getForeground() != null) {    		
	        	mFgBmp = Utils.getBitmapFromAsset(mContext, CommonDef.PATH_TO_FOREGROUND + "/" + template.getForeground());
	    	}
		} catch (IOException e) {
			// Nothing
		}
    	mImagesOnPages = template.getItems();
    	reLocateImages();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Matrix matrix;
        
    	drawBackground(canvas);
        //  Draw something
        for(int i = 0; i < mImagesOnPages.size(); i++) {            	
        	ImageLayoutDescription info = mImagesOnPages.get(i);

            if (info.mBmp == null) {
                info.mBmp = Utils.getBitmap(mContext, Uri.parse(info.mUrl), mWidth, mHeight);
            }

            Bitmap bmp;
            //if (overlapOnOthers(i)) {
            //	bmp = Utils.cropImageWithBorder(info, mPaintBorder);
            //}
            //else {
            	bmp = Utils.cropImage(info);
            //}
            
            if ((matrix = info.getMatrix()) == null) {
                matrix = new Matrix();

                matrix.reset();
                matrix.postRotate(info.getRotate());
                matrix.postTranslate(info.getX(), info.getY());
                matrix.postConcat(mMatrix);
                
                info.setMatrix(matrix);                
            }
            
            canvas.drawBitmap(bmp, matrix, null);
            if (mIsSwapMode && mSwapIndex == i) {
            	drawFrame(canvas, info, mPaintFrame);
            }
        }
        
        if (mIsSwapMode && mCropBmp != null) {
        	mCropBmpMatrix.reset();
        	mCropBmpMatrix.postRotate(mImagesOnPages.get(mFocusIndex).mRotateDegree);
        	mCropBmpMatrix.postTranslate(mCropBmpPos.x, mCropBmpPos.y);
        	
        	canvas.drawBitmap(mCropBmp, mCropBmpMatrix, mPaintCropBitmap);
        }
    }
        
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    	int width = MeasureSpec.getSize(widthMeasureSpec);
    	int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    	int height = MeasureSpec.getSize(heightMeasureSpec);    	
    	int margin = getResources().getDimensionPixelOffset(R.dimen.image_editor_margin);    	
    	float aspect = 1;
    	
    	if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
    		mWidth = width;
    		mHeight = height;
    	} else if (widthMode == MeasureSpec.EXACTLY) {
    		mWidth = width;
    		mHeight = (int) ((float) width / CommonDef.IMAGE_EDITOR_WIDTH * CommonDef.IMAGE_EDITOR_HEIGHT);
    	} else if (heightMode == MeasureSpec.EXACTLY) {
    		mHeight = height;
    		mWidth = (int) (((float)(height / CommonDef.IMAGE_EDITOR_HEIGHT)) * CommonDef.IMAGE_EDITOR_WIDTH);
    	} else {
        	width -= 2*margin; 
        	height -= 2*margin;
        	        	
    		float waspect = (float) width / CommonDef.IMAGE_EDITOR_WIDTH;
    		float haspect = (float) height / CommonDef.IMAGE_EDITOR_HEIGHT;
    		
    		if (waspect < haspect) {
    			height = (int) (waspect * CommonDef.IMAGE_EDITOR_HEIGHT);
    			aspect = waspect;
    		}
    		else {
    			width = (int) (haspect * CommonDef.IMAGE_EDITOR_WIDTH);
    			aspect = haspect;
    		}
        	
        	//mLeft = (w- width) /2;
        	//mTop = (h - height)/2;
        	mWidth = width;
        	mHeight = height;    		
    	}
    	
    	if (mMatrix == null) {
        	mMatrix = new Matrix();
        	//mMatrix.postTranslate(mLeft,  mTop);
        	mMatrix.postScale(aspect, aspect);        	
        	mMatrix.mapRect(mBackgroundRect);        	
        	reLocateImages();    		
    	}
    	
        setMeasuredDimension(mWidth, mHeight);
    }    
    
    /*
     private boolean overlapOnOthers(int index) {     
    	// Used to check if the i-th item is overlapped with 0~i-1 items
    	
    	ImageLayoutDescription top = mImagesOnPages.get(index);
    	for (int i = 0; i < index; i++) {
    		ImageLayoutDescription item = mImagesOnPages.get(i);
    		if (RectF.intersects(top.mDstRect, item.mDstRect))
    			return true;
    	}
    	return false;
    }
    */
    
    private void reLocateImages() {
    	if (mMatrix != null) {
        	for (int i = 0; i < mImagesOnPages.size(); ++i) {
        		ImageLayoutDescription info = mImagesOnPages.get(i);
        		mMatrix.mapPoints(info.mDstPs, info.mSrcPs);
        		mMatrix.mapRect(info.mDstRect, info.mSrcRect);        		
        	}    		
    	}
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
    	// To prevent event is handled by onTouchListener, 
    	// it must use dispatchTouchEvent to track ACTION_MOVE instead of onTouchEvent
    	int x = (int) event.getX();
    	int y = (int) event.getY();
    	
    	switch (event.getAction()) {
    	case MotionEvent.ACTION_UP:
	    	{
	    		if (mFocusIndex != -1) {	    			
	    			if (mIsSwapMode && mSwapIndex != mFocusIndex) {
	    				swapItem(mFocusIndex, mSwapIndex);
	    			}
	    			mFocusIndex = -1;
	    			mSwapIndex = -1;
	    			mIsSwapMode = false;
	        		invalidate();	    			
	    		}	    			
	    		break;
	    	}
    	case MotionEvent.ACTION_MOVE:
	    	{
	    		if (mIsSwapMode) {
	    			// adjust CropBmp position
		        		mCropBmpPos.x += x - mMoveStartPos.x;
		        		mCropBmpPos.y += y - mMoveStartPos.y;
		        		mMoveStartPos.x = x;
		        		mMoveStartPos.y = y;
	        		
	        		// Find out which image is highlighted
	    			for (int i = mImagesOnPages.size()-1; i >= 0; i--) {
	    				ImageLayoutDescription info = mImagesOnPages.get(i);
	    				if (isOnPic(info, x, y)) {
	    					mSwapIndex = i;
	    					invalidate();
	    					break;
	    				}
	    			}
	    			return true;
	    		}
	    		break;
	    	}
    	}

		return super.dispatchTouchEvent(event);
    }

    public boolean isOnPic(ImageLayoutDescription info, int x , int y){  
        if(info.contain(x, y)){  
            return true;  
        }else   
            return false;  
    }
    
    public void setFocusItem(int focusIndex) {
    	mFocusIndex = focusIndex;
    	invalidate();
    }
    
    public void moveItem(float offsetX, float offsetY) {
    	
    	if (mFocusIndex < 0 || mFocusIndex > mImagesOnPages.size())
    		return;
    	
		ImageLayoutDescription info = mImagesOnPages.get(mFocusIndex);
		
		if (info.mAllowMoveDirect == CommonDef.IMAGE_EDITOR_MOVE_VERT) {			
			info.mOffset += (int) offsetY;
		}
		else { // mImagesOnPages.get(mFocusIndex).mAllowMoveDirect == CommonDef.IMAGE_EDITOR_MOVE_HORZ			
			info.mOffset += (int) offsetX;
		}

		if (Math.abs(info.mOffset) > info.mOffsetMax) {
			info.mOffset = info.mOffsetMax * ((info.mOffset > 0) ? 1:-1);
		}
    	invalidate();
    }

    public boolean setSwapMode(int x, int y) {
    	
    	if (mFocusIndex != -1) {
        	mIsSwapMode = true;
        	mSwapIndex = mFocusIndex;
        	mMoveStartPos = new Point(x, y);
        	mCropBmpPos = new Point(mImagesOnPages.get(mSwapIndex).mX, mImagesOnPages.get(mSwapIndex).mY);
        	mCropBmp = Utils.cropImage(mImagesOnPages.get(mSwapIndex));
        	invalidate();    		
    	}
    	return mIsSwapMode;
    }

    private void swapItem(int item1, int item2) {
    	if (item1 == item2 || item1 < 0 || item2 < 0 ||
    			item1 >= mImagesOnPages.size() || item2 >= mImagesOnPages.size())
    		return;
    	
    	ImageLayoutDescription tmp = new ImageLayoutDescription(0, 0, 0, 0, 0);
    	ImageLayoutDescription info1 = mImagesOnPages.get(item1);
    	ImageLayoutDescription info2 = mImagesOnPages.get(item2);
    	
    	tmp.mImgId = info1.mImgId;
    	tmp.mUrl = info1.mUrl;
    	tmp.mImgCategoryId = info1.mImgCategoryId;

    	info1.mImgId = info2.mImgId;
    	info1.mUrl = info2.mUrl;
    	info1.mImgCategoryId = info2.mImgCategoryId;    	

    	info2.mImgId = tmp.mImgId;
    	info2.mUrl = tmp.mUrl;
    	info2.mImgCategoryId = tmp.mImgCategoryId;      	
     	
    	// Reset movement adjustment, it may be not applicable to new image
    	info1.mBmp.recycle();
    	info1.mBmp = null;
    	info1.mOffset = 0;
    	info1.mOffsetMax = 0;
    	info2.mBmp.recycle();
    	info2.mBmp = null;
    	info2.mOffset = 0;
    	info2.mOffsetMax = 0;
    	
    }
    
    private void drawBackground(Canvas canvas) {
    	if (mBgBmp != null) {
    		canvas.drawBitmap(mBgBmp, mMatrix, null);
    	}else {
        	canvas.drawRect(mBackgroundRect, mPaintBackground);    	    		
    	}
    }
    
    private void drawFrame(Canvas canvas, ImageLayoutDescription info, Paint paint) {           
        canvas.drawLine(info.mDstPs[0], info.mDstPs[1], info.mDstPs[4], info.mDstPs[5], paint);  
        canvas.drawLine(info.mDstPs[4], info.mDstPs[5], info.mDstPs[8], info.mDstPs[9], paint);  
        canvas.drawLine(info.mDstPs[8], info.mDstPs[9], info.mDstPs[12], info.mDstPs[13], paint);  
        canvas.drawLine(info.mDstPs[0], info.mDstPs[1], info.mDstPs[12], info.mDstPs[13], paint);  
    }          
    
    private void freeBitmaps() {
        for(int i = 0; i < mImagesOnPages.size(); i++) {        	
        	ImageLayoutDescription info = mImagesOnPages.get(i);

            if (info.mBmp != null) {
                info.mBmp.recycle();
                info.mBmp = null;
            }
        }
        if (mCropBmp != null) {
	        mCropBmp.recycle();
	        mCropBmp = null;
        }
        if (mBgBmp != null) {
        	mBgBmp.recycle();
        	mBgBmp = null;
        }
        if (mFgBmp != null) {
        	mFgBmp.recycle();
        	mFgBmp = null;
        }
    }
    
    public RectF getDisplayRect() {
    	return mBackgroundRect;
    }
}
