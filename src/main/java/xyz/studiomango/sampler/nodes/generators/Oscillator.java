package xyz.studiomango.sampler.nodes.generators;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;
import xyz.studiomango.sampler.parameters.Parameter;

public class Oscillator extends Node {
    
    public final Parameter frequency = new Parameter(440.0);
    
    public Oscillator() {}
    
    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {
        double freq = frequency.valueAt(ctx, index);
        return Math.sin(index * freq * 2 * Math.PI / ((double) ctx.sampleRate));
    }
    
    @Override
    public double avgSampleAt(SamplerContext ctx, long index) {
        return sampleAt(ctx, index, 0);
    }
    
    @Override
    public void resetThisNode() {
        frequency.resetInputNode();
    }
    
}
