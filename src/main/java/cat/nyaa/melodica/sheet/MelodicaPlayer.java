package cat.nyaa.melodica.sheet;

import cat.nyaa.melodica.api.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class MelodicaPlayer {
    public MelodicaPlayer() {

    }

    public void submit(PlayInfo sheet) {

    }

    public IMusicTask play(PlayInfo sheet, Location location) {
        PlayTask playTask = new PlayTask(sheet, location);
        playTask.start();
        return playTask;
    }


    public IMusicTask play(PlayInfo sheet, Entity entity) {
        PlayTask playTask = new PlayTask(sheet, entity);
        playTask.start();
        return playTask;
    }

    public static class PlayTask extends BukkitRunnable implements IMusicTask{
        private final PlayInfo info;
        private IMusicSheet iMusicSheet;
        private int finishTick;
        private int currentTick = 0;
        private Location location;
        private Entity entity;
        private TaskStatus status = TaskStatus.IDLE;

        public PlayTask(PlayInfo iMusicSheet, Location location) {
            this.info = iMusicSheet;
            this.finishTick = info.getPlayTime();
            this.location = location;
        }

        public PlayTask(PlayInfo iMusicSheet, Entity entity) {
            this.info = iMusicSheet;
            this.finishTick = info.getPlayTime();
            this.entity = entity;
        }

        @Override
        public void run() {
            if (status == TaskStatus.PAUSED){
                return;
            }
            iMusicSheet = info.getSheet();
            try {
                Collection<ISheetRecord> notesForTick = iMusicSheet.getNotesForTick(currentTick);
                Location sourceLocation;
                if (location == null){
                    sourceLocation = entity.getLocation();
                }else {
                    sourceLocation = location;
                }
                World world = sourceLocation.getWorld();
                notesForTick.forEach(sheetRecord -> {
                    INote note = sheetRecord.getNote();

                    if (world!=null){
                        world.playSound(sourceLocation, note.getSound(), note.getVolume(), note.getPitch());
                    }
                });
                currentTick++;
                if (currentTick > finishTick) {
                    this.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel();
            }
        }

        @Override
        public PlayInfo getPlayInfo() {
            return info;
        }

        @Override
        public TaskStatus getStatus() {
            return status;
        }

        @Override
        public void stop() {
            this.cancel();
        }

        @Override
        public void pause() {
            status = TaskStatus.PAUSED;
        }

        @Override
        public void start() {
            status = TaskStatus.PLAYING;
        }

        @Override
        public void reset() {
            currentTick = 0;
        }

        @Override
        public void loop(boolean willLoop) {
            info.setLoop(willLoop);
        }
    }
}
