package xyz.studiomango.sampler.nodes.outputs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;

public class TargetDataLineNode extends Node implements TargetDataLine {

    private SamplerContext ctx;
    public final int samples;
    
    private int currentSample = 0;
    
    public TargetDataLineNode(SamplerContext ctx, int samples) {
        this.ctx = ctx;
        this.samples = samples;
    }
    
    @Override
    public void drain() {}
    @Override
    public void flush() {}
    @Override
    public void start() {}
    @Override
    public void stop() {}
    @Override
    public boolean isRunning() {return true;}
    @Override
    public boolean isActive() {return true;}

    @Override
    public AudioFormat getFormat() {return ctx.getFormat();}

    @Override
    public int getBufferSize() {return 0;}
    @Override
    public int available() {return ctx.bitsDepth / 8 * ctx.channels * (samples - currentSample);}

    @Override
    public int getFramePosition() {return 0;}
    @Override
    public long getLongFramePosition() {return 0;}
    @Override
    public long getMicrosecondPosition() {return 0;}
    @Override
    public float getLevel() {return 0;}
    @Override
    public javax.sound.sampled.Line.Info getLineInfo() {return null;}
    
    @Override
    public void open() throws LineUnavailableException {}
    @Override
    public void close() {}
    @Override
    public boolean isOpen() {return true;}

    @Override
    public Control[] getControls() {return new Control[0];}
    @Override
    public boolean isControlSupported(Type control) {return false;}
    @Override
    public Control getControl(Type control) {return null;}

    @Override
    public void addLineListener(LineListener listener) {}
    @Override
    public void removeLineListener(LineListener listener) {}
    @Override
    public void open(AudioFormat format, int bufferSize) throws LineUnavailableException {}
    @Override
    public void open(AudioFormat format) throws LineUnavailableException {}

    @Override
    public int read(byte[] b, int off, int len) {
        if (currentSample >= samples) {
            return -1;
        }
        
        double v;
        for (int i = 0; i < ctx.channels; i++) {
            v = inputsSampleAt(ctx, currentSample, i);
            if (ctx.bitsDepth == 8) b[off + i] = (byte) (v * 127D);
            if (ctx.bitsDepth == 16) ByteBuffer.wrap(b).order(ByteOrder.BIG_ENDIAN).putShort(off + i * 2, (short) (v * 32767D));
            if (ctx.bitsDepth == 32) ByteBuffer.wrap(b).order(ByteOrder.BIG_ENDIAN).putInt(off + i * 4, (int) (v * 2147483647D));
        }
        
        currentSample++;
        return ctx.bitsDepth / 8 * ctx.channels;
    }

    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {return 0;}
    @Override
    public void resetThisNode() {}

}
