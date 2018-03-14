package gravity.gbot.utils;

import java.sql.*;

public class GuildConfig {
    public String getPrefix(String guild) {
        Connection conn;

        String fallback_bot_Prefix = Config.fallback_prefix;
        try {


            conn =
                    DriverManager.getConnection(Config.dbConnection);

            Statement stmt;
            ResultSet rs;

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM `Config` where guild_ID = " + guild + ";");

            String result = null;
            if (rs.next()) {
                result = rs.getString("Prefix");
            }
            conn.close();
            //checks if the prefix is null and if not proceeds to make sure the prefix is not empty nor contain any spaces
            if (result != null) {
                if (result.contains(" ")) {
                    return fallback_bot_Prefix;
                }
                if (result.equals("")) {
                    return fallback_bot_Prefix;
                }
            }
            //return the non null prefix
            return result;


        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            // return fallback prefix and output error
            System.out.println("[Warning] Critical Database Error! Guild not in Database" + " GUILD ID: " + guild);
            return fallback_bot_Prefix;
        } catch (Exception e) {
            e.printStackTrace();
            // return fallback prefix and output error
            System.out.println("[Warning] Critical Database Error! Guild not in Database" + " GUILD ID: " + guild);
            return fallback_bot_Prefix;
        }
    }

    public String isAdmin(String ID, String guild) {
        Connection conn;

        try {


            conn =
                    DriverManager.getConnection(Config.dbConnection);

            Statement stmt;
            ResultSet rs;

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM `Config` where guild_ID = " + guild + ";");

            String result = null;
            if (rs.next()) {
                result = rs.getString("bot_Admins");
            }
            conn.close();
            String[] dbData = result.split(",");

            for (String admin : dbData) {
                if (ID.equals(admin)) {
                    return admin;
                }
            }
            System.out.println("[Warning] Critical Database Error! Guild not in Database" + " GUILD ID: " + guild);
            return null;

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
