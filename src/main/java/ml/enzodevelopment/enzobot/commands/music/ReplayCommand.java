package ml.enzodevelopment.enzobot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.music.MusicUtils;
import ml.enzodevelopment.enzobot.music.TrackScheduler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplayCommand implements Command {
    private MusicUtils musicUtils = new MusicUtils();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        AudioPlayer player = musicUtils.getMusicManager(event.getGuild()).player;
        TrackScheduler scheduler =  musicUtils.getMusicManager(event.getGuild()).scheduler;
        AudioTrack track = player.getPlayingTrack();
        if (track == null)
            track = scheduler.lastTrack;
        if (track != null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("Restarting track: " + track.getInfo().title);
            event.getChannel().sendMessage(builder.build()).queue();
            player.playTrack(track.makeClone());
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("No track has been previously started, so the player cannot replay a track!");
            event.getChannel().sendMessage(builder.build()).queue();
        }
    }

    @Override
    public String getUsage() {
        return "replay";
    }

    @Override
    public String getDesc() {
        return "Restarts the currently playing track.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("replay"));
    }

    @Override
    public String getType() {
        return "music";
    }
}
