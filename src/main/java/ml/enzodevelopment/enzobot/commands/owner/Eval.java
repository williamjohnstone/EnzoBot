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

package ml.enzodevelopment.enzobot.commands.owner;

import ml.enzodevelopment.enzobot.BuildConfig;
import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import ml.enzodevelopment.enzobot.config.Config;
import ml.enzodevelopment.enzobot.utils.GuildSettingsUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Eval implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(BuildConfig.OWNER_ID)) {
            event.getChannel().sendMessage("This Command is reserved for the bot owner.").queue(null, null);
        } else {

            ScriptEngine engine;
            ScriptEngineManager factory = new ScriptEngineManager();

            engine = factory.getEngineByName("groovy");


            try {
                engine.put("Config", Config.class);
                engine.put("event", event);
                Object out = engine.eval(event.getMessage().getContentRaw().replace(GuildSettingsUtils.getGuild(event.getGuild()).getCustomPrefix() + "eval ", ""));
                if (out != null) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Successfully Evaluated");
                    builder.setColor(Color.WHITE);
                    builder.setDescription(out.toString());
                    event.getChannel().sendMessage(builder.build()).queue();
                }
            } catch (ScriptException e) {
                event.getChannel().sendMessage(e.getMessage()).queue();
            }

        }
    }

    @Override
    public String getName() {
        return getAliases().get(0);
    }

    @Override
    public String getUsage() {
        return "eval (Java/Groovy Code)";
    }

    @Override
    public String getDesc() {
        return "Executes Java/Groovy Code and sends the output to a text channel.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("eval"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.UNLISTED;
    }
}
