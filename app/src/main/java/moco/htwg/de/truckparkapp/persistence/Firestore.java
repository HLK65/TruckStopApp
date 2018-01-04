package moco.htwg.de.truckparkapp.persistence;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Set;

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

    public Task<QuerySnapshot> getParkingLots(String collection){
        return firebaseFirestore.collection(collection).get();
    }

    public DocumentReference getRealtimeUpdates(String collection, String document, final Set<ParkingLot> parkingLotSet){
        return firebaseFirestore.collection(collection).document(document);

    }

    @Override
    public void updateParkingLot(ParkingLot parkingLot) {
        firebaseFirestore.collection("parkingLots").document(parkingLot.getName()).set(parkingLot);
    }

}
