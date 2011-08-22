package com.appworldonline.android.indiaquiz.lib;

import java.util.Vector;

public class FavoriteDetails {
	private long rowID;
	private String name;
	private long[] associatedCategories;
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
	public long[] getAssociatedCategories() {
		return associatedCategories;
	}
	public void setAssociatedCategories(long[] associatedCategories) {
		this.associatedCategories = associatedCategories;
	}
	
}
