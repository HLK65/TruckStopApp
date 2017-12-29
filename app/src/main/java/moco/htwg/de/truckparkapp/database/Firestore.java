package moco.htwg.de.truckparkapp.database;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moco.htwg.de.truckparkapp.R;
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
                        }
                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        return parkingLotMap;
    }

}
