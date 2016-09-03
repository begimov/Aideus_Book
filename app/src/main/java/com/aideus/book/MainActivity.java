package com.aideus.book;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aideus.book.data.local.ContentsAdapter;
import com.aideus.book.data.ModelFragment;
import com.aideus.book.data.local.model.BookContents;
import com.aideus.book.events.BookLoadedEvent;
import com.aideus.book.ui.NoteActivity;
import com.aideus.book.ui.PreferencesActivity;
import com.aideus.book.ui.SimpleContentActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_ASSET_MISC_ABOUT_URI = "file:///android_asset/misc/about.html";

    private static final String FILE_ASSET_MISC_CONTACTS_URI = "file:///android_asset/misc/contacts.html";

    private static final String MODEL = "model";

    private ViewPager mPager = null;

    private ContentsAdapter mAdapter = null;

    private ModelFragment mFrag = null;

    private Toolbar mToolbar = null;

    private Drawer mDrawer = null;

    private Drawer mDrawerContents = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolbar);

        mPager = (ViewPager) findViewById(R.id.pager);

    }

    @Override
    protected void onResume() {

        super.onResume();

        EventBus.getDefault().register(this);

        if (mAdapter == null) {

            mFrag = (ModelFragment) getFragmentManager()
                    .findFragmentByTag(MODEL);

            if (mFrag == null) {

                mFrag = new ModelFragment();

                getFragmentManager()
                        .beginTransaction()
                        .add(mFrag, MODEL)
                        .commit();

            } else if (mFrag.getBook() != null) {

                setupPager();

                setupDrawer();

            }


        } else {

            //TODO Refresh pager only if font size changed
            //TODO Find other way to refresh pager content
            mPager.setAdapter(mAdapter);

            setPagerCurrentItem();

        }

    }

    @Override
    protected void onPause() {

        EventBus.getDefault().unregister(this);

        setPrefLastPosition();

        super.onPause();

    }

    @Override
    public void onBackPressed() {

        if (mDrawer != null && mDrawer.isDrawerOpen()) {

            mDrawer.closeDrawer();

        } else if (mDrawerContents != null && mDrawerContents.isDrawerOpen()) {

            mDrawerContents.closeDrawer();

        } else {

            super.onBackPressed();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.notes:

                Intent i = new Intent(this, NoteActivity.class);

                i.putExtra(NoteActivity.EXTRA_POSITION, mPager.getCurrentItem());

                startActivity(i);

                return true;

            case R.id.contents:

                mDrawerContents.openDrawer();

                return true;

            case R.id.update:

                setPrefLastPosition();

                mFrag.startDownloadCheckService(this);

                return true;

        }

        return super.onOptionsItemSelected(item);

    }

        private void setupPager() {

        //TODO Data Adapter in Activity!
        mAdapter = new ContentsAdapter(mFrag, this);

        mPager.setAdapter(mAdapter);

        findViewById(R.id.progressBar1).setVisibility(View.GONE);

        mPager.setVisibility(View.VISIBLE);

        mToolbar.setVisibility(View.VISIBLE);

        mPager.setKeepScreenOn(true);

        setPagerCurrentItem();

    }

    private void setPrefLastPosition() {

        mFrag.setPrefLastPosition(mPager.getCurrentItem());

    }

    private void setPagerCurrentItem() {

        mPager.setCurrentItem(mFrag.getPrefLastPosition());

    }

    private void setupDrawer() {

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.material_drawer_badge)
                .build();

        //TODO auto drawer items creation
        PrimaryDrawerItem item1 = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withName(R.string.book)
                .withIcon(GoogleMaterial.Icon.gmd_book);

        PrimaryDrawerItem item2 = new PrimaryDrawerItem()
                .withIdentifier(2)
                .withName(R.string.about)
                .withSelectable(false)
                .withIcon(GoogleMaterial.Icon.gmd_info);

        PrimaryDrawerItem item3 = new PrimaryDrawerItem()
                .withIdentifier(3)
                .withName(R.string.contacts)
                .withSelectable(false)
                .withIcon(GoogleMaterial.Icon.gmd_contacts);

        PrimaryDrawerItem item4 = new PrimaryDrawerItem()
                .withIdentifier(4)
                .withName(R.string.settings)
                .withSelectable(false)
                .withIcon(GoogleMaterial.Icon.gmd_settings);

        buildPrimaryNavigationDrawer(headerResult, item1, item2, item3, item4);

        buildSecondaryNavigationDrawer();

        lockNavigationDrawerClosed();

    }

    private void buildPrimaryNavigationDrawer(final AccountHeader headerResult,
                                              final PrimaryDrawerItem item1,
                                              final PrimaryDrawerItem item2,
                                              final PrimaryDrawerItem item3,
                                              final PrimaryDrawerItem item4) {

        mDrawer = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(mToolbar)
                //TODO auto drawer items adding
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        item4,
                        new DividerDrawerItem())
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            //TODO auto drawer items position ids parsing
                            case 2:
                                Intent i = new Intent(getApplicationContext(), SimpleContentActivity.class)
                                        .putExtra(SimpleContentActivity.EXTRA_FILE,
                                                FILE_ASSET_MISC_ABOUT_URI);
                                startActivity(i);
                                mDrawer.closeDrawer();
                                return true;
                            case 3:
                                i = new Intent(getApplicationContext(), SimpleContentActivity.class)
                                        .putExtra(SimpleContentActivity.EXTRA_FILE,
                                                FILE_ASSET_MISC_CONTACTS_URI);
                                startActivity(i);
                                mDrawer.closeDrawer();
                                return true;
                            case 4:
                                i = new Intent(getApplicationContext(), PreferencesActivity.class);
                                startActivity(i);
                                mDrawer.closeDrawer();
                                return true;
                        }
                        return true;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        unlockNavigationDrawer();
                    }
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        lockNavigationDrawerClosed();
                    }
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        mDrawerContents.setSelection(mPager.getCurrentItem(), false);
                    }
                })
                .build();

    }

    private void buildSecondaryNavigationDrawer() {

        mDrawerContents = new DrawerBuilder()
                .withActivity(this)
                .withDrawerGravity(Gravity.END)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        mPager.setCurrentItem(position);
                        mDrawerContents.closeDrawer();
                        return true;
                    }
                })
                .append(mDrawer);

        for (int i = 0; i < mFrag.getChaptersCount(); i++) {

            PrimaryDrawerItem primaryDrawerItem = new PrimaryDrawerItem()
                    .withIdentifier(i)
                    .withName(mFrag.getChapterTitle(i))
                    .withIcon(GoogleMaterial.Icon.gmd_book);

            mDrawerContents.addItem(primaryDrawerItem);

        }

    }

    @SuppressWarnings("unused")

    @Subscribe(threadMode = ThreadMode.MAIN)

    public void onEventMainThread(BookLoadedEvent event) {

        setupPager();

        setupDrawer();

    }

    private void lockNavigationDrawerClosed() {

        mDrawer.getDrawerLayout()
                .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    private void unlockNavigationDrawer() {

        mDrawer.getDrawerLayout()
                .setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

    }
}
