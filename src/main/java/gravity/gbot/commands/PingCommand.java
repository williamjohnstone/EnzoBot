package gravity.gbot.commands;

import gravity.gbot.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class PingCommand implements Command {

        final String DESC = "Replies with Ping times this is used to check if the bot is alive.";
        final String USAGE = "Ping";
        final String ALIAS = "ping";
        private final String type = "public";

        @Override
        public String cmdUsage() {
        return USAGE;
        }

        @Override
        public String cmdDesc() {
        return DESC;
        }

        @Override
        public String getAlias() {
        return ALIAS;
        }

    @Override
    public String cmdType() {
        return type;
    }


    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {

        long time = System.currentTimeMillis();
        event.getChannel().sendMessage("PONG!").queue( (message) ->
                message.editMessageFormat("PONG!%nMessage ping is: %dms%nWebsocket ping is: " + event.getJDA().getPing() + "ms", System.currentTimeMillis() - time).queue());


    }




}

