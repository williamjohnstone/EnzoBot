package gravity.gbot;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public interface Command {
    void execute(String[] args, GuildMessageReceivedEvent event);

    String getUsage();

    String getDesc();

    String getAlias();

    String getType();
}


