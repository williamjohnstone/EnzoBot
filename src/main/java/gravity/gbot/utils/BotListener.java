package gravity.gbot.utils;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import gravity.gbot.Command;
import gravity.gbot.Main;
import gravity.gbot.commands.HelpCommand;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.sql.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BotListener extends ListenerAdapter {

    private final HelpCommand help = new HelpCommand();

    private Command getCommand(String alias) {
        for (Command command : Main.cmdlist) {
            if (command.getAlias().equals(alias)) {
                return command;
            }
        }
        return null;
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("[GravityBot] GravityBot is running! Bot should be online.");
        int MINUTES = 5; // The delay in minutes
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() { // Function runs every MINUTES minutes.
                event.getJDA().getPresence().setGame(Game.watching(event.getJDA().getGuildCache().size() + " servers! | g-bot.tk"));
                String token = Config.API_Key;
                String botId = "391558265265192961";

                int serverCount = (int)event.getJDA().getGuildCache().size();

                Connection conn;
                try {
                    conn =
                            DriverManager.getConnection(Config.dbConnection);
                    Statement stmt;
                    stmt = conn.createStatement();
                    stmt.executeUpdate("UPDATE `API` SET `server_count` = '" + serverCount + "' WHERE `API`.`ID` = 1;");
                    conn.close();
                } catch (SQLException ex) {
                    // handle any errors
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONObject obj = new JSONObject()
                        .put("server_count", serverCount);

                try {
                    Unirest.post("https://discordbots.org/api/bots/" + botId + "/stats")
                            .header("Authorization", token)
                            .header("Content-Type", "application/json")
                            .body(obj.toString())
                            .asJson();
                } catch (UnirestException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000 * 60 * MINUTES);
        // 1000 milliseconds in a second * 60 per minute * the MINUTES variable.
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (Config.loggingALL) {
            if (!event.getAuthor().isBot()) {
                if (event.getChannelType() == ChannelType.PRIVATE) {
                    System.out.println("[GravityBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannelType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw());
                } else if (event.getChannelType() == ChannelType.TEXT) {
                    System.out.println("[GravityBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannelType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw() + ", Guild (Server): " + event.getGuild().getName());
                }
            }
        }
            GuildConfig config = new GuildConfig();

            String BotPrefix = config.getPrefix(event.getGuild().getId());
            boolean startsWithPrefix = event.getMessage().getContentRaw().startsWith(BotPrefix);
            boolean notBot = !event.getMessage().getAuthor().isBot();
            boolean notMusic = !event.getMessage().getContentRaw().startsWith(BotPrefix + "m");

            if (startsWithPrefix && notBot && notMusic) {
                if (Config.loggingCMD && !Config.loggingALL) {
                    if (!event.getAuthor().isBot()) {
                        if (event.getChannelType() == ChannelType.PRIVATE) {
                            System.out.println("[GravityBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannelType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw());
                        } else if (event.getChannelType() == ChannelType.TEXT) {
                            System.out.println("[GravityBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannelType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw() + ", Guild (Server): " + event.getGuild().getName());
                        }
                    }
                }

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
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
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
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





