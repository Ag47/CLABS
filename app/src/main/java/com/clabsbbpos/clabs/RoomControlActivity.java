package com.clabsbbpos.clabs;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RoomControlActivity extends ActionBarActivity {

    private int temperatureValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_roomcontrol);

        // custom action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Typeface hotelFont = Typeface.createFromAsset(getAssets(), "fonts/OptimusPrinceps.ttf");
        Typeface iconFont = Typeface.createFromAsset(getAssets(), "fonts/NuevaStd-Bold.otf");

        TextView activityTitle = (TextView) findViewById(R.id.activity_title);
        activityTitle.setTypeface(iconFont);

        TextView hotelName = (TextView) findViewById(R.id.hotel_name);
        hotelName.setTypeface(hotelFont);

        TextView roomNumber = (TextView) findViewById(R.id.room_number);
        roomNumber.setTypeface(hotelFont);

        // Switch
        SwitchCompat airConSwitch = (SwitchCompat) findViewById(R.id.aircon_switch);

        final RelativeLayout temperatureLayout = (RelativeLayout) findViewById(R.id.temp_layout);
        final RelativeLayout swingLayout = (RelativeLayout) findViewById(R.id.swing_layout);
        final RelativeLayout fanSpeedLayout = (RelativeLayout) findViewById(R.id.fanSpeed_layout);

        airConSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    temperatureLayout.setVisibility(View.VISIBLE);
                    swingLayout.setVisibility(View.VISIBLE);
                    fanSpeedLayout.setVisibility(View.VISIBLE);
                } else {
                    temperatureLayout.setVisibility(View.GONE);
                    swingLayout.setVisibility(View.GONE);
                    fanSpeedLayout.setVisibility(View.GONE);
                }
            }
        });

        TextView tempPlus = (TextView) findViewById(R.id.temp_plus);
        TextView tempMinus = (TextView) findViewById(R.id.temp_minus);
        final TextView temperature = (TextView) findViewById(R.id.temperature);
        temperatureValue = Integer.parseInt(temperature.getText().toString().substring(0, 2));
        Log.i("temperature value", Integer.toString(temperatureValue));

        tempPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temperature.setText(Integer.toString(++temperatureValue) + "°C");
            }
        });

        tempMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temperature.setText(Integer.toString(--temperatureValue) + "°C");
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
