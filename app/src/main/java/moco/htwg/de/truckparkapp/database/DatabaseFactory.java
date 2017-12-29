package moco.htwg.de.truckparkapp.database;

/**
 * Created by Sebastian Thümmel on 29.12.2017.
 */

public class DatabaseFactory {

    public enum Type {
        FIRESTORE
    }

    public static Database getDatabase(Type type){
        if(type == Type.FIRESTORE){
            return new Firestore();
        }
        return null;
    }

}
