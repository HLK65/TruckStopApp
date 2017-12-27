package moco.htwg.de.truckparkapp.view;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moco.htwg.de.truckparkapp.R;
import moco.htwg.de.truckparkapp.model.ParkingLot;
import moco.htwg.de.truckparkapp.service.GeofenceTransitionsIntentService;

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
    private List<Geofence> geofences;
    private PendingIntent geofencePendingIntent;
    private PendingGeofenceTask pendingGeofenceTask = PendingGeofenceTask.NONE;
    private Polygon parkingLot;
    private Map<String, ParkingLot> mockParkingLotsDatabase;
    private Context context;
    private MapsFragment.OnFragmentInteractionListener mListener;
    private TextView enteredTruckParkSlotIndicator;

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
    public static MapsFragment newInstance(/*String param1, String param2*/) {
        MapsFragment fragment = new MapsFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        context = view.getContext();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        settingsClient = LocationServices.getSettingsClient(context);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        enteredTruckParkSlotIndicator = view.findViewById(R.id.entered_truck_park_slot_indicator);

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingRequest();
        geofences = new ArrayList<>();
        mockParkingLotsDatabase = new HashMap<>();

        geofencingClient = LocationServices.getGeofencingClient(context);
        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("ADDITIONAL_INFO").startsWith("Start Parking")) {

                    PolygonOptions polygonOptions = mockParkingLotsDatabase.get(intent.getStringExtra("PARKING_LOT_ID")).getPolygonOptions();
                    if (polygonOptions != null) {
                        parkingLot = map.addPolygon(polygonOptions);
                    }
                } else if (intent.getStringExtra("ADDITIONAL_INFO").startsWith("Stop Parking")) {

                    parkingLot = null;

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
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        checkLocationPermission();
        performPendingGeofenceTask();

    }

    private void performPendingGeofenceTask() {
        if (pendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        }
    }

    private void addGeofences() {
        checkLocationPermission();
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencesPendingIntent()).addOnCompleteListener(this);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.addGeofences(geofences);
        return builder.build();
    }

    private PendingIntent getGeofencesPendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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
        getGeofencesFromDatabase();
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        pendingGeofenceTask = PendingGeofenceTask.NONE;
        Log.d(TAG, "on Complete");
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
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
                if (parkingLot != null) {
                    boolean containsLocation = PolyUtil.containsLocation(new LatLng(location.getLatitude(), location.getLongitude()), parkingLot.getPoints(), true);
                    if (containsLocation) {
                        //TODO inkrement value in database
                        enteredTruckParkSlotIndicator.setVisibility(View.VISIBLE);
                    } else if (!containsLocation) {
                        //TODO dekrement value in database
                        enteredTruckParkSlotIndicator.setVisibility(View.INVISIBLE);
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

    private void getGeofencesFromDatabase() {
        Map<String, LatLng> truckParkingSpaces = new HashMap<>();
        truckParkingSpaces.put("HTWG", new LatLng(47.668110, 9.169001));


        ParkingLot parkingLotHtwgKonstanz = new ParkingLot(
                new LatLng(47.668340, 9.169379),
                new LatLng(47.667807, 9.169234),
                new LatLng(47.667902, 9.168608),
                new LatLng(47.668447, 9.168759));
        parkingLotHtwgKonstanz.showParkingLotOnMap(Color.RED);
        parkingLotHtwgKonstanz.setName("HTWG");
        mockParkingLotsDatabase.put(parkingLotHtwgKonstanz.getName(), parkingLotHtwgKonstanz);

        for (Map.Entry<String, LatLng> truckParkingSpace : truckParkingSpaces.entrySet()) {
            geofences.add(new Geofence.Builder()
                    .setRequestId(truckParkingSpace.getKey())
                    .setCircularRegion(
                            truckParkingSpace.getValue().latitude,
                            truckParkingSpace.getValue().longitude, 200)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                    .setLoiteringDelay(10000)
                    .build());
        }
        pendingGeofenceTask = PendingGeofenceTask.ADD;
        performPendingGeofenceTask();
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
        void onMapsFragmentInteraction(Object o);
    }

}
