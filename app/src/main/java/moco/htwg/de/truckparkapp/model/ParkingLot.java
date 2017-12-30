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

    private List<String> devicesAtParkingArea;

    private int maxParkingLots;

    private LatLng geofencePosition;

    private String documentId;

    public ParkingLot(String name, List<LatLng> parkingLotDimensions, int maxParkingLots, LatLng geofencePosition) {
        this.name = name;
        this.parkingLotDimensions = parkingLotDimensions;
        this.maxParkingLots = maxParkingLots;
        this.geofencePosition = geofencePosition;
    }

    public ParkingLot(){
        this.devicesAtParkingArea = new ArrayList<>();
        this.parkingLotDimensions = new ArrayList<>();
    }

    public ParkingLot(LatLng... latLngs){
        this.devicesAtParkingArea = new ArrayList<>();
        this.parkingLotDimensions = Arrays.asList(latLngs);
    }

    public ParkingLot( int maxParkingLots, LatLng... latLngs){
        this.devicesAtParkingArea = new ArrayList<>();
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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public List<String> getDevicesAtParkingArea() {
        return devicesAtParkingArea;
    }

    public void setDevicesAtParkingArea(List<String> devicesAtParkingArea) {
        this.devicesAtParkingArea = devicesAtParkingArea;
    }

    public void addDeviceToParkingLot(String deviceId){
        if(!this.devicesAtParkingArea.contains(deviceId)){
            this.devicesAtParkingArea.add(deviceId);
        }
    }

    public void removeDeviceFromParkingLot(String deviceId){
        if(this.devicesAtParkingArea.contains(deviceId)){
            this.devicesAtParkingArea.remove(deviceId);
        }
    }

    @Override
    public String toString() {
        return "ParkingLot{" +
                "name='" + name + '\'' +
                ", parkingLotDimensions=" + parkingLotDimensions +
                ", maxParkingLots=" + maxParkingLots +
                ", geofencePosition=" + geofencePosition +
                '}';
    }
}
