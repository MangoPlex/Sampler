package xyz.studiomango.sampler.nodes.generators;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;

/**
 * The noise node. This node does nothing beside from generating random values.
 * @author nahkd
 *
 * @deprecated Please use {@link Oscillator} instead (with the type {@link Oscillator#NOISE})
 */
@Deprecated
public class Noise extends Node {

    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {return Math.random() * 2 - 1;}

    @Override
    public void resetThisNode() {}

}
