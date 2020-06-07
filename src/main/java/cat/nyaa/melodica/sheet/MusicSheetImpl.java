package cat.nyaa.melodica.sheet;

import cat.nyaa.melodica.api.IMusicSheet;
import cat.nyaa.melodica.api.ISheetRecord;
import org.bukkit.Sound;

import java.util.*;

public class MusicSheetImpl implements IMusicSheet {
    @Serializable
    Map<String, Collection<ISheetRecord>> sheetStructure;
    @Serializable
    Map<Integer, Collection<ISheetRecord>> compiledSheet;

    private boolean compiled = false;
    private boolean compiling = false;
    private int modCount = 0;
    private int lastCompiled = 0;

    public MusicSheetImpl() {
        sheetStructure = new LinkedHashMap<>();
    }

    @Override
    public String addTrack(Collection<ISheetRecord> sheetRecord) {
        int size = sheetStructure.size();
        String key = String.valueOf(size);
        sheetStructure.put(key, sheetRecord);
        return key;
    }

    @Override
    public boolean addTrack(String name, Collection<ISheetRecord> sheetRecord) {
        if (sheetStructure.containsKey(name)) {
            return false;
        }
        sheetStructure.put(name, sheetRecord);
        return true;
    }

    @Override
    public Collection<ISheetRecord> getNotesForTick(int tick) {
        ensureCompiled();
        return compiledSheet.getOrDefault(tick, Collections.emptyList());
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
                TreeMap<Integer, Collection<ISheetRecord>> result = new TreeMap<>();
                int compileVersion = modCount;
                sheetStructure.values().stream()
                        .flatMap(Collection::stream)
                        .forEach(sheetRecord -> {
                            int tick = sheetRecord.getTick();
                            Collection<ISheetRecord> iSheetRecords = result.computeIfAbsent(tick, integer -> new LinkedList<>());
                            iSheetRecords.add(sheetRecord);
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
        private Map<String, Collection<ISheetRecord>> tracks;

        public Builder() {
            tracks = new LinkedHashMap<>();
        }

        public Builder add(ISheetRecord record) {
            Sound sound = record.getNote().getSound();
            String name = sound.name();
            Collection<ISheetRecord> iSheetRecords = tracks.computeIfAbsent(name, s -> new LinkedList<>());
            iSheetRecords.add(record);
            return this;
        }

        public MusicSheetImpl build() {
            MusicSheetImpl musicSheet = new MusicSheetImpl();
            musicSheet.sheetStructure = tracks;
            return musicSheet;
        }
    }
}
