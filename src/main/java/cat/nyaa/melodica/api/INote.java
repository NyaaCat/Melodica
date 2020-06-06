package cat.nyaa.melodica.api;

import cat.nyaa.nyaacore.configuration.ISerializable;
import org.bukkit.Sound;

public interface INote extends ISerializable {
    float getVolume();
    float getPitch();
    double getPanning();
    Sound getSound();

}
