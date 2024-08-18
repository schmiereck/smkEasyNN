package de.schmiereck.smkEasyNN.engineWorld;

public class FPSCounter {
    private long startTime;
    private long frameCount;
    private long lastFps = 0;

    public FPSCounter() {
        this.startTime = System.currentTimeMillis();
        this.frameCount = 0;
    }

    public void frameRendered() {
        this.frameCount++;
        if ((this.lastFps == 0) || (this.frameCount % this.lastFps == 0)) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - this.startTime;
            if (elapsedTime > 1000) {
                this.lastFps = (this.frameCount * 1000) / elapsedTime;
                this.startTime = currentTime;
                this.frameCount = 0;
            }
        }
    }

    public long getFPS() {
        return this.lastFps;
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.frameCount = 0;
    }
}
