/*
 * Enzo Bot, a multipurpose discord bot
 *
 * Copyright (c) 2018 William "Enzo" Johnstone
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package ml.enzodevelopment.enzobot.utils;

import ml.enzodevelopment.enzobot.config.Config;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class StatsUpdater {

    public void StartupdateTimer(ReadyEvent event) {
        Connection conn = Config.DB.getConnManager().getConnection();
        Logger logger = LoggerFactory.getLogger(this.getClass().getName());

        int MINUTES = 5;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                event.getJDA().getPresence().setGame(Game.watching(event.getJDA().getGuildCache().size() + " servers! | !help"));
                String token = Config.API_Key;
                String botId = "391558265265192961";

                int serverCount = (int) event.getJDA().getGuildCache().size();

                Config.DB.run(() -> {
                    try (PreparedStatement stmt = conn.prepareStatement("UPDATE `API` SET `server_count` = ? WHERE `API`.`ID` = 1;")) {
                        stmt.setInt(1, serverCount);
                        stmt.executeUpdate();
                    } catch (SQLException ex) {
                        logger.error("Database Error", ex);
                    }
                });
                OkHttpClient client = new OkHttpClient();
                FormBody body = new FormBody.Builder().add("server_count", String.valueOf(serverCount)).build();
                Request request = new Request.Builder().url("https://discordbots.org/api/bots/" + botId + "/stats").post(body).addHeader("Authorization", token).addHeader("Content-Type", "application/json").build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.body() != null) {
                        response.body().close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000 * 60 * MINUTES);
    }
}
