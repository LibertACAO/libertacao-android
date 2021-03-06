package com.libertacao.libertacao.view.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.event.FirstLocationEncounteredEvent;
import com.libertacao.libertacao.manager.ConnectionManager;
import com.libertacao.libertacao.manager.LoginManager;
import com.libertacao.libertacao.manager.SyncManager;
import com.libertacao.libertacao.manager.UserManager;
import com.libertacao.libertacao.util.SnackbarUtils;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.admin.AdminFragment;
import com.libertacao.libertacao.view.contact.ContactFragment;
import com.libertacao.libertacao.view.event.EventFragment;
import com.libertacao.libertacao.view.info.AboutFragment;
import com.libertacao.libertacao.view.info.OtherAppsFragment;
import com.libertacao.libertacao.view.login.ParseLoginActivity;
import com.libertacao.libertacao.view.perfil.PerfilFragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int LOGIN_RESULT_CODE = 1;
    private BroadcastReceiver networkBroadcastReceiver;
    private Subscription syncSubscribe;

    /**
     * Interface elements
      */
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    // Used to get last known location
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.setHomeAsUpEnabled(this);
        ButterKnife.inject(this);
        setupDrawer();
        registerNetworkReceiver();
        buildGoogleApiClient();
    }

    private void setupDrawer() {
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(toolbar, R.id.navigation_drawer, mDrawerLayout);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void updateDrawer() {
        mNavigationDrawerFragment.updateDrawerAdapter();
    }

    public void setupEventFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, EventFragment.newInstance()).commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(@NavigationDrawerFragment.DrawerPosition int position) {
        if(position == NavigationDrawerFragment.NOTIFICATION) {
            openFragment(EventFragment.newInstance());
        } else if(position == NavigationDrawerFragment.PROFILE){
            if(LoginManager.getInstance().isLoggedIn()){
                openFragment(PerfilFragment.newInstance());
            } else {
                Intent intent = new Intent(this, ParseLoginActivity.class);
                startActivityForResult(intent, LOGIN_RESULT_CODE);
            }
        } else if(position == NavigationDrawerFragment.OTHER_APPS){
            openFragment(OtherAppsFragment.newInstance());
        } else if(position == NavigationDrawerFragment.CONTACT){
            openFragment(ContactFragment.newInstance());
        } else if(position == NavigationDrawerFragment.ABOUT){
            openFragment(AboutFragment.newInstance());
        } else if(position == NavigationDrawerFragment.ADMIN){
            openFragment(AdminFragment.newInstance());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavigationDrawerFragment.updateDrawerAdapter();
        registerSyncRepeat();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unsubscribeSyncSubscribe();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_RESULT_CODE) {
            if(resultCode == RESULT_OK && LoginManager.getInstance().isLoggedIn()) {
                SnackbarUtils.showDefaultSnackbar(mDrawerLayout, getResources().getString(R.string.welcomeUser, LoginManager.getInstance().getUsername()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(networkBroadcastReceiver != null) {
            this.unregisterReceiver(networkBroadcastReceiver);
        }
    }

    /**
     * Utility methods
     */

    /**
     * Open fragment if it is not already being displayed at the second level of fragments (fragment #1 will be drawer fragment)
     * @param fragment related fragment
     */
    private void openFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null && fragments.size() > 1){
            Fragment currentFragment = fragments.get(1);
            if(currentFragment.getClass().equals(fragment.getClass())){
                // We are already showing this fragment
                return;
            }
        }
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void registerNetworkReceiver() {
        if (networkBroadcastReceiver != null) {
            unregisterReceiver(networkBroadcastReceiver);
        }

        networkBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = conn.getActiveNetworkInfo();

                if (networkInfo != null) { // Connected
                    SyncManager.getInstance().sync(getBaseContext());
                } else { // Disconnected
                    SnackbarUtils.showNoInternetConnectionSnackbar(getBaseContext(), mDrawerLayout);
                }
            }
        };
        this.registerReceiver(networkBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void registerSyncRepeat() {
        unsubscribeSyncSubscribe();
        syncSubscribe = Observable.interval(1, 5, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object aLong) {
                        if (ConnectionManager.getInstance().isOnline(getBaseContext())) {
                            SyncManager.getInstance().sync(getBaseContext());
                        }
                    }
                });
    }

    private void unsubscribeSyncSubscribe(){
        if(syncSubscribe != null && !syncSubscribe.isUnsubscribed()){
            syncSubscribe.unsubscribe();
            syncSubscribe = null;
        }

    }

    /**
     * Code to get last known location
     */

    private synchronized void buildGoogleApiClient() {
        Timber.d("Initialized Google Api Client");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Timber.d("Google API client connection connected");
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            Timber.d("Got user location (" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + ")");
            UserManager.getInstance().setCurrentLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
        } else {
            Timber.d("Received empty location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.d("Google API client connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Timber.d("Google API client connection failed");
    }

    /**
     * Events
     */

    @SuppressWarnings("unused")
    public void onEventMainThread(FirstLocationEncounteredEvent event) {
        setupEventFragment();
    }
}
