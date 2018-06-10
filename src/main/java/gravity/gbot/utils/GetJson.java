package gravity.gbot.utils;

import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

public class GetJson {
    public static String getLink(String getUrl) {
        try {
            HttpsURLConnection conn = (HttpsURLConnection) new URL(getUrl).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
            String body = IOUtils.toString(conn.getInputStream(), "UTF-8");
            conn.disconnect();
            return body;
        } catch (Exception e) {
            return null;
        }
    }
}
