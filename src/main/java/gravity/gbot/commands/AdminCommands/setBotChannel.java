package gravity.gbot.commands.AdminCommands;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class setBotChannel implements Command {

    private final String Usage = "setBotChat in the channel you want to use as the bot chat";
    private final String Desc = "Changes the Bots Nickname";
    private final String Alias = "setbotchat";
    private final String type = "admin";

    GuildConfig config = new GuildConfig();


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

            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE `Config` SET `bot_Channel_ID` = '" + event.getChannel().getId() + "' WHERE `Config`.`guild_ID` = " + event.getGuild().getId() +";");



        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Bot Channel Set");
        builder.setColor(Color.WHITE);
        builder.setDescription("Success");
        event.getChannel().sendMessage(builder.build()).queue();
        event.getMessage().delete().queue();
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
