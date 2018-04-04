package gravity.gbot.commands;

import gravity.gbot.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class GuildInfoCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage("This Command is a WIP").queue();
    }

    @Override
    public String cmdUsage() {
        return "guildInfo";
    }

    @Override
    public String cmdDesc() {
        return "Displays info about the current guild.";
    }

    @Override
    public String getAlias() {
        return "guildinfo";
    }

    @Override
    public String cmdType() {
        return "public";
    }
}
