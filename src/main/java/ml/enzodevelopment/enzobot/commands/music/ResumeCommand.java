package ml.enzodevelopment.enzobot.commands.music;

import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.CommandCategory;
import ml.enzodevelopment.enzobot.music.GuildMusicManager;
import ml.enzodevelopment.enzobot.music.MusicUtils;
import ml.enzodevelopment.enzobot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResumeCommand implements Command {
    private MusicUtils musicUtils = Config.musicUtils;

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        GuildMusicManager mng = musicUtils.getMusicManager(event.getGuild());

        if (mng.player.isPaused()) {
            mng.player.setPaused(false);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription(":play_pause: Playback has been resumed.");
            event.getChannel().sendMessage(builder.build()).queue();
        } else if (mng.player.getPlayingTrack() != null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("Player is already playing!");
            event.getChannel().sendMessage(builder.build()).queue();
        }

    }

    @Override
    public String getUsage() {
        return "resume";
    }

    @Override
    public String getDesc() {
        return "Resumes the player.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("resume"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }
}
