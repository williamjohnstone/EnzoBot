package ml.enzodevelopment.enzobot.connections.database;

import ml.enzodevelopment.enzobot.utils.Config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

class MySQLConnectionManager implements DBConnectionManager {

    private Connection connection;

    MySQLConnectionManager() {
        try {
            this.connection = DriverManager.getConnection(Config.dbConnection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (!isConnected()) {
            try {
                this.connection = DriverManager.getConnection(Config.dbConnection);
            } catch (SQLException e) {
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
            ResultSet rs = connection.createStatement().executeQuery("SELECT DATABASE()");
            if (rs.next()) {
                return rs.getString("DATABASE()");
            }
            return null;
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
