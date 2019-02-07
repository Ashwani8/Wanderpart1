package com.example.admin.wander;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    // Aster getting KEY run map activity and then Change extends MapActivity to AppCompatActivity, then it will shop activity name
    // otherwise whole screen will fill with map
    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // step 2 create a new menu XML file and override to fill menu with options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        //  step 3 inflate with menu item
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    // step 4 select item from the menu and set the map accordingly
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the my type on the User's selection
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;

            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;

            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;

            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // step 5 remove the code for Sydney and replace it with
        // location of your home without marker Add a marker in Monmouth and move the camera
        LatLng home = new LatLng(40.914355, -90.638366);
        //mMap.addMarker(new MarkerOptions().position(home).title("Monmouth College campus"));
        // Create a camera update with appropriate zoom level ( 15 for street level
        // and 20 for building level)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 16));

        // step 15 Add an Overlay [TileOverlay vs GroundOverlay] drawings on top of maps

//        GroundOverlayOptions homeOverlay = new GroundOverlayOptions()
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.android))
//                .position(home,100);

//        mMap.addGroundOverlay(homeOverlay); // having trouble with this code of line app crashes

        // step 8
        setMapLongClick(mMap);

        // step 13 call this method to show poi
        setPoiClick(mMap);

        // step 14 Style your map we have created a map style online and copy the jason code
        // and saved it in a new folder raw with name map_style.jason , now we are importing it
        // into our map via onMapReady()
        try{
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if(!success){
                Log.e(TAG, "Style parsing failed.");
            }
        }catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find styles. Error: ", e);
        }
    }

    // step 6 add a marker (tap at a location for long and marker will appear there) and
    // extend markers to show contextual information in info window

    private void setMapLongClick(final GoogleMap map) {
        // set google map on click listener
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // step 7 call addMarker() and call the method at the end of the onMapReady()
                // map.addMarker(new MarkerOptions().position(latLng));
                // with step 7 code Navigation buttons appear at the bottom-left side of the screen,
                // allowing the user to use the Google Maps app to navigate to the marked position.

                // step 9 to add info window for the marker (compare with step 7)
                String snippet = String.format(Locale.getDefault(),
                        "Lat: %1$.5f, Long: %2$.5f",
                        latLng.latitude,
                        latLng.longitude);

                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.dropped_pin))
                        .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                // step 15 above line change the color of the default google balloon from red
                // to HUE_BLUE.
            }
        });
    }

    // step 10 Add POI point of interest, we will use GoogleMap.OnPoiClickListener that places a marker
    // on the map immediately, instead for waiting for a touch and hold.
    private void setPoiClick(final GoogleMap map) {
        //  step 11 set google map on click listener
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                // step 12 place a marker at the Poi
                Marker poiMarker = mMap.addMarker(new MarkerOptions()
                        .position(poi.latLng)
                        .title(poi.name));
                poiMarker.showInfoWindow();
            }
        });
    }

}
