package cat.nyaa.melodica.loader.custom;

import cat.nyaa.melodica.MelodicaPlugin;
import cat.nyaa.melodica.api.ISheetLoader;
import cat.nyaa.melodica.api.IMusicSheet;
import cat.nyaa.melodica.loader.MainSheetLoader;

import java.io.File;

public class SheetLoaderNBS implements ISheetLoader {
    File sheetDir = new File(MelodicaPlugin.plugin.getDataFolder(), "sheets/nbs");

    static boolean registered = false;

    public SheetLoaderNBS(){
        if (!registered){
            MainSheetLoader.registerLoader(".nbs", this);
        }
    }

    @Override
    public IMusicSheet load(String name) {
        return null;
    }

    @Override
    public File getDirectory() {
        return sheetDir;
    }
}
