package xyz.studiomango.sampler.nodes.generators;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.BufferedNode;
import xyz.studiomango.sampler.parameters.Parameter;

public class Resampling extends BufferedNode {
    
    public final Parameter scale = new Parameter(1);
    
    public Resampling(int samples) {
        super(samples);
    }
    
    private double previousSample = 0;
    private double sampleMixing = 0.5;

    @Override
    public void processBuffer(SamplerContext ctx, double[] bufferIn, double[] bufferOut, long startIndex, int channelNo) {
        for (int i = 0; i < bufferOut.length; i++) {
            bufferOut[i] = bufferIn[(int) (Math.round(i * scale.valueAtOrElipson(ctx, startIndex)) % bufferIn.length)];
        }
        
        // Prevent click sounds
        bufferOut[0] = bufferOut[0] * sampleMixing + previousSample * (1 - sampleMixing);
        previousSample = bufferOut[bufferOut.length - 1];
    }

    @Override
    public void resetThisNode() {
        previousSample = 0;
    }

}
