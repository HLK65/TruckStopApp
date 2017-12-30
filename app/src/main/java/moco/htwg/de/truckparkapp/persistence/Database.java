package moco.htwg.de.truckparkapp.persistence;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import moco.htwg.de.truckparkapp.model.ParkingLot;

/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public interface Database {

    void addParkingLot(ParkingLot parkingLot);
    Task<QuerySnapshot> getParkingLots(String collection);
    void getRealtimeUpdates(String collection, String document, final Map<String, ParkingLot> parkingLotMap);

}
