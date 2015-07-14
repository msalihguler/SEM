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
import android.widget.TextView;
import android.widget.Toast;

import com.example.sem_thesis.R;

public class LoginActivity extends Activity {

	private EditText username, password;
	private Button mSubmit;
	private TextView toRegister;
	SessionManagement session;
	 // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();



    //testing on Emulator:
    private static final String LOGIN_URL = "http://theduman.me/api/user_login/";


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		
		//Initializing session manager
		session = new SessionManagement(getApplicationContext());
		
		//setup input fields
		username = (EditText)findViewById(R.id.login_username);
		password = (EditText)findViewById(R.id.login_password);
		toRegister = (TextView)findViewById(R.id.toRegister);
		//setup buttons
		mSubmit = (Button)findViewById(R.id.login_submit);
		
		
		//register listeners
		mSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AttemptLogin().execute();

			}
		});
		toRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LoginActivity.this, com.example.sem_thesis.user.RegisterActivity.class);
				startActivity(i);
            	finish();

			}
		});

	}



	class AttemptLogin extends AsyncTask<String, String, String> {

		 /**
         * Before starting background thread Show Progress Dialog
         * */
		boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
            int success;
            int userid ;
            String usernameTosend = username.getText().toString();
            String passwordTosend = password.getText().toString();
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", usernameTosend));
                params.add(new BasicNameValuePair("password", passwordTosend));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                       LOGIN_URL, "GET", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt("status");
                if (success == 200) {
                	Log.d("Login Successful!", json.toString());
                	userid = json.getInt("user_id");
                	session.createLoginSession(usernameTosend, passwordTosend,userid);
                	Intent i = new Intent(LoginActivity.this, com.example.sem_thesis.SplashScreen.class);
                	
    				startActivity(i);
                	finish();

                	return json.getString(TAG_MESSAGE);
                }else{
                	Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                	return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
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
            	Toast.makeText(LoginActivity.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

	}

}