package gravity.gbot.utils;

import gravity.gbot.Command;
import gravity.gbot.Main;
import gravity.gbot.commands.basic.HelpCommand;
import gravity.gbot.utils.logging.MessageLogger;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class BotListener extends ListenerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public static Command getCommand(String alias) {
        for (Command command : Main.cmdlist) {
            if (command.getAlias().equals(alias)) {
                return command;
            }
        }
        return null;
    }

    @Override
    public void onReady(ReadyEvent event) {
        StatsUpdater updater = new StatsUpdater();
        logger.info("GravityBot is running! Bot should be online.");
        updater.StartupdateTimer(event);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String botPrefix = GuildConfig.getPrefix(event.getGuild().getId(), this.getClass().getName());
        MessageLogger.logMessage(event, botPrefix);

        String substringMessage = "";
        String msg = event.getMessage().getContentRaw().toLowerCase();
        String args[] = event.getMessage().getContentRaw().split("\\s+");
        if (msg.startsWith(botPrefix)) {
            substringMessage = msg.substring(botPrefix.length());
        }
        String[] parts = substringMessage.split("\\s+");
        String commandName = parts[0];
        Command cmd = getCommand(commandName);

        boolean checks = runChecks(event, botPrefix, cmd);
        if (checks) {

            if (cmd != null && !msg.startsWith(botPrefix + "help")) {
                try {
                    cmd.execute(args, event);
                } catch (InsufficientPermissionException e) {
                    return;
                }
            }

            //Help Command
            if (msg.startsWith(botPrefix + "help")) {
                if (args.length == 1 && cmd != null) {
                    cmd.execute(args, event);
                } else {
                    if (args.length == 2) {
                        Command Help_cmd = getCommand(args[1].toLowerCase());
                        if (Help_cmd != null)
                            HelpCommand.getSpecififcHelp(args, event, Help_cmd.getDesc(), Help_cmd.getUsage(), Help_cmd.getAlias());

                    }
                }
            }
        }
    }

    private boolean runChecks(GuildMessageReceivedEvent event, String botPrefix, Command cmd) {

        boolean startsWithPrefix = event.getMessage().getContentRaw().startsWith(botPrefix);
        boolean notBot = !event.getMessage().getAuthor().isBot();
        boolean notMusic = !event.getMessage().getContentRaw().startsWith(botPrefix + "m");

        if (startsWithPrefix && notBot && notMusic) {

            if (Config.dev_mode) {
                if (event.getChannel() != event.getGuild().getTextChannelById(Config.BOT_DEV_CHANNEL)) {
                    return false;
                }
            } else if (event.getJDA().getGuildById("367273834128080898") == event.getGuild() && event.getChannel() == event.getGuild().getTextChannelById(Config.BOT_DEV_CHANNEL)) {
                return false;
            }

            String botChannel = GuildConfig.getBotChannel(event.getGuild().getId(), this.getClass().getName());
            if (cmd != null && botChannel != null && !botChannel.equals(event.getChannel().getId())) {

                event.getMessage().delete().queue();
                event.getChannel().sendMessage("This is not the bot channel please use " + event.getGuild().getTextChannelById(botChannel).getAsMention() + " for bot commands!").queue((msg2 ->
                {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            msg2.delete().queue();
                        }
                    }, 5000);
                }));
                return false;
            }

        }
        return true;
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        try {
            event.getJDA().getUserById("205056315351891969").openPrivateChannel().queue((priv -> event.getGuild().getTextChannels().get(0).createInvite().queue((invite -> priv.sendMessage("https://discord.gg/invite/" + invite.getCode()).queue()))));
        } catch (InsufficientPermissionException e) {
            event.getJDA().getUserById("205056315351891969").openPrivateChannel().queue((priv -> priv.sendMessage("New guild! Name: " + event.getGuild().getName() + ", Member count: " + event.getGuild().getMembers().size()).queue()));
        }
        Database db = new Database(Config.dbConnection);
        db.init();
        db.executeUpdate("INSERT INTO `Config` (`ID`, `guild_ID`, `Prefix`, `bot_Channel_ID`, `bot_Admins`) VALUES (NULL, '" + event.getGuild().getId() + "', '!', '0', '" + event.getGuild().getOwner().getUser().getId() + "');");
        db.close();
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        Database db = new Database(Config.dbConnection);
        db.init();
        db.executeUpdate("DELETE FROM `Config` WHERE `Config`.`guild_ID` = " + event.getGuild().getId() + ";");
        db.close();
    }
}





