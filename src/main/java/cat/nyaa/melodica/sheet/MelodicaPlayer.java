package cat.nyaa.melodica.sheet;

import cat.nyaa.melodica.api.IMusicSheet;
import cat.nyaa.melodica.api.INote;
import cat.nyaa.melodica.api.ISheetRecord;
import cat.nyaa.melodica.api.PlayInfo;
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

    public void play(PlayInfo sheet, Location location) {
        new PlayTask(sheet, location);
    }


    public void play(PlayInfo sheet, Entity entity) {
        new PlayTask(sheet, entity);
    }

    public static class PlayTask extends BukkitRunnable {
        private final PlayInfo info;
        private IMusicSheet iMusicSheet;
        private int finishTick;
        private int currentTick = 0;
        private Location location;
        private Entity entity;
        private boolean stopped = false;

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
            iMusicSheet = info.getSheet();
            try {
                if (stopped){
                    this.cancel();
                    return;
                }
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
    }
}
