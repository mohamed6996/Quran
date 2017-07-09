package com.listenquran.quran;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class Tset extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {
    ImageView play, fast_forward, fast_rewind, skip_prev, skip_for, replay, shuffle;
    TextView sura_name_textview, songCurrentDuration, songTotalDuration;
    private boolean isRepeat = false;
    private boolean isShuffle = false;

    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            checkAudioFocus(focusChange);
        }
    };

    private final Handler handler = new Handler();
    private int seekForwardTime = 5000; // 5 seconds
    private int seekBackwardTime = 5000; // 5 seconds
    String reciter_server;
    int aya_number;                 // 1
    int aya_index;
    String formatted_aya_number;   // 001
    String dataSource;
    String[] sura_name;
    HashMap<Integer, Integer> shuffleMap;
    int currentSuraNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tset);
        reciter_server = getIntent().getStringExtra("reciter_server");
        aya_number = getIntent().getIntExtra("aya_number", 1);
        aya_index = Sura.mDataSet_sura_number.indexOf(aya_number);
        sura_name = this.getResources().getStringArray(R.array.sura_name);
        songCurrentDuration = (TextView) findViewById(R.id.songCurrentDuration);
        songTotalDuration = (TextView) findViewById(R.id.songTotalDuration);
        sura_name_textview = (TextView) findViewById(R.id.sura_name_textview);
        //   sura_name_textview.setText(sura_name[aya_number - 1]);
        play = (ImageView) findViewById(R.id.play);
        fast_forward = (ImageView) findViewById(R.id.fast_forward);
        fast_rewind = (ImageView) findViewById(R.id.fast_rewind);
        skip_prev = (ImageView) findViewById(R.id.skip_prev);
        skip_for = (ImageView) findViewById(R.id.skip_for);
        replay = (ImageView) findViewById(R.id.replay);
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRepeat) {
                    isRepeat = false;
                    replay.setColorFilter(ContextCompat.getColor(Tset.this, R.color.colorWhite));
                    Toast.makeText(getApplicationContext(), "Repeat is off", Toast.LENGTH_SHORT).show();

                } else {
                    isRepeat = true;
                    replay.setColorFilter(ContextCompat.getColor(Tset.this, R.color.colorAccent));
                    Toast.makeText(getApplicationContext(), "Repeat is on", Toast.LENGTH_SHORT).show();

                    isShuffle = false;
                    shuffle.setColorFilter(ContextCompat.getColor(Tset.this, R.color.colorWhite));

                }

            }
        });

        shuffle = (ImageView) findViewById(R.id.shuffle);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isShuffle) {
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is off", Toast.LENGTH_SHORT).show();
                    shuffle.setColorFilter(ContextCompat.getColor(Tset.this, R.color.colorWhite));

                } else {
                    // make repeat to true
                    isShuffle = true;
                    Toast.makeText(getApplicationContext(), "Shuffle is on", Toast.LENGTH_SHORT).show();
                    shuffle.setColorFilter(ContextCompat.getColor(Tset.this, R.color.colorAccent));

                    // make shuffle to false
                    isRepeat = false;
                    replay.setColorFilter(ContextCompat.getColor(Tset.this, R.color.colorWhite));

                }
            }
        });


        shuffleMap = new HashMap<>();
        for (int i = 0; i < Sura.mDataSet_sura_number.size(); i++) {
            shuffleMap.put(i, Sura.mDataSet_sura_number.get(i));
        }

        mediaPlayer = new MediaPlayer();

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // remove message Handler from updating progress bar
                handler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mediaPlayer.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        });


        audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(Tset.this);
        mediaPlayer.setOnPreparedListener(Tset.this);
        mediaPlayer.setOnCompletionListener(Tset.this);
        mediaPlayer.setOnErrorListener(this);

        playSong(aya_number);
      /*  try {
            constructDataSource(aya_number);
            mediaPlayer.setDataSource(dataSource);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        // play.setImageResource(R.drawable.ic_play_circle);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   play.setImageResource(R.drawable.ic_pause_circle);
                //    isPlaying =true;

                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.ic_pause_circle);

                    //  buttonPlayPause.setImageResource(R.drawable.button_pause);

                } else {
                    mediaPlayer.pause();
                    //  buttonPlayPause.setImageResource(R.drawable.button_play);
                    play.setImageResource(R.drawable.ic_play_circle);
                }

                //    primarySeekBarProgressUpdater();
                // request audio focus
                int result = audioManager.requestAudioFocus(onAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    //   audioManager.registerMediaButtonEventReceiver(onAudioFocusChangeListener);
                    // might take long! (for buffering, etc)   //@@

                    // mediaPlayer.start();

                }

            }

        });

     /*   pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.INVISIBLE);
                mediaPlayer.pause();
            }
        });
*/

        fast_forward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mediaPlayer.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()) {
                    // forward song
                    mediaPlayer.seekTo(currentPosition + seekForwardTime);
                } else {
                    // forward to end position
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                }
            }
        });


        fast_rewind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mediaPlayer.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if (currentPosition - seekBackwardTime >= 0) {
                    // forward song
                    mediaPlayer.seekTo(currentPosition - seekBackwardTime);
                } else {
                    // backward to starting position
                    mediaPlayer.seekTo(0);
                }

            }
        });


        skip_for.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //  aya_index = aya_index + 1;
                //  playSong(Sura.mDataSet_sura_number.get(aya_index));
                //   sura_name_textview.setText(sura_name[aya_number]);

               /* int index = Sura.mDataSet_sura_number.indexOf(currentSuraNumber);

                playSong(Sura.mDataSet_sura_number.get(index) + 1);*/
                aya_index = aya_index + 1;
                playSong(Sura.mDataSet_sura_number.get(aya_index));


            }
        });


        skip_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (aya_index > 0) {
                    aya_index = aya_index - 1;
                    playSong(Sura.mDataSet_sura_number.get(aya_index));

                } else {
                    Toast.makeText(Tset.this, "This is the first sura", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    /**
     * Next button click event
     * Plays next song by taking currentSongIndex + 1
     */

   /* @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }*/
    @Override
    protected void onPause() {
        super.onPause();
        //  releaseMediaPlayer();
        mediaPlayer.pause();
        //  buttonPlayPause.setImageResource(R.drawable.button_play);
        play.setImageResource(R.drawable.ic_play_circle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        seekBar.setSecondaryProgress(i);


    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        if (isRepeat) {
            mediaPlayer.start();
        } else if (isShuffle) {
            Random random = new Random();
            int randa = random.nextInt(shuffleMap.size());
            aya_index = randa;
            playSong(shuffleMap.get(randa));
        } else {
            // no repeat or shuffle is ON , play next song
            aya_index = aya_index + 1;
            playSong(Sura.mDataSet_sura_number.get(aya_index));
        }


    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        // primarySeekBarProgressUpdater();
        //  playSong(aya_number);
        //  play.setImageResource(R.drawable.ic_pause_circle);
    }


    public void checkAudioFocus(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            // Pause playback because your Audio Focus was
            // temporarily stolen, but will be back soon.
            // i.e. for a phone call
            mediaPlayer.pause();

        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            // Stop playback, because you lost the Audio Focus.
            // i.e. the user started some other playback app
            // Remember to unregister your controls/buttons here.
            // And release the kra — Audio Focus!
            // You’re done.
            //  audioManager.unregisterAudioRecordingCallback(onAudioFocusChangeListener);
            //  audioManager.unregisterMediaButtonEventReceiver(onAudioFocusChangeListener);
            //  audioManager.abandonAudioFocus();
            // or y could call yr helper method releaseMediaPlayer
            releaseMediaPlayer();
        } else if (focusChange ==
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            // Lower the volume, because something else is also
            // playing audio over you.
            // i.e. for notifications or navigation directions
            // Depending on your audio playback, you may prefer to
            // pause playback here instead. You do you.
            mediaPlayer.pause();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // Resume playback, because you hold the Audio Focus
            // again!
            // i.e. the phone call ended or the nav directions
            // are finished
            // If you implement ducking and lower the volume, be
            // sure to return it to normal here, as well.
            mediaPlayer.start();
        }
    }

    /**
     * Clean up the media player by releasing its resources.
     */
   /* private void releaseMediaPlayer() {

            mediaPlayer.release();
            //  release the media source after the sound finishes , befour the media player has initialized
            // to play diff. sound
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);

    }*/
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    public void playSong(int aya_number) {
        constructDataSource(aya_number);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(dataSource);
            play.setImageResource(R.drawable.ic_pause_circle);
            mediaPlayer.prepareAsync();
            // start the media player on the onPrepared method callback

            // Displaying Song title
            sura_name_textview.setText(sura_name[aya_number - 1]);

            // set Progress bar values
            seekBar.setProgress(0);
            seekBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String constructDataSource(int index) {
        formatted_aya_number = String.format("%03d", index);  // 001
        //  "http://server11.mp3quran.net/shatri/001.mp3"
        dataSource = reciter_server + "/" + formatted_aya_number + ".mp3";
        return dataSource;
    }

    public void updateProgressBar() {
        handler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();

                // Displaying Total Duration time
                songTotalDuration.setText("" + milliSecondsToTimer(totalDuration));
                // Displaying time completed playing
                songCurrentDuration.setText("" + milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                seekBar.setProgress(progress);

                // Running this thread after 100 milliseconds
                handler.postDelayed(this, 100);
                if (currentDuration >= (totalDuration / 8)) {
                    // layoutads.setVisibility(View.VISIBLE);
                }
            } catch (Exception ex) {
            }
        }
    };

    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return true;
    }
}
