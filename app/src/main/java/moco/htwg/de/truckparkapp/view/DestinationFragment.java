package moco.htwg.de.truckparkapp.view;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import moco.htwg.de.truckparkapp.R;
import moco.htwg.de.truckparkapp.parking.TruckParkLot;

/**
 * Created by Sebastian Thümmel on 28.12.2017.
 */

public class DestinationFragment extends Fragment {

    public DestinationFragment(){}

    public static DestinationFragment newInstance() {
        DestinationFragment fragment = new DestinationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_destination, container, false);

        /**
         * hard coded destination only for testing
         */
        final TextView destinationSteet = view.findViewById(R.id.destination_street);
        destinationSteet.setText("Königsstraße");
        final TextView destinationPostal = view.findViewById(R.id.destination_postal_number);
        destinationPostal.setText("70173");
        final TextView destinationPlace = view.findViewById(R.id.destination_place);
        destinationPlace.setText("Stuttgart");
        TruckParkLot.getInstance();

        ImageButton getRouteButton = view.findViewById(R.id.button_destination_get_route);
        getRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("FRAGMENT_INTENT");
                intent.putExtra("FragmentAction", "START_MAP");
                intent.putExtra("DESTINATION_STREET", destinationSteet.getText().toString());
                intent.putExtra("DESTINATION_POSTAL", destinationPostal.getText().toString());
                intent.putExtra("DESTINATION_PLACE", destinationPlace.getText().toString());

                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            }
        });
        return view;
    }
}
