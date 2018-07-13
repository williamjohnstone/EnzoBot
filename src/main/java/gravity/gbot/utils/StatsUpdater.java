package gravity.gbot.utils;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class StatsUpdater {

    public void StartupdateTimer(ReadyEvent event) {
        Logger logger = LoggerFactory.getLogger(this.getClass().getName());

        int MINUTES = 5;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                event.getJDA().getPresence().setGame(Game.watching(event.getJDA().getGuildCache().size() + " servers! | g-bot.tk"));
                String token = Config.API_Key;
                String botId = "391558265265192961";

                int serverCount = (int) event.getJDA().getGuildCache().size();

                Config.DB.run(() -> {
                   try {
                       Connection conn = Config.DB.getConnManager().getConnection();
                       PreparedStatement stmt = conn.prepareStatement("UPDATE `API` SET `server_count` = '?' WHERE `API`.`ID` = 1;");
                       stmt.setInt(1, serverCount);
                       stmt.executeUpdate();
                       conn.close();
                   } catch (SQLException ex) {
                       logger.error("Database Error", ex);
                   }
                });
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
    }
}
