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
import ml.enzodevelopment.enzobot.config.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoinFlipCommand implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        int outcome = (int) (Math.random() * 2 + 1);
        EmbedBuilder flipBuilder = new EmbedBuilder();
        flipBuilder.setTitle("Coin Flip");
        flipBuilder.setColor(Config.ENZO_BLUE);
        if  (outcome == 1) {
            flipBuilder.setDescription("You got Heads");
            flipBuilder.setImage("https://duncte123.me/img/coin/heads.png");
        } else if (outcome == 2) {
            flipBuilder.setDescription("You got Tails");
            flipBuilder.setImage("https://duncte123.me/img/coin/tails.png");
        } else {
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle("Error");
            error.setDescription("Invalid command usage!");
            error.setColor(Config.ENZO_BLUE);
            event.getChannel().sendMessage(error.build()).queue();
            return;
        }
        event.getChannel().sendMessage(flipBuilder.build()).queue();
    }

    @Override
    public String getUsage() {
        return "coinFlip";
    }

    @Override
    public String getDesc() {
        return "Flips a coin.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("coinflip", "coin"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MAIN;
    }
}
