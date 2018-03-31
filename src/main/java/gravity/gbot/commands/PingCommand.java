package gravity.gbot.commands;

import gravity.gbot.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class PingCommand implements Command {


    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {

        long time = System.currentTimeMillis();
        event.getChannel().sendMessage("PONG!").queue( (message) ->
                message.editMessageFormat("PONG!%nRoundtrip took: %dms%nWebsocket ping is: " + event.getJDA().getPing() + "ms", System.currentTimeMillis() - time).queue());
    }

    @Override
    public String cmdUsage() {
        return "Ping";
    }

    @Override
    public String cmdDesc() {
        return "Replies with Ping times this is used to check if the bot is alive.";
    }

    @Override
    public String getAlias() {
        return "ping";
    }

    @Override
    public String cmdType() {
        return "public";
    }
}

