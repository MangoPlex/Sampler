package xyz.studiomango.sampler.nodes.effects;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.math.Complex;
import xyz.studiomango.sampler.math.FFTOld;
import xyz.studiomango.sampler.nodes.BufferedNode;
import xyz.studiomango.sampler.parameters.Parameter;

public class FFTEqualizer extends BufferedNode {
    
    public final Parameter[] sliders;
    
    public FFTEqualizer(int samples, int sliders) {
        super(samples);
        this.sliders = new Parameter[sliders];
        for (int i = 0; i < sliders; i++) this.sliders[i] = new Parameter(1);
    }

    @Override
    public void processBuffer(SamplerContext ctx, double[] bufferIn, double[] bufferOut, long startIndex, int channelNo) {
        int binPerSlider = bufferIn.length / sliders.length;
        Complex[] complx = FFTOld.fft(Complex.toComplexArray(bufferIn));
        Complex oldBin;
        for (int i = 0; i < sliders.length; i++) {
            for (int j = 0; j < binPerSlider; j++) {
                oldBin = complx[i * binPerSlider + j];
                complx[i * binPerSlider + j] = new Complex(
                    oldBin.re() * sliders[i].valueAt(ctx, startIndex),
                    oldBin.im() * sliders[i].valueAt(ctx, startIndex)
                ); 
            }
        }
        Complex.toDoubleArray(FFTOld.ifft(complx), bufferOut);
    }

    @Override
    public void resetThisNode() {}

}
