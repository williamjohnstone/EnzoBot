package ml.enzodevelopment.enzobot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.music.GuildMusicManager;
import ml.enzodevelopment.enzobot.music.MusicUtils;
import ml.enzodevelopment.enzobot.music.TrackScheduler;
import ml.enzodevelopment.enzobot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class QueueCommand implements Command {
    private MusicUtils musicUtils = Config.musicUtils;

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        GuildMusicManager mng = musicUtils.getMusicManager(event.getGuild());
        TrackScheduler scheduler = mng.scheduler;
        Queue<AudioTrack> queue = scheduler.queue;
        synchronized (queue) {
            if (queue.isEmpty()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription("The queue is currently empty!");
                event.getChannel().sendMessage(builder.build()).queue();
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);

                int trackCount = 0;
                long queueLength = 0;
                StringBuilder sb = new StringBuilder();
                for (AudioTrack track : queue) {
                    queueLength += track.getDuration();
                    if (trackCount < 10) {
                        sb.append("`[").append(musicUtils.getTimestamp(track.getDuration())).append("]` ");
                        sb.append(track.getInfo().title).append("\n");
                        trackCount++;
                    }
                }
                sb.append("\n").append("Total Queue Time Length: ").append(musicUtils.getTimestamp(queueLength));
                String queueString = sb.toString();
                if (queueString.length() > 800) {
                    trackCount = 0;
                    queueLength = 0;
                    sb = new StringBuilder();
                    for (AudioTrack track : queue) {
                        queueLength += track.getDuration();
                        if (trackCount < 5) {
                            sb.append("`[").append(musicUtils.getTimestamp(track.getDuration())).append("]` ");
                            sb.append(track.getInfo().title).append("\n");
                            trackCount++;
                        }
                    }
                }

                builder.addField("Current Queue: Entries: " + queue.size(), sb.toString(), false);
                event.getChannel().sendMessage(builder.build()).queue();
            }

        }
    }

    @Override
    public String getUsage() {
        return "queue";
    }

    @Override
    public String getDesc() {
        return "Displays the player queue.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("queue", "q"));
    }

    @Override
    public String getType() {
        return "music";
    }
}
