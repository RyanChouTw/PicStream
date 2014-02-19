package com.happypig.picstream;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class PictureStreamCoverEditorActivity extends ActionBarActivity {

	private int mActiveMode;
	private String mStreamUri = null;
	private String mStreamName = null;
	private String mStreamCoverImgUri = null;
	private ArrayList<ImageInfo> mCoverImages;
	private int mCoverIndex=0;
	private ImageView mCoverImgView;	
	private EditText mStreamNameTextView;
	private PictureStreamStore mStreamStore = PictureStreamStore.getInstance();

    private GestureDetector gestureDetector;    
    View.OnTouchListener gestureListener;   	
	private class ImageViewGestureListner extends SimpleOnGestureListener {    	  	
	    @Override
        public boolean onDown(MotionEvent e) {        	
        	return true;
        }
	        
    	@Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    		try {
    			if (Math.abs(e1.getY() - e2.getY()) > CommonDef.SWIPE_MAX_OFF_PATH)
    				return false;    			
    			
    			Bitmap bmp;
    			// right to left swipe
    			if (e1.getX() - e2.getX() > CommonDef.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > CommonDef.SWIPE_THRESHOLD_VELOCITY) {
    				if (mCoverImages.size()-1 > mCoverIndex) {
    					mCoverIndex += 1;
    					bmp = Utils.cropSquareImage(Utils.getBitmap(PictureStreamCoverEditorActivity.this, Uri.parse(mCoverImages.get(mCoverIndex).getUrl()), 360, 360));
    					mCoverImgView.setImageBitmap(bmp);
    				}
    			}
    			else if (e2.getX() - e1.getX() > CommonDef.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > CommonDef.SWIPE_THRESHOLD_VELOCITY) {
    				if (mCoverIndex > 0) {
    					mCoverIndex -= 1;
    					bmp = Utils.cropSquareImage(Utils.getBitmap(PictureStreamCoverEditorActivity.this, Uri.parse(mCoverImages.get(mCoverIndex).getUrl()), 360, 360));
    					mCoverImgView.setImageBitmap(bmp);
    				}
    			}    				
    		} catch (Exception e) {
    			// nothing
    		}
            return true;
        } 
	    	
    }    	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_stream_cover_editor);               
        
        gestureDetector = new GestureDetector(this, new ImageViewGestureListner());
        gestureListener = new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}
		};
		
		mActiveMode = getIntent().getIntExtra(CommonDef.EXTRA_TAG_PICSTREAM_EDITOR_MODE, CommonDef.PICSTREAM_EDITOR_MODE_NEW);
        mCoverImages = getIntent().getParcelableArrayListExtra(CommonDef.EXTRA_TAG_PASS_ARRAYLIST);

        mStreamUri = getIntent().getStringExtra(CommonDef.EXTRA_TAG_PASS_STREAM_URI);
        mStreamName = getIntent().getStringExtra(CommonDef.EXTRA_TAG_PASS_STREAM_NAME);
        mStreamCoverImgUri = getIntent().getStringExtra(CommonDef.EXTRA_TAG_PASS_COVER_IMG_URI);
        
        mCoverImgView = (ImageView) findViewById(R.id.coverImageView);

        if (mStreamCoverImgUri != null) {
        	mCoverImgView.setImageBitmap(Utils.cropSquareImage(Utils.getBitmap(this, Uri.parse(mStreamCoverImgUri), 360, 360)));
        	for (int i = 0; i < mCoverImages.size(); i++) {
        		if (mCoverImages.get(i).getUrl().equals(mStreamCoverImgUri)) {
        			mCoverIndex = i;
        			break;
        		}
        	}
        } else {
        	mCoverImgView.setImageBitmap(Utils.cropSquareImage(Utils.getBitmap(this, Uri.parse(mCoverImages.get(mCoverIndex).getUrl()), 360, 360)));
        	
        }
               
        mStreamNameTextView = (EditText) findViewById(R.id.edittext_name);
        if (mStreamName != null) {
        	mStreamNameTextView.setText(mStreamName);
        }
        
        mCoverImgView.setOnTouchListener(gestureListener);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, R.id.ID_ACTION_SAVE, Menu.NONE, R.string.save)
        	.setIcon(R.drawable.ic_save)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
    	switch (itemId) {
    	case android.R.id.home:
    		finish();
    		break;
    	case R.id.ID_ACTION_SAVE:
    		String name = mStreamNameTextView.getText().toString();    		
    		if (name.length() <= 0) {
    			Toast.makeText(this, 
    					getString(R.string.warning_empty_name),
    					Toast.LENGTH_LONG).show();
    		}
    		else {
    			
    			if (mActiveMode == CommonDef.PICSTREAM_EDITOR_MODE_NEW) {
        			mStreamStore.addPictureStream(name, mCoverImages.get(mCoverIndex).getUrl(), mStreamUri);    				
    			} else {
    				mStreamStore.editPicStreamCoverImgUri(mStreamUri, mCoverImages.get(mCoverIndex).getUrl());
    				mStreamStore.editPicStreamName(mStreamUri, name);
    			}
    			mStreamStore.sync();
    			// back to Picture Stream List
    			startPictureStreamList();
    		}    			
    		break;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    	return true;		
    }
    
    private void startPictureStreamList() {
        Intent startPictureStreamList = new Intent(this, PictureStreamMainActivity.class);
        startPictureStreamList.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startPictureStreamList.putExtra(CommonDef.EXTRA_TAG_PASS_ACTION, CommonDef.ACTION_START_PICSTREAM_LIST);
        startActivity(startPictureStreamList);    	
    }
}
