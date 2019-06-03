

package uk.org.windridge.biketweaks;

import android.app.Activity;
import android.content.Context;
import android.hardware.input.InputManager;
import android.media.session.MediaSession;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.AudioFormat;

import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyCharacterMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MediaSession ms;

    private AudioTrack _dummyAudioTrack;


    // 3 possible methods
    // 1) hacky from https://android.googlesource.com/platform/cts/+/master/tests/tests/media/src/android/media/cts/MediaSessionManagerTest.java
    // 2) hacky using hidden injectInputEvent (based on https://stackoverflow.com/questions/18699614/android-inputmanager-injectinputevent)
    // 3) the correct accessibility service
    // All need extra permissions

    // Implement later

    private void pressKey(int keyCode)  {

    }

    public void wake(){
        PowerManager pm =(PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);

        PowerManager.WakeLock wakeLock  =pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP,TAG);
        wakeLock.acquire();
        wakeLock.release();
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
                Snackbar.make(view, "Make media session active and play dummy audio", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.e(TAG, "refresh media session");

                // Take back priority
                ms.setActive(true);
                _dummyAudioTrack.play();
                _dummyAudioTrack.pause();


            }
        });



        // Based on https://stackoverflow.com/a/50678833/4601240
        ms = new MediaSession(getApplicationContext(), getPackageName());
        ms.setActive(true);


        ms.setCallback(new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                Log.e(TAG, "media button callback");

                KeyEvent keyEvent = (KeyEvent) mediaButtonIntent.getExtras().get(Intent.EXTRA_KEY_EVENT);
                Log.e(TAG, keyEvent.toString());


                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT) {
                    Log.e(TAG, "want to zoom with volume up");
                    pressKey(KeyEvent.KEYCODE_VOLUME_UP);

                    return super.onMediaButtonEvent(mediaButtonIntent);


                } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                    Log.e(TAG, "want to zoom with volume down");
                    pressKey(KeyEvent.KEYCODE_VOLUME_DOWN);

                    return super.onMediaButtonEvent(mediaButtonIntent);
                }


                if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    Log.e(TAG, "media button release");


                    wake();

                    // Open OruxMaps
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.orux.oruxmapsDonate");
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                        Log.e(TAG, "startActivity ok");
                    }
                }

                return super.onMediaButtonEvent(mediaButtonIntent);
            }
        });

        // ms.setMediaButtonReceiver(mbr);

        // dummy audio
        _dummyAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 48000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);

        // play and immediately pause
        _dummyAudioTrack.play();
        _dummyAudioTrack.pause();


        // let garbage disposal deal with when app exits
        // _dummyAudioTrack.stop();
        // _dummyAudioTrack.release();
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


}
