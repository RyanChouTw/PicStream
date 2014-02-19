package com.happypig.picstream;

import java.util.ArrayList;
import java.util.Collections;

import com.facebook.Session;
import com.happypig.picstream.ImageInfo;
import com.happypig.picstream.PictureGalleryAdapter;
import com.happypig.picstream.R;
import com.happypig.picstream.Utils;

import android.content.Context;
import android.os.AsyncTask;


public class FacebookImageQueryTask extends AsyncTask<String, Integer, String> {
    Context mContext;
    ArrayList<ImageInfo> mImagePoll;
    PictureGalleryAdapter mAdapter;
    
    
    public FacebookImageQueryTask(Context context, ArrayList<ImageInfo> imgPoll, PictureGalleryAdapter adapter) {
        this.mContext = context;
        this.mImagePoll = imgPoll;
        this.mAdapter = adapter;               
    } 
    
    @Override
    protected String doInBackground(String... userId) {
    	
    	try {
    		String accessToken = Session.getActiveSession().getAccessToken();    		
    		String albumJson = Utils.downloadUrl(mContext.getResources().getString(R.string.facebook_url_albums).replace("[USER_ID]", userId[0]).replace("[ACCESS_TOKEN]", accessToken));
    		ArrayList<FacebookAlbumItem> albums = FacebookJSONParser.parseAlbums(albumJson);
    		
    		for (int i = 0; i < albums.size(); i++) {
    			String photoJson;
    			ArrayList<ImageInfo> photos;
    			String albumId = albums.get(i).getId();
    			String command = mContext.getResources().getString(R.string.facebook_url_photos).replace("[ACCESS_TOKEN]", accessToken).replace("[ALBUMID]", albumId); 
    			do {
        			photoJson = Utils.downloadUrl(command);
        			photos = FacebookJSONParser.parsePhotos(photoJson);
        			mImagePoll.addAll(photos);
    			} while((command = FacebookJSONParser.getNextPage(photoJson)).isEmpty() == false);    			    		
    		}
    	} catch (Exception e) {
    		// Add exception handler here
    	}    	
    	
    	return null;
    }
    
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        
        Collections.sort(mImagePoll);
        mAdapter.notifyDataSetChanged();
    }    
}
