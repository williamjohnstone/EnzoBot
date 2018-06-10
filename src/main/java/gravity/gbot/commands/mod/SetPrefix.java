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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SetPrefix implements Command {

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
            conn = DriverManager.getConnection(Config.dbConnection);
            Statement stmt;
            stmt = conn.createStatement();
            if (event.getMessage().getContentRaw().replace(args[0] + " ", "").contains(" ")) {
                event.getChannel().sendMessage("Error guild prefix CANNOT contain a space!").queue();
            }
            stmt.executeUpdate("UPDATE `Config` SET `Prefix` = '" + args[1] + "' WHERE `Config`.`guild_ID` = " + event.getGuild().getId() +";");

        } catch (SQLException ex) {
            // handle any errors
            MDC.put("SQLState", ex.getSQLState());
            MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
            logger.error(ex.getMessage());
            MDC.clear();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Bot Prefix Set");
        builder.setColor(Color.WHITE);
        builder.setDescription("Success");
        event.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String getUsage() {
        return "setPrefix (Prefix)";
    }

    @Override
    public String getDesc() {
        return "Changes the Bots Prefix";
    }

    @Override
    public String getAlias() {
        return "setprefix";
    }

    @Override
    public String getType() {
        return "admin";
    }
}
