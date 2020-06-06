package cat.nyaa.melodica.api;

public interface IMusicTask {
    TaskStatus getStatus();
    boolean isLoop();

    void stop();
    void start();
    void start(int maxTick);
    void loop(boolean willLoop);
}
