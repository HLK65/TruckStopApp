package moco.htwg.de.truckparkapp.direction;


/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public class DirectionApiFactory {

    public enum DirectionApiType {
        GOOGLE_MAPS_DIRECTION_API, OPEN_ROUTE_SERVICE, HERE
    }

    public static DirectionApi getDirectionApi(DirectionApiType type, String apiKey){
        if(type == DirectionApiType.GOOGLE_MAPS_DIRECTION_API){
            return new GoogleMapsDirectionApi(apiKey);
        } else if (type == DirectionApiType.OPEN_ROUTE_SERVICE){
            throw new UnsupportedOperationException("not yet implemented");
        } else if (type == DirectionApiType.HERE){
            throw new UnsupportedOperationException("not yet implemented");
        }

        return null;
    }


}
