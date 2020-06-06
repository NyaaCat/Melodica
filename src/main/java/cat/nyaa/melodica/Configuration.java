package cat.nyaa.melodica;

import cat.nyaa.nyaacore.configuration.PluginConfigure;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration extends PluginConfigure {
    @Serializable
    public String language = "en_US";

    @Override
    protected JavaPlugin getPlugin() {
        return MelodicaPlugin.plugin;
    }
}
