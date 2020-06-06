package cat.nyaa.melodica.sheet;

import cat.nyaa.melodica.api.INote;
import cat.nyaa.melodica.api.ISheetRecord;

public class SheetRecord implements ISheetRecord {
    int tick;
    INote note;

    public SheetRecord(int tick, INote note) {
        this.tick = tick;
        this.note = note;
    }

    @Override
    public int getTick() {
        return tick;
    }

    @Override
    public INote getNote() {
        return note;
    }
}
