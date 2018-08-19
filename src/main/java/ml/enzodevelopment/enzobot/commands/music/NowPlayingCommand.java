/*
 * Enzo Bot, a multipurpose discord bot
 *
 * Copyright (c) 2018 William "Enzo" Johnstone
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package ml.enzodevelopment.enzobot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import ml.enzodevelopment.enzobot.audio.GuildMusicManager;
import ml.enzodevelopment.enzobot.utils.MusicUtils;
import ml.enzodevelopment.enzobot.config.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NowPlayingCommand implements Command {
    private MusicUtils musicUtils = Config.musicUtils;

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        GuildMusicManager mng = musicUtils.getMusicManager(event.getGuild());
        AudioPlayer player = mng.player;
        AudioTrack currentTrack = player.getPlayingTrack();
        if (currentTrack != null) {

            Long current = currentTrack.getPosition();
            Long total = currentTrack.getDuration();

            String position = musicUtils.getTimestamp(currentTrack.getPosition());
            String duration = musicUtils.getTimestamp(currentTrack.getDuration());

            String Time = String.format("(%s / %s)", position, duration);
            String progressBar = musicUtils.getProgressBar(current, total);

            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Queue");
            builder.setColor(Color.WHITE);
            builder.setDescription("Now Playing");
            builder.addField("Title", currentTrack.getInfo().title, true);
            builder.addField("Author", currentTrack.getInfo().author, true);
            builder.addField("Duration", duration, true);
            builder.addField("URL", currentTrack.getInfo().uri, true);
            builder.addField("Time", progressBar + " " + Time, false);
            event.getChannel().sendMessage(builder.build()).queue();

        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Info");
            builder.setColor(Color.WHITE);
            builder.setDescription("The player is not currently playing anything!");
            event.getChannel().sendMessage(builder.build()).queue();
        }
    }

    @Override
    public String getName() {
        return getAliases().get(0);
    }

    @Override
    public String getUsage() {
        return "nowplaying";
    }

    @Override
    public String getDesc() {
        return "Displays the currently playing track.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("nowplaying", "playing", "np"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }
}
