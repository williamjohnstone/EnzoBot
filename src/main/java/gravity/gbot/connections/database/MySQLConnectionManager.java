package gravity.gbot.connections.database;

import gravity.gbot.utils.Config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class MySQLConnectionManager implements DBConnectionManager {

    private Connection connection;

    MySQLConnectionManager() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(Config.dbConnection);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (!isConnected()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection(Config.dbConnection);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getName() {
        try {
            return connection.createStatement().executeQuery("SELECT DATABASE()").getString("DATABASE()");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        try {
            if (isConnected())
                connection.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

}
