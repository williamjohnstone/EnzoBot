package gravity.gbot.connections.database;

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
