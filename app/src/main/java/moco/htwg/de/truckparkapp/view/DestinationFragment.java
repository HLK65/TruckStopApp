package moco.htwg.de.truckparkapp.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import moco.htwg.de.truckparkapp.R;

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


        Button getRouteButton = view.findViewById(R.id.button_destination_get_route);
        getRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = MapsFragment.newInstance(
                        destinationSteet.getText().toString(),
                        destinationPostal.getText().toString(),
                        destinationPlace.getText().toString());

                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;
    }
}
