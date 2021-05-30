package xyz.studiomango.sampler.test;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.buffers.AudioBuffer;
import xyz.studiomango.sampler.nodes.generators.Resampling;
import xyz.studiomango.sampler.nodes.generators.Player;
import xyz.studiomango.sampler.nodes.outputs.TargetDataLineNode;

public class SpeakerTest {

    public static void main(String[] args) {
        SamplerContext ctx = new SamplerContext();
        AudioBuffer input1 = new AudioBuffer(new File("clueless.wav"));
        
        Player player = new Player(input1);
        //DeepFry fry = new DeepFry();
        Resampling shift = new Resampling(1024);
        //Speaker speaker = new Speaker(ctx);
        TargetDataLineNode out = new TargetDataLineNode(ctx, 44100 * 10);
        
        player.connectTo(shift);
        shift.connectTo(out);
        
        player.sampleSpeed = 1.0;
        shift.scale.value = 1.25;
        
        System.out.println("Playing...");
        //speaker.nextSeconds(9999);
        //speaker.closeSpeaker();
        AudioInputStream stream = new AudioInputStream(out);
        try {
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, new File("output.wav"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Speaker closed!");
    }

}
