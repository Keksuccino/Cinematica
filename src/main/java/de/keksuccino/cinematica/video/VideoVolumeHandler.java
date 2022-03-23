package de.keksuccino.cinematica.video;

import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;
import de.keksuccino.konkrete.properties.PropertiesSerializer;
import de.keksuccino.konkrete.properties.PropertiesSet;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;

import java.io.File;
import java.util.List;

public class VideoVolumeHandler {

    protected static final File PROPS_FILE = new File(Cinematica.MOD_DIRECTORY.getPath() + "/video_volume.properties");

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

            PropertiesSet set = new PropertiesSet("video_volume");
            PropertiesSection sec = new PropertiesSection("video_volume");
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
                List<PropertiesSection> secs = set.getPropertiesOfType("video_volume");
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
        for (VideoRenderer r : VideoHandler.getCachedRenderers()) {
            updateRendererVolume(r);
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

    public static void updateRendererVolume(VideoRenderer renderer) {

        int baseVol = renderer.baseVolume; //100% for volume handler
        double baseVolPercent = ((double)baseVol) / 100.0D;
        int newVolTemp = (int) (baseVolPercent * volume); //100% for MASTER

        int mcMasterVol = (int) (Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.MASTER) * 100);
        double tempPercent = ((double)newVolTemp) / 100.0D;
        int newVol = (int) (tempPercent * mcMasterVol);

        renderer.setVolume(newVol);

    }

}
