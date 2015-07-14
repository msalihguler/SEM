package com.example.sem_thesis.userpage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.sem_thesis.R;
import com.example.sem_thesis.listgames.AppController;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ListGamesFinished extends SherlockFragment {
	
	LatLng latlng,latLong,LatiLng;	
	GoogleMap map;
	MapView mapView;
	Marker marker;
	View rootView;
	RatingBar rating;
	TextView title,description;
	String name,descString,latiude,longitude,photoName,rate;
	NetworkImageView thumbNail;
	private static String PHOTOURL = "http://theduman.me/uploads/";


	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			
			rootView = inflater.inflate(R.layout.singlefinisheditem, container, false);
			if(getArguments().getStringArrayList("findtoSee").get(5).toString().equals("findtosee")){
			name = getArguments().getStringArrayList("findtoSee").get(0).toString();
			descString = getArguments().getStringArrayList("findtoSee").get(1).toString();
			latiude = getArguments().getStringArrayList("findtoSee").get(2).toString();
			longitude = getArguments().getStringArrayList("findtoSee").get(3).toString();
			photoName = getArguments().getStringArrayList("findtoSee").get(4).toString();
			rate = getArguments().getStringArrayList("findtoSee").get(6).toString();

			latlng = new LatLng(Double.parseDouble(latiude),Double.parseDouble(longitude));
			}
			else{
				name = getArguments().getStringArrayList("findtoSee").get(0).toString();
				descString = getArguments().getStringArrayList("findtoSee").get(1).toString();
				latiude = getArguments().getStringArrayList("findtoSee").get(2).toString();
				longitude = getArguments().getStringArrayList("findtoSee").get(3).toString();
				rate = getArguments().getStringArrayList("findtoSee").get(4).toString();

			}
		    title=(TextView)rootView.findViewById(R.id.titleInScreen);
	        description=(TextView)rootView.findViewById(R.id.descInScreen);
      	  	thumbNail = (NetworkImageView) rootView.findViewById(R.id.thumbnailToDownload1);
      	  	rating = (RatingBar)rootView.findViewById(R.id.ratingBar1);
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
	        if(photoName!=null){
	        	  map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,14));
	  	    	marker = map.addMarker(new MarkerOptions() 
	              .position(latlng).draggable(true)
	              .icon(BitmapDescriptorFactory
	              .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

	        }else{
	        	String[] strLat =latiude.split("\\*");
				String[] strLong =longitude.split("\\*");

				latLong = new LatLng(Double.parseDouble(strLat[1]),Double.parseDouble(strLong[1]));
				int sizeOfLat = strLat.length;
				 map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong,14));
			        for(int i=1;i<sizeOfLat;i++){
			        	LatiLng = new LatLng(Double.parseDouble(strLat[i]),Double.parseDouble(strLong[i]));
			        	marker = map.addMarker(new MarkerOptions() 
			            .position(LatiLng).draggable(true)
			            .icon(BitmapDescriptorFactory
			            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			        }
	        }
	    
	        if(photoName!=null){
	            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
	   	        if (imageLoader == null)
	   	            imageLoader = AppController.getInstance().getImageLoader();
	   	        	thumbNail.setImageUrl(PHOTOURL+photoName, imageLoader);
	   	        	
	        }else
	        	thumbNail.setVisibility(View.INVISIBLE);
	        rating.setRating(Float.parseFloat(rate));
	        
	        title.setText(name);
	        description.setText(descString);
	        
	     
	        return rootView;
	        	}
	
}
