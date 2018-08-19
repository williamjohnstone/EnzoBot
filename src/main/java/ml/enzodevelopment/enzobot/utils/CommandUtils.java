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

package ml.enzodevelopment.enzobot.utils;

import ml.enzodevelopment.enzobot.EnzoBot;
import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import ml.enzodevelopment.enzobot.objects.command.WebCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ml.enzodevelopment.enzobot.BotListener.getCommand;

public class CommandUtils {

    public static List<WebCommand> getWebCommands() {
        List<Command> sorted = getSortedCommands();
        List<WebCommand> commands = new ArrayList<>();
        for (Command cmd: sorted) {
            commands.add(new WebCommand(cmd.getName(), cmd.getUsage(), cmd.getDesc(), cmd.getAliases()));
        }
        return commands;
    }

    private static List<Command> getSortedCommands() {
        List<Command> commandSet = new ArrayList<>();
        List<String> names = new ArrayList<>();
        EnzoBot.cmdList.stream().filter(cmd -> cmd.getCategory() != CommandCategory.UNLISTED)
                .collect(Collectors.toSet()).forEach(c -> names.add(c.getAliases().get(0)));
        Collections.sort(names);
        names.forEach(n -> commandSet.add(getCommand(n)));
        return new ArrayList<>(commandSet);
    }

}
