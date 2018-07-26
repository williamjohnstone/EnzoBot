package ml.enzodevelopment.enzobot.commands.music;

import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import ml.enzodevelopment.enzobot.audio.GuildMusicManager;
import ml.enzodevelopment.enzobot.utils.MusicUtils;
import ml.enzodevelopment.enzobot.config.Config;
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
                    boolean playlist;
                    if (sb.toString().toLowerCase().contains("playlist")) {
                        playlist = true;
                        link = GetJson.getLink(String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s&maxResults=5&type=playlist&key=%s", sb, Config.google_api));
                    } else {
                        playlist = false;
                        link = GetJson.getLink(String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s&maxResults=5&type=video&key=%s", sb, Config.google_api));
                    }

                    if (link == null) {
                        return;
                    }
                    JSONObject results = new JSONObject(link);

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Search Results");
                    builder.setColor(Color.WHITE);
                    for (int i = 1; i <= 5;) {
                        builder.addField("Result " + i + ":", musicUtils.searchTitle(i-1, results), false);
                        i++;
                    }
                    builder.setFooter("React with your selection.", event.getJDA().getSelfUser().getAvatarUrl());

                    List<String> videoIds = new ArrayList<>();
                    for (int i = 0; i <= 4;) {
                        videoIds.add(musicUtils.searchId(i, results, playlist));
                        i++;
                    }

                    event.getChannel().sendMessage(builder.build()).queue((msg -> {
                        new EventAwaiter().awaitEvent(event.getJDA(), MessageReactionAddEvent.class,
                                e -> e.getUser().getId().equals(event.getAuthor().getId()) &&
                                        (e.getReactionEmote().getName().equals("\u0031\u20E3")
                                                || e.getReactionEmote().getName().equals("\u0032\u20E3")
                                                || e.getReactionEmote().getName().equals("\u0033\u20E3")
                                                || e.getReactionEmote().getName().equals("\u0034\u20E3")
                                                || e.getReactionEmote().getName().equals("\u0035\u20E3")),
                                e -> musicUtils.play(e.getReactionEmote().getName(), event, mng,
                                        videoIds,
                                        e.getMessageId(), event.getGuild(), playlist, event.getAuthor()),
                                30, TimeUnit.SECONDS, () -> msg.delete().queue());
                        msg.addReaction("\u0031\u20E3").queue();
                        msg.addReaction("\u0032\u20E3").queue();
                        msg.addReaction("\u0033\u20E3").queue();
                        msg.addReaction("\u0034\u20E3").queue();
                        msg.addReaction("\u0035\u20E3").queue();
                    }), null);
                }
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
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }
}
