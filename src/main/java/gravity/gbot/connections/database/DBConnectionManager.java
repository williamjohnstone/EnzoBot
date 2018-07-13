package gravity.gbot.connections.database;

import java.io.Closeable;
import java.sql.Connection;

public interface DBConnectionManager extends Closeable {

    Connection getConnection();

    boolean isConnected();

    String getName();

}
