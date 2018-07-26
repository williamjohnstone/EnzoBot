package ml.enzodevelopment.enzobot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import ml.enzodevelopment.enzobot.audio.GuildMusicManager;
import ml.enzodevelopment.enzobot.audio.MusicUtils;
import ml.enzodevelopment.enzobot.config.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;

public class VolumeCommand implements Command {
    private MusicUtils musicUtils = Config.musicUtils;

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        GuildMusicManager mng = musicUtils.getMusicManager(event.getGuild());
        AudioPlayer player = mng.player;
        if (args.length == 1) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription(":speaker: Current player volume: **" + player.getVolume() + "**");
            event.getChannel().sendMessage(builder.build()).queue();
        } else {
            try {
                int newVolume = Math.max(15, Math.min(100, parseInt(args[1])));
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
                builder.setDescription(":speaker: `" + args[1] + "` is not a valid integer. (5 - 100)");
                event.getChannel().sendMessage(builder.build()).queue();
            }
        }
    }

    @Override
    public String getUsage() {
        return "volume (15 - 100)";
    }

    @Override
    public String getDesc() {
        return "Changes the player volume.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("volume", "playerlevel"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }
}
