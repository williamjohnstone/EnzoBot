package ml.enzodevelopment.enzobot.commands.music;

import ml.enzodevelopment.enzobot.Command;
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

public class ResetCommand implements Command {
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
        synchronized (musicUtils.musicManagers) {
            mng.scheduler.queue.clear();
            mng.player.destroy();
            event.getGuild().getAudioManager().setSendingHandler(null);
            musicUtils.musicManagers.remove(event.getGuild().getId());
        }
        musicUtils.hasVoted = new ArrayList<>();
        event.getGuild().getAudioManager().setSendingHandler(mng.sendHandler);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Info");
        builder.setColor(Color.WHITE);
        builder.setDescription("The player has been completely reset!");
        event.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String getUsage() {
        return "reset";
    }

    @Override
    public String getDesc() {
        return "Resets the player.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("reset"));
    }

    @Override
    public String getType() {
        return "music";
    }
}