package moco.htwg.de.truckparkapp.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import moco.htwg.de.truckparkapp.R;
import moco.htwg.de.truckparkapp.model.ParkingLot;

/**
 * Created by Sebastian Thümmel on 03.01.2018.
 */

public class ParkingLotsAdapter extends RecyclerView.Adapter<ParkingLotsAdapter.MyViewHolder> {

    private List<ParkingLot> parkingLotList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, kilometres ,parkinglotsFree;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.parkinglotName);
            kilometres = (TextView) itemView.findViewById(R.id.parkinglotDistance);
            parkinglotsFree = (TextView) itemView.findViewById(R.id.parkinglotFree);
        }
    }

    public ParkingLotsAdapter(List<ParkingLot> parkingLotList){
        this.parkingLotList = parkingLotList;
    }


    @Override
    public ParkingLotsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parkinglot_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ParkingLotsAdapter.MyViewHolder holder, int position) {
        ParkingLot parkingLot = parkingLotList.get(position);
        holder.name.setText(parkingLot.getName());
        //parkingLot.getMaxParkingLots()-parkingLot.getDevicesAtParkingArea().size()
        holder.parkinglotsFree.setText("1");
    }

    @Override
    public int getItemCount() {
        return parkingLotList.size();
    }
}
