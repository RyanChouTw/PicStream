package com.happypig.picstream;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.happypig.picstream.PictureStreamStore.PicStream;

public class PictureStreamListActivity extends ActionBarActivity {
	GridView mGridView;
	PictureStreamListAdapter mListAdapter;
	
	private AdapterView.OnItemClickListener mListListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			PicStream stream = mListAdapter.getItem(position);
			String streamUrl = stream.getPicStreamUri();
			String streamDispName = stream.getDisplayName();
			String streamCoverImgUri = stream.getCoverImgUri();
			
			Intent startPictureStreamEditor = new Intent(PictureStreamListActivity.this, PictureStreamEditorActivity.class);
			startPictureStreamEditor.putExtra(CommonDef.EXTRA_TAG_PICSTREAM_EDITOR_MODE, CommonDef.PICSTREAM_EDITOR_MODE_VIEWER);
			startPictureStreamEditor.putExtra(CommonDef.EXTRA_TAG_PASS_STREAM_URI, streamUrl);
			startPictureStreamEditor.putExtra(CommonDef.EXTRA_TAG_PASS_STREAM_NAME, streamDispName);
			startPictureStreamEditor.putExtra(CommonDef.EXTRA_TAG_PASS_COVER_IMG_URI, streamCoverImgUri);
			
			startActivity(startPictureStreamEditor);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_stream_list);               

        mGridView = (GridView) findViewById(R.id.listViewPictureStreamList);
        mListAdapter = new PictureStreamListAdapter(this);
        mGridView.setAdapter(mListAdapter);
        mGridView.setOnItemClickListener(mListListener);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);        
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
	    	default:
	    		return super.onOptionsItemSelected(item); 
	    		
    	}    	
        
        return true;
    }   	
}
