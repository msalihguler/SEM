package com.example.sem_thesis.user;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sem_thesis.R;

public class RegisterActivity extends Activity {

	EditText username,password,email,name,surname;
    Button  mRegister,backtoLogin;
    String usernameT,passwordT,emailT,nameT,surnameT;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

  
    private static final String REGISTER_URL = "http://theduman.me/api/user_register/";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_page);

		username = (EditText)findViewById(R.id.register_username);
		password = (EditText)findViewById(R.id.register_password);
		email = (EditText)findViewById(R.id.register_email);
		name = (EditText)findViewById(R.id.register_name);
		surname = (EditText)findViewById(R.id.register_surname);
		
		mRegister = (Button)findViewById(R.id.register_submit);
		backtoLogin = (Button)findViewById(R.id.toLogin);
		usernameT=username.getText().toString();
		passwordT=password.getText().toString();
		emailT=email.getText().toString();
		nameT=name.getText().toString();
		surnameT=surname.getText().toString();
		mRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//	if((usernameT.trim().equals("")||passwordT.trim().equals("")||emailT.trim().equals("")||nameT.trim().equals("")||surnameT.trim().equals("")))
				//	Toast.makeText(getApplicationContext(), "Please fill all the information",Toast.LENGTH_SHORT).show();

				//else
					new CreateUser().execute();

			}
		});
		backtoLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			  	Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            	startActivity(intent);
            	finish();
			}
		});
	}


	class CreateUser extends AsyncTask<String, String, String> {

		 /**
         * Before starting background thread Show Progress Dialog
         * */
		boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Creating User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
            int success;
            String usernameTosend = username.getText().toString();
            String passwordTosend = password.getText().toString();
            String nameTosend = name.getText().toString();
            String surnameTosend = surname.getText().toString();
            String emailTosend = email.getText().toString();
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", usernameTosend));
                params.add(new BasicNameValuePair("password", passwordTosend));
                params.add(new BasicNameValuePair("name", nameTosend));
                params.add(new BasicNameValuePair("surname", surnameTosend));
                params.add(new BasicNameValuePair("email", emailTosend));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                       REGISTER_URL, "GET", params);

                // full json response
                Log.d("Login attempt", json.toString());

                // json success element
               	Log.d("User Created!", json.toString());
               	Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            	startActivity(intent);
                	finish();
                	
                	return json.getString(TAG_MESSAGE);
                }catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

		}
		/**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
            	Toast.makeText(RegisterActivity.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

	}

}