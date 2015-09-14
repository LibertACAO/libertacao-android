package com.libertacao.libertacao.view.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.DrawerLayout;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.notificacoes.NotificacaoFragment;
import com.libertacao.libertacao.view.perfil.PerfilActivity;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
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
    }

    private void setupDrawer() {
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(toolbar, R.id.navigation_drawer, mDrawerLayout);
    }

    @Override
    public void onNavigationDrawerItemSelected(@NavigationDrawerFragment.DrawerPosition int position) {
        if(position == NavigationDrawerFragment.NOTIFICACAO) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, NotificacaoFragment.newInstance()).commit();
        } else if(position == NavigationDrawerFragment.CADASTRO_PERFIL){
            if(ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())){
                Intent intent = new Intent(this, PerfilActivity.class);
                startActivity(intent);
            } else {
                ParseLoginBuilder builder = new ParseLoginBuilder(this);
                startActivityForResult(builder.build(), 0);
            }
        } else if(position == NavigationDrawerFragment.CONTATO){
            // TODO: create contato screen
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavigationDrawerFragment.updateDrawerAdapter();
    }
}
