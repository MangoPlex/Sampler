package xyz.studiomango.sampler.nodes.generators;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.BufferedNode;

public class Microphone extends BufferedNode {

    private TargetDataLine line;
    private byte[] buffer;
    private ByteBuffer bufferWrap;
    private long sampleIndex = -1;
    private int bufferSize;
    private double[][] internalBuffer;
    
    public Microphone(SamplerContext ctx, int bufferSize) {
        super(bufferSize);
        try {
            line = AudioSystem.getTargetDataLine(ctx.getFormat());
            line.open();
            line.start();
            
            buffer = new byte[ctx.bitsDepth / 8 * ctx.channels * bufferSize];
            bufferWrap = ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN);
            
            this.bufferSize = bufferSize;
            internalBuffer = new double[ctx.channels][bufferSize];
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void processBuffer(SamplerContext ctx, double[] bufferIn, double[] bufferOut, long startIndex, int channelNo) {
        if (sampleIndex != startIndex) {
            sampleIndex = startIndex;
            int bytesRead = 0;
            while (bytesRead < buffer.length) bytesRead += line.read(buffer, bytesRead, buffer.length - bytesRead);
            
            // Process raw input
            // The binary buffer frame: (Sample 0 [Channel A][Channel B]) (Sample 1...) ...
            for (int i = 0; i < bufferSize; i++) for (int ch = 0; ch < ctx.channels; ch++) {
                if (ctx.bitsDepth == 8) internalBuffer[ch][i] = buffer[i * ctx.channels + ch] / 127D;
                if (ctx.bitsDepth == 16) internalBuffer[ch][i] = bufferWrap.getShort((i * ctx.channels + ch) * 2) / 32767D;
                if (ctx.bitsDepth == 32) internalBuffer[ch][i] = bufferWrap.getShort((i * ctx.channels + ch) * 4) / 2147483647D;
            }
        }
        System.arraycopy(internalBuffer[channelNo], 0, bufferOut, 0, bufferSize);
    }
    
    @Override
    public void resetThisNode() {
    }
    
    public void closeMicrophone() {
        line.stop();
        line.close();
    }

}
