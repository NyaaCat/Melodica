package cat.nyaa.melodica;

import cat.nyaa.melodica.api.IMusicSheet;
import cat.nyaa.melodica.api.IMusicTask;
import cat.nyaa.melodica.api.MelodicaAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class MelodicaPlugin extends JavaPlugin implements MelodicaAPI {
    public static MelodicaPlugin plugin;

    public Configuration mainConfig;

    @Override
    public void onEnable() {
        super.onEnable();
        plugin = this;
        mainConfig = new Configuration();
        mainConfig.load();
    }

    @Override
    public IMusicTask play(IMusicSheet musicSheet, Location location) {
        //todo
        return null;
    }

    @Override
    public IMusicTask play(IMusicSheet musicSheet, Entity entity) {
        //todo
        return null;
    }
}
