package gravity.gbot.utils;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

public class StatsUpdater {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public void StartupdateTimer(ReadyEvent event) {
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
                    MDC.put("SQLState", ex.getSQLState());
                    MDC.put("VendorError", String.valueOf(ex.getErrorCode()));
                    logger.error(ex.getMessage());
                    MDC.clear();
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
}
