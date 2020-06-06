package cat.nyaa.melodica.api;

import cat.nyaa.nyaacore.configuration.ISerializable;

public interface ISheetRecord extends ISerializable {
    int getTick();
    INote getNote();
}
