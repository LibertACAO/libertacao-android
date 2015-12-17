package com.libertacao.libertacao.view.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.persistence.DatabaseHelper;

/**
 * This class is responsible to control a fragment that only contains a map. It can work in normal mode or in edit mode.
 * Based on: http://code.tutsplus.com/tutorials/getting-started-with-google-maps-for-android-basics--cms-24635
 *
 * This is leaking because there is a known bug in Google Play Services: https://github.com/googlesamples/android-play-location/issues/26
 */
public class MapFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener {

    private static final String EVENT_ID = "EVENT_ID";
    private GoogleApiClient mGoogleApiClient;
    @Nullable private Event event;

    public static MapFragment newInstance(Event event) {
        MapFragment mapFragment = new MapFragment();
        Bundle bundle = new Bundle();
        if(event != null) {
            bundle.putInt(EVENT_ID, event.getId());
        }
        mapFragment.setArguments(bundle);
        return mapFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int eventId = getArguments().getInt(EVENT_ID, -1);
        if(eventId != -1) {
            event = DatabaseHelper.getHelper(getContext()).getEventIntegerRuntimeExceptionDao().queryForId(eventId);
        }

        if(event == null) {
            Toast.makeText(getContext(), getString(R.string.eventNotFound), Toast.LENGTH_LONG).show();
        } else {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            initListeners();
        }
    }

    private void initListeners() {
        if(getMap() != null) {
            getMap().setOnMarkerClickListener(this);
            getMap().setOnInfoWindowClickListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(getMap() != null) {
            initCamera();
        }
    }

    private void initCamera() {
        getMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getMap().setTrafficEnabled(true);
        getMap().setMyLocationEnabled(true);
        getMap().getUiSettings().setZoomControlsEnabled(true);

        if(event != null && event.hasLocation()) {
            LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());
            CameraPosition position = CameraPosition.builder()
                    .target(eventLocation)
                    .zoom(16f)
                    .bearing(0.0f)
                    .tilt(0.0f)
                    .build();

            getMap().animateCamera(CameraUpdateFactory.newCameraPosition(position), null);

            // Create marker at event location
            MarkerOptions options = new MarkerOptions().position(eventLocation);
            options.title(event.getTitle());
            options.icon(BitmapDescriptorFactory.defaultMarker());
            getMap().addMarker(options);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }
}
