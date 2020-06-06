package cat.nyaa.melodica.api;

public interface IMusicTask {
    PlayInfo getPlayInfo();
    TaskStatus getStatus();

    void stop();
    void pause();
    void start();
    void reset();
    void loop(boolean willLoop);
}
