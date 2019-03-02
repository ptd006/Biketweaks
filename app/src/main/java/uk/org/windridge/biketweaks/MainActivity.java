

package uk.org.windridge.biketweaks;

import android.content.Context;
import android.media.session.MediaSession;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.AudioFormat;

import android.os.Bundle;
import android.os.PowerManager;
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


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private AudioTrack _dummyAudioTrack;

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
                Snackbar.make(view, "Play dummy audio", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //_dummyAudioTrack.play();
            }
        });



        // Source: https://stackoverflow.com/a/50678833/4601240
        MediaSession ms = new MediaSession(getApplicationContext(), getPackageName());
        ms.setActive(true);

        ms.setCallback(new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                Log.e(TAG, "media button callback");

                KeyEvent keyEvent = (KeyEvent) mediaButtonIntent.getExtras().get(Intent.EXTRA_KEY_EVENT);

                if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    Log.e(TAG, "media button release");
                    wake();
                }

                return super.onMediaButtonEvent(mediaButtonIntent);
            }
        });

        // you can button by receiver after terminating your app
        // ms.setMediaButtonReceiver(mbr);

        // play dummy audio
        AudioTrack _dummyAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 48000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);

        _dummyAudioTrack.play();
        _dummyAudioTrack.pause();


        // let garbage disposal deal with it when app exits
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
