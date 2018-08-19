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

package ml.enzodevelopment.enzobot.web;

import static spark.Spark.*;

import ml.enzodevelopment.enzobot.utils.CommandUtils;
import spark.ModelAndView;
import spark.template.jtwig.JtwigTemplateEngine;

import java.util.HashMap;

public class Website {

    private HashMap<String, Object> settings = new HashMap<>();

    public void init(int port) {
        final JtwigTemplateEngine engine = new JtwigTemplateEngine("views");
        staticFileLocation("public");

        port(port);

        get("/", (req, res) ->  {
            settings.put("title", "EnzoBot");
            return engine.render(new ModelAndView(settings, "home.twig"));
        });

        get("/commands", (req, res) ->  {
            settings.put("title", "List of Commands");
            settings.put("commands", CommandUtils.getWebCommands());
            return engine.render(new ModelAndView(settings, "commands.twig"));
        });

        get("/dashboard", (req,res) -> {
            settings.put("title", "EnzoBot Dashboard");
            return engine.render(new ModelAndView(settings, "dashboard.twig"));
        });

    }

}
