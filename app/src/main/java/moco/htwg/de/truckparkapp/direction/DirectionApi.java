package moco.htwg.de.truckparkapp.direction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.model.DirectionsResult;

/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public interface DirectionApi {

    DirectionsResult sendDirectionRequest(LatLng origin, String destination, GoogleMap map);
    //DirectionsResult sendDirectionRequest(LatLng origin, LatLng destination, GoogleMap map);

}
