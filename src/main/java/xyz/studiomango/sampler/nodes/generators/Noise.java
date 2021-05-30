package xyz.studiomango.sampler.nodes.generators;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;

public class Noise extends Node {

    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {return Math.random() * 2 - 1;}

    @Override
    public void resetThisNode() {}

}
