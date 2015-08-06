package com.clabsbbpos.clabs;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HouseKeepingActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_housekeeping);

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

        RelativeLayout makeUpLayout = (RelativeLayout) findViewById(R.id.makeUp_layout);
        RelativeLayout doNotDisturbLayout = (RelativeLayout) findViewById(R.id.doNotDisturb_layout);

        final ImageView makeUpTick = (ImageView) findViewById(R.id.makeUp_tick);
        final ImageView doNotDisturbTick = (ImageView) findViewById(R.id.doNotDisturb_tick);

        makeUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (makeUpTick.getVisibility() == View.GONE)
                    if (doNotDisturbTick.getVisibility() == View.VISIBLE) {
                        makeUpTick.setVisibility(View.VISIBLE);
                        doNotDisturbTick.setVisibility(View.GONE);
                    } else
                        makeUpTick.setVisibility(View.VISIBLE);
                else
                    makeUpTick.setVisibility(View.GONE);
            }
        });

        doNotDisturbLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doNotDisturbTick.getVisibility() == View.GONE)
                    if (makeUpTick.getVisibility() == View.VISIBLE) {
                        doNotDisturbTick.setVisibility(View.VISIBLE);
                        makeUpTick.setVisibility(View.GONE);
                    } else
                        doNotDisturbTick.setVisibility(View.VISIBLE);
                else
                    doNotDisturbTick.setVisibility(View.GONE);
            }
        });

        TextView roomNumber = (TextView) findViewById(R.id.room_number);
        roomNumber.setTypeface(hotelFont);

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
