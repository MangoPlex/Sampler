package xyz.studiomango.sampler.nodes.effects;

import java.util.ArrayList;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;
import xyz.studiomango.sampler.parameters.Parameter;

/**
 * Biquad EQ filter.
 * @author nahkd
 *
 */
public class Biquad extends Node {

    private static class BiquadChannel {
        private double xn1 = 0, xn2 = 0, yn = 0, yn1 = 0, yn2 = 0;
    }
    
    /**
     * The center frequency
     */
    public final Parameter frequency = new Parameter(440.0);
    
    /**
     * The gain value
     */
    public final Parameter gain = new Parameter(1);
    
    /**
     * The Q value
     */
    public final Parameter Q = new Parameter(1);
    
    public Biquad() {}
    
    private double b0, b1, b2, a0, a1, a2;
    private double omega, sn, cs, alpha, beta;
    private final ArrayList<BiquadChannel> channels = new ArrayList<>();
    
    private void configurateBase(double sampleRate, double centerFrequency, double Q, double gain) {
        omega = 2 * Math.PI * centerFrequency / sampleRate;
        sn = Math.sin(omega);
        cs = Math.cos(omega);
        alpha = sn / (2 * Q);
        beta = Math.sqrt(gain + gain);
    }
    
    private void prescale() {
        b0 /= a0;
        b1 /= a0;
        b2 /= a0;
        a1 /= a0;
        a2 /= a0;
    }
    
    private void configuratePeak(double sampleRate, double centerFrequency, double Q, double gain) {
        configurateBase(sampleRate, centerFrequency, Q, gain);
        b0 = 1 + (alpha * gain);
        b1 = -2 * cs;
        b2 = 1 - (alpha * gain);
        a0 = 1 + (alpha / gain);
        a1 = -2 * cs;
        a2 = 1 - (alpha / gain);
        prescale();
    }
    
    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {
        configuratePeak(ctx.sampleRate, frequency.valueAt(ctx, index), Q.valueAt(ctx, index), gain.valueAt(ctx, index));
        BiquadChannel ch;
        if (channels.size() < channelNo + 1) channels.add(ch = new BiquadChannel());
        else ch = channels.get(channelNo);
        
        double xn = inputsSampleAt(ctx, index, channelNo);
        ch.yn = b0 * xn + b1 * ch.xn1 + b2 * ch.xn2 - a1 * ch.yn1 - a2 * ch.yn2;
        ch.xn2 = ch.xn1;
        ch.xn1 = xn;
        ch.yn2 = ch.yn1;
        ch.yn1 = ch.yn;
        return (ch.yn);
    }
    
    @Override
    public void resetThisNode() {
        frequency.resetInputNode();
        gain.resetInputNode();
        Q.resetInputNode();
        channels.forEach(ch -> {
            ch.xn1 = ch.xn2 = ch.yn = ch.yn1 = ch.yn2 = 0;
        });
    }

}
