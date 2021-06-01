package xyz.studiomango.sampler.test;

import java.io.File;

import xyz.studiomango.sampler.Sampler;
import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.buffers.AudioBuffer;
import xyz.studiomango.sampler.nodes.effects.fourier.PitchShifting;
import xyz.studiomango.sampler.nodes.generators.Player;
import xyz.studiomango.sampler.nodes.outputs.Speaker;

public class SpeakerTest {

    public static void main(String[] args) {
        SamplerContext ctx = new SamplerContext();
        AudioBuffer buffer = new AudioBuffer(new File("audio.wav"));
        
        Player player = new Player(buffer);
        PitchShifting pitchShift = new PitchShifting(256, 2);
        Speaker speaker = new Speaker(ctx);
        
        Sampler.connectChain(
            player,
            pitchShift,
            speaker
        );
        
        System.out.println("Playing...");
        speaker.nextSeconds(9999);
    }

}
