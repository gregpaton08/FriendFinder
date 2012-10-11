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
import android.view.View;
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
            	Log.i("", "before image");
                downloadImage(_ivFriend1);
            	Log.i("", "after image");
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
    
    public void downloadImage(View v) {
		//((ImageView)v).setImageBitmap(null);
		new DownloadImageTask()
				.execute("http://www.winlab.rutgers.edu/~shubhamj/mickey.png");
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... url) {
			Log.i("image", "doInBack");
			return loadImageFromNetwork(url[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			Log.i("image", "onPost");
			_ivFriend1.setImageBitmap(result);
		}
	}

	private Bitmap loadImageFromNetwork(String url) {
		Bitmap bitmap = null;

		try {
			Log.i("image", "download");
			bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}
}
