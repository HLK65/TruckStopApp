package moco.htwg.de.truckparkapp.model;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

/**
 * Created by Sebastian Thümmel on 23.12.2017.
 */

public class ParkingLot {

    private String name;

    private PolygonOptions polygonOptions;

    private int maxParkingLots;

    private int parkingLotsOccupied;

    public ParkingLot(){}

    public ParkingLot(LatLng... latLngs){
        this.polygonOptions = new PolygonOptions().add(latLngs);
    }

    public ParkingLot( int maxParkingLots, LatLng... latLngs){
        this.polygonOptions = new PolygonOptions().add(latLngs);
        this.maxParkingLots = maxParkingLots;
    }

    public void showParkingLotOnMap(int color){
        polygonOptions.strokeColor(color);
    }

    public void hideParkingLotOnMap(){
        polygonOptions.visible(false);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PolygonOptions getPolygonOptions() {
        return polygonOptions;
    }

    public void setPolygonOptions(PolygonOptions polygonOptions) {
        this.polygonOptions = polygonOptions;
    }

    public int getMaxParkingLots() {
        return maxParkingLots;
    }

    public void setMaxParkingLots(int maxParkingLots) {
        this.maxParkingLots = maxParkingLots;
    }

    public int getParkingLotsOccupied() {
        return parkingLotsOccupied;
    }

    public void setParkingLotsOccupied(int parkingLotsOccupied) {
        this.parkingLotsOccupied = parkingLotsOccupied;
    }

    @Override
    public String toString() {
        return "ParkingLot{" +
                "name='" + name + '\'' +
                ", polygonOptions=" + polygonOptions +
                ", maxParkingLots=" + maxParkingLots +
                ", parkingLotsOccupied=" + parkingLotsOccupied +
                '}';
    }
}
