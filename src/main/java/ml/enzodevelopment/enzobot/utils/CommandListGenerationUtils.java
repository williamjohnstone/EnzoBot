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

import ml.enzodevelopment.enzobot.EnzoBot;
import ml.enzodevelopment.enzobot.config.Config;
import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ml.enzodevelopment.enzobot.BotListener.getCommand;

public class CommandListGenerationUtils {

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    public String postAndGenerate() {
        JSONObject jsonObject = new JSONObject().put("token", Config.command_api_token);
        JSONArray commands = new JSONArray();
        for (Command cmd : getSortedCommands()) {
            if (cmd.getCategory() != CommandCategory.OWNER) {
                JSONObject commandEntry = new JSONObject();
                commandEntry.put("name", cmd.getAliases().get(0));
                commandEntry.put("desc", cmd.getDesc());
                if (!cmd.getUsage().equals(cmd.getAliases().get(0))) {
                    commandEntry.put("usage", cmd.getUsage());
                } else {
                    commandEntry.put("usage", "");
                }
                StringBuilder aliases = new StringBuilder();
                if (cmd.getAliases().size() != 1) {
                    for (String alias : cmd.getAliases().subList(1, cmd.getAliases().size())) {
                        if (aliases.toString().equals("")) {
                            aliases.append(alias);
                        } else {
                            aliases.append(",").append(alias);
                        }
                    }
                }
                commandEntry.put("aliases", aliases.toString());
                commands.put(commandEntry);
            }
        }
        jsonObject.put("commands", commands);
        try {
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url("https://bot.enzodevelopment.ml/api")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.body().string().equals("Accepted")) {
                return "Commands added to json api";
            }
            return "Adding commands failed";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private List<Command> getSortedCommands() {
        List<Command> commandSet = new ArrayList<>();
        List<String> names = new ArrayList<>();
        EnzoBot.cmdList.stream().filter(cmd -> cmd.getCategory() != CommandCategory.UNLISTED)
                .collect(Collectors.toSet()).forEach(c -> names.add(c.getAliases().get(0)));
        Collections.sort(names);
        names.forEach(n -> commandSet.add(getCommand(n)));
        return new ArrayList<>(commandSet);
    }


}
