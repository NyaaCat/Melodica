package cat.nyaa.melodica.api;

import cat.nyaa.nyaacore.configuration.ISerializable;

import java.util.Collection;

public interface IMusicSheet extends ISerializable {
    String addTrack(Collection<ISheetRecord> sheetRecord);

    boolean addTrack(String name, Collection<ISheetRecord> sheetRecord);

    Collection<ISheetRecord> getNotesForTick(int tick);

    int getLength();

    int getTickInteval();
}
