package gravity.gbot.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import gravity.gbot.utils.Config;
import gravity.gbot.utils.EventAwaiter;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONObject;
import org.json.JSONPointer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static java.lang.Integer.parseInt;

public class PlayerControl extends ListenerAdapter {

    private static final int DEFAULT_VOLUME = 35; //(0 - 150, where 100 is default max volume)

    private final AudioPlayerManager playerManager;
    private final Map<String, GuildMusicManager> musicManagers;
    private final EventAwaiter waiter = new EventAwaiter();
    private List<Member> hasVoted = new ArrayList<>();

    public PlayerControl() {
        java.util.logging.Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);

        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());

        musicManagers = new HashMap<>();
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        GuildConfig config = new GuildConfig();

        final String musicAlias = "m";


        if (!event.isFromType(ChannelType.TEXT))
            return;

        String[] command = event.getMessage().getContentRaw().split(" +");

        if (!command[0].toLowerCase().startsWith(config.getPrefix(event.getGuild().getId(), this.getClass().getName()) + musicAlias)) { //message doesn't start with prefix. or is too short
            return;
        }else {
            if (command.length < 2) {
                return;
            }
        }

        event.getMessage().delete().queue();
        Guild guild = event.getGuild();
        GuildMusicManager mng = getMusicManager(guild);
        AudioPlayer player = mng.player;
        TrackScheduler scheduler = mng.scheduler;

        if (command[1].toLowerCase().equals("play") && command.length >= 3) {
            if (command[2].startsWith("http://") || command[2].startsWith("https://")) {
                join(guild, event, getMusicManager(guild));
                {
                    if (command[2].contains("&list=") || command[2].contains("?list=")) {
                        loadAndPlay(mng, event.getChannel(), command[2], true);
                    } else {
                        loadAndPlay(mng, event.getChannel(), command[2], false);
                    }
                }
            } else {
                {

                    StringBuilder sb = new StringBuilder();
                    for (String s: command) {
                        sb.append(s).append("+");
                    }
                    sb.delete(0,8);
                    String link;
                    String type;
                    if (sb.toString().toLowerCase().contains("playlist")) {
                        type = "playlist";
                        link = getYT.getLink(String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s&maxResults=5&type=playlist&key=%s", sb, Config.google_api));
                    } else {
                        type = "video";
                        link = getYT.getLink(String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s&maxResults=5&type=video&key=%s", sb, Config.google_api));
                    }

                    if(link == null) {
                        return;
                    }
                    JSONObject results = new JSONObject(link);

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Search Results");
                    builder.setColor(Color.WHITE);
                    builder.addField("Result 1:", searchTitle(0, results), false);
                    builder.addField("Result 2:", searchTitle(1, results), false);
                    builder.addField("Result 3:", searchTitle(2, results), false);
                    builder.addField("Result 4:", searchTitle(3, results), false);
                    builder.addField("Result 5:", searchTitle(4, results), false);
                    builder.setFooter("React with your selection.", event.getJDA().getSelfUser().getAvatarUrl());

                    event.getChannel().sendMessage(builder.build()).queue((msg -> {
                        waiter.awaitEvent(event.getJDA(), MessageReactionAddEvent.class,
                                e ->e.getUser().getId().equals(event.getAuthor().getId()) &&
                                        (e.getReactionEmote().getName().equals("\u0031\u20E3")
                                                || e.getReactionEmote().getName().equals("\u0032\u20E3")
                                                || e.getReactionEmote().getName().equals("\u0033\u20E3")
                                                || e.getReactionEmote().getName().equals("\u0034\u20E3")
                                                || e.getReactionEmote().getName().equals("\u0035\u20E3")),
                                e -> play(e.getReactionEmote().getName(), event, getMusicManager(guild),
                                        searchId(0, results, type),
                                        searchId(1, results, type),
                                        searchId(2, results, type),
                                        searchId(3, results, type),
                                        searchId(4, results, type),
                                        e.getMessageId(), event.getGuild(), type),
                                1, TimeUnit.MINUTES, () -> msg.delete().queue());
                        msg.addReaction("\u0031\u20E3").queue();
                        msg.addReaction("\u0032\u20E3").queue();
                        msg.addReaction("\u0033\u20E3").queue();
                        msg.addReaction("\u0034\u20E3").queue();
                        msg.addReaction("\u0035\u20E3").queue();
                    }));


                }
            }
        } else if ("leave".equals(command[1].toLowerCase()))
        {
            guild.getAudioManager().setSendingHandler(null);
            guild.getAudioManager().closeAudioConnection();
            hasVoted = new ArrayList<>();
            scheduler.queue.clear();
            player.stopTrack();
            player.setPaused(false);
        }  else if ("resume".equals(command[1].toLowerCase()) | "play".equals(command[1].toLowerCase()) && command.length == 2) //It is only the command to start playback (probably after pause)
        {
            if (player.isPaused()) {
                player.setPaused(false);
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription(":play_pause: Playback has been resumed.");
                event.getChannel().sendMessage(builder.build()).queue();
            } else if (player.getPlayingTrack() != null) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription("Player is already playing!");
                event.getChannel().sendMessage(builder.build()).queue();
            }
        } else if ("skip".equals(command[1].toLowerCase())) {
            String adminCheck = config.isAdmin(event.getAuthor().getId(), guild.getId(), event.getJDA());
            if (adminCheck == null) {
                List<Member> vcMembers = event.getMember().getVoiceState().getChannel().getMembers();
                //take one due to the bot being in there as well
                int requiredVotes = (int) Math.round((vcMembers.size() - 1) * 0.6);
                Member voter = event.getMember();
                if (hasVoted.contains(voter)) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Vote Skip");
                    builder.setColor(Color.WHITE);
                    builder.setDescription("You have already voted!");
                    event.getChannel().sendMessage(builder.build()).queue();
                } else {
                    if (vcMembers.contains(voter)) {
                        hasVoted.add(voter);
                        if (hasVoted.size() >= requiredVotes) {
                            scheduler.nextTrack();
                            EmbedBuilder builder = new EmbedBuilder();
                            builder.setTitle("Info");
                            builder.setColor(Color.WHITE);
                            builder.setDescription(":fast_forward: Track Skipped!");
                            event.getChannel().sendMessage(builder.build()).queue();
                            hasVoted = new ArrayList<>();
                            return;
                        }
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setTitle("Vote Skip");
                        builder.setColor(Color.WHITE);
                        if (player.getPlayingTrack() != null) {
                            builder.setDescription("You have voted to skip. " + player.getPlayingTrack().getInfo().title + " " + hasVoted.size() + "/" + requiredVotes + " votes to skip.");
                        }
                        event.getChannel().sendMessage(builder.build()).queue();
                    }
                }
            } else {
                scheduler.nextTrack();
                hasVoted = new ArrayList<>();
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription(":fast_forward: Track Skipped!");
                event.getChannel().sendMessage(builder.build()).queue();
            }
        } else if ("pause".equals(command[1].toLowerCase())) {
            if (player.getPlayingTrack() == null) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription("Cannot pause or resume player because no track is loaded for playing.");
                event.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            player.setPaused(!player.isPaused());
            if (player.isPaused()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription(":play_pause: The player has been paused.");
                event.getChannel().sendMessage(builder.build()).queue();
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription(":play_pause: The player has resumed playing.");
                event.getChannel().sendMessage(builder.build()).queue();
            }
        } else if ("stop".equals(command[1].toLowerCase())) {
            scheduler.queue.clear();
            hasVoted = new ArrayList<>();
            player.stopTrack();
            player.setPaused(false);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription(":stop_button: Playback has been completely stopped and the queue has been cleared.");
            event.getChannel().sendMessage(builder.build()).queue();
        } else if ("volume".equals(command[1])) {
            if (command.length == 2) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription(":speaker: Current player volume: **" + player.getVolume() + "**");
                event.getChannel().sendMessage(builder.build()).queue();
            } else {
                try {
                    int newVolume = Math.max(5, Math.min(100, parseInt(command[2])));
                    int oldVolume = player.getVolume();
                    player.setVolume(newVolume);
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Info");
                    builder.setColor(Color.WHITE);
                    builder.setDescription(":speaker: Player volume changed from `" + oldVolume + "` to `" + newVolume + "`");
                    event.getChannel().sendMessage(builder.build()).queue();
                } catch (NumberFormatException e) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Info");
                    builder.setColor(Color.WHITE);
                    builder.setDescription(":speaker: `" + command[2] + "` is not a valid integer. (5 - 100)");
                    event.getChannel().sendMessage(builder.build()).queue();
                }
            }
        } else if ("restart".equals(command[1].toLowerCase())) {
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
        } else if ("reset".equals(command[1].toLowerCase())) {
            synchronized (musicManagers) {
                scheduler.queue.clear();
                player.destroy();
                guild.getAudioManager().setSendingHandler(null);
                musicManagers.remove(guild.getId());
            }

            hasVoted = new ArrayList<>();
            mng = getMusicManager(guild);
            guild.getAudioManager().setSendingHandler(mng.sendHandler);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("The player has been completely reset!");
            event.getChannel().sendMessage(builder.build()).queue();

        } else if ("nowplaying".equals(command[1].toLowerCase()) || "np".equals(command[1].toLowerCase())) {
            AudioTrack currentTrack = player.getPlayingTrack();
            if (currentTrack != null) {

                String position = getTimestamp(currentTrack.getPosition());
                String duration = getTimestamp(currentTrack.getDuration());

                String Time = String.format("Current Time: [%s / %s]", position, duration);

                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle("Queue");
                builder.setColor(Color.WHITE);
                builder.setDescription("Now Playing");
                builder.addField("Title", currentTrack.getInfo().title, true);
                builder.addField("Author", currentTrack.getInfo().author, true);
                builder.addField("Duration", getTimestamp(currentTrack.getDuration()), true);
                builder.addField("URL", currentTrack.getInfo().uri, true);
                builder.addField("Time", Time, true);
                event.getChannel().sendMessage(builder.build()).queue();

            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription("The player is not currently playing anything!");
                event.getChannel().sendMessage(builder.build()).queue();
            }
        } else if ("queue".equals(command[1].toLowerCase()) || "q".equals(command[1].toLowerCase())) {
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
                            sb.append("`[").append(getTimestamp(track.getDuration())).append("]` ");
                            sb.append(track.getInfo().title).append("\n");
                            trackCount++;
                        }
                    }
                    sb.append("\n").append("Total Queue Time Length: ").append(getTimestamp(queueLength));
                    String queueString = sb.toString();
                    if (queueString.length() > 800) {
                        trackCount = 0;
                        queueLength = 0;
                        sb = new StringBuilder();
                        for (AudioTrack track : queue) {
                            queueLength += track.getDuration();
                            if (trackCount < 5) {
                                sb.append("`[").append(getTimestamp(track.getDuration())).append("]` ");
                                sb.append(track.getInfo().title).append("\n");
                                trackCount++;
                            }
                        }
                    }

                    builder.addField("Current Queue: Entries: " + queue.size(), sb.toString(), false);
                    event.getChannel().sendMessage(builder.build()).queue();
                }
            }
        } else if ("shuffle".equals(command[1].toLowerCase())) {
            if (scheduler.queue.isEmpty()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription("The queue is currently empty!");
                event.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            scheduler.shuffle();
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("The queue has been shuffled!");
            event.getChannel().sendMessage(builder.build()).queue();
        }
    }

    private void join(Guild guild, MessageReceivedEvent event, GuildMusicManager mng) {
        VoiceChannel chan;
        try {
            chan = guild.getVoiceChannelById(event.getMember().getVoiceState().getChannel().getId());
        } catch (NullPointerException e) {
            event.getChannel().sendMessage("You're not currently in a voice channel please join one and try again").queue();
            return;
        }
        if (chan != null)
            guild.getAudioManager().setSendingHandler(mng.sendHandler);
            try {
                guild.getAudioManager().openAudioConnection(chan);
            } catch (PermissionException e) {
                if (e.getPermission() == Permission.VOICE_CONNECT) {
                    assert chan != null;
                    event.getChannel().sendMessage("I don't have permission to connect to: " + chan.getName()).queue();
                }
            }
        }


    private String searchId(int number, JSONObject results, String type) {
        JSONPointer pointer;
        if (type.equals("playlist")) {
            pointer = new JSONPointer("/items/" + number + "/id/playlistId");
        } else {
            pointer = new JSONPointer("/items/" + number + "/id/videoId");
        }
        return String.valueOf(pointer.queryFrom(results));
    }
    private String searchTitle(int number, JSONObject results) {
        JSONPointer pointer = new JSONPointer("/items/" + number + "/snippet/title");
        return String.valueOf(pointer.queryFrom(results));
    }
    private void play(String Emoji, MessageReceivedEvent event, GuildMusicManager mng, String url0, String url1, String url2, String url3, String url4, String msgID, Guild guild, String type){
        join(guild, event, getMusicManager(guild));
        String start;
        Boolean isPlaylist;
        if (type.equals("playlist")) {
            start = "https://www.youtube.com/playlist?list=";
            isPlaylist = true;
        } else {
            start = "https://youtube.com/watch?v=";
            isPlaylist = false;
        }
        if (Emoji != null && !Emoji.equals(""))
            switch (Emoji) {
                case "\u0031\u20E3":
                    loadAndPlay(mng, event.getChannel(), start + url0, isPlaylist);
                    event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));
                    break;
                case "\u0032\u20E3":
                    loadAndPlay(mng, event.getChannel(), start + url1, isPlaylist);
                    event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));
                    break;
                case "\u0033\u20E3":
                    loadAndPlay(mng, event.getChannel(), start + url2, isPlaylist);
                    event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));
                    break;
                case "\u0034\u20E3":
                    loadAndPlay(mng, event.getChannel(), start + url3, isPlaylist);
                    event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));
                    break;
                case "\u0035\u20E3":
                    loadAndPlay(mng, event.getChannel(), start + url4, isPlaylist);
                    event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));
                    break;
                default:
                    event.getTextChannel().sendMessage("Invalid Selection").queue();
                    event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));
                    break;
            }
    }

    private void loadAndPlay(GuildMusicManager mng, final MessageChannel channel, String url, final boolean addPlaylist) {
        final String trackUrl;

        //Strip <>'s that prevent discord from embedding link main.resources
        if (url.startsWith("<") && url.endsWith(">"))
            trackUrl = url.substring(1, url.length() - 1);
        else
            trackUrl = url;

        playerManager.loadItemOrdered(mng, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle("Queue");
                builder.setColor(Color.WHITE);
                builder.setDescription("Queued");
                builder.addField("Title", track.getInfo().title, true);
                builder.addField("Author", track.getInfo().author, true);
                builder.addField("Duration", getTimestamp(track.getDuration()), true);
                builder.addField("URL", track.getInfo().uri, true);
                channel.sendMessage(builder.build()).queue();
                mng.scheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();


                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                if (addPlaylist) {
                    EmbedBuilder builder = new EmbedBuilder();

                    builder.setTitle("Queue");
                    builder.setColor(Color.WHITE);
                    builder.setDescription("Queued " + playlist.getTracks().size() + " songs from " + playlist.getName());
                    channel.sendMessage(builder.build()).queue();
                    tracks.forEach(mng.scheduler::queue);
                } else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Queue");
                    builder.setColor(Color.WHITE);
                    builder.setDescription("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")");
                    channel.sendMessage(builder.build()).queue();
                    mng.scheduler.queue(firstTrack);
                }
            }

            @Override
            public void noMatches() {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Queue");
                builder.setColor(Color.WHITE);
                builder.setDescription("Nothing found by " + trackUrl);
                channel.sendMessage(builder.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Queue");
                builder.setColor(Color.WHITE);
                builder.setDescription("Could not play: " + exception.getMessage());
                channel.sendMessage(builder.build()).queue();
            }
        });
    }

    private GuildMusicManager getMusicManager(Guild guild) {
        String guildId = guild.getId();
        GuildMusicManager mng = musicManagers.get(guildId);
        if (mng == null) {
            synchronized (musicManagers) {
                mng = musicManagers.get(guildId);
                if (mng == null) {
                    mng = new GuildMusicManager(playerManager);
                    mng.player.setVolume(DEFAULT_VOLUME);
                    musicManagers.put(guildId, mng);
                }
            }
        }
        return mng;
    }

    private static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }
}