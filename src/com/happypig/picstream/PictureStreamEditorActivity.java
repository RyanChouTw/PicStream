package com.happypig.picstream;

import java.util.Random;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by ryanchou on 2013/6/28.
 */
public class PictureStreamEditorActivity extends ActionBarActivity {
	private int mActiveMode;
	private String mPicStreamUri = null, mPicStreamName = null, mPicStreamCoverImgUri = null;
    private ArrayList<ImageInfo> mImages;    
    private int mCurrentPageIndex, mTotalPages;
    private ArrayList<Template> mImagesLayout = new ArrayList<Template>();
    private PictureStreamFormatFactory mTemplateFactory;

    private ImageEditorView mImageEditor;
    private GestureDetector gestureDetector;    
    View.OnTouchListener gestureListener;   
    
    CirclePageIndicator mIndicator;
    
    private class ImageViewGestureListner extends SimpleOnGestureListener {    	  	
        @Override
        public boolean onDown(MotionEvent e) {
        	
        	if (mActiveMode != CommonDef.PICSTREAM_EDITOR_MODE_VIEWER) {
            	int x = (int) e.getX();
            	int y = (int) e.getY();
            	ArrayList<ImageLayoutDescription> imgsInPage = mImagesLayout.get(mCurrentPageIndex).getItems();
            	for (int i = imgsInPage.size()-1; i >= 0 ; i--) {
            		ImageLayoutDescription info = imgsInPage.get(i);
            		if (mImageEditor.isOnPic(info, x, y)){
            			mImageEditor.setFocusItem(i);
            			break;
            		}        			
            	}
        	}
        	return true;
        }
        
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        	if (mActiveMode != CommonDef.PICSTREAM_EDITOR_MODE_VIEWER) {
        		mImageEditor.moveItem(distanceX, distanceY);        	
        		return true;
        	}
        	else {
        		return false;
        	}
        
        }
        
    	@Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    		try {
    			if (Math.abs(e1.getY() - e2.getY()) > CommonDef.SWIPE_MAX_OFF_PATH)
    				return false;
    			
    			
    			// right to left swipe
    			if (e1.getX() - e2.getX() > CommonDef.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > CommonDef.SWIPE_THRESHOLD_VELOCITY) {
    				if (mTotalPages-1 > mCurrentPageIndex) {
    	    			mCurrentPageIndex += 1;
    					mImageEditor.setLayout(mImagesLayout.get(mCurrentPageIndex));
    					mImageEditor.invalidate();
    					mIndicator.setCurrentItem(mCurrentPageIndex);
    				}
    			}
    			else if (e2.getX() - e1.getX() > CommonDef.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > CommonDef.SWIPE_THRESHOLD_VELOCITY) {
    				if (mCurrentPageIndex > 0) {
    					mCurrentPageIndex -= 1;
    					mImageEditor.setLayout(mImagesLayout.get(mCurrentPageIndex));
    					mImageEditor.invalidate();
    					mIndicator.setCurrentItem(mCurrentPageIndex);
    				}
    			}    				
    		} catch (Exception e) {
    			// nothing
    		}
            return true;
        } 
    	
    	@Override
    	public void onLongPress(MotionEvent e) {
    		if (mActiveMode != CommonDef.PICSTREAM_EDITOR_MODE_VIEWER) {
    			mImageEditor.setSwapMode((int)e.getX(), (int)e.getY());
    		}
    	}
    }    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_stream_editor);

        gestureDetector = new GestureDetector(this, new ImageViewGestureListner());
        gestureListener = new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}
		};
        
		mTemplateFactory = new PictureStreamFormatFactory(this);

		mActiveMode = getIntent().getIntExtra(CommonDef.EXTRA_TAG_PICSTREAM_EDITOR_MODE, CommonDef.PICSTREAM_EDITOR_MODE_NEW);
		if (mActiveMode == CommonDef.PICSTREAM_EDITOR_MODE_NEW) { // CommonDef.PICSTREAM_EDITOR_ACTION_NEW
	        mImages = getIntent().getParcelableArrayListExtra(CommonDef.EXTRA_TAG_PASS_ARRAYLIST);
	        generateLayout(0);     			
		}
		else if (mActiveMode == CommonDef.PICSTREAM_EDITOR_MODE_VIEWER) {
			mImages = new ArrayList<ImageInfo>();
			mPicStreamUri = getIntent().getStringExtra(CommonDef.EXTRA_TAG_PASS_STREAM_URI);
			mPicStreamName = getIntent().getStringExtra(CommonDef.EXTRA_TAG_PASS_STREAM_NAME);
			mPicStreamCoverImgUri = getIntent().getStringExtra(CommonDef.EXTRA_TAG_PASS_COVER_IMG_URI);
			generateLayout(mPicStreamUri);
		} 
		else if (mActiveMode == CommonDef.PICSTREAM_EDITOR_MODE_EDITOR) {
			
		}

        mImageEditor = (ImageEditorView) findViewById(R.id.imgEditorPictureStreamEditor);        
        mImageEditor.setOnTouchListener(gestureListener);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

        if (mImagesLayout.size() > 0) {			
	        mImageEditor.setLayout(mImagesLayout.get(0));        
	        mIndicator.setTotalItem(mImagesLayout.size()); 
		}        
                
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

    	switch(mActiveMode) {
    	case CommonDef.PICSTREAM_EDITOR_MODE_VIEWER:
	        menu.add(Menu.NONE, R.id.ID_ACTION_EDIT, Menu.NONE, R.string.edit)        	
        			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    		break;
    	case CommonDef.PICSTREAM_EDITOR_MODE_NEW:
    	case CommonDef.PICSTREAM_EDITOR_MODE_EDITOR:
		default:
	        menu.add(Menu.NONE, R.id.ID_ACTION_ADD, Menu.NONE, R.string.add)
		        	.setIcon(R.drawable.ic_add)
		        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        menu.add(Menu.NONE, R.id.ID_ACTION_NEXT, Menu.NONE, R.string.next)        	
		        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    		break;
    	}
        
        return true;
    }   

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CommonDef.ACTION_CODE_PICK_GALLERY_IMAGE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked some photos
                ArrayList<ImageInfo> addedImgs = data.getParcelableArrayListExtra(CommonDef.EXTRA_TAG_PASS_ARRAYLIST);
                int numOriImgs = mImages.size();
                mImages.addAll(addedImgs);
                generateLayout(numOriImgs);
                mIndicator.setTotalItem(mImagesLayout.size());                
            }
        }
    }
    
    private void generateLayout(int startIndex) {
        Template template;
        ArrayList<ImageLayoutDescription> list;
        int numImagesInPages;
        
        boolean bFinish = false;
        int accumulatedNumImages = startIndex;
        while (bFinish == false) {
            if (mImages.size() - accumulatedNumImages > CommonDef.MAX_NUM_IMAGES_IN_PAGE) {
            	numImagesInPages = new Random().nextInt(CommonDef.MAX_NUM_IMAGES_IN_PAGE) + 1;
            } else {
            	numImagesInPages = new Random().nextInt(mImages.size() - accumulatedNumImages)+1;
            	if (numImagesInPages == mImages.size() - accumulatedNumImages) {
            		bFinish = true;
            	}
            }
            
            template = mTemplateFactory.getTemplate(numImagesInPages);
            list = template.getItems();
            
            for (int i = 0; i < list.size(); i++) {
            	ImageLayoutDescription info = list.get(i);
            	info.mImgId = mImages.get(accumulatedNumImages + i).getImageId();
            	
            	Log.d("PictureStreamEditorActivity", "imgId = " + info.mImgId);
            	info.mUrl = mImages.get(accumulatedNumImages + i).getUrl();
            	info.mImgCategoryId = mImages.get(accumulatedNumImages + i).getCategoryId();
            }
            
            mImagesLayout.add(template);
            accumulatedNumImages += list.size();      
        }
        mTotalPages = mImagesLayout.size();
    }
    
    private void generateLayout(String picStreamUri) {
    	String data = Utils.readStringFromFile(Uri.parse(picStreamUri));
    	
    	try {
    		JSONObject jObject = new JSONObject(data);        	
        	parsePicStream(jObject);    		
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    }
    
    private void parsePicStream(JSONObject jPicStream) {
    	mTotalPages = jPicStream.optInt(CommonDef.JSON_PIC_STREAM_TAG_NUM_PAGES);
    	JSONArray jPages = jPicStream.optJSONArray(CommonDef.JSON_PIC_STREAM_TAG_PAGES);
    	
    	try {
        	for (int i = 0; i < mTotalPages; i++) {       	    
        		JSONObject jPage = jPages.optJSONObject(i);
        		if (jPage == null)	
        			break;
        		
        		Template temp = new Template();

        		if (jPage.isNull(CommonDef.JSON_PIC_STREAM_TAG_PAGE_BACKGROUND_COLOR) != true) {
        			temp.setBackgroundColor(jPage.getInt(CommonDef.JSON_PIC_STREAM_TAG_PAGE_BACKGROUND_COLOR));
        		}        		
        		temp.setBackground(jPage.optString(CommonDef.JSON_PIC_STREAM_TAG_PAGE_BACKGROUND_IMG));        		
        		temp.setForeground(jPage.optString(CommonDef.JSON_PIC_STREAM_TAG_PAGE_FOREGROUND_IMG));
        		
        		int numImagesInPage = jPage.optInt(CommonDef.JSON_PIC_STREAM_TAG_PAGE_NUM_IMAGES);
        		JSONArray jImages = jPage.optJSONArray(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGES);
        		ArrayList<ImageLayoutDescription> imgList = new ArrayList<ImageLayoutDescription>(CommonDef.MAX_NUM_IMAGES_IN_PAGE);
        		for (int j = 0; j < numImagesInPage; j++) {    			
        			JSONObject jImage = jImages.optJSONObject(j);
        			if (jImage == null) 
        				break;
        			
        			int x = jImage.optInt(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_POSITION_X);
        			int y = jImage.optInt(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_POSITION_Y);
        			int rotate = jImage.optInt(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_ROTATION);
        			int width = jImage.optInt(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_WIDTH);
        			int height = jImage.optInt(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_HEIGHT);
        			int offset = jImage.optInt(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_OFFSET);
        			ImageLayoutDescription imgLayout = new ImageLayoutDescription(x, y, width, height, rotate);

        			imgLayout.setOffset(offset);
        			int imgId = jImage.optInt(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_ID);
        			String imgUrl = jImage.optString(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_URL);
        			int imgCategoryId = jImage.optInt(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_CATEGORY);
        			imgLayout.setImgId(imgId);
        			imgLayout.setUrl(imgUrl);
        			imgLayout.setCategoryId(imgCategoryId);    		
        			
        			ImageInfo img = new ImageInfo(imgId, imgUrl, imgCategoryId);
        			mImages.add(img);
        			
        			imgList.add(imgLayout);
        		}
        		temp.setItems(imgList);
        		mImagesLayout.add(temp);
        	}
    		
    	} catch (JSONException e) {
    		// nothing
    	}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
    	switch (itemId) {
    	case android.R.id.home:
    		finish();
    		break;
    	case R.id.ID_ACTION_ADD:
    		Intent i = new Intent(this, PictureGalleryActivity.class);
    		i.putExtra(CommonDef.EXTRA_TAG_PICSTREAM_GALLERY_MODE, CommonDef.PICSTREAM_GALLERY_MODE_ADD);
    		startActivityForResult(i, CommonDef.ACTION_CODE_PICK_GALLERY_IMAGE);
    		break;
    	case R.id.ID_ACTION_NEXT:
    		String filename = null;
    		
    		if (mPicStreamUri == null) {
        		filename = Utils.getUniqueFileName() + CommonDef.FILE_EXTENSION_PICTURE_STREAM;
    		}
    		else {
    			filename = Uri.parse(mPicStreamUri).getLastPathSegment();
    		}
    		
    		mPicStreamUri = savePicStream(filename).toString();
    		
            // Create another activity which generate photo stream
    		startPictureStreamCoverEditor();
            break;
    	case R.id.ID_ACTION_EDIT:
    		mActiveMode = CommonDef.PICSTREAM_EDITOR_MODE_EDITOR; 
			invalidateOptionsMenu();
    		break;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    	return true;		
    }
    
    private void startPictureStreamCoverEditor() {
        Intent startPictureStreamCoverEditor = new Intent(PictureStreamEditorActivity.this, PictureStreamCoverEditorActivity.class);
        
        startPictureStreamCoverEditor.putExtra(CommonDef.EXTRA_TAG_PICSTREAM_EDITOR_MODE, mActiveMode);
        startPictureStreamCoverEditor.putParcelableArrayListExtra(CommonDef.EXTRA_TAG_PASS_ARRAYLIST, mImages);
        startPictureStreamCoverEditor.putExtra(CommonDef.EXTRA_TAG_PASS_STREAM_URI, mPicStreamUri);
        startPictureStreamCoverEditor.putExtra(CommonDef.EXTRA_TAG_PASS_STREAM_NAME, mPicStreamName);
        startPictureStreamCoverEditor.putExtra(CommonDef.EXTRA_TAG_PASS_COVER_IMG_URI, mPicStreamCoverImgUri);        
        
        startActivity(startPictureStreamCoverEditor);    	
    }
    
    private Uri savePicStream(String streamName) {    	
    	JSONObject stream = new JSONObject();
    	Uri fileUri = null;
    	
    	try {
	    	// total pages
	    	stream.put(CommonDef.JSON_PIC_STREAM_TAG_NUM_PAGES, mTotalPages);
	    	// create JSONArray which contains all pages
	    	JSONArray pagesOnStream = new JSONArray();
	    	for (int i = 0; i < mTotalPages; i++) {
	        	JSONObject streamPage = savePicStreamPage(mImagesLayout.get(i));
	        	pagesOnStream.put(streamPage);
	    	}
	    	stream.put(CommonDef.JSON_PIC_STREAM_TAG_PAGES, pagesOnStream);
	    	
	    	fileUri = Utils.writeStringToFile(streamName, stream.toString());
    	} catch (JSONException e) {
    		// nothing
    	}
    	
    	return fileUri;
    }
    
    private JSONObject savePicStreamPage(Template data) {
    	int numImagesOnPage;
    	JSONObject page = new JSONObject();

    	try {
        	if (data.getBackground() != null)
        		page.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_BACKGROUND_IMG, data.getBackground());
        	if (data.getBackgroundColor() >= 0)
        		page.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_BACKGROUND_COLOR, data.getBackgroundColor());
        	if (data.getForeground() != null)
        		page.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_FOREGROUND_IMG, data.getForeground());
    		page.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_NUM_IMAGES, data.getItemNum());
    		numImagesOnPage = data.getItemNum();
    		if (numImagesOnPage > 0) {
    			JSONArray imagesOnPage = new JSONArray();
    			for (int i = 0; i < numImagesOnPage; i++) {
        			JSONObject img = saveImagesOnPage(data.getItems().get(i));
        			imagesOnPage.put(img);
    			}
    			page.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGES, imagesOnPage);
    		}
    		
    		
    	} catch (JSONException e) {
    		// Nothing
    	}
    	
    	return page;
    }
    
    private JSONObject saveImagesOnPage(ImageLayoutDescription info) {
    	JSONObject img = new JSONObject();
    	
    	try {
    		img.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_ID, info.mImgId);
    		img.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_URL, info.mUrl);
    		img.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_CATEGORY, info.mImgCategoryId);
    		img.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_POSITION_X, info.mX);
    		img.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_POSITION_Y, info.mY);
    		img.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_ROTATION, info.mRotateDegree);
    		img.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_WIDTH, info.mWidth);
    		img.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_HEIGHT, info.mHeight);
    		img.put(CommonDef.JSON_PIC_STREAM_TAG_PAGE_IMAGE_OFFSET, info.mOffset);    		
    	} catch (JSONException e) {
    		// Nothing
    	}
    	
    	
    	return img;
    	
    }
}
