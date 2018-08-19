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

package ml.enzodevelopment.enzobot.commands.basic;

import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuoteCommand implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        for (TextChannel chan : event.getGuild().getTextChannels()) {
            chan.getMessageById(args[1]).queue(msg -> {
                if (msg != null) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(event.getGuild().getMember(msg.getAuthor()).getColor());
                    builder.setDescription(msg.getContentRaw());
                    builder.setAuthor(msg.getAuthor().getName() + "#" + msg.getAuthor().getDiscriminator(), null, msg.getAuthor().getEffectiveAvatarUrl());
                    builder.setTimestamp(msg.getCreationTime());
                    builder.setFooter("#" + msg.getChannel().getName() + " | Sent", null);
                    event.getChannel().sendMessage(builder.build()).queue();

                }
            });
        }
    }

    @Override
    public String getName() {
        return getAliases().get(0);
    }

    @Override
    public String getUsage() {
        return "quote (MessageID)";
    }

    @Override
    public String getDesc() {
        return "Quotes the supplied message.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("quote"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MAIN;
    }
}
