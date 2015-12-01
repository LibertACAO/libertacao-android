package com.libertacao.libertacao.view.event;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.event.ChangedOrderByEvent;
import com.libertacao.libertacao.manager.LoginManager;
import com.libertacao.libertacao.persistence.UserPreferences;
import com.libertacao.libertacao.view.main.MainActivity;
import com.libertacao.libertacao.view.main.NavigationDrawerFragment;
import com.libertacao.libertacao.view.rssitem.ThirdPartyNewsFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class EventFragment extends Fragment {

    // Order by
    public static final int END_DATE = 0;
    public static final int LAST_UPDATED = 1;
    public static final int MOST_POPULAR = 2;
    @OrderBy private int selectedOrderBy = UserPreferences.getSelectedOrderBy();

    @IntDef({END_DATE, LAST_UPDATED, MOST_POPULAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrderBy {}

    /**
     * Interface elements
     */
    @InjectView(R.id.events_tab_layout) TabLayout tabLayout;
    @InjectView(R.id.events_view_pager) ViewPager viewPager;

    public static EventFragment newInstance() {
        return new EventFragment();
    }

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.inject(this, layout);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayoutListener();
        viewPager.setCurrentItem(UserPreferences.getSelectedTab());
        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_fragment_menu, menu);
        inflater.inflate(R.menu.add_event_menu, menu);
        MenuItem menuAddEvent = menu.findItem(R.id.menu_add_event);
        if(LoginManager.getInstance().isAdmin()) {
            menuAddEvent.setTitle(getString(R.string.addEvent));
        } else {
            menuAddEvent.setTitle(getString(R.string.suggestEvent));
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_event:
                if(LoginManager.getInstance().isLoggedIn()) {
                    startActivity(EditEventActivity.newIntent(getContext()));
                } else {
                    new android.app.AlertDialog.Builder(getContext())
                            .setMessage(getString(R.string.mustBeLoggedIn))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(getString(android.R.string.ok), null)
                            .setNegativeButton(getString(R.string.login), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((MainActivity) getActivity()).onNavigationDrawerItemSelected(NavigationDrawerFragment.CADASTRO_PERFIL);
                                }
                            })
                            .show();
                }
                return true;
            case R.id.menu_order_by_event:
                CharSequence[] orderByItems = new CharSequence[3];
                orderByItems[0] = getString(R.string.orderByDate);
                orderByItems[1] = getString(R.string.orderByLastUpdated);
                orderByItems[2] = getString(R.string.orderByMostPopular);
                AlertDialog.Builder orderByBuilder = new AlertDialog.Builder(getContext());
                orderByBuilder.setTitle(getString(R.string.orderBy));
                orderByBuilder.setSingleChoiceItems(orderByItems, selectedOrderBy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedOrderBy = which;
                        UserPreferences.setSelectedOrderBy(selectedOrderBy);
                        EventBus.getDefault().post(new ChangedOrderByEvent(selectedOrderBy));
                        dialog.dismiss();
                    }
                });
                orderByBuilder.setNegativeButton(getString(android.R.string.cancel), null);
                orderByBuilder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper UI methods
     */

    private void setupTabLayoutListener() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                UserPreferences.setSelectedTab(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        if(LoginManager.getInstance().isAdmin()) {
            adapter.addFrag(EventFragmentBase.newInstance(EventFragmentBase.ADMIN), getString(R.string.admin));
        }
        adapter.addFrag(EventFragmentBase.newInstance(EventFragmentBase.NEAR_ME), getString(R.string.nearMe));
        adapter.addFrag(ThirdPartyNewsFragment.newInstance(), getString(R.string.thirdPartyNew));
        adapter.addFrag(EventFragmentBase.newInstance(EventFragmentBase.EVENT), getResources().getQuantityString(R.plurals.event, 2));
        adapter.addFrag(EventFragmentBase.newInstance(EventFragmentBase.VAKINHAS), getResources().getQuantityString(R.plurals.vakinha, 2));
        adapter.addFrag(EventFragmentBase.newInstance(EventFragmentBase.PETITIONS), getResources().getQuantityString(R.plurals.petition, 2));
        adapter.addFrag(EventFragmentBase.newInstance(EventFragmentBase.PROTEST), getResources().getQuantityString(R.plurals.protest, 2));
        adapter.addFrag(EventFragmentBase.newInstance(EventFragmentBase.COMMUNITY_NEWS), getResources().getQuantityString(R.plurals.singleNew, 2));
        adapter.addFrag(EventFragmentBase.newInstance(EventFragmentBase.OTHERS), getResources().getQuantityString(R.plurals.variado, 2));
        adapter.addFrag(EventFragmentBase.newInstance(EventFragmentBase.ALL), getString(R.string.all));
        viewPager.setAdapter(adapter);
    }
}
