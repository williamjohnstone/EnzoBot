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

package ml.enzodevelopment.enzobot.connections.database;

import java.sql.Connection;
import java.util.concurrent.*;

public class DBManager {

    public final DBConnectionManager connManager;
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(1,
            r -> new Thread(r, "SQL-thread"));

    private final String name;

    public DBManager() {
        this.connManager = createDBManager();
        this.name = connManager.getName();
    }

    private static DBConnectionManager createDBManager() {
        return new MySQLConnectionManager();
    }

    public boolean isConnected() {
        return connManager.isConnected();
    }

    public String getName() {
        return this.name;
    }

    @Deprecated
    public Connection getConnection() {
        return connManager.getConnection();
    }

    public DBConnectionManager getConnManager() {
        return connManager;
    }

    public <T> ScheduledFuture<T> run(Callable<T> c) {
        return service.schedule(c, 0L, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> run(Runnable r) {
        return service.schedule(r, 0L, TimeUnit.MILLISECONDS);
    }

    public ScheduledExecutorService getService() {
        return service;
    }

}
