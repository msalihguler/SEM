package com.example.sem_thesis.userpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.sem_thesis.R;
import com.example.sem_thesis.listgames.FindToSeeSinglePage;
import com.example.sem_thesis.listgames.MyCustomAdapter;
import com.example.sem_thesis.listgames.TrekkingOnTheRouteSinglePage;
import com.example.sem_thesis.user.JSONParser;
import com.example.sem_thesis.user.SessionManagement;

public class ListCreatedGames extends SherlockFragment{
	MyCustomAdapter adapter1,adapter;
	ProgressDialog pDialog;
	Spinner spinner;
	ListView listedGames;
	View rootView;
	String category,latitude,longitude,toDeleteId;
	HashMap<String, Integer> user_id;
	SessionManagement session;
	int user_idtoSend;
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> gameList;
	ArrayList<String> arrayToSend;
	AlertDialog dialogToDelete;
	
	
	private static final String GAME_URL = "http://theduman.me/api/get_games_created_by_me";
	private static final String DELETE_URL = "http://theduman.me/api/delete_find_to_see";
	private static final String DELETE_TOTR = "http://theduman.me/api/delete_trekking_on_the_route";

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.list_totr, container, false);
		
		gameList = new ArrayList<HashMap<String,String>>();
		
		
		spinner = (Spinner) rootView.findViewById(R.id.categories);
		listedGames=(ListView)rootView.findViewById(R.id.listTosee);
		session = new SessionManagement(getActivity().getApplicationContext());
		user_id = session.getUserID();
		user_idtoSend = user_id.get(SessionManagement.KEY_USERID);

		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
				R.array.gameTypes, android.R.layout.simple_spinner_item);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		//Spinner Item Click Listener
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				category=spinner.getSelectedItem().toString();
				if(category.equals("Find to See")){
					new getFindToSee().execute();
				}
			
				else{
					new getTrekking().execute();

				}
			
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		listedGames.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				toDeleteId = gameList.get(position).get("id");
		        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		      
				builder.setPositiveButton("OK",new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						new deletegames().execute();
					}
				});
				builder.setNegativeButton("Cancel", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialogToDelete.dismiss();
					}
				});
				  dialogToDelete = builder.create();
					dialogToDelete.setTitle("Deleting Item");
					dialogToDelete.setMessage("Are you sure you want to delete this game?");
				dialogToDelete.show();
				return true;
			}
		});
		listedGames.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(category.equals("Find to See")){
					ArrayList<String> arrayToSend = new ArrayList<String>();
					arrayToSend.add(gameList.get(position).get("name"));
					arrayToSend.add(gameList.get(position).get("description"));
					arrayToSend.add(gameList.get(position).get("latitude"));
					arrayToSend.add(gameList.get(position).get("longitude"));
					arrayToSend.add(gameList.get(position).get("photo"));
					arrayToSend.add("");
					arrayToSend.add("");
					arrayToSend.add(gameList.get(position).get("id"));
					FindToSeeSinglePage fragmentSinglePage = new FindToSeeSinglePage();
					Bundle args = new Bundle();
					args.putStringArrayList("findtoSee", arrayToSend);
					fragmentSinglePage.setArguments(args);
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
					
					ft.replace(R.id.content_frame, fragmentSinglePage);
					ft.commit();


				}else if(category.equals("Trekking on the Route")){
				ArrayList<String> arrayToSend = new ArrayList<String>();
					arrayToSend.add(gameList.get(position).get("name"));
					arrayToSend.add(gameList.get(position).get("description"));
					arrayToSend.add(gameList.get(position).get("latitude"));
					arrayToSend.add(gameList.get(position).get("longitude"));
					arrayToSend.add("");

					arrayToSend.add(gameList.get(position).get("id"));

					TrekkingOnTheRouteSinglePage fragmentSinglePage = new TrekkingOnTheRouteSinglePage();
					Bundle args = new Bundle();
					args.putStringArrayList("trekkingontheroute", arrayToSend);
					fragmentSinglePage.setArguments(args);
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
					
					ft.replace(R.id.content_frame, fragmentSinglePage);
					ft.commit();
					

				}
			
			}
		});
		
		return rootView;
	}

	private class deletegames extends AsyncTask<String, String, String> {
		 @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            // Showing progress dialog
	            pDialog = new ProgressDialog(getActivity());
	            pDialog.setMessage("Deleting...");
	            pDialog.setCancelable(false);
	            pDialog.show();
	 
	        }

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id",toDeleteId));
			  if(category.equals("Find to See")){
					JSONObject jObj = jsonParser.makeHttpRequest(
							DELETE_URL, "GET", params);

	            }	else{
	            	JSONObject jObj = jsonParser.makeHttpRequest(
	    					DELETE_TOTR, "GET", params);

				}
		
		
			return null;
		}
	    @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
         
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(category.equals("Find to See")){
				new getFindToSee().execute();

            }	else{
				new getTrekking().execute();

			}
            
         
        }
	}
	
	
	private class getFindToSee extends AsyncTask<String, String, String> {
		 @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            // Showing progress dialog
	            pDialog = new ProgressDialog(getActivity());
	            pDialog.setMessage("Please wait...");
	            pDialog.setCancelable(false);
	            pDialog.show();
	 
	        }
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("user_id",String.valueOf(user_idtoSend)));
				JSONObject jObj = jsonParser.makeHttpRequest(
						GAME_URL, "GET", params);
				if(!gameList.isEmpty())
	        		gameList.clear();
				Log.d("asdad",jObj.toString());
				JSONArray jsonArray = jObj.getJSONArray("find_to_see");
				for(int i=0;i<jsonArray.length();i++){
	        		JSONObject  games = (JSONObject) jsonArray.get(i);
	        		games.getString("name");
	        		 HashMap<String, String> game = new HashMap<String, String>();
	        		 String id = games.getString("id");
	        		 String name = games.getString("name");
	        		 String description = games.getString("description");
	        		 String rate = games.getString("rate");
	        		 String latitude = games.getString("latitude");
	        		 String longitude = games.getString("longitude");
	        		 String photo = games.getString("photo");

                     // adding each child node to HashMap key => value
                     game.put("id", id);
                     game.put("name", name);
                     game.put("description",description);
                     game.put("rate", rate);
                     game.put("latitude", latitude);
                     game.put("longitude", longitude);
                     game.put("photo", photo);



                     
                     gameList.add(game);
                     
	        		
	        	}
	        	}catch(Exception e){
	        		Log.e("error",e.toString());
	        	}
	        	return null;
	        }
		
		
		  @Override
	        protected void onPostExecute(String result) {
	            super.onPostExecute(result);
	         	adapter1=new MyCustomAdapter(getActivity().getApplicationContext(), R.layout.list_totr,gameList); 

                listedGames.setAdapter(adapter1);
	            if (pDialog.isShowing())
	                pDialog.dismiss();
	         
	         
	        }
	}
	  private class getTrekking extends AsyncTask<String, String, String> {

			
			 @Override
		        protected void onPreExecute() {
		            super.onPreExecute();
		            // Showing progress dialog
		            pDialog = new ProgressDialog(getActivity());
		            pDialog.setMessage("Please wait...");
		            pDialog.setCancelable(false);
		            pDialog.show();
		 
		        }
		 
		        @Override
		        protected String doInBackground(String... arg0) {
		            // Creating service handler class instance
		        	try{
		        		List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("user_id",String.valueOf(user_idtoSend)));
						JSONObject jObj = jsonParser.makeHttpRequest(
								GAME_URL, "GET", params);
						if(!gameList.isEmpty())
			        		gameList.clear();
		        	JSONArray jsonArray = jObj.getJSONArray("trekking_on_the_route");
		        	JSONArray markerArray = jObj.getJSONArray("markers");
		        	Log.d("array",jsonArray.toString());
		         	if(!gameList.isEmpty())
		        		gameList.clear();
		        	for(int i=0;i<jsonArray.length();i++){
		        		
		        		JSONObject  games = (JSONObject) jsonArray.get(i);
		        		 HashMap<String, String> game = new HashMap<String, String>();
		        		 String id = games.getString("id");
		        		 String name = games.getString("name");
		        		 String description = games.getString("description");
		        		 String rate = games.getString("rate");
		        		 latitude="";
		        		 longitude="";
		        		 for(int j=0;j<markerArray.length();j++){
		        			 JSONObject  marker = (JSONObject) markerArray.get(j);
		        			
		        			if(id.equals(marker.get("trekking_on_the_route_id"))){
		 	        			latitude = latitude+"*"+marker.getString("latitude"); 
		 	        			longitude = longitude+"*"+marker.getString("longitude"); 
		 	        		}
		 	        		
			        		}
	                     // adding each child node to HashMap key => value
	                     game.put("id", id);
	                     game.put("name", name);
	                     game.put("description",description);
	                     game.put("rate", rate);
	                     game.put("latitude",latitude);
	                     game.put("longitude",longitude);
	                     
	                     gameList.add(game);
	                     
		        		
		        	}
		        	}catch(Exception e){
		        		Log.e("error",e.toString());
		        	}
		        	return null;
		        }
		 
		        @Override
		        protected void onPostExecute(String result) {
		            super.onPostExecute(result);
		        	adapter=new MyCustomAdapter(getActivity().getApplicationContext(), R.layout.list_totr,gameList);
		       
		        	listedGames.setAdapter(adapter);
		            if (pDialog.isShowing())
		                pDialog.dismiss();
		         
		         
		        }
		
		    
		 
		}
}
