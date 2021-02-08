package cat.nyaa.melodica.sheet;

import cat.nyaa.melodica.api.ISheetRecord;
import cat.nyaa.melodica.api.ITrack;

import java.util.ArrayList;
import java.util.List;

public class TrackImpl implements ITrack {
    private String name;
    private List<ISheetRecord> records = new ArrayList<>();

    public TrackImpl(String name){
        this.name = name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<ISheetRecord> getSheetRecords() {
        return records;
    }
}
