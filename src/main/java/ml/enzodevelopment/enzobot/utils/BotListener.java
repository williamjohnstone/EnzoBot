package ml.enzodevelopment.enzobot.utils;

import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class BotListener extends ListenerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private GuildConfig guildConfig = new GuildConfig();

    public static Command getCommand(String alias) {
        for (Command command : Main.cmdlist) {
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
        StatsUpdater updater = new StatsUpdater();
        logger.info("EnzoBot is running! Bot should be online.");
        updater.StartupdateTimer(event);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String botPrefix = guildConfig.getPrefix(event.getGuild().getId());

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

            if (Config.dev_mode) {
                if (event.getChannel() != event.getGuild().getTextChannelById(Config.BOT_DEV_CHANNEL)) {
                    return false;
                }
            } else if (event.getJDA().getGuildById("367273834128080898") == event.getGuild() && event.getChannel() == event.getGuild().getTextChannelById(Config.BOT_DEV_CHANNEL)) {
                return false;
            }

            String botChannel = guildConfig.getBotChannel(event.getGuild().getId());
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
        event.getJDA().getUserById("205056315351891969").openPrivateChannel().queue((priv -> priv.sendMessage("New guild! Name: " + event.getGuild().getName() + ", Member count: " + event.getGuild().getMembers().size()).queue()));
        Config.DB.run(() -> {
            try {
                Connection conn = Config.DB.getConnManager().getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO `Config` (`ID`, `guild_ID`, `Prefix`, `bot_Channel_ID`, `bot_Admins`) VALUES (NULL, '?', '?', '?', '?');");
                stmt.setInt(1, (int) event.getGuild().getIdLong());
                stmt.setString(2, "!");
                stmt.setInt(3, 0);
                stmt.setString(4, event.getGuild().getOwner().getUser().getId());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                logger.error("Database Error", ex);
            }
        });
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        Config.DB.run(() -> {
            try {
                Connection conn = Config.DB.getConnManager().getConnection();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM `Config` WHERE `Config`.`guild_ID` = ?;");
                stmt.setString(1, event.getGuild().getId());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                logger.error("Database Error", ex);
            }
        });
    }
}





