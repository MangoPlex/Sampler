package xyz.studiomango.sampler.math;

public class FFT {
    
    public static final int FORWARD = -1;
    public static final int REVERSE = 1;
    
    private int frameSize;
    private int bits;
    private int[] flip;
    
    private double[][] forward_urs;
    private double[][] forward_uis;
    private double[][] reverse_urs;
    private double[][] reverse_uis;
    
    public FFT(int frameSize) {
        this.frameSize = frameSize;
        this.bits = (int)(Math.log(frameSize) / Math.log(2.0D));
        createBitFlipArray();
        double[][][] arrays = computeCoeff(-1);
        this.forward_urs = arrays[0];
        this.forward_uis = arrays[1];
        arrays = computeCoeff(1);
        this.reverse_urs = arrays[0];
        this.reverse_uis = arrays[1];
    }
    
    private void createBitFlipArray() {
        this.flip = new int[this.frameSize];
        for (int i = 1; i < this.frameSize - 1; i++) {
            int j = 0;
            for (int bitm = 1; bitm < this.frameSize; bitm <<= 1) {
                if ((i & bitm) != 0)
                    j++; 
                j <<= 1;
            } 
            this.flip[i] = j / 2;
        } 
    }
    
    public void smsFft(double[] fftBuffer, int sign) {
        if (sign != -1 && sign != 1)
            throw new IllegalArgumentException("invalid sign: " + sign); 
        if (sign == -1) {
            double[][] urs = this.forward_urs;
            double[][] uis = this.forward_uis;
        } else {
            double[][] urs = this.reverse_urs;
            double[][] uis = this.reverse_uis;
        } 
        for (int i = 1; i < this.frameSize - 1; i++) {
            int j = this.flip[i];
            if (i < j) {
                double temp = fftBuffer[2 * i];
                fftBuffer[2 * i] = fftBuffer[2 * j];
                fftBuffer[2 * j] = temp;
                temp = fftBuffer[2 * i + 1];
                fftBuffer[2 * i + 1] = fftBuffer[2 * j + 1];
                fftBuffer[2 * j + 1] = temp;
            } 
        } 
        for (int k = 0, le = 2; k < this.bits; k++) {
            le <<= 1;
            int le2 = le >> 1;
            double ur = 1.0D;
            double ui = 0.0D;
            double arg = Math.PI / (le2 >> 1);
            double wr = Math.cos(arg);
            double wi = sign * Math.sin(arg);
            int idx = 0;
            for (int j = 0; j < le2; j += 2) {
                int p1r = j;
                int p1i = p1r + 1;
                int p2r = p1r + le2;
                int p2i = p2r + 1;
                int m;
                for (m = j; m < 2 * this.frameSize; m += le) {
                    double d1 = fftBuffer[p2r] * ur - fftBuffer[p2i] * ui;
                    double ti = fftBuffer[p2r] * ui + fftBuffer[p2i] * ur;
                    idx++;
                    fftBuffer[p2r] = fftBuffer[p1r] - d1;
                    fftBuffer[p2i] = fftBuffer[p1i] - ti;
                    fftBuffer[p1r] = fftBuffer[p1r] + d1;
                    fftBuffer[p1i] = fftBuffer[p1i] + ti;
                    p1r += le;
                    p1i += le;
                    p2r += le;
                    p2i += le;
                } 
                double tr = ur * wr - ui * wi;
                ui = ur * wi + ui * wr;
                ur = tr;
            } 
        } 
    }
    
    private double[][][] computeCoeff(int sign) {
        double[][] urs = new double[this.bits][];
        double[][] uis = new double[this.bits][];
        int k;
        for (k = 0; k < this.bits; k++) {
            urs[k] = new double[this.frameSize / 2];
            uis[k] = new double[this.frameSize / 2];
        } 
        int le;
        for (k = 0, le = 2; k < this.bits; k++) {
            le <<= 1;
            int le2 = le >> 1;
            double ur = 1.0D;
            double ui = 0.0D;
            double arg = Math.PI / (le2 >> 1);
            double wr = Math.cos(arg);
            double wi = sign * Math.sin(arg);
            int idx = 0;
            for (int j = 0; j < le2; j += 2) {
                int p1r = j;
                int p1i = p1r + 1;
                int p2r = p1r + le2;
                int p2i = p2r + 1;
                int i;
                for (i = j; i < 2 * this.frameSize; i += le) {
                    urs[k][idx] = ur;
                    uis[k][idx] = ui;
                    idx++;
                    p1r += le;
                    p1i += le;
                    p2r += le;
                    p2i += le;
                } 
                double tr = ur * wr - ui * wi;
                ui = ur * wi + ui * wr;
                ur = tr;
            } 
        } 
        return new double[][][] { urs, uis };
    }
  }
