package ml.enzodevelopment.enzobot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.music.GuildMusicManager;
import ml.enzodevelopment.enzobot.music.MusicUtils;
import ml.enzodevelopment.enzobot.music.TrackScheduler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkipCommand implements Command {
    private MusicUtils musicUtils = new MusicUtils();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        GuildMusicManager mng = musicUtils.getMusicManager(event.getGuild());
        AudioPlayer player = mng.player;
        TrackScheduler scheduler = mng.scheduler;

        if (player.getPlayingTrack() == null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("No Track is currently playing.");
            event.getChannel().sendMessage(builder.build()).queue();
            return;
        }
        User requested = (User) player.getPlayingTrack().getUserData();
        if (args.length == 2) {
            if ("all".equals(args[1].toLowerCase())) {
                scheduler.queue.removeIf(track -> track.getUserData() == requested);
                MusicUtils.hasVoted = new ArrayList<>();
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription(":fast_forward: All your tracks have been removed from the queue!");
                event.getChannel().sendMessage(builder.build()).queue();
                return;
            }
        }
        if (event.getAuthor() == requested) {
            scheduler.nextTrack();
            MusicUtils.hasVoted = new ArrayList<>();
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription(":fast_forward: Track Skipped!");
            event.getChannel().sendMessage(builder.build()).queue();
            return;
        }
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            List<Member> vcMembers = event.getMember().getVoiceState().getChannel().getMembers();
            //take one due to the bot being in there as well
            int requiredVotes = (int) Math.round((vcMembers.size() - 1) * 0.6);
            Member voter = event.getMember();
            if (MusicUtils.hasVoted.contains(voter)) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Vote Skip");
                builder.setColor(Color.WHITE);
                builder.setDescription("You have already voted!");
                event.getChannel().sendMessage(builder.build()).queue();
            } else {
                if (vcMembers.contains(voter)) {
                    MusicUtils.hasVoted.add(voter);
                    if (MusicUtils.hasVoted.size() >= requiredVotes) {
                        scheduler.nextTrack();
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setTitle("Info");
                        builder.setColor(Color.WHITE);
                        builder.setDescription(":fast_forward: Track Skipped!");
                        event.getChannel().sendMessage(builder.build()).queue();
                        MusicUtils.hasVoted = new ArrayList<>();
                        return;
                    }
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Vote Skip");
                    builder.setColor(Color.WHITE);
                    if (player.getPlayingTrack() != null) {
                        builder.setDescription("You have voted to skip. " + player.getPlayingTrack().getInfo().title + " " + MusicUtils.hasVoted.size() + "/" + requiredVotes + " votes to skip.");
                    }
                    event.getChannel().sendMessage(builder.build()).queue();
                }
            }
        } else {
            if (args.length == 1) {
                scheduler.nextTrack();
                MusicUtils.hasVoted = new ArrayList<>();
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription(":fast_forward: Track Skipped!");
                event.getChannel().sendMessage(builder.build()).queue();
            }

        }
    }

    @Override
    public String getUsage() {
        return "skip or skip all";
    }

    @Override
    public String getDesc() {
        return "Skips the currently playing song. (skip all clears all your tracks from the queue)";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("skip"));
    }

    @Override
    public String getType() {
        return "music";
    }
}
