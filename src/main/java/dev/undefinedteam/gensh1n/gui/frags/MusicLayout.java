package dev.undefinedteam.gensh1n.gui.frags;

import dev.undefinedteam.gensh1n.gui.overlay.MusicSpectrum;
import dev.undefinedteam.gensh1n.music.GMusic;
import dev.undefinedteam.gensh1n.music.MusicApi;
import icyllis.modernui.audio.FFT;
import icyllis.modernui.core.Context;
import icyllis.modernui.mc.MusicFragment;
import icyllis.modernui.mc.MusicPlayer;
import icyllis.modernui.mc.ui.ThemeControl;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.widget.*;

import java.nio.file.Path;
import java.util.Objects;

import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MusicLayout extends LinearLayout {
    static final MusicApi api = GMusic.INSTANCE.api;

    public final MusicPlayer mMusicPlayer;
    private final MusicSpectrum mSpectrumDrawable;

    private Button mTitleButton;
    public Button mPlayButton;
    public TextView mLyricView;
    public boolean isPlaying = false;
    private final MusicFragment.SeekLayout mSeekLayout;

    public MusicLayout(Context context, MusicSpectrum spectrumDrawable) {
        super(context);
        this.mSpectrumDrawable = spectrumDrawable;

        mMusicPlayer = MusicPlayer.getInstance();
        mMusicPlayer.setOnTrackLoadCallback(track -> {
            if (track != null) {
                mMusicPlayer.setAnalyzerCallback(
                    fft -> {
                        fft.setLogAverages(250, 14);
                        fft.setWindowFunc(FFT.NONE);
                    },
                    mSpectrumDrawable::updateAmplitudes
                );
                track.play();
                mPlayButton.setText("⏸");
                isPlaying = true;
            } else {
                mPlayButton.setText("⏵");
                isPlaying = false;
                Toast.makeText(context,
                    "Failed to open Ogg Vorbis file", Toast.LENGTH_SHORT).show();
            }

            this.mLyricView.setText("...");
            var trackName = mMusicPlayer.getTrackName();
            mTitleButton.setText(Objects.requireNonNullElse(trackName, "Play"));
        });

        {
            setOrientation(VERTICAL);
            setVerticalGravity(Gravity.LEFT);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            setLayoutParams(params);
        }
        {
            mMusicPlayer.setAnalyzerCallback(null, mSpectrumDrawable::updateAmplitudes);
        }

        {
            LinearLayout playStop = new LinearLayout(context);
            playStop.setOrientation(HORIZONTAL);
            playStop.setHorizontalGravity(Gravity.START);
            {
                var button = new Button(context);
                button.setOnClickListener(v -> {
                    String path = MusicPlayer.openDialogGet();
                    if (path != null) {
                        mMusicPlayer.replaceTrack(Path.of(path));
                    }
                });
                var trackName = mMusicPlayer.getTrackName();
                button.setText(Objects.requireNonNullElse(trackName, "Play"));
                button.setTextSize(16f);
                button.setTextColor(0xFF28A3F3);
                button.setPadding(dp(5), dp(4), 0, dp(4));
                button.setMinWidth(button.dp(40));
                mTitleButton = button;

                LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1);
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                params.setMargins(dp(4), dp(2), dp(4), dp(2));
                playStop.addView(button, params);
            }
            {
                var button = new Button(context);
                button.setOnClickListener(v -> {
                    var btn = (Button) v;
                    if (mMusicPlayer.isPlaying()) {
                        mMusicPlayer.pause();
                        btn.setText("⏵");
                        isPlaying = false;
                    } else {
                        mMusicPlayer.play();
                        btn.setText("⏸");
                        isPlaying = true;
                    }
                });
                if (mMusicPlayer.isPlaying()) {
                    button.setText("⏸");
                    isPlaying = true;
                } else {
                    button.setText("⏵");
                    isPlaying = false;
                }
                button.setTextSize(24f);
                button.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                button.setPadding(dp(5), 0, 0, 0);
                button.setMinWidth(button.dp(20));
                mPlayButton = button;
                LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                params.gravity = Gravity.CENTER_VERTICAL;
                params.setMargins(dp(4), dp(2), dp(4), dp(2));

                playStop.addView(button, params);
            }
            addView(playStop, MATCH_PARENT, WRAP_CONTENT);
        }

        {
            LinearLayout status = new LinearLayout(context);
            status.setOrientation(HORIZONTAL);
            status.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            { // Progress
                var seekLayout = new MusicFragment.SeekLayout(context, 12);
                mSeekLayout = seekLayout;
                seekLayout.setPadding(dp(2), 0, 0, 0);
                LayoutParams params = new LayoutParams(dp(200), WRAP_CONTENT);
                params.gravity = Gravity.CENTER_VERTICAL;
                params.setMargins(dp(1), dp(1), dp(4), dp(1));

                status.addView(seekLayout, params);

                seekLayout.post(this::updateProgress);
                seekLayout.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    boolean mPlaying;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            float fraction = progress / 10000f;
                            float length = mMusicPlayer.getTrackLength();
                            mSeekLayout.mMinText.setText(formatTime((int) (fraction * length)));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mPlaying = mMusicPlayer.isPlaying();
                        if (mPlaying) {
                            mMusicPlayer.pause();
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mMusicPlayer.seek(seekBar.getProgress() / 10000f);
                        if (mPlaying) {
                            mMusicPlayer.play();
                        }
                    }
                });
            }

            { // Volume
                var volumeBar = new MusicFragment.SeekLayout(context, 18);
                volumeBar.setPadding(dp(2), 0, 0, 0);
                LayoutParams params = new LayoutParams(dp(200), WRAP_CONTENT);
                params.setMargins(dp(4), dp(1), dp(1), dp(1));
                status.addView(volumeBar, params);

                volumeBar.mMinText.setText("\uD83D\uDD07");
                volumeBar.mMaxText.setText("\uD83D\uDD0A");
                volumeBar.mSeekBar.setProgress(Math.round(mMusicPlayer.getGain() * 10000));
                volumeBar.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mMusicPlayer.setGain(progress / 10000f);
                        }
                    }
                });
            }

            addView(status, WRAP_CONTENT, WRAP_CONTENT);
        }

        {
            mLyricView = new TextView(context);
            mLyricView.setText("...");
            mLyricView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            mLyricView.setTextIsSelectable(true);
            mLyricView.setTextSize(16);
            ThemeControl.addBackground(mLyricView);

            mLyricView.post(this::updateLyric);
            addView(mLyricView, MATCH_PARENT, WRAP_CONTENT);
        }
    }

    public String trackTimeStr() {
        if (mMusicPlayer.isPlaying()) {
            float time = mMusicPlayer.getTrackTime();
            float length = mMusicPlayer.getTrackLength();
            String cTime = formatTime((int) time);
            String maxTime = formatTime((int) length);
            return cTime + "/" + maxTime;
        } else return "Paused";
    }

    private void updateLyric() {
        var lyric = GMusic.INSTANCE.currentLyric;
        if (mMusicPlayer.isPlaying() && lyric != null) {
            float time = mMusicPlayer.getTrackTime();
            if (lyric.checkTime((int) (time * 1000), false)) {
                if (!lyric.getLyric().equals(this.mLyricView.getText())) {
                    this.mLyricView.setText(lyric.getLyric());
                }
            }
            if (this.mLyricView.getText().isEmpty()) {
                this.mLyricView.setText("...");
            }
        }
        mLyricView.postDelayed(this::updateLyric, 2);
    }

    private void updateProgress() {
        if (mMusicPlayer.isPlaying()) {
            float time = mMusicPlayer.getTrackTime();
            float length = mMusicPlayer.getTrackLength();

            mSeekLayout.mMinText.setText(formatTime((int) time));
            mSeekLayout.mSeekBar.setProgress((int) (time / length * 10000));
            mSeekLayout.mMaxText.setText(formatTime((int) length));
        }
        mSeekLayout.postDelayed(this::updateProgress, 200);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        int hours = minutes / 60;
        minutes -= hours * 60;
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }
}
