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

package ml.enzodevelopment.enzobot;

import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.utils.CommandListGenerationUtils;
import ml.enzodevelopment.enzobot.utils.GuildSettingsUtils;
import ml.enzodevelopment.enzobot.utils.ModUtils;
import ml.enzodevelopment.enzobot.utils.StatsUpdater;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotListener extends ListenerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private Timer delTimer = new Timer();
    private final ScheduledExecutorService systemPool = Executors.newScheduledThreadPool(3,
            r -> new Thread(r, "Bot-Service-Thread"));
    private boolean unbanTimerRunning = false;
    private boolean unmuteTimerRunning = false;

    public static Command getCommand(String alias) {
        for (Command command : EnzoBot.cmdList) {
            for (String commandAlias : command.getAliases()) {
                if (commandAlias.equals(alias)) {
                    return command;
                }
            }
        }
        return null;
    }

    @Override
    public void onReady(ReadyEvent event) {
        CommandListGenerationUtils generationUtils = new CommandListGenerationUtils();
        String code = generationUtils.postAndGenerate();
        logger.info(String.valueOf(code));
        StatsUpdater updater = new StatsUpdater();

        if (!unbanTimerRunning) {
            ModUtils.checkUnbans(event.getJDA());
            logger.info("Starting the unban timer.");
            systemPool.scheduleAtFixedRate(() ->
                    ModUtils.checkUnbans(event.getJDA()), 5, 5, TimeUnit.MINUTES);
            unbanTimerRunning = true;
        }

        if (!unmuteTimerRunning) {
            ModUtils.checkUnmutes(event.getJDA());
            logger.info("Satring the unmute timer");
            systemPool.scheduleAtFixedRate(() ->
                    ModUtils.checkUnmutes(event.getJDA()), 5, 5, TimeUnit.MINUTES);
            unmuteTimerRunning = true;
        }

        updater.StartupdateTimer(event);
        logger.info("EnzoBot is running! Bot should be online.");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String botPrefix = GuildSettingsUtils.getGuild(event.getGuild()).getCustomPrefix();

        String substringMessage = "";
        String msg = event.getMessage().getContentRaw().toLowerCase();
        String args[] = event.getMessage().getContentRaw().split("\\s+");
        if (msg.startsWith(botPrefix)) {
            substringMessage = msg.substring(botPrefix.length());
        }
        String[] parts = substringMessage.split("\\s+");
        Command cmd = getCommand(parts[0]);

        boolean checks = runChecks(event, botPrefix, cmd);
        if (checks && cmd != null) {
            cmd.execute(args, event);
        }
    }

    private boolean runChecks(GuildMessageReceivedEvent event, String botPrefix, Command cmd) {
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_WRITE)) {
            return false;
        } else if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_WRITE)) {
            return false;
        }
        boolean startsWithPrefix = event.getMessage().getContentRaw().startsWith(botPrefix);
        boolean notBot = !event.getMessage().getAuthor().isBot();
        boolean notMusic = !event.getMessage().getContentRaw().startsWith(botPrefix + "m");

        if (startsWithPrefix && notBot && notMusic) {
            String botChannel;
            if (GuildSettingsUtils.getGuild(event.getGuild()).usingBotChannel()) {
                botChannel = GuildSettingsUtils.getGuild(event.getGuild()).getBotChannel();
            } else {
                botChannel = null;
            }
            if (cmd != null && botChannel != null && !botChannel.equals(event.getChannel().getId())) {

                event.getMessage().delete().queue();
                event.getChannel().sendMessage("This is not the bot channel please use " + event.getGuild().getTextChannelById(botChannel).getAsMention() + " for bot commands!").queue((msg2 ->
                        delTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                msg2.delete().queue();
                            }
                        }, 5000)));
                return false;
            }

        }

        return true;
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        event.getJDA().getUserById("205056315351891969").openPrivateChannel().queue((priv -> priv.sendMessage("New guild! Name: " + event.getGuild().getName() + ", Member count: " + event.getGuild().getMembers().size()).queue()));
        GuildSettingsUtils.registerNewGuild(event.getGuild());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        GuildSettingsUtils.deleteGuild(event.getGuild());
    }
}





