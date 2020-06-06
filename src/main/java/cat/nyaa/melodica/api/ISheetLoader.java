package cat.nyaa.melodica.api;

import java.io.File;

public interface ISheetLoader {
    IMusicSheet load(String name);

    File getDirectory();

}
