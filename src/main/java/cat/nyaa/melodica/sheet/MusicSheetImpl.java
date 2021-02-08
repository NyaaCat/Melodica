package cat.nyaa.melodica.sheet;

import cat.nyaa.melodica.api.IMusicSheet;
import cat.nyaa.melodica.api.ISheetRecord;
import cat.nyaa.melodica.api.ITrack;
import org.bukkit.Sound;

import java.util.*;

public class MusicSheetImpl implements IMusicSheet {
    @Serializable
    Map<String, ITrack> sheetStructure;
    @Serializable
    Map<Integer, ITrack> compiledSheet;

    private boolean compiled = false;
    private boolean compiling = false;
    private int modCount = 0;
    private int lastCompiled = 0;

    public MusicSheetImpl() {
        sheetStructure = new LinkedHashMap<>();
    }

    @Override
    public String addTrack(ITrack sheetRecord) {
        String key = getNewTrackName();
        sheetStructure.put(key, sheetRecord);
        return key;
    }

    private String getNewTrackName() {
        int size = sheetStructure.size();
        String key = String.valueOf(size);
        return key;
    }

    @Override
    public boolean addTrack(String name, ITrack sheetRecord) {
        if (sheetStructure.containsKey(name)) {
            return false;
        }
        sheetStructure.put(name, sheetRecord);
        return true;
    }

    @Override
    public ITrack getNotesForTick(int tick) {
        ensureCompiled();
        return compiledSheet.getOrDefault(tick, new TrackImpl(getNewTrackName()));
    }

    @Override
    public int getLength() {
        return compiledSheet.keySet().stream().max(Comparator.comparingInt(Integer::intValue)).orElse(0);
    }

    @Override
    public int getTickInteval() {
        return 1;
    }

    private void ensureCompiled() {
        if (compiling) {
            synchronized (this) {
                if (compiling) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (!compiled || lastCompiled != modCount) {
            compile();
        }
    }

    public void compile() {
        synchronized (this) {
            try {
                compiling = true;
                TreeMap<Integer, ITrack> result = new TreeMap<>();
                int compileVersion = modCount;
                sheetStructure.values().stream()
                        .flatMap(iTrack -> iTrack.getSheetRecords().stream())
                        .forEach(sheetRecord -> {
                            int tick = sheetRecord.getTick();
                            ITrack iSheetRecords = result.computeIfAbsent(tick, integer -> new TrackImpl(getNewTrackName()));
                            iSheetRecords.getSheetRecords().add(sheetRecord);
                        });
                this.lastCompiled = compileVersion;
                this.compiledSheet = result;
                this.compiled = true;
            } finally {
                compiling = false;
                this.notifyAll();
            }
        }
    }

    public static class Builder {
        private Map<String, ITrack> tracks;

        public Builder() {
            tracks = new LinkedHashMap<>();
        }

        public Builder add(ISheetRecord record) {
            Sound sound = record.getNote().getSound();
            String name = sound.name();
            ITrack iSheetRecords = tracks.computeIfAbsent(name, s -> new TrackImpl(build().getNewTrackName()));
            iSheetRecords.getSheetRecords().add(record);
            return this;
        }

        private String getNewTrackName() {
            int size = tracks.size();
            String key = String.valueOf(size);
            return key;
        }

        public MusicSheetImpl build() {
            MusicSheetImpl musicSheet = new MusicSheetImpl();
            musicSheet.sheetStructure = tracks;
            return musicSheet;
        }
    }
}
