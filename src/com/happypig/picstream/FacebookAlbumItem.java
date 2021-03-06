package com.happypig.picstream;

public class FacebookAlbumItem{
	private String id;
	private String name;
	private String description;
	
	public FacebookAlbumItem(String id){
		this.id=id;		
	}	
	public FacebookAlbumItem(String id,String name){
		this.id=id;
		this.name=name;		
	}
	public FacebookAlbumItem(String id,String name, String desc){
		this.id=id;
		this.name=name;
		this.description=desc;
	}
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id=id;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name=name;
	}
	public String getDescription(){
		return description;
	}
	public void setDescription(String description){
		this.description=description;
	}
	@Override
	public String toString(){
		//returning name because that is what will be displayed in the Spinner control
		return(this.name);
	}
}
