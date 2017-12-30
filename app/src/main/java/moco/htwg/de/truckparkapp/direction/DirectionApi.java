package moco.htwg.de.truckparkapp.direction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public interface DirectionApi {

    void sendDirectionRequest(LatLng origin, String destination, GoogleMap map);
    void sendDirectionRequest(LatLng origin, LatLng destination, GoogleMap map);

}
