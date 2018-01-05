package moco.htwg.de.truckparkapp.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import moco.htwg.de.truckparkapp.R;
import moco.htwg.de.truckparkapp.model.ParkingLot;

/**
 * Created by Sebastian Thümmel on 03.01.2018.
 */

public class ParkingLotsAdapter extends RecyclerView.Adapter<ParkingLotsAdapter.MyViewHolder> {

    private List<ParkingLot> parkingLotList;
    DecimalFormat decimalFormat;

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
        decimalFormat = new DecimalFormat("#.# km");
    }


    @Override
    public ParkingLotsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parkinglot_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ParkingLotsAdapter.MyViewHolder holder, int position) {
        ParkingLot parkingLot = (ParkingLot) new ArrayList(parkingLotList).get(position);
        holder.name.setText(parkingLot.getName());
        double distance = parkingLot.getDistanceFromCurrentLocationInKilometres();
        double timeRemaining = distance/70;
        long minutes = (long) (timeRemaining*60);
        if(minutes > 60){
            long hours = TimeUnit.MINUTES.toHours(minutes);
            long restMinutes = minutes%60;
            holder.kilometres.setText(decimalFormat.format(distance) + " /\n" + hours+" h "+restMinutes+" min");
        } else {
            holder.kilometres.setText(decimalFormat.format(distance) + " /\n" + minutes+" min");
        }

        holder.parkinglotsFree.setText("Frei: " + (parkingLot.getMaxParkingLots() - parkingLot.getDevicesAtParkingArea().size())+" / "+parkingLot.getMaxParkingLots());

    }

    @Override
    public int getItemCount() {
        return parkingLotList.size();
    }
}
