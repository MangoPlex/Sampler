package xyz.studiomango.sampler.test;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.generators.Oscillator;
import xyz.studiomango.sampler.nodes.outputs.Speaker;

public class SpeakerTest {

    public static void main(String[] args) {
        SamplerContext ctx = new SamplerContext();
        
        Oscillator triangle = new Oscillator(Oscillator.NOISE);
        Speaker out = new Speaker(ctx);
        
        triangle.connectTo(out);
        
        System.out.println("Playing...");
        out.nextSeconds(10);
        System.out.println("Speaker closed");
    }

}
