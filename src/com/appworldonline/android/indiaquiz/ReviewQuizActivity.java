package com.appworldonline.android.indiaquiz;

import com.appworldonline.android.indiaquiz.lib.QuestionDetails;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ReviewQuizActivity extends Activity {
	private ListView questionAnswerListView;
	private QuestionAnswerListAdapter questionAnswerListAdapter;
	private QuestionAnswerOnItemClickListener questionAnswerOnItemClickListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review_quiz);
		
		questionAnswerListAdapter = new QuestionAnswerListAdapter();
		
		questionAnswerListView = (ListView)findViewById(R.id.reviewlist);
		questionAnswerListView.setAdapter(questionAnswerListAdapter);
		questionAnswerOnItemClickListener = new QuestionAnswerOnItemClickListener();
		questionAnswerListView.setOnItemClickListener(questionAnswerOnItemClickListener);
	}
	class QuestionAnswerOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			QuestionDetails qds = QuestionsLists.getInstance().getAttemptedQuestionsDetails(position);
			String link = qds.getLink();
			Intent myIntent = new Intent(getApplicationContext(), InfoScreen.class);
			myIntent.putExtra("LINK", link);
			startActivity(myIntent);
		}
		
	}
	class QuestionAnswerListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return QuestionsLists.getInstance().getAttemptedQuestionsCount();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return QuestionsLists.getInstance().getAttemptedQuestionsDetails(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return QuestionsLists.getInstance().getAttemptedQuestionsDetails(position).getQuestionID();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView == null){
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.questionanswerdetail, null);
			}
			TextView questionText = (TextView)convertView.findViewById(R.id.questiontext);
			TextView usersanswer = (TextView)convertView.findViewById(R.id.usersanswer);
			TextView realanswer = (TextView)convertView.findViewById(R.id.realanswer);
			TextView counter = (TextView)convertView.findViewById(R.id.questionCounter);
			counter.setText(String.valueOf(position+1));
			QuestionDetails qds = QuestionsLists.getInstance().getAttemptedQuestionsDetails(position);
			questionText.setText(qds.getQuestion());
			int usersAnswerIndex = QuestionsLists.getInstance().getAttemptedQuestionsAnswer(position).intValue();
			if(usersAnswerIndex == qds.getRightAnswer()){
				usersanswer.setTextColor(Color.GREEN);
			}else{
				usersanswer.setTextColor(Color.RED);
			}
			usersanswer.setText(qds.getAnswers().get(usersAnswerIndex-1));
			realanswer.setText(qds.getAnswers().get(qds.getRightAnswer()-1));
			
			return convertView;
		}
		
	}

}
