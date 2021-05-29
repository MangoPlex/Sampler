package xyz.studiomango.sampler.nodes.effects;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;
import xyz.studiomango.sampler.parameters.Parameter;

public class Gain extends Node {
    
    public final Parameter gain = new Parameter(1.0);

    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {
        return inputsSampleAt(ctx, index, channelNo) * gain.valueAt(ctx, index);
    }

    @Override
    public void resetThisNode() {
        gain.resetInputNode();
    }

}
