package com.appworldonline.android.indiaquiz;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.KeyCharacterMap.KeyData;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class LoadingPage extends Activity{
	ImageView rotatingImage;
	StopLoadingBroadcastReceiver stopLoadingBroadcastReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		stopLoadingBroadcastReceiver = new StopLoadingBroadcastReceiver();
		registerReceiver(stopLoadingBroadcastReceiver, new IntentFilter("StopLoading"));
		setContentView(R.layout.loadingpage);
		rotatingImage = (ImageView)findViewById(R.id.loadingIcon);
		Animation rotation;
		rotation = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.rotation);
		rotatingImage.setAnimation(rotation);
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK) return true;
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		rotatingImage.clearAnimation();
		try{
			unregisterReceiver(stopLoadingBroadcastReceiver);
		}catch(Exception e){
			
		}
		super.finish();
		
	}
	class StopLoadingBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("StopLoading")){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						finish();
					}
				});
				
			}
		}
		
	}
}
