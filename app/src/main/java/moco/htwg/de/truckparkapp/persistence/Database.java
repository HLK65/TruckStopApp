package moco.htwg.de.truckparkapp.persistence;

import java.util.Map;

import moco.htwg.de.truckparkapp.model.ParkingLot;

/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public interface Database {

    void addParkingLot(ParkingLot parkingLot);
    Map<String, ParkingLot> getParkingLots();

}
