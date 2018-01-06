package moco.htwg.de.truckparkapp.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.text.DecimalFormat;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
        int minutes = (int) (timeRemaining*60);
        
        LocalTime now = LocalTime.now();
        LocalTime estimatedArrivalTime = now.plusMinutes(minutes);
        int estimatedArrivalTimeInterval = estimatedArrivalTime.plusMinutes(30).getHourOfDay();
        System.out.println("estimatedArrivalTimeInterval: "+estimatedArrivalTimeInterval);
        int nowHour = now.getHourOfDay();
        DateTime dateTime = org.joda.time.LocalDate.now().toDateTime(now);

        String dayOfWeek = dateTime.dayOfWeek().getAsShortText(Locale.ENGLISH);
        String hourOfDay = dateTime.minusHours(3).hourOfDay().getAsShortText(Locale.ENGLISH);
        String key = dayOfWeek + "_" + hourOfDay;
        if(estimatedArrivalTimeInterval == now.plusMinutes(30).getHourOfDay()){
            holder.kilometres.setText("Entf: "+decimalFormat.format(distance) + "\nAnk: " + estimatedArrivalTime.toString("HH:mm"));
            holder.parkinglotsFree.setText("Frei: " + (parkingLot.getMaxParkingLots() - parkingLot.getDevicesAtParkingArea().size())+" / "+parkingLot.getMaxParkingLots());
        } else {
            holder.kilometres.setText("Entf: "+decimalFormat.format(distance) + "\nAnk: " + estimatedArrivalTime.toString("HH:mm"));
            holder.parkinglotsFree.setText("Progn: " + (parkingLot.getMaxParkingLots() - parkingLot.getPrediction().get(key).get(0))+" / "+parkingLot.getMaxParkingLots());
        }


    }

    @Override
    public int getItemCount() {
        return parkingLotList.size();
    }
}
