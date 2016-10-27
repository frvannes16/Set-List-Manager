package com.example.franklin.setlistmanager.helpers;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by Franklin on 10/25/2016.
 */

public class MetronomeTask implements Runnable {

    private static final int NUM_MEASURES = 4;
    private static String TAG = "MetronomeTask:";
    private static final int LOW_TONE_FREQUENCY = 1500;
    private static final int HIGH_TONE_FREQUENCY = 2000;
    public static final int DURATION = 44100;  // in bits.

    // Consts
    private static long NANOSECONDS_IN_MINUTE = 60000000000L;

    // Options
    private int BPM = 0;
    private double num = 4;
    private double denom = 4;

    // Resources
    private long interval;
    private Tone beep;

    public MetronomeTask(int BPM) {
        this.BPM = BPM;
        init();
    }

    private void init() {
        interval = ((NANOSECONDS_IN_MINUTE / BPM) * (long) (4 / denom));
        beep = new Tone();  // 1500 Hz for 0.1 seconds.
    }

    public long getInterval() {
        return interval;
    }

    @Override
    public void run() {
        beep.startMetronome();
    }


    /* The way we are able to keep time is due to how the tone is generated. Instead of playing
     * the tone and then forcing the thread to sleep or wait for the time interval between beats
      * to pass, we are instead just passing silence to the stream for the exact amount of bits
      * required to fill the empty space. While this leads to perfect timing via noise, it
      * makes it incredibly hard for us to time anything on the UI thread to it since we don't
      * have any way to indicate to the UI thread when the tone is being played.
      *
      * One option could be inheriting the AudioTrack class into its own class and commandeering
      * the play control so that after every x bits played it notifies a listener on the UI branch.
      * */
    private class Tone {
        static final int SAMPLE_RATE = 44100;
        // 44100 bits/second bit rate
        // interval in bits = 441000 bits/second * interval *10^9 (=seconds) - input bit length
        private int mBufferSize;
        private double[] mSound;
        private short[] mBlip; // low blip
        private short[] mBleep; // high blip
        AudioTrack mAudioTrack;

        // frequency in Hz. Duration in bits/second 44100 = 1 second.
        private short[] getNoise(double frequency, long interval_in_nanoseconds) {
            int bits = (int) (SAMPLE_RATE * interval_in_nanoseconds / Math.pow(10, 9));
            int mBufferSize = AudioTrack.getMinBufferSize(44100,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_8BIT);

            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    mBufferSize, AudioTrack.MODE_STREAM);

            // Generate Sine wave
            mSound = new double[4410];
            short[] mBuffer = new short[bits];
            for (int i = 0; i < mSound.length; i++) {
                mSound[i] = Math.sin((2.0 * Math.PI * i / (44100 / frequency)));
                mBuffer[i] = (short) (mSound[i] * Short.MAX_VALUE);
            }
            return mBuffer;
        }

        void startMetronome() {
            // Generate the noises
            mBlip = getNoise(LOW_TONE_FREQUENCY, interval);
            mBleep = getNoise(HIGH_TONE_FREQUENCY, interval);
            mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
            Log.d(TAG, "Playing");
            // Open the audio stream
            mAudioTrack.play();
            // Write to the audio stream
            for (int i = 0; i < num * NUM_MEASURES; i++) {
                short[] mBuffer = (i % num == 0) ? mBleep : mBlip;
                mAudioTrack.write(mBuffer, 0, mBuffer.length);
            }
            mAudioTrack.release();
            mAudioTrack.stop();
        }

    }
}
