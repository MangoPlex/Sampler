package xyz.studiomango.sampler.nodes.effects;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;
import xyz.studiomango.sampler.parameters.Parameter;

public class Biquad extends Node {
    
    private SamplerContext ctx;

    public final Parameter frequency = new Parameter(440.0);
    public final Parameter gain = new Parameter(1);
    public final Parameter Q = new Parameter(1);

    public static final int LOWPASS = 0;
    public static final int HIGHPASS = 1;
    public static final int BANDPASS = 2;
    public static final int PEAK = 3;
    public static final int NOTCH = 4;
    public static final int LOWSHELF = 5;
    public static final int HIGHSHELF = 6;
    public int type = PEAK;
    
    public Biquad(int type) {
        this.type = type;
    }
    
    public Biquad() {}
    
    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {
        if (this.ctx == null) this.ctx = ctx;
        configurate(frequency.valueAt(ctx, index), ctx.sampleRate, index);
        
        y = b0 * inputsSampleAt(ctx, index, channelNo) + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2;
        x2 = x1;
        x1 = inputsSampleAt(ctx, index, channelNo);
        y2 = y1;
        y1 = y;
        return (y);
    }
    
    @Override
    public void resetThisNode() {
        frequency.resetInputNode();
        gain.resetInputNode();
        Q.resetInputNode();
    }
    
    double a0, a1, a2, b0, b1, b2;
    double x1, x2, y, y1, y2;
    
    public void configurate(double freq, double sampleRate, long sampleIndex) {
        x1 = x2 = y1 = y2 = 0;
        reconfigurate(freq, sampleRate, sampleIndex);
    }
    
    public void reconfigurate(double freq, double sampleRate, long sampleIndex) {
        double omega = 2 * Math.PI * freq / sampleRate;
        double sn = Math.sin(omega);
        double cs = Math.cos(omega);
        double alpha = sn / (2 * Q.valueAt(ctx, sampleIndex));
        double beta = gain.valueAt(ctx, sampleIndex) * 2;
        
        switch (type) {
        case BANDPASS:
            b0 = alpha;
            b1 = 0;
            b2 = -alpha;
            a0 = 1 + alpha;
            a1 = -2 * cs;
            a2 = 1 - alpha;
            break;
        case LOWPASS:
            b0 = (1 - cs) / 2;
            b1 = 1 - cs;
            b2 = (1 - cs) / 2;
            a0 = 1 + alpha;
            a1 = -2 * cs;
            a2 = 1 - alpha;
            break;
        case HIGHPASS:
            b0 = (1 + cs) / 2;
            b1 = -(1 + cs);
            b2 = (1 + cs) / 2;
            a0 = 1 + alpha;
            a1 = -2 * cs;
            a2 = 1 - alpha;
            break;
        case NOTCH:
            b0 = 1;
            b1 = -2 * cs;
            b2 = 1;
            a0 = 1 + alpha;
            a1 = -2 * cs;
            a2 = 1 - alpha;
            break;
        case PEAK:
            b0 = 1 + (alpha * gain.valueAt(ctx, sampleIndex));
            b1 = -2 * cs;
            b2 = 1 - (alpha * gain.valueAt(ctx, sampleIndex));
            a0 = 1 + (alpha / gain.valueAt(ctx, sampleIndex));
            a1 = -2 * cs;
            a2 = 1 - (alpha / gain.valueAt(ctx, sampleIndex));
            break;
        case LOWSHELF:
            b0 = gain.valueAt(ctx, sampleIndex) * ((gain.valueAt(ctx, sampleIndex) + 1) - (gain.valueAt(ctx, sampleIndex) - 1) * cs + beta * sn);
            b1 = 2 * gain.valueAt(ctx, sampleIndex) * ((gain.valueAt(ctx, sampleIndex) - 1) - (gain.valueAt(ctx, sampleIndex) + 1) * cs);
            b2 = gain.valueAt(ctx, sampleIndex) * ((gain.valueAt(ctx, sampleIndex) + 1) - (gain.valueAt(ctx, sampleIndex) - 1) * cs - beta * sn);
            a0 = (gain.valueAt(ctx, sampleIndex) + 1) + (gain.valueAt(ctx, sampleIndex) - 1) * cs + beta * sn;
            a1 = -2 * ((gain.valueAt(ctx, sampleIndex) - 1) + (gain.valueAt(ctx, sampleIndex) + 1) * cs);
            a2 = (gain.valueAt(ctx, sampleIndex) + 1) + (gain.valueAt(ctx, sampleIndex) - 1) * cs - beta * sn;
            break;
        case HIGHSHELF:
            b0 = gain.valueAt(ctx, sampleIndex) * ((gain.valueAt(ctx, sampleIndex) + 1) + (gain.valueAt(ctx, sampleIndex) - 1) * cs + beta * sn);
            b1 = -2 * gain.valueAt(ctx, sampleIndex) * ((gain.valueAt(ctx, sampleIndex) - 1) + (gain.valueAt(ctx, sampleIndex) + 1) * cs);
            b2 = gain.valueAt(ctx, sampleIndex) * ((gain.valueAt(ctx, sampleIndex) + 1) + (gain.valueAt(ctx, sampleIndex) - 1) * cs - beta * sn);
            a0 = (gain.valueAt(ctx, sampleIndex) + 1) - (gain.valueAt(ctx, sampleIndex) - 1) * cs + beta * sn;
            a1 = 2 * ((gain.valueAt(ctx, sampleIndex) - 1) - (gain.valueAt(ctx, sampleIndex) + 1) * cs);
            a2 = (gain.valueAt(ctx, sampleIndex) + 1) - (gain.valueAt(ctx, sampleIndex) - 1) * cs - beta * sn;
            break;
        }
        
        b0 /= a0;
        b1 /= a0;
        b2 /= a0;
        a1 /= a0;
        a2 /= a0;
    }

}
