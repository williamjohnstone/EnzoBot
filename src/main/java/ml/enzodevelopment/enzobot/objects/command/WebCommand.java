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

package ml.enzodevelopment.enzobot.objects.command;

import java.util.List;

public class WebCommand {
    private final String name;
    private final String usage;
    private final String desc;
    private final List<String> aliases;

    public WebCommand(String name, String usage, String desc, List<String> aliases) {
        this.name = name;
        this.usage = usage;
        this.desc = desc;
        this.aliases = aliases;
    }

    String getName() {
        return name;
    }

    String getUsage() {
        if (getName() != usage) {
            return usage;
        }
        return "";
    }

    String getDesc() {
        return desc;
    }

    String getAliasString() {
        List<String> sublist = aliases.subList(1, aliases.size());
        if (sublist.size() != 0) {
            StringBuilder builder = new StringBuilder();
            for (String alias: sublist) {
                builder.append(alias).append(", ");
            }
            return builder.substring(0, builder.length() - 2);
        }
        return "";
    }
}
