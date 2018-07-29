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

package ml.enzodevelopment.enzobot.commands.mod;

import ml.enzodevelopment.enzobot.config.Config;
import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import ml.enzodevelopment.enzobot.utils.GuildSettingsUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class setWarningThresholdCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (args.length < 2) {
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle("Error");
            error.setColor(Config.ENZO_BLUE);
            error.setDescription("Invalid Usage");
            event.getChannel().sendMessage(error.build()).queue();
            return;
        }
        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle("Error");
            error.setColor(Config.ENZO_BLUE);
            error.setDescription("You require permission to manage the server to use this command.");
            event.getChannel().sendMessage(error.build()).queue();
            return;
        }
        if (!isInt(args[1])) {
            return;
        }
        int threshold = Integer.parseInt(args[1]);
        GuildSettingsUtils.updateGuildSettings(event.getGuild(), GuildSettingsUtils.getGuild(event.getGuild()).setWarningThreshold(threshold));
        EmbedBuilder error = new EmbedBuilder();
        error.setTitle("Success");
        error.setColor(Color.WHITE);
        error.setDescription("Max warnings set to: `" + threshold + "`");
        event.getChannel().sendMessage(error.build()).queue();
    }

    @Override
    public String getUsage() {
        return "warningthreshold (amount)";
    }

    @Override
    public String getDesc() {
        return "Sets the max amount of warnings a user can receive before being kicked.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("warningthreshold", "maxwarns"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }

    private static boolean isInt(String integer) {
        return integer.matches("^\\d{1,11}$");
    }

}
