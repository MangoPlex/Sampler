package xyz.studiomango.sampler.test;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.nodes.effects.Gain;
import xyz.studiomango.sampler.nodes.generators.Oscillator;
import xyz.studiomango.sampler.nodes.outputs.Speaker;

public class SpeakerTest {

    public static void main(String[] args) {
        SamplerContext ctx = new SamplerContext();
        
        Oscillator osc = new Oscillator();
        osc.frequency.value = 440.0;
        
        Oscillator osc2 = new Oscillator();
        osc.frequency.value = 2.0;
        
        Gain gain = new Gain();
        osc2.connectTo(gain.gain);
        osc.connectTo(gain);
        
        Speaker speaker = new Speaker(ctx);
        gain.connectTo(speaker);
        
        System.out.println("Playing...");
        speaker.nextSamples(44100 * 4);
        speaker.closeSpeaker();
        System.out.println("Played sample!");
    }

}
