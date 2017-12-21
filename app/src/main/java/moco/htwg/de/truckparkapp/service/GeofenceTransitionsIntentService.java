package moco.htwg.de.truckparkapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian Thümmel on 21.12.2017.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = "GeofenceTransIntServ";

    public static final String PARKING_BROADCAST = "Parking", ADDITIONAL_INFO = "additional info";

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate(){
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent != null && geofencingEvent.hasError()){
            Log.e(TAG, Integer.toString(geofencingEvent.getErrorCode()));
            return;
        }

        int geofenceTransition = geofencingEvent != null ? geofencingEvent.getGeofenceTransition() : 0;

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);
            Log.i(TAG, geofenceTransitionDetails);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);
            Log.i(TAG, geofenceTransitionDetails);
            Intent broadCastIntent = new Intent(PARKING_BROADCAST);
            broadCastIntent.putExtra("ADDITIONAL_INFO", "Stop Parking");
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            /*
             * this transaction will be called if device stays for a given time period inside geofence area
             * -> setLoiteringDelay(time in millis) when creating geofence object
             */
            //TODO inkrement/dekrement value in database

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);
            Log.i(TAG, geofenceTransitionDetails);
            Intent broadCastIntent = new Intent(PARKING_BROADCAST);
            broadCastIntent.putExtra("ADDITIONAL_INFO", "Start Parking");
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent);
        } else {
            Log.e(TAG, "unknown geofence type");
        }
    }

    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences) {
        String geofenceTransitionString = getTransitionString(geofenceTransition);
        List<String> triggeringGeofencesIdList = new ArrayList<>();
        for(Geofence geofence : triggeringGeofences){
            triggeringGeofencesIdList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdString = TextUtils.join(", ", triggeringGeofencesIdList);
        return geofenceTransitionString + ": "+ triggeringGeofencesIdString;
    }

    private String getTransitionString(int geofenceTransition) {
        switch (geofenceTransition){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "enter Geofence";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "exit Geofence";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "dwell geofence";
            default:
                return "unknown";
        }
    }
}
