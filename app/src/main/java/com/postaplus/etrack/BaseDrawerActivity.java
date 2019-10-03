package com.postaplus.etrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;



/**
 * Created by ahlaam.kazi on 12/21/2017.
 */

public class BaseDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    FrameLayout frameLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_drawer);
        ;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        //to prevent current item select over and over


        if (id == R.id.nav_events) {
            Intent int1 = new Intent(getBaseContext(),EventActivity.class);
           /* int1.putExtra("username","TEST99");
            int1.putExtra("password","TEST99");*/
           // getevent();
            startActivity(new Intent(int1));


            // Handle the event action
        } else if (id == R.id.nav_rstverfctn) {

            Intent int1 = new Intent(getBaseContext(),RstVerificationActivity.class);
            int1.putExtra("username","TEST99");
            int1.putExtra("password","TEST99");
            startActivity(new Intent(int1));
            //  startActivity(new Intent(MainActivity.this, RstVerificationActivity.class));




        } else if (id == R.id.nav_pckpverfctn) {

            Intent int1 = new Intent(getBaseContext(),PickupVerificationActivity.class);

            startActivity(new Intent(int1));


        } else if (id == R.id.nav_wcmax) {

            Intent int1 = new Intent(getBaseContext(),WC_MaxActivity.class);

            startActivity(new Intent(int1));


        } else if (id == R.id.nav_bagconso) {

            Intent int1 = new Intent(getBaseContext(),BagConsolidateActivity.class);

            startActivity(new Intent(int1));


        }else if (id == R.id.nav_manifest) {

            Intent int1 = new Intent(getBaseContext(),ManifestActivity.class);

            startActivity(new Intent(int1));


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            Intent int1= new Intent(getBaseContext(),LoginActivity.class);
            startActivity(new Intent(int1));

        }
        drawerLayout.openDrawer(GravityCompat.START);
        return true;
    }
}
