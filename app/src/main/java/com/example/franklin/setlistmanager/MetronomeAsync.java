package com.example.franklin.setlistmanager;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by Franklin on 10/25/2016.
 */

public class MetronomeAsync extends AsyncTask<Boolean, Void, Void> {

    // Consts
    private static long NANOSECONDS_IN_MINUTE = 60000000000L;

    // Options
    private int BPM = 0;
    private double num = 4;
    private double denom = 4;

    // Resources
    private long startTime;
    private long interval;

    private Tone beep;

    public MetronomeAsync() {
        BPM = 120;  // Default. Only used in case pushing to FirebaseDB.
        init();
    }

    public MetronomeAsync(int BPM) {
        this.BPM = BPM;
        init();
    }

    private void init(){
        interval = ((NANOSECONDS_IN_MINUTE / BPM) * (long) (4/denom));
        beep = new Tone(1500, 4410);  // 1500 Hz for 0.1 seconds.
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Setup
    }

    @Override
    protected Void doInBackground(Boolean... params) {
        // Background work. Here we check to see if it has passed the timer. We then call publishProgress()
        // Make beep sound here?
        startTime = System.nanoTime();
        long estimated_time;
        boolean hit = false;
        while(params[0]){
            estimated_time =  System.nanoTime() - startTime;
            if (!hit && estimated_time % interval < 100){
                hit = true;
                publishProgress();
            } else {
                hit = false;
            }
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        // Runs on the UI thread after publishProgress is invoked.
        // Make UI changes. Make beep sound here?
        beep.playSound();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // Done, stop time and cleanup.
    }

    private class Tone {
        private int mBufferSize;
        private double[] mSound;
        private short[] mBuffer;
        AudioTrack mAudioTrack;

        // frequency in Hz. Duration in bits/second 44100 = 1 second.
        public Tone(double frequency, int duration) {
            // AudioTrack definition
            int mBufferSize = AudioTrack.getMinBufferSize(44100,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_8BIT);

            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    mBufferSize, AudioTrack.MODE_STREAM);

            // Sine wave
            double[] mSound = new double[4410];
            short[] mBuffer = new short[duration];
            for (int i = 0; i < mSound.length; i++) {
                mSound[i] = Math.sin((2.0*Math.PI * i/(44100/frequency)));
                mBuffer[i] = (short) (mSound[i]*Short.MAX_VALUE);
            }
        }

        void playSound() {
            mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
            mAudioTrack.play();
            mAudioTrack.write(mBuffer, 0, mSound.length);
            mAudioTrack.stop();
            mAudioTrack.setPlaybackHeadPosition(0);
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            mAudioTrack.release();
        }
    }

}
