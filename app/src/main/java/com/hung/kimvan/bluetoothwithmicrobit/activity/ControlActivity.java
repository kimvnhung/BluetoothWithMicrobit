package com.hung.kimvan.bluetoothwithmicrobit.activity;

import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.hung.kimvan.bluetoothwithmicrobit.Fragment.AdvanceFragment;
import com.hung.kimvan.bluetoothwithmicrobit.Fragment.BasicFragment;
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
        BasicFragment.BasicFragmentListener {
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_ID = "extra_id";

    public  static final String TAG = "BluetoothWithMicrobit";
    private ViewPager viewPager;
    private TabLayout tabLayout;


    private BleAdapterService bluetooth_le_adapter;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetooth_le_adapter = ((BleAdapterService.LocalBinder) service).getService();
            bluetooth_le_adapter.setActivityHandler(mMessageHandler);
            connectToDevice();
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
            vPager.setAdapter(adapter);
    }

    private void showMsg(final String msg) {
        Log.d(Constants.TAG, msg);
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      /*BasicFragment basicFragment = (BasicFragment) ((SelectionPagerAdapter)viewPager.getAdapter()).
                                              getItem(viewPager.getCurrentItem());
                                      basicFragment.setStatus(Html.fromHtml(msg));
                                      */
                                  }
                              });
                          }
                      }
        );
    }

    // Service message handler�//////////////////
    private Handler mMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            String service_uuid = "";
            String characteristic_uuid = "";
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
            }
        }
    };

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
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            bluetooth_le_adapter.writeCharacteristic(Utility.normaliseUUID(BleAdapterService.UARTSERVICE_SERVICE_UUID), Utility.normaliseUUID(BleAdapterService.UART_RX_CHARACTERISTIC_UUID), ascii_bytes);
            BasicFragment basicFragment = (BasicFragment) ((SelectionPagerAdapter)viewPager.getAdapter()).
                    getItem(viewPager.getCurrentItem());
            basicFragment.setReceiveContent(content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            showMsg("Unable to convert text to ASCII bytes");
        }
    }
}
