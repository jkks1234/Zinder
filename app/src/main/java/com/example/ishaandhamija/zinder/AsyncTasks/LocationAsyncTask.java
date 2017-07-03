package com.example.ishaandhamija.zinder.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.ishaandhamija.zinder.Activities.DashboardActivity;
import com.example.ishaandhamija.zinder.Interfaces.GetLL;
import com.example.ishaandhamija.zinder.Utils.GPSTracker;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by ishaandhamija on 02/07/17.
 */

public class LocationAsyncTask extends AsyncTask<LocationManager, Void, Void> {

    public LocationAsyncTask(Location location, LocationManager locationManager, String provide_info) {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Void doInBackground(LocationManager... params) {

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);
    }
}
