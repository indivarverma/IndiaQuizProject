package com.appworldonline.android.indiaquiz;

import java.util.Vector;

import com.appworldonline.android.indiaquiz.lib.QuestionsDBAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ResultsScreen extends Activity implements OnClickListener{
	TextView quizname;
	ListView resultsList;
	Vector leftLabels;
	ResultsAdapter resultsAdapter;
	int totalQuestionCount;
	int scoreValue;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	leftLabels = new Vector<String>();
	fillLeftLabels();
	resultsAdapter = new ResultsAdapter();
	totalQuestionCount = getIntent().getExtras().getInt("TOTALQUESTIONSATTEMPTED");
	scoreValue = getIntent().getExtras().getInt("SCORE");
	String quiznametext = getIntent().getExtras().getString("QUIZNAME");
	
	setContentView(R.layout.resultspage);
	quizname = (TextView)findViewById(R.id.quizname);
	resultsList = (ListView)findViewById(R.id.resultslist);
	resultsList.setAdapter(resultsAdapter);
	quizname.setText(quiznametext);
	Button tryAgain = (Button)findViewById(R.id.tryagain);
	tryAgain.setOnClickListener(this);
	Button reviewQuiz = (Button)findViewById(R.id.reviewbutton);
	reviewQuiz.setOnClickListener(this);
	
}
	private void fillLeftLabels(){
		leftLabels.add("Total Questions Attempted");
		leftLabels.add("Questions correctly answered");
		leftLabels.add("Wrong answers");
		leftLabels.add("Accuracy (%)");
	}


	class ResultsAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return leftLabels.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return leftLabels.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			TextView lefttext = null;
			TextView righttext = null;
			RelativeLayout rl = null;
			if(arg1 == null){
				LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rl = (RelativeLayout)inflater.inflate(R.layout.resultlistitem, null);
			}else{
				rl = (RelativeLayout)arg1;
			}
			lefttext = (TextView)rl.findViewById(R.id.resultleftlabel);
			righttext = (TextView)rl.findViewById(R.id.resultrightlabel);
			lefttext.setText((String)getItem(arg0));
			switch(arg0){
			case 0:
				righttext.setText(String.valueOf(totalQuestionCount));
				break;
			case 1:
				righttext.setText(String.valueOf(scoreValue));
				break;
			case 2:
				righttext.setText(String.valueOf(totalQuestionCount - scoreValue));
				break;
			case 3:
				righttext.setText(scoreValue*100/totalQuestionCount+"%");
				break;
			}
			return rl;
		}
		
	}
@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch(v.getId()){
	case R.id.tryagain:
		/*Intent i = new Intent(ResultsScreen.this.getApplicationContext(), CategoryFavoriteSelectionScreen.class);
		startActivity(i);*/
		finish();
		break;
	case R.id.reviewbutton:
		Intent i = new Intent(ResultsScreen.this.getApplicationContext(), ReviewQuizActivity.class);
		startActivity(i);
		finish();
		break;
	}
}
}
