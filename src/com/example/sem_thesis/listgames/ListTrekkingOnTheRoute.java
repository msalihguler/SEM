package com.example.sem_thesis.listgames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.array;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.sem_thesis.R;
import com.example.sem_thesis.user.JSONParser;
import com.example.sem_thesis.user.SessionManagement;
import com.example.sem_thesis.userpage.SingleGamePlay;

public class ListTrekkingOnTheRoute extends SherlockFragment {
	MyCustomAdapter adapter1,adapter;
	Spinner spinner;
	ProgressDialog pDialog;
	private static String URLTOGET = "http://theduman.me/api/get_all_trekking_on_the_route/";
	private static String FINDTOSEE = "http://theduman.me/api/get_all_find_to_see/";
	private static String CHECKFTS = "http://theduman.me/api/check_find_to_see/";
	private static String CHECKTOTR = "http://theduman.me/api/check_trekking_on_the_route/";
	

	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_DESCR = "description";
	JSONParser jsonParser = new JSONParser();
	ListView listedGames;
	JSONArray games = null;
	ArrayList<HashMap<String, String>> gameList;
	ArrayList<String> arrayToSend;
	HashMap<String, Integer> user_id;
	int user_idtoSend,positionTest;
	SessionManagement session;
	
	String category,latitude,longitude,message,play_find_to_see_id,play_trekking;
	 float rate_value;
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.list_totr, container, false);
		gameList = new ArrayList<HashMap<String,String>>();
		spinner = (Spinner) rootView.findViewById(R.id.categories);
		session = new SessionManagement(getActivity().getApplicationContext());
		user_id = session.getUserID();
		user_idtoSend = user_id.get(SessionManagement.KEY_USERID);
		listedGames=(ListView)rootView.findViewById(R.id.listTosee);

		listedGames.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					 int position, long id) {
				// TODO Auto-generated method stub
				if(category.equals("Find to See")){
					positionTest=position;
					message="";
					play_find_to_see_id="";
					new Thread(){
					    public void run(){
					        //TODO Run network requests here.
					    new checkFindToSee().execute();	 }
					}.start();
				}
					else if(category.equals("Trekking on the Route")){
						positionTest=position;
						message="";
						play_trekking="";
					new Thread(){
					    public void run(){
					    	new checkTrekking().execute();
					  			    }
					}.start();
				

				

				}
			
			}
		});
		
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
						if(category.equals("Find to See"))
							new retrieveFindToSee().execute();
						else
							new retrieveGames().execute();

							
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
						
					}
				});
		return rootView;
	  }
	  private class retrieveGames extends AsyncTask<String, String, String> {

		
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
	        		JSONObject jObj = jsonParser.getJSONFromUrl(URLTOGET);
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
	        		 String rated_people = games.getString("rated_people");
	        		 float rate_value = (Float.parseFloat(rate)/Float.parseFloat(rated_people));

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
                     game.put(TAG_NAME, name);
                     game.put(TAG_DESCR,description);
                     game.put("rate", String.valueOf((Math.round(rate_value))));
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
	  private class retrieveFindToSee extends AsyncTask<String, String, String> {

			
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
	        	JSONArray jsonArray = jsonParser.getJSONArrayFromUrl(FINDTOSEE);
	        	Log.d("array",jsonArray.toString());
	        	if(!gameList.isEmpty())
	        		gameList.clear();
	        	
	        	for(int i=0;i<jsonArray.length();i++){
	        		JSONObject  games = (JSONObject) jsonArray.get(i);
	        		games.getString("name");
	        		 HashMap<String, String> game = new HashMap<String, String>();
	        		 String id = games.getString("id");
	        		 String name = games.getString("name");
	        		 String description = games.getString("description");
	        		 String rate = games.getString("rate");
	        		 String rated_people = games.getString("rated_people");
	        		 String latitude = games.getString("latitude");
	        		 String longitude = games.getString("longitude");
	        		 String photo = games.getString("photo");
	        		 if(rated_people.equals("0"))
	        			 rate_value = Float.parseFloat(rate);
	        		 else
	        			 rate_value = (Float.parseFloat(rate)/Float.parseFloat(rated_people));

                     // adding each child node to HashMap key => value
                     game.put(TAG_ID, id);
                     game.put(TAG_NAME, name);
                     game.put(TAG_DESCR,description);
                     game.put("rate", String.valueOf(Math.round(rate_value)));
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
	private class checkFindToSee extends AsyncTask<String,String, String>{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id",gameList.get(positionTest).get("id")));
			params.add(new BasicNameValuePair("user_id",String.valueOf(user_idtoSend)));
			JSONObject jObj = jsonParser.makeHttpRequest(
					CHECKFTS, "GET", params);
			Log.e("json",jObj.toString());
			try {
				message=jObj.get("message").toString();
				JSONArray jsonArray =jObj.getJSONArray("id");
				JSONObject  games = (JSONObject) jsonArray.get(0);
				play_find_to_see_id = games.getString("id");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("mesaasge",e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			ArrayList<String> arrayToSend = new ArrayList<String>();
			arrayToSend.add(gameList.get(positionTest).get("name"));
			arrayToSend.add(gameList.get(positionTest).get("description"));
			arrayToSend.add(gameList.get(positionTest).get("latitude"));
			arrayToSend.add(gameList.get(positionTest).get("longitude"));
			arrayToSend.add(gameList.get(positionTest).get("photo"));
			arrayToSend.add("findtosee");
			arrayToSend.add(play_find_to_see_id);
			arrayToSend.add(gameList.get(positionTest).get("id"));
		
			
			Log.e("json",play_find_to_see_id);

			if(message.equals("playing")){
				SingleGamePlay fragmentSinglePage = new SingleGamePlay();
				Bundle args = new Bundle();
				args.putStringArrayList("findtoSee", arrayToSend);
				fragmentSinglePage.setArguments(args);
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				
				ft.replace(R.id.content_frame, fragmentSinglePage);
				ft.commit();
			}else if(message.equals("completed")){
				Toast.makeText(getActivity(), "You have played this game!",Toast.LENGTH_SHORT).show();
			}else{
				FindToSeeSinglePage fragmentSinglePage = new FindToSeeSinglePage();
				Bundle args = new Bundle();
				args.putStringArrayList("findtoSee", arrayToSend);
				fragmentSinglePage.setArguments(args);
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				
				ft.replace(R.id.content_frame, fragmentSinglePage);
				ft.commit();
			}
		}
		
	}
	private class checkTrekking extends AsyncTask<String,String, String>{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id",gameList.get(positionTest).get("id")));
			params.add(new BasicNameValuePair("user_id",String.valueOf(user_idtoSend)));
			JSONObject jObj = jsonParser.makeHttpRequest(
					CHECKTOTR, "GET", params);
			Log.e("json",jObj.toString());
			try {
				message=jObj.get("message").toString();
				JSONArray jsonArray =jObj.getJSONArray("id");
				JSONObject  games = (JSONObject) jsonArray.get(0);
				play_trekking = games.getString("id");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("mesaasge",e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			ArrayList<String> arrayToSend = new ArrayList<String>();
			arrayToSend.add(gameList.get(positionTest).get("name"));
			arrayToSend.add(gameList.get(positionTest).get("description"));
			arrayToSend.add(gameList.get(positionTest).get("latitude"));
			arrayToSend.add(gameList.get(positionTest).get("longitude"));
			arrayToSend.add("trekkingOnTheRoute");
			arrayToSend.add(gameList.get(positionTest).get("id"));
			arrayToSend.add(play_trekking);
			
			Log.e("json",play_trekking);

			if(message.equals("playing")){
				SingleGamePlay fragmentSinglePage = new SingleGamePlay();
				Bundle args = new Bundle();
				args.putStringArrayList("findtoSee", arrayToSend);
				fragmentSinglePage.setArguments(args);
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);
				
				ft.replace(R.id.content_frame, fragmentSinglePage);
				ft.commit();
			}else if(message.equals("completed")){
				Toast.makeText(getActivity(), "You have played this game.",Toast.LENGTH_SHORT).show();
			}else{
				TrekkingOnTheRouteSinglePage fragmentSinglePage = new TrekkingOnTheRouteSinglePage();
				Bundle args = new Bundle();
				args.putStringArrayList("trekkingontheroute", arrayToSend);
				fragmentSinglePage.setArguments(args);
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				
				ft.replace(R.id.content_frame, fragmentSinglePage);
				ft.commit();
				
			}
		}
		
	}
}
