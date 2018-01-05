package moco.htwg.de.truckparkapp.parking;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import moco.htwg.de.truckparkapp.model.ParkingLot;
import moco.htwg.de.truckparkapp.persistence.Database;
import moco.htwg.de.truckparkapp.persistence.DatabaseFactory;
import moco.htwg.de.truckparkapp.view.adapter.ParkingLotsAdapter;

/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public class TruckParkLot {

    private final String TAG = this.getClass().getSimpleName();

    private static TruckParkLot truckParkLot;

    private Map<String, ParkingLot> parkingLots;
    private List<ParkingLot> parkingLotsOnRoute;
    private List<Geofence> geofenceList;

    private ParkingLotsAdapter parkingLotsAdapter;

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
                    }
                } else {
                    Log.w(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void liveUpdateParkingLotsOnRoute(final ParkingLot parkingLot){

        DocumentReference documentReference = database.getRealtimeUpdates("parkingLots", parkingLot.getName(), parkingLotsOnRoute);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if ( e != null){
                    Log.w(TAG, "Listen failed", e);
                }
                if(documentSnapshot != null && documentSnapshot.exists()){
                    ParkingLot updatedParkingLot = documentSnapshot.toObject(ParkingLot.class);
                    int index = -1;
                    for(ParkingLot foundParkingLot : parkingLotsOnRoute){
                        if(updatedParkingLot.getName().equals(foundParkingLot.getName())){
                            index = parkingLotsOnRoute.indexOf(foundParkingLot);
                        }
                    }
                    if(index != -1){
                        parkingLotsOnRoute.remove(index);
                        parkingLotsOnRoute.add(updatedParkingLot);
                    }

                    sortParkingLotsOnRouteByDistance();
                    parkingLotsAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Updated Data: " + updatedParkingLot.toString());
                } else {
                    Log.d(TAG, "Updated Data: null");
                }
            }
        });

    }

    public boolean getParkingLotsOnRouteAndAddToParkingListOnRoute(Iterator<String> keys, ParkingLotsAdapter adapter){
        parkingLotsAdapter = adapter;
        List<String> parkingLotsOnRouteNames = new ArrayList<>();
        while (keys.hasNext()){
            parkingLotsOnRouteNames.add(keys.next());
        }
        ParkingLot parkingLot = null;
        for(String parkingLotName : parkingLotsOnRouteNames){
            parkingLot = this.parkingLots.get(parkingLotName);
            ParkingLot finalParkingLot = parkingLot;

            Optional<ParkingLot> optionalParkinglot = this.parkingLotsOnRoute.stream()
                    .filter(parkingLot1 -> parkingLot1
                            .getName().equals(finalParkingLot.getName()))
                    .findFirst();

            if(!optionalParkinglot.isPresent()){
                this.parkingLotsOnRoute.add(parkingLot);
            }
            if(!optionalParkinglot.isPresent()){
                this.geofenceList.add(createGeofence(parkingLot));
            }
            liveUpdateParkingLotsOnRoute(parkingLot);
        }
        return parkingLotsOnRouteNames.size() == this.parkingLotsOnRoute.size();
    }

    public Map<String, ParkingLot> getParkingLots() {
        return parkingLots;
    }

    public void addParkingLot(ParkingLot parkingLot){
        parkingLots.put(parkingLot.getName(), parkingLot);
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

    public Task<Void> updateParkingLot(ParkingLot parkingLot){
        return database.updateParkingLot(parkingLot);
    }



    public List<ParkingLot> getParkingLotsOnRoute() {
        sortParkingLotsOnRouteByDistance();
        return parkingLotsOnRoute;
    }

    public void setParkingLotsOnRoute(List<ParkingLot> parkingLotsOnRoute) {
        this.parkingLotsOnRoute = parkingLotsOnRoute;
    }

    public void calculateDistanceToParkingLot(LatLng currentPosition){
        for(ParkingLot parkingLot : parkingLotsOnRoute){
            float[] results = new float[1];
            Location.distanceBetween(parkingLot.getGeofencePosition().lat, parkingLot.getGeofencePosition().lng, currentPosition.lat, currentPosition.lng, results);
            parkingLot.setDistanceFromCurrentLocationInKilometres(results[0]/1000);
        }
        sortParkingLotsOnRouteByDistance();
        if(parkingLotsAdapter != null) parkingLotsAdapter.notifyDataSetChanged();
    }

    public void removePassedParkingLotFromParkingLotsOnRouteList(ParkingLot passedParkingLot){
        this.parkingLotsOnRoute.remove(passedParkingLot);
        if(parkingLotsAdapter != null){
            parkingLotsAdapter.notifyDataSetChanged();
        }
    }

    private void sortParkingLotsOnRouteByDistance(){
        parkingLotsOnRoute.sort(new Comparator<ParkingLot>() {
            @Override
            public int compare(ParkingLot o1, ParkingLot o2) {
                if (o1.getDistanceFromCurrentLocationInKilometres() < o2.getDistanceFromCurrentLocationInKilometres()) {
                    return -1;
                } else if (o1.getDistanceFromCurrentLocationInKilometres() == o2.getDistanceFromCurrentLocationInKilometres()) {
                    return 0;
                } else if (o1.getDistanceFromCurrentLocationInKilometres() > o2.getDistanceFromCurrentLocationInKilometres()) {
                    return 1;
                }
                return 0;
            }
        });
    }
}
