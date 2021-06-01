package xyz.studiomango.sampler.nodes.effects;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;
import xyz.studiomango.sampler.parameters.Parameter;

public class SimpleReverb extends Node {

    public final Parameter decay = new Parameter(0.8);
    public final Parameter mix = new Parameter(0.9);
    public final int reverbSamples;
    
    public SimpleReverb(int reverbSamples) {
        this.reverbSamples = reverbSamples;
    }
    
    private double[][] internalBuffer;

    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {
        if (internalBuffer == null) {
            internalBuffer = new double[ctx.channels][reverbSamples];
        }
        
        for (int ch = 0; ch < internalBuffer.length; ch++) System.arraycopy(internalBuffer[ch], 0, internalBuffer[ch], 1, reverbSamples - 1);
        
        double audioIn = inputsSampleAt(ctx, index, channelNo);
        double mix = this.mix.valueAt(ctx, index);
        double reverbIn = internalBuffer[channelNo][reverbSamples - 1] * mix;
        double out = audioIn + reverbIn;
        internalBuffer[channelNo][0] = out * decay.valueAt(ctx, index);
        return out;
    }

    @Override
    public void resetThisNode() {
    }

}
