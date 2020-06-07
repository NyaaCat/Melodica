package cat.nyaa.melodica;

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
    public SheetManager sheetManager;
    public MelodicaCommand melodicaCommand;
    public I18n i18n;

    @Override
    public void onEnable() {
        super.onEnable();
        plugin = this;
        i18n = new I18n();
        reload();
        melodicaCommand = new MelodicaCommand(this, i18n);
        getCommand("melodica").setExecutor(melodicaCommand);

    }

    public void reload() {
        mainConfig = new Configuration();
        mainConfig.load();
        i18n.setLanguage(mainConfig.language);
        sheetManager = SheetManager.getInstance();
        sheetManager.load();
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
