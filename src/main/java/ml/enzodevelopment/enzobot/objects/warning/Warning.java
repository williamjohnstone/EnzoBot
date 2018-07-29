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

package ml.enzodevelopment.enzobot.objects.warning;

import java.util.Date;

public class Warning {

    private final int id;
    private final Date date;
    private final Date expiryDate;
    private final String modId;
    private final String reason;
    private final String guildId;

    public Warning (int id, Date date, Date expiryDate, String modId, String reason, String guildId) {
        this.id = id;
        this.date = date;
        this.expiryDate = expiryDate;
        this.modId = modId;
        this.reason = reason;
        this.guildId = guildId;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public String getModId() {
        return modId;
    }

    public String getReason() {
        return reason;
    }

    public String getGuildId() {
        return guildId;
    }

}
