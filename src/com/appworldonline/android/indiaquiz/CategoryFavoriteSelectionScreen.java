package com.appworldonline.android.indiaquiz;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.appworldonline.android.indiaquiz.QuestionTemplate.DoneBroadcastReceiver;
import com.appworldonline.android.indiaquiz.lib.ApplicationConstants;
import com.appworldonline.android.indiaquiz.lib.CategoryDetails;
import com.appworldonline.android.indiaquiz.lib.CategoryDBAdapter;
import com.appworldonline.android.indiaquiz.lib.FavoriteDetails;
import com.appworldonline.android.indiaquiz.lib.FavoriteDBAdapter;
import com.appworldonline.android.indiaquiz.lib.MyLogger;
import com.appworldonline.android.indiaquiz.lib.PersistenceStorage;
import com.appworldonline.android.indiaquiz.lib.QuestionsDBAdapter;
import com.appworldonline.android.indiaquiz.uicomponents.CustomImageButton;
import com.appworldonline.android.indiaquiz.uicomponents.TwoWayTabChangeListener;
import com.appworldonline.android.indiaquiz.uicomponents.TwoWayTabControl;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class CategoryFavoriteSelectionScreen extends Activity {
	
	private TwoWayTabControl tabcontrol;
	private MyTwoWayTabChangeListener tabchangelistener;
	private ListView categoryListView;
	private ListView favoritesListView;
	private CustomImageButton addNewFavoriteButton;
	private String current_date;
	
	//private CategoryDBAdapter categoryDBAdapter;
	private FavoriteDBAdapter favoriteDBAdapter;
	private CategoriesAdapter categoriesAdapter;
	private FavoritesAdapter favoritesAdapter;
	private CustomImageButton refreshButton;
	private MyOnItemLongClickListener myOnItemLongClickListener;
	private CategoryOnItemLongClickListener categoryOnItemLongClickListener;
	private TextView hinttext;
	
	private ButtonsClickListener buttonsClickListener;
	private CategoryOnItemClickListener categoryOnItemClickListener;
	private FavoriteOnItemClickListener favoriteOnItemClickListener;
	
	private int category_row_id_for_long_click = 0;
	
	DoneBroadcastReceiver dbr;
	Vector<CategoryDetails> categoryListing;
	Vector<FavoriteDetails> favoritesListing;
	IntentFilter intentfilter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		buttonsClickListener = new ButtonsClickListener();
		myOnItemLongClickListener = new MyOnItemLongClickListener();
		
		dbr = new DoneBroadcastReceiver();
		intentfilter = new IntentFilter();
		intentfilter.addAction("DoneCategoryLoading");
		intentfilter.addAction("DoneFavoriteLoading");
		intentfilter.addAction("DoneLoading");
		registerReceiver(dbr, intentfilter);
		setContentView(R.layout.categoryselection);
		
		hinttext = (TextView)findViewById(R.id.hinttext);
		categoriesAdapter = new CategoriesAdapter();
		favoritesAdapter = new FavoritesAdapter();
		tabcontrol = (TwoWayTabControl)findViewById(R.id.tabcontrol);
		tabchangelistener = new MyTwoWayTabChangeListener();
		tabcontrol.addTwoWayTabChangeListener(tabchangelistener);
		
		refreshButton = (CustomImageButton)findViewById(R.id.refreshbutton);
		refreshButton.setOnClickListener(buttonsClickListener);
		
		categoryListView = (ListView)findViewById(R.id.categoriesList);
		favoritesListView = (ListView)findViewById(R.id.favoritesList);
		addNewFavoriteButton = (CustomImageButton)findViewById(R.id.addFavoriteControl);
		addNewFavoriteButton.setOnClickListener(buttonsClickListener);
		
		
		
		categoryOnItemClickListener = new CategoryOnItemClickListener();
		categoryOnItemLongClickListener = new CategoryOnItemLongClickListener();
		categoryListView.setAdapter(categoriesAdapter);
		categoryListView.setOnItemClickListener(categoryOnItemClickListener);
		categoryListView.setOnItemLongClickListener(categoryOnItemLongClickListener);
		
		favoritesListView.setAdapter(favoritesAdapter);
		favoritesListView.setLongClickable(true);
		favoriteOnItemClickListener = new FavoriteOnItemClickListener();
		favoritesListView.setOnItemClickListener(favoriteOnItemClickListener);
		favoritesListView.setOnItemLongClickListener(myOnItemLongClickListener);
		
		arrangeItemsOnScreenForSelectedIndex(tabcontrol.getSelectedIndex());
		String lastUpdatedDateCalled = PersistenceStorage.getInstance(getApplicationContext()).getCategoryLastUpdatedDate();
		Date date = new Date();
		current_date = date.getDate()+"/"+(date.getMonth()+1)+"/"+date.getYear();
		boolean updateRequiredForDate = false;
		if(!current_date.equals(lastUpdatedDateCalled)){
			updateRequiredForDate = true;
		}
		//categoryDBAdapter = new CategoryDBAdapter(getApplicationContext());
		if(!updateRequiredForDate)
			categoryListing = CategoryDBAdapter.getCategoryListFromLocalDB(getApplicationContext());
		if(updateRequiredForDate || categoryListing == null || categoryListing.size() == 0){
			try {
				CategoryDBAdapter.getInstance().fetchCategoriesFromServer(getApplicationContext(), ApplicationConstants.CATEGORY_DETAILS_URL);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//favoriteDBAdapter = new FavoriteDBAdapter(getApplicationContext());
		favoritesListing = FavoriteDBAdapter.getFavoriteListFromLocalDB(getApplicationContext());
		//favoriteDBAdapter.updateFavoriteList();
	}
	
	
	protected void arrangeItemsOnScreenForSelectedIndex(int _index){
		switch(_index){
			case 0:
				addNewFavoriteButton.setVisibility(View.GONE);
				
				refreshButton.setVisibility(View.VISIBLE);
				favoritesListView.setVisibility(View.GONE);
				
				categoryListView.setVisibility(View.VISIBLE);
				hinttext.setText("(Choose from the list below to start Quiz.)");
				break;
			case 1:
				addNewFavoriteButton.setVisibility(View.VISIBLE);
				
				refreshButton.setVisibility(View.GONE);
				favoritesListView.setVisibility(View.VISIBLE);
				
				categoryListView.setVisibility(View.GONE);
				hinttext.setText("(You can \"long press\" on favorite item to edit.)");
				break;
		}
	}
	protected void refreshCategoryListing(){
		categoriesAdapter.notifyDataSetChanged();
	}
	public void finish(){
		super.finish();
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		String laskitwithonetimemessage = PersistenceStorage.getInstance(getApplicationContext()).getLastKitInWhichOneTimeMessageWasShown();
		PackageInfo pinfo = null;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = pinfo.versionName;
			if(!versionName.equals(laskitwithonetimemessage)){
				PersistenceStorage.getInstance(getApplicationContext()).setLastKitInWhichOneTimeMessageWasShown(versionName);
				Intent onetimemessage = new Intent(getApplicationContext(), CustomPopup.class);
				onetimemessage.putExtra("statement", ApplicationConstants.ONE_TIME_MESSAGE);
				startActivity(onetimemessage);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(dbr, intentfilter);
		QuestionsLists.getInstance().clearAttemptedQuestions();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		try{
			unregisterReceiver(dbr);
		}catch(Exception e){
			
		}
	}
	class CategoriesAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(categoryListing==null)
				return 0;
			else
				return categoryListing.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			if(categoryListing==null)
				return null;
			else
				return categoryListing.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if(categoryListing==null)
				return null;
			
			CategoryDetails cd = (CategoryDetails)getItem(arg0);
			String value = cd.getName();
			
			RelativeLayout rl = null;
			TextView text = null;
			TextView subtext = null;
			ImageView backgroundImage = null;
			ImageView lockImage = null;
			if(arg1 == null){
				LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rl = (RelativeLayout)inflater.inflate(R.layout.categorylistitem, null);
			}else{
				rl = (RelativeLayout)arg1;
			}
			text = (TextView)rl.findViewById(R.id.categoryListingText);
			subtext = (TextView)rl.findViewById(R.id.categoryListingSubText);
			backgroundImage = (ImageView)rl.findViewById(R.id.normal_bg);
			lockImage = (ImageView)rl.findViewById(R.id.lock_image);
			if(arg0%2==1){
				backgroundImage.setBackgroundResource(R.drawable.green_mag);
			}
			else{
				backgroundImage.setBackgroundResource(R.drawable.white_mag);
			}
			backgroundImage.setVisibility(View.INVISIBLE);
			if(cd.isBlocked()){
				lockImage.setVisibility(View.VISIBLE);
			}else{
				lockImage.setVisibility(View.INVISIBLE);
			}
			text.setText(value);
			if(cd.isBlocked()){
				subtext.setText("This is a locked category.");
			}else{
				if ( QuestionsDBAdapter.getInstance().getAllURLsFetchedForCategory(getApplicationContext(), (int)cd.getRowID()).size()< cd.getUrls().size()){
					subtext.setText("New Questions are available for download.");
				}else{
					subtext.setText("Questions List saved locally is up to date.");
				}
			}
			return rl;
		}
		
	}
	class FavoritesAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(favoritesListing == null)
				return 0;
			else
				return favoritesListing.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			if(favoritesListing==null)
				return null;
			else
				return favoritesListing.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if(favoritesListing == null)
				return null;
			else{
				FavoriteDetails fd = favoritesListing.get(arg0); 
				String value = fd.getName();
				long[] categoryRowIDs = fd.getAssociatedCategories();
				StringBuffer subtextValue = new StringBuffer();
				for(int i = 0; categoryRowIDs!=null && i < categoryRowIDs.length; i++){
					CategoryDetails cd = CategoryDBAdapter.getCategoryDetails(getApplicationContext(), categoryRowIDs[i]);
					if(subtextValue.length()>0){
						subtextValue.append(", ");
					}
					subtextValue.append(cd.getName());
				}
				LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				RelativeLayout rl = (RelativeLayout)inflater.inflate(R.layout.favoritelistitem, null);
				TextView text = (TextView)rl.findViewById(R.id.favoriteListingText);
				TextView subtext = (TextView)rl.findViewById(R.id.favoriteListingSubText);
				ImageView backgroundImage = (ImageView)rl.findViewById(R.id.normal_bg);
				
				if(arg0%2==1){
					backgroundImage.setBackgroundResource(R.drawable.blue_mag);
					
				}
				else{
					backgroundImage.setBackgroundResource(R.drawable.white_mag);
					
				}
				
				backgroundImage.setVisibility(View.INVISIBLE);
				
				
				text.setText(value);
				subtext.setText(subtextValue.toString());
				return rl;
			}
		}
		
	}
	class FavoriteOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Log.d("FavoriteOnItemClickListener", "Clicked on favorite: "+arg2);
			FavoriteDetails fd = (FavoriteDetails)favoritesAdapter.getItem(arg2);
			String favoriteName = fd.getName();
			Log.d("FavoriteOnItemClickListener", "Clicked on favorite: "+favoriteName);
			try{
				unregisterReceiver(dbr);
			}catch(Exception e){
				
			}
			Intent intent = new Intent(CategoryFavoriteSelectionScreen.this.getApplicationContext(), QuestionTemplate.class);
			intent.putExtra("Title", favoriteName);
			
			//ArrayList<String> urls = new ArrayList<String>();
			ArrayList<Integer> category_ids = new ArrayList<Integer>();
			
			long[] categoryRowIDs = fd.getAssociatedCategories();
			for(int i = 0 ; categoryRowIDs!=null && i < categoryRowIDs.length; i++){
				CategoryDetails cd = CategoryDBAdapter.getCategoryDetails(getApplicationContext(), categoryRowIDs[i]);
				//urls.add(cd.getUrls().get(0));
				category_ids.add(new Integer((int)categoryRowIDs[i]));
			}
			//
			//intent.putExtra("urls", urls);
			intent.putExtra("category_ids", category_ids);
			startActivity(intent);
			//finish();
		}
		
	}
	class CategoryOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			CategoryDetails cd = (CategoryDetails)categoriesAdapter.getItem(arg2);
			String categoryName = cd.getName();
			boolean blocked = cd.isBlocked();
			if(blocked){
				Toast.makeText(getApplicationContext(), "Sorry, you can't select a locked category.", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent i = new Intent(CategoryFavoriteSelectionScreen.this.getApplicationContext(), QuestionTemplate.class);
			i.putExtra("Title", categoryName);
			//ArrayList<String> urls = new ArrayList<String>();
			ArrayList<Integer> category_ids = new ArrayList<Integer>();
			Log.d("the following url will be fetched: ", "url: "+cd.getUrls().get(0));
			//urls.add(cd.getUrls().get(0));
			category_ids.add(new Integer((int)cd.getRowID()));
			//i.putExtra("urls", urls);
			i.putExtra("category_ids", category_ids);
			try{
				unregisterReceiver(dbr);
			}catch(Exception e){
				
			}
			startActivity(i);
			//finish();
		}
		
	}
	class CategoryOnItemLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			CategoryDetails cd = (CategoryDetails)categoriesAdapter.getItem(arg2);
			if(cd.isBlocked()){
				Intent blockedCategory = new Intent(getApplicationContext(), CustomPopup.class);
				blockedCategory.putExtra("statement", "This is a blocked category.\n\nCan not fetch questions for a blocked category.");
				startActivity(blockedCategory);
				return true;
			}
			category_row_id_for_long_click = (int)cd.getRowID();
			Intent confirmationPopup = new Intent(CategoryFavoriteSelectionScreen.this.getApplicationContext(), ConfirmationPopup.class);
			confirmationPopup.putExtra("statement", "This will fetch all questions for category \""+cd.getName()+"\" from the server.\nDo you want to continue?");
			startActivityForResult(confirmationPopup, 561237);
			return true;
		}
		
	}
	class MyTwoWayTabChangeListener implements TwoWayTabChangeListener{

		@Override
		public void tabChanged(int index) {
			// TODO Auto-generated method stub
			CategoryFavoriteSelectionScreen.this.arrangeItemsOnScreenForSelectedIndex(index);
		}
		
	}
	class DoneBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, final Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("DoneCategoryLoading")){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						if(intent.getExtras().getBoolean("SUCCESSFUL")){
							PersistenceStorage.getInstance(getApplicationContext()).setCategoryLastUpdatedDate(current_date);
							categoryListing = CategoryDBAdapter.getCategoryListFromLocalDB(getApplicationContext());
							refreshCategoryListing();
						}else{
							categoryListing = CategoryDBAdapter.getCategoryListFromLocalDB(getApplicationContext());
							refreshCategoryListing();
							Intent networkIssue = new Intent(getApplicationContext(), CustomPopup.class);
							if(categoryListing==null || categoryListing.size() == 0)
								networkIssue.putExtra("statement", "There was a problem fetching category list from the server.\n\nPlease check your internet connection and try later.");
							else
								
								networkIssue.putExtra("statement", "There was a problem updating category list from the server.\n\nPlease check your internet connection.");
							startActivityForResult(networkIssue, 502);
						}
					}
				});
				
			}
			if(intent.getAction().equals("DoneFavoriteLoading")){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(favoritesAdapter!=null){
							favoritesAdapter.notifyDataSetChanged();
						}
					}
				});
			}
			if(intent.getAction().equals("DoneLoading"))
			{
				if(intent.getExtras().getBoolean("SUCCESSFUL"))
				{
					int categoryRowID = intent.getExtras().getInt("category_row_id");
					String urlLoaded = intent.getExtras().getString("category_url_loaded");
					
					if(urlLoaded!=null)
					{
						QuestionsDBAdapter.getInstance().fetchFromServerAndSaveLocally(getApplicationContext(), categoryRowID);
					}else{
						refreshCategoryListing();
						int categoryRowID2 = intent.getExtras().getInt("category_row_id");
						CategoryDetails cds = CategoryDBAdapter.getCategoryDetails(getApplicationContext(), categoryRowID2);
						String categoryName = cds.getName();
						Intent successfulAttempt = new Intent(getApplicationContext(), CustomPopup.class);
						successfulAttempt.putExtra("statement", "The questions for category \""+categoryName+"\" have been loaded successfully.");
						startActivity(successfulAttempt);
					}
				}else
				{
					int categoryRowID2 = intent.getExtras().getInt("category_row_id");
					CategoryDetails cds = CategoryDBAdapter.getCategoryDetails(getApplicationContext(), categoryRowID2);
					String categoryName = cds.getName();
					Intent networkIssue = new Intent(getApplicationContext(), CustomPopup.class);
					networkIssue.putExtra("statement", "There was a problem pulling all questions for category \""+categoryName+"\" from the server.\n\nPlease check your internet connection.");
					startActivityForResult(networkIssue, 502);
				}
			}
		}
		
	}
	class MyOnItemLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			FavoriteDetails fd = (FavoriteDetails)favoritesAdapter.getItem(arg2);
			Intent i = new Intent(CategoryFavoriteSelectionScreen.this.getApplicationContext(), FavoriteCreationScreen.class);
			i.putExtra("FAVROWID", fd.getRowID());
			startActivityForResult(i, 401);
			return true;
		}
		
	}
	
	class ButtonsClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.refreshbutton:
				Intent confirmationPopup = new Intent(CategoryFavoriteSelectionScreen.this.getApplicationContext(), ConfirmationPopup.class);
				confirmationPopup.putExtra("statement", "This will update your categories list from the server.\nDo you want to continue?");
				startActivityForResult(confirmationPopup, 123);
				
				
				break;
			case R.id.addFavoriteControl:
				Intent i = new Intent(CategoryFavoriteSelectionScreen.this.getApplicationContext(), FavoriteCreationScreen.class);
				startActivityForResult(i, 401);
				break;
			default:
				break;
			}
			
		}
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 401:
			favoritesListing = favoriteDBAdapter.getFavoriteListFromLocalDB(getApplicationContext());
			if(favoritesAdapter!=null){
				favoritesAdapter.notifyDataSetChanged();
			}
			break;
		case 561237:
			switch(resultCode){
			case 0:
				category_row_id_for_long_click = 0;
				break;
			case 1:
				QuestionsDBAdapter.getInstance().fetchFromServerAndSaveLocally(getApplicationContext(), category_row_id_for_long_click);
				category_row_id_for_long_click = 0;
				break;
			
			}
			break;
		case 123:
			switch(resultCode){
			case 1:
				
					try {
						CategoryDBAdapter.getInstance().fetchCategoriesFromServer(getApplicationContext(), ApplicationConstants.CATEGORY_DETAILS_URL);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				break;
			}
		}
		
	}
}
