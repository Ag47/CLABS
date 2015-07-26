package com.clabsbbpos.clabs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
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
        Typeface iconFont = Typeface.createFromAsset(getAssets(), "fonts/NuevaStd-Bold.otf");

        // info box TextView
        time.setTypeface(infoFont);
        date.setTypeface(infoFont);

        TextView temperature = (TextView) findViewById(R.id.temperature);
        temperature.setTypeface(infoFont);

        TextView marqueeMessage = (TextView) findViewById(R.id.marguee_message);
        marqueeMessage.setTypeface(infoFont);
        marqueeMessage.setSelected(true);

        // menu icon TextView
        TextView mystayTV = (TextView) findViewById(R.id.mystay_tv);
        mystayTV.setTypeface(iconFont);

        TextView roomcontrolTV = (TextView) findViewById(R.id.roomcontrol_tv);
        roomcontrolTV.setTypeface(iconFont);

        TextView roomserviceTV = (TextView) findViewById(R.id.roomservice_tv);
        roomserviceTV.setTypeface(iconFont);

        TextView hotelfacilitiesTV = (TextView) findViewById(R.id.hotelfacilities_tv);
        hotelfacilitiesTV.setTypeface(iconFont);

        TextView myReservationsTV = (TextView) findViewById(R.id.myreservations_tv);
        myReservationsTV.setTypeface(iconFont);

        TextView callTV = (TextView) findViewById(R.id.directphonecall_tv);
        callTV.setTypeface(iconFont);

        TextView moreTV = (TextView) findViewById(R.id.more_tv);
        moreTV.setTypeface(iconFont);

        TextView locationTV = (TextView) findViewById(R.id.location_tv);
        locationTV.setTypeface(iconFont);

        TextView checkoutTV = (TextView) findViewById(R.id.checkout_tv);
        checkoutTV.setTypeface(iconFont);

        // footer TextView
        TextView hotelName = (TextView) findViewById(R.id.hotel_name);
        hotelName.setTypeface(hotelFont);

        TextView roomNumber = (TextView) findViewById(R.id.room_number);
        roomNumber.setTypeface(hotelFont);

        // menu icon intent
        // TODO vibrate when click for accessibility
        final Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        RelativeLayout mystayLayout = (RelativeLayout) findViewById(R.id.mystay_layout);
        mystayLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                vibrator.vibrate(100);
                return false;
            }
        });
        mystayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MyStayActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout roomcontrolLayout = (RelativeLayout) findViewById(R.id.roomcontrol_layout);
        roomcontrolLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), .class);
//                startActivity(intent);
            }
        });

        RelativeLayout roomserviceLayout = (RelativeLayout) findViewById(R.id.roomservice_layout);
        roomcontrolLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), .class);
//                startActivity(intent);
            }
        });

        RelativeLayout hotelfacilitiesLayout = (RelativeLayout) findViewById(R.id.hotelfacilities_layout);
        hotelfacilitiesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), .class);
//                startActivity(intent);
            }
        });

        RelativeLayout myReservationsLayout = (RelativeLayout) findViewById(R.id.myreservations_layout);
        myReservationsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), .class);
//                startActivity(intent);
            }
        });

        RelativeLayout callLayout = (RelativeLayout) findViewById(R.id.directphonecall_layout);
        callLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), .class);
//                startActivity(intent);
            }
        });

        RelativeLayout moreLayout = (RelativeLayout) findViewById(R.id.more_layout);
        moreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), .class);
//                startActivity(intent);
            }
        });

        RelativeLayout locationLayout = (RelativeLayout) findViewById(R.id.location_layout);
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), .class);
//                startActivity(intent);
            }
        });

        RelativeLayout checkoutLayout = (RelativeLayout) findViewById(R.id.checkout_layout);
        checkoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), .class);
//                startActivity(intent);
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

        return super.onOptionsItemSelected(item);
    }
}
