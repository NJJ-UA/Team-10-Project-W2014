package ca.ualberta.team10projectw2014;

import java.util.ArrayList;
import java.util.Calendar;

import ca.ualberta.team10projectw2014.controller.CommentDataController;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateCommentActivity extends Activity{
	
	private String postTitle;
	private String postUsername;
	private String postContents;
	private LocationModel postLocation;
	private Bitmap postPhoto;
	private CommentModel parentModel;
	private EditText ueditText;
	private EditText teditText;
	private EditText ceditText;
	protected double longitude;
	protected double latitude;
 	protected Location bestKnownLoc = null;
 	protected CommentModel model;
 	protected LocationListenerController locationListener;
 	protected LocationManager mLocationManager;
 	protected Boolean gpsEnabled;
 	protected Boolean netEnabled;
 	private CommentDataController CDC;
	
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_comment_activity);
        CDC = new CommentDataController(this, this.getString(R.string.file_name_string));
        
//        //Receive information from the intent
//        Bundle bundle = getIntent().getExtras();
//        String receivedUsername = (String) bundle.getString("username", null);
//        CommentModel receivedComment = (CommentModel) bundle.getSerializable("comment");
//        fillContents(receivedUsername, receivedComment);
        

        
        
      
        ueditText = (EditText)findViewById(R.id.cc_username);
		teditText = (EditText)findViewById(R.id.cc_title);
		ceditText = (EditText)findViewById(R.id.cc_content);
	}
	
	
	@Override 
	protected void onResume(){
		super.onResume();
		//TODO Check to see if GPS is enabled
        //TODO Start listening for location information
        startListeningLocation();
	}
	
	// The following class is a direct copy from http://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled received on March 9 at 2:00PM
	protected void noGPSError(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                    dialog.cancel();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	private void startListeningLocation(){
		Toast.makeText(getBaseContext(), "Starting to listen for location...", Toast.LENGTH_LONG).show();
		LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		

	    if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	    	gpsEnabled = false;
	        noGPSError();
	    }
	    
	    else{
	    	gpsEnabled = true;
	    }
	    
	    if ( !mLocationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
	    	netEnabled = false;
	    }
	    else{
	    	netEnabled = true;
	    }

    	LocationListenerController locationListener = new LocationListenerController(this);  
    	if (gpsEnabled){
    		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 60 * 1000 , 10, locationListener);
    	}
    	if (netEnabled){
    		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5 * 60 * 1000 , 10, locationListener);
    	}
	}

	// Retrieved from http://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android on March 6 at 5:00
	
	private Location getLastBestLocation() {

	    Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

	    long GPSLocationTime = 0;
	    if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

	    long NetLocationTime = 0;

	    if (null != locationNet) {
	        NetLocationTime = locationNet.getTime();
	    }

	    if ( 0 < GPSLocationTime - NetLocationTime ) {
	        return locationGPS;
	    }
	    else{
	        return locationNet;
	    }

	}

	public void fillContents(String username, CommentModel parentModel){
		setLocation();
		if(username != null){
			this.postUsername = username;
			setUsernameView(username);
		}
		else{
			if (this.postUsername == null){
				this.postUsername = null;
			}
			//setUsernameView("Please set a username");
		}
		if(parentModel != null){
			this.parentModel = parentModel;
			this.postTitle = "RE:" + parentModel.getTitle();
			teditText.setKeyListener(null);
			setTitleView(postTitle);
		}
		else{
			if (this.postTitle == null){
				this.postTitle = null;
			}
			//setTitleView("Create a Name for Your Post");
		}
	}
	
	private void setUsernameView(String name){
		ueditText.setText(name, TextView.BufferType.EDITABLE);
	}
	
	private void setTitleView(String title){
		teditText.setText(title, TextView.BufferType.EDITABLE);
	}

	public void chooseLocation(View v){
		Toast.makeText(getBaseContext(), "You Want to Choose a Location, Eh?", Toast.LENGTH_LONG).show();
	}
	
	public void choosePhoto(View v){
		Toast.makeText(getBaseContext(), "I Don't Remember Asking You To Take a Picture", Toast.LENGTH_LONG).show();
	}
	
	private void setLocation(){
		this.postLocation = new LocationModel("TITLE", bestKnownLoc);
		//TODO Set location variable to...?
		// Location should never be null
	}
	
	//Called when the user presses "Post" button
	public void attemptCommentCreation(View v){
		
		this.postContents = ceditText.getText().toString();
		this.postUsername = ueditText.getText().toString();
		this.postTitle = teditText.getText().toString();
		
		if(this.postContents == null){
			raiseContentsIncompleteError();
		}
		if(this.postUsername == null){
			raiseUsernameIncompleteError();
		}
		if(this.postTitle == null){
			raiseTitleIncompleteError();
		}
		if(this.postTitle != null && this.postUsername != null && this.postContents != null){
			
			if(this.parentModel != null){
				// This should be edited so that the model handles all the getting and setting
				model = new SubCommentModel(this.parentModel);
				model.setAuthor(this.postUsername);
				model.setContent(this.postContents);
				model.setLocation(this.postLocation);
				model.setPhoto(this.postPhoto);
				model.setTitle(this.postTitle);
				
				// Sets the current date and time for the comment
				// Referenced http://stackoverflow.com/questions/16686298/string-timestamp-to-calendar-in-java on March 2
				long timestamp = System.currentTimeMillis();
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(timestamp);
				model.setTimestamp(calendar);
				model.setNumFavourites(0);
				
				//Adds the newly created model to its referrent's list of subcomments
				this.parentModel.addSubComment((SubCommentModel) model);
			}
			else{
				// This should be edited so that the model handles all the getting and setting
				model = new CommentModel();
				model.setAuthor(this.postUsername);
				model.setContent(this.postContents);
				model.setLocation(this.postLocation);
				model.setPhoto(this.postPhoto);
				model.setTitle(this.postTitle);
				
				long timestamp = System.currentTimeMillis();
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(timestamp);
				model.setTimestamp(calendar);
				model.setNumFavourites(0);
				
				//TODO Add this head comment to the list of head comments on the phone
			
			}
			
			//TODO Stop listening for location information

			//stopListeningLocation();
			//setLocation();
			//model.setLocation(this.postLocation);


			ArrayList<CommentModel> tempList = CDC.loadFromFile();

			tempList.add(model);
			//TODO Save the new list
			CDC.saveToFile(tempList);
			
			
			//Destroy this activity so that we return to the previous one.
			goBack();
		}
	}
	
	private void stopListeningLocation(){
		bestKnownLoc = getLastBestLocation();
		if (bestKnownLoc == null){
			Toast.makeText(getBaseContext(), "Ain't no location here", Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(getBaseContext(), "Getting location...", Toast.LENGTH_LONG).show();
			Location location = locationListener.getCurrentBestLocation();
			bestKnownLoc.setLatitude(location.getLatitude());
			bestKnownLoc.setLongitude(location.getLongitude());
			mLocationManager.removeUpdates(locationListener);
		}
	}
	
	private void raiseContentsIncompleteError(){
		Toast.makeText(getBaseContext(), "Please Add Contents to Your Post", Toast.LENGTH_LONG).show();
	}
	
	private void raiseUsernameIncompleteError(){
		Toast.makeText(getBaseContext(), "Please Add a Username", Toast.LENGTH_LONG).show();
	}
	
	private void raiseTitleIncompleteError(){
		Toast.makeText(getBaseContext(), "Please Create a Title", Toast.LENGTH_LONG).show();
	}
	
	private void goBack(){
		finish();
	}
	
	
}
