package com.example.sem_thesis;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ArrayRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.sem_thesis.user.JSONParser;
import com.example.sem_thesis.user.SessionManagement;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AddGameFragment extends SherlockFragment {

	//Declaring of variables
	Spinner spinner;
	View rootView;
	ImageButton toMap;
	String category,findToSeeCoordinates,path,TOTRCoordinations;
	Fragment findToSee = new FindToSeeActivity();
	Fragment totr = new TrekkingOntheRoute();
	TextView coordinateScreen;
	Button addPhoto,sendToServer;
	Uri imageUri;
	ProgressDialog pDialog;
	EditText title,explanation;
	SessionManagement session;
	HashMap<String, String> user;
	HashMap<String, Integer> user_id; 
	JSONParser jsonParser = new JSONParser();
	ImageView previewImage;
	InputStream inputStream;
	Bitmap bm;
	String selectedImagePath="",selectedImageCode="",fileName="";
	RequestParams params = new RequestParams();

	   ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

	private static final String FINDTOSEE_URL = "http://theduman.me/api/save_find_to_see/";
	private static final String TOTR_URL = "http://theduman.me/api/save_trekking_on_the_route/";

	private static final String PHOTO_UPLOAD = "http://theduman.me/upload.php";


	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.newgame, container, false);

		//Declaring of our objects
		session = new SessionManagement(getActivity().getApplicationContext());
		sendToServer = (Button)rootView.findViewById(R.id.uploadToServer);
		toMap = (ImageButton)rootView.findViewById(R.id.toMap);
		coordinateScreen = (TextView)rootView.findViewById(R.id.coordinatesWritten);
		coordinateScreen.setVisibility(View.INVISIBLE);
		spinner = (Spinner) rootView.findViewById(R.id.spinnerGameType);
		title=(EditText)rootView.findViewById(R.id.editText1);
		explanation=(EditText)rootView.findViewById(R.id.editText2);
		addPhoto = (Button)rootView.findViewById(R.id.pickPhotoType);
		previewImage=(ImageView)rootView.findViewById(R.id.imagePreview);
		user = session.getUserDetails();
		user_id = session.getUserID();
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
				if(category.equals("Find to See")){
					addPhoto.setVisibility(View.VISIBLE);
				}else{
					addPhoto.setVisibility(View.INVISIBLE);
				}
				
				Toast.makeText(getActivity(),spinner.getSelectedItem().toString() , Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});

		//Getting Location List from the map
		try{
			
			findToSeeCoordinates = getArguments().getStringArrayList("findtoSee").get(1).toString() +"  "+ getArguments().getStringArrayList("findtoSee").get(2).toString();
			coordinateScreen.setVisibility(View.VISIBLE);
			coordinateScreen.setText(findToSeeCoordinates);
		}catch(Exception e){

			Log.i("hata",e.toString());

		}
	try{
	
			TOTRCoordinations = getArguments().getStringArrayList("trekkingOnTheRoute").get(0)+"  "+ getArguments().getStringArrayList("trekkingOnTheRoute").get(1);
			coordinateScreen.setVisibility(View.VISIBLE);
			coordinateScreen.setText(TOTRCoordinations);

		}catch(Exception e){

			Log.i("hata",e.toString());

		}
	
	




		//photo type picker
		addPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Add Photo!");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						if (items[item].equals("Take Photo")) {
							Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
							File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
							startActivityForResult(intent, 1);
						} else if (items[item].equals("Choose from Library")) {
							Intent intent = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							intent.setType("image/*");
							startActivityForResult(
									Intent.createChooser(intent, "Select File"),
									2);
						} else if (items[item].equals("Cancel")) {
							dialog.dismiss();
						}
					}
				});
				builder.show();
			}
		});

		//Changing map according to category
		toMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				if(category.equals("Find to See")){
					ft.replace(R.id.content_frame, findToSee);
					ft.commit();

				}else if (category.equals("Trekking on the Route")) {
					ft.replace(R.id.content_frame, totr);
					ft.commit();
				}

			}
		});
		sendToServer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(category.equals("Find to See"))
					new CreateFindToseeGame().execute();
				else if(category.equals("Trekking on the Route"))
					new CreateTrekkingOnTheRoute().execute();
			}
		});

		return rootView;

	}
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * THIS CODE BELONGS TO TREKKING ON THE ROUTE GAME
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * */
	
	class CreateTrekkingOnTheRoute extends AsyncTask<String,String, String>{
		ArrayList<ArrayList<String>> arrayReceived;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Creating Game...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();		}


		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub  
			int success;
			String titleTosend = title.getText().toString();
			String explanationTosend = explanation.getText().toString();
			int  user_idtoSend = user_id.get(SessionManagement.KEY_USERID);
			String latitude =  getArguments().getStringArrayList("trekkingOnTheRoute").get(0);
			String longitude =  getArguments().getStringArrayList("trekkingOnTheRoute").get(1);
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("name", titleTosend));
				params.add(new BasicNameValuePair("description", explanationTosend));
				params.add(new BasicNameValuePair("latitude", latitude));
				params.add(new BasicNameValuePair("longitude", longitude));
				params.add(new BasicNameValuePair("user_id", String.valueOf(user_idtoSend)));
			
				Log.d("saveGame", "starting");
		
				//Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(
						TOTR_URL, "GET", params);
				Log.d("Login attempt", json.toString());

				// json success element
				Log.d("Game Created!", json.toString());

				return json.getString("success");

			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
		
			Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
		}

		
	}
	
	
	
	
	
	
	
/*
 * 
 * 
 * 
 * 
 * 
 * THIS CODE BELONGS TO FIND TO SEE GAME 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * */
	
	class CreateFindToseeGame extends AsyncTask<String, String, String>{

		boolean failure = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Creating Game...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub  
			int success;
			String titleTosend = title.getText().toString();
			String explanationTosend = explanation.getText().toString();
			int  user_idtoSend = user_id.get(SessionManagement.KEY_USERID);
			String latitude = getArguments().getStringArrayList("findtoSee").get(1).toString();
			String longitude = getArguments().getStringArrayList("findtoSee").get(2).toString();
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("name", titleTosend));
				params.add(new BasicNameValuePair("description", explanationTosend));
				params.add(new BasicNameValuePair("latitude", latitude));
				params.add(new BasicNameValuePair("longitude", longitude));
				params.add(new BasicNameValuePair("user_id", String.valueOf(user_idtoSend)));
				if(!selectedImagePath.equals(""))
					params.add(new BasicNameValuePair("image", "1"));
				else
					params.add(new BasicNameValuePair("image", "0"));


				Log.d("saveGame", "starting");
		
				//Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(
						FINDTOSEE_URL, "GET", params);
				Log.d("Login attempt", json.toString());

				// json success element
				Log.d("User Created!", json.toString());
				fileName=json.getString("lastInsertedID");

				return json.getString("success");

			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			if(!selectedImagePath.equals(""))
				encodeImagetoString();
			Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
		}

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == 1) {
				 File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
				 selectedImagePath= file.getPath();
			       Bitmap bitmap = decodeSampledBitmapFromFile(selectedImagePath, 1000, 700);
					 selectedImagePath= file.getPath();

			       previewImage.setImageBitmap(bitmap);
			}else if (requestCode == 2) {
				
				
				
				   Uri selectedImage = data.getData();
	                String[] filePathColumn = { MediaStore.Images.Media.DATA };
	 
	                // Get the cursor
	                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
	                        filePathColumn, null, null, null);
	                // Move to first row
	                cursor.moveToFirst();
	 
	                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	                selectedImagePath = cursor.getString(columnIndex);
	                cursor.close();
	                
	                // Set the Image in ImageView
	                previewImage.setImageBitmap(BitmapFactory
	                        .decodeFile(selectedImagePath));
	           
	                // Put file name in Async Http Post Param which will used in Php web app
	                
	                Log.i("abooooooo", fileName);
		
			}
		}
		
	}
	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) 
	{ // BEST QUALITY MATCH
	     
	    //First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);
	 
	    // Calculate inSampleSize, Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    options.inPreferredConfig = Bitmap.Config.RGB_565;
	    int inSampleSize = 1;
	 
	    if (height > reqHeight) 
	    {
	        inSampleSize = Math.round((float)height / (float)reqHeight);
	    }
	    int expectedWidth = width / inSampleSize;
	 
	    if (expectedWidth > reqWidth) 
	    {
	        //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
	        inSampleSize = Math.round((float)width / (float)reqWidth);
	    }
	 
	    options.inSampleSize = inSampleSize;
	 
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	 
	    return BitmapFactory.decodeFile(path, options);
	}
	 public void encodeImagetoString() {
	        new AsyncTask<Void, Void, String>() {
	 
	            protected void onPreExecute() {
	            	super.onPreExecute();
	    			pDialog = new ProgressDialog(getActivity());
	    			pDialog.setMessage("Uploading Image...");
	    			pDialog.setIndeterminate(false);
	    			pDialog.setCancelable(true);
	    			pDialog.show();
	            };
	 
	            @Override
	            protected String doInBackground(Void... params) {
	                BitmapFactory.Options options = null;
	                options = new BitmapFactory.Options();
	                options.inSampleSize = 3;
	                bm = BitmapFactory.decodeFile(selectedImagePath,
	                        options);
	                ByteArrayOutputStream stream = new ByteArrayOutputStream();
	                // Must compress the Image to reduce image size to make upload easy
	                bm.compress(Bitmap.CompressFormat.JPEG, 40, stream); 
	                byte[] byte_arr = stream.toByteArray();
	                // Encode Image to String
	                selectedImageCode = Base64.encodeToString(byte_arr, 0);
	                return "";
	            }
	 
	            @Override
	            protected void onPostExecute(String msg) {
	                pDialog.setMessage("Calling Upload");
	                // Put converted Image string into Async Http Post param
	                params.put("filename", fileName+".jpg");

	                params.put("image", selectedImageCode);
	                // Trigger Image upload
	                triggerImageUpload();
	            }
	        }.execute(null, null, null);
	    }
	     
	    public void triggerImageUpload() {
	        makeHTTPCall();
	    }
	 
	    // Make Http call to upload Image to Php server
	    public void makeHTTPCall() {
	        AsyncHttpClient client = new AsyncHttpClient();
	        client.post(PHOTO_UPLOAD,
	                params, new AsyncHttpResponseHandler() {
	            		@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							String statusCode = (new String(arg2));
						     pDialog.hide();
		                        // When Http response code is '404'
		                        if (arg0 == 404) {
		                            Toast.makeText(getActivity(),
		                                    "Requested resource not found",
		                                    Toast.LENGTH_LONG).show();
		                        }
		                        // When Http response code is '500'
		                        else if (arg0 == 500) {
		                            Toast.makeText(getActivity(),
		                                    "Something went wrong at server end",
		                                    Toast.LENGTH_LONG).show();
		                        }
		                        else if (arg0 == 403) {
		                            Log.v("", statusCode);
		                        }
		                        // When Http response code other than 404, 500
		                        else {
		                            Toast.makeText(
		                                   getActivity(),
		                                    "Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
		                                            + statusCode, Toast.LENGTH_LONG)
		                                    .show();
		                            Log.v("", statusCode);

		                        }
						  					
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							  pDialog.hide();
		                        Toast.makeText(getActivity(), (new String(arg2)),
		                                Toast.LENGTH_LONG).show();		
							
						}

				
	                });
	    }

}
