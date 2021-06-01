package xyz.studiomango.sampler.nodes.generators;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.buffers.AudioBuffer;
import xyz.studiomango.sampler.nodes.Node;
import xyz.studiomango.sampler.nodes.effects.Resampling;

/**
 * Player node. This node is capable of playing audio buffer. See {@link AudioBuffer} for information on how to create
 * your own buffer (or load from *.wav file)
 * @author nahkd
 *
 */
public class Player extends Node {
    
    /**
     * The targeted audio buffer
     */
    public final AudioBuffer buffer;
    
    /**
     * Sample speed. This will modify the sample rate, which might cause the audio to play faster and the pitch increase.
     * If you don't want to increase speed but you want to change pitch, see {@link Resampling}. This node is capable of doing
     * real time pitch shifting as well!
     */
    public double sampleSpeed = 1;
    
    /**
     * The duration to play in samples unit. Calculate the number of samples based on seconds by using this formula: context
     * sample rate * time in seconds. If this value is less than or equals 0, it will play the entire buffer
     */
    public int samplesDuration = 0;
    
    /**
     * Create new audio buffer player
     * @param buffer
     */
    public Player(AudioBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {
        if (samplesDuration > 0 && samplesDuration < index) return 0;
        if (channelNo >= buffer.channelsData.length) return 0;
        int bufferSampleIndex = (int) (index * (buffer.sampleRate * sampleSpeed) / ctx.sampleRate);
        if (bufferSampleIndex >= buffer.samples) return 0;
        return buffer.channelsData[channelNo][bufferSampleIndex];
    }

    @Override
    public void resetThisNode() {}

}
