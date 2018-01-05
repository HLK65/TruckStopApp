package moco.htwg.de.truckparkapp.persistence;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import moco.htwg.de.truckparkapp.model.ParkingLot;

/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public class Firestore implements Database {

    private final String TAG = this.getClass().getSimpleName();

    FirebaseFirestore firebaseFirestore;

    Firestore() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> addParkingLot(ParkingLot parkingLot) {
        return firebaseFirestore.collection("parkingLots")
                .add(parkingLot)
                .addOnSuccessListener(documentReference -> Log.d("FirestoreService", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, parkingLot.getName() + "could not be saved"));
    }

    public Task<QuerySnapshot> getParkingLots(String collection) {
        return firebaseFirestore.collection(collection).get();
    }

    public DocumentReference getRealtimeUpdates(String collection, String document, final List<ParkingLot> parkingLotSet) {
        return firebaseFirestore.collection(collection).document(document);
    }

    @Override
    public Task<Void> updateParkingLot(ParkingLot parkingLot) {
        return firebaseFirestore.collection("parkingLots").document(parkingLot.getName())
                .set(parkingLot)
                .addOnSuccessListener(documentReference -> Log.d("FirestoreService", "ParkingLot updated with Name: " + parkingLot.getName()))
                .addOnFailureListener(e -> Log.w(TAG, parkingLot.getName() + "could not be saved"));
    }

    @Override
    public DocumentReference subscribeParkingLot(String parkingLotId) {
        return firebaseFirestore.collection("parkingLots").document(parkingLotId);
    }

}
