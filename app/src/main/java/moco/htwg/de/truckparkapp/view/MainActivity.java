package moco.htwg.de.truckparkapp.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import moco.htwg.de.truckparkapp.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MapsFragment.OnFragmentInteractionListener, FirestoreFragment.OnFragmentInteractionListener {

    private final String TAG = this.getClass().getSimpleName();

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

        Fragment fragment = MapsFragment.newInstance(); //Default Fragment (app start)
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        // Handle Action
        if (id == R.id.nav_map) {
            fragment = MapsFragment.newInstance();
        } else if (id == R.id.nav_settings) {
            // todo
        } else if (id == R.id.nav_firestore) {
            fragment = FirestoreFragment.newInstance();
        } else if (id == R.id.nav_test) {
            // todo
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content, fragment)
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
        Log.d("onFragmentInteraction", "onFragmentInteraction: " + uri);
        //todo
    }

    @Override
    public void onMapsFragmentInteraction(Object o) {
        Log.d(TAG, "onMapsFragmentInteraction: " + o);
    }
}
