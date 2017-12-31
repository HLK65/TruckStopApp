package moco.htwg.de.truckparkapp.direction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.List;


/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public class GoogleMapsDirectionApi implements DirectionApi {

    private String apiKey;

    GoogleMapsDirectionApi(){}

    GoogleMapsDirectionApi(String apiKey){
        this.apiKey = apiKey;
    }

    @Override
    public DirectionsResult sendDirectionRequest(LatLng origin, String destination, GoogleMap map) {
        DirectionsApiRequest directionsApiRequest = DirectionsApi.newRequest(getGeoApiContext());
        directionsApiRequest.mode(TravelMode.DRIVING);
        directionsApiRequest.origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude));
        directionsApiRequest.destination(destination);
        DirectionsResult result = null;
        try {
            result = directionsApiRequest.await();
            addRouteToMap(result,map);

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
    @Override
    public DirectionsResult sendDirectionRequest(LatLng origin, LatLng destination, GoogleMap map) {
        //TODO implement
        return null;
    }
*/
    private void addRouteToMap(DirectionsResult result, GoogleMap map){
        List<LatLng> path = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
        map.addPolyline(new PolylineOptions().addAll(path));
    }

    private GeoApiContext getGeoApiContext() {
        GeoApiContext.Builder geoApiContext = new GeoApiContext.Builder();
        geoApiContext.apiKey(apiKey);

        return geoApiContext.build();
    }
}
