package xyz.studiomango.sampler.nodes.effects;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.BufferedNode;

/**
 * The effect which store audio data in buffer then read it. Basically it doesn't do anything special, more than
 * just taking memory space. We use this node for testing purpose only
 * @author nahkd
 *
 */
public class BufferedEffect extends BufferedNode {

    public BufferedEffect() {
        super(256);
    }
    
    @Override
    public void processBuffer(SamplerContext ctx, double[] bufferIn, double[] bufferOut, long startIndex, int channelNo) {
        for (int i = 0; i < 256; i++) bufferOut[i] = bufferIn[i];
    }

    @Override
    public void resetThisNode() {}

}
