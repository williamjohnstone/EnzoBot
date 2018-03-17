package gravity.gbot.utils.Logging;

import gravity.gbot.utils.BotListener;
import gravity.gbot.utils.Config;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class msgLogger {
    public void log(GuildMessageReceivedEvent event) {
        String args[] = event.getMessage().getContentRaw().split(" +");
        if (Config.loggingALL) {
            if (!event.getAuthor().isBot()) {
                if (event.getChannelType() == ChannelType.PRIVATE) {
                    System.out.println("[GravityBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannelType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw());
                } else if (event.getChannelType() == ChannelType.TEXT) {
                    System.out.println("[GravityBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannelType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw() + ", Guild (Server): " + event.getGuild().getName());
                }
            }
        }
        BotListener listner = new BotListener();
        if (listner.getCommand(args[0]) != null) {
            if (Config.loggingCMD && !Config.loggingALL) {
                if (!event.getAuthor().isBot()) {
                    if (event.getChannelType() == ChannelType.PRIVATE) {
                        System.out.println("[GravityBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannelType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw());
                    } else if (event.getChannelType() == ChannelType.TEXT) {
                        System.out.println("[GravityBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannelType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw() + ", Guild (Server): " + event.getGuild().getName());
                    }
                }
            }
        }
    }
}
