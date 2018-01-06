package moco.htwg.de.truckparkapp.view.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import moco.htwg.de.truckparkapp.R;
import moco.htwg.de.truckparkapp.model.ParkingLot;

/**
 * Created by Sebastian Thümmel on 03.01.2018.
 */

public class ParkingLotsAdapter extends RecyclerView.Adapter<ParkingLotsAdapter.MyViewHolder> {

    private List<ParkingLot> parkingLotList;
    DecimalFormat decimalFormat;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, kilometres ,parkinglotsFree, arrivingTime, remainingTime;
        public ImageView parkingPredictor;
        public Drawable background;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.parkinglotName);
            kilometres = itemView.findViewById(R.id.parkinglotDistance);
            parkinglotsFree = itemView.findViewById(R.id.parkinglotFree);
            arrivingTime = itemView.findViewById(R.id.arrivingTime);
            background = itemView.getBackground();
            parkingPredictor = itemView.findViewById(R.id.parkingPredictorView);
            remainingTime = itemView.findViewById(R.id.remainingTime);
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

        int nowHour = now.getHourOfDay();
        DateTime dateTime = org.joda.time.LocalDate.now().toDateTime(now);

        String dayOfWeek = dateTime.dayOfWeek().getAsShortText(Locale.ENGLISH);
        String hourOfDay = dateTime.minusHours(3).hourOfDay().getAsShortText(Locale.ENGLISH);
        String key = dayOfWeek + "_" + hourOfDay;
        int occupancy;

        if(estimatedArrivalTimeInterval == now.plusMinutes(30).getHourOfDay()){
            occupancy = (parkingLot.getMaxParkingLots() - parkingLot.getDevicesAtParkingArea().size());

            holder.kilometres.setText(decimalFormat.format(distance));
            holder.remainingTime.setText(getRemainingTime(minutes));
            holder.arrivingTime.setText(estimatedArrivalTime.toString("HH:mm")+ " Uhr");
            holder.parkinglotsFree.setText(occupancy +" / "+parkingLot.getMaxParkingLots());
        } else {
            occupancy = (parkingLot.getMaxParkingLots() - parkingLot.getPrediction().get(key).get(0));
            holder.kilometres.setText(decimalFormat.format(distance));
            holder.remainingTime.setText(getRemainingTime(minutes));
            holder.arrivingTime.setText(estimatedArrivalTime.toString("HH:mm")+ " Uhr");

            holder.parkinglotsFree.setText(occupancy+" / "+parkingLot.getMaxParkingLots());
        }
        double occupencyRate = (double)occupancy / (double)parkingLot.getMaxParkingLots();

        if(occupencyRate > 0.3 ){
            holder.parkingPredictor.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.parking_green, null));
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.green_google_light));
        } else if(occupencyRate > 0.1){
            holder.parkingPredictor.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.parking_yellow, null));
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.yellow_google_light));

        } else {
            holder.parkingPredictor.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.parking_red, null));
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.red_google_light));
        }
    }

    private String getRemainingTime(int timeInMinutes){

        String remaining;
        if(timeInMinutes<60){
            remaining = String.valueOf(timeInMinutes)+" min";
        } else {
            int hours = timeInMinutes/60;
            int minutes = timeInMinutes%60;
            remaining = String.valueOf(hours)+"h "+String.valueOf(minutes)+"min";
        }
        return remaining;
    }

    @Override
    public int getItemCount() {
        return parkingLotList.size();
    }
}
