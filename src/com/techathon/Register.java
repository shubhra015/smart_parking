package com.techathon;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import pack.coderzheaven.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends Activity {
	Button register;
	EditText ifname,ilname,iusername,ipass,iemail,imobile;
	TextView tv;
	HttpPost httppost;
	StringBuffer buffer;
	HttpResponse response;
	HttpClient httpclient;
	List<NameValuePair> nameValuePairs;
	ProgressDialog dialog = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regisrationpage);
        
        register = (Button)findViewById(R.id.register);  
        ifname = (EditText)findViewById(R.id.fname);
        ilname= (EditText)findViewById(R.id.lname);
        iemail= (EditText)findViewById(R.id.email);
        iusername= (EditText)findViewById(R.id.uname);
        ipass= (EditText)findViewById(R.id.pword);
        imobile= (EditText)findViewById(R.id.mobile);
        
        
        register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(Register.this, "", 
                        "Creating user...", true);
				 new Thread(new Runnable() {
					    public void run() {
					    	registeruser();					      
					    }
					  }).start();				
			}
		});
        
    }
	
	void registeruser(){
		try{			
			 
			httpclient=new DefaultHttpClient();
			httppost= new HttpPost("http://smartadmin.mybluemix.net/register.php"); // make sure the url is correct.
			//add your data
			nameValuePairs = new ArrayList<NameValuePair>(2);
			// Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar, 
			nameValuePairs.add(new BasicNameValuePair("fname",ifname.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("lname",ilname.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("email",iemail.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("username",iusername.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("password",ipass.getText().toString().trim())); 
			nameValuePairs.add(new BasicNameValuePair("mobile",imobile.getText().toString().trim()));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			//Execute HTTP Post Request
			response=httpclient.execute(httppost);
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			final String response = httpclient.execute(httppost, responseHandler);
			System.out.println("Response : " + response); 
			runOnUiThread(new Runnable() {
			    public void run() {
			    	
					dialog.dismiss();
			    }
			});
			
			if(response.equalsIgnoreCase("user created")){
				runOnUiThread(new Runnable() {
				    public void run() {
				    	Toast.makeText(Register.this,"Login Success", Toast.LENGTH_SHORT).show();
				    }
				});
				
				Intent intent = new Intent(getApplicationContext(),UserPage.class);
				intent.putExtra("username",iusername.getText().toString().trim());
				startActivity(intent);
			}else{
				showAlert();				
			}
			
		}catch(Exception e){
			dialog.dismiss();
			System.out.println("Exception : " + e.getMessage());
		}
	}
	public void showAlert(){
Register.this.runOnUiThread(new Runnable() {
		    public void run() {
		    	AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
		    	builder.setTitle("Login Error.");
		    	builder.setMessage("User not Found.")  
		    	       .setCancelable(false)
		    	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	           }
		    	       });		    	       
		    	AlertDialog alert = builder.create();
		    	alert.show();		    	
		    }
		});
	}
}