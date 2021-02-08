package cat.nyaa.melodica.loader;

import cat.nyaa.melodica.MelodicaPlugin;
import cat.nyaa.melodica.api.ISheetLoader;
import cat.nyaa.melodica.api.IMusicSheet;
import cat.nyaa.melodica.sheet.MusicSheetImpl;
import cat.nyaa.melodica.sheet.NoteImpl;
import cat.nyaa.melodica.sheet.SheetRecord;
import cat.nyaa.melodica.utils.NBSDecoder;
import org.bukkit.Sound;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainSheetLoader implements ISheetLoader {
    static File sheetDir = new File(MelodicaPlugin.plugin.getDataFolder(), "sheets");

    public static IMusicSheet loadFromFile(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String string;
        MusicSheetImpl.Builder builder = new MusicSheetImpl.Builder();
        while ((string = bufferedReader.readLine()) != null) {
            try {
                String[] split = string.split("\t");
                String s = split[0];
                String s1 = split[1];
                String s2 = split[2];
                String s3 = split[3];
                int tick = Integer.parseInt(s);
                s1 = s1.toUpperCase();
                Sound sound = Sound.valueOf("BLOCK_NOTE_BLOCK_" + s1);
                float volume = Float.parseFloat(s2);
                float pitch = Float.parseFloat(s3);
                NoteImpl note = new NoteImpl(sound, volume, pitch);
                SheetRecord record = new SheetRecord(tick, note);
                builder.add(record);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        MusicSheetImpl build = builder.build();
        build.compile();
        return build;
    }

    public static Map<String, IMusicSheet> loadDir() {
        if (!sheetDir.exists()) {
            sheetDir.mkdirs();
        }
        Map<String, IMusicSheet> result = new HashMap<>();
        File[] files = sheetDir.listFiles();
        if (files != null && files.length>0){
            for (File file : files) {
                if (file.getName().endsWith(".sheet")) {
                    try {
                        IMusicSheet sheet = loadFromFile(file);
                        result.put(file.getName().substring(0, file.getName().lastIndexOf(".")), sheet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (file.getName().endsWith(".nbs")){
                    IMusicSheet parse = NBSDecoder.parse(file);
                    result.put(file.getName().substring(0, file.getName().lastIndexOf(".")), parse);
                }
            }
        }
        return result;
    }

    public static void registerLoader(String s, ISheetLoader loaderNBS) {
        //todo
    }


    @Override
    public IMusicSheet load(String name) {
        try{
            return loadFromFile(new File(sheetDir, name));
        }catch (IOException e){
            throw new RuntimeException("error loading file"+name);
        }
    }

    @Override
    public File getDirectory() {
        return sheetDir;
    }
}
