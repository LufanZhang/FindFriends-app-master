package com.example.map_google;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements
OnMarkerClickListener,
OnInfoWindowClickListener,
OnMarkerDragListener,
OnSeekBarChangeListener{
    private Button list, checkIn,update;
    private TextView longitude,latitude,address;
    private ImageView myImage;
    private GoogleMap mMap;
    private LocationManager locManager;
    private static final double EARTH_RADIUS = 6378137;  
    private Geocoder geocoder;
    private Location location;
    private String locationProvider; 
    private MarkerOptions markerOpt,markerMickey,markerDonald,markerGoofy,markerGarfield;
    private Marker mOpt,mMickey,mDonald,mGoofy,mGarfield;
    private Marker globalMarker;
    private CameraPosition cameraPosition;
    private SQLiteDatabase mysql ;
    private ArrayList<Integer> time;
    private File path = new File("/sdcard/findFriends");
	private File file = new File("/sdcard/findFriends/my_location_history.db");
	private static String MICKEYURL = "http://winlab.rutgers.edu/~huiqing/mickey.png";
	private static String DONALDURL = "http://winlab.rutgers.edu/~huiqing/donald.jpg";
	private static String GOOFYURL = "http://winlab.rutgers.edu/~huiqing/goofy.png";
	private static String GARFIELDURL = "http://winlab.rutgers.edu/~huiqing/garfield.jpg ";
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	findViews();
    	initProvider();
    	
    	mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
    	mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    	LocationListener locationListener = new LocationListener() {
    	    public void onLocationChanged(Location location) {
    	    	updateToNewLocation(location);
    	    }

    	    public void onStatusChanged(String provider, int status, Bundle extras) {
    	    	
    	    }

    	    public void onProviderEnabled(String provider) {
    	    	
    	    }

    	    public void onProviderDisabled(String provider) {
    	    	updateToNewLocation(null);
    	    }
    	  };
          mMap.setOnMarkerClickListener(this);
          mMap.setOnInfoWindowClickListener(this);
          mMap.setOnMarkerDragListener(this);
          mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
          
          locManager.requestLocationUpdates(locationProvider,  60 * 1000, 0, locationListener); 
	}
 
    
    private void updateToNewLocation(Location location){
        mMap.clear();
        time = getTime();
        markerOpt = new MarkerOptions();
        markerMickey = new MarkerOptions();;
        markerDonald = new MarkerOptions();;
        markerGoofy = new MarkerOptions();;
        markerGarfield = new MarkerOptions();;
        double dLong = 0.00;
        double dLat = 0.00;
        
        Double[] lat_long = new Double[] { location.getLatitude(), location.getLongitude() };
		new ReverseGeocodingTask(getBaseContext()).execute(lat_long);
        
        if(location != null){
        	dLong = location.getLongitude();
        	dLat = location.getLatitude();
        	longitude.setText("Longitude:"+dLong);
        	latitude.setText("Latitude:"+dLat);

        	String distanceMickey = DistanceOfTwoPoints(dLat,dLong,40.517838,-74.465297);
        	String distanceDonald = DistanceOfTwoPoints(dLat,dLong,40.513189,-74.433849);
        	String distanceGoofy = DistanceOfTwoPoints(dLat,dLong,40.495527,-74.467142);
        	String distanceGafield = DistanceOfTwoPoints(dLat,dLong,40.497127,-74.417056);
        	
        	initMarker(dLat,dLong,markerOpt,"0.00");
        	markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        	initMarker(40.517838,-74.465297,markerMickey,distanceMickey);
        	markerMickey.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        	initMarker(40.513189,-74.433849,markerDonald,distanceDonald);
        	markerDonald.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        	initMarker(40.495527,-74.467142,markerGoofy,distanceGoofy);
        	markerGoofy.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        	initMarker(40.497127,-74.417056,markerGarfield,distanceGafield);
        	markerGarfield.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        	
        	markerOpt.anchor(0.5f, 0.5f);
        	mOpt = mMap.addMarker(markerOpt);
        	mMickey = mMap.addMarker(markerMickey);
        	mDonald = mMap.addMarker(markerDonald);
        	mGoofy = mMap.addMarker(markerGoofy);
        	mGarfield = mMap.addMarker(markerGarfield);

        	cameraPosition = new CameraPosition.Builder()
        		.target(new LatLng(dLat, dLong))              
            	.zoom(12)                   
            	.bearing(0)                
            	.tilt(30)                  
            	.build();                   
            	mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    
    private void initMarker(double dLat,double dLong,MarkerOptions marker,String distance){
    	marker.position(new LatLng(dLat, dLong));
    	marker.draggable(false);
    	marker.visible(true);
    	if(marker!=markerOpt)
    	marker.snippet("he is "+distance + "miles away from you");
    	else
    	marker.snippet("hear I am!");
    }
    private double rad(double d) {  
        return d * Math.PI / 180.0;  
    } 
    public String DistanceOfTwoPoints(double lat1,double lng1,   
            double lat2,double lng2) { 
    	
       double radLat1 = rad(lat1);  
       double radLat2 = rad(lat2);  
       double a = radLat1 - radLat2;  
       double b = rad(lng1) - rad(lng2);  
       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)  
               + Math.cos(radLat1) * Math.cos(radLat2)  
               * Math.pow(Math.sin(b / 2), 2)));  
       s = s * EARTH_RADIUS;  
       s = s * 10000 / 10000/1500;  
       DecimalFormat newFormat = new DecimalFormat("##0.00");
       String result =  newFormat.format(s);

       return result;  
     }  
    private void initProvider() {
    	locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	locationProvider = LocationManager.NETWORK_PROVIDER;
    	location = locManager.getLastKnownLocation(locationProvider);
    }
    
    private void findViews() {
    	list = (Button) findViewById(R.id.listBtn);
    	list.setOnClickListener(new listListener());
    	checkIn = (Button) findViewById(R.id.checkInBtn);
    	checkIn.setOnClickListener(new CheckInListener());
    	update = (Button)findViewById(R.id.updateBtn);
    	update.setOnClickListener(new updateListener());
       	longitude = (TextView) findViewById(R.id.longitude);
    	latitude = (TextView) findViewById(R.id.latitude);
    	address = (TextView) findViewById(R.id.address);
     }
    //use AsyncTask to get the address from latitude and longitude
	private class ReverseGeocodingTask extends AsyncTask<Double, Void, String> {
		Context mContext;

		public ReverseGeocodingTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected String doInBackground(Double... params) {
			geocoder = new Geocoder(mContext, Locale.ENGLISH);
			double latitude = params[0].doubleValue();
			double longitude = params[1].doubleValue();

			List<Address> addresses = null;
			String addr = "";

			try {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				addr+=address.getAddressLine(0)+",";
				addr+=address.getLocality()+",";
				addr+=address.getPostalCode()+",";
				addr+=address.getCountryName();
			}
			return addr;
		}
		@Override
		protected void onPostExecute(String addressText) {
			address.setText(addressText);
		}
	}
  class updateListener implements OnClickListener{

	@Override
	public void onClick(View v) {
		//executeAsyncTask();
		location = locManager.getLastKnownLocation(locationProvider);
		time = getTime();
		Toast.makeText(getApplicationContext(), "your location has been updated!", Toast.LENGTH_SHORT).show();
	}
	  
  }
  //this check in is involved with SOLiteDatabase
  class CheckInListener implements OnClickListener{
		@Override
		public void onClick(View v) {
	        		if (!path.exists()) {
	        			path.mkdirs();
	        		}
	        		if (!file.exists()) {
	        			try {
	        				file.createNewFile();
	        			} catch (IOException e) {
	        				e.printStackTrace();
	        			}
	        		}
	        		mysql = SQLiteDatabase.openOrCreateDatabase(file, null); 
	        		 String TABLE_NAME = "location_ht";  
	        	        String ID = "id";  
	        	        String T = "Ctime";
	        	        String LOCATION = "location";  
	        	        String str_sql2 = "create table if not exists " + TABLE_NAME + "(" 
	        	        					+ ID  + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        	        					+ T + " text,"
	        	        					+ LOCATION + " text );";  
	        	        mysql.execSQL(str_sql2); 
	        	        ContentValues cv = new ContentValues();
	        			String value = (String) address.getText();
	        			ArrayList<Integer> timeCheckIn = getTime();
	        			String timeCheckInString = timeCheckIn.get(3)+":"+timeCheckIn.get(4)+":"+timeCheckIn.get(5)+"   "+timeCheckIn.get(1)+"/"+timeCheckIn.get(2)+"/"+timeCheckIn.get(0);
	        			cv.put(MySQLiteOpenHelper.LOCATION, value);
	        			cv.put(MySQLiteOpenHelper.T, timeCheckInString);
	        			mysql.insert(MySQLiteOpenHelper.TABLE_NAME, null, cv);
					}

    }
	public ArrayList<Integer> getTime(){
		Time t=new Time(); 
		t.setToNow(); 
		ArrayList<Integer> timeList = new ArrayList<Integer>();
		int year = t.year;  
		int month = t.month;  
		int date = t.monthDay;  
		int hour = t.hour+1; 
		int minute = t.minute;  
		int second = t.second;
		timeList.add(year);
		timeList.add(month);
		timeList.add(date);
		timeList.add(hour);
		timeList.add(minute);
		timeList.add(second);
		return timeList;
	}
//this will go other activity
  class listListener implements OnClickListener{
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(MainActivity.this,ListView_location.class);
		startActivity(intent);
	}
	  
  }
@Override
public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	// TODO Auto-generated method stub
}
@Override
public void onStartTrackingTouch(SeekBar seekBar) {
	// TODO Auto-generated method stub
}
@Override
public void onStopTrackingTouch(SeekBar seekBar) {
	// TODO Auto-generated method stub
	
}
@Override
public void onMarkerDrag(Marker marker) {
	// TODO Auto-generated method stub
}
@Override
public void onMarkerDragEnd(Marker marker) {
	// TODO Auto-generated method stub
	
}


@Override
public void onMarkerDragStart(Marker marker) {
	// TODO Auto-generated method stub
}
@Override
public void onInfoWindowClick(Marker marker) {
	// TODO Auto-generated method stub
	Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
}
@Override
public boolean onMarkerClick(Marker marker) {
	
	globalMarker = marker;
	ArrayList<Integer> currentTime = getTime();
	String currenTimeString = "";
	int timeDiff = (currentTime.get(4)-time.get(4))*60+currentTime.get(5)-time.get(5);

	if(timeDiff>=60)
		currenTimeString+=String.valueOf(currentTime.get(4)-time.get(4))+" minutes";
	
	currenTimeString+=String.valueOf(timeDiff%60)+" seconds";
	marker.setTitle(currenTimeString+" ago");
	//use AsyncTask to download corresponding picture of certain marker and show the view
	executeAsyncTask(marker);

	if (marker.equals(mOpt)) {
		return false;
    } 
    return true;
    }
public void executeAsyncTask(Marker marker) {
	myImage.setImageResource(R.drawable.ic_launcher);//set the default image when waiting loading picture
	marker.showInfoWindow();
	BitMapWorkTask bitMapWorkTask = new BitMapWorkTask();
	String imageUrl = "";
	if (marker.equals(mMickey)) {
        imageUrl = MICKEYURL;
    } else if (marker.equals(mDonald)) {
        imageUrl = DONALDURL;
    } else if (marker.equals(mGoofy)) {
        imageUrl = GOOFYURL;
    } else if (marker.equals(mGarfield)) {
        imageUrl = GARFIELDURL;
    }
	bitMapWorkTask.execute(imageUrl);
}
class BitMapWorkTask extends AsyncTask<String, Void, Bitmap> {
	//when bitMapWorkTask.execute(imageUrl) get called, this function will be executed, get the url as argument
	//download picture from web
	@Override
	protected Bitmap doInBackground(String... params) {
		return decodeStreamBitmap(params[0]);
	}
//get the Bitmap from above function and give this Bitmap as resource of myImage
//and call showInfoWindow to update the picture
	@Override
	protected void onPostExecute(Bitmap result) {
		if (result != null) {  
	        myImage.setImageBitmap(result);  
	        globalMarker.showInfoWindow();
	        
	        } 
	}
}
public Bitmap decodeStreamBitmap(String uri) {
	Bitmap bitmap = null;
	HttpGet get = new HttpGet(uri);
	try {
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		InputStream inputStream = entity.getContent();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(inputStream, null, options);
		options.inJustDecodeBounds = false;
		response = client.execute(get);
		entity = response.getEntity();
		inputStream = entity.getContent();
		bitmap = BitmapFactory.decodeStream(inputStream, null, options);

	} catch (Exception e) {
		e.printStackTrace();
	}
	return bitmap;
}


 class CustomInfoWindowAdapter implements InfoWindowAdapter {
	
    private final View mWindow;

    CustomInfoWindowAdapter() {
        mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        myImage = ((ImageView) mWindow.findViewById(R.id.badge));
    }
	//View mWindow, show this layout;
    public View getInfoWindow(Marker marker) {
        render(marker, mWindow);
        return mWindow;
    }

    public View getInfoContents(Marker marker) {

    	return null;
    }
    //this function will get all the element of this view;
    private void render(Marker marker, View view) {
        String snippet = marker.getSnippet();
        String title = marker.getTitle();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
            titleUi.setText(titleText);
        } else {
            titleUi.setText("");
        }

    }
}
}

