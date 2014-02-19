package com.happypig.picstream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;

public class PictureStreamStore {

    private static PictureStreamStore instance = null;
    private int mNumStreams;
    private ArrayList<PicStream> mStreams = null;
    
    protected PictureStreamStore () {
        initPictureStreamStore();
    }

    public static PictureStreamStore getInstance() {
        if (instance == null)
            instance = new PictureStreamStore();
        return instance;
    }
        
    private void initPictureStreamStore() {
        File root = Environment.getExternalStorageDirectory();
        File outDir = new File(root.getAbsolutePath() + File.separator + "PicStream");

        if (!outDir.isDirectory()) {
          outDir.mkdir();
        }
        			
    	InputStream iStream = null;
        String data = "";

    	try {
			if (!outDir.isDirectory()) {
				throw new IOException("Unable to create directory PicStream. Maybe the SD card is mounted?");
			}

			File outputFile = new File(outDir, CommonDef.PICSTREAM_STORE);
			if (!outputFile.exists()) {
	            mNumStreams = 0;
	            mStreams = new ArrayList<PicStream>();
				return;
			}

    		iStream = new FileInputStream(outputFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            data = sb.toString();
            JSONObject jStore = new JSONObject(data);
            iStream.close();
            
            mNumStreams = jStore.getInt(CommonDef.JSON_PIC_STREAM_STORE_TAG_NUM_STREAMS);
            mStreams = getAllPicStream(jStore);
            
    	} catch (JSONException e) {
    		e.printStackTrace();
    	} catch (FileNotFoundException e) {        	   
    	    e.printStackTrace();
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    }

    private ArrayList<PicStream> getAllPicStream(JSONObject jStore) {
    	ArrayList<PicStream> picStreamList = null;
    	
    	try {
        	JSONArray jStreams = jStore.getJSONArray(CommonDef.JSON_PIC_STREAM_STORE_TAG_STREAMS);
        	int size = jStreams.length();
        	    	    
        	picStreamList = new ArrayList<PicStream>(size);

        	for (int i = 0; i < size; i++) {
        		PicStream tmp = new PicStream();
        		JSONObject jStream = jStreams.getJSONObject(i);
        		
        		
        		tmp.setId(jStream.optString(CommonDef.JSON_PIC_STREAM_STORE_TAG_STREAM_ID));
        		tmp.setDisplayName(jStream.optString(CommonDef.JSON_PIC_STREAM_STORE_TAG_STREAM_NAME));
        		tmp.setDescription(jStream.optString(CommonDef.JSON_PIC_STREAM_STORE_TAG_STREAM_DESCRIPTION));
        		tmp.setPicStreamUri(jStream.optString(CommonDef.JSON_PIC_STREAM_STORE_TAG_STREAM_URL));
        		tmp.setCoverImgUri(jStream.optString(CommonDef.JSON_PIC_STREAM_STORE_TAG_STREAM_COVER_IMG));
        		picStreamList.add(tmp);
        	}    		
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	
    	return picStreamList;
    }     
    
    public void addPictureStream(String name, String coverImgUri, String streamUri) {
    	PicStream stream = new PicStream();
    	
		stream.setCoverImgUri(coverImgUri);
		stream.setDisplayName(name);
		stream.setPicStreamUri(streamUri);
        
    	mNumStreams += 1;
        mStreams.add(stream);    	    		
    }
    
    public boolean editPicStreamCoverImgUri(String streamUri, String coverImgUri) {
    	for (int i = 0; i < mStreams.size(); i++) {
    		if (mStreams.get(i).mPicStreamUri.equals(streamUri)) {
    			mStreams.get(i).mCoverImgUri = coverImgUri;
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean editPicStreamName(String streamUri, String streamName) {
    	for (int i = 0; i < mStreams.size(); i++) {
    		if (mStreams.get(i).mPicStreamUri.equals(streamUri)) {
    			mStreams.get(i).mDisplayName = streamName;
    			return true;
    		}
    	}
    	return false;    	
    }
    
    public void deletePictureStream(String streamId) {
    	for (int i = 0; i < mStreams.size(); i++) {
    		if (mStreams.get(i).getId().equals(streamId)) {
    			mStreams.remove(i);
    			break;
    		}
    	}
    }
    
    public int getPicStreamNum() {
    	return mNumStreams;
    }
    
    public PicStream getPicStream(int index) {
    	return mStreams.get(index);
    }
    
    public void sync() {
    	try {
    		JSONObject jStreamStore = new JSONObject();
    		
    		jStreamStore.put(CommonDef.JSON_PIC_STREAM_STORE_TAG_NUM_STREAMS, mNumStreams);
    		JSONArray jStreamList = new JSONArray();
    		for (int i = 0; i < mNumStreams; i++) {
    			JSONObject jStream = new JSONObject();

    			jStream.put(CommonDef.JSON_PIC_STREAM_STORE_TAG_STREAM_ID, mStreams.get(i).getId());
    			jStream.put(CommonDef.JSON_PIC_STREAM_STORE_TAG_STREAM_NAME, mStreams.get(i).getDisplayName());
    			jStream.put(CommonDef.JSON_PIC_STREAM_STORE_TAG_STREAM_DESCRIPTION, mStreams.get(i).getDescription());
    			jStream.put(CommonDef.JSON_PIC_STREAM_STORE_TAG_STREAM_COVER_IMG, mStreams.get(i).getCoverImgUri());
    			jStream.put(CommonDef.JSON_PIC_STREAM_STORE_TAG_STREAM_URL, mStreams.get(i).getPicStreamUri());

    			jStreamList.put(jStream);
    		}
    		jStreamStore.put(CommonDef.JSON_PIC_STREAM_STORE_TAG_STREAMS, jStreamList);
    		
    		Utils.writeStringToFile(CommonDef.PICSTREAM_STORE, jStreamStore.toString());
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    }
    
    public class PicStream {
    	private String mId;
    	private String mCoverImgUri;
    	private String mDisplayName;
    	private String mDescription;
    	private String mPicStreamUri;   
    	
    	public void setId(String id) {
    		mId = id;
    	}
    	public String getId() {
    		return mId;
    	}
    	public void setCoverImgUri(String Uri) {
    		mCoverImgUri = Uri;
    	}
    	public String getCoverImgUri() {
    		return mCoverImgUri;
    	}
    	public void setDisplayName(String name) {
    		mDisplayName = name;
    	}
    	public String getDisplayName() {
    		return mDisplayName;
    	}
    	public void setDescription(String desc) {
    		mDescription = desc;
    	}
    	public String getDescription() {
    		return mDescription;
    	}
    	public void setPicStreamUri(String uri) {
    		mPicStreamUri = uri;
    	}
    	public String getPicStreamUri() {
    		return mPicStreamUri;
    	}    	
    }
    

    
       
}
