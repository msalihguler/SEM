package com.example.sem_thesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.sem_thesis.listgames.FindToSeeSinglePage;
import com.example.sem_thesis.user.JSONParser;
import com.example.sem_thesis.user.SessionManagement;
import com.example.sem_thesis.userpage.SingleGamePlay;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ShowAllFindToSeeOnMap extends SherlockFragment  {

	//Declarations for the objects we are going to use
	Routing routing;
	LatLng start,end,markerPoint;
	GoogleMap map;
	Button calculateRoad;
	ArrayList<Polyline> polylineList;
	ProgressDialog dialog;
	Marker marker;
	Polyline plyline;
	ArrayList<String> latLng;
	View rootView;
	MapView mapView;
	String ids,names,descs,rates,latitudes,longitudes,photos,message,play_find_to_see_id;
	String[] id,name,description,rate,latitude,longitude,photo;
	int markerID,user_idtoSend;
	SessionManagement session;
	HashMap<String, Integer> user_id;
	JSONParser jsonParser = new JSONParser();
	private static String CHECKFTS = "http://theduman.me/api/check_find_to_see/";
	ArrayList<String> arrayToSend = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = new SessionManagement(getActivity().getApplicationContext());
		user_id = session.getUserID();
		user_idtoSend = user_id.get(SessionManagement.KEY_USERID);
		try{
			ids = getActivity().getIntent().getStringArrayListExtra("findtoseemap").get(0);
			names = getActivity().getIntent().getStringArrayListExtra("findtoseemap").get(1);
			descs = getActivity().getIntent().getStringArrayListExtra("findtoseemap").get(2);
			rates = getActivity().getIntent().getStringArrayListExtra("findtoseemap").get(3);
			latitudes = getActivity().getIntent().getStringArrayListExtra("findtoseemap").get(4);
			longitudes = getActivity().getIntent().getStringArrayListExtra("findtoseemap").get(5);
			photos = getActivity().getIntent().getStringArrayListExtra("findtoseemap").get(6);
}catch(Exception e){
			Log.e("null pointer",e.toString());
		}
		rootView = inflater.inflate(R.layout.allfindtoseemap, container, false);
		mapView = (MapView) rootView.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);

		mapView.onResume();

		try {
			MapsInitializer.initialize(getActivity().getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		 id = ids.split("\\*");
		 name = names.split("\\*");
		 description = descs.split("\\*");
		 rate = rates.split("\\*");
		 latitude = latitudes.split("\\*");
		 longitude = longitudes.split("\\*");
		 photo = photos.split("\\*");
		
		map = mapView.getMap();

		map.setMyLocationEnabled(true);
		for(int i = 1; i<latitude.length;i++){
			markerPoint = new LatLng(Double.parseDouble(latitude[i]),Double.parseDouble(longitude[i]));
			marker = map.addMarker(new MarkerOptions() 
            .position(markerPoint).draggable(true)
            .icon(BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			int markerID = Integer.parseInt(marker.getId().substring(marker.getId().length()-1));
			marker.setTitle(name[markerID+1]);
			marker.showInfoWindow();
			
		}
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker arg0) {
				// TODO Auto-generated method stub
				markerID = Integer.parseInt(arg0.getId().substring(arg0.getId().length()-1));
				message="";
				play_find_to_see_id="";
				if(!arrayToSend.isEmpty())
					arrayToSend.clear();
				arrayToSend.add(name[markerID+1]);
				arrayToSend.add(description[markerID+1]);
				arrayToSend.add(latitude[markerID+1]);
				arrayToSend.add(longitude[markerID+1]);
				arrayToSend.add(photo[markerID+1]);
				arrayToSend.add("findtosee");
				arrayToSend.add(play_find_to_see_id);
				arrayToSend.add(id[markerID+1]);
				new Thread(){
				    public void run(){
				        //TODO Run network requests here.
				    new checkFindToSee().execute();	 }
				}.start();
		
			}
		});


		polylineList = new ArrayList<Polyline>();
		dialog = new ProgressDialog(getActivity());

		//On button click calculate the distance between location and marker
	

		return rootView;

	}
	private class checkFindToSee extends AsyncTask<String,String, String>{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id",arrayToSend.get(7)));
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
				Toast.makeText(getActivity(), "You have played this game.",Toast.LENGTH_SHORT).show();
			}else{
				FindToSeeSinglePage fragmentSinglePage = new FindToSeeSinglePage();
				Bundle args = new Bundle();
				args.putStringArrayList("findtoSee", arrayToSend);
				fragmentSinglePage.setArguments(args);
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);
				
				ft.replace(R.id.content_frame, fragmentSinglePage);
				ft.commit();
			}
		}
		
	}
}
