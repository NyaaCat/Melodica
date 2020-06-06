package cat.nyaa.melodica.sheet;

import cat.nyaa.melodica.api.INote;
import org.bukkit.Sound;

public class NoteImpl implements INote {
    @Serializable
    private Sound sound = Sound.BLOCK_NOTE_BLOCK_HARP;
    @Serializable
    private float volume;
    @Serializable
    private float pitch;
    @Serializable
    private double panning = 0;

    public NoteImpl(Sound sound, float volume, float pitch){
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public NoteImpl(Sound sound, float volume, float pitch, double panning){
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.panning = panning;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public double getPanning() {
        return panning;
    }

    @Override
    public Sound getSound() {
        return sound;
    }
}
