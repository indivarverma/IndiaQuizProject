package com.appworldonline.android.indiaquiz.lib;

import java.util.Vector;

public class CategoryDetails {
	private String name;
	private boolean blocked;
	private long rowID;
	private Vector<String> urls;
	public long getRowID() {
		return rowID;
	}
	public void setRowID(long rowID) {
		this.rowID = rowID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isBlocked() {
		return blocked;
	}
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	public Vector<String> getUrls() {
		return urls;
	}
	public void setUrls(Vector<String> url) {
		this.urls = url;
	}
	public void addUrl(String url){
		if(url == null) return;
		if(urls==null){
			urls = new Vector<String>();
		}
		urls.add(url.trim());
	}
}
