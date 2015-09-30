package com.libertacao.libertacao.view.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.DrawerLayout;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.manager.ConnectionManager;
import com.libertacao.libertacao.manager.LoginManager;
import com.libertacao.libertacao.manager.SyncManager;
import com.libertacao.libertacao.util.SnackbarUtils;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.login.ParseLoginActivity;
import com.libertacao.libertacao.view.notificacoes.NotificacaoFragment;
import com.libertacao.libertacao.view.perfil.PerfilFragment;
import com.parse.ParseUser;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static final int LOGIN_RESULT_CODE = 1;
    private BroadcastReceiver networkBroadcastReceiver;
    private Subscription syncSubscribe;

    /**
     * Interface elements
      */
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.setHomeAsUpEnabled(this);
        ButterKnife.inject(this);
        setupDrawer();
        registerNetworkReceiver();
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

    @Override
    public void onNavigationDrawerItemSelected(@NavigationDrawerFragment.DrawerPosition int position) {
        if(position == NavigationDrawerFragment.NOTIFICACAO) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, NotificacaoFragment.newInstance()).commit();
        } else if(position == NavigationDrawerFragment.CADASTRO_PERFIL){
            if(LoginManager.getInstance().isLoggedIn()){
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, PerfilFragment.newInstance()).commit();
            } else {
                Intent intent = new Intent(this, ParseLoginActivity.class);
                startActivityForResult(intent, LOGIN_RESULT_CODE); // TODO: get result
            }
        } else if(position == NavigationDrawerFragment.CONTATO){
            // TODO: create contato screen
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
                SnackbarUtils.showDefaultSnackbar(mDrawerLayout, getResources().getString(R.string.welcomeUser, ParseUser.getCurrentUser().getUsername()));
            }
        }
    }

    /**
     * Utility methods
     */

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
}
