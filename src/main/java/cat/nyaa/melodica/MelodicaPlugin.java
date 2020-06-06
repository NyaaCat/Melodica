package cat.nyaa.melodica;

import cat.nyaa.melodica.api.IMusicSheet;
import cat.nyaa.melodica.api.IMusicTask;
import cat.nyaa.melodica.api.MelodicaAPI;
import cat.nyaa.melodica.api.PlayInfo;
import cat.nyaa.melodica.sheet.MelodicaPlayer;
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
    public IMusicTask play(PlayInfo playInfo, Location location) {
        return new MelodicaPlayer().play(playInfo, location);
    }

    @Override
    public IMusicTask play(PlayInfo playInfo, Entity entity) {
        return new MelodicaPlayer().play(playInfo, entity);
    }
}
