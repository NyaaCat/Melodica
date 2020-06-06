package cat.nyaa.melodica.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface MelodicaAPI {
    IMusicTask play(PlayInfo playInfo, Location location);
    IMusicTask play(PlayInfo playInfo, Entity entity);
}
