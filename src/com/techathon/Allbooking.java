package com.techathon;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import pack.coderzheaven.R;
 
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
@SuppressLint("NewApi")
public class Allbooking extends ListActivity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    JSONParser jsonParser = new JSONParser();
    ArrayList<HashMap<String, String>> parkingList;
 
    // url to get all products list
    private static String url_all_parking = "http://smartadmin.mybluemix.net/allbooking.php";
    private static final String url_delete_parking = "http://smartadmin.mybluemix.net/deletebooking.php";
	
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "parking";
    private static final String TAG_locat = "location";
    private static final String TAG_dat = "date";
    private static final String TAG_tim = "time";
    private static final String TAG_PID = "sno";
    private String pid;
    private String username;
    private static final String TAG_username = "username";
    // products JSONArray
    JSONArray products = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allbooking);
        Intent intent = getIntent();
    	username = intent.getStringExtra(TAG_username);
        // Hashmap for ListView
        parkingList = new ArrayList<HashMap<String, String>>();
 
        // Loading products in Background Thread
        new LoadAllbooking().execute();
 
        // Get listview
        ListView lv = getListView();
 
        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                 pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();
                new DeleteParking().execute();
                
 
                
            }
        });
 
    }
 
    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
 
    }
 
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    @SuppressLint("NewApi")
	class LoadAllbooking extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Allbooking.this);
            pDialog.setMessage("Loading parking. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
        	
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_parking, "GET", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Parking: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);
 
                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
 
                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String locat = c.getString(TAG_locat);
                        String dat = c.getString(TAG_dat);
                        String tim = c.getString(TAG_tim);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_locat, locat);
                        map.put(TAG_dat, dat);
                        map.put(TAG_tim, tim);
 
                        // adding HashList to ArrayList
                        parkingList.add(map);
                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            Newbooking.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
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
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            Allbooking.this, parkingList,
                            R.layout.list_item, new String[] { TAG_PID,
                                    TAG_locat,TAG_dat,TAG_tim},
                            new int[] { R.id.pid, R.id.locat,R.id.date1,R.id.time1 });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
 
        }
 
    }
    class DeleteParking extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Allbooking.this);
			pDialog.setMessage("cancelling parking...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deleting product
		 * */
		protected String doInBackground(String... args) {

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("sno", pid));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_parking, "POST", params);

				// check your log for json response
				Log.d("Delete Parking", json.toString());
				
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// product successfully deleted
					// notify previous activity by sending code 100
					runOnUiThread(new Runnable() {
					    public void run() {
					Toast.makeText(getApplicationContext(), "cancelled successfully", Toast.LENGTH_LONG).show();
					pDialog.dismiss();
					    }
					});
					Intent i = new Intent(getApplicationContext(),UserPage.class);
					// send result code 100 to notify about product deletion
					i.putExtra("username",username );
					startActivity(i);
					finish();
				}
				else{
					runOnUiThread(new Runnable() {
					    public void run() {
					
					    Toast.makeText(getApplicationContext(), "some error occured try again", Toast.LENGTH_LONG).show();
					    pDialog.dismiss();
					    }
					});
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
			

		}

	}
}
