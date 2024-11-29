package pl.daniu.helpOpVelocity.api;

import dev.dejvokep.boostedyaml.route.Route;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import pl.daniu.helpOpVelocity.HelpOpVelocity;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author : Daniu
 **/
public class API {

    public static @NotNull TextComponent FixColor(String message){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public static final HashMap<UUID, Long> helpOpCooldownList = new HashMap<>();



    public static String getConfigString(String key){
        return  HelpOpVelocity.getInstance().config.getString(Route.fromString(key));
    }

    public static Integer getConfigInt(String key){
        return  HelpOpVelocity.getInstance().config.getInt(Route.fromString(key));
    }

    public static String mergeArgs(final String[] args, final int start) {
        final StringBuilder bldr = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i != start) {
                bldr.append(" ");
            }
            bldr.append(args[i]);
        }
        return bldr.toString();
    }
}
