package gravity.gbot.commands.mod;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class TestKick implements Command {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        String u = event.getMessage().getMentionedMembers().get(0).getUser().getId();
        String guild = event.getGuild().getId();
        String type = "kick";
        String reason = "Being an idiot";
        String length = "5d";
        java.util.Date dt = new java.util.Date();

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String[] timeParts = length.split("(?<=\\D)+(?=\\d)+|(?<=\\d)+(?=\\D)+");
        System.out.println(timeParts[0] + " Next " + timeParts[1]);
        String currentTime = sdf.format(dt);
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(dt);
        String pun = event.getAuthor().getId();
        Connection conn;
        try {
            conn =
                    DriverManager.getConnection(Config.dbConnection);
            Statement stmt;
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO `Punishments` (`ID`, `user_id`, `guild_id`, `type`, `reason`, `length`, `ends`, `dateTime`, `punisherId`) VALUES (NULL, '"+ u +"', '" + guild + "', '" + type + "', '" + reason + "', '" + length + "', '" + "', '" + currentTime + "', '" + pun +"')");
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
        event.getChannel().sendMessage("Added!").queue();
    }

    @Override
    public String getUsage() {
        return "no usage";
    }

    @Override
    public String getDesc() {
        return "test kick";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("fakekick"));
    }

    @Override
    public String getType() {
        return "admin";
    }
}
