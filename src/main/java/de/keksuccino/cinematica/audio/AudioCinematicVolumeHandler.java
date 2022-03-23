package de.keksuccino.cinematica.audio;

import de.keksuccino.auudio.audio.AudioClip;
import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;
import de.keksuccino.konkrete.properties.PropertiesSerializer;
import de.keksuccino.konkrete.properties.PropertiesSet;

import java.io.File;
import java.util.List;

public class AudioCinematicVolumeHandler {

    protected static final File PROPS_FILE = new File(Cinematica.MOD_DIRECTORY.getPath() + "/audio_volume.properties");

    protected static int volume = 100;

    public static void init() {

        try {

            if (!PROPS_FILE.isFile()) {
                PROPS_FILE.createNewFile();
                writeToFile();
            }

            readFromFile();

            updateVolume();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected static void writeToFile() {

        try {

            PropertiesSet set = new PropertiesSet("audio_volume");
            PropertiesSection sec = new PropertiesSection("audio_volume");
            sec.addEntry("volume", "" + volume);
            set.addProperties(sec);

            PropertiesSerializer.writeProperties(set, PROPS_FILE.getPath());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected  static void readFromFile() {

        try {

            PropertiesSet set = PropertiesSerializer.getProperties(PROPS_FILE.getPath());
            if (set != null) {
                List<PropertiesSection> secs = set.getPropertiesOfType("audio_volume");
                if (!secs.isEmpty()) {
                    PropertiesSection sec = secs.get(0);
                    String vol = sec.getEntryValue("volume");
                    if ((vol != null) && MathUtils.isInteger(vol)) {
                        volume = Integer.parseInt(vol);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Value between 0% and 100%
     */
    public static void setVolume(int vol) {
        if (vol < 0) {
            vol = 0;
        }
        if (vol > 100) {
            vol = 100;
        }
        volume = vol;
        writeToFile();
        for (AudioClip c : AudioCinematicHandler.getCachedAudios()) {
            c.setBaseVolume(vol);
        }
    }

    /**
     * Value between 0% and 100%
     */
    public static int getVolume() {
        return volume;
    }

    public static void updateVolume() {
        setVolume(getVolume());
    }

}
