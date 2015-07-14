package com.example.sem_thesis.userpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import com.example.sem_thesis.R;
import com.example.sem_thesis.listgames.AppController;
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
import net.sourceforge.zbar.Symbol;

public class SingleGamePlay extends SherlockFragment{
	
	ProgressDialog pDialog;
	LatLng latlng,latLong,LatiLng;	
	GoogleMap map;
	MapView mapView;
	Marker marker;
	View rootView;
	TextView title,description;
	String name,descString,latiude,longitude,photoName,rate,totrid,id,commentToSend,result,success="";
	NetworkImageView thumbNail;
	Button FinishGame,ok,cancel;
	Dialog rankComment;
	RatingBar giveRate;
	EditText comment;
	SessionManagement session;
	HashMap<String, Integer> user_id;
	JSONParser jsonParser = new JSONParser();;
	int user_idtoSend;
	float rateToSend;
	AlertDialog dialog;
	private static String PHOTOURL = "http://theduman.me/uploads/";
	private static String COMPLETE_TOTR = "http://theduman.me/api/complete_trekking_on_the_route";
	private static String COMPLETE_FTS = "http://theduman.me/api/complete_find_to_see";
	
	 @Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			 super.onActivityResult(requestCode, resultCode, data);
		                if (resultCode == getActivity().RESULT_OK) {
		      		      if(requestCode==1){
		      		    	  result =data.getStringExtra(ZBarConstants.SCAN_RESULT);
		      		    	  if(result.equals("SEM!")){
		      		    	    rankComment = new Dialog(getActivity());
		   			         rankComment.setContentView(R.layout.rankandcomment);
		   			         giveRate=(RatingBar)rankComment.findViewById(R.id.rank);
		   			          comment=(EditText)rankComment.findViewById(R.id.comment);
		   			         ok = (Button)rankComment.findViewById(R.id.okayButton);
		   			         cancel=(Button)rankComment.findViewById(R.id.cancelButton);
		   					rankComment.show();
		   			        ok.setOnClickListener(new OnClickListener() {
		   						
		   						@Override
		   						public void onClick(View v) {
		   							// TODO Auto-generated method stub
		   							rateToSend = giveRate.getRating();
		   							commentToSend = comment.getText().toString();

		   							new finishFindtoSee().execute();
		   						}
		   					});
		   			        cancel.setOnClickListener(new OnClickListener() {
		   						
		   						@Override
		   						public void onClick(View v) {
		   							// TODO Auto-generated method stub
		   						rankComment.dismiss();	
		   						}
		   					});
		      		    	  }
		      		    	  else{
			                        Toast.makeText(getActivity(), "You have found the wrong QRCode", Toast.LENGTH_SHORT).show();

		      		    	  }
		                } 
		      		      }
		                else if(resultCode == getActivity().RESULT_CANCELED && data != null) {
		                    String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
		                    if(!TextUtils.isEmpty(error)) {
		                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
		                    }else{
		                        Toast.makeText(getActivity(), "okumadý", Toast.LENGTH_SHORT).show();

		                    }
		                }
		           
		        
		    }
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			
			rootView = inflater.inflate(R.layout.singleplayeditem, container, false);
			session = new SessionManagement(getActivity().getApplicationContext());
			user_id = session.getUserID();
			user_idtoSend = user_id.get(SessionManagement.KEY_USERID);
		
			if(getArguments().getStringArrayList("findtoSee").get(5).toString().equals("findtosee")){
			name = getArguments().getStringArrayList("findtoSee").get(0).toString();
			descString = getArguments().getStringArrayList("findtoSee").get(1).toString();
			latiude = getArguments().getStringArrayList("findtoSee").get(2).toString();
			longitude = getArguments().getStringArrayList("findtoSee").get(3).toString();
			photoName = getArguments().getStringArrayList("findtoSee").get(4).toString();
			totrid = getArguments().getStringArrayList("findtoSee").get(6).toString();
			id =getArguments().getStringArrayList("findtoSee").get(7).toString();
			latlng = new LatLng(Double.parseDouble(latiude),Double.parseDouble(longitude));
			}
			else{
				name = getArguments().getStringArrayList("findtoSee").get(0).toString();
				descString = getArguments().getStringArrayList("findtoSee").get(1).toString();
				latiude = getArguments().getStringArrayList("findtoSee").get(2).toString();
				longitude = getArguments().getStringArrayList("findtoSee").get(3).toString();
				id =getArguments().getStringArrayList("findtoSee").get(5).toString();
				totrid =getArguments().getStringArrayList("findtoSee").get(6).toString();
			}
			
		    title=(TextView)rootView.findViewById(R.id.titleInScreen);
	        description=(TextView)rootView.findViewById(R.id.descInScreen);
      	  	thumbNail = (NetworkImageView) rootView.findViewById(R.id.thumbnailToDownload1);
	        mapView = (MapView) rootView.findViewById(R.id.map);
	        FinishGame = (Button)rootView.findViewById(R.id.finishGame);
	
	        if(photoName!=null){
	        	FinishGame.setText("Found the Item");

	        }else{
	        	FinishGame.setText("Finish my Trekking");
	        	
	        }
	        
	        mapView.onCreate(savedInstanceState);

	        mapView.onResume();
	        FinishGame.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
			        if(photoName!=null){
			        AlertDialog.Builder builder = new Builder(getActivity());
			        builder.setTitle("Warning!");
			        builder.setMessage("If you found the QR Code you will be directed to QR Reader Page");
			        builder.setPositiveButton("GO!",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (isCameraAvailable()) {
					            Intent intent = new Intent(getActivity(), ZBarScannerActivity.class);
					            intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
					           startActivityForResult(intent, 1);
					        } else {
					            Toast.makeText(getActivity(), "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
					        }
							
					    }
						

					    public boolean isCameraAvailable() {
					        PackageManager pm = getActivity().getPackageManager();
					        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
					    }
					    
						
					});
			        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
			        dialog = builder.create();
			        dialog.show();
			        	
			        	
			        	
			        }else{
			         rankComment = new Dialog(getActivity());
			         rankComment.setContentView(R.layout.rankandcomment);
			         giveRate=(RatingBar)rankComment.findViewById(R.id.rank);
			          comment=(EditText)rankComment.findViewById(R.id.comment);
			         ok = (Button)rankComment.findViewById(R.id.okayButton);
			         cancel=(Button)rankComment.findViewById(R.id.cancelButton);
					rankComment.show();
			        ok.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							rateToSend = giveRate.getRating();
							commentToSend = comment.getText().toString();
							Toast.makeText(getActivity(), commentToSend, Toast.LENGTH_SHORT).show();
						new finishTrekking().execute();
						}
					});
			        cancel.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
						rankComment.dismiss();	
						}
					});
			        
			        	
			        }

				}
				
			});
	        
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
	        
	        title.setText(name);
	        description.setText(descString);
	        
	     
	        return rootView;
	        	}
	private class finishTrekking extends AsyncTask<String, String, String> {
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
			params.add(new BasicNameValuePair("play_trekking_on_the_route_id",totrid));
			params.add(new BasicNameValuePair("trekking_on_the_route_id",id));
			params.add(new BasicNameValuePair("rate",String.valueOf(rateToSend)));
			params.add(new BasicNameValuePair("comment",commentToSend));
			JSONObject jObj = jsonParser.makeHttpRequest(
					COMPLETE_TOTR, "GET", params);
			Log.v("response",jObj.toString());
			return null;
		}
		  @Override
	        protected void onPostExecute(String result) {
	            super.onPostExecute(result);
	         
	            if (pDialog.isShowing())
	                pDialog.dismiss();
	         
	         
	        }
	}
	private class finishFindtoSee extends AsyncTask<String, String, String> {
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
			Log.d("comment",commentToSend);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user_id",String.valueOf(user_idtoSend)));
			params.add(new BasicNameValuePair("play_find_to_see_id",totrid));
			params.add(new BasicNameValuePair("find_to_see_id",id));
			params.add(new BasicNameValuePair("rate",String.valueOf(rateToSend)));
			params.add(new BasicNameValuePair("comment",commentToSend));
			JSONObject jObj = jsonParser.makeHttpRequest(
					COMPLETE_FTS, "GET", params);
			try {
				success = jObj.get("status").toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v("response",jObj.toString());
			return null;
		}
		  @Override
	        protected void onPostExecute(String result) {
	            super.onPostExecute(result);
	         
	            if (pDialog.isShowing())
	                pDialog.dismiss();
	            if(success.equals("200")){
	            	dialog.dismiss();
	            	Toast.makeText(getActivity(), "Game Finished!",Toast.LENGTH_SHORT).show();
	            }
	            
	         
	        }
	}
}


