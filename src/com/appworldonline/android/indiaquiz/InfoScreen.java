package com.appworldonline.android.indiaquiz;

import com.appworldonline.android.indiaquiz.lib.MyLogger;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class InfoScreen extends Activity {
	private TextView loadingText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		WebView.enablePlatformNotifications();
		setContentView(R.layout.infoscreen);
		loadingText = (TextView)findViewById(R.id.loadingText);
		
		
		Bundle ex = getIntent().getExtras();
		String link = ex.getString("LINK");
		link = link.trim();
		final WebView wv = (WebView)findViewById(R.id.webView);
		wv.setWebChromeClient(new WebChromeClient());
		
		WebView.enablePlatformNotifications();
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setBlockNetworkLoads(false);
		wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		
		wv.setWebViewClient(new WebViewClient() {
	        /*@Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        	view.loadUrl(url);
	        	   return true;
	        }*/
	        @Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				MyLogger.d("loaded url", wv.getOriginalUrl());
				if(loadingText!=null)
				loadingText.setVisibility(View.INVISIBLE);
			}
	    });
		wv.loadUrl(link);
		
	}
}
