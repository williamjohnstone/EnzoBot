package ml.enzodevelopment.enzobot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import ml.enzodevelopment.enzobot.audio.GuildMusicManager;
import ml.enzodevelopment.enzobot.utils.MusicUtils;
import ml.enzodevelopment.enzobot.audio.TrackScheduler;
import ml.enzodevelopment.enzobot.config.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaveCommand implements Command {
    private MusicUtils musicUtils = Config.musicUtils;

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        GuildMusicManager mng = musicUtils.getMusicManager(event.getGuild());
        AudioPlayer player = mng.player;
        TrackScheduler scheduler = mng.scheduler;
        if (player.getPlayingTrack() != null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("Cannot leave voice channel when a song is currently playing.");
            event.getChannel().sendMessage(builder.build()).queue();
            return;
        }
        event.getGuild().getAudioManager().setSendingHandler(null);
        event.getGuild().getAudioManager().closeAudioConnection();
        musicUtils.hasVoted = new ArrayList<>();
        scheduler.queue.clear();
        player.stopTrack();
        player.setPaused(false);
    }

    @Override
    public String getUsage() {
        return "leave";
    }

    @Override
    public String getDesc() {
        return "Makes the bot leave the current voice channel.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("leave", "exit", "disconnect"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }
}
