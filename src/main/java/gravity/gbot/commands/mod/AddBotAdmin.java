package gravity.gbot.commands.mod;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.awt.*;
import java.sql.*;

public class AddBotAdmin implements Command {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        boolean adminCheck = GuildConfig.isAdmin(event.getAuthor().getId(), event.getGuild().getId(), event.getJDA());
        if (!adminCheck) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }
        Connection conn;

        try {


            conn =
                    DriverManager.getConnection(Config.dbConnection);

            Statement stmt;
            ResultSet rs;

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM `Config` where guild_ID = " + event.getGuild().getId() + ";");

            Connection conn1;

            try {


                conn1 =
                        DriverManager.getConnection(Config.dbConnection);

                Statement stmt1;

                String currentAdmins = null;

                String mentionToID = args[1].replace("<", "").replace("@", "").replace("!", "").replace(">", "");

                if(rs.next()){
                    currentAdmins = rs.getString("bot_Admins");
                }

                stmt1 = conn1.createStatement();
                stmt1.executeUpdate("UPDATE `Config` SET `bot_Admins` = '" + currentAdmins + "," + mentionToID + "' WHERE `Config`.`guild_ID` = " + event.getGuild().getId() +";");



            } catch (SQLException ex) {
                // handle any errors
                MDC.put("SQLState", ex.getSQLState());
                MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
                logger.error(ex.getMessage());
                MDC.clear();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Bot Admin Added");
            builder.setColor(Color.WHITE);
            builder.setDescription("Success");
            event.getChannel().sendMessage(builder.build()).queue();



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
    public String getUsage() {
        return "addAdmin @member";
    }

    @Override
    public String getDesc() {
        return "Adds a member to the list of bot admins for the guild.";
    }

    @Override
    public String getAlias() {
        return "addadmin";
    }

    @Override
    public String getType() {
        return "admin";
    }
}
