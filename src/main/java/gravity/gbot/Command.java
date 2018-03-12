package gravity.gbot;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public interface Command {
    void execute(String[] args, GuildMessageReceivedEvent event);

    String cmdUsage();

    String cmdDesc();

    String getAlias();

    String cmdType();
}


