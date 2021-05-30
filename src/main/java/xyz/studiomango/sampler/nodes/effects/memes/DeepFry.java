package xyz.studiomango.sampler.nodes.effects.memes;

import xyz.studiomango.sampler.nodes.effects.FFTEqualizer;

public class DeepFry extends FFTEqualizer {
    
    public DeepFry() {
        super(256, 16);
        
        sliders[0].value = 3;
        sliders[1].value = 2.75;
        sliders[2].value = 1.5;
        sliders[3].value = 1;
        sliders[4].value = 1;
        sliders[5].value = 1;
    }
    
}
