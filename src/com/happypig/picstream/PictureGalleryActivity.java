package com.happypig.picstream;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;
import com.facebook.Session;
import com.origamilabs.library.views.StaggeredGridView;

import java.util.ArrayList;

public class PictureGalleryActivity extends ActionBarActivity {

	private int mActiveMode;
    private StaggeredGridView mStaggeredGridView;
    private PictureGalleryAdapter mGridViewAdapter;
    private ArrayList<ImageInfo> mImagePoll;

    private HorizontalListView mPocketListView;
    private PocketAdapter mPocketAdapter;
    private ArrayList<ImageInfo> mImageInPocket;

    private StaggeredGridView.OnItemClickListener mPictureGalleryGridListener = new StaggeredGridView.OnItemClickListener() {
        @Override
        public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
            mImageInPocket.add(mImagePoll.get(position));
            mPocketAdapter.notifyDataSetChanged();
        }
    };

    private HorizontalListView.OnItemClickListener mPocketListListener = new HorizontalListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mImageInPocket.remove(position);
            mPocketAdapter.notifyDataSetChanged();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_gallery);

        mImagePoll = new ArrayList<ImageInfo>();
        mImageInPocket = new ArrayList<ImageInfo>();
        mGridViewAdapter = new PictureGalleryAdapter(this, mImagePoll);

        LocalImageQueryTask queryLocalImageTask = new LocalImageQueryTask(this, mImagePoll, mGridViewAdapter);
        queryLocalImageTask.execute();

        Session session = Session.getActiveSession();
        if(session != null && session.isOpened()) {        	
            String mFbUserId = getIntent().getStringExtra(CommonDef.EXTRA_TAG_PASS_FB_USER_ID);
	        FacebookImageQueryTask queryFbImageTask = new FacebookImageQueryTask(this, mImagePoll, mGridViewAdapter);
	        queryFbImageTask.execute(mFbUserId);
        }
        
        mStaggeredGridView = (StaggeredGridView) findViewById(R.id.gridViewPicGallery);
        int gridMargin = getResources().getDimensionPixelOffset(R.dimen.staggered_gird_margin);
        mStaggeredGridView.setItemMargin(gridMargin);
        mStaggeredGridView.setPadding(gridMargin, 0, gridMargin, 0);
        mStaggeredGridView.setAdapter(mGridViewAdapter);
        mStaggeredGridView.setOnItemClickListener(mPictureGalleryGridListener);

        mPocketAdapter = new PocketAdapter(this, mImageInPocket);
        mPocketListView = (HorizontalListView) findViewById(R.id.horListViewPicGallery);
        mPocketListView.setAdapter(mPocketAdapter);
        mPocketListView.setOnItemClickListener(mPocketListListener);
        
        mActiveMode = getIntent().getIntExtra(CommonDef.EXTRA_TAG_PICSTREAM_GALLERY_MODE, CommonDef.PICSTREAM_GALLERY_MODE_FIRST_ENTRY);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

    	if (mActiveMode == CommonDef.PICSTREAM_GALLERY_MODE_FIRST_ENTRY) {
	        menu.add(Menu.NONE, R.id.ID_ACTION_NEXT, Menu.NONE, R.string.next)
	        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    	}
    	else {
    		menu.add(Menu.NONE, R.id.ID_ACTION_DONE, Menu.NONE, R.string.done)
    			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    	}
    	
        return true;
    }   
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            return false;
        }
        
        //This uses the imported MenuItem from ActionBarSherlock
    	int itemId = item.getItemId();
    	switch (itemId) {
	    	case android.R.id.home:
	    		finish();
	    		break;
	    	case R.id.ID_ACTION_NEXT:
	    	{
	        	if (mImageInPocket.size() > 0) {
	                // Create another activity which generate photo stream        	
	                Intent startPictureStreamEditor = new Intent(PictureGalleryActivity.this, PictureStreamEditorActivity.class);
	                startPictureStreamEditor.putParcelableArrayListExtra(CommonDef.EXTRA_TAG_PASS_ARRAYLIST, mImageInPocket);
	                startActivity(startPictureStreamEditor);
	                finish();
	        	}
	        	else {
	        		Toast.makeText(this, R.string.warning_no_image_selected, Toast.LENGTH_SHORT).show();
	        	}
	    		break;    	
	    	}
	    	case R.id.ID_ACTION_DONE:
	    	{
	    		Intent i = new Intent();
	    		i.putParcelableArrayListExtra(CommonDef.EXTRA_TAG_PASS_ARRAYLIST, mImageInPocket);
	    		setResult(RESULT_OK, i);
	    		finish();
	    		break;
	    	}
	    	default:
	    		return super.onOptionsItemSelected(item); 
	    		
    	}    	
    	
        
        return true;
    }    
}
