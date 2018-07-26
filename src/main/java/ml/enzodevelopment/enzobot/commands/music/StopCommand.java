package ml.enzodevelopment.enzobot.commands.music;

import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.CommandCategory;
import ml.enzodevelopment.enzobot.music.GuildMusicManager;
import ml.enzodevelopment.enzobot.music.MusicUtils;
import ml.enzodevelopment.enzobot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StopCommand implements Command {
    private MusicUtils musicUtils = Config.musicUtils;

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        GuildMusicManager mng = musicUtils.getMusicManager(event.getGuild());
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("You do not have the permission to do that!");
            return;
        }
        mng.scheduler.queue.clear();
        musicUtils.hasVoted = new ArrayList<>();
        mng.player.stopTrack();
        mng.player.setPaused(false);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Info");
        builder.setColor(Color.WHITE);
        builder.setDescription(":stop_button: Playback has been completely stopped and the queue has been cleared.");
        event.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String getUsage() {
        return "stop";
    }

    @Override
    public String getDesc() {
        return "Stops the player and clears the queue.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("stop"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }
}
