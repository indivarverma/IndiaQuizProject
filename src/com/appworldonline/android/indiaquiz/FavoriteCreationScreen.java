package com.appworldonline.android.indiaquiz;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.appworldonline.android.indiaquiz.lib.ApplicationConstants;
import com.appworldonline.android.indiaquiz.lib.CategoryDetails;
import com.appworldonline.android.indiaquiz.lib.CategoryDBAdapter;
import com.appworldonline.android.indiaquiz.lib.FavoriteDetails;
import com.appworldonline.android.indiaquiz.lib.FavoriteDBAdapter;
import com.appworldonline.android.indiaquiz.uicomponents.CustomImageButton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FavoriteCreationScreen extends Activity {
	//private CategoryDBAdapter categoryList;
	private ListView categoryListView;
	private EditText favoritename;
	private CategoriesAdapter categoriesAdapter;
	Vector selectedCategories;
	CategorySelectionListener categorySelectionListener;
	FavoriteCreationClickListener favoriteCreationClickListener;
	FavoriteDetails favoriteDetails;
	CustomImageButton deleteButton;
	TextView headertext;
	CustomImageButton okbutton;
	CustomImageButton okbuttoncenter;
	long favoriteRowID;
	private int mode;
	private static final int NEW_MODE = 1;
	private static final int EDIT_MODE = 2;
	Vector<CategoryDetails> categories;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		
		setContentView(R.layout.favoritecreation);
		headertext = (TextView)findViewById(R.id.headertext);
		favoritename = (EditText)findViewById(R.id.favoritename);
		categoriesAdapter = new CategoriesAdapter();
		categoryListView = (ListView)findViewById(R.id.categoriesList);
		categoryListView.setAdapter(categoriesAdapter);
		selectedCategories = new Vector();
		//categoryList = new CategoryDBAdapter(this);
		categorySelectionListener = new CategorySelectionListener();
		categoryListView.setOnItemClickListener(categorySelectionListener);
		favoriteCreationClickListener = new FavoriteCreationClickListener();
		okbutton = (CustomImageButton)findViewById(R.id.okbutton);
		okbuttoncenter = (CustomImageButton)findViewById(R.id.okbuttoncenter);
		deleteButton = (CustomImageButton)findViewById(R.id.deletebutton);
		
		
		deleteButton.setOnClickListener(favoriteCreationClickListener);
		okbutton.setOnClickListener(favoriteCreationClickListener);
		okbuttoncenter.setOnClickListener(favoriteCreationClickListener);
		
		Intent launchintent = getIntent();
		favoriteRowID = launchintent.getLongExtra("FAVROWID", -1);
		if(favoriteRowID!=-1){
			mode = FavoriteCreationScreen.EDIT_MODE;
			okbuttoncenter.setVisibility(View.GONE);
			//FavoriteDBAdapter favlist = new FavoriteDBAdapter(getApplicationContext());
			FavoriteDetails fd = FavoriteDBAdapter.getFavoriteDetailsForRowID(getApplicationContext(), favoriteRowID);
			favoritename.setText(fd.getName());
			long[] associatedCategories = fd.getAssociatedCategories();
			for(int i = 0; associatedCategories!=null && i < associatedCategories.length; i++){
				selectedCategories.add(new Long(associatedCategories[i]));
			}
			String textToShow = fd.getName();
			if(textToShow!=null && textToShow.length()>15){
				textToShow = textToShow.substring(0, 15).concat("..");
			}
			headertext.setText("Editing \""+textToShow+"\" group");
		}else{
			mode = FavoriteCreationScreen.NEW_MODE;
			deleteButton.setVisibility(View.GONE);
			okbutton.setVisibility(View.GONE);
			headertext.setText("Creating a new favorite group");
		}
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		categories = CategoryDBAdapter.getCategoryListFromLocalDB(getApplicationContext());
		if(categories == null || categories.size() == 0){
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
	}
	class CategoriesAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(categories==null)
				return 0;
			else
				return categories.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			if(categories==null)
				return null;
			else
				return categories.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if(categories==null)
				return null;
			
			CategoryDetails cd = (CategoryDetails)getItem(arg0);
			String value = cd.getName();
			
			RelativeLayout rl = null;
			TextView text = null;
			ImageView backgroundImage = null;
			ImageView checkboxImage = null;
			ImageView lockImage = null;
			if(arg1 == null){
				LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rl = (RelativeLayout)inflater.inflate(R.layout.categoryselectionlistitem, null);
			}else{
				rl = (RelativeLayout)arg1;
			}
			text = (TextView)rl.findViewById(R.id.categoryListingText);
			backgroundImage = (ImageView)rl.findViewById(R.id.normal_bg);
			checkboxImage = (ImageView)rl.findViewById(R.id.checkbox_image);
			if(selectedCategories.contains(new Long(cd.getRowID()))){
				checkboxImage.setImageResource(R.drawable.catagory_checkbox);
			}else{
				checkboxImage.setImageResource(R.drawable.catagory_blankcheckbox);
			}
			lockImage = (ImageView)rl.findViewById(R.id.lock_image);
			if(arg0%2==1){
				backgroundImage.setBackgroundResource(R.drawable.green_mag);
			}
			else{
				backgroundImage.setBackgroundResource(R.drawable.white_mag);
			}
			backgroundImage.setVisibility(View.INVISIBLE);
			text.setText(value);
			if(cd.isBlocked()){
				lockImage.setVisibility(View.VISIBLE);
			}else{
				lockImage.setVisibility(View.INVISIBLE);
			}
			return rl;
		}
		
	}
	class CategorySelectionListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			CategoryDetails selectedCategory = (CategoryDetails)categoriesAdapter.getItem(arg2);
			if(selectedCategory.isBlocked()){
				Toast.makeText(getApplicationContext(), "Sorry, you can't select a locked category.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(selectedCategories.contains(new Long(selectedCategory.getRowID()))){
				selectedCategories.remove(new Long(selectedCategory.getRowID()));
			}else{
				selectedCategories.add(new Long(selectedCategory.getRowID()));
			}
			categoriesAdapter.notifyDataSetChanged();
		}
		
	}
	
	class FavoriteCreationClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.okbutton:
			case R.id.okbuttoncenter:
				if(favoritename.getText().toString().trim().length()==0){
					Toast.makeText(getApplicationContext(), "Please enter name.", Toast.LENGTH_SHORT).show();
					break;
				}
				if(selectedCategories==null || selectedCategories.size()==0){
					Toast.makeText(getApplicationContext(), "Please select at least one category.", Toast.LENGTH_SHORT).show();
					break;
				}
				//FavoriteDBAdapter favlist = new FavoriteDBAdapter(getApplicationContext());
				long[] selectedCats = new long[selectedCategories.size()];
				for(int i = 0 ; i < selectedCategories.size(); i++){
					Long value = (Long)selectedCategories.get(i);
					selectedCats[i] = value.longValue();
				}
				switch(mode){
				case FavoriteCreationScreen.NEW_MODE:
					FavoriteDBAdapter.addNewFavorite(getApplicationContext(), favoritename.getText().toString(), selectedCats);
					break;
				case FavoriteCreationScreen.EDIT_MODE:
					FavoriteDBAdapter.updateFavorite(getApplicationContext(), favoriteRowID, favoritename.getText().toString(), selectedCats);
					break;
				}
				
				finish();
				break;
			case R.id.deletebutton:
				Log.d("favorite creation screen", "delete button was clicked, fav row id: "+favoriteRowID);
				if(favoriteRowID!=-1){
					Intent confirmationPopup = new Intent(FavoriteCreationScreen.this.getApplicationContext(), ConfirmationPopup.class);
					confirmationPopup.putExtra("statement", "This will delete this favorite group.\nDo you want to continue?");
					startActivityForResult(confirmationPopup, 1223);
					
				}
				break;
		}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 1223:
			switch(resultCode){
			case 1:
				//FavoriteDBAdapter favlist2 = new FavoriteDBAdapter(getApplicationContext());
				FavoriteDBAdapter.deleteFavorite(getApplicationContext(), favoriteRowID);
				finish();
				break;
			}
			
			break;
			
		}
	}
		
	}
	/*class MyVector extends Vector{
		@Override
		public boolean contains(Object object) {
			// TODO Auto-generated method stub
				
				Long comparable = (Long)object;
				Log.d("My Vector", "comparable: "+comparable);
				for(int i = 0; i < size(); i++){
					Long element = (Long)this.elementAt(i);
					if(element.longValue() == comparable.longValue()){
						Log.d("My Vector", "comparable: "+comparable+" is present in vector");
						return true;
					}
					
				}
				Log.d("My Vector", "comparable: "+comparable+" is not present in vector");
				return false;
			
		}
	}*/

