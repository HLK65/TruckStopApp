package moco.htwg.de.truckparkapp.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import moco.htwg.de.truckparkapp.model.ParkingLot;

/**
 * Created by Sebastian Thümmel on 27.12.2017.
 */

public class FirestoreService {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirestoreService(){
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document:task.getResult()){
                        Log.d("FirestoreService", document.getId()+" => " + document.getData());
                    }
                }
            }
        });
    }

    public void addParkingLot(ParkingLot parkingLot){
        Map<String, ParkingLot> parkingLotMap = new HashMap<>();
        db.collection("parkingLots").add(parkingLot).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("FirestoreService", "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        });
    }

}
