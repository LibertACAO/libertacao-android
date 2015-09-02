package com.libertacao.libertacao.view.main;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.main.NavigationDrawerFragment;
import com.libertacao.libertacao.view.notificacoes.NotificacaoFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    /**
     * Interface elements
      */
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.setHomeAsUpEnabled(this);
        ButterKnife.inject(this);
        setupDrawer();
    }

    private void setupDrawer() {
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(toolbar, R.id.navigation_drawer, mDrawerLayout);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // TODO: instantiate fragments accrodingly to position
        fragmentManager.beginTransaction().replace(R.id.container, NotificacaoFragment.newInstance()).commit();
    }
}
