package xyz.studiomango.sampler.nodes.outputs;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;

public class Speaker extends Node {

    public SourceDataLine dataLine;
    public final SamplerContext ctx;
    
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
    
    private byte[] channelsBuffer = new byte[2];
    public void nextSamples(int count) {
        int ch;
        
        while (count > 0) {
            for (ch = 0; ch < ctx.channels; ch++) channelsBuffer[ch] = (byte)(inputsSampleAt(ctx, currentSample, ch) * 127D);
            dataLine.write(channelsBuffer, 0, channelsBuffer.length);
            
            count--;
            currentSample++;
        }
    }

    @Override
    public void resetThisNode() {
    }
    
    public void closeSpeaker() {
        dataLine.drain();
        dataLine.close();
    }

}
