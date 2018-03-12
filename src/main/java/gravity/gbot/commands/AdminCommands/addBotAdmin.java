package gravity.gbot.commands.AdminCommands;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.sql.*;

public class addBotAdmin implements Command {

    private final String Usage = "addAdmin @member";
    private final String Desc = "Adds a member to the list of bot admins for the guild.";
    private final String Alias = "addadmin";
    private final String type = "admin";

    Config config = new Config();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        String admincheck = config.isAdmin(event.getAuthor().getId(), event.getGuild().getId());
        if (admincheck == null) {
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
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
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
            event.getMessage().delete().queue();



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
    public String cmdUsage() {
        return Usage;
    }

    @Override
    public String cmdDesc() {
        return Desc;
    }

    @Override
    public String getAlias() {
        return Alias;
    }

    @Override
    public String cmdType() {
        return type;
    }
}
