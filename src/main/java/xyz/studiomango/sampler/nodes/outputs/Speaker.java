package xyz.studiomango.sampler.nodes.outputs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;

/**
 * The system speaker output node. This node does not produce any audio.
 * @author nahkd
 *
 */
public class Speaker extends Node {

    /**
     * The data line to write
     */
    public SourceDataLine dataLine;
    
    /**
     * The context, required for creating data line
     */
    public final SamplerContext ctx;
    
    /**
     * Create new speaker output node
     * @param ctx
     */
    public Speaker(SamplerContext ctx) {
        this.ctx = ctx;
        
        try {
            Info speakerInfo = new Info(SourceDataLine.class, ctx.getFormat());
            dataLine = (SourceDataLine) AudioSystem.getLine(speakerInfo);
            dataLine.open();
            dataLine.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {return 0;}
    
    private long currentSample;
    public long getCurrentSampleIndex() {return currentSample;}
    
    private byte[] channelsBuffer;
    
    /**
     * Play next n samples to speaker. See {@link #nextSeconds(double)} for friendlier solution
     * @param count The number of samples to read then play
     */
    public void nextSamples(long count) {
        if (channelsBuffer == null) channelsBuffer = new byte[ctx.channels * ctx.bitsDepth / 8];
        
        int ch;
        while (count > 0) {
            for (ch = 0; ch < ctx.channels; ch++) {
                double val = inputsSampleAt(ctx, currentSample, ch);
                if (ctx.bitsDepth == 8) channelsBuffer[ch] = (byte) (val * 127D);
                else if (ctx.bitsDepth == 16) ByteBuffer.wrap(channelsBuffer).order(ByteOrder.BIG_ENDIAN).putShort(ch * 2, (short) (val * 32767D));
                else if (ctx.bitsDepth == 32) ByteBuffer.wrap(channelsBuffer).order(ByteOrder.BIG_ENDIAN).putInt(ch * 4, (short) (val * 2147483647D));
            }
            dataLine.write(channelsBuffer, 0, channelsBuffer.length);
            
            count--;
            currentSample++;
        }
    }
    
    /**
     * Play next t seconds to speaker
     * @param seconds The number of seconds to read then play
     */
    public void nextSeconds(double seconds) {
        nextSamples(Math.round(ctx.sampleRate * seconds));
    }

    @Override
    public void resetThisNode() {}
    
    /**
     * Drain all enqueued audio data and close the speaker. Always call it when you no longer need speaker node
     */
    public void closeSpeaker() {
        dataLine.drain();
        dataLine.close();
    }

}
