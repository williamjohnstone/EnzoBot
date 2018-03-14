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

public class setPrefix implements Command {

    private final String Usage = "setPrefix (Prefix)";
    private final String Desc = "Changes the Bots Prefix";
    private final String Alias = "setprefix";
    private final String type = "admin";

    private GuildConfig config = new GuildConfig();

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
            if (event.getMessage().getContentRaw().replace(args[0] + " ", "").contains(" ")) {
                event.getChannel().sendMessage("Error guild prefix CANNOT contain a space!").queue();
            }
            stmt.executeUpdate("UPDATE `Config` SET `Prefix` = '" + args[1] + "' WHERE `Config`.`guild_ID` = " + event.getGuild().getId() +";");



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
        builder.setTitle("Bot Prefix Set");
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
