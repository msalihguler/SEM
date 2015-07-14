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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.sem_thesis.R;
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
public class FindToSeeSinglePage extends SherlockFragment implements RoutingListener {
	
	Routing routing;
	LatLng latlng,start,end;	
	ArrayList<Polyline> polylineList;
	ProgressDialog dialog;
	GoogleMap map;
	MapView mapView;
	Marker marker;
	View rootView;
	TextView title,description;
	String name,descString,latiude,longitude,photoName,id,message;
	NetworkImageView thumbNail;
	ImageButton goToDirection;
	Polyline plyline;
	Button startPlaying;
	SessionManagement session;
	HashMap<String, Integer> user_id;
	int user_idtoSend;
	ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	private static String PHOTOURL = "http://theduman.me/uploads/";
	private static String PLAY_FTS = "http://theduman.me/api/play_find_to_see";


	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			
			rootView = inflater.inflate(R.layout.gamescreen, container, false);
			session = new SessionManagement(getActivity().getApplicationContext());
			user_id = session.getUserID();
			user_idtoSend = user_id.get(SessionManagement.KEY_USERID);
			name = getArguments().getStringArrayList("findtoSee").get(0).toString();
			descString = getArguments().getStringArrayList("findtoSee").get(1).toString();
			latiude = getArguments().getStringArrayList("findtoSee").get(2).toString();
			longitude = getArguments().getStringArrayList("findtoSee").get(3).toString();
			photoName = getArguments().getStringArrayList("findtoSee").get(4).toString();
			id = getArguments().getStringArrayList("findtoSee").get(7).toString();
			latlng = new LatLng(Double.parseDouble(latiude),Double.parseDouble(longitude));
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
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,14));
	    	marker = map.addMarker(new MarkerOptions() 
            .position(latlng).draggable(true)
            .icon(BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	        dialog = new ProgressDialog(getActivity());
	        startPlaying=(Button)rootView.findViewById(R.id.startplaying);
	    	goToDirection = (ImageButton)rootView.findViewById(R.id.toDirection);
	        polylineList = new ArrayList<Polyline>();
	        title=(TextView)rootView.findViewById(R.id.titleInScreen);
	        description=(TextView)rootView.findViewById(R.id.descInScreen);
	        thumbNail = (NetworkImageView) rootView.findViewById(R.id.thumbnailToDownload);
	        startPlaying.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new playFindToSee().execute();
				}
			});
	        goToDirection.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					routing = new Routing(Routing.TravelMode.WALKING);
					routing.registerListener(FindToSeeSinglePage.this);
					 start= latlng;
					end= new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude());
				
					routing.execute(start, end);	
				}
			});
	        title.setText(name);
	        description.setText(descString);
	        
	        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
	        if (imageLoader == null)
	            imageLoader = AppController.getInstance().getImageLoader();
	        
	   
	        if(photoName.equals("."))
	        	thumbNail.setImageResource(R.drawable.pin);
	        else
	        	thumbNail.setImageUrl(PHOTOURL+photoName, imageLoader);
	        return rootView;
	        	}
	@Override
	public void onRoutingFailure() {

		
	}

	@Override
	public void onRoutingStart() {
		//shows dialog
		dialog.setMessage("Getting Direction for Walking...");
		dialog.show();
		
	}
	@Override
	public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route) {
		
		
		//creates polylines and adds them to list
		
		String k = route.getDistanceText();
		try{
			Toast.makeText(getActivity(), k, Toast.LENGTH_SHORT).show();
		}catch(Exception e){
			Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
		}
	      PolylineOptions polyoptions = new PolylineOptions();
	      polyoptions.color(Color.RED);
	      polyoptions.width(10);
	      polyoptions.addAll(mPolyOptions.getPoints());
	      plyline = map.addPolyline(polyoptions);
	      polylineList.add(plyline);
	     
	      //closes dialog
	      dialog.dismiss();
	  	
	}
	private class playFindToSee extends AsyncTask<String, String, String> {
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
			params.add(new BasicNameValuePair("find_to_see_id",id));
			
			JSONObject jObj = jsonParser.makeHttpRequest(
					PLAY_FTS, "GET", params);
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
}
