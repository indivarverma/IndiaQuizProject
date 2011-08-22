package com.appworldonline.android.indiaquiz.uicomponents;

import java.util.Vector;

import com.appworldonline.android.indiaquiz.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RemoteViews.ActionException;

public class TwoWayTabControl extends View {
	private String leftLabel;
	private String rightLabel;
	private Drawable leftSelectedDrawable;
	private Drawable rightSelectedDrawable;
	private boolean leftSelected = true;
	private Paint paint;
	private TwoWayTabControlTouchListener twoWayTabControlTouchListener;
	private Vector<TwoWayTabChangeListener> twoWayTabChangeListeners;
	

	public TwoWayTabControl(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TwoWayTabControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(attrs);
	}

	public TwoWayTabControl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(attrs);
	}
	private void init(AttributeSet attrs) {
		
		TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.TwoWayTabControlStyle);
		leftSelectedDrawable = a.getDrawable(R.styleable.TwoWayTabControlStyle_leftItemSelected);
		rightSelectedDrawable = a.getDrawable(R.styleable.TwoWayTabControlStyle_rightItemSelected);
		
		leftLabel = a.getString(R.styleable.TwoWayTabControlStyle_leftLabel);
		rightLabel = a.getString(R.styleable.TwoWayTabControlStyle_rightLabel);
		if(twoWayTabControlTouchListener == null)
			twoWayTabControlTouchListener = new TwoWayTabControlTouchListener();
		setOnTouchListener(twoWayTabControlTouchListener);
		twoWayTabChangeListeners = new Vector<TwoWayTabChangeListener>();
		paint = new Paint();
	}
	
	public void addTwoWayTabChangeListener(TwoWayTabChangeListener twoWayTabChangeListener) {
		this.twoWayTabChangeListeners.add(twoWayTabChangeListener);
	}

	public String getLeftLabel() {
		return leftLabel;
	}

	public void setLeftLabel(String leftLabel) {
		this.leftLabel = leftLabel;
	}

	public String getRightLabel() {
		return rightLabel;
	}

	public void setRightLabel(String rightLabel) {
		this.rightLabel = rightLabel;
	}
	
	public boolean isLeftSelected() {
		return leftSelected;
	}

	public void setLeftSelected(boolean leftSelected) {
		this.leftSelected = leftSelected;
	}
	public int getSelectedIndex(){
		return leftSelected?0:1;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(leftSelected){
			setBackgroundDrawable(leftSelectedDrawable);
		}else{
			setBackgroundDrawable(rightSelectedDrawable);
		}
		
		int areaheight = getHeight();
		int textHeight = (int)paint.getTextSize();
		int labelY = textHeight + (areaheight - textHeight)/2;
		
		int halfwidth = getWidth()/2;
		paint.setAntiAlias(true);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		if(leftSelected){
			paint.setARGB(0xff, 0x00, 0x00, 0x00);
		}else{
			paint.setARGB(0xff, 0x7f, 0x7f, 0x7f);
		}
		if(leftLabel!=null){
			int textWidth = (int)paint.measureText(leftLabel);
			int leftLabelX = (halfwidth - textWidth)/2;
			canvas.drawText(leftLabel, leftLabelX, labelY, paint);
		}
		if(!leftSelected){
			paint.setARGB(0xff, 0x00, 0x00, 0x00);
		}else{
			paint.setARGB(0xff, 0x7f, 0x7f, 0x7f);
		}
		if(rightLabel!=null){
			int textWidth = (int)paint.measureText(rightLabel);
			int rightLabelX = halfwidth + (halfwidth - textWidth)/2;
			canvas.drawText(rightLabel, rightLabelX, labelY, paint);
		}
	}
	private void informTabChangeListeners(int index){
		if(twoWayTabChangeListeners!=null && !twoWayTabChangeListeners.isEmpty()){
			for(int i = 0 ; i < twoWayTabChangeListeners.size(); i++){
				twoWayTabChangeListeners.get(i).tabChanged(index);
			}
		}
	}
	private void findLocationAndSetTab(float x, float y){
		if(x<getWidth()/2){
			if(!leftSelected){
				leftSelected = true;
				invalidate();
				informTabChangeListeners(leftSelected?0:1);
			}
		}else{
			if(leftSelected){
				leftSelected = false;
				invalidate();
				informTabChangeListeners(leftSelected?0:1);
			}
		}
	}
	private class TwoWayTabControlTouchListener implements OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(v == TwoWayTabControl.this){
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						findLocationAndSetTab(event.getX(), event.getY());
						break;
				}
			}
			return false;
		}
		
	}
}
