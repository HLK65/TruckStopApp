package moco.htwg.de.truckparkapp.view;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

import moco.htwg.de.truckparkapp.R;
import moco.htwg.de.truckparkapp.model.ParkingLot;
import moco.htwg.de.truckparkapp.persistence.Database;
import moco.htwg.de.truckparkapp.persistence.DatabaseFactory;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InputFreeSlotsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InputFreeSlotsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputFreeSlotsFragment extends Fragment {
    private static final String PARKING_LOT_ID = "parkingLotId";
    private final String TAG = this.getClass().getSimpleName();
    private ParkingLot parkingLot;
    private int numberPickerValue = 0;
    private String parkingLotId;
    private OnFragmentInteractionListener mListener;

    public InputFreeSlotsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param parkingLotId db-Id (=name) of parking lot.
     * @return A new instance of fragment InputFreeSlotsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InputFreeSlotsFragment newInstance(String parkingLotId) {
        InputFreeSlotsFragment fragment = new InputFreeSlotsFragment();
        Bundle args = new Bundle();
        args.putString(PARKING_LOT_ID, parkingLotId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parkingLotId = getArguments().getString(PARKING_LOT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_input_free_slots, container, false);

        Database database = DatabaseFactory.getDatabase(DatabaseFactory.Type.FIRESTORE);
        assert database != null;

        NumberPicker np = view.findViewById(R.id.numberPicker);
        np.setEnabled(false); //wait for db data
        np.setOnValueChangedListener((numberPicker, i, i1) -> numberPickerValue = numberPicker.getValue()
                /*Toast.makeText(getContext(),
                "selected number " + numberPicker.getValue(), Toast.LENGTH_SHORT).show()*/);

        Button submitButton = view.findViewById(R.id.button_submit_free_slots);
        submitButton.setEnabled(false);
        submitButton.setOnClickListener(v -> {
            while (parkingLot.getDevicesAtParkingArea().size() < np.getValue()) {
                parkingLot.addDeviceToParkingLot("userUpdate." + System.currentTimeMillis());
            }
            database.updateParkingLot(parkingLot)
                    .addOnSuccessListener(aVoid -> Snackbar.make(view, R.string.updatedFreeSlots, Snackbar.LENGTH_LONG)
                            /*.setAction("Action", null)*/.show());
        });

        TextView textView = view.findViewById(R.id.how_many_trucks);
        String textViewString = textView.getText().toString();
        textViewString = textViewString.replace("%s", parkingLotId);
        textView.setText(textViewString);

        DocumentReference parkingLotRef = database.subscribeParkingLot(parkingLotId);
        parkingLotRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Log.d(TAG, "Current data: " + documentSnapshot.getData());
                parkingLot = documentSnapshot.toObject(ParkingLot.class);
                List<String> devicesCopy = new ArrayList<>(parkingLot.getDevicesAtParkingArea());
                devicesCopy.removeIf(s -> s.contains("userUpdate."));
                np.setMinValue(devicesCopy.size());
                np.setMaxValue(parkingLot.getMaxParkingLots());
                //dont change value after user could have changed it
                if (!np.isEnabled()) {
                    Log.d(TAG, "DevicesAtParkingArea: " + parkingLot.getDevicesAtParkingArea().size());
                    Log.d(TAG, "np Value: " + np.getValue());
                    // must be called after setting min/max!
                    np.setValue(parkingLot.getDevicesAtParkingArea().size());
                    Log.d(TAG, "np Value: " + np.getValue());
                }
                np.setEnabled(true);
                submitButton.setEnabled(true);
            } else {
                Log.d(TAG, "Current data: null");
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
