package moco.htwg.de.truckparkapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
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

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent != null && geofencingEvent.hasError()){
            Log.e(TAG, Integer.toString(geofencingEvent.getErrorCode()));
            return;
        }

        int geofenceTransition = geofencingEvent != null ? geofencingEvent.getGeofenceTransition() : 0;

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);
            //TODO inkrement/dekrement value in database
            Log.i(TAG, geofenceTransitionDetails);
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
            default:
                return "unknown";
        }
    }
}
