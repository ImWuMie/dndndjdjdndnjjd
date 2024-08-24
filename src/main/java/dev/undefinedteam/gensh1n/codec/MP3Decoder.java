package dev.undefinedteam.gensh1n.codec;

import icyllis.modernui.audio.SoundSample;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.system.MemoryUtil.memAllocShort;
import static org.lwjgl.system.MemoryUtil.memFree;

public class MP3Decoder extends SoundSample {
    private final short[] pcm_data;
    private int sampleIndex;

    public MP3Decoder(ByteBuffer mPayload) {

        try {
            BufferInputStream input = new BufferInputStream(mPayload);
            Bitstream bitstream = new Bitstream(input);
            Decoder decoder = new Decoder();

            int divider = 1;
            List<ShortBuffer> frames = new CopyOnWriteArrayList<>();

            Header header;
            while ((header = bitstream.readFrame()) != null) {
                SampleBuffer sb = (SampleBuffer) decoder.decodeFrame(header, bitstream);
                if (divider == 1) {
                    if (decoder.getOutputFrequency() <= 24000)
                        divider *= 2;
                    if (decoder.getOutputChannels() == 1)
                        divider *= 2;
                }
                ShortBuffer sbuf = memAllocShort(decoder.getOutputBlockSize());
                sbuf.put(sb.getBuffer());
                sbuf.flip();
                bitstream.closeFrame();
                frames.add(sbuf);
            }
            input.close();
            bitstream.close();

            this.mChannels = decoder.getOutputChannels();
            this.mSampleRate = decoder.getOutputFrequency();

            ShortBuffer _pcm_buffer = memAllocShort(frames.size() * decoder.getOutputBlockSize() / divider);
            for (ShortBuffer frame : frames) {
                for (int j = 0; j < decoder.getOutputBlockSize() / divider; j++)
                    _pcm_buffer.put(frame.get(j));
                memFree(frame);
            }
            _pcm_buffer.flip();
            this.pcm_data = new short[_pcm_buffer.limit()];
            _pcm_buffer.get(this.pcm_data);
            this.mTotalSamples = this.pcm_data.length / this.mChannels;
        } catch (Exception e) {
            throw new RuntimeException("failed");
        }
    }

    @Override
    public boolean seek(int sampleOffset) {
        this.sampleIndex = sampleOffset * this.mChannels;
        return true;
    }

    @Override
    public int getSamplesShortInterleaved(ShortBuffer pcmBuffer) {
        int decoded = 0;
        int copyLen = pcmBuffer.remaining();
        try {
            pcmBuffer.put(Arrays.copyOfRange(this.pcm_data, this.sampleIndex, this.sampleIndex + copyLen));
            this.sampleIndex += copyLen;
            decoded += copyLen;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return decoded / this.mChannels;
    }

    @Override
    public void close() {
    }
}
