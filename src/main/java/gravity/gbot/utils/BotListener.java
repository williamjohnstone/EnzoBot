package gravity.gbot.utils;

import gravity.gbot.Command;
import gravity.gbot.Main;
import gravity.gbot.commands.basic.HelpCommand;
import gravity.gbot.utils.Logging.msgLogger;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class BotListener extends ListenerAdapter {

    private final HelpCommand help = new HelpCommand();
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public Command getCommand(String alias) {
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
        System.out.println(event.getMessage().getContentRaw());
        if (Config.dev_mode) {
            if (event.getChannel() != event.getGuild().getTextChannelById(Config.dev_bot_channel)) {
                return;
            }
        } else {
            if (event.getJDA().getGuildById("367273834128080898") == event.getGuild()) {
                if (event.getChannel() == event.getGuild().getTextChannelById(Config.dev_bot_channel)) {
                    return;
                }
            }
        }
        GuildConfig config = new GuildConfig();
        msgLogger logMsg = new msgLogger();
        String channelBot = config.isBotChannel(event.getGuild().getId(), this.getClass().getName());
        String admin = config.isAdmin(event.getAuthor().getId(), event.getGuild().getId(), event.getJDA());
        String BotPrefix = config.getPrefix(event.getGuild().getId(), this.getClass().getName());
        logMsg.log(event, BotPrefix);

        boolean startsWithPrefix = event.getMessage().getContentRaw().startsWith(BotPrefix);
        boolean notBot = !event.getMessage().getAuthor().isBot();
        boolean notMusic = !event.getMessage().getContentRaw().startsWith(BotPrefix + "m");

        if (startsWithPrefix && notBot && notMusic) {

            String msg = event.getMessage().getContentRaw().toLowerCase();
            String msg1 = event.getMessage().getContentRaw().toLowerCase();
            String args[] = event.getMessage().getContentRaw().split("\\s+");
            if (msg1.startsWith(BotPrefix)) {
                msg1 = msg1.substring(BotPrefix.length());
            }
            String[] parts = msg1.split("\\s+");
            String commandName = parts[0];
            Command cmd = getCommand(commandName);

            if (cmd != null) {
                if (channelBot != null) {
                    if (!channelBot.equals(event.getChannel().getId())) {
                        if (admin == null) {
                            event.getMessage().delete().queue();
                            event.getChannel().sendMessage("This is not the bot channel please use " + event.getGuild().getTextChannelById(channelBot).getAsMention() + " for bot commands!").queue((msg2 ->
                            {
                                Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        msg2.delete().queue();
                                    }
                                }, 5000);
                            }));
                            return;
                        }
                    }
                }
            }
            if (cmd != null && !msg.startsWith(BotPrefix + "help")) {
                try {
                    cmd.execute(args, event);
                } catch (InsufficientPermissionException e) {
                    return;
                }
            }

            //Help Command
            if (msg.startsWith(BotPrefix + "help")) {
                if (args.length == 1 && cmd != null) {
                    try {
                        cmd.execute(args, event);
                    } catch (InsufficientPermissionException e) {
                        return;
                    }
                } else {
                    if (args.length == 2) {
                        Command Help_cmd = getCommand(args[1].toLowerCase());
                        if (Help_cmd != null)
                            try {
                                help.HelpSpecific(args, event, Help_cmd.cmdDesc(), Help_cmd.cmdUsage(), Help_cmd.getAlias());
                            } catch (InsufficientPermissionException e) {
                                return;
                            }
                    }
                }
            }
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        try {
            event.getJDA().getUserById("205056315351891969").openPrivateChannel().queue((priv -> event.getGuild().getTextChannels().get(0).createInvite().queue((invite -> priv.sendMessage("https://discord.gg/invite/" + invite.getCode()).queue()))));
        } catch (InsufficientPermissionException e) {
            event.getJDA().getUserById("205056315351891969").openPrivateChannel().queue((priv -> priv.sendMessage("New guild! Name: " + event.getGuild().getName() + ", Member count: " + event.getGuild().getMembers().size()).queue()));
        }
        Connection conn;
        try {
            conn =
                    DriverManager.getConnection(Config.dbConnection);
            Statement stmt;
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO `Config` (`ID`, `guild_ID`, `Prefix`, `bot_Channel_ID`, `bot_Admins`) VALUES (NULL, '" + event.getGuild().getId() + "', '!', '0', '" + event.getGuild().getOwner().getUser().getId() + "');");
            conn.close();
        } catch (SQLException ex) {
            // handle any errors
            MDC.put("SQLState", ex.getSQLState());
            MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
            logger.error(ex.getMessage());
            MDC.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        Connection conn;
        try {
            conn =
                    DriverManager.getConnection(Config.dbConnection);
            Statement stmt;
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM `Config` WHERE `Config`.`guild_ID` = " + event.getGuild().getId() + ";");
            conn.close();
        } catch (SQLException ex) {
            // handle any errors
            MDC.put("SQLState", ex.getSQLState());
            MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
            logger.error(ex.getMessage());
            MDC.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





