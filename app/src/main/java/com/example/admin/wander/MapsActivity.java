package com.example.admin.wander;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {
    // start getting KEY run map activity and then Change extends MapActivity to AppCompatActivity, then it will shop activity name
    // otherwise whole screen will fill with map
    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;
    private static final LatLng CSBWEST = new LatLng(40.912682, -90.640544);
    private static final LatLng CSBEAST = new LatLng(40.912658, -90.639101);
    private static final LatLng HUFF = new LatLng(40.913602, -90.638929);

    private Marker mCSBWEST;
    private Marker mCSBEAST;
    private Marker mHUFF;

    private static MediaPlayer mMediaPlayer;
    /**
     * This listener gets triggered when the Media Player has completed playing audio file
     */
    MediaPlayer.OnCompletionListener mCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    releaseMediaPlayer();
                }
            };

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

        // Add some markers to the map, and add a data object to each marker. this could be done via array list
        mCSBEAST = mMap.addMarker(new MarkerOptions()
        .position(CSBEAST)
        .title("CSB East"));
        mCSBEAST.setTag(0);

        mCSBWEST = mMap.addMarker(new MarkerOptions()
                .position(CSBWEST)
                .title("CSB West")
        .snippet("Center for Science and Business was built on .. "));
        mCSBWEST.setTag(0);

        mHUFF = mMap.addMarker(new MarkerOptions()
                .position(HUFF)
                .title("Huff Center"));
        mHUFF.setTag(0);


        // set the listener for the marker click;
        mMap.setOnMarkerClickListener(this);

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

    /**
     * Called when the user click a marker
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // retrieve the data from the marker
        Integer clickCount;
        clickCount = (Integer) marker.getTag();
        String markerId = (String) marker.getId();
        // check if a click count was set, then display the click count.
        if (clickCount != null){
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this, marker.getTitle() + "has been clicked "
            + clickCount + " times. It's id is " + markerId, Toast.LENGTH_SHORT).show();

            // Release the media player just in case it was currently playing different audio
            releaseMediaPlayer();
            // Create and setup the MediaPlayer for the audio associated with the current place
            mMediaPlayer = MediaPlayer.create(this, R.raw.om_jai);
            // starts the audio file
            mMediaPlayer.start(); // no need to call prepare

            // Setup a listener on the media player, so that we can stop and release the
            // media player once the sounds has finised playing
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
        }
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;
        }
    }

}
