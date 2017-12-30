package moco.htwg.de.truckparkapp.model;




import com.google.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sebastian Thümmel on 23.12.2017.
 */

public class ParkingLot {

    private String name;

    private List<LatLng> parkingLotDimensions;

    private int maxParkingLots;

    private int parkingLotsOccupied;

    private LatLng geofencePosition;

    public ParkingLot(String name, List<LatLng> parkingLotDimensions, int maxParkingLots, int parkingLotsOccupied, LatLng geofencePosition) {
        this.name = name;
        this.parkingLotDimensions = parkingLotDimensions;
        this.maxParkingLots = maxParkingLots;
        this.parkingLotsOccupied = parkingLotsOccupied;
        this.geofencePosition = geofencePosition;
    }

    public ParkingLot(){}

    public ParkingLot(LatLng... latLngs){
        this.parkingLotDimensions = Arrays.asList(latLngs);
    }

    public ParkingLot( int maxParkingLots, LatLng... latLngs){
        this.parkingLotDimensions = Arrays.asList(latLngs);
        this.maxParkingLots = maxParkingLots;
    }

    public List<LatLng> getParkingLotDimensions() {
        return parkingLotDimensions;
    }

    public void setParkingLotDimensions(List<LatLng> parkingLotDimensions) {
        this.parkingLotDimensions = parkingLotDimensions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LatLng getGeofencePosition() {
        return geofencePosition;
    }

    public void setGeofencePosition(LatLng geofencePosition) {
        this.geofencePosition = geofencePosition;
    }

    /*
     * necessary conversion because PolygonOptions needs latlng coordinates from type com.google.android.gms.maps.model.LatLng
     * and this type causes problems in firestore
     *
     */
    public List<com.google.android.gms.maps.model.LatLng> getLatLngForPolygonOptions(){
        List<com.google.android.gms.maps.model.LatLng> returnList = new ArrayList<>();
        for(LatLng latLng : this.parkingLotDimensions){
            returnList.add(new com.google.android.gms.maps.model.LatLng(latLng.lat, latLng.lng));
        }
        return returnList;
    }

    @Override
    public String toString() {
        return "ParkingLot{" +
                "name='" + name + '\'' +
                ", parkingLotDimensions=" + parkingLotDimensions +
                ", maxParkingLots=" + maxParkingLots +
                ", parkingLotsOccupied=" + parkingLotsOccupied +
                ", geofencePosition=" + geofencePosition +
                '}';
    }
}
