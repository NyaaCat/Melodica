package cat.nyaa.melodica.api;

import java.util.List;

public interface ITrack {
    String getName();
    void setName(String name);
    List<ISheetRecord> getSheetRecords();

    void setVolume(byte volume);

    void setPanning(int panning);
}
