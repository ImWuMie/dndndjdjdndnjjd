/*
 * Modern UI.
 * Copyright (C) 2019-2023 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package icyllis.modernui.mc;

import dev.undefinedteam.gensh1n.codec.MP3Decoder;
import dev.undefinedteam.gensh1n.music.GMusic;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.network.Http;
import icyllis.modernui.ModernUI;
import icyllis.modernui.annotation.Nullable;
import icyllis.modernui.audio.*;
import icyllis.modernui.core.Core;
import org.apache.commons.io.FilenameUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MusicPlayer {

    private static volatile MusicPlayer sInstance;

    private Track mCurrentTrack;
    private FFT mFFT;
    private float mGain = 1.0f;

    private String mName;

    private Consumer<Track> mOnTrackLoadCallback;

    public static MusicPlayer getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (MusicPlayer.class) {
            if (sInstance == null) {
                sInstance = new MusicPlayer();
            }
        }
        return sInstance;
    }

    private MusicPlayer() {
    }

    public static String openDialogGet() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer filters = stack.mallocPointer(1);
            stack.nUTF8("*.mp3;;*.ogg", true);
            filters.put(stack.getPointerAddress());
            filters.rewind();
            return TinyFileDialogs.tinyfd_openFileDialog(null, null,
                filters, "Audio files (*.mp3,*.ogg)", false);
        }
    }

    public void clearTrack() {
        GMusic.setCurrent(null);
        if (mCurrentTrack != null) {
            mCurrentTrack.close();
            mCurrentTrack = null;
        }
        mName = null;
    }

    public void replaceTrack(String name, String url) {
        clearTrack();
        CompletableFuture.supplyAsync(() -> {
            byte[] data = downloadFile(url);
            if (data != null) {
                ByteBuffer nativeEncodedData = ByteBuffer.wrap(data);
                SoundSample decoder;
                try {
                    decoder = new MP3Decoder(nativeEncodedData);
                } catch (RuntimeException e) {
                    decoder = new VorbisPullDecoder(nativeEncodedData);
                }
                return new Track(decoder);
            } else {
                ModernUI.LOGGER.error("Failed to open audio file, null buffer");
                return null;
            }
        }).whenCompleteAsync((track, ex) -> {
            mCurrentTrack = track;
            if (track != null) {
                track.setGain(mGain);
                mName = name;
            }
            if (mOnTrackLoadCallback != null) {
                mOnTrackLoadCallback.accept(track);
            }
        }, Core.getUiThreadExecutor());
    }

    private byte[] downloadFile(String str) {
        try {
            URL url = new URL(str);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            InputStream inputStream = conn.getInputStream();
            byte[] getData = readInputStream(inputStream);
            inputStream.close();
            return getData;
        } catch (Exception e) {
            InputStream inputStream;
            try {
                inputStream = Http.get(str).sendInputStream();
            } catch (Exception e1) {
                inputStream = Http.post(str).sendInputStream();
            }
            try {
                if (inputStream != null) {
                    byte[] getData = readInputStream(inputStream);
                    inputStream.close();
                    return getData;
                }
            } catch (IOException ex) {
            }
        }
        return null;
    }

    private byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4 * 1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public void replaceTrack(Path path) {
        clearTrack();
        CompletableFuture.supplyAsync(() -> {
            try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
                ByteBuffer nativeEncodedData = Core.readIntoNativeBuffer(channel).flip();
                String extension = FilenameUtils.getExtension(path.toFile().getName());
                SoundSample decoder = extension.equals("mp3") ? new MP3Decoder(nativeEncodedData) : new VorbisPullDecoder(nativeEncodedData);
                return new Track(decoder);
            } catch (IOException e) {
                ModernUI.LOGGER.error("Failed to open audio file, {}", path, e);
                return null;
            }
        }).whenCompleteAsync((track, ex) -> {
            mCurrentTrack = track;
            if (track != null) {
                track.setGain(mGain);
                mName = path.getFileName().toString();
            }
            if (mOnTrackLoadCallback != null) {
                mOnTrackLoadCallback.accept(track);
            }
        }, Core.getUiThreadExecutor());
    }

    public void setOnTrackLoadCallback(Consumer<Track> onTrackLoadCallback) {
        mOnTrackLoadCallback = onTrackLoadCallback;
    }

    @Nullable
    public String getTrackName() {
        return mName;
    }

    public boolean hasTrack() {
        return mCurrentTrack != null;
    }

    public float getTrackTime() {
        if (mCurrentTrack != null) {
            return mCurrentTrack.getTime();
        }
        return 0;
    }

    public float getTrackLength() {
        if (mCurrentTrack != null) {
            return mCurrentTrack.getLength();
        }
        return 0;
    }

    public void play() {
        if (mCurrentTrack != null) {
            mCurrentTrack.play();
        }
    }

    public void pause() {
        if (mCurrentTrack != null) {
            mCurrentTrack.pause();
        }
    }

    public boolean isPlaying() {
        if (mCurrentTrack != null) {
            return mCurrentTrack.isPlaying();
        }
        return false;
    }

    public boolean seek(float fraction) {
        if (mCurrentTrack != null) {
            return mCurrentTrack.seekToSeconds(fraction * mCurrentTrack.getLength());
        }
        return true;
    }

    public void setGain(float gain) {
        if (mGain != gain) {
            mGain = gain;
            if (mCurrentTrack != null) {
                mCurrentTrack.setGain(gain);
            }
        }
    }

    public float getGain() {
        return mGain;
    }

    public void setAnalyzerCallback(Consumer<FFT> setup, Consumer<FFT> callback) {
        if (mCurrentTrack == null) {
            return;
        }
        if (setup == null && callback == null) {
            mCurrentTrack.setAnalyzer(null, null);
        } else {
            if (mFFT == null || mFFT.getSampleRate() != mCurrentTrack.getSampleRate()) {
                mFFT = FFT.create(1024, mCurrentTrack.getSampleRate());
            }
            if (setup != null) {
                setup.accept(mFFT);
            }
            mCurrentTrack.setAnalyzer(mFFT, callback);
        }
    }
}
