package cat.nyaa.melodica;

import cat.nyaa.melodica.api.IMusicSheet;
import cat.nyaa.melodica.loader.MainSheetLoader;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class SheetManager {
    private static SheetManager INSTANCE;
    public static SheetManager getInstance() {
        if (INSTANCE == null) {
            synchronized (SheetManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SheetManager();
                }
            }
        }
        return INSTANCE;
    }

    private Map<String, IMusicSheet> sheetByName = new TreeMap<>();

    private SheetManager(){}

    public void load(){
        sheetByName.clear();
        sheetByName.putAll(MainSheetLoader.loadDir());
    }
    
    public Collection<? extends String> getSheets() {
        return sheetByName.keySet();
    }

    public IMusicSheet getSheet(String sheetName) {
        return sheetByName.get(sheetName);
    }
}
