package com.happypig.picstream;

import java.util.ArrayList;

public class Template {
    private String mFgImageName, mBgImageName;
    private int mBgColor = -1;
    
	private int mNumItems;    	
	private ArrayList<ImageLayoutDescription> mItems;    
	
	public Template() {
		
	}
	
	public Template(ArrayList<ImageLayoutDescription> items) {
		mNumItems = items.size();
		mItems = items;
	}    	
	
    public void setForeground(String fgName) {
    	mFgImageName = fgName;
    }
    
    public String getForeground() {
    	return mFgImageName;
    }
    
    public void setBackground(String bgName) {
    	mBgImageName = bgName;
    }
    
    public String getBackground() {
    	return mBgImageName;
    }
    
    public void setBackgroundColor(int color) {
    	mBgColor = color;
    }
    
    public int getBackgroundColor() {
    	return mBgColor;
    }    	
    
    public void setItems(ArrayList<ImageLayoutDescription> items) {
    	mNumItems = items.size();
    	mItems = items;
    }
    public int getItemNum() {
    	return mNumItems;
    }
    public ArrayList<ImageLayoutDescription> getItems() {
    	return mItems;
    }
} 