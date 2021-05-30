package xyz.studiomango.sampler.nodes;

import java.util.ArrayList;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.parameters.Parameter;

public abstract class Node {
    
    public final ArrayList<Node> inputNodes = new ArrayList<>();
    
    public abstract double sampleAt(SamplerContext ctx, long index, int channelNo);
    public abstract void resetThisNode();
    
    public void connectTo(Node node) {
        if (node == this) throw new IllegalArgumentException("Recursive node not allowed. See BufferedNode");
        node.inputNodes.add(this);
    }
    public void connectTo(Parameter param) {
        param.inputNode = this;
    }
    
    public double inputsSampleAt(SamplerContext ctx, long index, int channelNo) {
        double sample = 0;
        for (Node n : inputNodes) sample += n.sampleAt(ctx, index, channelNo);
        return sample;
    }
    
    public double avgSampleAt(SamplerContext ctx, long index) {
        double avg = 0;
        for (int i = 0; i < ctx.channels; i++) avg += sampleAt(ctx, index, i);
        return avg / ctx.channels;
    }
    
    public void resetNode() {
        resetThisNode();
        inputNodes.forEach(n -> n.resetNode());
    }
    
}
