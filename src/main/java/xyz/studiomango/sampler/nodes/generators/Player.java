package xyz.studiomango.sampler.nodes.generators;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.buffers.AudioBuffer;
import xyz.studiomango.sampler.nodes.Node;

public class Player extends Node {
    
    public final AudioBuffer buffer;
    
    /**
     * Sample speed. This will modify the sample rate, which might cause the audio to play faster and the pitch increase
     */
    public double sampleSpeed = 1;
    
    public Player(AudioBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {
        if (channelNo >= buffer.channelsData.length) return 0;
        int bufferSampleIndex = (int) (index * (buffer.sampleRate * sampleSpeed) / ctx.sampleRate);
        if (bufferSampleIndex >= buffer.samples) return 0;
        return buffer.channelsData[channelNo][bufferSampleIndex];
    }

    @Override
    public void resetThisNode() {}

}
