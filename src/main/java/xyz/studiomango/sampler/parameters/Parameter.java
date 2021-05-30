package xyz.studiomango.sampler.parameters;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.Node;

public class Parameter {
    
    public double value = 0;
    public Node inputNode = null;
    
    public Parameter(double value) {
        this.value = value;
    }
    
    public Parameter() {}
    
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
