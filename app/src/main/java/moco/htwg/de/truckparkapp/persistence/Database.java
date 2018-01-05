package moco.htwg.de.truckparkapp.persistence;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import moco.htwg.de.truckparkapp.model.ParkingLot;

/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public interface Database {

    Task<DocumentReference> addParkingLot(ParkingLot parkingLot);
    Task<QuerySnapshot> getParkingLots(String collection);
    DocumentReference getRealtimeUpdates(String collection, String document, final List<ParkingLot> parkingLotSet);

    Task<Void> updateParkingLot(ParkingLot parkingLot);

    DocumentReference subscribeParkingLot(String parkingLotId);

}
