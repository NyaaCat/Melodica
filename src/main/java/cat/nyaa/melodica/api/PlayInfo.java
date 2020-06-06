package cat.nyaa.melodica.api;

public class PlayInfo {
    IMusicSheet sheet;
    double playSpeed = 1.0;
    double volume = 1.0;
    boolean loop = false;
    int playTime = -1;

    public IMusicSheet getSheet() {
        return sheet;
    }

    public void setSheet(IMusicSheet sheet) {
        this.sheet = sheet;
    }

    public double getPlaySpeed() {
        return playSpeed;
    }

    public void setPlaySpeed(double playSpeed) {
        this.playSpeed = playSpeed;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }
}
