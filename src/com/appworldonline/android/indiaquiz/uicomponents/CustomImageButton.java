package com.appworldonline.android.indiaquiz.uicomponents;

import com.appworldonline.android.indiaquiz.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

public class CustomImageButton extends ImageButton {
	private int normalRes;
	private int selectedRes;
	private boolean selected = false;
	public CustomImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public CustomImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		// TODO Auto-generated constructor stub
	}
	public CustomImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
		// TODO Auto-generated constructor stub
	}
	private void init(AttributeSet attrs){
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomImageButton);
		normalRes = a.getResourceId(R.styleable.CustomImageButton_res_normal, -1);
		selectedRes = a.getResourceId(R.styleable.CustomImageButton_res_selected, -1);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(selected)
			setBackgroundResource(selectedRes);
		else
			setBackgroundResource(normalRes);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			selected = true;
			invalidate();
			break;
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			selected = false;
			invalidate();
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

}
