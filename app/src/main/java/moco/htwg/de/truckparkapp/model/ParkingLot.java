package moco.htwg.de.truckparkapp.model;




import com.google.firebase.firestore.Exclude;
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

    private Directions drivingDirection;

    private LatLng geofencePosition;

    @Exclude
    private double distanceFromCurrentLocationInKilometres;

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

    public List<String> getDevicesAtParkingArea() {
        return devicesAtParkingArea;
    }

    public void setDevicesAtParkingArea(List<String> devicesAtParkingArea) {
        this.devicesAtParkingArea = devicesAtParkingArea;
    }

    public boolean addDeviceToParkingLot(String deviceId){
        if(!this.devicesAtParkingArea.contains(deviceId)){
            this.devicesAtParkingArea.add(deviceId);
            return true;
        }
        return false;
    }

    public void removeDeviceFromParkingLot(String deviceId){
        if(this.devicesAtParkingArea.contains(deviceId)){
            this.devicesAtParkingArea.remove(deviceId);
        }
    }

    public Directions getDrivingDirection() {
        return drivingDirection;
    }

    public void setDrivingDirection(Directions drivingDirection) {
        this.drivingDirection = drivingDirection;
    }

    @Exclude
    public double getDistanceFromCurrentLocationInKilometres() {
        return distanceFromCurrentLocationInKilometres;
    }

    @Exclude
    public void setDistanceFromCurrentLocationInKilometres(double distanceFromCurrentLocationInKilometres) {
        this.distanceFromCurrentLocationInKilometres = distanceFromCurrentLocationInKilometres;
    }

    @Override
    public String toString() {
        return "ParkingLot{" +
                "name='" + name + '\'' +
                ", parkingLotDimensions=" + parkingLotDimensions +
                ", devicesAtParkingArea=" + devicesAtParkingArea +
                ", maxParkingLots=" + maxParkingLots +
                ", drivingDirection=" + drivingDirection +
                ", geofencePosition=" + geofencePosition +
                ", distanceFromCurrentLocationInKilometres=" + distanceFromCurrentLocationInKilometres +
                '}';
    }

    public enum Directions {
        NORTH (270,90), EAST(0,180), SOUTH(90,270), WEST(180,360), ALL(0,360);

        private double lowerBoundaryDirection;
        private double upperBoundaryDirection;

        Directions(double lowerBoundaryDirection, double upperBoundaryDirection){
            this.lowerBoundaryDirection = lowerBoundaryDirection;
            this.upperBoundaryDirection = upperBoundaryDirection;
        }

        public double getLowerBoundaryDirection() {
            return lowerBoundaryDirection;
        }

        public void setLowerBoundaryDirection(double lowerBoundaryDirection) {
            this.lowerBoundaryDirection = lowerBoundaryDirection;
        }

        public double getUpperBoundaryDirection() {
            return upperBoundaryDirection;
        }

        public void setUpperBoundaryDirection(double upperBoundaryDirection) {
            this.upperBoundaryDirection = upperBoundaryDirection;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParkingLot that = (ParkingLot) o;

        if (maxParkingLots != that.maxParkingLots) return false;
        if (Double.compare(that.distanceFromCurrentLocationInKilometres, distanceFromCurrentLocationInKilometres) != 0)
            return false;
        if (!name.equals(that.name)) return false;
        if (!parkingLotDimensions.equals(that.parkingLotDimensions)) return false;
        if (!devicesAtParkingArea.equals(that.devicesAtParkingArea)) return false;
        if (drivingDirection != that.drivingDirection) return false;
        return geofencePosition.equals(that.geofencePosition);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode();
        result = 31 * result + parkingLotDimensions.hashCode();
        result = 31 * result + devicesAtParkingArea.hashCode();
        result = 31 * result + maxParkingLots;
        result = 31 * result + drivingDirection.hashCode();
        result = 31 * result + geofencePosition.hashCode();
        temp = Double.doubleToLongBits(distanceFromCurrentLocationInKilometres);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
