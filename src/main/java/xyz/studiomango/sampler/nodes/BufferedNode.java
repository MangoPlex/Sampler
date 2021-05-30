package xyz.studiomango.sampler.nodes;

import xyz.studiomango.sampler.SamplerContext;

/**
 * Buffered node. A buffered node let you read a chunk of audio then process it. Useful for FFT processing / real time
 * pitch shifting
 * @author nahkd
 *
 */
public abstract class BufferedNode extends Node {
    
    /**
     * An array of buffers for processing. {@link BufferedNode} will reuse buffers for performance
     */
    public double[][] buffers;
    
    /**
     * The temporary input buffer, which will be reused for performance
     */
    public double[] tempBufferIn;
    
    /**
     * The processing buffer size
     */
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
    
    private void putToBuffer(SamplerContext ctx, long startIndex, double[] buffer, int channelNo) {
        for (int i = 0; i < buffer.length; i++) buffer[i] = inputsSampleAt(ctx, startIndex + i, channelNo);
    }
    
    /**
     * Process a small chunk of audio buffer. The input buffer is so short that you can achieve low latency (unless you set
     * the buffer size that's 1/2 of context sampling rate)
     * @param ctx The context
     * @param bufferIn Input buffer, contains audio data from {@link #inputsSampleAt(SamplerContext, long, int)}
     * @param bufferOut Output buffer
     * @param startIndex The start index of buffer
     * @param channelNo The channel index
     */
    public abstract void processBuffer(SamplerContext ctx, double[] bufferIn, double[] bufferOut, long startIndex, int channelNo);

}
