package moco.htwg.de.truckparkapp.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.List;

import moco.htwg.de.truckparkapp.R;
import moco.htwg.de.truckparkapp.model.ParkingLot;
import moco.htwg.de.truckparkapp.parking.TruckParkLot;
import moco.htwg.de.truckparkapp.view.adapter.ParkingLotsAdapter;

/**
 * Created by Sebastian Thümmel on 02.01.2018.
 */

public class ParkingLotListFragment extends Fragment {

    private static final String TAG = "ParkingLotListFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 60;

    private List<ParkingLot> parkingLots;
    private RecyclerView recyclerView;

    private LayoutManagerType currentLayoutManagerType;


    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    public static ParkingLotListFragment newInstance() {
        return new ParkingLotListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parkingLots = TruckParkLot.getInstance().getParkingLotsOnRoute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        rootView.setTag(TAG);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);


        currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if(savedInstanceState != null){
            currentLayoutManagerType = (LayoutManagerType)savedInstanceState.getSerializable(KEY_LAYOUT_MANAGER);
        }

        setRecyclerViewLayoutManager(currentLayoutManagerType);

        //
        //recyclerView.setAdapter(parkingLotsAdapter);

        return rootView;
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType){

        int scrollPosition = 0;

        if(recyclerView.getLayoutManager() != null){
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }
/*
        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                layoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                currentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                layoutManager = new LinearLayoutManager(getActivity());
                currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                layoutManager = new LinearLayoutManager(getActivity());
                currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(scrollPosition);*/
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, currentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

}
