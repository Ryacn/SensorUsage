package com.radiantkey.sensorusage;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    float maxValue;
    SensorManager sensorManager;
    Sensor lightSensor;
    boolean indicator = false;

    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDSTOP = "stop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if(lightSensor == null){
            Toast.makeText(this, "Device has no light sesor", Toast.LENGTH_SHORT).show();
            finish();
        }
        maxValue = lightSensor.getMaximumRange();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT){
            float value = sensorEvent.values[0];

//            set to value between 0 to wanted value
            int newValue = (int) (255f * value / maxValue);

            if(newValue < 150 && !indicator){
                Toast.makeText(MainActivity.this, "Broadcasting", Toast.LENGTH_SHORT).show();

                //Test1:
                //does not work on pixel 2 but may work on other devices
//                Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
//                synchronized (this){
//                    i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
//                    sendOrderedBroadcast(i, null);
//
//                    i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT));
//                    sendOrderedBroadcast(i, null);
//                }

                //Test2:
                //works on pixel 2 but may not work on other devices
                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                if(mAudioManager.isMusicActive()) {
                    Intent i = new Intent(SERVICECMD);
                    i.putExtra(CMDNAME , CMDNEXT );
                    MainActivity.this.sendBroadcast(i);
                }

                //Test3:
                //does not work on pixel 2 but may work on other devices
//                long eventtime = SystemClock.uptimeMillis();
//                Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
//                KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN,   KeyEvent.KEYCODE_MEDIA_NEXT, 0);
//                downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
//                sendOrderedBroadcast(downIntent, null);

                indicator = true;
            }else if(newValue > 150 && indicator){
                indicator = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
