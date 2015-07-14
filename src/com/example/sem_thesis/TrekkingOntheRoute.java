package com.example.sem_thesis;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.graphics.Color;
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
 
public class TrekkingOntheRoute extends SherlockFragment implements RoutingListener {
	//Declarations for the objects we are going to use
		Routing routing;
		LatLng start;
		LatLng end;
		GoogleMap map;
		Button calculateRoad,sendMarkers;
		ArrayList<Polyline> polylineList;
		ArrayList<ArrayList<String>> markerPoints;
		ProgressDialog dialog;
		ArrayList<String> latLng;
		Marker marker;
		Polyline plyline;
		View rootView;
		MapView mapView;
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.trekkingontheroute, container, false);
        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map = mapView.getMap();
        map.setMyLocationEnabled(true);
        //Finding views and making them functional
        
        calculateRoad=(Button)rootView.findViewById(R.id.yol);
        sendMarkers=(Button)rootView.findViewById(R.id.sendCoordinates);

        
        //initializing of lists and dialog window
        markerPoints =  new ArrayList<ArrayList<String>>();
        polylineList = new ArrayList<Polyline>();
        dialog = new ProgressDialog(getActivity());
        
        //putting markers on long click to map
        
    	map.setOnMapLongClickListener(new OnMapLongClickListener() {
   		 
			@Override
			public void onMapLongClick(LatLng arg0) {
				
				ArrayList<String> row = new ArrayList<String>();
				marker = map.addMarker(new MarkerOptions() 
	                .position(arg0).draggable(true)
	                .icon(BitmapDescriptorFactory
	                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
				marker.setTitle(marker.getId().toString());
				marker.showInfoWindow();
			
				row.add(marker.getId().toString());
			 
				row.add(String.valueOf((marker.getPosition().latitude)));
				row.add(String.valueOf((marker.getPosition().longitude)));
				
				markerPoints.add(row);
			}
		});
    	
    	//Deleting markers on click to the box
    	map.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker arg0) {
			
				int sizeofArray = markerPoints.size();
				for(int i=0;i<(sizeofArray);i++){	
					
					try{
					//checks whether the title is the one we wanted	
					if(arg0.getTitle().equals(markerPoints.get(i).get(0).toString())){
						//deletes markers form the list and map
						markerPoints.remove(i);
						arg0.remove();
						Toast.makeText(getActivity(),"Marker has been deleted", Toast.LENGTH_SHORT).show();
						}
						}catch(Exception e){
							Toast.makeText(getActivity(),"404", Toast.LENGTH_SHORT).show();
					}
				}
				
				return false;
				
			
			}
		});
    	//send information from the markers
    	
    	sendMarkers.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(markerPoints.size()==0)
					Toast.makeText(getActivity(), "You haven't chosen any marker", Toast.LENGTH_SHORT).show();
				else
				{
					String Latitude=String.valueOf(markerPoints.get(0).get(1));
					String Longitude=String.valueOf(markerPoints.get(0).get(2));
					int sizeofArray = markerPoints.size();
					Toast.makeText(getActivity(),String.valueOf(polylineList.size()), Toast.LENGTH_SHORT).show();
					for(int i=1;i<(sizeofArray);i++){
						
						Latitude = Latitude +"*" +String.valueOf(markerPoints.get(i).get(1));
						Longitude = Longitude + "*"+String.valueOf(markerPoints.get(i).get(2));
						
					}
					latLng = new ArrayList<String>();

					latLng.add(Latitude);
					latLng.add(Longitude);
					Log.e("lat", latLng.get(0));
					Log.e("long", latLng.get(1));
					AddGameFragment addGame = new AddGameFragment();
					Bundle args = new Bundle();
					args.putStringArrayList("trekkingOnTheRoute", latLng);
					addGame.setArguments(args);
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
					
					ft.replace(R.id.content_frame, addGame);
					ft.remove(TrekkingOntheRoute.this);
					ft.commit();
				}
			}
		});
    	
    	
    	//On button click calculate the distance between markers
    		calculateRoad.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//clears the map from polylines so when we make a difference it will be seen automatically on refreshing
				int sizeofPolylines = polylineList.size();
				for(int i=0;i<(sizeofPolylines);i++){
					polylineList.get(i).remove();
				}
				
				
				int sizeofArray = markerPoints.size();
				Toast.makeText(getActivity(),String.valueOf(polylineList.size()), Toast.LENGTH_SHORT).show();
				for(int i=0;i<(sizeofArray)-1;i++){
					
				
					//Takes markers to a loop and calculates distance between each two of them from the starting point
					routing = new Routing(Routing.TravelMode.WALKING);
					routing.registerListener(TrekkingOntheRoute.this);
					 start= new LatLng(Double.parseDouble(markerPoints.get(i).get(1)), Double.parseDouble(markerPoints.get(i).get(2)));
					end= new LatLng(Double.parseDouble(markerPoints.get(i+1).get(1)), Double.parseDouble(markerPoints.get(i+1).get(2)));
					routing.execute(start, end);
			}
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