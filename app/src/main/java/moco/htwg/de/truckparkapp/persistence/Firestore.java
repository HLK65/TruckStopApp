package moco.htwg.de.truckparkapp.persistence;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

import moco.htwg.de.truckparkapp.model.ParkingLot;

/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public class Firestore implements Database {

    private final String TAG = this.getClass().getSimpleName();

    private FirebaseFirestore firebaseFirestore;

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
    public void addDevicesToParkingArea(String parkingLotName, List<String> deviceIds) {
        firebaseFirestore.collection("parkingLots").document(parkingLotName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> devices = documentSnapshot
                            .toObject(ParkingLot.class)
                            .getDevicesAtParkingArea();
                    deviceIds.addAll(devices);

                    firebaseFirestore.collection("parkingLots").document(parkingLotName).collection("devicesAtParkingArea").add(deviceIds)
                            .addOnSuccessListener(documentReference -> Log.d(TAG, "addDeviceToParkingArea: success"))
                            .addOnFailureListener(e -> Log.w(TAG, "addDeviceToParkingArea: " + e));
                })
                .addOnFailureListener(e -> Log.w(TAG, "addDevicesToParkingArea: ", e));
    }

    @Override
    public DocumentReference subscribeParkingLot(String parkingLotId) {
        return firebaseFirestore.collection("parkingLots").document(parkingLotId);
    }

}
