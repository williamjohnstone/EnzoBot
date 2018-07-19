package ml.enzodevelopment.enzobot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
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

public class NowPlayingCommand implements Command {
    private MusicUtils musicUtils = new MusicUtils();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        GuildMusicManager mng = musicUtils.getMusicManager(event.getGuild());
        AudioPlayer player = mng.player;
        AudioTrack currentTrack = player.getPlayingTrack();
        if (currentTrack != null) {

            Long current = currentTrack.getPosition();
            Long total = currentTrack.getDuration();

            String position = musicUtils.getTimestamp(currentTrack.getPosition());
            String duration = musicUtils.getTimestamp(currentTrack.getDuration());

            String Time = String.format("(%s / %s)", position, duration);
            String progressBar = musicUtils.getProgressBar(current, total);

            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Queue");
            builder.setColor(Color.WHITE);
            builder.setDescription("Now Playing");
            builder.addField("Title", currentTrack.getInfo().title, true);
            builder.addField("Author", currentTrack.getInfo().author, true);
            builder.addField("Duration", duration, true);
            builder.addField("URL", currentTrack.getInfo().uri, true);
            builder.addField("Time", progressBar + " " + Time, false);
            event.getChannel().sendMessage(builder.build()).queue();

        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("The player is not currently playing anything!");
            event.getChannel().sendMessage(builder.build()).queue();
        }
    }

    @Override
    public String getUsage() {
        return "nowplaying";
    }

    @Override
    public String getDesc() {
        return "Displays the currently playing track.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("nowplaying", "playing", "np"));
    }

    @Override
    public String getType() {
        return "music";
    }
}
