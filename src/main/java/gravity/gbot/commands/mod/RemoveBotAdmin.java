package gravity.gbot.commands.mod;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import gravity.gbot.utils.Database;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.awt.*;
import java.sql.*;

public class RemoveBotAdmin implements Command {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());


    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        boolean adminCheck = GuildConfig.isAdmin(event.getAuthor().getId(), event.getGuild().getId(), event.getJDA());
        if (!adminCheck) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }
        try {
            Database db = new Database(Config.dbConnection);
            db.init();
            ResultSet rs = db.executeQuery("SELECT * FROM `Config` where guild_ID = " + event.getGuild().getId() + ";");
            String currentAdmins = null;
            String mentionToID = args[1].replace("<", "").replace("@", "").replace("!", "").replace(">", "");
            if (rs.next()) {
                currentAdmins = rs.getString("bot_Admins");
            }
            if (currentAdmins == null) {
                return;
            }
            db.executeUpdate("UPDATE `Config` SET `bot_Admins` = '" + currentAdmins.replace("," + mentionToID, "") + "' WHERE `Config`.`guild_ID` = " + event.getGuild().getId() + ";");

        } catch (SQLException ex) {
            MDC.put("SQLState", ex.getSQLState());
            MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
            logger.error(ex.getMessage());
            MDC.clear();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Bot Admin Removed");
        builder.setColor(Color.WHITE);
        builder.setDescription("Success");
        event.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String getUsage() {
        return "rmAdmin @member";
    }

    @Override
    public String getDesc() {
        return "Removes a member from the list of bot admins for the guild.";
    }

    @Override
    public String getAlias() {
        return "rmadmin";
    }

    @Override
    public String getType() {
        return "admin";
    }
}
