package xyz.studiomango.sampler;

import javax.sound.sampled.AudioFormat;

public class SamplerContext {
    
    public int sampleRate = 44100;
    public int bitsDepth = 8;
    public int channels = 2;
    
    public AudioFormat getFormat() {
        return new AudioFormat(sampleRate, bitsDepth, channels, true, true);
    }
    
}
