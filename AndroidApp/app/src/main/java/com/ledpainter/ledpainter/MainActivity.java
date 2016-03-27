package com.ledpainter.ledpainter;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ToggleButton;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

import com.rarepebble.colorpicker.ColorPickerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SmoothBluetooth.Listener mListener = new SmoothBluetooth.Listener() {
        @Override
        public void onBluetoothNotSupported() {
            //device does not support bluetooth
        }

        @Override
        public void onBluetoothNotEnabled() {}

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

        @Override public void onDevicesFound(List<Device> deviceList, SmoothBluetooth.ConnectionCallback connectionCallback) {}

        @Override
        public void onDataReceived(int data) {}
    };

    protected ToggleButton buttonToggleBrush;
    protected ToggleButton buttonToggleErase;
    protected Button buttonSetColor;
    protected ToggleButton buttonTogglePreview;

    protected PaintingView paintingView;
    protected ColorPickerView colorPickerView;
    protected AlertDialog colorPickerDialog;

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

    public void onButtonSetColorClick() {
        colorPickerDialog.show();
        buttonSetColor.setTextColor(paintingView.brushColor);
        buttonSetColor.invalidate();
    }
}
