package com.happypig.picstream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class FacebookJSONParser{

	public static String parseLatestStatus(String json){
		String latestStatus="";
		String startTag="\"message\":\"";

		int indexOf=json.indexOf(startTag);

		if(indexOf>0){
			int start=indexOf+startTag.length();
			String endTag="\",";
			return(json.substring(start,json.indexOf(endTag,start)));
		}
		return(latestStatus);
	}

	public static ArrayList<FacebookAlbumItem> parseAlbums(String json) throws JSONException{

		ArrayList<FacebookAlbumItem> albums=new ArrayList<FacebookAlbumItem>();
		JSONObject rootObj=new JSONObject(json);
		JSONArray itemList=rootObj.getJSONArray("data");
		
		int albumCount=itemList.length();
		for(int albumIndex=0;albumIndex<albumCount;albumIndex++){

			JSONObject album=itemList.getJSONObject(albumIndex);			
			albums.add(new FacebookAlbumItem(album.getString("id")));
		}
		return(albums);

	}

	public static ArrayList<ImageInfo> parsePhotos(String json) throws JSONException {
		ArrayList<ImageInfo> photos=new ArrayList<ImageInfo>();
		JSONObject rootObj=new JSONObject(json);
		JSONArray itemList=rootObj.getJSONArray("data");

		int photoCount=itemList.length();
		SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSSS");
		for(int photoIndex=0;photoIndex<photoCount;photoIndex++){
			JSONObject photo=itemList.getJSONObject(photoIndex);
			String cTimeStr = photo.getString("created_time");
			long cTime = 0;
			try {
				// format of created_time = "2013-06-22T05:52:14+0000"
				cTime = dateFmt.parse(cTimeStr).getTime();							
			} catch (Exception e) {
				e.printStackTrace();
			}
			photos.add(new ImageInfo(0,	// there is no image id for Facebook image 
									photo.getString("picture"),
									CommonDef.IMG_SOURCE_CATEGORY_FACEBOOK,
									cTime));
		}
		return(photos);
	}
	
	public static String getNextPage(String json) throws JSONException {
		JSONObject rootObj=new JSONObject(json);
		if (rootObj.isNull("paging"))
			return "";
		else {
			JSONObject pagingObj = rootObj.getJSONObject("paging");
			return pagingObj.optString("next");
		}		
	}
}
