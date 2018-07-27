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

import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import ml.enzodevelopment.enzobot.config.Config;
import ml.enzodevelopment.enzobot.objects.command.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RollCommand implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (args.length == 2) {
            int max;
            try {
                max = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                EmbedBuilder error = new EmbedBuilder();
                error.setTitle("Error");
                error.setDescription("Invalid command usage!");
                error.setColor(Config.ENZO_BLUE);
                event.getChannel().sendMessage(error.build()).queue();
                return;
            }

            int outcome;

            EmbedBuilder rolling = new EmbedBuilder();
            rolling.setColor(Config.ENZO_BLUE);
            rolling.setTitle("Roll");
            rolling.setDescription("Rolling random number between 0 and " + max);
            event.getChannel().sendMessage(rolling.build()).queue();
            event.getChannel().sendTyping().queue();

            outcome = (int) (Math.random() * max + 1);

            EmbedBuilder result = new EmbedBuilder();
            result.setColor(Config.ENZO_BLUE);
            result.setTitle("Success");
            result.setDescription("Your number is " + outcome + "!");
            event.getChannel().sendMessage(result.build()).queueAfter(2, TimeUnit.SECONDS);

        } else {
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle("Error");
            error.setDescription("Invalid command usage!");
            error.setColor(Config.ENZO_BLUE);
            event.getChannel().sendMessage(error.build()).queue();
        }
    }

    @Override
    public String getUsage() {
        return "roll (Maximum Number)";
    }

    @Override
    public String getDesc() {
        return "Rolls a random number between 0 and the supplied number.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("roll"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MAIN;
    }
}
