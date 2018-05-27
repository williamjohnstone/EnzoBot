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

public class SetBotChannel implements Command {

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
            if (args[1].equals("off")) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Bot Channel Removed");
                builder.setColor(Color.WHITE);
                builder.setDescription("Success");
                event.getChannel().sendMessage(builder.build()).queue();
                channel = "0";
            } else {
                channel = event.getMessage().getMentionedChannels().get(0).getId();
            }
        } else if (args.length == 1) {
            channel = event.getMessage().getChannel().getId();
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
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (!channel.equals("0")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Bot Channel Set");
            builder.setColor(Color.WHITE);
            builder.setDescription("Success");
            event.getChannel().sendMessage(builder.build()).queue();
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Bot Channel Removed");
            builder.setColor(Color.WHITE);
            builder.setDescription("Success");
            event.getChannel().sendMessage(builder.build()).queue();
        }
    }

    @Override
    public String cmdUsage() {
        return "'setBotChat (#Channel)' or 'setBotChat off' to disable bot channel.";
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
