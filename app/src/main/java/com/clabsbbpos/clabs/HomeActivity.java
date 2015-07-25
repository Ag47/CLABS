package com.clabsbbpos.clabs;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("h:mm a");
        String currentTime = currentTimeFormat.format(new Date()).toString().toUpperCase();
        TextView time = (TextView) findViewById(R.id.time);
        time.setText(currentTime);

        SimpleDateFormat currentDateFormat = new SimpleDateFormat("EEEE d MMMM yyyy");
        String currentDate = currentDateFormat.format(new Date());
        TextView date = (TextView) findViewById(R.id.date);
        date.setText(currentDate);

        Typeface infoFont = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
        Typeface hotelFont = Typeface.createFromAsset(getAssets(), "fonts/OptimusPrinceps.ttf");

        TextView hotelName = (TextView) findViewById(R.id.hotel_name);
        hotelName.setTypeface(hotelFont);

        TextView roomNumber = (TextView) findViewById(R.id.room_number);
        roomNumber.setTypeface(hotelFont);

        time.setTypeface(infoFont);
        date.setTypeface(infoFont);

        TextView temperature = (TextView) findViewById(R.id.temperature);
        temperature.setTypeface(infoFont);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
