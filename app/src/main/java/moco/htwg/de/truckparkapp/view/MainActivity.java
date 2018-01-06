package moco.htwg.de.truckparkapp.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import moco.htwg.de.truckparkapp.R;
import moco.htwg.de.truckparkapp.service.GeofenceTransitionsIntentService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MapsFragment.OnFragmentInteractionListener, FirestoreFragment.OnFragmentInteractionListener, InputFreeSlotsFragment.OnFragmentInteractionListener {

    private final String TAG = this.getClass().getSimpleName();
    FragmentManager fragmentManager;
    private String parkingLotId;
    private String destinationSteet;
    private String destinationPostal;
    private String destinationPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Default Fragment (app start)
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, DestinationFragment.newInstance(), DestinationFragment.class.getSimpleName())
                .commit();

        /**
         * Fragment intent listener used to request fragment change from within fragment
         */
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    switch (intent.getStringExtra("FragmentAction")) {
                        // ask for precise parking area usage on arrival
                        case "START_INPUT_FREE_SLOTS":
                            parkingLotId = intent.getStringExtra("PARKING_LOT_ID");
                            navigationView.getMenu().findItem(R.id.nav_inputSlots).setEnabled(true).setChecked(true);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content, InputFreeSlotsFragment.newInstance(parkingLotId), InputFreeSlotsFragment.class.getSimpleName())
                                    .commit();
                            break;

                        case "START_MAP":
                            if (intent.getStringExtra("DESTINATION_STREET") != null
                                    && intent.getStringExtra("DESTINATION_POSTAL") != null
                                    && intent.getStringExtra("DESTINATION_PLACE") != null) {
                                destinationSteet = intent.getStringExtra("DESTINATION_STREET");
                                destinationPostal = intent.getStringExtra("DESTINATION_POSTAL");
                                destinationPlace = intent.getStringExtra("DESTINATION_PLACE");
                            }

                            navigationView.getMenu().findItem(R.id.nav_map).setChecked(true);

                            Fragment fragment;
                            if (destinationSteet != null && !destinationSteet.isEmpty()
                                    && destinationPostal != null && !destinationPostal.isEmpty()
                                    && destinationPlace != null && !destinationPlace.isEmpty()) {
                                fragment = MapsFragment.newInstance(destinationSteet, destinationPostal, destinationPlace);
                            } else {
                                fragment = MapsFragment.newInstance();
                            }
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content, fragment, MapsFragment.class.getSimpleName())
                                    .commit();

                            break;
                    }
                }
            }
        }, new IntentFilter("FRAGMENT_INTENT"));

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // hide InputFreeSlotsFragment when leaving geofence of parking area
                if (intent.getStringExtra("ADDITIONAL_INFO").startsWith("Stop Parking")) {
                    Log.d(TAG, "onReceive: stop parking");
                    parkingLotId = "";

                    if (fragmentManager.findFragmentByTag(InputFreeSlotsFragment.class.getSimpleName()) != null &&
                            fragmentManager.findFragmentByTag(InputFreeSlotsFragment.class.getSimpleName()).isVisible()) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.content, MapsFragment.newInstance(), MapsFragment.class.getSimpleName())
                                .commit();
                        navigationView.getMenu().findItem(R.id.nav_map).setChecked(true);
                    }

                    navigationView.getMenu().findItem(R.id.nav_inputSlots).setChecked(false).setEnabled(false);
                }
            }
        }, new IntentFilter(GeofenceTransitionsIntentService.PARKING_BROADCAST));
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = null;

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if ((fragmentManager.findFragmentByTag(InputFreeSlotsFragment.class.getSimpleName()) != null
                && fragmentManager.findFragmentByTag(InputFreeSlotsFragment.class.getSimpleName()).isVisible())
                || (fragmentManager.findFragmentByTag(DestinationFragment.class.getSimpleName()) != null &&
                fragmentManager.findFragmentByTag(DestinationFragment.class.getSimpleName()).isVisible())){
            if (destinationSteet != null && !destinationSteet.isEmpty()
                    && destinationPostal != null && !destinationPostal.isEmpty()
                    && destinationPlace != null && !destinationPlace.isEmpty()) {
                fragment = MapsFragment.newInstance(destinationSteet, destinationPostal, destinationPlace);
            } else {
                fragment = MapsFragment.newInstance();
            }
        } else {
            super.onBackPressed();
        }
        if (fragment != null) {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        // Handle Action
        if (id == R.id.nav_map) {
            if (destinationSteet != null && !destinationSteet.isEmpty()
                    && destinationPostal != null && !destinationPostal.isEmpty()
                    && destinationPlace != null && !destinationPlace.isEmpty()) {
                fragment = MapsFragment.newInstance(destinationSteet, destinationPostal, destinationPlace);
            } else {
                fragment = MapsFragment.newInstance();
            }
        } else if (id == R.id.nav_destination) {
            fragment = DestinationFragment.newInstance();
        } else if (id == R.id.nav_inputSlots) {
            fragment = InputFreeSlotsFragment.newInstance(parkingLotId);
        } else if (id == R.id.nav_settings) {
            // todo
        } else if (id == R.id.nav_firestore) {
            fragment = FirestoreFragment.newInstance();
        } else if (id == R.id.nav_test) {
            // todo
        }

        if (fragment != null) {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content, fragment, fragment.getClass().getSimpleName())
                    .commit();
            item.setChecked(true);

        } else {
            Snackbar.make(findViewById(android.R.id.content), "Menu Item " + item.getTitle() + " pressed but not implemented", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(TAG, "onFragmentInteraction: " + uri);
        Log.w(TAG, "onFragmentInteraction: No Action Implemented!");
    }


}
