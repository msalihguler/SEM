package com.example.sem_thesis.userpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.sem_thesis.R;

import com.example.sem_thesis.listgames.MyCustomAdapter;
import com.example.sem_thesis.user.JSONParser;
import com.example.sem_thesis.user.SessionManagement;


public class ListGamesPlayed extends SherlockFragment  {
	
	Button toPlayed,toCompleted;
	MyCustomAdapter adapter1,adapter,adapter2,adapter3;
	ProgressDialog pDialog;
	Spinner spinner;
	ListView listedGames,listedGames2;
	View rootView;
	String category,latitude,longitude,toDeleteId;
	HashMap<String, Integer> user_id;
	SessionManagement session;
	int user_idtoSend;
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> gameList,completeGameList;
	ArrayList<String> arrayToSend;

	private static final String GET_PLAYED = "http://theduman.me/api/get_games_played";
	private static final String GET_COMPLETED = "http://theduman.me/api/get_games_completed";

	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
			rootView = inflater.inflate(R.layout.gamesplay, container, false);
			session = new SessionManagement(getActivity().getApplicationContext());
			gameList = new ArrayList<HashMap<String,String>>();
			completeGameList = new ArrayList<HashMap<String,String>>();

			spinner = (Spinner) rootView.findViewById(R.id.categories);
		

			listedGames=(ListView)rootView.findViewById(R.id.listTosee);
			listedGames2=(ListView)rootView.findViewById(R.id.listTosee2);
			listedGames2.setVisibility(View.INVISIBLE);
			user_id = session.getUserID();
			user_idtoSend = user_id.get(SessionManagement.KEY_USERID);
			toCompleted = (Button)rootView.findViewById(R.id.finish);
			toPlayed=(Button)rootView.findViewById(R.id.start);
			
			
			toPlayed.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					toPlayed.setBackgroundResource(R.drawable.androidbutton);
					toCompleted.setBackgroundResource(R.drawable.androidbuttonunclicked);
					listedGames2.setVisibility(View.INVISIBLE);
					listedGames.setVisibility(View.VISIBLE);
				}
			});
			toCompleted.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					toCompleted.setBackgroundResource(R.drawable.androidbutton);
					toPlayed.setBackgroundResource(R.drawable.androidbuttonunclicked);
					listedGames.setVisibility(View.INVISIBLE);
					listedGames2.setVisibility(View.VISIBLE);
				}
			});
			listedGames2.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					if(category.equals("Find to See")){
						ArrayList<String> arrayToSend = new ArrayList<String>();
						arrayToSend.add(completeGameList.get(position).get("name"));
						arrayToSend.add(completeGameList.get(position).get("description"));
						arrayToSend.add(completeGameList.get(position).get("latitude"));
						arrayToSend.add(completeGameList.get(position).get("longitude"));
						arrayToSend.add(completeGameList.get(position).get("photo"));
						arrayToSend.add("findtosee");
						arrayToSend.add(completeGameList.get(position).get("rate"));
						

						ListGamesFinished fragmentSinglePage = new ListGamesFinished();
						Bundle args = new Bundle();
						args.putStringArrayList("findtoSee", arrayToSend);
						fragmentSinglePage.setArguments(args);
						FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
						
						ft.replace(R.id.content_frame, fragmentSinglePage);
						ft.commit();
					}else if(category.equals("Trekking on the Route")){
						
						ArrayList<String> arrayToSend = new ArrayList<String>();


						arrayToSend.add(completeGameList.get(position).get("name"));
						arrayToSend.add(completeGameList.get(position).get("description"));
						arrayToSend.add(completeGameList.get(position).get("latitude"));
						
						arrayToSend.add(completeGameList.get(position).get("longitude"));
						arrayToSend.add(completeGameList.get(position).get("rate"));
						arrayToSend.add("trekkingontheroute");
						
						ListGamesFinished fragmentSinglePage = new ListGamesFinished();
						Bundle args = new Bundle();
						args.putStringArrayList("findtoSee", arrayToSend);
						fragmentSinglePage.setArguments(args);
						FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
						
						ft.replace(R.id.content_frame, fragmentSinglePage);
						ft.commit();
						

					}
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
						arrayToSend.add("findtosee");
						arrayToSend.add(gameList.get(position).get("findtoseeid"));
						arrayToSend.add(gameList.get(position).get("id"));


						SingleGamePlay fragmentSinglePage = new SingleGamePlay();
						Bundle args = new Bundle();
						args.putStringArrayList("findtoSee", arrayToSend);
						fragmentSinglePage.setArguments(args);
						FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);
						
						ft.replace(R.id.content_frame, fragmentSinglePage);
						ft.commit();
					}else if(category.equals("Trekking on the Route")){
						ArrayList<String> arrayToSend = new ArrayList<String>();
						arrayToSend.add(gameList.get(position).get("name"));
						arrayToSend.add(gameList.get(position).get("description"));
						arrayToSend.add(gameList.get(position).get("latitude"));
						arrayToSend.add(gameList.get(position).get("longitude"));
						arrayToSend.add("trekkingontheroute");
						
						arrayToSend.add(gameList.get(position).get("id"));
						arrayToSend.add(gameList.get(position).get("totrid"));


						SingleGamePlay fragmentSinglePage = new SingleGamePlay();
						Bundle args = new Bundle();
						args.putStringArrayList("findtoSee", arrayToSend);
						fragmentSinglePage.setArguments(args);
						FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
						
						ft.replace(R.id.content_frame, fragmentSinglePage);
						ft.commit();
						

					}
				}
			});
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
					R.array.gameTypes, android.R.layout.simple_spinner_item);

			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			// Apply the adapter to the spinner
			spinner.setAdapter(adapter);//Spinner Item Click Listener
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					category=spinner.getSelectedItem().toString();
					if(category.equals("Find to See")){
						
						        new getFindToSeePlayed().execute();
}
				
					else{

						new getTrekkingPlayed().execute();
}
				
				}	@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
			});
		

		return rootView;
	}

	private class getFindToSeePlayed extends AsyncTask<String, String, String> {
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
			// Building Parameters
			try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user_id",String.valueOf(user_idtoSend)));
			JSONObject jObj = jsonParser.makeHttpRequest(
					GET_PLAYED, "GET", params);
			JSONObject jObject = jsonParser.makeHttpRequest(
					GET_COMPLETED, "GET", params);
			if(!gameList.isEmpty())
        		gameList.clear();
			if(!completeGameList.isEmpty())
        		completeGameList.clear();
			JSONArray jsonArray = jObj.getJSONArray("find_to_see");
			JSONArray foreignArray = jObj.getJSONArray("play_find_to_see_id");
			JSONArray jsonArray2 = jObject.getJSONArray("find_to_see");

			for(int i=0;i<jsonArray.length();i++){
        		JSONObject  games = (JSONObject) jsonArray.get(i);
        		JSONObject ids = (JSONObject)foreignArray.get(i);
        	
        		 HashMap<String, String> game = new HashMap<String, String>();
        		 String id = games.getString("id");
        		 String name = games.getString("name");
        		 String description = games.getString("description");
        		 String rate = games.getString("rate");
        		 String rated_people = games.getString("rated_people");
        		 String latitude = games.getString("latitude");
        		 String longitude = games.getString("longitude");
        		 String photo = games.getString("photo");
        		 String findtoseeid = ids.getString("id");
        		 float rate_value = (Float.parseFloat(rate)/Float.parseFloat(rated_people));
                 // adding each child node to HashMap key => value
                 game.put("id", id);
                 game.put("name", name);
                 game.put("description",description);
                 game.put("rate", String.valueOf(Math.round(rate_value)));
                 game.put("latitude", latitude);
                 game.put("longitude", longitude);
                 game.put("photo", photo);
                 game.put("findtoseeid", findtoseeid);


                 
                 gameList.add(game);
                 
        		
        	}
			for(int i=0;i<jsonArray2.length();i++){
        		JSONObject  gamesCompleted = (JSONObject) jsonArray2.get(i);

        	
        		 HashMap<String, String> game = new HashMap<String, String>();
        		 String id = gamesCompleted.getString("id");
        		 String name = gamesCompleted.getString("name");
        		 String description = gamesCompleted.getString("description");
        		 String rate = gamesCompleted.getString("rate");
        		 String rated_people = gamesCompleted.getString("rated_people");
        		 String latitude = gamesCompleted.getString("latitude");
        		 String longitude = gamesCompleted.getString("longitude");
        		 String photo = gamesCompleted.getString("photo");
        		 float rate_value = (Float.parseFloat(rate)/Float.parseFloat(rated_people));

                 // adding each child node to HashMap key => value
                 game.put("id", id);
                 game.put("name", name);
                 game.put("description",description);
                 game.put("rate", String.valueOf(Math.round(rate_value)));
                 game.put("latitude", latitude);
                 game.put("longitude", longitude);
                 game.put("photo", photo);


                 
                 
                completeGameList.add(game);
                 
        		
        	}
        	}catch(Exception e){
        		Log.e("error",e.toString());
        	}
        	return null;
        }
	
	
	  @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
         	adapter1=new MyCustomAdapter(getActivity().getApplicationContext(), R.layout.gamesplay,gameList); 
         	adapter2=new MyCustomAdapter(getActivity().getApplicationContext(), R.layout.gamesplay,completeGameList); 

            listedGames.setAdapter(adapter1);
            listedGames2.setAdapter(adapter2);
            if (pDialog.isShowing())
                pDialog.dismiss();
         
         
        }
}
	private class getTrekkingPlayed extends AsyncTask<String, String, String> {
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
		protected String doInBackground(String... args0) {
			try{
        		List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("user_id",String.valueOf(user_idtoSend)));
				JSONObject jObj = jsonParser.makeHttpRequest(
						GET_PLAYED, "GET", params);
				JSONObject jObject = jsonParser.makeHttpRequest(
						GET_COMPLETED, "GET", params);
				if(!gameList.isEmpty())
	        		gameList.clear();
				if(!completeGameList.isEmpty())
	        		completeGameList.clear();
        	JSONArray jsonArray = jObj.getJSONArray("trekking_on_the_route");
			JSONArray foreignArray = jObj.getJSONArray("play_trekking_on_the_route_id");
        	JSONArray markerArray = jObj.getJSONArray("markers");
        	Log.d("array",jsonArray.toString());
         	if(!gameList.isEmpty())
        		gameList.clear();
        	for(int i=0;i<jsonArray.length();i++){
        		
        		JSONObject  games = (JSONObject) jsonArray.get(i);
        		JSONObject ids = (JSONObject)foreignArray.get(i);

        		 HashMap<String, String> game = new HashMap<String, String>();
        		 String id = games.getString("id");
        		 String name = games.getString("name");
        		 String description = games.getString("description");
        		 String rate = games.getString("rate");
        		 String rated_people = games.getString("rated_people");
        		 String findtoseeid = ids.getString("id");
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
                 game.put("name", name);
                 game.put("description",description);
                 game.put("rate", String.valueOf(Math.round(rate_value)));
                 game.put("latitude",latitude);
                 game.put("longitude",longitude);
                 game.put("totrid", findtoseeid);

                 gameList.add(game);
                 
        		
        	}	
        	JSONArray jsonArray2=jObject.getJSONArray("trekking_on_the_route");
        	JSONArray markerArray2 = jObject.getJSONArray("markers");
       	 	Log.v("araaaaaaaaaaaaaaaay1",jsonArray2.toString());

       	 	Log.v("araaaaaaaaaaaaaaaay2",markerArray2.toString());
       	 	
        	for(int i=0;i<jsonArray2.length();i++){
        		
        		JSONObject  games = (JSONObject) jsonArray2.get(i);
        		
        		 HashMap<String, String> game = new HashMap<String, String>();
        		 String id = games.getString("trekking_on_the_route_id");
        		 String name = games.getString("name");
        		 String description = games.getString("description");
        		 String rate = games.getString("rate");
        		 String rated_people = games.getString("rated_people");
        		 float rate_value = (Float.parseFloat(rate)/Float.parseFloat(rated_people));

        		 latitude="";
        		 longitude="";
        		 for(int j=0;j<markerArray2.length();j++){
        			 JSONObject  marker = (JSONObject) markerArray2.get(j);
        			
        			if(id.equals(marker.get("trekking_on_the_route_id"))){
    					Log.e("eben",latitude);

 	        			latitude = latitude+"*"+marker.getString("latitude"); 
 	        			longitude = longitude+"*"+marker.getString("longitude"); 
 	        		}
 	        		
	        		}

                 // adding each child node to HashMap key => value
                 game.put("id", id);
                 game.put("name", name);
                 game.put("description",description);
                 game.put("rate", String.valueOf(Math.round(rate_value)));
                 game.put("latitude",latitude);
                 game.put("longitude",longitude);
                 
                 completeGameList.add(game);
                 
        		
        	}
        	
        	}catch(Exception e){
        		Log.e("error",e.toString());
        	}
        	return null;
        }
 
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        	adapter=new MyCustomAdapter(getActivity().getApplicationContext(), R.layout.gamesplay,gameList);
       
        	listedGames.setAdapter(adapter);
        	adapter3=new MyCustomAdapter(getActivity().getApplicationContext(), R.layout.gamesplay,completeGameList);
            
        	listedGames2.setAdapter(adapter3);
            if (pDialog.isShowing())
                pDialog.dismiss();
         
         
        }
	}

}
