package moco.htwg.de.truckparkapp.view;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import moco.htwg.de.truckparkapp.R;
import moco.htwg.de.truckparkapp.app.AppController;
import moco.htwg.de.truckparkapp.direction.DirectionApi;
import moco.htwg.de.truckparkapp.direction.DirectionApiFactory;
import moco.htwg.de.truckparkapp.model.ParkingLot;
import moco.htwg.de.truckparkapp.parking.TruckParkLot;
import moco.htwg.de.truckparkapp.service.GeofenceTransitionsIntentService;
import moco.htwg.de.truckparkapp.view.adapter.ParkingLotsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, OnCompleteListener<Void> {

    public static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static float DEFAULT_ZOOM = 17.0f;
    private final String TAG = this.getClass().getSimpleName();
    RecyclerView recyclerView;
    private GoogleMap map;
    private MapView mapView;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownPosition;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LocationSettingsRequest locationSettingsRequest;
    private SettingsClient settingsClient;
    private GeofencingClient geofencingClient;

    private PendingIntent geofencePendingIntent;
    private PendingGeofenceTask pendingGeofenceTask = PendingGeofenceTask.NONE;
    private Polygon parkingLotPolygon;
    private Context context;
    private MapsFragment.OnFragmentInteractionListener mListener;
    private TextView enteredTruckParkSlotIndicator;

    private String destinationStreet;
    private String destinationPlace;

    private DirectionApi directionApi;
    private TruckParkLot truckParkLot;
    private ParkingLot parkingLot;

    private RecyclerView.LayoutManager layoutManager;
    private ParkingLotsAdapter parkingLotsAdapter;

    private FragmentActivity activity;

    private View view;

    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     *
     * @return A new instance of fragment MapsFragment.
     */
    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    public static MapsFragment newInstance(String destinationStreet, String destinationPostal, String destinationPlace) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString("destinationStreet", destinationStreet);
        args.putString("destinationPostal", destinationPostal);
        args.putString("destinationPlace", destinationPlace);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);
        context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        settingsClient = LocationServices.getSettingsClient(context);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //enteredTruckParkSlotIndicator = view.findViewById(R.id.entered_truck_park_slot_indicator);
        parkingLotsAdapter = new ParkingLotsAdapter(TruckParkLot.getInstance().getParkingLotsOnRoute());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(parkingLotsAdapter);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingRequest();


        if (getArguments() != null) {
            Bundle bundle = getArguments();
            destinationStreet = bundle.getString("destinationStreet");
            //String destinationPostal = bundle.getString("destinationPostal");
            destinationPlace = bundle.getString("destinationPlace");
        }


        directionApi = DirectionApiFactory.getDirectionApi(DirectionApiFactory.DirectionApiType.GOOGLE_MAPS_DIRECTION_API, getString(R.string.google_api_key));


        geofencingClient = LocationServices.getGeofencingClient(context);
        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("ADDITIONAL_INFO").startsWith("Start Parking")) {
                    parkingLot = truckParkLot.getParkingLots().get(intent.getStringExtra("PARKING_LOT_ID"));
                    if (parkingLot != null) {
                        PolygonOptions polygonOptions = new PolygonOptions();
                        polygonOptions.addAll(parkingLot.getLatLngForPolygonOptions());
                        parkingLotPolygon = map.addPolygon(polygonOptions);
                    }
                } else if (intent.getStringExtra("ADDITIONAL_INFO").startsWith("Stop Parking")) {
                    if (parkingLot != null) {
                        parkingLot.removeDeviceFromParkingLot(Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
                        truckParkLot.removePassedParkingLotFromParkingLotsOnRouteList(parkingLot);
                    }
                    parkingLotPolygon = null;

                }

            }
        }, new IntentFilter(GeofenceTransitionsIntentService.PARKING_BROADCAST));


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapsFragment.OnFragmentInteractionListener) {
            mListener = (MapsFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        saveLastKnowPosToSharedPref();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        checkLocationPermission();
        performPendingGeofenceTask();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveLastKnowPosToSharedPref();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveLastKnowPosToSharedPref();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        saveLastKnowPosToSharedPref();

        mapView.onLowMemory();
    }

    private void saveLastKnowPosToSharedPref() {
        if(lastKnownPosition != null){
            editor = sharedPref.edit();
            editor.putString("latitude", String.valueOf(lastKnownPosition.getLatitude()));
            editor.putString("longitude", String.valueOf(lastKnownPosition.getLongitude()));
            editor.commit();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        updateLocationUI();
        getDeviceLocation();
        startLocationUpdates();
        truckParkLot = TruckParkLot.getInstance();
        //addParkingLotIntoDatabase();

        navigateToDestination();

    }

    private void navigateToDestination() {
        final ObjectMapper mapper = new ObjectMapper();
        if (destinationPlace != null && destinationStreet != null) {
            DirectionsResult directionsResult;
            if (lastKnownPosition == null) {
                directionsResult = directionApi.sendDirectionRequest
                        (new LatLng(Double.parseDouble(sharedPref.getString("latitude","47.6681")),
                                Double.parseDouble(sharedPref.getString("longitude","9.1687"))),
                                destinationStreet + "," + destinationPlace, map);
            } else {
                directionsResult = directionApi.sendDirectionRequest(new LatLng(lastKnownPosition.getLatitude(), lastKnownPosition.getLongitude()), destinationStreet + "," + destinationPlace, map);
            }
            if (directionsResult == null) {
                Log.e(TAG, "could not read from directions api");
                Toast toast = Toast.makeText(context, R.string.could_not_read_directions_api_toast, Toast.LENGTH_LONG);
                toast.show();
            } else if (directionsResult.routes.length < 1){
                Log.e(TAG, "could not find route");
                Toast toast = Toast.makeText(context, "Route konnte nicht berechnet werden", Toast.LENGTH_LONG);
                toast.show();
            }

            else {
                final List<LatLng> path = PolyUtil.decode(directionsResult.routes[0].overviewPolyline.getEncodedPath());
                Map<String, String> params = new HashMap<>();
                try {
                    int i = 0;
                    for (LatLng latLng : path) {
                        //TODO Build latlng-latlong-converter
                        com.google.maps.model.LatLng anotherLatLong = new com.google.maps.model.LatLng(latLng.latitude, latLng.longitude);
                        params.put(String.valueOf(i++), mapper.writeValueAsString(anotherLatLong));
                    }

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        getString(R.string.rest_server_url) + "/parkinglots",
                        new JSONObject(params),
                        response -> {
                            Iterator<String> keys = response.keys();
                            TruckParkLot.getInstance().clearParkingLotsOnRouteList();
                            if(keys.hasNext()){
                                pendingGeofenceTask = PendingGeofenceTask.ADD;
                                boolean addedToParkingListOnRouteList = TruckParkLot.getInstance().getParkingLotsOnRouteAndAddToParkingListOnRoute(keys, parkingLotsAdapter);
                                if(addedToParkingListOnRouteList){
                                    parkingLotsAdapter.notifyDataSetChanged();
                                    performPendingGeofenceTask();
                                }
                            }

                        }, error -> Log.e(TAG, error.getMessage()));
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(jsonObjectRequest);
            }
        }
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        pendingGeofenceTask = PendingGeofenceTask.NONE;
        Log.d(TAG, "on Complete");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
    }

    private void performPendingGeofenceTask() {
        if (pendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        }
    }

    private void addGeofences() {
        checkLocationPermission();
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencesPendingIntent()).addOnCompleteListener(this);
        pendingGeofenceTask = PendingGeofenceTask.NONE;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        for(Geofence geofence : truckParkLot.getGeofenceList()){
            System.out.println("set: "+geofence.getRequestId());
        }
        builder.addGeofences(truckParkLot.getGeofenceList());
        return builder.build();

    }

    private PendingIntent getGeofencesPendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        checkLocationPermission();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                checkLocationPermission();
            }
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        lastKnownPosition = task.getResult();
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownPosition.getLatitude(), lastKnownPosition.getLongitude()), DEFAULT_ZOOM));
                    }
                }
            });

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation", e);
        }
    }

    private void buildLocationSettingRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        /*
          desired request interval. may be faster or lower, depends on other applications (faster) and location sources (lower)
         */
        locationRequest.setInterval(1000);
        /*
         * update wont be faster than entered interval
         */
        locationRequest.setFastestInterval(1000);
        /*
         * self explaining
         */
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                lastKnownPosition = location;


                TruckParkLot.getInstance().calculateDistanceToParkingLot(new com.google.maps.model.LatLng(location.getLatitude(), location.getLongitude()));
                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                if (parkingLotPolygon != null) {
                    boolean containsLocation = PolyUtil.containsLocation(new LatLng(location.getLatitude(), location.getLongitude()), parkingLotPolygon.getPoints(), true);
                    if (containsLocation) {
                        if (parkingLot != null) {
                            //TODO think about a more anonymous way to identify a device (e.g. uuid)
                            /*
                             * check if device is already located at parking lot.
                             * If no, device will be added to parkinglot object and database entry will be updated. otherwise nothing happens
                             */
                            if (activity != null && parkingLot.addDeviceToParkingLot(Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID))) {
                                Log.d(TAG, "onLocationResult: " + parkingLot.getName());
                                truckParkLot.updateParkingLot(parkingLot);

                                // ask for precise parking area usage
                                Intent intent = new Intent("FRAGMENT_INTENT");
                                intent.putExtra("FragmentAction", "START_INPUT_FREE_SLOTS");
                                intent.putExtra("PARKING_LOT_ID", parkingLot.getName());

                                Log.d(TAG, "sending " + intent.getAction() + " broadcast");
                                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                            }

                        }
                    }
                }
            }
        };
    }

    private void startLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "location settings successful checked");
                        checkLocationPermission();
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(getActivity(), 0x1);
                            Log.i(TAG, "resolved resolution required error");
                        } catch (IntentSender.SendIntentException e1) {
                            Log.i(TAG, "unable to resolve error");
                        }
                        break;
                }
                Log.e(TAG, "location settings error", e);
            }
        });
    }

    private void checkLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
