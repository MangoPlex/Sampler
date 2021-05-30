package xyz.studiomango.sampler.nodes;

import java.util.ArrayList;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.effects.Biquad;
import xyz.studiomango.sampler.parameters.Parameter;

/**
 * The base component of audio nodes system. You can also extends this and make your own node as well!. For processing
 * a small chunk of audio in real time, see {@link BufferedNode}
 * @author nahkd
 *
 */
public abstract class Node {
    
    /**
     * An array of input (or incoming) nodes
     */
    public final ArrayList<Node> inputNodes = new ArrayList<>();
    
    /**
     * Get the sample value at index.
     * @param ctx The context. Some procedurally generators might use this to generate audio correctly (such as context sample rate)
     * @param index The sample index. Every index must starts at 0 and can goes up to infinity (unless it reachs signed 64-bit value)
     * @param channelNo The channel index. Some generators might gives you different result on different channel
     * @return The sample value
     */
    public abstract double sampleAt(SamplerContext ctx, long index, int channelNo);
    
    /**
     * Reset all components inside this node only. You should only use {@link #resetNode()} to reset node (and it's children)
     */
    public abstract void resetThisNode();
    
    /**
     * Connect to another node. Recursive connections are not allowed (although you can bypass it by creating a "bridge" node)
     * @param node
     */
    public void connectTo(Node node) {
        if (node == this) throw new IllegalArgumentException("Recursive node not allowed. See BufferedNode");
        node.inputNodes.add(this);
    }
    
    /**
     * Connect to audio parameter. The audio parameter will completely ignore {@link Parameter#value} and only uses
     * value at channel #0 of the node sample output.
     * @param param
     */
    public void connectTo(Parameter param) {
        param.inputNode = this;
    }
    
    /**
     * Get the input sample value at index. Effect nodes mostly uses this to process incoming audio data
     * @param ctx
     * @param index
     * @param channelNo
     * @return
     */
    public double inputsSampleAt(SamplerContext ctx, long index, int channelNo) {
        double sample = 0;
        for (Node n : inputNodes) sample += n.sampleAt(ctx, index, channelNo);
        return sample;
    }
    
    /**
     * Get the average sample value at index. The value is the average of all values in every channels
     * @param ctx
     * @param index
     * @return
     */
    public double avgSampleAt(SamplerContext ctx, long index) {
        double avg = 0;
        for (int i = 0; i < ctx.channels; i++) avg += sampleAt(ctx, index, i);
        return avg / ctx.channels;
    }
    
    /**
     * Reset this node and it's children. Use this if you're making DAW (Digital Audio Workspace) and you
     * wanted to render audio to file (otherwise nodes like {@link Biquad} might gives you unexpected audio data)
     */
    public void resetNode() {
        resetThisNode();
        inputNodes.forEach(n -> n.resetNode());
    }
    
}
