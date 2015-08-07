package com.clabsbbpos.clabs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpos.wisepad.BarcodeReaderController;
import com.bbpos.wisepad.BarcodeReaderController.BarcodeReaderControllerListener;
import com.bbpos.wisepad.WisePadController;
import com.bbpos.wisepad.WisePadController.AmountInputType;
import com.bbpos.wisepad.WisePadController.BatteryStatus;
import com.bbpos.wisepad.WisePadController.CheckCardMode;
import com.bbpos.wisepad.WisePadController.CheckCardResult;
import com.bbpos.wisepad.WisePadController.ConnectionMode;
import com.bbpos.wisepad.WisePadController.CurrencyCharacter;
import com.bbpos.wisepad.WisePadController.DisplayText;
import com.bbpos.wisepad.WisePadController.EmvOption;
import com.bbpos.wisepad.WisePadController.Error;
import com.bbpos.wisepad.WisePadController.PhoneEntryResult;
import com.bbpos.wisepad.WisePadController.PinEntryResult;
import com.bbpos.wisepad.WisePadController.PinEntrySource;
import com.bbpos.wisepad.WisePadController.PrintResult;
import com.bbpos.wisepad.WisePadController.ReferralResult;
import com.bbpos.wisepad.WisePadController.StartEmvResult;
import com.bbpos.wisepad.WisePadController.TerminalSettingStatus;
import com.bbpos.wisepad.WisePadController.TransactionResult;
import com.bbpos.wisepad.WisePadController.TransactionType;
import com.bbpos.wisepad.WisePosController;
import com.bbpos.wisepad.WisePosController.WisePosControllerListener;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CardActivity extends Activity {

    protected static final String[] DEVICE_NAMES = new String[]{"WisePad", "WP", "MPOS", "M360", "M368"};
    protected static String masterKey = "11223344556677889900AABBCCDDEEFF";
    protected static String pinSessionKey = "A1223344556677889900AABBCCDDEEFF";
    protected static String encryptedPinSessionKey = "";
    protected static String pinKcv = "";
    protected static String dataSessionKey = "A2223344556677889900AABBCCDDEEFF";
    protected static String encryptedDataSessionKey = "";
    protected static String dataKcv = "";
    protected static String trackSessionKey = "A4223344556677889900AABBCCDDEEFF";
    protected static String encryptedTrackSessionKey = "";
    protected static String trackKcv = "";
    protected static String macSessionKey = "A6223344556677889900AABBCCDDEEFF";
    protected static String encryptedMacSessionKey = "";
    protected static String macKcv = "";
    protected static CheckCardMode checkCardMode;
    protected ArrayAdapter<String> arrayAdapter;
    protected List<BluetoothDevice> foundDevices;
    private Handler handler;
    private Button startButton;
    private EditText amountEditText;
    private EditText statusEditText;
    private ListView appListView;
    private Dialog dialog;
    private WisePosController wisePosController;
    private BarcodeReaderController barcodeReaderController;
    private String amount = "";
    private String cashbackAmount = "";
    private boolean isPinCanceled = false;
    private ArrayList<byte[]> receipts;
    private Context context;

    private static byte[] hexToByteArray(String s) {
        if (s == null) {
            s = "";
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for (int i = 0; i < s.length() - 1; i += 2) {
            String data = s.substring(i, i + 2);
            bout.write(Integer.parseInt(data, 16));
        }
        return bout.toByteArray();
    }

    private static String toHexString(byte[] b) {
        if (b == null) {
            return "null";
        }
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xFF) + 0x100, 16).substring(1);
        }
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_card);

        ((TextView) findViewById(R.id.modelTextView)).setText(Build.MANUFACTURER.toUpperCase(Locale.ENGLISH) + " - " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")");

        startButton = (Button) findViewById(R.id.startButton);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        statusEditText = (EditText) findViewById(R.id.statusEditText);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        startButton.setOnClickListener(myOnClickListener);

        wisePosController = WisePosController.getInstance(this, new MyWisePosControllerListener());
        barcodeReaderController = BarcodeReaderController.getInstance(this, new MyBarcodeReaderControllerListener());

        handler = new Handler();
//        promptForConnection();
        wisePosController.startSerial();
        checkCardMode = CheckCardMode.INSERT;
        isPinCanceled = false;
        amountEditText.setText("");
        statusEditText.setText(R.string.starting);
        Hashtable<String, Object> data = new Hashtable<String, Object>();
        data.put("checkCardTimeout", "30");
        data.put("checkCardMode", checkCardMode);
        wisePosController.checkCard(data);
        context = getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopConnection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_start_connection) {
            promptForConnection();
            return true;
        } else if (item.getItemId() == R.id.menu_stop_connection) {
            stopConnection();
            return true;
        } else if (item.getItemId() == R.id.menu_get_deivce_info) {
            statusEditText.setText(R.string.getting_info);
            wisePosController.getDeviceInfo();
        } else if (item.getItemId() == R.id.menu_inject_session_key) {
            encryptedPinSessionKey = encrypt(pinSessionKey, masterKey);
            encryptedDataSessionKey = encrypt(dataSessionKey, masterKey);
            encryptedTrackSessionKey = encrypt(trackSessionKey, masterKey);
            encryptedMacSessionKey = encrypt(macSessionKey, masterKey);

            pinKcv = "0000000000000000";
            pinKcv = encrypt(pinKcv, pinSessionKey);
            pinKcv = pinKcv.substring(0, 6);

            dataKcv = "0000000000000000";
            dataKcv = encrypt(dataKcv, dataSessionKey);
            dataKcv = dataKcv.substring(0, 6);

            trackKcv = "0000000000000000";
            trackKcv = encrypt(trackKcv, trackSessionKey);
            trackKcv = trackKcv.substring(0, 6);

            macKcv = "0000000000000000";
            macKcv = encrypt(macKcv, macSessionKey);
            macKcv = macKcv.substring(0, 6);

            injectNextSessionKey();
        } else if (item.getItemId() == R.id.menu_enable_input_amount) {
            Hashtable<String, Object> data = new Hashtable<String, Object>();
            data.put("currencyCode", "840");
            data.put("currencyCharacters", new CurrencyCharacter[]{CurrencyCharacter.DOLLAR});
            data.put("amountInputType", AmountInputType.AMOUNT_AND_CASHBACK);
            wisePosController.enableInputAmount(data);
        } else if (item.getItemId() == R.id.menu_read_barcode) {
            barcodeReaderController.startBarcodeReader();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == 138) {
            barcodeReaderController.startBarcodeReader();
            barcodeReaderController.getBarcode();
            return true;
        }
        return super.onKeyDown(keycode, event);
    }

    public String encrypt(String data, String key) {
        if (key.length() == 16) {
            key += key.substring(0, 8);
        }
        byte[] d = hexToByteArray(data);
        byte[] k = hexToByteArray(key);

        SecretKey sk = new SecretKeySpec(k, "DESede");
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, sk);
            byte[] enc = cipher.doFinal(d);
            return toHexString(enc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void injectNextSessionKey() {
        if (!encryptedPinSessionKey.equals("")) {
            Hashtable<String, String> data = new Hashtable<String, String>();
            data.put("index", "1");
            data.put("encSK", encryptedPinSessionKey);
            data.put("kcv", pinKcv);
            statusEditText.setText(getString(R.string.sending_encrypted_pin_session_key));
            encryptedPinSessionKey = "";
            wisePosController.injectSessionKey(data);
            return;
        }

        if (!encryptedDataSessionKey.equals("")) {
            Hashtable<String, String> data = new Hashtable<String, String>();
            data.put("index", "2");
            data.put("encSK", encryptedDataSessionKey);
            data.put("kcv", dataKcv);
            statusEditText.setText(getString(R.string.sending_encrypted_data_session_key));
            encryptedDataSessionKey = "";
            wisePosController.injectSessionKey(data);
            return;
        }

        if (!encryptedTrackSessionKey.equals("")) {
            Hashtable<String, String> data = new Hashtable<String, String>();
            data.put("index", "3");
            data.put("encSK", encryptedTrackSessionKey);
            data.put("kcv", trackKcv);
            statusEditText.setText(getString(R.string.sending_encrypted_track_session_key));
            encryptedTrackSessionKey = "";
            wisePosController.injectSessionKey(data);
            return;
        }

        if (!encryptedMacSessionKey.equals("")) {
            Hashtable<String, String> data = new Hashtable<String, String>();
            data.put("index", "4");
            data.put("encSK", encryptedMacSessionKey);
            data.put("kcv", macKcv);
            statusEditText.setText(getString(R.string.sending_encrypted_mac_session_key));
            encryptedMacSessionKey = "";
            wisePosController.injectSessionKey(data);
            return;
        }
    }

    public void stopConnection() {
        ConnectionMode connectionMode = wisePosController.getConnectionMode();
        if (connectionMode == ConnectionMode.BLUETOOTH_2) {
            wisePosController.stopBTv2();
        } else if (connectionMode == ConnectionMode.BLUETOOTH_4) {
            wisePosController.disconnectBTv4();
        } else if (connectionMode == ConnectionMode.AUDIO) {
            wisePosController.stopAudio();
        } else if (connectionMode == ConnectionMode.SERIAL) {
            wisePosController.stopSerial();
        }
    }

    public void promptForConnection() {
        dismissDialog();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.connection_dialog);
        dialog.setTitle(getString(R.string.connection));

        String[] connections = new String[4];
        connections[0] = "Bluetooth 2";
        connections[1] = "BLE";
        connections[2] = "Audio";
        connections[3] = "Serial";

        ListView listView = (ListView) dialog.findViewById(R.id.connectionList);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, connections));
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismissDialog();
                if (position == 0) {
                    Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
                    final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
                    for (int i = 0; i < pairedObjects.length; ++i) {
                        pairedDevices[i] = (BluetoothDevice) pairedObjects[i];
                    }

                    final ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(CardActivity.this, android.R.layout.simple_list_item_1);
                    for (int i = 0; i < pairedDevices.length; ++i) {
                        mArrayAdapter.add(pairedDevices[i].getName());
                    }

                    dismissDialog();

                    dialog = new Dialog(CardActivity.this);
                    dialog.setContentView(R.layout.bluetooth_2_device_list_dialog);
                    dialog.setTitle(R.string.bluetooth_devices);

                    ListView listView1 = (ListView) dialog.findViewById(R.id.pairedDeviceList);
                    listView1.setAdapter(mArrayAdapter);
                    listView1.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            statusEditText.setText(getString(R.string.connecting_bluetooth_2));
                            wisePosController.startBTv2(pairedDevices[position]);
                            dismissDialog();
                        }

                    });

                    arrayAdapter = new ArrayAdapter<String>(CardActivity.this, android.R.layout.simple_list_item_1);
                    ListView listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList);
                    listView2.setAdapter(arrayAdapter);
                    listView2.setOnItemClickListener(new OnItemClickListener() {


                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            statusEditText.setText(getString(R.string.connecting_bluetooth_2));
                            wisePosController.startBTv2(foundDevices.get(position));
                            dismissDialog();
                        }

                    });

                    dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            wisePosController.stopScanBTv2();
                            dismissDialog();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();

                    wisePosController.scanBTv2(DEVICE_NAMES, 120);
                } else if (position == 1) {
                    statusEditText.setText(getString(R.string.scanning_bluetooth_4));
                    wisePosController.scanBTv4(DEVICE_NAMES, 120);

                    arrayAdapter = new ArrayAdapter<String>(CardActivity.this, android.R.layout.simple_list_item_1);
                    dialog = new Dialog(CardActivity.this);
                    dialog.setContentView(R.layout.bluetooth_4_device_list_dialog);
                    dialog.setTitle(R.string.btv4_devices);

                    ListView listView2 = (ListView) dialog.findViewById(R.id.deviceList);
                    listView2.setAdapter(arrayAdapter);
                    listView2.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            statusEditText.setText(getString(R.string.connecting_bluetooth_4));
                            wisePosController.connectBTv4(foundDevices.get(position));
                            dismissDialog();
                        }

                    });

                    dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            wisePosController.stopScanBTv4();
                            dismissDialog();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                } else if (position == 2) {
                    wisePosController.startAudio();
                } else if (position == 3) {
                    wisePosController.startSerial();
                }
            }

        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        dialog.show();
    }

    public void promptForCheckCard() {
        dismissDialog();
        dialog = new Dialog(CardActivity.this);
        dialog.setContentView(R.layout.check_card_mode_dialog);
        dialog.setTitle(getString(R.string.select_mode));

        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                RadioButton swipeRadioButton = (RadioButton) dialog.findViewById(R.id.swipeRadioButton);
                RadioButton insertRadioButton = (RadioButton) dialog.findViewById(R.id.insertRadioButton);
                RadioButton tapRadioButton = (RadioButton) dialog.findViewById(R.id.tapRadioButton);
                RadioButton swipeOrInsertRadioButton = (RadioButton) dialog.findViewById(R.id.swipeOrInsertRadioButton);

                if (swipeRadioButton.isChecked()) {
                    checkCardMode = CheckCardMode.SWIPE;
                } else if (insertRadioButton.isChecked()) {
                    checkCardMode = CheckCardMode.INSERT;
                } else if (tapRadioButton.isChecked()) {
                    checkCardMode = CheckCardMode.TAP;
                } else if (swipeOrInsertRadioButton.isChecked()) {
                    checkCardMode = CheckCardMode.SWIPE_OR_INSERT;
                } else {
                    dismissDialog();
                    return;
                }

                isPinCanceled = false;
                amountEditText.setText("");
                statusEditText.setText(R.string.starting);

                if (checkCardMode == CheckCardMode.TAP) {
                    String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
                    Hashtable<String, Object> data = new Hashtable<String, Object>();
                    data.put("terminalTime", terminalTime);
                    data.put("checkCardTimeout", "120");
                    data.put("setAmountTimeout", "120");
                    data.put("selectApplicationTimeout", "120");
                    data.put("finalConfirmTimeout", "120");
                    data.put("onlineProcessTimeout", "120");
                    data.put("pinEntryTimeout", "120");
                    data.put("emvOption", EmvOption.START);
                    data.put("checkCardMode", checkCardMode);
                    wisePosController.startEmv(data);
                } else {
                    Hashtable<String, Object> data = new Hashtable<String, Object>();
                    data.put("checkCardTimeout", "30");
                    data.put("checkCardMode", checkCardMode);
                    wisePosController.checkCard(data);
                }

                dismissDialog();
            }
        };

        RadioButton swipeRadioButton = (RadioButton) dialog.findViewById(R.id.swipeRadioButton);
        RadioButton insertRadioButton = (RadioButton) dialog.findViewById(R.id.insertRadioButton);
        RadioButton tapRadioButton = (RadioButton) dialog.findViewById(R.id.tapRadioButton);
        RadioButton swipeOrInsertRadioButton = (RadioButton) dialog.findViewById(R.id.swipeOrInsertRadioButton);

        swipeRadioButton.setOnClickListener(onClickListener);
        insertRadioButton.setOnClickListener(onClickListener);
        tapRadioButton.setOnClickListener(onClickListener);
        swipeOrInsertRadioButton.setOnClickListener(onClickListener);

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissDialog();
            }

        });

        dialog.show();
    }

    public void promptForAmount() {
        dismissDialog();
        dialog = new Dialog(CardActivity.this);
        dialog.setContentView(R.layout.amount_dialog);
        dialog.setTitle(getString(R.string.set_amount));

        String[] symbols = new String[]{
                "DOLLAR",
                "RUPEE",
                "YEN",
                "POUND",
                "EURO",
                "WON",
                "DIRHAM",
                "RIYAL",
                "AED",
                "BS.",
                "YUAN",
                "NULL"
        };
        ((Spinner) dialog.findViewById(R.id.symbolSpinner)).setAdapter(new ArrayAdapter<String>(CardActivity.this, android.R.layout.simple_spinner_item, symbols));

        String[] transactionTypes = new String[]{
                "GOODS",
                "SERVICES",
                "CASHBACK",
                "INQUIRY",
                "TRANSFER",
                "PAYMENT",
                "REFUND"
        };
        ((Spinner) dialog.findViewById(R.id.transactionTypeSpinner)).setAdapter(new ArrayAdapter<String>(CardActivity.this, android.R.layout.simple_spinner_item, transactionTypes));

        dialog.findViewById(R.id.setButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String amount = ((EditText) (dialog.findViewById(R.id.amountEditText))).getText().toString();
                String cashbackAmount = ((EditText) (dialog.findViewById(R.id.cashbackAmountEditText))).getText().toString();
                String transactionTypeString = (String) ((Spinner) dialog.findViewById(R.id.transactionTypeSpinner)).getSelectedItem();
                String symbolString = (String) ((Spinner) dialog.findViewById(R.id.symbolSpinner)).getSelectedItem();

                TransactionType transactionType = TransactionType.GOODS;
                if (transactionTypeString.equals("GOODS")) {
                    transactionType = TransactionType.GOODS;
                } else if (transactionTypeString.equals("SERVICES")) {
                    transactionType = TransactionType.SERVICES;
                } else if (transactionTypeString.equals("CASHBACK")) {
                    transactionType = TransactionType.CASHBACK;
                } else if (transactionTypeString.equals("INQUIRY")) {
                    transactionType = TransactionType.INQUIRY;
                } else if (transactionTypeString.equals("TRANSFER")) {
                    transactionType = TransactionType.TRANSFER;
                } else if (transactionTypeString.equals("PAYMENT")) {
                    transactionType = TransactionType.PAYMENT;
                } else if (transactionTypeString.equals("REFUND")) {
                    transactionType = TransactionType.REFUND;
                }

                CurrencyCharacter[] currencyCharacters = new CurrencyCharacter[]{CurrencyCharacter.A, CurrencyCharacter.B, CurrencyCharacter.C};
                if (symbolString.equals("DOLLAR")) {
                    currencyCharacters = new CurrencyCharacter[]{CurrencyCharacter.DOLLAR};
                } else if (symbolString.equals("RUPEE")) {
                    currencyCharacters = new CurrencyCharacter[]{CurrencyCharacter.RUPEE};
                } else if (symbolString.equals("YEN")) {
                    currencyCharacters = new CurrencyCharacter[]{CurrencyCharacter.YEN};
                } else if (symbolString.equals("POUND")) {
                    currencyCharacters = new CurrencyCharacter[]{CurrencyCharacter.POUND};
                } else if (symbolString.equals("EURO")) {
                    currencyCharacters = new CurrencyCharacter[]{CurrencyCharacter.EURO};
                } else if (symbolString.equals("WON")) {
                    currencyCharacters = new CurrencyCharacter[]{CurrencyCharacter.WON};
                } else if (symbolString.equals("DIRHAM")) {
                    currencyCharacters = new CurrencyCharacter[]{CurrencyCharacter.DIRHAM};
                } else if (symbolString.equals("RIYAL")) {
                    currencyCharacters = new CurrencyCharacter[]{CurrencyCharacter.RIYAL, CurrencyCharacter.RIYAL_2};
                } else if (symbolString.equals("AED")) {
                    currencyCharacters = new CurrencyCharacter[]{CurrencyCharacter.A, CurrencyCharacter.E, CurrencyCharacter.D};
                } else if (symbolString.equals("BS.")) {
                    currencyCharacters = new CurrencyCharacter[]{CurrencyCharacter.B, CurrencyCharacter.S, CurrencyCharacter.DOT};
                } else if (symbolString.equals("YUAN")) {
                    currencyCharacters = new CurrencyCharacter[]{CurrencyCharacter.YUAN};
                } else if (symbolString.equals("NULL")) {
                    currencyCharacters = null;
                }

                if (wisePosController.setAmount(amount, cashbackAmount, "840", transactionType, currencyCharacters)) {
                    amountEditText.setText("$" + amount);
                    CardActivity.this.amount = amount;
                    CardActivity.this.cashbackAmount = cashbackAmount;
                    dismissDialog();
                } else {
                    promptForAmount();
                }
            }

        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                wisePosController.cancelSetAmount();
                dialog.dismiss();
            }

        });

        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    class MyWisePosControllerListener implements WisePosControllerListener {

        @Override
        public void onWaitingForCard(CheckCardMode checkCardMode) {
            dismissDialog();
            switch (checkCardMode) {
                case INSERT:
                    statusEditText.setText(getString(R.string.please_insert_card));
                    break;
                case SWIPE:
                    statusEditText.setText(getString(R.string.please_swipe_card));
                    break;
                case SWIPE_OR_INSERT:
                    statusEditText.setText(getString(R.string.please_swipe_or_insert_card));
                    break;
                case TAP:
                    statusEditText.setText(getString(R.string.please_tap_card));
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onWaitingReprintOrPrintNext() {
            statusEditText.setText(statusEditText.getText() + "\n" + getString(R.string.please_press_reprint_or_print_next));
        }

        @Override
        public void onBTv2Connected(BluetoothDevice bluetoothDevice) {
            statusEditText.setText(getString(R.string.bluetooth_2_connected) + ": " + bluetoothDevice.getAddress());
        }

        @Override
        public void onBTv2Detected() {
            statusEditText.setText(getString(R.string.bluetooth_2_detected));
        }

        @Override
        public void onBTv2DeviceListRefresh(List<BluetoothDevice> foundDevices) {
            CardActivity.this.foundDevices = foundDevices;
            if (arrayAdapter != null) {
                arrayAdapter.clear();
                for (int i = 0; i < foundDevices.size(); ++i) {
                    arrayAdapter.add(foundDevices.get(i).getName());
                }
                arrayAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onBTv2Disconnected() {
            statusEditText.setText(getString(R.string.bluetooth_2_disconnected));
        }

        @Override
        public void onBTv2ScanStopped() {
            statusEditText.setText(getString(R.string.bluetooth_2_scan_stopped));
        }

        @Override
        public void onBTv2ScanTimeout() {
            statusEditText.setText(getString(R.string.bluetooth_2_scan_timeout));
        }

        @Override
        public void onReturnMagStripeCardNumber(CheckCardResult checkCardResult, String cardNumber) {
            dismissDialog();
            Log.i("POSS", cardNumber);
            if (checkCardResult == CheckCardResult.NONE) {
                statusEditText.setText(getString(R.string.no_card_detected));
            } else if (checkCardResult == CheckCardResult.ICC) {
                statusEditText.setText(getString(R.string.icc_card_inserted));
            } else if (checkCardResult == CheckCardResult.NOT_ICC) {
                statusEditText.setText(getString(R.string.card_inserted));
            } else if (checkCardResult == CheckCardResult.BAD_SWIPE) {
                statusEditText.setText(getString(R.string.bad_swipe));
            } else if (checkCardResult == CheckCardResult.MCR) {
                statusEditText.setText(getString(R.string.pan) + " " + cardNumber);
            } else if (checkCardResult == CheckCardResult.NO_RESPONSE) {
                statusEditText.setText(getString(R.string.card_no_response));
            } else if (checkCardResult == CheckCardResult.TRACK2_ONLY) {
                statusEditText.setText(getString(R.string.pan) + " " + cardNumber);
            }
        }

        @Override
        public void onBTv4Connected() {
            statusEditText.setText(getString(R.string.bluetooth_4_connected));
        }

        @Override
        public void onBTv4DeviceListRefresh(List<BluetoothDevice> foundDevices) {
            CardActivity.this.foundDevices = foundDevices;
            if (arrayAdapter != null) {
                arrayAdapter.clear();
                for (int i = 0; i < foundDevices.size(); ++i) {
                    arrayAdapter.add(foundDevices.get(i).getName());
                }
                arrayAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onBTv4Disconnected() {
            statusEditText.setText(getString(R.string.bluetooth_4_disconnected));
        }

        @Override
        public void onBTv4ScanStopped() {
            statusEditText.setText(getString(R.string.bluetooth_4_scan_stopped));
        }

        @Override
        public void onBTv4ScanTimeout() {
            statusEditText.setText(getString(R.string.bluetooth_4_scan_timeout));
        }

        @Override
        public void onSerialConnected() {
            final ProgressDialog progressDialog = ProgressDialog.show(CardActivity.this, getString(R.string.please_wait), getString(R.string.initializing));
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            statusEditText.setText(getString(R.string.serial_connected));
                        }
                    });
                }
            }).start();

        }

        @Override
        public void onSerialDisconnected() {
            statusEditText.setText(getString(R.string.serial_disconnected));
        }

        @Override
        public void onReturnCheckCardResult(CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
            dismissDialog();
            if (checkCardResult == CheckCardResult.NONE) {
                statusEditText.setText(getString(R.string.no_card_detected));
            } else if (checkCardResult == CheckCardResult.ICC) {
                statusEditText.setText(getString(R.string.icc_card_inserted));
                Hashtable<String, Object> data = new Hashtable<String, Object>();
                data.put("emvOption", EmvOption.START);
                data.put("checkCardMode", CheckCardMode.SWIPE_OR_INSERT);
                wisePosController.startEmv(data);
            } else if (checkCardResult == CheckCardResult.NOT_ICC) {
                statusEditText.setText(getString(R.string.card_inserted));
            } else if (checkCardResult == CheckCardResult.BAD_SWIPE) {
                statusEditText.setText(getString(R.string.bad_swipe));
            } else if (checkCardResult == CheckCardResult.MCR) {
                String formatID = decodeData.get("formatID");
                String maskedPAN = decodeData.get("maskedPAN");
                String PAN = decodeData.get("PAN");
                String expiryDate = decodeData.get("expiryDate");
                String cardHolderName = decodeData.get("cardholderName");
                String ksn = decodeData.get("ksn");
                String serviceCode = decodeData.get("serviceCode");
                String track1Length = decodeData.get("track1Length");
                String track2Length = decodeData.get("track2Length");
                String track3Length = decodeData.get("track3Length");
                String encTracks = decodeData.get("encTracks");
                String encTrack1 = decodeData.get("encTrack1");
                String encTrack2 = decodeData.get("encTrack2");
                String encTrack3 = decodeData.get("encTrack3");
                String track1Status = decodeData.get("track1Status");
                String track2Status = decodeData.get("track2Status");
                String track3Status = decodeData.get("track3Status");
                String partialTrack = decodeData.get("partialTrack");
                String productType = decodeData.get("productType");
                String finalMessage = decodeData.get("finalMessage");
                String randomNumber = decodeData.get("randomNumber");
                String encWorkingKey = decodeData.get("encWorkingKey");

                String content = getString(R.string.card_swiped);

                content += getString(R.string.format_id) + " " + formatID + "\n";
                content += getString(R.string.masked_pan) + " " + maskedPAN + "\n";
                content += getString(R.string.pan) + " " + PAN + "\n";
                content += getString(R.string.expiry_date) + " " + expiryDate + "\n";
                content += getString(R.string.cardholder_name) + " " + cardHolderName + "\n";
                content += getString(R.string.ksn) + " " + ksn + "\n";
                content += getString(R.string.service_code) + " " + serviceCode + "\n";
                content += getString(R.string.track_1_length) + " " + track1Length + "\n";
                content += getString(R.string.track_2_length) + " " + track2Length + "\n";
                content += getString(R.string.track_3_length) + " " + track3Length + "\n";
                content += getString(R.string.encrypted_tracks) + " " + encTracks + "\n";
                content += getString(R.string.encrypted_track_1) + " " + encTrack1 + "\n";
                content += getString(R.string.encrypted_track_2) + " " + encTrack2 + "\n";
                content += getString(R.string.encrypted_track_3) + " " + encTrack3 + "\n";
                content += getString(R.string.track_1_status) + " " + track1Status + "\n";
                content += getString(R.string.track_2_status) + " " + track2Status + "\n";
                content += getString(R.string.track_3_status) + " " + track3Status + "\n";
                content += getString(R.string.partial_track) + " " + partialTrack + "\n";
                content += getString(R.string.product_type) + " " + productType + "\n";
                content += getString(R.string.final_message) + " " + finalMessage + "\n";
                content += getString(R.string.random_number) + " " + randomNumber + "\n";
                content += getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";

                statusEditText.setText(content);

                if (serviceCode.endsWith("0") || serviceCode.endsWith("6")) {
                    Hashtable<String, Object> data = new Hashtable<String, Object>();
                    data.put("pinEntryTimeout", 120);
                    wisePosController.startPinEntry(data);
                }
            } else if (checkCardResult == CheckCardResult.NO_RESPONSE) {
                statusEditText.setText(getString(R.string.card_no_response));
            } else if (checkCardResult == CheckCardResult.TRACK2_ONLY) {
                String formatID = decodeData.get("formatID");
                String maskedPAN = decodeData.get("maskedPAN");
                String PAN = decodeData.get("PAN");
                String expiryDate = decodeData.get("expiryDate");
                String cardHolderName = decodeData.get("cardholderName");
                String ksn = decodeData.get("ksn");
                String serviceCode = decodeData.get("serviceCode");
                String track1Length = decodeData.get("track1Length");
                String track2Length = decodeData.get("track2Length");
                String track3Length = decodeData.get("track3Length");
                String encTracks = decodeData.get("encTracks");
                String encTrack1 = decodeData.get("encTrack1");
                String encTrack2 = decodeData.get("encTrack2");
                String encTrack3 = decodeData.get("encTrack3");
                String track1Status = decodeData.get("track1Status");
                String track2Status = decodeData.get("track2Status");
                String track3Status = decodeData.get("track3Status");
                String partialTrack = decodeData.get("partialTrack");
                String productType = decodeData.get("productType");
                String finalMessage = decodeData.get("finalMessage");
                String randomNumber = decodeData.get("randomNumber");
                String encWorkingKey = decodeData.get("encWorkingKey");

                String content = getString(R.string.card_swiped_track2_only);
                content += getString(R.string.format_id) + " " + formatID + "\n";
                content += getString(R.string.masked_pan) + " " + maskedPAN + "\n";
                content += getString(R.string.pan) + " " + PAN + "\n";
                content += getString(R.string.expiry_date) + " " + expiryDate + "\n";
                content += getString(R.string.cardholder_name) + " " + cardHolderName + "\n";
                content += getString(R.string.ksn) + " " + ksn + "\n";
                content += getString(R.string.service_code) + " " + serviceCode + "\n";
                content += getString(R.string.track_1_length) + " " + track1Length + "\n";
                content += getString(R.string.track_2_length) + " " + track2Length + "\n";
                content += getString(R.string.track_3_length) + " " + track3Length + "\n";
                content += getString(R.string.encrypted_tracks) + " " + encTracks + "\n";
                content += getString(R.string.encrypted_track_1) + " " + encTrack1 + "\n";
                content += getString(R.string.encrypted_track_2) + " " + encTrack2 + "\n";
                content += getString(R.string.encrypted_track_3) + " " + encTrack3 + "\n";
                content += getString(R.string.track_1_status) + " " + track1Status + "\n";
                content += getString(R.string.track_2_status) + " " + track2Status + "\n";
                content += getString(R.string.track_3_status) + " " + track3Status + "\n";
                content += getString(R.string.partial_track) + " " + partialTrack + "\n";
                content += getString(R.string.product_type) + " " + productType + "\n";
                content += getString(R.string.final_message) + " " + finalMessage + "\n";
                content += getString(R.string.random_number) + " " + randomNumber + "\n";
                content += getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";

                statusEditText.setText(content);

                if (serviceCode.endsWith("0") || serviceCode.endsWith("6")) {
                    Hashtable<String, Object> data = new Hashtable<String, Object>();
                    data.put("pinEntryTimeout", 120);
                    wisePosController.startPinEntry(data);
                }
            } else if (checkCardResult == CheckCardResult.USE_ICC_CARD) {
                statusEditText.setText(getString(R.string.use_icc_card));
            } else if (checkCardResult == CheckCardResult.TAP_CARD_DETECTED) {
                statusEditText.setText(getString(R.string.tap_card_detected));
            }
        }

        @Override
        public void onReturnCancelCheckCardResult(boolean isSuccess) {
            if (isSuccess) {
                statusEditText.setText(R.string.cancel_check_card_success);
            } else {
                statusEditText.setText(R.string.cancel_check_card_fail);
            }
        }

        @Override
        public void onReturnStartEmvResult(StartEmvResult startEmvResult, String ksn) {
            if (startEmvResult == StartEmvResult.SUCCESS) {
                statusEditText.setText(getString(R.string.start_emv_success));
            } else {
                statusEditText.setText(getString(R.string.start_emv_fail));
            }
        }

        @Override
        public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
            dismissDialog();
            String isSupportedTrack1 = deviceInfoData.get("isSupportedTrack1");
            String isSupportedTrack2 = deviceInfoData.get("isSupportedTrack2");
            String isSupportedTrack3 = deviceInfoData.get("isSupportedTrack3");
            String bootloaderVersion = deviceInfoData.get("bootloaderVersion");
            String firmwareVersion = deviceInfoData.get("firmwareVersion");
            String isUsbConnected = deviceInfoData.get("isUsbConnected");
            String isCharging = deviceInfoData.get("isCharging");
            String batteryLevel = deviceInfoData.get("batteryLevel");
            String batteryPercentage = deviceInfoData.get("batteryPercentage");
            String hardwareVersion = deviceInfoData.get("hardwareVersion");
            String productId = deviceInfoData.get("productId");
            String pinKsn = deviceInfoData.get("pinKsn");
            String emvKsn = deviceInfoData.get("emvKsn");
            String trackKsn = deviceInfoData.get("trackKsn");
            String csn = deviceInfoData.get("csn");
            String vendorID = deviceInfoData.get("vendorID");
            String terminalSettingVersion = deviceInfoData.get("terminalSettingVersion");
            String deviceSettingVersion = deviceInfoData.get("deviceSettingVersion");
            String formatID = deviceInfoData.get("formatID");

            String content = "";
            content += getString(R.string.format_id) + formatID + "\n";
            content += getString(R.string.bootloader_version) + bootloaderVersion + "\n";
            content += getString(R.string.firmware_version) + firmwareVersion + "\n";
            content += getString(R.string.usb) + isUsbConnected + "\n";
            content += getString(R.string.charge) + isCharging + "\n";
            content += getString(R.string.battery_level) + batteryLevel + "\n";
            content += getString(R.string.battery_percentage) + batteryPercentage + "\n";
            content += getString(R.string.hardware_version) + hardwareVersion + "\n";
            content += getString(R.string.track_1_supported) + isSupportedTrack1 + "\n";
            content += getString(R.string.track_2_supported) + isSupportedTrack2 + "\n";
            content += getString(R.string.track_3_supported) + isSupportedTrack3 + "\n";
            content += getString(R.string.product_id) + productId + "\n";
            content += getString(R.string.pin_ksn) + pinKsn + "\n";
            content += getString(R.string.emv_ksn) + emvKsn + "\n";
            content += getString(R.string.track_ksn) + trackKsn + "\n";
            content += getString(R.string.csn) + csn + "\n";
            content += getString(R.string.vendor_id) + vendorID + "\n";
            content += getString(R.string.terminal_setting_version) + terminalSettingVersion + "\n";
            content += getString(R.string.device_setting_version) + deviceSettingVersion + "\n";

            statusEditText.setText(content);
        }

        @Override
        public void onReturnTransactionResult(TransactionResult transactionResult) {
        }

        @Override
        public void onReturnTransactionResult(TransactionResult transactionResult, Hashtable<String, String> data) {
            dismissDialog();
            dialog = new Dialog(CardActivity.this);
            dialog.setContentView(R.layout.alert_dialog);
            dialog.setTitle(R.string.transaction_result);
            TextView messageTextView = (TextView) dialog.findViewById(R.id.messageTextView);

            String message = "";
            if (transactionResult == TransactionResult.APPROVED) {
                message = getString(R.string.transaction_approved) + "\n"
                        + getString(R.string.amount) + ": $" + amount + "\n";
                if (!cashbackAmount.equals("")) {
                    message += getString(R.string.cashback_amount) + ": $" + cashbackAmount;
                }
            } else if (transactionResult == TransactionResult.TERMINATED) {
                message += getString(R.string.transaction_terminated);
            } else if (transactionResult == TransactionResult.DECLINED) {
                message += getString(R.string.transaction_declined);
            } else if (transactionResult == TransactionResult.CANCEL) {
                message += getString(R.string.transaction_cancel);
            } else if (transactionResult == TransactionResult.CAPK_FAIL) {
                message += getString(R.string.transaction_capk_fail);
            } else if (transactionResult == TransactionResult.NOT_ICC) {
                message += getString(R.string.transaction_not_icc);
            } else if (transactionResult == TransactionResult.SELECT_APP_FAIL) {
                message += getString(R.string.transaction_app_fail);
            } else if (transactionResult == TransactionResult.DEVICE_ERROR) {
                message += getString(R.string.transaction_device_error);
            } else if (transactionResult == TransactionResult.APPLICATION_BLOCKED) {
                message += getString(R.string.transaction_application_blocked);
            } else if (transactionResult == TransactionResult.ICC_CARD_REMOVED) {
                messageTextView.setText(getString(R.string.transaction_icc_card_removed));
            } else if (transactionResult == TransactionResult.CARD_BLOCKED) {
                message += getString(R.string.transaction_card_blocked);
            } else if (transactionResult == TransactionResult.CARD_NOT_SUPPORTED) {
                message += getString(R.string.transaction_card_not_supported);
            } else if (transactionResult == TransactionResult.CONDITION_NOT_SATISFIED) {
                message += getString(R.string.transaction_condition_not_satisfied);
            } else if (transactionResult == TransactionResult.INVALID_ICC_DATA) {
                message += getString(R.string.transaction_invalid_icc_data);
            } else if (transactionResult == TransactionResult.MISSING_MANDATORY_DATA) {
                message += getString(R.string.transaction_missing_mandatory_data);
            } else if (transactionResult == TransactionResult.NO_EMV_APPS) {
                message += getString(R.string.transaction_no_emv_apps);
            }

            if (data.get("receiptData") != null) {
                message += "\n" + getString(R.string.receipt_data) + "" + data.get("receiptData");
            }

            messageTextView.setText(message);

            amount = "";
            cashbackAmount = "";
            amountEditText.setText("");

            dialog.findViewById(R.id.confirmButton).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismissDialog();
                }
            });

            dialog.show();
        }

        @Override
        public void onReturnBatchData(String tlv) {
            dismissDialog();
            String content = getString(R.string.batch_data) + "\n";
            Hashtable<String, String> decodeData = WisePadController.decodeTlv(tlv);
            Object[] keys = decodeData.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                if (((String) key).matches(".*[a-z].*")) {
                    continue;
                }
                String value = decodeData.get(key);
                content += key + ": " + value + "\n";
            }
            statusEditText.setText(content);
        }

        @Override
        public void onReturnTransactionLog(String tlv) {
            dismissDialog();
            String content = getString(R.string.transaction_log);
            content += tlv;
            statusEditText.setText(content);
        }

        @Override
        public void onReturnReversalData(String tlv) {
            dismissDialog();
            String content = getString(R.string.reversal_data);
            content += tlv;
            statusEditText.setText(content);
        }

        @Override
        public void onReturnAmountConfirmResult(boolean isSuccess) {
            if (isSuccess) {
                statusEditText.setText(getString(R.string.amount_confirmed));
            } else {
                statusEditText.setText(getString(R.string.amount_canceled));
            }
        }

        @Override
        public void onReturnPinEntryResult(PinEntryResult pinEntryResult, Hashtable<String, String> data) {
            String epb = data.get("epb");
            String ksn = data.get("ksn");
            String randomNumber = data.get("randomNumber");
            String encWorkingKey = data.get("encWorkingKey");

            if (pinEntryResult == PinEntryResult.ENTERED) {
                String content = getString(R.string.pin_entered);
                content += "\n" + getString(R.string.epb) + epb;
                content += "\n" + getString(R.string.ksn) + ksn;
                content += "\n" + getString(R.string.random_number) + randomNumber;
                content += "\n" + getString(R.string.encrypted_working_key) + encWorkingKey;

                statusEditText.setText(content);
            } else if (pinEntryResult == PinEntryResult.BYPASS) {
                statusEditText.setText(getString(R.string.pin_bypassed));
            } else if (pinEntryResult == PinEntryResult.CANCEL) {
                statusEditText.setText(getString(R.string.pin_canceled));
            } else if (pinEntryResult == PinEntryResult.TIMEOUT) {
                statusEditText.setText(getString(R.string.pin_timeout));
            } else if (pinEntryResult == PinEntryResult.KEY_ERROR) {
                statusEditText.setText(getString(R.string.key_error));
            } else if (pinEntryResult == PinEntryResult.NO_PIN) {
                statusEditText.setText(getString(R.string.no_pin));
            }
        }

        @Override
        public void onReturnPrintResult(PrintResult printerResult) {
            if (printerResult == PrintResult.SUCCESS) {
                statusEditText.setText(getString(R.string.printer_command_success));
            } else if (printerResult == PrintResult.NO_PAPER) {
                statusEditText.setText(getString(R.string.no_paper));
            } else if (printerResult == PrintResult.WRONG_CMD) {
                statusEditText.setText(getString(R.string.wrong_printer_cmd));
            }
        }

        @Override
        public void onReturnAmount(Hashtable<String, String> data) {
            String amount = data.get("amount");
            String cashbackAmount = data.get("cashbackAmount");
            String currencyCode = data.get("currencyCode");

            String text = "";
            text += getString(R.string.amount_with_colon) + amount + "\n";
            text += getString(R.string.cashback_with_colon) + cashbackAmount + "\n";
            text += getString(R.string.currency_with_colon) + currencyCode + "\n";

            statusEditText.setText(text);
        }

        @Override
        public void onReturnUpdateTerminalSettingResult(TerminalSettingStatus terminalSettingStatus) {
            dismissDialog();
            if (terminalSettingStatus == TerminalSettingStatus.SUCCESS) {
                statusEditText.setText(getString(R.string.update_terminal_setting_success));
            } else if (terminalSettingStatus == TerminalSettingStatus.TAG_NOT_FOUND) {
                statusEditText.setText(getString(R.string.update_terminal_setting_tag_not_found));
            } else if (terminalSettingStatus == TerminalSettingStatus.LENGTH_INCORRECT) {
                statusEditText.setText(getString(R.string.update_terminal_setting_length_incorrect));
            } else if (terminalSettingStatus == TerminalSettingStatus.TLV_INCORRECT) {
                statusEditText.setText(getString(R.string.update_terminal_setting_tlv_incorrect));
            } else if (terminalSettingStatus == TerminalSettingStatus.BOOTLOADER_NOT_SUPPORT) {
                statusEditText.setText(getString(R.string.update_terminal_setting_bootloader_not_support));
            }
        }

        @Override
        public void onReturnReadTerminalSettingResult(TerminalSettingStatus terminalSettingStatus, String value) {
            dismissDialog();
            if (terminalSettingStatus == TerminalSettingStatus.SUCCESS) {
                statusEditText.setText(getString(R.string.read_terminal_setting_success) + "\n" + getString(R.string.value) + " " + value);
            } else if (terminalSettingStatus == TerminalSettingStatus.TAG_NOT_FOUND) {
                statusEditText.setText(getString(R.string.read_terminal_setting_tag_not_found));
            } else if (terminalSettingStatus == TerminalSettingStatus.TAG_INCORRECT) {
                statusEditText.setText(getString(R.string.read_terminal_setting_tag_incorrect));
            }
        }

        @Override
        public void onReturnEnableInputAmountResult(boolean isSuccess) {
            if (isSuccess) {
                statusEditText.setText(getString(R.string.enable_input_amount_success));
            } else {
                statusEditText.setText(getString(R.string.enable_input_amount_fail));
            }
        }

        @Override
        public void onReturnDisableInputAmountResult(boolean isSuccess) {
            if (isSuccess) {
                statusEditText.setText(getString(R.string.disable_input_amount_success));
            } else {
                statusEditText.setText(getString(R.string.disable_input_amount_fail));
            }
        }

        @Override
        public void onReturnPhoneNumber(PhoneEntryResult phoneEntryResult, String phoneNumber) {
            if (phoneEntryResult == PhoneEntryResult.ENTERED) {
                statusEditText.setText(getString(R.string.phone_number) + " " + phoneNumber);
            } else if (phoneEntryResult == PhoneEntryResult.TIMEOUT) {
                statusEditText.setText(getString(R.string.timeout));
            } else if (phoneEntryResult == PhoneEntryResult.CANCEL) {
                statusEditText.setText(getString(R.string.canceled));
            } else if (phoneEntryResult == PhoneEntryResult.WRONG_LENGTH) {
                statusEditText.setText(getString(R.string.wrong_length));
            } else if (phoneEntryResult == PhoneEntryResult.BYPASS) {
                statusEditText.setText(getString(R.string.bypass));
            }
        }

        @Override
        public void onReturnEmvCardBalance(boolean isSuccess, String tlv) {
            if (isSuccess) {
                statusEditText.setText(getString(R.string.emv_card_balance_result) + tlv);
            } else {
                statusEditText.setText(getString(R.string.emv_card_balance_failed));
            }
        }

        @Override
        public void onReturnEmvCardDataResult(boolean isSuccess, String tlv) {
            if (isSuccess) {
                statusEditText.setText(getString(R.string.emv_card_data_result) + tlv);
            } else {
                statusEditText.setText(getString(R.string.emv_card_data_failed));
            }
        }

        @Override
        public void onReturnEmvCardNumber(String cardNumber) {
            statusEditText.setText(getString(R.string.pan) + cardNumber);
        }

        @Override
        public void onReturnEmvTransactionLog(String[] transactionLogs) {
            String content = getString(R.string.transaction_log) + "\n";
            for (int i = 0; i < transactionLogs.length; ++i) {
                content += (i + 1) + ": " + transactionLogs[i] + "\n";
            }
            statusEditText.setText(content);
        }

        @Override
        public void onReturnEmvLoadLog(String[] loadLogs) {
            String content = getString(R.string.load_log) + "\n";
            for (int i = 0; i < loadLogs.length; ++i) {
                content += (i + 1) + ": " + loadLogs[i] + "\n";
            }
            statusEditText.setText(content);
        }

        @Override
        public void onReturnEncryptDataResult(boolean isSuccess, Hashtable<String, String> data) {
            if (isSuccess) {
                String content = "";
                if (data.containsKey("ksn")) {
                    content += getString(R.string.ksn) + data.get("ksn") + "\n";
                }
                if (data.containsKey("randomNumber")) {
                    content += getString(R.string.random_number) + data.get("randomNumber") + "\n";
                }
                if (data.containsKey("encData")) {
                    content += getString(R.string.encrypted_data) + data.get("encData") + "\n";
                }
                if (data.containsKey("mac")) {
                    content += getString(R.string.mac) + data.get("mac") + "\n";
                }
                statusEditText.setText(content);
            } else {
                statusEditText.setText(getString(R.string.encrypt_data_failed));
            }
        }

        @Override
        public void onReturnInjectSessionKeyResult(boolean isSuccess) {
            if (isSuccess) {
                statusEditText.setText(getString(R.string.inject_session_key_success));
                injectNextSessionKey();
            } else {
                statusEditText.setText(getString(R.string.inject_session_key_failed));
            }
        }

        @Override
        public void onReturnViposBatchExchangeApduResult(Hashtable<Integer, String> data) {
            Object[] keys = data.keySet().toArray();
            Arrays.sort(keys);
            String content = getString(R.string.apdu_result) + "\n";
            for (int i = 0; i < keys.length; ++i) {
                content += keys[i] + ": " + data.get(keys[i]) + "\n";
            }
            statusEditText.setText(content);
        }

        @Override
        public void onReturnViposExchangeApduResult(String apdu) {
            statusEditText.setText(getString(R.string.apdu_result) + apdu);
        }

        @Override
        public void onReturnPowerOnIccResult(boolean isSuccess, String ksn, String atr, int atrLength) {
        }

        @Override
        public void onReturnPowerOffIccResult(boolean isSuccess) {
        }

        @Override
        public void onReturnApduResult(boolean isSuccess, String apdu, int apduLength) {
        }

        @Override
        public void onReturnApduResultWithPkcs7Padding(boolean isSuccess, String apdu) {
        }

        @Override
        public void onRequestSelectApplication(ArrayList<String> appList) {
            dismissDialog();

            dialog = new Dialog(CardActivity.this);
            dialog.setContentView(R.layout.application_dialog);
            dialog.setTitle(R.string.please_select_app);

            String[] appNameList = new String[appList.size()];
            for (int i = 0; i < appNameList.length; ++i) {
                appNameList[i] = appList.get(i);
            }

            appListView = (ListView) dialog.findViewById(R.id.appList);
            appListView.setAdapter(new ArrayAdapter<String>(CardActivity.this, android.R.layout.simple_list_item_1, appNameList));
            appListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    wisePosController.selectApplication(position);
                    dismissDialog();
                }

            });

            dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    wisePosController.cancelSelectApplication();
                    dismissDialog();
                }
            });
            dialog.show();
        }

        @Override
        public void onRequestSetAmount() {
            promptForAmount();
        }

        @Override
        public void onRequestPinEntry(PinEntrySource pinEntrySource) {
            dismissDialog();
            statusEditText.setText(getString(R.string.enter_pin));
        }

        @Override
        public void onRequestVerifyID(String tlv) {
            dismissDialog();

            dialog = new Dialog(CardActivity.this);
            dialog.setContentView(R.layout.verify_id_dialog);
            dialog.setTitle(R.string.verify_id);

            String content = "";
            try {
                List<TLV> tlvList = TLVParser.parse(tlv);
                TLV cardholderCertificateTLV = TLVParser.searchTLV(tlvList, "9F61");
                TLV certificateTypeTLV = TLVParser.searchTLV(tlvList, "9F62");

                if (cardholderCertificateTLV != null) {
                    content += "\n" + getString(R.string.cardholder_certificate) + " " + new String(cardholderCertificateTLV.value);
                }

                if (certificateTypeTLV != null) {
                    content += "\n" + getString(R.string.certificate_type) + " " + certificateTypeTLV.value;
                }
            } catch (Exception e) {
            }

            ((TextView) dialog.findViewById(R.id.messageTextView)).setText(content);

            dialog.findViewById(R.id.successButton).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    wisePosController.sendVerifyIDResult(true);
                    dismissDialog();
                }
            });

            dialog.findViewById(R.id.failButton).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    wisePosController.sendVerifyIDResult(true);
                    dismissDialog();
                }
            });

            dialog.show();
        }

        @Override
        public void onRequestCheckServerConnectivity() {
            dismissDialog();
            dialog = new Dialog(CardActivity.this);
            dialog.setContentView(R.layout.alert_dialog);
            dialog.setTitle(R.string.online_process_requested);

            ((TextView) dialog.findViewById(R.id.messageTextView)).setText(R.string.replied_connected);

            dialog.findViewById(R.id.confirmButton).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    wisePosController.sendServerConnectivity(true);
                    dismissDialog();
                }
            });

            dialog.show();
        }

        @Override
        public void onRequestOnlineProcess(String tlv) {
            String content = getString(R.string.request_data_to_server) + "\n";
            Hashtable<String, String> decodeData = WisePadController.decodeTlv(tlv);
            Object[] keys = decodeData.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                if (((String) key).matches(".*[a-z].*")) {
                    continue;
                }
                String value = decodeData.get(key);
                content += key + ": " + value + "\n";
            }
            statusEditText.setText(content);

            dismissDialog();
            dialog = new Dialog(CardActivity.this);
            dialog.setContentView(R.layout.alert_dialog);
            dialog.setTitle(R.string.request_data_to_server);

            if (isPinCanceled) {
                ((TextView) dialog.findViewById(R.id.messageTextView)).setText(R.string.replied_failed);
            } else {
                ((TextView) dialog.findViewById(R.id.messageTextView)).setText(R.string.replied_success);
            }

            dialog.findViewById(R.id.confirmButton).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isPinCanceled) {
                        wisePosController.sendOnlineProcessResult(null);
                    } else {
                        wisePosController.sendOnlineProcessResult("8A023030");
                    }
                    dismissDialog();
                }
            });

            dialog.show();
        }

        @Override
        public void onRequestTerminalTime() {
            dismissDialog();
            String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
            wisePosController.sendTerminalTime(terminalTime);
            Log.i("BBPOS", "time " + terminalTime);
            statusEditText.setText(getString(R.string.request_terminal_time) + " " + terminalTime);
            Intent intent = new Intent(context, HomeActivity.class);
            startActivity(intent);
        }

        @Override
        public void onRequestDisplayAsterisk(int numOfAsterisk) {
            String content = getString(R.string.pin) + ": ";
            for (int i = 0; i < numOfAsterisk; ++i) {
                content += "*";
            }
            statusEditText.setText(content);
        }

        @Override
        public void onRequestDisplayText(DisplayText displayText) {
            dismissDialog();

            String msg = "";
            if (displayText == DisplayText.AMOUNT_OK_OR_NOT) {
                msg = getString(R.string.amount_ok);
            } else if (displayText == DisplayText.APPROVED) {
                msg = getString(R.string.approved);
            } else if (displayText == DisplayText.CALL_YOUR_BANK) {
                msg = getString(R.string.call_your_bank);
            } else if (displayText == DisplayText.CANCEL_OR_ENTER) {
                msg = getString(R.string.cancel_or_enter);
            } else if (displayText == DisplayText.CARD_ERROR) {
                msg = getString(R.string.card_error);
            } else if (displayText == DisplayText.DECLINED) {
                msg = getString(R.string.decline);
            } else if (displayText == DisplayText.ENTER_PIN) {
                msg = getString(R.string.enter_pin);
            } else if (displayText == DisplayText.INCORRECT_PIN) {
                msg = getString(R.string.incorrect_pin);
            } else if (displayText == DisplayText.INSERT_CARD) {
                msg = getString(R.string.insert_card);
            } else if (displayText == DisplayText.NOT_ACCEPTED) {
                msg = getString(R.string.not_accepted);
            } else if (displayText == DisplayText.PIN_OK) {
                msg = getString(R.string.pin_ok);
            } else if (displayText == DisplayText.PLEASE_WAIT) {
                msg = getString(R.string.wait);
            } else if (displayText == DisplayText.PROCESSING_ERROR) {
                msg = getString(R.string.processing_error);
            } else if (displayText == DisplayText.REMOVE_CARD) {
                msg = getString(R.string.remove_card);
            } else if (displayText == DisplayText.USE_CHIP_READER) {
                msg = getString(R.string.use_chip_reader);
            } else if (displayText == DisplayText.USE_MAG_STRIPE) {
                msg = getString(R.string.use_mag_stripe);
            } else if (displayText == DisplayText.TRY_AGAIN) {
                msg = getString(R.string.try_again);
            } else if (displayText == DisplayText.REFER_TO_YOUR_PAYMENT_DEVICE) {
                msg = getString(R.string.refer_payment_device);
            } else if (displayText == DisplayText.TRANSACTION_TERMINATED) {
                msg = getString(R.string.transaction_terminated);
            } else if (displayText == DisplayText.TRY_ANOTHER_INTERFACE) {
                msg = getString(R.string.try_another_interface);
            } else if (displayText == DisplayText.ONLINE_REQUIRED) {
                msg = getString(R.string.online_required);
            } else if (displayText == DisplayText.PROCESSING) {
                msg = getString(R.string.processing);
            } else if (displayText == DisplayText.WELCOME) {
                msg = getString(R.string.welcome);
            } else if (displayText == DisplayText.PRESENT_ONLY_ONE_CARD) {
                msg = getString(R.string.present_one_card);
            } else if (displayText == DisplayText.CAPK_LOADING_FAILED) {
                msg = getString(R.string.capk_failed);
            } else if (displayText == DisplayText.LAST_PIN_TRY) {
                msg = getString(R.string.last_pin_try);
            } else if (displayText == DisplayText.SELECT_ACCOUNT) {
                msg = getString(R.string.select_account);
            } else if (displayText == DisplayText.ENTER_AMOUNT) {
                msg = getString(R.string.enter_amount);
            } else if (displayText == DisplayText.INSERT_OR_TAP_CARD) {
                msg = getString(R.string.insert_or_tap_card);
            } else if (displayText == DisplayText.APPROVED_PLEASE_SIGN) {
                msg = getString(R.string.approved_please_sign);
            } else if (displayText == DisplayText.TAP_CARD_AGAIN) {
                msg = getString(R.string.tap_card_again);
            } else if (displayText == DisplayText.AUTHORISING) {
                msg = getString(R.string.authorising);
            } else if (displayText == DisplayText.INSERT_OR_SWIPE_CARD_OR_TAP_ANOTHER_CARD) {
                msg = getString(R.string.insert_or_swipe_card_or_tap_another_card);
            } else if (displayText == DisplayText.INSERT_OR_SWIPE_CARD) {
                msg = getString(R.string.insert_or_swipe_card);
            } else if (displayText == DisplayText.MULTIPLE_CARDS_DETECTED) {
                msg = getString(R.string.multiple_cards_detected);
            }

            statusEditText.setText(msg);
        }

        @Override
        public void onRequestClearDisplay() {
            dismissDialog();
            statusEditText.setText("");
        }

        @Override
        public void onRequestReferProcess(String pan) {
            dismissDialog();
            dialog = new Dialog(CardActivity.this);
            dialog.setContentView(R.layout.refer_process_dialog);
            dialog.setTitle(getString(R.string.call_your_bank));

            dialog.findViewById(R.id.approvedButton).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    wisePosController.sendReferProcessResult(ReferralResult.APPROVED);
                }
            });

            dialog.findViewById(R.id.declinedButton).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    wisePosController.sendReferProcessResult(ReferralResult.DECLINED);
                }
            });

            dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    wisePosController.cancelReferProcess();
                }
            });
        }

        @Override
        public void onRequestAdviceProcess(String tlv) {
            dismissDialog();
            statusEditText.setText(getString(R.string.advice_process));
        }

        @Override
        public void onRequestFinalConfirm() {
            dismissDialog();
            if (!isPinCanceled) {
                dialog = new Dialog(CardActivity.this);
                dialog.setContentView(R.layout.confirm_dialog);
                dialog.setTitle(getString(R.string.confirm_amount));

                String message = getString(R.string.amount) + ": $" + amount;
                if (!cashbackAmount.equals("")) {
                    message += "\n" + getString(R.string.cashback_amount) + ": $" + cashbackAmount;
                }

                ((TextView) dialog.findViewById(R.id.messageTextView)).setText(message);

                dialog.findViewById(R.id.confirmButton).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wisePosController.sendFinalConfirmResult(true);
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wisePosController.sendFinalConfirmResult(false);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            } else {
                wisePosController.sendFinalConfirmResult(false);
            }
        }

        @Override
        public void onRequestInsertCard() {
            statusEditText.setText(getString(R.string.insert_card));
        }

        @Override
        public void onRequestPrintData(int index, boolean isReprint) {
            wisePosController.sendPrintData(receipts.get(index));
            if (isReprint) {
                statusEditText.setText(getString(R.string.request_reprint_data) + index);
            } else {
                statusEditText.setText(getString(R.string.request_printer_data) + index);
            }
        }

        @Override
        public void onPrintDataCancelled() {
            dismissDialog();
            statusEditText.setText(getString(R.string.printer_operation_cancelled));
        }

        @Override
        public void onPrintDataEnd() {
            statusEditText.setText(getString(R.string.printer_operation_end));

            amount = "";
            cashbackAmount = "";
            amountEditText.setText("");
        }

        @Override
        public void onBatteryLow(BatteryStatus batteryStatus) {
            if (batteryStatus == BatteryStatus.LOW) {
                statusEditText.setText(getString(R.string.battery_low));
            } else if (batteryStatus == BatteryStatus.CRITICALLY_LOW) {
                statusEditText.setText(getString(R.string.battery_critically_low));
            }
        }

        @Override
        public void onBTv2DeviceNotFound() {
            dismissDialog();
            statusEditText.setText(getString(R.string.no_device_detected));
        }

        @Override
        public void onAudioDeviceNotFound() {
            statusEditText.setText(getString(R.string.no_device_detected));
        }

        @Override
        public void onDevicePlugged() {
            statusEditText.setText(getString(R.string.device_plugged));
        }

        @Override
        public void onDeviceUnplugged() {
            statusEditText.setText(getString(R.string.device_unplugged));
        }

        @Override
        public void onError(Error errorState, String errorMessage) {
            //dismissDialog();
            amountEditText.setText("");
            if (errorState == Error.CMD_NOT_AVAILABLE) {
                statusEditText.setText(getString(R.string.command_not_available));
            } else if (errorState == Error.TIMEOUT) {
                statusEditText.setText(getString(R.string.device_no_response));
            } else if (errorState == Error.DEVICE_RESET) {
                statusEditText.setText(getString(R.string.device_reset));
            } else if (errorState == Error.UNKNOWN) {
                statusEditText.setText(getString(R.string.unknown_error));
            } else if (errorState == Error.DEVICE_BUSY) {
                statusEditText.setText(getString(R.string.device_busy));
            } else if (errorState == Error.INPUT_OUT_OF_RANGE) {
                statusEditText.setText(getString(R.string.out_of_range));
            } else if (errorState == Error.INPUT_INVALID_FORMAT) {
                statusEditText.setText(getString(R.string.invalid_format));
                Toast.makeText(CardActivity.this, getString(R.string.invalid_format), Toast.LENGTH_LONG).show();
            } else if (errorState == Error.INPUT_ZERO_VALUES) {
                statusEditText.setText(getString(R.string.zero_values));
            } else if (errorState == Error.INPUT_INVALID) {
                statusEditText.setText(getString(R.string.input_invalid));
                Toast.makeText(CardActivity.this, getString(R.string.input_invalid), Toast.LENGTH_LONG).show();
            } else if (errorState == Error.CASHBACK_NOT_SUPPORTED) {
                statusEditText.setText(getString(R.string.cashback_not_supported));
                Toast.makeText(CardActivity.this, getString(R.string.cashback_not_supported), Toast.LENGTH_LONG).show();
            } else if (errorState == Error.CRC_ERROR) {
                statusEditText.setText(getString(R.string.crc_error));
            } else if (errorState == Error.COMM_ERROR) {
                statusEditText.setText(getString(R.string.comm_error));
            } else if (errorState == Error.FAIL_TO_START_BTV2) {
                statusEditText.setText(getString(R.string.fail_to_start_bluetooth_v2));
            } else if (errorState == Error.FAIL_TO_START_BTV4) {
                statusEditText.setText(getString(R.string.fail_to_start_bluetooth_v4));
            } else if (errorState == Error.FAIL_TO_START_AUDIO) {
                statusEditText.setText(getString(R.string.fail_to_start_audio));
            } else if (errorState == Error.INVALID_FUNCTION_IN_CURRENT_MODE) {
                statusEditText.setText(getString(R.string.invalid_function));
            } else if (errorState == Error.COMM_LINK_UNINITIALIZED) {
                statusEditText.setText(getString(R.string.comm_link_uninitialized));
            } else if (errorState == Error.BTV2_ALREADY_STARTED) {
                statusEditText.setText(getString(R.string.bluetooth_2_already_started));
            } else if (errorState == Error.BTV4_ALREADY_STARTED) {
                statusEditText.setText(getString(R.string.bluetooth_4_already_started));
            } else if (errorState == Error.BTV4_NOT_SUPPORTED) {
                statusEditText.setText(getString(R.string.bluetooth_4_not_supported));
                Toast.makeText(CardActivity.this, getString(R.string.bluetooth_4_not_supported), Toast.LENGTH_LONG).show();
            } else if (errorState == Error.FAIL_TO_START_SERIAL) {
                statusEditText.setText(getString(R.string.fail_to_start_serial));
            }
        }
    }

    class MyBarcodeReaderControllerListener implements BarcodeReaderControllerListener {

        @Override
        public void onBarcodeReaderConnected() {
            barcodeReaderController.getBarcode();
        }

        @Override
        public void onBarcodeReaderDisconnected() {
        }

        @Override
        public void onReturnBarcode(String barcode) {
            statusEditText.setText(getString(R.string.barcode) + barcode);
            barcodeReaderController.stopBarCodeReader();
        }

    }

    class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            statusEditText.setText("");

            if (v == startButton) {
                promptForCheckCard();
            }
        }
    }
}
