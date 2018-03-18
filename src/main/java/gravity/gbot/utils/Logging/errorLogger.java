package gravity.gbot.utils.Logging;

import org.slf4j.Logger;
import org.slf4j.MDC;

// Unused Class
public class errorLogger {
    public void logError(Logger log, String msg) {
        MDC.put("Test Tag", "Test");
        log.error(msg);
    }
}
