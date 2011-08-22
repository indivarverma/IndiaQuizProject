package com.appworldonline.android.indiaquiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmationPopup extends Activity {
	Button okbutton, cancelbutton;
	MyOnClickListener clicklistener;
	String statement;
	TextView statementView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(getIntent().getExtras()!=null)
			statement = getIntent().getExtras().getString("statement");
		clicklistener = new MyOnClickListener();
		setContentView(R.layout.exitconfirmationpopup);
		statementView = (TextView)findViewById(R.id.statement);
		if(statement!=null){
			statementView.setText(statement);
		}
		okbutton = (Button)findViewById(R.id.okbutton);
		cancelbutton = (Button)findViewById(R.id.cancelbutton);
		okbutton.setOnClickListener(clicklistener);
		cancelbutton.setOnClickListener(clicklistener);
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
			case R.id.cancelbutton:
				setResult(-1);
				finish();
				break;
			}
		}
		
	}
}
