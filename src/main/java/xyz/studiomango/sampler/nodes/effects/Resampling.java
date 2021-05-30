package xyz.studiomango.sampler.nodes.effects;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.BufferedNode;
import xyz.studiomango.sampler.parameters.Parameter;

/**
 * Resampling node. This node will resample a really small chunk of audio data (basically speed it up or slow it down).
 * The buffer length is so short that it's like real time pitch shifting, which might useful for some cases. (ah yes, real
 * time voice changer...) 
 * @author nahkd
 *
 */
public class Resampling extends BufferedNode {
    
    public final Parameter scale = new Parameter(1);
    
    /**
     * Create new resampling node
     * @param samples The samples count for effect buffer. You want to set is as small as possible, but you also
     * want it to be large enough to deliver good quality. For me, I would choose 1/100 of context sampling rate
     */
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
        
        // Prevent click sounds (hopefully)
        bufferOut[0] = bufferOut[0] * sampleMixing + previousSample * (1 - sampleMixing);
        previousSample = bufferOut[bufferOut.length - 1];
    }

    @Override
    public void resetThisNode() {
        previousSample = 0;
    }

}
