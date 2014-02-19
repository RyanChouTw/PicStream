package com.happypig.picstream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ryanchou on 2013/7/3.
 */


public class PictureStreamFormatFactory {
	
    Context mContext;    
    ArrayList[] mTemplatePool = new ArrayList[CommonDef.MAX_NUM_IMAGES_IN_PAGE];

    public PictureStreamFormatFactory(Context context) {
        mContext = context;
        init();
    }

    private void init() {
    	String[] templates;
		StringBuffer buf = new StringBuffer();    	
    	
    	AssetManager asset = mContext.getResources().getAssets();
    	try {
        	templates = asset.list(CommonDef.PATH_TO_TEMPLATES);
        	
        	for (int i = 0; i < templates.length; i++) {
        		buf.delete(0, buf.length());
        		buf.append(CommonDef.PATH_TO_TEMPLATES);
        		buf.append("/");
        		buf.append(templates[i]);
        		Template tmp = parseTemplate(buf.toString());
        		if (tmp.getItemNum() <= 0) {
        			// error in Template
        			continue;
        		}
        		ArrayList<Template> list = mTemplatePool[tmp.getItemNum()-1];
        		if (list == null) {
        			list = new ArrayList<Template>(CommonDef.MAX_NUM_IMAGES_IN_PAGE);
        			list.add(tmp);
        			mTemplatePool[tmp.getItemNum()-1] = list;
        		}
        		else {
        			list.add(tmp);
        		}        		        		
        	}

    	} catch (IOException e) {
    		Log.d("PictureStreamFormatFactory", "IO exception : " + e.toString());
    	}
    }
    
    private Template parseTemplate(String path) {
    	Template template = null;
        InputStream iStream = null;
        JSONObject jObject;
        String data = "";

        try {
            iStream = mContext.getResources().getAssets().open(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            data = sb.toString();
            jObject = new JSONObject(data);
            iStream.close();
                      
            template = generateTemplate(jObject);
        } catch (Exception e) {
            Log.d("PictureSteamFormatFactory", "Exception to load template : " + e.toString());
        }    	
        
        return template;
    }
    
    private Template generateTemplate(JSONObject json) {
    	Template template = new Template();
    	String bgName=null, fgName=null;
    	int bgColor = -1;
        ArrayList<ImageLayoutDescription> images = new ArrayList<ImageLayoutDescription>(CommonDef.MAX_NUM_IMAGES_IN_PAGE);

        try {
        	if (!json.isNull(CommonDef.JSON_TEMPLATE_TAG_PAGE_BACKGROUND_IMG)) {
        		bgName = json.getString(CommonDef.JSON_TEMPLATE_TAG_PAGE_BACKGROUND_IMG);
        	}
        	if (!json.isNull(CommonDef.JSON_TEMPLATE_TAG_PAGE_BACKGROUND_COLOR)) {
        		try {
            		bgColor = Color.parseColor(json.getString(CommonDef.JSON_TEMPLATE_TAG_PAGE_BACKGROUND_COLOR));        			
        		} catch (IllegalArgumentException e) {
        			// nothing
        		}
        	}
        	if (!json.isNull(CommonDef.JSON_TEMPLATE_TAG_PAGE_FOREGROUND_IMG)) {
        		fgName = json.getString(CommonDef.JSON_TEMPLATE_TAG_PAGE_FOREGROUND_IMG);
        	}        	
            if (bgName!=null) template.setBackground(bgName);
            if (bgColor >= 0) template.setBackgroundColor(bgColor);
            if (fgName!=null) template.setForeground(fgName);
            
            int size = json.getInt(CommonDef.JSON_TEMPLATE_TAG_PAGE_NUM_IMAGES);
            JSONArray icons = json.getJSONArray(CommonDef.JSON_TEMPLATE_TAG_PAGE_IMAGES);

            for (int i = 0; i < size; i++) {
                JSONObject element = icons.getJSONObject(i);
                JSONObject position = element.getJSONObject(CommonDef.JSON_TEMPLATE_TAG_PAGE_IMAGE_POSITION);
                int x = position.getInt(CommonDef.JSON_TEMPLATE_TAG_PAGE_IMAGE_POSITION_X);
                int y = position.getInt(CommonDef.JSON_TEMPLATE_TAG_PAGE_IMAGE_POSITION_Y);
                float rotate = (float) element.getDouble(CommonDef.JSON_TEMPLATE_TAG_PAGE_IMAGE_ROTATION);
                int width = element.getInt(CommonDef.JSON_TEMPLATE_TAG_PAGE_IMAGE_WIDTH);
                int height = element.getInt(CommonDef.JSON_TEMPLATE_TAG_PAGE_IMAGE_HEIGHT);

                ImageLayoutDescription info = new ImageLayoutDescription(x, y, width, height, rotate);
                
                images.add(info);
            }
            template.setItems(images);
        } catch (JSONException e) {
            Log.d("PictureStreamFormatFactory", "Fail to parse JSON : " + e.toString());
        }
        
        return template;
    }
    
    public Template getTemplate(int numImagesInPage) {
    	ArrayList<Template> list = mTemplatePool[numImagesInPage-1];
    	
    	if (list == null) return null;
    	
    	Template tmp = list.get(new Random().nextInt(list.size()));
    	Template result = new Template();
    	
    	result.setBackground(tmp.getBackground());
    	if (tmp.getBackgroundColor() >= 0) {
    		result.setBackgroundColor(tmp.getBackgroundColor());
    	}
    	result.setForeground(tmp.getForeground());
    	
    	ArrayList<ImageLayoutDescription> layout = new ArrayList<ImageLayoutDescription>(tmp.getItemNum());
    	for (int i = 0; i < tmp.getItemNum(); i++) {
    		ImageLayoutDescription info = tmp.getItems().get(i);
    		layout.add(new ImageLayoutDescription(info.mX, 
    												info.mY,
    												info.mWidth,
    												info.mHeight,
    												info.mRotateDegree));
    	}
    	result.setItems(layout);    	
    	
    	return result;
    }
    
}
