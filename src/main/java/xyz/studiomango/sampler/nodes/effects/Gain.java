package xyz.studiomango.sampler.nodes.effects;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;
import xyz.studiomango.sampler.parameters.Parameter;

/**
 * Percentage audio gain. This node will scale the PCM data from -value to +value, based on {@link #gain} parameter
 * @author nahkd
 *
 */
public class Gain extends Node {
    
    /**
     * The gain parameter. The default value is 1.0, which is 100% of the input
     */
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
