package xyz.studiomango.sampler.nodes;

import xyz.studiomango.sampler.SamplerContext;

/**
 * Buffered node. A buffered node let you read a chunk of audio then process it. Useful for FFT processing / real time
 * pitch shifting
 * @author nahkd
 *
 */
public abstract class BufferedNode extends Node {
    
    public double[][] buffers;
    public double[] tempBufferIn;
    public final int bufferSize;
    
    public BufferedNode(int bufferSize) {
        this.bufferSize = bufferSize;
        tempBufferIn = new double[bufferSize];
    }

    private long[] previousChunkIndexes;
    
    @Override
    public double sampleAt(SamplerContext ctx, long index, int channelNo) {
        if (buffers == null) {
            buffers = new double[ctx.channels][];
            previousChunkIndexes = new long[ctx.channels];
            for (int i = 0; i < ctx.channels; i++) previousChunkIndexes[i] = -1;
        }
        if (buffers[channelNo] == null) buffers[channelNo] = new double[bufferSize];
        
        double[] in = tempBufferIn;
        double[] out = buffers[channelNo];
        long startChunk = index / bufferSize;
        long startIndex = bufferSize * startChunk;
        
        if (previousChunkIndexes[channelNo] != startChunk) {
            putToBuffer(ctx, startIndex, in, channelNo);
            processBuffer(ctx, in, out, startIndex, channelNo);
            previousChunkIndexes[channelNo] = startChunk;
        }
        
        return out[(int) (index % bufferSize)];
    }
    
    public void putToBuffer(SamplerContext ctx, long startIndex, double[] buffer, int channelNo) {
        for (int i = 0; i < buffer.length; i++) buffer[i] = inputsSampleAt(ctx, startIndex + i, channelNo);
    }
    
    public abstract void processBuffer(SamplerContext ctx, double[] bufferIn, double[] bufferOut, long startIndex, int channelNo);

}
