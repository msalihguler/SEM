package com.example.sem_thesis;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.sem_thesis.navigationbar.MainActivity;
import com.example.sem_thesis.user.JSONParser;
import com.example.sem_thesis.user.SessionManagement;
import com.example.sem_thesis.userpage.SingleGamePlay;

public class SplashScreen extends Activity {
	SessionManagement session;
    JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> gameList;
	private static String FINDTOSEE = "http://theduman.me/api/get_all_find_to_see/";
	private static String TREKKINGONTHEROUTE = "http://theduman.me/api/get_all_trekking_on_the_route/";

	float rate_value;
	String id_tosend,name_tosend,desc_tosend,rate_tosend,latitude_tosend,longitude_tosend,photo_tosend,latitude,longitude;

	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        session = new SessionManagement(getApplicationContext());
			  session.checkLogin();
		       // get user data from session
		        HashMap<String, String> user = session.getUserDetails();
		        if(!session.isLoggedIn()){
		        	finish();
		        }
	        setContentView(R.layout.splashscreen);
	        
			gameList = new ArrayList<HashMap<String,String>>();
			
	        new PrefetchData().execute();
	 
	    }
	 
	  
	    private class PrefetchData extends AsyncTask<String, String, String> {
	 
	  
	 
	        @Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}

			@Override
	        protected String doInBackground(String... arg0) {
	         try{
	        	 //FIND TO SEE CODE
	           	JSONArray jsonArray= jsonParser.getJSONArrayFromUrl(FINDTOSEE);
	           	if(!gameList.isEmpty())
	        		gameList.clear();
	           	id_tosend="";
	           	name_tosend="";
	           	desc_tosend="";
	           	rate_tosend="";
	           	latitude_tosend="";
	           	longitude_tosend="";
	           	photo_tosend="";
	       
	        	for(int i=0;i<jsonArray.length();i++){
	        		JSONObject  games = (JSONObject) jsonArray.get(i);
	        		
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
	        		 id_tosend=id_tosend+"*"+id;
	        		 name_tosend=name_tosend+"*"+name;
	        		 desc_tosend=desc_tosend+"*"+description;
	        		 rate_tosend=rate_tosend+"*"+String.valueOf(Math.round(rate_value));
	        		 latitude_tosend=latitude_tosend+"*"+latitude;
	        		 longitude_tosend=longitude_tosend+"*"+longitude;
	        		 photo_tosend = photo_tosend+"*"+photo;
	        	}
                     // adding each child node to HashMap key => value
       		 		HashMap<String, String> game = new HashMap<String, String>();

                     game.put("id", id_tosend);
                     game.put("name", name_tosend);
                     game.put("description",desc_tosend);
                     game.put("rate", rate_tosend);
                     game.put("latitude", latitude_tosend);
                     game.put("longitude", longitude_tosend);
                     game.put("photo", photo_tosend);
                     
                     gameList.add(game);
	        	
                     
                     //trekking on the route part 
                	 id_tosend="";
	        		 name_tosend="";
	        		 desc_tosend="";
	        		 rate_tosend="";
	        		 latitude_tosend="";
	        		 longitude_tosend="";
                 	JSONObject jObj = jsonParser.getJSONFromUrl(TREKKINGONTHEROUTE);
    	        	JSONArray trekkingArray = jObj.getJSONArray("trekking_on_the_route");
    	        	JSONArray markerArray = jObj.getJSONArray("markers");
    	        	Log.e("array2",trekkingArray.toString());
    	        	for(int i=0;i<trekkingArray.length();i++){
    	        		
    	        		JSONObject  games = (JSONObject) trekkingArray.get(i);
    	        		
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
    	 	        			latitude = latitude+"+"+marker.getString("latitude"); 
    	 	        			longitude = longitude+"+"+marker.getString("longitude"); 
    	 	        		}
    	 	        		
    		        		}
    	        		 id_tosend=id_tosend+"*"+id;
    	        		 name_tosend=name_tosend+"*"+name;
    	        		 desc_tosend=desc_tosend+"*"+description;
    	        		 rate_tosend=rate_tosend+"*"+String.valueOf(Math.round(rate_value));
    	        		 latitude_tosend=latitude_tosend+"*"+latitude;
    	        		 longitude_tosend=longitude_tosend+"*"+longitude;
    	        	}
                         // adding each child node to HashMap key => value
    	        	HashMap<String, String> game2 = new HashMap<String, String>();

                         game2.put("id", id_tosend);
                         game2.put("name", name_tosend);
                         game2.put("description",desc_tosend);
                         game2.put("rate", rate_tosend);
                         game2.put("latitude",latitude_tosend);
                         game2.put("longitude",longitude_tosend);
                         
                         gameList.add(game2);
                         
    	        		
    	        	
	         }catch(Exception e){
	        		Log.e("error",e.toString());

	         }

	          
	 
	            return null;
	        }
	 
	        @Override
	        protected void onPostExecute(String result) {
	            super.onPostExecute(result);
	        	ArrayList<String> arrayToSend = new ArrayList<String>();
	        	
	        		arrayToSend.add(gameList.get(0).get("id"));
	        	
		        	arrayToSend.add(gameList.get(0).get("name"));
		        
		        	arrayToSend.add(gameList.get(0).get("description"));
		       
		        	arrayToSend.add(gameList.get(0).get("rate"));
		        
		        	arrayToSend.add(gameList.get(0).get("latitude"));
		        
		        	arrayToSend.add(gameList.get(0).get("longitude"));
		        
		        	arrayToSend.add(gameList.get(0).get("photo"));
		        
		        	ArrayList<String> arrayToSend2 = new ArrayList<String>();
		        	
	        		arrayToSend2.add(gameList.get(1).get("id"));
	        	
		        	arrayToSend2.add(gameList.get(1).get("name"));
		        
		        	arrayToSend2.add(gameList.get(1).get("description"));
		       
		        	arrayToSend2.add(gameList.get(1).get("rate"));
		        
		        	arrayToSend2.add(gameList.get(1).get("latitude"));
		        
		        	arrayToSend2.add(gameList.get(1).get("longitude"));
				Intent i = new Intent(SplashScreen.this, com.example.sem_thesis.navigationbar.MainActivity.class);
	            i.putStringArrayListExtra("findtoseemap",arrayToSend);
	            i.putStringArrayListExtra("trekkingontheroute",arrayToSend2);
	            startActivity(i);
	            finish();
	        }
	 
	    }
	 
	}

