package cat.nyaa.melodica.api;

import cat.nyaa.nyaacore.configuration.ISerializable;

import java.util.Collection;

public interface IMusicSheet extends ISerializable {
    String addTrack(ITrack sheetRecord);

    boolean addTrack(String name, ITrack sheetRecord);

    ITrack getNotesForTick(int tick);

    int getLength();

    int getTickInteval();
}
