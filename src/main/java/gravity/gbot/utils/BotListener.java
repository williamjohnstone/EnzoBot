package gravity.gbot.utils;

import gravity.gbot.Command;
import gravity.gbot.Main;
import gravity.gbot.commands.HelpCommand;
import gravity.gbot.utils.Logging.msgLogger;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.sql.*;
import java.util.List;

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
        statsUpdater updater = new statsUpdater();
        System.out.println("[GravityBot] GravityBot is running! Bot should be online.");
        updater.StartupdateTimer(event);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
            GuildConfig config = new GuildConfig();
            msgLogger logMsg = new msgLogger();

            logMsg.log(event);

            String BotPrefix = config.getPrefix(event.getGuild().getId(), this.getClass().getName());
            boolean startsWithPrefix = event.getMessage().getContentRaw().startsWith(BotPrefix);
            boolean notBot = !event.getMessage().getAuthor().isBot();
            boolean notMusic = !event.getMessage().getContentRaw().startsWith(BotPrefix + "m");

            if (startsWithPrefix && notBot && notMusic) {

                String args[] = event.getMessage().getContentRaw().split(" +");
                Command cmd = getCommand(args[0].toLowerCase().replace(BotPrefix, ""));
                String msg = event.getMessage().getContentRaw().toLowerCase();

                if (cmd != null && !msg.startsWith(BotPrefix + "help")) {
                    cmd.execute(args, event);
                }

                    //Help Command
                     if (msg.startsWith(BotPrefix + "help")) {
                        if (args.length == 1 && cmd != null) {
                            cmd.execute(args, event);
                        } else {
                            Command Help_cmd = getCommand(args[1].toLowerCase());
                            if (Help_cmd != null)
                                help.HelpSpecific(args, event, Help_cmd.cmdDesc(), Help_cmd.cmdUsage(), Help_cmd.getAlias());
                            }
                        }
                    }
                }




    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        VoiceChannel chan;
        Guild guild = event.getGuild();
        chan = event.getChannelLeft();
        List<Member> memList = chan.getMembers();
        if (memList.stream().allMatch(guild.getSelfMember()::equals)) {
            guild.getAudioManager().setSendingHandler(null);
            guild.getAudioManager().closeAudioConnection();
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        event.getJDA().getUserById("205056315351891969").openPrivateChannel().queue((priv -> event.getGuild().getTextChannels().get(0).createInvite().queue((invite -> priv.sendMessage("https://discord.gg/invite/" + invite.getCode()).queue()))));
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





