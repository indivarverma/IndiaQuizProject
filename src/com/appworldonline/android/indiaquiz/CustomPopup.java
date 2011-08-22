package com.appworldonline.android.indiaquiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CustomPopup extends Activity {
	Button okbutton;
	MyOnClickListener clicklistener;
	String statement;
	TextView statementView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		clicklistener = new MyOnClickListener();
		setContentView(R.layout.custompopup);
		if(getIntent().getExtras()!=null)
			statement = getIntent().getExtras().getString("statement");
		statementView = (TextView)findViewById(R.id.statement);
		statementView.setText(statement);
		okbutton = (Button)findViewById(R.id.okbutton);
		
		okbutton.setOnClickListener(clicklistener);
		
	}
	class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.okbutton:
				setResult(1);
				finish();
				break;
			}
		}
		
	}
}
