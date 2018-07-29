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

package ml.enzodevelopment.enzobot.objects.guild;


import ml.enzodevelopment.enzobot.config.Config;

public class GuildSettings {

    private final String guildId;
    private String customPrefix = Config.fallback_prefix;
    private String logChannel = null;
    private boolean useBotChannel = false;
    private String commandChannel = null;
    private String muteRoleId = null;
    private int warningThreshold = 3;
    public GuildSettings(String guildId) {
        this.guildId = guildId;
    }

    public GuildSettings setBotChannel(String commandChannel) {
        this.commandChannel = commandChannel;
        this.useBotChannel = true;
        return this;
    }

    public String getBotChannel() {
        return commandChannel;
    }

    public GuildSettings useBotChannel(boolean useBotChannel) {
        this.useBotChannel = useBotChannel;
        return this;
    }

    public boolean usingBotChannel() {
        return useBotChannel;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getCustomPrefix() {
        return customPrefix;
    }

    public GuildSettings setCustomPrefix(String customPrefix) {
        this.customPrefix = customPrefix;
        return this;
    }

    public String getLogChannel() {
        return logChannel;
    }

    public GuildSettings setLogChannel(String tc) {
        this.logChannel = tc;
        return this;
    }

    public String getMuteRoleId() {
        return muteRoleId;
    }

    public GuildSettings setMuteRoleId(String muteRoleId) {
        this.muteRoleId = muteRoleId;
        return this;
    }

    public int getWarningThreshold() {
        return warningThreshold;
    }

    public GuildSettings setWarningThreshold(int warningThreshold) {
        this.warningThreshold = warningThreshold;
        return this;
    }
}
