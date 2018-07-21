package ml.enzodevelopment.enzobot.commands.music;

import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.music.GuildMusicManager;
import ml.enzodevelopment.enzobot.music.MusicUtils;
import ml.enzodevelopment.enzobot.utils.Config;
import ml.enzodevelopment.enzobot.utils.EventAwaiter;
import ml.enzodevelopment.enzobot.utils.GetJson;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayCommand implements Command {

    private MusicUtils musicUtils = Config.musicUtils;

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        GuildMusicManager mng = musicUtils.getMusicManager(event.getGuild());
        if (args.length >= 2) {
            if (args[1].startsWith("http://") || args[1].startsWith("https://")) {
                boolean join = musicUtils.join(event.getGuild(), event, mng);
                if (!join) {
                    return;
                }
                {
                    if (args[1].contains("&list=") || args[1].contains("?list=")) {
                        musicUtils.loadAndPlay(mng, event.getChannel(), args[1], true, event.getAuthor());
                    } else {
                        musicUtils.loadAndPlay(mng, event.getChannel(), args[1], false, event.getAuthor());
                    }
                }
            } else {
                {

                    StringBuilder sb = new StringBuilder();
                    for (String s : args) {
                        sb.append(s).append("+");
                    }
                    sb.delete(0, 6);
                    String link;
                    String type;
                    if (sb.toString().toLowerCase().contains("playlist")) {
                        type = "playlist";
                        link = GetJson.getLink(String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s&maxResults=5&type=playlist&key=%s", sb, Config.google_api));
                    } else {
                        type = "video";
                        link = GetJson.getLink(String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s&maxResults=5&type=video&key=%s", sb, Config.google_api));
                    }

                    if (link == null) {
                        return;
                    }
                    JSONObject results = new JSONObject(link);

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Search Results");
                    builder.setColor(Color.WHITE);
                    builder.addField("Result 1:", musicUtils.searchTitle(0, results), false);
                    builder.addField("Result 2:", musicUtils.searchTitle(1, results), false);
                    builder.addField("Result 3:", musicUtils.searchTitle(2, results), false);
                    builder.addField("Result 4:", musicUtils.searchTitle(3, results), false);
                    builder.addField("Result 5:", musicUtils.searchTitle(4, results), false);
                    builder.setFooter("React with your selection.", event.getJDA().getSelfUser().getAvatarUrl());

                    event.getChannel().sendMessage(builder.build()).queue((msg -> {
                        new EventAwaiter().awaitEvent(event.getJDA(), MessageReactionAddEvent.class,
                                e -> e.getUser().getId().equals(event.getAuthor().getId()) &&
                                        (e.getReactionEmote().getName().equals("\u0031\u20E3")
                                                || e.getReactionEmote().getName().equals("\u0032\u20E3")
                                                || e.getReactionEmote().getName().equals("\u0033\u20E3")
                                                || e.getReactionEmote().getName().equals("\u0034\u20E3")
                                                || e.getReactionEmote().getName().equals("\u0035\u20E3")),
                                e -> musicUtils.play(e.getReactionEmote().getName(), event, mng,
                                        musicUtils.searchId(0, results, type),
                                        musicUtils.searchId(1, results, type),
                                        musicUtils.searchId(2, results, type),
                                        musicUtils.searchId(3, results, type),
                                        musicUtils.searchId(4, results, type),
                                        e.getMessageId(), event.getGuild(), type, event.getAuthor()),
                                30, TimeUnit.SECONDS, () -> msg.delete().queue());
                        msg.addReaction("\u0031\u20E3").queue();
                        msg.addReaction("\u0032\u20E3").queue();
                        msg.addReaction("\u0033\u20E3").queue();
                        msg.addReaction("\u0034\u20E3").queue();
                        msg.addReaction("\u0035\u20E3").queue();
                    }), null);
                }
            }
        } else if (args.length == 1) {
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

    }

    @Override
    public String getUsage() {
        return "play (url or song name)";
    }

    @Override
    public String getDesc() {
        return "Plays the supplied url or does a youtube search and returns the output if no url is supplied.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("play", "p"));
    }

    @Override
    public String getType() {
        return "music";
    }
}
