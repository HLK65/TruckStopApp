package moco.htwg.de.truckparkapp.persistence;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import moco.htwg.de.truckparkapp.model.ParkingLot;

/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public class Firestore implements Database {

    private final String TAG = this.getClass().getSimpleName();

    FirebaseFirestore firebaseFirestore;


    Firestore(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void addParkingLot(ParkingLot parkingLot){
        firebaseFirestore.collection("parkingLots")
            .add(parkingLot)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("FirestoreService", "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            });
    }

    public Map<String, ParkingLot> getParkingLots(){
        final Map<String, ParkingLot> parkingLotMap = new HashMap<>();
        firebaseFirestore.collection("parkingLots")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            ParkingLot newParkinglot = document.toObject(ParkingLot.class);
                            parkingLotMap.put(newParkinglot.getName(), newParkinglot);
                            getRealtimeUpdates("parkingLots", document.getId(), parkingLotMap);
                        }
                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        return parkingLotMap;
    }

    private void getRealtimeUpdates(String collection, String document, final Map<String, ParkingLot> parkingLotMap){
        final DocumentReference documentReference = firebaseFirestore.collection(collection).document(document);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if ( e != null){
                    Log.w(TAG, "Listen failed", e);
                }
                if(documentSnapshot != null && documentSnapshot.exists()){
                    ParkingLot updatedParkingLot = documentSnapshot.toObject(ParkingLot.class);
                    parkingLotMap.put(updatedParkingLot.getName(), updatedParkingLot);
                    Log.d(TAG, "Updated Data: " + updatedParkingLot.toString());
                } else {
                    Log.d(TAG, "Updated Data: null");
                }
            }
        });
    }

}
