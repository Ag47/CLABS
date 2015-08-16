package com.clabsbbpos.clabs;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bbpos.wisepad.WisePadController;
import com.bbpos.wisepad.WisePosController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class HomeActivity extends ActionBarActivity implements WisePosController.WisePosControllerListener {

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

        TextView bizTV = (TextView) findViewById(R.id.business_service_tv);
        bizTV.setTypeface(iconFont);

        TextView callTV = (TextView) findViewById(R.id.phone_tv);
        callTV.setTypeface(iconFont);

        TextView locationTV = (TextView) findViewById(R.id.location_tv);
        locationTV.setTypeface(iconFont);

        TextView moreTV = (TextView) findViewById(R.id.more_tv);
        moreTV.setTypeface(iconFont);

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
                Intent intent = new Intent(getApplicationContext(), RoomControlActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout roomserviceLayout = (RelativeLayout) findViewById(R.id.roomservice_layout);
        roomserviceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RoomServiceActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout hotelfacilitiesLayout = (RelativeLayout) findViewById(R.id.hotelfacilities_layout);
        hotelfacilitiesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FacilitiesActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout myReservationsLayout = (RelativeLayout) findViewById(R.id.myreservations_layout);
        myReservationsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyBookingActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout businessServiceLayout = (RelativeLayout) findViewById(R.id.business_service_layout);
        businessServiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BizServicesActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout callLayout = (RelativeLayout) findViewById(R.id.call_layout);
        callLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DirectPhoneCallActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout locationLayout = (RelativeLayout) findViewById(R.id.location_layout);
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout moreLayout = (RelativeLayout) findViewById(R.id.more_layout);
        moreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MoreActivity.class);
                startActivity(intent);
            }
        });

        TextView checkout = (TextView) findViewById(R.id.checkOut);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CheckOutActivity.class);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSerialConnected() {

    }

    @Override
    public void onSerialDisconnected() {

    }

    @Override
    public void onRequestDisplayAsterisk(int i) {

    }

    @Override
    public void onWaitingForCard(WisePadController.CheckCardMode checkCardMode) {

    }

    @Override
    public void onWaitingReprintOrPrintNext() {

    }

    @Override
    public void onBTv2Detected() {

    }

    @Override
    public void onBTv2DeviceListRefresh(List<BluetoothDevice> list) {

    }

    @Override
    public void onBTv2Connected(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void onBTv2Disconnected() {

    }

    @Override
    public void onBTv2ScanStopped() {

    }

    @Override
    public void onBTv2ScanTimeout() {

    }

    @Override
    public void onBTv4DeviceListRefresh(List<BluetoothDevice> list) {

    }

    @Override
    public void onBTv4Connected() {

    }

    @Override
    public void onBTv4Disconnected() {

    }

    @Override
    public void onBTv4ScanStopped() {

    }

    @Override
    public void onBTv4ScanTimeout() {

    }

    @Override
    public void onReturnCheckCardResult(WisePadController.CheckCardResult checkCardResult, Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnCancelCheckCardResult(boolean b) {

    }

    @Override
    public void onReturnStartEmvResult(WisePadController.StartEmvResult startEmvResult, String s) {

    }

    @Override
    public void onReturnDeviceInfo(Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnEmvTransactionLog(String[] strings) {

    }

    @Override
    public void onReturnEmvLoadLog(String[] strings) {

    }

    @Override
    public void onReturnTransactionResult(WisePadController.TransactionResult transactionResult) {

    }

    @Override
    public void onReturnTransactionResult(WisePadController.TransactionResult transactionResult, Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnBatchData(String s) {

    }

    @Override
    public void onReturnTransactionLog(String s) {

    }

    @Override
    public void onReturnReversalData(String s) {

    }

    @Override
    public void onReturnAmountConfirmResult(boolean b) {

    }

    @Override
    public void onReturnPinEntryResult(WisePadController.PinEntryResult pinEntryResult, Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnPrintResult(WisePadController.PrintResult printResult) {

    }

    @Override
    public void onReturnAmount(Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnUpdateTerminalSettingResult(WisePadController.TerminalSettingStatus terminalSettingStatus) {

    }

    @Override
    public void onReturnReadTerminalSettingResult(WisePadController.TerminalSettingStatus terminalSettingStatus, String s) {

    }

    @Override
    public void onReturnEnableInputAmountResult(boolean b) {

    }

    @Override
    public void onReturnDisableInputAmountResult(boolean b) {

    }

    @Override
    public void onReturnPhoneNumber(WisePadController.PhoneEntryResult phoneEntryResult, String s) {

    }

    @Override
    public void onReturnEmvCardBalance(boolean b, String s) {

    }

    @Override
    public void onReturnEmvCardDataResult(boolean b, String s) {

    }

    @Override
    public void onReturnEmvCardNumber(String s) {

    }

    @Override
    public void onReturnMagStripeCardNumber(WisePadController.CheckCardResult checkCardResult, String s) {

    }

    @Override
    public void onReturnEncryptDataResult(boolean b, Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnInjectSessionKeyResult(boolean b) {

    }

    @Override
    public void onReturnViposBatchExchangeApduResult(Hashtable<Integer, String> hashtable) {

    }

    @Override
    public void onReturnViposExchangeApduResult(String s) {

    }

    @Override
    public void onReturnPowerOnIccResult(boolean b, String s, String s1, int i) {

    }

    @Override
    public void onReturnPowerOffIccResult(boolean b) {

    }

    @Override
    public void onReturnApduResult(boolean b, String s, int i) {

    }

    @Override
    public void onReturnApduResultWithPkcs7Padding(boolean b, String s) {

    }

    @Override
    public void onRequestSelectApplication(ArrayList<String> arrayList) {

    }

    @Override
    public void onRequestSetAmount() {

    }

    @Override
    public void onRequestPinEntry(WisePadController.PinEntrySource pinEntrySource) {

    }

    @Override
    public void onRequestCheckServerConnectivity() {

    }

    @Override
    public void onRequestOnlineProcess(String s) {

    }

    @Override
    public void onRequestTerminalTime() {
        Log.i("BBPOS", "termianl TIMe");


    }

    @Override
    public void onRequestDisplayText(WisePadController.DisplayText displayText) {

    }

    @Override
    public void onRequestClearDisplay() {

    }

    @Override
    public void onRequestReferProcess(String s) {

    }

    @Override
    public void onRequestAdviceProcess(String s) {

    }

    @Override
    public void onRequestFinalConfirm() {

    }

    @Override
    public void onRequestVerifyID(String s) {

    }

    @Override
    public void onRequestInsertCard() {

    }

    @Override
    public void onRequestPrintData(int i, boolean b) {

    }

    @Override
    public void onPrintDataCancelled() {

    }

    @Override
    public void onPrintDataEnd() {

    }

    @Override
    public void onBatteryLow(WisePadController.BatteryStatus batteryStatus) {

    }

    @Override
    public void onBTv2DeviceNotFound() {

    }

    @Override
    public void onAudioDeviceNotFound() {

    }

    @Override
    public void onDevicePlugged() {

    }

    @Override
    public void onDeviceUnplugged() {

    }

    @Override
    public void onError(WisePadController.Error error, String s) {

    }
}