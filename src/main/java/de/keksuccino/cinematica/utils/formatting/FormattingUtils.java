package de.keksuccino.cinematica.utils.formatting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.keksuccino.cinematica.Cinematica;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;

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
                INBT d = stack.getTag().get("display");
                if (d != null) {
                    CompoundNBT display = (CompoundNBT) d;
                    INBT l = display.get("Lore");
                    if (l != null) {
                        ListNBT lore = (ListNBT) l;
                        List<String> linesList = new ArrayList<>();
                        lore.iterator().forEachRemaining((nbt) -> {
                            StringNBT loreLine = (StringNBT) nbt;
                            String lineString = FormattingUtils.deserializedComponentArrayToUnformattedString(FormattingUtils.deserializeNbtTextComponentString(loreLine.getString()));
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
