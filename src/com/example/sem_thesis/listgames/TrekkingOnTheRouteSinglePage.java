package com.example.sem_thesis.listgames;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.sem_thesis.R;
import com.example.sem_thesis.TrekkingOntheRoute;
import com.example.sem_thesis.user.JSONParser;
import com.example.sem_thesis.user.SessionManagement;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class TrekkingOnTheRouteSinglePage extends SherlockFragment implements RoutingListener{

	Routing routing;
	LatLng latLong,LatiLng;
	GoogleMap map;
	MapView mapView;
	Marker marker;
	View rootView;
	TextView title,description;
	String name,descString,latitude,longitude,id,message;
	SessionManagement session;
	ArrayList<Polyline> polylineList;
	ArrayList<ArrayList<String>> markerPoints;
	Polyline plyline;
	HashMap<String, Integer> user_id;
	int user_idtoSend;
	ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	Button startGame;
	ImageButton getDirection;
	LatLng start,end;
	String[] strLat,strLong;
	private static String PLAY_TOTR = "http://theduman.me/api/play_trekking_on_the_route";

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.totrgamescreen, container, false);
		startGame = (Button)rootView.findViewById(R.id.starttotr);
		getDirection= (ImageButton)rootView.findViewById(R.id.drawroad);
		session = new SessionManagement(getActivity().getApplicationContext());
		user_id = session.getUserID();
        pDialog = new ProgressDialog(getActivity());
        polylineList = new ArrayList<Polyline>();
       
		user_idtoSend = user_id.get(SessionManagement.KEY_USERID);
		name = getArguments().getStringArrayList("trekkingontheroute").get(0).toString();
		descString = getArguments().getStringArrayList("trekkingontheroute").get(1).toString();
		latitude = getArguments().getStringArrayList("trekkingontheroute").get(2).toString();
		longitude = getArguments().getStringArrayList("trekkingontheroute").get(3).toString();
		id =  getArguments().getStringArrayList("trekkingontheroute").get(5).toString();
		strLat =latitude.split("\\*");
		strLong =longitude.split("\\*");
		getDirection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for(int i=1;i<(strLat.length)-1;i++){
					
					
					//Takes markers to a loop and calculates distance between each two of them from the starting point
					routing = new Routing(Routing.TravelMode.WALKING);
					routing.registerListener(TrekkingOnTheRouteSinglePage.this);
					 start= new LatLng(Double.parseDouble(strLat[i]), Double.parseDouble(strLong[i]));
					end= new LatLng(Double.parseDouble(strLat[i+1]), Double.parseDouble(strLong[i+1]));
					routing.execute(start, end);
			}	
			}
		});
		
		
		startGame.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new playTrekking().execute();
			}
		});
	
		latLong = new LatLng(Double.parseDouble(strLat[1]),Double.parseDouble(strLong[1]));
		int sizeOfLat = strLat.length;
	    mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();
        
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map = mapView.getMap();

        map.setMyLocationEnabled(true);
        map.clear();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong,14));
        for(int i=1;i<sizeOfLat;i++){
        	LatiLng = new LatLng(Double.parseDouble(strLat[i]),Double.parseDouble(strLong[i]));
        	marker = map.addMarker(new MarkerOptions() 
            .position(LatiLng).draggable(true)
            .icon(BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
		

        title=(TextView)rootView.findViewById(R.id.titleInScreen);
        description=(TextView)rootView.findViewById(R.id.descInScreen);
        title.setText(name);
        description.setText(descString);
		return rootView;
		
	}
	private class playTrekking extends AsyncTask<String, String, String> {
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
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user_id",String.valueOf(user_idtoSend)));
			params.add(new BasicNameValuePair("trekking_on_the_route_id",id));
			
			JSONObject jObj = jsonParser.makeHttpRequest(
					PLAY_TOTR, "GET", params);
			try {
				message = jObj.getString("message");
			} catch (JSONException e) {
				Log.e("sadawd", e.toString());		
				}
			Log.v("response",jObj.toString());
			
			return null;
		}
		  @Override
	        protected void onPostExecute(String result) {
	            super.onPostExecute(result);
	         
	            if (pDialog.isShowing())
	                pDialog.dismiss();
	          Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
	            
	         
	        }
	}
	@Override
	public void onRoutingFailure() {

		
	}

	@Override
	public void onRoutingStart() {
		//shows dialog
		pDialog.setMessage("Getting Direction for Walking...");
		pDialog.show();
		
	}
	@Override
	public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route) {
		
		
		//creates polylines and adds them to list
	      
	      PolylineOptions polyoptions = new PolylineOptions();
	      polyoptions.color(Color.RED);
	      polyoptions.width(10);
	      polyoptions.addAll(mPolyOptions.getPoints());
	      plyline = map.addPolyline(polyoptions);
	      polylineList.add(plyline);
	     
	      //closes dialog
	      pDialog.dismiss();
	  	
	}
}
