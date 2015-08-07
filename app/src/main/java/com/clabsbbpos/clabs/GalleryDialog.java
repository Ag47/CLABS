package com.clabsbbpos.clabs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class GalleryDialog extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gallery);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        GalleryAdapter adapter = new GalleryAdapter(this);
        viewPager.setAdapter(adapter);
    }

}