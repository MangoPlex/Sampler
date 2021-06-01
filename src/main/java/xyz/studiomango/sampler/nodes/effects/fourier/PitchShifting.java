package xyz.studiomango.sampler.nodes.effects.fourier;

import java.util.ArrayList;
import java.util.Arrays;

import xyz.studiomango.sampler.SamplerContext;
import xyz.studiomango.sampler.math.FFT;
import xyz.studiomango.sampler.nodes.BufferedNode;
import xyz.studiomango.sampler.nodes.effects.Resampling;

/**
 * Pitch shifting node. This node will uses Fourier Transform to shift pitch in frequency domain. For time domain pitch shifting,
 * see {@link Resampling}. Some people said that this one is better than time domain method
 * @author nahkd
 *
 */
public class PitchShifting extends BufferedNode {
    
    //public final Parameter detune = new Parameter(0);
    
    /**
     * Detune value. This value can't be changed in real time, but you can try replacing it with another pitch shifting
     * node
     */
    public final double detune; // We have to find a way to edit detune in real time...
    
    /**
     * Oversampling value, act like anti-aliasing filter for audio
     */
    public final int oversampling;
    
    /**
     * Create new frequency domain pitch shifting node
     * @param samples The number of samples for FFT to analyze. Higher number can gives you higher quality result, but sometimes
     * lower number gives better result
     * @param detune Detune value in MIDI note index. Eg: 12 = 1 octave
     * @param oversampling The oversampling value (which act like anti-aliasing filter for audio). The default value is 4x
     */
    public PitchShifting(int samples, double detune, int oversampling) {
        super(samples);
        this.detune = detune;
        this.oversampling = oversampling;
    }
    
    /**
     * Create new frequency domain pitch shifting node with default oversampling value
     * @param samples The number of samples for FFT to analyze. Higher number can gives you higher quality result, but sometimes
     * lower number gives better result
     * @param detune Detune value in MIDI note index. Eg: 12 = 1 octave
     */
    public PitchShifting(int samples, double detune) {
        this(samples, detune, 4);
    }
    
    private final ArrayList<PitchShiftingChannel> channels = new ArrayList<>();
    
    @Override
    public void processBuffer(SamplerContext ctx, double[] bufferIn, double[] bufferOut, long startIndex, int channelNo) {
        if (channelNo != 0) return; // Dual channel support soon!
        double pitchScale = detuneToFactor(detune);
        while (channels.size() <= channelNo) channels.add(new PitchShiftingChannel(pitchScale, bufferIn.length, oversampling, ctx.sampleRate));
        PitchShiftingChannel channel = channels.get(channelNo);
        
        channel.smsPitchScale(bufferIn.length, bufferIn, bufferOut);
    }
    
    @Override
    public synchronized void resetThisNode() {
        channels.clear();
    }
    
    private double detuneToFactor(double detune) {return Math.pow(2, detune / 12);}
    
    private static class PitchShiftingChannel {
        
        private double[] gInFIFO;
        private double[] gOutFIFO;
        private double[] gFFTworksp;
        private double[] gLastPhase;
        private double[] gSumPhase;
        private double[] gOutputAccum;
        private double[] gAnaFreq;
        private double[] gAnaMagn;
        private double[] gSynFreq;
        private double[] gSynMagn;
        private double[] gWindow;
        
        private FFT fft;
        private int gRover;
        private double pitchScale;
        private int fftFrameSize;
        private int osamp;
        private double sampleRate;
        private int fftFrameSize2;
        private int stepSize;
        
        private double freqPerBin;
        private double expct;
        private double inFifoLatency;
        
        public PitchShiftingChannel(double pitchScale, int fftFrameSize, int osamp, double sampleRate) {
            this.pitchScale = pitchScale;
            this.fftFrameSize = fftFrameSize;
            this.osamp = osamp;
            this.sampleRate = sampleRate;
            setUpBuffers();
            computeWindow();
        }
        
        private void setUpBuffers() {
            this.fftFrameSize2 = this.fftFrameSize / 2;
            this.stepSize = this.fftFrameSize / this.osamp;
            this.freqPerBin = this.sampleRate / this.fftFrameSize;
            this.expct = 6.283185307179586D * this.stepSize / this.fftFrameSize;
            this.inFifoLatency = (this.fftFrameSize - this.stepSize);
            this.gInFIFO = new double[this.fftFrameSize];
            this.gOutFIFO = new double[this.fftFrameSize];
            this.gFFTworksp = new double[2 * this.fftFrameSize];
            this.gLastPhase = new double[this.fftFrameSize / 2];
            this.gSumPhase = new double[this.fftFrameSize / 2];
            this.gOutputAccum = new double[2 * this.fftFrameSize];
            this.gAnaFreq = new double[this.fftFrameSize];
            this.gAnaMagn = new double[this.fftFrameSize];
            this.gSynFreq = new double[this.fftFrameSize];
            this.gSynMagn = new double[this.fftFrameSize];
            this.gWindow = new double[this.fftFrameSize];
            Arrays.fill(this.gInFIFO, 0.0D);
            Arrays.fill(this.gOutFIFO, 0.0D);
            Arrays.fill(this.gFFTworksp, 0.0D);
            Arrays.fill(this.gLastPhase, 0.0D);
            Arrays.fill(this.gSumPhase, 0.0D);
            Arrays.fill(this.gOutputAccum, 0.0D);
            Arrays.fill(this.gAnaFreq, 0.0D);
            Arrays.fill(this.gAnaMagn, 0.0D);
            Arrays.fill(this.gSynFreq, 0.0D);
            Arrays.fill(this.gSynMagn, 0.0D);
            this.fft = new FFT(this.fftFrameSize);
            this.gRover = 0;
        }
        
        private void computeWindow() {
            for (int k = 0; k < this.fftFrameSize; k++) this.gWindow[k] = -0.5D * Math.cos(6.283185307179586D * k / this.fftFrameSize) + 0.5D;
        }
        
        /*
        public void setPitchScale(double pitchScale) {
            this.pitchScale = pitchScale;
            setUpBuffers();
            computeWindow();
        }
        public void setFFTFrameSize(int fftFrameSize) {
            this.fftFrameSize = fftFrameSize;
            setUpBuffers();
            computeWindow();
        }
        public void setFFTOversampling(int osamp) {
            this.osamp = osamp;
            setUpBuffers();
            computeWindow();
        }
        */
        
        public void smsPitchScale(int numSampsToProcess, double[] indata, double[] outdata) {
            if (this.gRover == 0) this.gRover = (int)this.inFifoLatency;
            for (int i = 0; i < numSampsToProcess; i++) {
                this.gInFIFO[this.gRover] = indata[i];
                outdata[i] = this.gOutFIFO[this.gRover - (int)this.inFifoLatency] * 0.5;
                this.gRover++;
                if (this.gRover >= this.fftFrameSize) {
                    this.gRover = (int)this.inFifoLatency;
                    processFrame();
                }
            }
        }
        
        private void processFrame() {
            windowAndInterleave();
            analyze();
            process();
            synthesize();
            windowAndAccumulate();
            
            System.arraycopy(this.gOutputAccum, 0, this.gOutFIFO, 0, this.stepSize);
            System.arraycopy(this.gOutputAccum, this.stepSize, this.gOutputAccum, 0, this.fftFrameSize);
            System.arraycopy(this.gInFIFO, this.stepSize, this.gInFIFO, 0, (int)this.inFifoLatency);
        }
        
        private void windowAndInterleave() {
            for (int k = 0; k < this.fftFrameSize; k++) {
                this.gFFTworksp[2 * k] = this.gInFIFO[k] * this.gWindow[k];
                this.gFFTworksp[2 * k + 1] = 0.0D;
            }
        }
        
        private void analyze() {
            this.fft.smsFft(this.gFFTworksp, -1);
            for (int k = 0; k < this.fftFrameSize2; k++) {
                double real = this.gFFTworksp[2 * k];
                double imag = this.gFFTworksp[2 * k + 1];
                double magn = 2.0D * Math.sqrt(real * real + imag * imag);
                double phase = Math.atan2(imag, real);
                double tmp = phase - this.gLastPhase[k];
                this.gLastPhase[k] = phase;
                tmp -= k * this.expct;
                int qpd = (int)(tmp / Math.PI);
                
                if (qpd >= 0) qpd += qpd & 0x1;
                else qpd -= qpd & 0x1;
                
                tmp -= Math.PI * qpd;
                tmp = this.osamp * tmp / 6.283185307179586D;
                tmp = k * this.freqPerBin + tmp * this.freqPerBin;
                this.gAnaMagn[k] = magn;
                this.gAnaFreq[k] = tmp;
            }
        }
        
        private void process() {
            Arrays.fill(this.gSynMagn, 0, this.fftFrameSize, 0.0D);
            Arrays.fill(this.gSynFreq, 0, this.fftFrameSize, 0.0D);
            
            for (int k = 0; k <= this.fftFrameSize2; k++) {
                int index = (int)(k / this.pitchScale);
                if (index <= this.fftFrameSize2) {
                    if (this.gAnaMagn[index] > this.gSynMagn[k]) {
                        this.gSynMagn[k] = this.gAnaMagn[index];
                        this.gSynFreq[k] = this.gAnaFreq[index] * this.pitchScale;
                    }
                    
                    if (k > 0 && this.gSynFreq[k] == 0.0D) {
                        this.gSynFreq[k] = this.gSynFreq[k - 1];
                        this.gSynMagn[k] = this.gSynMagn[k - 1];
                    }
                }
            }
        }
        
        private void synthesize() {
            for (int k = 0; k < this.fftFrameSize2; k++) {
                double magn = this.gSynMagn[k];
                double tmp = this.gSynFreq[k];
                tmp = (tmp - k * this.freqPerBin) / this.freqPerBin * 2.0D * Math.PI / this.osamp + k * this.expct;
                this.gSumPhase[k] = this.gSumPhase[k] + tmp;
                double phase = this.gSumPhase[k];
                this.gFFTworksp[2 * k] = magn * Math.cos(phase);
                this.gFFTworksp[2 * k + 1] = magn * Math.sin(phase);
            }
            
            Arrays.fill(this.gFFTworksp, this.fftFrameSize + 2, 2 * this.fftFrameSize, 0.0D);
            this.fft.smsFft(this.gFFTworksp, 1);
        }
        
        private void windowAndAccumulate() {
            for (int k = 0; k < this.fftFrameSize; k++) {
                this.gOutputAccum[k] = this.gOutputAccum[k] + 2.0D * this.gWindow[k] * this.gFFTworksp[2 * k] / (this.fftFrameSize2 * this.osamp);
            }
        }
    }
    
}
