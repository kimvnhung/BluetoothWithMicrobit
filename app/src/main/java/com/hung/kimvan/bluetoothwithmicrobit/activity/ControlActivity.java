package com.hung.kimvan.bluetoothwithmicrobit.activity;

import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hung.kimvan.bluetoothwithmicrobit.Fragment.AdvanceFragment;
import com.hung.kimvan.bluetoothwithmicrobit.Fragment.BasicFragment;
import com.hung.kimvan.bluetoothwithmicrobit.Fragment.SettingFragment;
import com.hung.kimvan.bluetoothwithmicrobit.bluetooth.BleAdapterService;
import com.hung.kimvan.bluetoothwithmicrobit.bluetooth.ConnectionStatusListener;
import com.hung.kimvan.bluetoothwithmicrobit.help.Constants;
import com.hung.kimvan.bluetoothwithmicrobit.help.DeviceListAdapter;
import com.hung.kimvan.bluetoothwithmicrobit.R;
import com.hung.kimvan.bluetoothwithmicrobit.help.MicroBit;
import com.hung.kimvan.bluetoothwithmicrobit.help.SelectionPagerAdapter;
import com.hung.kimvan.bluetoothwithmicrobit.help.Utility;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ControlActivity extends AppCompatActivity implements ConnectionStatusListener,
        BasicFragment.BasicFragmentListener, TabLayout.OnTabSelectedListener, AdvanceFragment.AdvancedListener {
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_ID = "extra_id";

    public  static final String TAG = "BluetoothWithMicrobit";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private RelativeLayout statusLayout;

    private TextView status;


    private boolean exiting=false;
    private boolean indications_on=false;

    private BleAdapterService bluetooth_le_adapter;


    private SharedPreferences prefs;



    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetooth_le_adapter = ((BleAdapterService.LocalBinder) service).getService();
            bluetooth_le_adapter.setActivityHandler(mMessageHandler);
            connectToDevice();


            if (bluetooth_le_adapter.setIndicationsState(Utility.normaliseUUID(BleAdapterService.UARTSERVICE_SERVICE_UUID),
                    Utility.normaliseUUID(BleAdapterService.UART_TX_CHARACTERISTIC_UUID), true)) {
                showMsg(Utility.htmlColorGreen("UART TX indications ON"));
            } else {
                showMsg(Utility.htmlColorRed("Failed to set UART TX indications ON"));
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetooth_le_adapter = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_control);
        init();
        bluetoothConnection();

    }

    private void bluetoothConnection() {
        // read intent data
        final Intent intent = getIntent();
        MicroBit.getInstance().setMicrobit_name(intent.getStringExtra(EXTRA_NAME));
        MicroBit.getInstance().setMicrobit_address(intent.getStringExtra(EXTRA_ID));

        MicroBit.getInstance().setConnection_status_listener(this);

        // connect to the Bluetooth service
        Intent gattServiceIntent = new Intent(this, BleAdapterService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void init() {

        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);

        statusLayout = findViewById(R.id.status_layout);
        status = findViewById(R.id.message);

        this.prefs = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
    }

    private void connectToDevice() {
        showMsg(Utility.htmlColorBlue("Connecting to micro:bit"));
        if (bluetooth_le_adapter.connect(MicroBit.getInstance().getMicrobit_address())) {
        } else {
            showMsg(Utility.htmlColorRed("onConnect: failed to connect"));
        }
    }


    private void setupViewPager(ViewPager vPager) {
            SelectionPagerAdapter adapter = new SelectionPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new BasicFragment(),"Cơ bản");
            adapter.addFragment(new AdvanceFragment(),"Nâng cao");
            adapter.addFragment(new SettingFragment(),"Cài đặt");
            vPager.setAdapter(adapter);
    }

    private void showMsg(final String msg) {
        Log.d(Constants.TAG, msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText(Html.fromHtml(msg));
            }
        });

    }


    // Service message handler�//////////////////
    private Handler mMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            String service_uuid = "";
            String characteristic_uuid = "";

            String descriptor_uuid = "";
            byte[] b = null;
            TextView value_text = null;

            switch (msg.what) {
                case BleAdapterService.GATT_CONNECTED:
                    showMsg(Utility.htmlColorGreen("Connected"));
                    showMsg(Utility.htmlColorGreen("Discovering services..."));
                    bluetooth_le_adapter.discoverServices();
                    break;
                case BleAdapterService.GATT_DISCONNECT:
                    showMsg(Utility.htmlColorRed("Disconnected"));
                    break;
                case BleAdapterService.GATT_SERVICES_DISCOVERED:
                    Log.d(Constants.TAG, "XXXX Services discovered");
                    showMsg(Utility.htmlColorGreen("Ready"));
                    //((LinearLayout) ControlActivity.this.findViewById(R.id.menu_items_area)).setVisibility(View.VISIBLE);
                    List<BluetoothGattService> slist = bluetooth_le_adapter.getSupportedGattServices();
                    for (BluetoothGattService svc : slist) {
                        Log.d(Constants.TAG, "UUID=" + svc.getUuid().toString().toUpperCase() + " INSTANCE=" + svc.getInstanceId());
                        MicroBit.getInstance().addService(svc);
                    }
                    MicroBit.getInstance().setMicrobit_services_discovered(true);
                    break;
                case BleAdapterService.NOTIFICATION_OR_INDICATION_RECEIVED:
                    bundle = msg.getData();
                    service_uuid = bundle.getString(BleAdapterService.PARCEL_SERVICE_UUID);
                    characteristic_uuid = bundle.getString(BleAdapterService.PARCEL_CHARACTERISTIC_UUID);
                    b = bundle.getByteArray(BleAdapterService.PARCEL_VALUE);
                    Log.d(Constants.TAG, "Value=" + Utility.byteArrayAsHexString(b));
                    if (characteristic_uuid.equalsIgnoreCase((Utility.normaliseUUID(BleAdapterService.UART_TX_CHARACTERISTIC_UUID)))) {
                        String ascii="";
                        Log.d(Constants.TAG, "UART TX received");
                        try {
                            ascii = new String(b,"US-ASCII");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            showMsg(Utility.htmlColorGreen("Could not convert TX data to ASCII"));
                            return;
                        }
                        Log.d(Constants.TAG, "micro:bit answer: " + ascii);
                        if (!ascii.equals(Constants.AVM_CORRECT_RESPONSE)) {
                            showAnswer(ascii);
                        } else {
                            showAnswer(ascii+" You only needed "+0+" guesses!");
                        }
                    }
                    break;
            }
        }
    };


    // Service message handler�//////////////////
    private Handler mMessageHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle;
            String service_uuid = "";
            String characteristic_uuid = "";
            String descriptor_uuid = "";
            byte[] b = null;
            TextView value_text = null;

            switch (msg.what) {
                case BleAdapterService.GATT_CHARACTERISTIC_WRITTEN:
                    Log.d(Constants.TAG, "Handler received characteristic written result");
                    bundle = msg.getData();
                    service_uuid = bundle.getString(BleAdapterService.PARCEL_SERVICE_UUID);
                    characteristic_uuid = bundle.getString(BleAdapterService.PARCEL_CHARACTERISTIC_UUID);
                    Log.d(Constants.TAG, "characteristic " + characteristic_uuid + " of service " + service_uuid + " written OK");
                    showMsg(Utility.htmlColorGreen("Ready"));
                    break;
                case BleAdapterService.GATT_DESCRIPTOR_WRITTEN:
                    Log.d(Constants.TAG, "Handler received descriptor written result");
                    bundle = msg.getData();
                    service_uuid = bundle.getString(BleAdapterService.PARCEL_SERVICE_UUID);
                    characteristic_uuid = bundle.getString(BleAdapterService.PARCEL_CHARACTERISTIC_UUID);
                    descriptor_uuid = bundle.getString(BleAdapterService.PARCEL_DESCRIPTOR_UUID);
                    Log.d(Constants.TAG, "descriptor " + descriptor_uuid + " of characteristic " + characteristic_uuid + " of service " + service_uuid + " written OK");
                    if (!exiting) {
                        showMsg(Utility.htmlColorGreen("UART TX indications ON"));
                        indications_on=true;
                    } else {
                        showMsg(Utility.htmlColorGreen("UART TX indications OFF"));
                        indications_on=false;
                        finish();
                    }
                    break;

                case BleAdapterService.NOTIFICATION_OR_INDICATION_RECEIVED:
                    bundle = msg.getData();
                    service_uuid = bundle.getString(BleAdapterService.PARCEL_SERVICE_UUID);
                    characteristic_uuid = bundle.getString(BleAdapterService.PARCEL_CHARACTERISTIC_UUID);
                    b = bundle.getByteArray(BleAdapterService.PARCEL_VALUE);
                    Log.d(Constants.TAG, "Value=" + Utility.byteArrayAsHexString(b));
                    if (characteristic_uuid.equalsIgnoreCase((Utility.normaliseUUID(BleAdapterService.UART_TX_CHARACTERISTIC_UUID)))) {
                        String ascii="";
                        Log.d(Constants.TAG, "UART TX received");
                        try {
                            ascii = new String(b,"US-ASCII");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            showMsg(Utility.htmlColorGreen("Could not convert TX data to ASCII"));
                            return;
                        }
                        Log.d(Constants.TAG, "micro:bit answer: " + ascii);
                        if (!ascii.equals(Constants.AVM_CORRECT_RESPONSE)) {
                            showAnswer(ascii);
                        } else {
                            showAnswer(ascii+" You only needed "+0+" guesses!");
                        }
                    }
                    break;
                case BleAdapterService.MESSAGE:
                    bundle = msg.getData();
                    String text = bundle.getString(BleAdapterService.PARCEL_TEXT);
                    showMsg(Utility.htmlColorRed(text));
                    break;
                case BleAdapterService.GATT_SERVICES_DISCOVERED:
                    Log.d(Constants.TAG, "XXXX Services discovered");
                    showMsg(Utility.htmlColorGreen("Ready"));
                    //((LinearLayout) ControlActivity.this.findViewById(R.id.menu_items_area)).setVisibility(View.VISIBLE);
                    List<BluetoothGattService> slist = bluetooth_le_adapter.getSupportedGattServices();
                    for (BluetoothGattService svc : slist) {
                        Log.d(Constants.TAG, "UUID=" + svc.getUuid().toString().toUpperCase() + " INSTANCE=" + svc.getInstanceId());
                        MicroBit.getInstance().addService(svc);
                    }
                    MicroBit.getInstance().setMicrobit_services_discovered(true);
                    break;
            }
        }
    };

    private void showAnswer(String answer) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Answer");
        builder.setMessage(answer);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    @Override
    public void connectionStatusChanged(boolean new_state) {
        if (new_state) {
            showMsg(Utility.htmlColorGreen("Connected"));
        } else {
            showMsg(Utility.htmlColorRed("Disconnected"));
        }
    }

    @Override
    public void serviceDiscoveryStatusChanged(boolean new_state) {

    }

    private void refreshBluetoothServices() {
        if (MicroBit.getInstance().isMicrobit_connected()) {
            Toast toast = Toast.makeText(this, "Refreshing GATT services", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            MicroBit.getInstance().resetAttributeTables();
            bluetooth_le_adapter.refreshDeviceCache();
            bluetooth_le_adapter.discoverServices();
        } else {
            Toast toast = Toast.makeText(this, "Request Ignored - Not Connected", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(Constants.TAG, "onBackPressed");
        if (MicroBit.getInstance().isMicrobit_connected()) {
            try {
                bluetooth_le_adapter.disconnect();
                // may already have unbound. No API to check state so....
                unbindService(mServiceConnection);
            } catch (Exception e) {
            }
        }
        if (MicroBit.getInstance().isMicrobit_connected() && indications_on) {
            exiting = true;
            bluetooth_le_adapter.setIndicationsState(Utility.normaliseUUID(BleAdapterService.UARTSERVICE_SERVICE_UUID), Utility.normaliseUUID(BleAdapterService.UART_TX_CHARACTERISTIC_UUID), false);
        }
        exiting=true;
        if (!MicroBit.getInstance().isMicrobit_connected()) {
            try {
                // may already have unbound. No API to check state so....
                unbindService(mServiceConnection);
            } catch (Exception e) {
            }
        }
        finish();
        exiting=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (indications_on) {
            exiting = true;
            bluetooth_le_adapter.setIndicationsState(Utility.normaliseUUID(BleAdapterService.UARTSERVICE_SERVICE_UUID), Utility.normaliseUUID(BleAdapterService.UART_TX_CHARACTERISTIC_UUID), false);
        }
        try {
            // may already have unbound. No API to check state so....
            unbindService(mServiceConnection);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MicroBit.getInstance().isMicrobit_connected()) {
            showMsg(Utility.htmlColorGreen("Connected"));
        } else {
            showMsg(Utility.htmlColorRed("Disconnected"));
        }
    }

    @Override
    public void sendContent(String content) {
        try {
            String question = content + ":";
            byte[] ascii_bytes = question.getBytes("US-ASCII");
            Log.d(Constants.TAG, "ASCII bytes: 0x" + Utility.byteArrayAsHexString(ascii_bytes));
            bluetooth_le_adapter.writeCharacteristic(Utility.normaliseUUID(BleAdapterService.UARTSERVICE_SERVICE_UUID),
                    Utility.normaliseUUID(BleAdapterService.UART_RX_CHARACTERISTIC_UUID), ascii_bytes);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            showMsg("Unable to convert text to ASCII bytes");
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.parent == tabLayout){
            if (tabLayout.getSelectedTabPosition() == 2) {
                statusLayout.setVisibility(View.INVISIBLE);
            }else {
                statusLayout.setVisibility(View.VISIBLE);
            }


        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onForward() {
        Log.d(Constants.TAG,"onForward()");
        sendContent(prefs.getString(getString(R.string.forward_pref),"0"));
    }

    @Override
    public void onBackward() {
        Log.d(Constants.TAG,"onBackward()");
        sendContent(prefs.getString(getString(R.string.backward_pref),"1"));
    }

    @Override
    public void onStoping() {
        Log.d(Constants.TAG,"onStoping()");
        sendContent(prefs.getString(getString(R.string.stop_pref),"2"));
    }

    @Override
    public void onRight() {
        Log.d(Constants.TAG,"onRight() ");
        sendContent(prefs.getString(getString(R.string.right_pref),"3"));
    }

    @Override
    public void onLeft() {
        Log.d(Constants.TAG,"onLeft() ");
        sendContent(prefs.getString(getString(R.string.left_pref),"4"));
    }

    @Override
    public void onRightForward() {
        Log.d(Constants.TAG,"onRightForward()");
        sendContent(prefs.getString(getString(R.string.rightf_pref),"5"));
    }

    @Override
    public void onLeftForward() {
        Log.d(Constants.TAG,"onLeftForward()");
        sendContent(prefs.getString(getString(R.string.leftf_pref),"6"));
    }

    @Override
    public void onRightBackward() {
        Log.d(Constants.TAG,"onRightBackward()");
        sendContent(prefs.getString(getString(R.string.rightb_pref),"7"));
    }

    @Override
    public void onLeftBackward() {
        Log.d(Constants.TAG,"onLeftBackward() ");
        sendContent(prefs.getString(getString(R.string.leftb_pref),"8"));
    }

    @Override
    public void onP0() {
        Log.d(Constants.TAG,"onP0()");
        sendContent(prefs.getString(getString(R.string.p0_pref),"9"));
    }

    @Override
    public void onP1() {
        Log.d(Constants.TAG,"onP1()");
        sendContent(prefs.getString(getString(R.string.p1_pref),"10"));
    }

    @Override
    public void onP2() {
        Log.d(Constants.TAG,"onP2()");
        sendContent(prefs.getString(getString(R.string.p2_pref),"11"));
    }

    @Override
    public void onP3() {
        Log.d(Constants.TAG,"onP3()");
        sendContent(prefs.getString(getString(R.string.p3_pref),"12"));
    }

}
