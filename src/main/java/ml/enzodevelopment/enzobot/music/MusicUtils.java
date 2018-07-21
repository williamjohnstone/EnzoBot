package ml.enzodevelopment.enzobot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.managers.AudioManager;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.json.JSONObject;
import org.json.JSONPointer;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicUtils {
    private static final int DEFAULT_VOLUME = 35;
    public final Map<String, GuildMusicManager> musicManagers;
    private final AudioPlayerManager playerManager;
    public static List<Member> hasVoted = new ArrayList<>();
    private static final Pattern timeRegex = Pattern.compile("^([0-9]*):?([0-9]*)?:?([0-9]*)?$");

    public MusicUtils() {
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

    public int getActiveConnections(GuildMessageReceivedEvent event) {
        int activeCnt = 0;
        for (AudioManager mng : event.getJDA().getAudioManagerCache()) {
            if (mng.isConnected()) {
                activeCnt++;
            }
        }
        return activeCnt;
    }

    public boolean join(Guild guild, GuildMessageReceivedEvent event, GuildMusicManager mng) {
        VoiceChannel chan;
        try {
            chan = guild.getVoiceChannelById(event.getMember().getVoiceState().getChannel().getId());
        } catch (NullPointerException e) {
            event.getChannel().sendMessage("You're not currently in a voice channel please join one and try again").queue();
            return false;
        }
        if (chan != null)
            guild.getAudioManager().setSendingHandler(mng.sendHandler);
        try {
            guild.getAudioManager().openAudioConnection(chan);
        } catch (PermissionException e) {
            if (e.getPermission() == Permission.VOICE_CONNECT) {
                assert chan != null;
                event.getChannel().sendMessage("I don't have permission to connect to: " + chan.getName()).queue();
                return false;
            }
        }
        return true;
    }

    public GuildMusicManager getMusicManager(Guild guild) {
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

    public String searchId(int number, JSONObject results, String type) {
        JSONPointer pointer;
        if (type.equals("playlist")) {
            pointer = new JSONPointer("/items/" + number + "/id/playlistId");
        } else {
            pointer = new JSONPointer("/items/" + number + "/id/videoId");
        }
        return String.valueOf(pointer.queryFrom(results));
    }

    public String searchTitle(int number, JSONObject results) {
        JSONPointer pointer = new JSONPointer("/items/" + number + "/snippet/title");
        return String.valueOf(pointer.queryFrom(results));
    }

    public void play(String Emoji, GuildMessageReceivedEvent event, GuildMusicManager mng, String url0, String url1, String url2, String url3, String url4, String msgID, Guild guild, String type, User user) {
        boolean join = join(guild, event, getMusicManager(guild));
        if (!join) {
            return;
        }
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
                    loadAndPlay(mng, event.getChannel(), start + url0, isPlaylist, user);
                    event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));
                    break;
                case "\u0032\u20E3":
                    loadAndPlay(mng, event.getChannel(), start + url1, isPlaylist, user);
                    event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));
                    break;
                case "\u0033\u20E3":
                    loadAndPlay(mng, event.getChannel(), start + url2, isPlaylist, user);
                    event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));
                    break;
                case "\u0034\u20E3":
                    loadAndPlay(mng, event.getChannel(), start + url3, isPlaylist, user);
                    event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));
                    break;
                case "\u0035\u20E3":
                    loadAndPlay(mng, event.getChannel(), start + url4, isPlaylist, user);
                    event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));
                    break;
                default:
                    event.getChannel().sendMessage("Invalid Selection").queue();
                    event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));
                    break;
            }
    }

    public void loadAndPlay(GuildMusicManager mng, final MessageChannel channel, String url, final boolean addPlaylist, User user) {
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
                track.setUserData(user);
                mng.scheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();
                for (AudioTrack track : tracks) {
                    track.setUserData(user);
                }

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                firstTrack.setUserData(user);

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

    public String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

    public String getProgressBar(Long current, Long total) {
        int ActiveBlocks = (int) ((float) current / total * 15);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int inactive = 0;
        while (ActiveBlocks > i) {
            sb.append("[\u25AC](https://g-bot.tk/)");
            i++;
        }
        int remaining = 15 - i;
        while (remaining > inactive) {
            sb.append("\u25AC");
            inactive++;
        }
        return sb.toString();
    }

    public Long parseTime(String time) {
        Matcher digitMatcher = timeRegex.matcher(time);
        if (digitMatcher.matches()) {
            try {
                return new PeriodFormatterBuilder()
                        .appendHours().appendSuffix(":")
                        .appendMinutes().appendSuffix(":")
                        .appendSeconds()
                        .toFormatter()
                        .parsePeriod(time)
                        .toStandardDuration().getMillis();
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendHours().appendSuffix("h")
                .appendMinutes().appendSuffix("m")
                .appendSeconds().appendSuffix("s")
                .toFormatter();
        Period period;
        try {
            period = formatter.parsePeriod(time);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return period.toStandardDuration().getMillis();
    }

}
