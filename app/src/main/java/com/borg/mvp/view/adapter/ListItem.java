package com.borg.mvp.view.adapter;

public class ListItem {

	public static final int TYPE_TEXT = 0;
	public static final int TYPE_EDIT = 1;
	public static final int TYPE_BUTTON = 2;
	public static final int TYPE_COUNT = 3;

	private String name;
	private int type;

	public ListItem(int type, String name) {
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}
}
