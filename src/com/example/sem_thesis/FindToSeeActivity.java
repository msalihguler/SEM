package com.example.sem_thesis;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
 
public class FindToSeeActivity extends SherlockFragment implements RoutingListener {
 
	//Declarations for the objects we are going to use
		Routing routing;
		LatLng start;
		LatLng end;
		GoogleMap map;
		Button calculateRoad,bilgiYolla;
		ArrayList<Polyline> polylineList;
		ProgressDialog dialog;
		Marker marker;
		Polyline plyline;
		ArrayList<String> latLng;
		View rootView;
		MapView mapView;
	
		
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {	
 
        rootView = inflater.inflate(R.layout.findtosee, container, false);
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

        //Finding views and making them functional
        
        calculateRoad=(Button)rootView.findViewById(R.id.yol);
        bilgiYolla=(Button)rootView.findViewById(R.id.bilgiYolla);
        //initializing of lists and dialog window
      
        polylineList = new ArrayList<Polyline>();
        dialog = new ProgressDialog(getActivity());
        
        //putting marker on long click to map
        
    	map.setOnMapLongClickListener(new OnMapLongClickListener() {
   		 
			@Override
			public void onMapLongClick(LatLng arg0) {
				map.clear();
				latLng = new ArrayList<String>();
				marker = map.addMarker(new MarkerOptions() 
	                .position(arg0).draggable(true)
	                .icon(BitmapDescriptorFactory
	                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
				marker.setTitle(marker.getId().toString());
				marker.showInfoWindow();
			
				latLng.add(marker.getId().toString());
			 
				latLng.add(String.valueOf((marker.getPosition().latitude)));
				latLng.add(String.valueOf((marker.getPosition().longitude)));
				
		
			}
		});
    	
    	//Deleting marker on click to the marker
    	map.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker arg0) {
			
				arg0.remove();
				map.clear();
			return true;
		
			}
		});
    	
    	//On button click calculate the distance between location and marker
    		calculateRoad.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				//Takes marker and location and calculates the road
					routing = new Routing(Routing.TravelMode.WALKING);
					routing.registerListener(FindToSeeActivity.this);
					 start= new LatLng(Double.parseDouble(latLng.get(1)), Double.parseDouble(latLng.get(2)));
					end= new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude());
				
					routing.execute(start, end);
			
			}
		});
    		bilgiYolla.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AddGameFragment addGame = new AddGameFragment();
					Bundle args = new Bundle();
					args.putStringArrayList("findtoSee", latLng);
					addGame.setArguments(args);
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
					
					ft.replace(R.id.content_frame, addGame);
					ft.remove(FindToSeeActivity.this);
					ft.commit();
					
							}
			});
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

}
