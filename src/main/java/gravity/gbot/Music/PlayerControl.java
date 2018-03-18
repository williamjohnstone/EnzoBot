package gravity.gbot.Music;

import gravity.gbot.utils.Config;
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
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONObject;
import java.awt.*;
import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

import static java.lang.Integer.parseInt;

public class PlayerControl extends ListenerAdapter {

    private static final int DEFAULT_VOLUME = 35; //(0 - 150, where 100 is default max volume)

    private final AudioPlayerManager playerManager;
    private final Map<String, GuildMusicManager> musicManagers;

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


    //todo correct these entries
    //Prefix for all commands: !music or !m
    //Example:  !music play or !m play
    //Current commands
    // play         - Plays songs from the current queue. Starts playing again if it was previously paused
    // play [url]   - Adds a new song to the queue and starts playing if it wasn't playing already
    // pplay        - Adds a playlist to the queue and starts playing if not already playing
    // pause        - Pauses audio playback
    // stop         - Completely stops audio playback, skipping the current song.
    // skip         - Skips the current song, automatically starting the next
    // nowplaying   - Prints information about the currently playing song (title, current time)
    // np           - alias for nowplaying
    // list         - Lists the songs in the queue
    // volume [val] - Sets the volume of the MusicPlayer [10 - 100]
    // restart      - Restarts the current song or restarts the previous song if there is no current song playing.
    // repeat       - Makes the player repeat the currently playing song
    // reset        - Completely resets the player, fixing all errors and clearing the queue.


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        GuildConfig config = new GuildConfig();

        final String musicAlias = "m";
        final String select = "select";
        int selection = 0;

        if (!event.isFromType(ChannelType.TEXT))
            return;

        String[] command = event.getMessage().getContentRaw().split(" +");

        if (!command[0].startsWith(config.getPrefix(event.getGuild().getId(), this.getClass().getName()) + musicAlias)) { //message doesn't start with prefix. or is too short
            return;
        }else {
            if (command.length < 2) {
                return;
            }
        }

        Guild guild = event.getGuild();
        GuildMusicManager mng = getMusicManager(guild);
        AudioPlayer player = mng.player;
        TrackScheduler scheduler = mng.scheduler;


        //play command
        if (command[1].equals("play") && command.length >= 3) {
            if (command[2].startsWith("http://") || command[2].startsWith("https://")) {
                VoiceChannel chan;
                try {
                    chan = guild.getVoiceChannelById(event.getMember().getVoiceState().getChannel().getId());
                } catch (NullPointerException e) {
                    event.getChannel().sendMessage("You're not currently in a voice channel please join one and try again").queue();
                    return;
                }
                if (chan == null) {
                    return;
                } else {
                    guild.getAudioManager().setSendingHandler(mng.sendHandler);
                    try {
                        guild.getAudioManager().openAudioConnection(chan);
                    } catch (PermissionException e) {
                        if (e.getPermission() == Permission.VOICE_CONNECT) {
                            event.getChannel().sendMessage("I don't have permission to connect to: " + chan.getName()).queue();
                            return;
                        }
                    }
                }
                {
                    if (command[2].contains("&list=")) {
                        loadAndPlay(mng, event.getChannel(), command[2], true);
                        event.getMessage().delete().queue();
                    } else {
                        loadAndPlay(mng, event.getChannel(), command[2], false);
                        event.getMessage().delete().queue();
                    }
                }
            } else {
                {

                    StringBuilder sb = new StringBuilder();
                    String searchterm[] = command;
                    for (String s: searchterm) {
                        sb.append(s + "+");
                    }
                    sb.delete(0,8);

                    String link = getYT.getLink(String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s&maxResults=5&type=video&key=%s", sb, "AIzaSyBaQKBM51U0RuFGiGmraWPPPwPGPQiJgxM"));

                    JSONObject results = new JSONObject(link);


                    String url0 = results.getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");
                    String url1 = results.getJSONArray("items").getJSONObject(1).getJSONObject("id").getString("videoId");
                    String url2 = results.getJSONArray("items").getJSONObject(2).getJSONObject("id").getString("videoId");
                    String url3 = results.getJSONArray("items").getJSONObject(3).getJSONObject("id").getString("videoId");
                    String url4 = results.getJSONArray("items").getJSONObject(4).getJSONObject("id").getString("videoId");

                    String url0name = results.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("title");
                    String url1name = results.getJSONArray("items").getJSONObject(1).getJSONObject("snippet").getString("title");
                    String url2name = results.getJSONArray("items").getJSONObject(2).getJSONObject("snippet").getString("title");
                    String url3name = results.getJSONArray("items").getJSONObject(3).getJSONObject("snippet").getString("title");
                    String url4name = results.getJSONArray("items").getJSONObject(4).getJSONObject("snippet").getString("title");

                    try {
                        PrintWriter writer = new PrintWriter(guild.getId() + "_" + event.getAuthor().getId() + ".txt", "UTF-8");
                        writer.println(url0);
                        writer.println(url1);
                        writer.println(url2);
                        writer.println(url3);
                        writer.println(url4);
                        writer.close();
                    } catch (FileNotFoundException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Search Results");
                    builder.setColor(Color.WHITE);
                    builder.addField("Result 1:", url0name, false);
                    builder.addField("Result 2:", url1name, false);
                    builder.addField("Result 3:", url2name, false);
                    builder.addField("Result 4:", url3name, false);
                    builder.addField("Result 5:", url4name, false);

                    event.getChannel().sendMessage(builder.build()).queue((msg -> {
                        try {
                            PrintWriter writer = new PrintWriter(guild.getId() + "_" + event.getAuthor().getId() + "_msgID" + ".txt", "UTF-8");
                            writer.println(msg.getId());
                            writer.close();
                        } catch (FileNotFoundException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }));
                    event.getMessage().delete().queue();
                }
            }
        } else if ("leave".equals(command[1]))
        {
            guild.getAudioManager().setSendingHandler(null);
            guild.getAudioManager().closeAudioConnection();
        } else if (command[1].equals(select)) {
            try {
                FileInputStream fstream = new FileInputStream(guild.getId() + "_" + event.getAuthor().getId() + "_msgID" + ".txt");
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                java.util.ArrayList<String> list = new java.util.ArrayList<>();

                while ((strLine = br.readLine()) != null) {
                    list.add(strLine);
                }


                String msgID = list.get(0);

                in.close();

                event.getChannel().getMessageById(msgID).queue((msg -> msg.delete().queue()));

                try {
                    Files.deleteIfExists(Paths.get(guild.getId() + "_" + event.getAuthor().getId() + "_msgID" + ".txt"));
                } catch (NoSuchFileException | DirectoryNotEmptyException e) {
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

            try {
                FileInputStream fstream = new FileInputStream(guild.getId() + "_" + event.getAuthor().getId() + ".txt");
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                java.util.ArrayList<String> list = new java.util.ArrayList<>();

                while ((strLine = br.readLine()) != null) {
                    list.add(strLine);
                }


                String url0 = list.get(0);
                String url1 = list.get(1);
                String url2 = list.get(2);
                String url3 = list.get(3);
                String url4 = list.get(4);


                in.close();

                try {
                    Files.deleteIfExists(Paths.get(guild.getId() + "_" + event.getAuthor().getId() + ".txt"));
                } catch (NoSuchFileException | DirectoryNotEmptyException e) {
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }


                switch (command[2]) {
                    case "1":
                        selection = 1;
                        break;
                    case "2":
                        selection = 2;
                        break;
                    case "3":
                        selection = 3;
                        break;
                    case "4":
                        selection = 4;
                        break;
                    case "5":
                        selection = 5;
                        break;
                }

                if (selection != 0) {
                    VoiceChannel chan;
                    try {
                        chan = guild.getVoiceChannelById(event.getMember().getVoiceState().getChannel().getId());
                    } catch (NullPointerException e) {
                        event.getChannel().sendMessage("You're not currently in a voice channel please join one and try again").queue();
                        return;
                    }
                    if (chan == null) {
                        return;
                    } else {
                        guild.getAudioManager().setSendingHandler(mng.sendHandler);
                        try {
                            guild.getAudioManager().openAudioConnection(chan);
                        } catch (PermissionException e) {
                            if (e.getPermission() == Permission.VOICE_CONNECT) {
                                event.getChannel().sendMessage("I don't have permission to connect to: " + chan.getName()).queue();
                                return;
                            }
                        }
                    }


                    switch (selection) {
                        case 1:
                            loadAndPlay(mng, event.getChannel(), "https://youtube.com/watch?v=" + url0, false);
                            event.getMessage().delete().queue();
                            break;
                        case 2:
                            loadAndPlay(mng, event.getChannel(), "https://youtube.com/watch?v=" + url1, false);
                            event.getMessage().delete().queue();
                            break;
                        case 3:
                            loadAndPlay(mng, event.getChannel(), "https://youtube.com/watch?v=" + url2, false);
                            event.getMessage().delete().queue();
                            break;
                        case 4:
                            loadAndPlay(mng, event.getChannel(), "https://youtube.com/watch?v=" + url3, false);
                            event.getMessage().delete().queue();
                            break;
                        case 5:
                            loadAndPlay(mng, event.getChannel(), "https://youtube.com/watch?v=" + url4, false);
                            event.getMessage().delete().queue();
                            break;
                        default:
                            event.getTextChannel().sendMessage("Invalid Selection").queue();
                            event.getMessage().delete().queue();
                            break;
                    }
                }


            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

        } else if ("resume".equals(command[1]) | "play".equals(command[1]) && command.length == 2) //It is only the command to start playback (probably after pause)
        {
            if (player.isPaused()) {
                player.setPaused(false);
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription(":play_pause: Playback has been resumed.");
                event.getChannel().sendMessage(builder.build()).queue();
                event.getMessage().delete().queue();
            } else if (player.getPlayingTrack() != null) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription("Player is already playing!");
                event.getChannel().sendMessage(builder.build()).queue();
                event.getMessage().delete().queue();
            }
        } else if ("skip".equals(command[1])) {
            scheduler.nextTrack();
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription(":fast_forward: Track Skipped!");
            event.getChannel().sendMessage(builder.build()).queue();
            event.getMessage().delete().queue();
        } else if ("pause".equals(command[1])) {
            if (player.getPlayingTrack() == null) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription("Cannot pause or resume player because no track is loaded for playing.");
                event.getChannel().sendMessage(builder.build()).queue();
                event.getMessage().delete().queue();
                return;
            }

            player.setPaused(!player.isPaused());
            if (player.isPaused()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription(":play_pause: The player has been paused.");
                event.getChannel().sendMessage(builder.build()).queue();
                event.getMessage().delete().queue();
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription(":play_pause: The player has resumed playing.");
                event.getChannel().sendMessage(builder.build()).queue();
                event.getMessage().delete().queue();
            }
        } else if ("stop".equals(command[1])) {
            scheduler.queue.clear();
            player.stopTrack();
            player.setPaused(false);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription(":stop_button: Playback has been completely stopped and the queue has been cleared.");
            event.getChannel().sendMessage(builder.build()).queue();
            event.getMessage().delete().queue();
        } else if ("volume".equals(command[1])) {
            if (command.length == 2) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription(":speaker: Current player volume: **" + player.getVolume() + "**");
                event.getChannel().sendMessage(builder.build()).queue();
                event.getMessage().delete().queue();
            } else {
                try {
                    int newVolume = Math.max(10, Math.min(100, parseInt(command[2])));
                    int oldVolume = player.getVolume();
                    player.setVolume(newVolume);
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Info");
                    builder.setColor(Color.WHITE);
                    builder.setDescription(":speaker: Player volume changed from `" + oldVolume + "` to `" + newVolume + "`");
                    event.getChannel().sendMessage(builder.build()).queue();
                    event.getMessage().delete().queue();
                } catch (NumberFormatException e) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Info");
                    builder.setColor(Color.WHITE);
                    builder.setDescription(":speaker: `" + command[2] + "` is not a valid integer. (10 - 100)");
                    event.getChannel().sendMessage(builder.build()).queue();
                }
            }
        } else if ("restart".equals(command[1])) {
            AudioTrack track = player.getPlayingTrack();
            if (track == null)
                track = scheduler.lastTrack;
            if (track != null) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription("Restarting track: " + track.getInfo().title);
                event.getChannel().sendMessage(builder.build()).queue();
                event.getMessage().delete().queue();
                player.playTrack(track.makeClone());
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription("No track has been previously started, so the player cannot replay a track!");
                event.getChannel().sendMessage(builder.build()).queue();
                event.getMessage().delete().queue();
            }
        } else if ("reset".equals(command[1])) {
            synchronized (musicManagers) {
                scheduler.queue.clear();
                player.destroy();
                guild.getAudioManager().setSendingHandler(null);
                musicManagers.remove(guild.getId());
            }

            mng = getMusicManager(guild);
            guild.getAudioManager().setSendingHandler(mng.sendHandler);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("The player has been completely reset!");
            event.getChannel().sendMessage(builder.build()).queue();

        } else if ("nowplaying".equals(command[1]) || "np".equals(command[1])) {
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
                event.getMessage().delete().queue();
            }
        } else if ("queue".equals(command[1])) {
            Queue<AudioTrack> queue = scheduler.queue;
            synchronized (queue) {
                if (queue.isEmpty()) {
                    event.getChannel().sendMessage("The queue is currently empty!").queue();
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

                    builder.addField("Current Queue: Entries: " + queue.size(), sb.toString(), false);
                    event.getChannel().sendMessage(builder.build()).queue();
                    event.getMessage().delete().queue();
                }
            }
        } else if ("shuffle".equals(command[1])) {
            if (scheduler.queue.isEmpty()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Info");
                builder.setColor(Color.WHITE);
                builder.setDescription("The queue is currently empty!");
                event.getChannel().sendMessage(builder.build()).queue();
                event.getMessage().delete().queue();
                return;
            }

            scheduler.shuffle();
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("The queue has been shuffled!");
            event.getChannel().sendMessage(builder.build()).queue();
            event.getMessage().delete().queue();
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