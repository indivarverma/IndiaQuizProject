package com.appworldonline.android.indiaquiz;

import com.appworldonline.android.indiaquiz.uicomponents.CustomImageButton;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LaunchScreen extends Activity {
	private CustomImageButton startButton;
	private MyOnClickListener myOnClickListener; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup);
		startButton = (CustomImageButton)findViewById(R.id.start_button);
		myOnClickListener = new MyOnClickListener();
		startButton.setOnClickListener(myOnClickListener);
	}
	
	class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId()){
			case R.id.start_button:
				Intent i = new Intent(LaunchScreen.this.getApplicationContext(), CategoryFavoriteSelectionScreen.class);
				startActivity(i);
				finish();
				break;
			}
		}

		
		
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		if(startButton!=null){
			startButton.setOnClickListener(null);
			startButton = null;
		}
	}
	
}
