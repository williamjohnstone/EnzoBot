package ml.enzodevelopment.enzobot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.music.GuildMusicManager;
import ml.enzodevelopment.enzobot.music.MusicUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeekCommand implements Command {
    private MusicUtils musicUtils = new MusicUtils();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        GuildMusicManager mng = musicUtils.getMusicManager(event.getGuild());
        Long millis = musicUtils.parseTime(args[1]);
        if (millis == null || millis > Integer.MAX_VALUE) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("You have entered an invalid duration to skip to!");
            event.getChannel().sendMessage(builder.build()).queue();
            return;
        }
        AudioTrack t = mng.player.getPlayingTrack();
        if (t == null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("There is no song currently playing!");
            event.getChannel().sendMessage(builder.build()).queue();
        } else {
            if (t.getInfo().isStream) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription("Cannot seek on a livestream!");
                event.getChannel().sendMessage(builder.build()).queue();
            } else if (!t.isSeekable()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription("Cannot seek on this track!");
                event.getChannel().sendMessage(builder.build()).queue();
            } else {
                if (millis >= t.getDuration()) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Info");
                    builder.setColor(Color.WHITE);
                    builder.setDescription("The duration specified is bigger than the length of the video!");
                    event.getChannel().sendMessage(builder.build()).queue();
                } else {
                    t.setPosition(millis);
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Info");
                    builder.setColor(Color.WHITE);
                    builder.setDescription("The track has been skipped to: " + "`[" + musicUtils.getTimestamp(millis) + "]`");
                    event.getChannel().sendMessage(builder.build()).queue();
                }
            }
        }
    }

    @Override
    public String getUsage() {
        return "seek (time)";
    }

    @Override
    public String getDesc() {
        return "Seeks to the specified point in the track.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("seek"));
    }

    @Override
    public String getType() {
        return "music";
    }
}
