package de.keksuccino.cinematica.utils.formatting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FormattingUtils {

    @Nullable
    public static DeserializedNbtTextComponent[] deserializeNbtTextComponentString(String nbtString) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(nbtString, DeserializedNbtTextComponent[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String deserializedComponentArrayToUnformattedString(DeserializedNbtTextComponent[] array) {
        String s = "";
        for (DeserializedNbtTextComponent d : array) {
            s += d.text;
        }
        return s;
    }

    @Nullable
    public static String deserializeItemLoreToString(ItemStack stack) {
        try {
            if (stack.getTag() != null) {
                Tag d = stack.getTag().get("display");
                if (d != null) {
                    CompoundTag display = (CompoundTag) d;
                    Tag l = display.get("Lore");
                    if (l != null) {
                        ListTag lore = (ListTag) l;
                        List<String> linesList = new ArrayList<>();
                        lore.iterator().forEachRemaining((nbt) -> {
                            StringTag loreLine = (StringTag) nbt;
                            String lineString = FormattingUtils.deserializedComponentArrayToUnformattedString(FormattingUtils.deserializeNbtTextComponentString(loreLine.getAsString()));
                            if (lineString != null) {
                                linesList.add(lineString);
                            }
                        });
                        String mergedLinesString = "";
                        boolean firstLine = true;
                        for (String lineString : linesList) {
                            if (firstLine) {
                                mergedLinesString += lineString;
                                firstLine = false;
                            } else {
                                mergedLinesString += "%n%" + lineString;
                            }
                        }
                        return mergedLinesString;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
