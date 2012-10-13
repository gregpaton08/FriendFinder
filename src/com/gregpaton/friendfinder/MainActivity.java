package com.gregpaton.friendfinder;

import java.io.InputStream;
import java.net.URL;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private final String TAG = this.getClass().getSimpleName();
	
	TextView _tvLocation;
	TextView _tvFriend1;
	TextView _tvFriend2;
	TextView _tvFriend3;
	TextView _tvFriend4;
	ImageView _ivFriend1;
	ImageView _ivFriend2;
	ImageView _ivFriend3;
	ImageView _ivFriend4;
	Friend friend1;
	Friend friend2;
	Friend friend3;
	Friend friend4;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.i(TAG, "Main thread: " + Thread.currentThread().getId());
        
        _tvLocation = (TextView) findViewById(R.id.tvLocation);
        _tvFriend1 = (TextView) findViewById(R.id.tvFriend1);
        _tvFriend2 = (TextView) findViewById(R.id.tvFriend2);
        _tvFriend3 = (TextView) findViewById(R.id.tvFriend3);
        _tvFriend4 = (TextView) findViewById(R.id.tvFriend4);
        _ivFriend1 = (ImageView) findViewById(R.id.ivFriend1);
        _ivFriend2 = (ImageView) findViewById(R.id.ivFriend2);
        _ivFriend3 = (ImageView) findViewById(R.id.ivFriend3);
        _ivFriend4 = (ImageView) findViewById(R.id.ivFriend4);

        friend1 = new Friend("Mickey", _tvFriend1, _ivFriend1, "http://winlab.rutgers.edu/~shubhamj/mickey.png");
        friend2 = new Friend("Donald", _tvFriend2, _ivFriend2, "http://winlab.rutgers.edu/~shubhamj/donald.jpg");
        friend3 = new Friend("Goofy", _tvFriend3, _ivFriend3, "http://winlab.rutgers.edu/~shubhamj/goofy.png");
        friend4 = new Friend("Garfield", _tvFriend4, _ivFriend4, "http://winlab.rutgers.edu/~shubhamj/garfield.jpg");
        
        // download and display friend images
		new DownloadImageTask().execute(friend1, friend2, friend3, friend4);
        
        //Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);

        		String locationText = "Time: " + "Lat = " + location.getLatitude() + " Long = " + location.getLongitude() + "\n";
            	
            	if (location != null)
            	{
            		//Toast.makeText(getApplicationContext(), locationText, Toast.LENGTH_SHORT).show();
            		_tvLocation.setText(locationText);
            	}
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
    	    }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    // class to handle downloading friend images. Allows at most two
    // concurrent downloads
	private class DownloadImageTask extends AsyncTask<Friend, Void, Friend> {
		@Override
		protected Friend doInBackground(Friend... friend) {
			int length = friend.length;
			// If downloading one image, process request when possible
			if (length == 1) {
				Friend.waitForOthers();
				friend[0].setWorking(true);
				friend[0].bitmap = loadImageFromNetwork(friend[0].url);
				return friend[0];
			}
			// if downloading two images, create second thread and 
			// process first request when possible
			if (length == 2) {	
				new DownloadImageTask().execute(friend[1]);
				Friend.waitForOthers();
				friend[0].setWorking(true);
				friend[0].bitmap = loadImageFromNetwork(friend[0].url);
				return friend[0];
			}
			// if downloading more than two images, create threads for 
			// first all images
			if (length > 2) {
				for (int i = 0; i < length; i+=2) {
					if (i+1 < length)
						new DownloadImageTask().execute(friend[i], friend[i+1]);
					else
						new DownloadImageTask().execute(friend[i]);
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Friend friend) {
			if (friend == null)
				return;
			friend.imageView.setImageBitmap(friend.bitmap);
			friend.setWorking(false);
		}
	}

	private Bitmap loadImageFromNetwork(String url) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}
}


