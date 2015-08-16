package com.clabsbbpos.clabs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LocationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_location);

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

        final String[] itemname = {
                "Car Reservation",
                "Transportation",
                "Map"
        };

        final Integer[] imgid = {
                R.drawable.carreservation,
                R.drawable.transport,
                R.drawable.map,
        };
        String[] itemtext = {
                "chauffeured luxury cars\t\tbook now",
                "MTR, bus, train information\tsee the details",
                "Hotel location                            view the map",
        };
        CustomListAdapter adapter = new CustomListAdapter(this, itemname, imgid, itemtext);
        ListView list = (ListView) findViewById(R.id.service_lv);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String Slecteditem = itemname[+position];
//                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(getApplicationContext(), CarReservationActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getApplicationContext(), TransportationActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(intent);
                        break;
                }

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

    private class CustomListAdapter extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] itemname;
        private final Integer[] imgid;
        private final String[] itemtext;

        public CustomListAdapter(Activity context, String[] itemname, Integer[] imgid, String[] itemtext) {
            super(context, R.layout.card_services, itemname);

            this.context = context;
            this.itemname = itemname;
            this.imgid = imgid;
            this.itemtext = itemtext;
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.card_services, null, true);

            RelativeLayout serviceBackground = (RelativeLayout) rowView.findViewById(R.id.service_layout);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.service_title);
            TextView extratxt = (TextView) rowView.findViewById(R.id.detail_text);

            txtTitle.setText(itemname[position]);
            serviceBackground.setBackgroundResource(imgid[position]);
            extratxt.setText(itemtext[position]);
            return rowView;

        }

        ;
    }
}
