package gravity.gbot.commands.AdminCommands;

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

public class setBotChannel implements Command {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private GuildConfig config = new GuildConfig();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        String admincheck = config.isAdmin(event.getAuthor().getId(), event.getGuild().getId(), event.getJDA());
        if (admincheck == null) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }
        String channel = "0";
        if (args.length == 2) {
            channel = event.getMessage().getMentionedChannels().get(0).getId();
        }
        Connection conn;

        try {

            conn =
                    DriverManager.getConnection(Config.dbConnection);

            Statement stmt;

            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE `Config` SET `bot_Channel_ID` = '" + channel + "' WHERE `Config`.`guild_ID` = " + event.getGuild().getId() +";");



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
        builder.setTitle("Bot Channel Set");
        builder.setColor(Color.WHITE);
        builder.setDescription("Success");
        event.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String cmdUsage() {
        return "setBotChat (#Channel) or setBotChat without any arguments to disable bot channel.";
    }

    @Override
    public String cmdDesc() {
        return "Changes the channel the bot uses this channel is used for all commands. (Note: Admins bypass this)";
    }

    @Override
    public String getAlias() {
        return "setbotchat";
    }

    @Override
    public String cmdType() {
        return "admin";
    }
}
