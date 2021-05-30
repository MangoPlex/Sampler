package xyz.studiomango.sampler.nodes.generators;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;
import xyz.studiomango.sampler.parameters.Parameter;

/**
 * The oscillator generator node. This node generate audio signal based on given frequency (see {@link #frequency} parameter)
 * @author nahkd
 *
 */
public class Oscillator extends Node {

    /**
     * Sine wave. This is the default type when you create new oscillator without constructor argument
     */
    public static final int SINE = 0;
    
    /**
     * Triangle wave.
     */
    public static final int TRIANGLE = 1;
    
    /**
     * Saw tooth wave.
     */
    public static final int SAW_TOOTH = 2;
    
    /**
     * Square wave. This wave is based on sine wave.
     */
    public static final int SQUARE = 3;
    
    /**
     * Noise. This will uses {@link Math#random()} to generate noise
     */
    public static final int NOISE = 4;
    
    /**
     * Oscillator frequency. The frequency can be controlled either by setting the value (see {@link Parameter#value}) or
     * connect an audio node to it (see {@link Node#connectTo(Parameter)}
     */
    public final Parameter frequency = new Parameter(440.0);
    
    /**
     * Oscillator type. You can alter it while it's running, but the quality will be affected (click sounds for example)
     * @see #SINE
     * @see #TRIANGLE
     * @see #SAW_TOOTH
     * @see #SQUARE
     * @see #NOISE
     */
    public int oscillatorType = SINE;
    
    /**
     * Create new oscillator with type. The type constants are inside {@link Oscillator} class and can be accessed statically
     * @param type
     * @see #SINE
     * @see #TRIANGLE
     * @see #SAW_TOOTH
     * @see #SQUARE
     * @see #NOISE
     */
    public Oscillator(int type) {
        this.oscillatorType = type;
    }
    
    /**
     * Create new oscillator. The oscillator will used sine wave by default. See {@link #Oscillator(int)}
     */
    public Oscillator() {}
    
    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {
        double freq = frequency.valueAt(ctx, index);
        return switch (oscillatorType) {
        case SINE -> Math.sin(index * freq * 2 * Math.PI / ((double) ctx.sampleRate));
        case TRIANGLE -> (2 / Math.PI) * Math.asin(Math.sin(2 * Math.PI * index * freq / ((double) ctx.sampleRate)));
        case SAW_TOOTH -> (-2 / Math.PI) * Math.atan(1 / Math.tan(index * freq / ((double) ctx.sampleRate)));
        case SQUARE -> Math.sin(index * freq * 2 * Math.PI / ((double) ctx.sampleRate)) > 0? 1 : -1;
        case NOISE -> Math.random() * 2 - 1;
        default -> 0;
        };
    }
    
    @Override
    public double avgSampleAt(SamplerContext ctx, long index) {
        return sampleAt(ctx, index, 0);
    }
    
    @Override
    public void resetThisNode() {
        frequency.resetInputNode();
    }
    
}
