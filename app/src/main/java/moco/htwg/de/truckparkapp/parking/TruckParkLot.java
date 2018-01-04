package moco.htwg.de.truckparkapp.parking;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import moco.htwg.de.truckparkapp.model.ParkingLot;
import moco.htwg.de.truckparkapp.persistence.Database;
import moco.htwg.de.truckparkapp.persistence.DatabaseFactory;

/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public class TruckParkLot {

    private final String TAG = this.getClass().getSimpleName();

    private static TruckParkLot truckParkLot;

    private Map<String, ParkingLot> parkingLots;
    private List<ParkingLot> parkingLotsOnRoute;
    private List<Geofence> geofenceList;


    private Database database;

    public static TruckParkLot getInstance(){
        if(truckParkLot == null){
            truckParkLot = new TruckParkLot();
        }
        return truckParkLot;
    }

    private TruckParkLot(){
        this.database = DatabaseFactory.getDatabase(DatabaseFactory.Type.FIRESTORE);
        parkingLots = new HashMap<>();
        geofenceList = new ArrayList<>();
        parkingLotsOnRoute = new ArrayList<>();
        database.getParkingLots("parkingLots").addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        ParkingLot newParkinglot = document.toObject(ParkingLot.class);
                        addParkingLot(newParkinglot);
                        database.getRealtimeUpdates("parkingLots", document.getId(), parkingLots);
                    }
                } else {
                    Log.w(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public boolean getParkingLotsOnRouteAndAddToParkingListOnRoute(Iterator<String> keys){
        List<String> parkingLotsOnRouteNames = new ArrayList<>();
        while (keys.hasNext()){
            parkingLotsOnRouteNames.add(keys.next());
        }
        ParkingLot parkingLot = null;
        for(String parkingLotName : parkingLotsOnRouteNames){
            parkingLot = this.parkingLots.get(parkingLotName);
            this.parkingLotsOnRoute.add(parkingLot);
            this.geofenceList.add(createGeofence(parkingLot));
        }
        return parkingLotsOnRouteNames.size() == this.parkingLotsOnRoute.size();
    }

    public Map<String, ParkingLot> getParkingLots() {
        return parkingLots;
    }

    public void addParkingLot(ParkingLot parkingLot){
        parkingLots.put(parkingLot.getName(), parkingLot);
        //parkingLotsOnRoute.add(parkingLot);
        //geofenceList.add(createGeofence(parkingLot));
    }

    private Geofence createGeofence(ParkingLot parkingLot){
        return new Geofence.Builder()
                .setRequestId(parkingLot.getName())
                .setCircularRegion(
                        parkingLot.getGeofencePosition().lat,
                        parkingLot.getGeofencePosition().lng, 200)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(10000)
                .build();
    }

    public List<Geofence> getGeofenceList() {
        return geofenceList;
    }

    public void saveNewParkingLot(ParkingLot parkingLot){
        database.addParkingLot(parkingLot);
    }

    public void updateParkingLot(ParkingLot parkingLot){
        database.updateParkingLot(parkingLot);
    }

    public List<ParkingLot> getParkingLotsOnRoute() {
        return parkingLotsOnRoute;
    }

    public void setParkingLotsOnRoute(List<ParkingLot> parkingLotsOnRoute) {
        this.parkingLotsOnRoute = parkingLotsOnRoute;
    }
}
