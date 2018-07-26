package ml.enzodevelopment.enzobot.commands.music;

import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.CommandCategory;
import ml.enzodevelopment.enzobot.music.GuildMusicManager;
import ml.enzodevelopment.enzobot.music.MusicUtils;
import ml.enzodevelopment.enzobot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShuffleCommand implements Command {
    private MusicUtils musicUtils = Config.musicUtils;

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        GuildMusicManager mng = musicUtils.getMusicManager(event.getGuild());
        if (mng.scheduler.queue.isEmpty()) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("The queue is currently empty!");
            event.getChannel().sendMessage(builder.build()).queue();
            return;
        }
        mng.scheduler.shuffle();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Info");
        builder.setColor(Color.WHITE);
        builder.setDescription("The queue has been shuffled!");
        event.getChannel().sendMessage(builder.build()).queue();

    }

    @Override
    public String getUsage() {
        return "shuffle";
    }

    @Override
    public String getDesc() {
        return "Shuffles the player.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("shuffle", "randomise"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }
}
