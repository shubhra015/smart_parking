package com.techathon;

import pack.coderzheaven.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserPage extends Activity {
	Button newbooking,editbooking,cancelbooking;
    private String username;
    private static final String TAG_username = "username";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userpage);
        Intent intent = getIntent();
    	username = intent.getStringExtra(TAG_username);
        newbooking =(Button)findViewById(R.id.button1);
        editbooking =(Button)findViewById(R.id.button2);
        cancelbooking=(Button)findViewById(R.id.button3);
        newbooking.setOnClickListener(new View.OnClickListener() {
        	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(getApplicationContext(),Newbooking.class);
				intent.putExtra("username", username);
				startActivity(intent);
			}
		});
editbooking.setOnClickListener(new View.OnClickListener() {
        	
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(getApplicationContext(),Viewbooking.class);
				intent.putExtra("username", username);
				startActivity(intent);
			}
		});
cancelbooking.setOnClickListener(new View.OnClickListener() {
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent(getApplicationContext(),Allbooking.class);
		intent.putExtra("username", username);
		startActivity(intent);
	}
});
    }
}