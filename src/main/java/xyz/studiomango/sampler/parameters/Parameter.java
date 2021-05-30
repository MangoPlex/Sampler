package xyz.studiomango.sampler.parameters;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;

/**
 * Node parameter. A parameter controls how audio node will output.
 * @author nahkd
 *
 */
public class Parameter {
    
    /**
     * The default value of this parameter
     */
    public double value = 0;
    
    /**
     * The audio node which controls the parameter actual value. Setting it to null will disconnect it
     */
    public Node inputNode = null;
    
    public Parameter(double value) {
        this.value = value;
    }
    
    public Parameter() {}
    
    /**
     * Get the final value
     * @param ctx
     * @param sampleIndex
     * @return
     */
    public double valueAt(SamplerContext ctx, long sampleIndex) {
        if (inputNode == null) return value;
        return inputNode.avgSampleAt(ctx, sampleIndex);
    }
    
    public double valueAtOrElipson(SamplerContext ctx, long sampleIndex) {
        return Math.max(1e-9, valueAt(ctx, sampleIndex));
    }
    
    public void resetInputNode() {
        if (inputNode == null) return;
        inputNode.resetNode();
    }
    
}
