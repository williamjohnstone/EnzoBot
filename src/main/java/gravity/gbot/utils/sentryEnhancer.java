package gravity.gbot.utils;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.event.EventBuilder;
import io.sentry.event.helper.EventBuilderHelper;

//This Class is currently unused
public class sentryEnhancer {
    public void StartEnhancer() {
        SentryClient client = Sentry.getStoredClient();

        EventBuilderHelper myEventBuilderHelper = new EventBuilderHelper() {
            @Override
            public void helpBuildingEvent(EventBuilder eventBuilder) {
                eventBuilder.withTag("Test Tag", "Test");
            }
        };
        client.addBuilderHelper(myEventBuilderHelper);


    }
}
