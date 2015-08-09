package com.clabsbbpos.clabs;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FacilitiesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_facilities);

        // custom action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Typeface hotelFont = Typeface.createFromAsset(getAssets(), "fonts/OptimusPrinceps.ttf");
        Typeface iconFont = Typeface.createFromAsset(getAssets(), "fonts/NuevaStd-Bold.otf");
        Typeface arialFont = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");

        TextView activityTitle = (TextView) findViewById(R.id.activity_title);
        activityTitle.setTypeface(iconFont);

        TextView hotelName = (TextView) findViewById(R.id.hotel_name);
        hotelName.setTypeface(hotelFont);

        TextView roomNumber = (TextView) findViewById(R.id.room_number);
        roomNumber.setTypeface(hotelFont);

        RelativeLayout restaurantLayout = (RelativeLayout) findViewById(R.id.restaurant_layout);
        restaurantLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout functionroomLayout = (RelativeLayout) findViewById(R.id.functionroom_layout);
        functionroomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FunctionRoomActivity.class);
                startActivity(intent);
            }
        });


        RelativeLayout fitnessLayout = (RelativeLayout) findViewById(R.id.fitness_layout);
        fitnessLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FitnessActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout spaLayout = (RelativeLayout) findViewById(R.id.spa_layout);
        spaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SpaActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout swimLayout = (RelativeLayout) findViewById(R.id.swimming_layout);
        swimLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SwimActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
