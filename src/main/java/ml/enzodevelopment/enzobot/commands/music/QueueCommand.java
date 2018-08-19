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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
import java.util.Queue;

public class QueueCommand implements Command {
    private MusicUtils musicUtils = Config.musicUtils;

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        GuildMusicManager mng = musicUtils.getMusicManager(event.getGuild());
        TrackScheduler scheduler = mng.scheduler;
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
                        sb.append("`[").append(musicUtils.getTimestamp(track.getDuration())).append("]` ");
                        sb.append(track.getInfo().title).append("\n");
                        trackCount++;
                    }
                }
                sb.append("\n").append("Total Queue Time Length: ").append(musicUtils.getTimestamp(queueLength));
                String queueString = sb.toString();
                if (queueString.length() > 800) {
                    trackCount = 0;
                    queueLength = 0;
                    sb = new StringBuilder();
                    for (AudioTrack track : queue) {
                        queueLength += track.getDuration();
                        if (trackCount < 5) {
                            sb.append("`[").append(musicUtils.getTimestamp(track.getDuration())).append("]` ");
                            sb.append(track.getInfo().title).append("\n");
                            trackCount++;
                        }
                    }
                }

                builder.addField("Current Queue: Entries: " + queue.size(), sb.toString(), false);
                event.getChannel().sendMessage(builder.build()).queue();
            }

        }
    }

    @Override
    public String getName() {
        return getAliases().get(0);
    }

    @Override
    public String getUsage() {
        return "queue";
    }

    @Override
    public String getDesc() {
        return "Displays the player queue.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("queue", "q"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }
}
