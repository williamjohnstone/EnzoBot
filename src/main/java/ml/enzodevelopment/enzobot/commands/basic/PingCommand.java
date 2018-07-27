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
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PingCommand implements Command {


    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {

        long time = System.currentTimeMillis();
        event.getChannel().sendMessage("PONG!").queue((message) -> message.editMessageFormat("PONG!%nRoundtrip took: %dms%nWebsocket ping is: " + event.getJDA().getPing() + "ms", System.currentTimeMillis() - time).queue());
    }

    @Override
    public String getUsage() {
        return "ping";
    }

    @Override
    public String getDesc() {
        return "Replies with Ping times this is used to check if the bot is alive.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("ping", "pong"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MAIN;
    }
}

