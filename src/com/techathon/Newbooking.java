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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Newbooking extends Activity implements OnItemSelectedListener{
	
	HttpPost httppost;
	StringBuffer buffer;
	HttpResponse response;
	HttpClient httpclient;
	EditText date,time;
	List<NameValuePair> nameValuePairs;
	ProgressDialog dialog = null;
	Spinner spinner,spinner1;
	Button submit;
	private String username;
	private static final String TAG_username = "username";
	private String item,item1;
	
	private static final String TAG_EMAIL = "username";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_page);
        Intent intent = getIntent();
    	username = intent.getStringExtra(TAG_username);
        // Spinner element
       spinner = (Spinner) findViewById(R.id.spinner1);
       spinner1 = (Spinner) findViewById(R.id.spinner2);
       submit = (Button)findViewById(R.id.button1);
       date = (EditText)findViewById(R.id.date);
       time = (EditText)findViewById(R.id.time);
       
        // Spinner click listener
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				item = parent.getItemAtPosition(position).toString();	
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				item1 = parent.getItemAtPosition(position).toString();
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Forum-koramangala");
        categories.add("jayadeva Hospital");
        categories.add("Brigade Road");
        categories.add("Meenakshi mall");
        categories.add("gopalan Mall");
        categories.add("Iskon Temple");
        
        List<String> categories1 = new ArrayList<String>();
        categories1.add("two wheeler");
        categories1.add("four wheeler");
        
       
        
        // Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories1);
		
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		
		// attaching data adapter to spinner
		spinner.setAdapter(dataAdapter);
		spinner1.setAdapter(dataAdapter1);
		 submit.setOnClickListener(new OnClickListener() {
			
			
				@Override
				public void onClick(View v) {
					dialog = ProgressDialog.show(Newbooking.this, "", 
	                        "Creating user...", true);
					 new Thread(new Runnable() {
						    public void run() {
						    	newparking();					      
						    }
						  }).start();
						
					
				}
			});
    }
    void newparking(){
		try{			
			 
			httpclient=new DefaultHttpClient();
			httppost= new HttpPost("http://smartadmin.mybluemix.net/newbooking.php"); // make sure the url is correct.
			//add your data
			nameValuePairs = new ArrayList<NameValuePair>(2);
			// Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar, 
			nameValuePairs.add(new BasicNameValuePair("location",item));
			nameValuePairs.add(new BasicNameValuePair("type",item1));
			nameValuePairs.add(new BasicNameValuePair("date",date.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("time",time.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("username",username));
			
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
			
			if(response.equalsIgnoreCase("parking reserved")){
				runOnUiThread(new Runnable() {
				    public void run() {
				    	Toast.makeText(Newbooking.this,"parking reserved", Toast.LENGTH_SHORT).show();
				    }
				});
				
				Intent intent = new Intent(getApplicationContext(),UserPage.class);
				intent.putExtra("username", username);
				startActivity(intent);
			}else if(response.equalsIgnoreCase("parking is full")){
				runOnUiThread(new Runnable() {
				    public void run() {
				    	AlertDialog.Builder builder = new AlertDialog.Builder(Newbooking.this);
				    	builder.setTitle("Parking full");
				    	builder.setMessage("Sorry!! Parking is full for the selected time slot.please try someother slot")  
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
			else{
				showAlert();				
			}
			
		}catch(Exception e){
			dialog.dismiss();
			System.out.println("Exception : " + e.getMessage());
		}
	}
	public void showAlert(){
Newbooking.this.runOnUiThread(new Runnable() {
		    public void run() {
		    	AlertDialog.Builder builder = new AlertDialog.Builder(Newbooking.this);
		    	builder.setTitle("some error occured try again");
		    	builder.setMessage("some error occured try again after sometime.")  
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
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
    
    
	

}