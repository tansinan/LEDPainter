package com.ledpainter.ledpainter;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

import com.rarepebble.colorpicker.ColorPickerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    final static int REQUEST_ENABLE_BLUETOOTH = 100;

    private SmoothBluetooth.Listener bluetoothListener = new SmoothBluetooth.Listener() {
        @Override
        public void onBluetoothNotSupported() {
            Toast.makeText(MainActivity.this,
                    "Sorry, this device doesn't support bluetooth.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBluetoothNotEnabled() {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            MainActivity.this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        @Override
        public void onConnecting(Device device) {}

        @Override
        public void onConnected(Device device) {
        }

        @Override public void onDisconnected() {}

        @Override
        public void onConnectionFailed(Device device) {
        }

        @Override public void onDiscoveryStarted() {}

        @Override public void onDiscoveryFinished() {}

        @Override public void onNoDevicesFound() {}

        @Override public void onDevicesFound(List<Device> deviceList, SmoothBluetooth.ConnectionCallback connectionCallback) {
            Device validDevice = null;
            for(Device device : deviceList) {
                if(device.getAddress().contains("98:D3:31:20:4D:C9")) {
                    Toast.makeText(MainActivity.this, "Device found!", Toast.LENGTH_SHORT).show();
                    validDevice = device;
                    bluetoothManager.cancelDiscovery();
                }
            }
            if(validDevice != null) {
                connectionCallback.connectTo(validDevice);
            }
            else {
                Toast.makeText(MainActivity.this, "No device found!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDataReceived(int data) {}
    };

    protected ToggleButton buttonToggleBrush;
    protected ToggleButton buttonToggleErase;
    protected Button buttonSetColor;
    protected ToggleButton buttonTogglePreview;
    protected Button buttonConnect;
    protected Button buttonPushImage;

    protected PaintingView paintingView;
    protected ColorPickerView colorPickerView;
    protected AlertDialog colorPickerDialog;

    protected SmoothBluetooth bluetoothManager;

    protected void setupColorPickerDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Your Title");

        // set dialog message
        alertDialogBuilder
                .setView(colorPickerView)
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        PaintingView paintingView = MainActivity.this.paintingView;
                        ColorPickerView colorPickerView = MainActivity.this.colorPickerView;
                        paintingView.setBrushColor(colorPickerView.getColor());
                        paintingView.setEraseModeEnabled(false);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        colorPickerDialog = alertDialogBuilder.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bluetoothManager = new SmoothBluetooth(this,
                SmoothBluetooth.ConnectionTo.OTHER_DEVICE,
                SmoothBluetooth.Connection.INSECURE, bluetoothListener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        buttonToggleBrush = (ToggleButton)findViewById(R.id.button_toggle_brush);
        buttonToggleBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.paintingView.setEraseModeEnabled(false);
                MainActivity.this.buttonToggleBrush.setChecked(true);
                MainActivity.this.buttonToggleErase.setChecked(false);
            }
        });
        buttonToggleErase = (ToggleButton)findViewById(R.id.button_toggle_eraser);
        buttonToggleErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.paintingView.setEraseModeEnabled(true);
                MainActivity.this.buttonToggleBrush.setChecked(false);
                MainActivity.this.buttonToggleErase.setChecked(true);
            }
        });
        buttonSetColor = (Button)findViewById(R.id.button_set_color);
        buttonSetColor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.this.onButtonSetColorClick();
            }
        });
        buttonTogglePreview = (ToggleButton)findViewById(R.id.button_toggle_preview);
        buttonTogglePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintingView.setPixelModeEnabled(buttonTogglePreview.isChecked());
            }
        });
        buttonConnect = (Button)findViewById(R.id.button_connect);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.onButtonConnect();
            }
        });
        buttonPushImage = (Button)findViewById(R.id.button_push_image);
        buttonPushImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.onButtonPushImage();
            }
        });
        paintingView = (PaintingView)findViewById(R.id.view);
        colorPickerView = new ColorPickerView(this);
        colorPickerView.setColor(Color.BLACK);
        setupColorPickerDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            //Handles the result of the bluetooth intent
            case REQUEST_ENABLE_BLUETOOTH:
                if(resultCode == RESULT_OK) {
                    bluetoothManager.doDiscovery();
                }
                break;
        }
    }

    public void onButtonSetColorClick() {
        colorPickerDialog.show();
        buttonSetColor.setTextColor(paintingView.brushColor);
        buttonSetColor.invalidate();
    }

    public void onButtonConnect() {
        bluetoothManager.doDiscovery();
    }

    public void onButtonPushImage() {
        if(bluetoothManager.isConnected()) {
            for(int i = 0; i < paintingView.gridWidth; i++)
            {
                for(int j = 0; j < paintingView.gridHeight; j++)
                {
                    int color = paintingView.gridData[i][j];
                    bluetoothManager.send(Integer.toString(Color.red(color)) + " ");
                    bluetoothManager.send(Integer.toString(Color.green(color)) + " ");
                    bluetoothManager.send(Integer.toString(Color.blue(color)) + " ");
                }
            }
        }
    }
}
