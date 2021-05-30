package xyz.studiomango.sampler.buffers;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import xyz.studiomango.sampler.nodes.generators.Player;

import javax.sound.sampled.AudioFormat.Encoding;

/**
 * The audio buffer. This contains the audio data, which can be played by {@link Player} node. To create new empty
 * buffer, see {@link #AudioBuffer(int, int, int)}. To load from file, see {@link #AudioBuffer(File)}
 * @author nahkd
 *
 */
public class AudioBuffer {
    
    /**
     * Buffer sampling rate (a.k.a how much samples per second)
     */
    public final int sampleRate;
    
    /**
     * The number of samples inside this buffer. The audio duration can be calculated by using this simple formula:
     * d = {@link #samples} / {@link #sampleRate}
     */
    public final int samples;
    
    /**
     * Channel data. The first dimension is the channel index, and the second one is the sample index, which must always
     * less than {@link #samples}
     */
    public final double[][] channelsData;
    
    /**
     * Create new empty audio buffer. The buffer does not contains any meaningful audio data
     * @param sampleRate The buffer sampling rate
     * @param samples The number of samples
     * @param channels Channels count
     */
    public AudioBuffer(int sampleRate, int samples, int channels) {
        this.sampleRate = sampleRate;
        this.samples = samples;
        this.channelsData = new double[channels][];
        
        for (int i = 0; i < channels; i++) channelsData[i] = new double[samples];
    }
    
    /**
     * Load audio buffer from file. Currently only *.wav files are supported (and they must be uncompressed)
     * @param file
     */
    public AudioBuffer(File file) {
        AudioFileFormat format;
        AudioInputStream stream;
        
        try {
            format = AudioSystem.getAudioFileFormat(file);
            stream = AudioSystem.getAudioInputStream(file);
            
            sampleRate = Math.round(format.getFormat().getSampleRate());
            samples = format.getFrameLength();
            channelsData = new double[format.getFormat().getChannels()][];
            int bitDepth = format.getFormat().getSampleSizeInBits();
            int bytesPerFrame = format.getFormat().getFrameSize();
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) bytesPerFrame = 1;
            Encoding encoding = format.getFormat().getEncoding();
            boolean bigEndian = format.getFormat().isBigEndian();
            
            if (bitDepth < 8) throw new UnsupportedAudioFileException("The bit depth of given audio is less than 8, which Player can't process it");
            if (
                !encoding.equals(Encoding.PCM_SIGNED) &&
                !encoding.equals(Encoding.PCM_UNSIGNED)
            ) throw new UnsupportedAudioFileException("The encoding is not supported");
            
            for (int i = 0; i < channelsData.length; i++) channelsData[i] = new double[samples];
            
            byte[] frame = new byte[bytesPerFrame];
            int numBytesRead = 0;
            int totalBytesRead = 0;
            
            for (int i = 0; i < samples; i++) {
                while ((numBytesRead = stream.read(frame, totalBytesRead, bytesPerFrame - totalBytesRead)) != -1 && totalBytesRead < bitDepth / 8) {
                    totalBytesRead += numBytesRead;
                }
                totalBytesRead = 0;
                if (numBytesRead == -1) break;
                
                for (int j = 0; j < channelsData.length; j++) channelsData[j][i] = bytesToPCM(frame, j, bitDepth, encoding, bigEndian);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private double bytesToPCM(byte[] frame, int channelNo, int bitDepth, Encoding encoding, boolean bigEndian) {
        int byteDepth = bitDepth / 8;
        if (byteDepth == 1) {
            // 8-bit depth
            if (encoding.equals(Encoding.PCM_UNSIGNED)) return (Byte.toUnsignedInt(frame[channelNo]) / 255.0D) * 2 - 1;
            if (encoding.equals(Encoding.PCM_SIGNED)) return frame[channelNo] / 128.0D;
        } else if (byteDepth == 2) {
            // 16-bit depth
            short val;
            if (bigEndian) val = ByteBuffer.wrap(frame).order(ByteOrder.BIG_ENDIAN).getShort(channelNo * byteDepth);
            else val = ByteBuffer.wrap(frame).order(ByteOrder.LITTLE_ENDIAN).getShort(channelNo * byteDepth);
            
            if (encoding.equals(Encoding.PCM_UNSIGNED)) return (Short.toUnsignedInt(val) / 65535D) * 2 - 1;
            if (encoding.equals(Encoding.PCM_SIGNED)) return val / 32768D; 
        }
        return 0;
    }
    
}
