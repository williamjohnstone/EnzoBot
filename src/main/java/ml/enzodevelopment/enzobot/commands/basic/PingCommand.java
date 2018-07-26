package ml.enzodevelopment.enzobot.commands.basic;

import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PingCommand implements Command {


    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {

        long time = System.currentTimeMillis();
        event.getChannel().sendMessage("PONG!").queue((message) -> message.editMessageFormat("PONG!%nRoundtrip took: %dms%nWebsocket ping is: " + event.getJDA().getPing() + "ms", System.currentTimeMillis() - time).queue());
    }

    @Override
    public String getUsage() {
        return "ping";
    }

    @Override
    public String getDesc() {
        return "Replies with Ping times this is used to check if the bot is alive.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("ping", "pong"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MAIN;
    }
}

