package gravity.gbot.utils;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class StatsUpdater {

    public void StartupdateTimer(ReadyEvent event) {
        int MINUTES = 5;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                event.getJDA().getPresence().setGame(Game.watching(event.getJDA().getGuildCache().size() + " servers! | g-bot.tk"));
                String token = Config.API_Key;
                String botId = "391558265265192961";

                int serverCount = (int) event.getJDA().getGuildCache().size();

                Database db = new Database(Config.dbConnection);
                db.init();
                db.executeUpdate("UPDATE `API` SET `server_count` = '" + serverCount + "' WHERE `API`.`ID` = 1;");
                db.close();

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
