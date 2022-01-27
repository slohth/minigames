package dev.slohth.minigames.utils.framework.assemble.adapter;

import dev.slohth.minigames.Minigames;
import dev.slohth.minigames.game.GameState;
import dev.slohth.minigames.game.types.oitc.OITC;
import dev.slohth.minigames.plugin.MinigamesPlugin;
import dev.slohth.minigames.profile.Profile;
import dev.slohth.minigames.utils.framework.assemble.AssembleAdapter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ScoreboardAdapter implements AssembleAdapter {

    @Override
    public String getTitle(final Player player) {
        Minigames core = JavaPlugin.getPlugin(MinigamesPlugin.class).core();
        Profile profile = core.profileManager().profile(player.getUniqueId());
        if (profile == null || profile.data() == null) return null;

        switch (profile.data().game().type()) {
            case ONE_IN_THE_CHAMBER: {
                if (profile.data().game().state() != GameState.ONGOING) return null;
                return "&3&lOITC";
            }
        }

        return null;
    }

    @Override
    public List<String> getLines(final Player player) {
        Minigames core = JavaPlugin.getPlugin(MinigamesPlugin.class).core();
        Profile profile = core.profileManager().profile(player.getUniqueId());
        if (profile == null || profile.data() == null) return null;

        List<String> lines = new ArrayList<>();

        switch (profile.data().game().type()) {
            case ONE_IN_THE_CHAMBER: {
                if (profile.data().game().state() != GameState.ONGOING) return null;
                OITC game = (OITC) profile.data().game();
                LinkedList<Profile> kills = game.topKillers();

                for (Profile killer : kills) lines.add("&7" + killer.player().getName() + ": &3" + killer.data().kills());

            }
        }

        return lines;
    }
}
