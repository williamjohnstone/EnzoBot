package gravity.gbot.utils.Logging;

import gravity.gbot.utils.BotListener;
import gravity.gbot.utils.Config;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class msgLogger {
    public void log(GuildMessageReceivedEvent event, String BotPrefix) {
        String args[] = event.getMessage().getContentRaw().split(" +");
        if (Config.loggingALL) {
            if (!event.getAuthor().isBot()) {
                if (event.getChannel().getType() == ChannelType.PRIVATE) {
                    System.out.println("[GravityBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannel().getType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw());
                } else if (event.getChannel().getType() == ChannelType.TEXT) {
                    System.out.println("[GravityBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannel().getType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw() + ", Guild (Server): " + event.getGuild().getName());
                }
            }
        }
        String msg = event.getMessage().getContentRaw().toLowerCase();
        if(msg.startsWith(BotPrefix)) {
            msg = msg.substring(BotPrefix.length());
        }
        String[] parts = msg.split(" +");
        String commandName = parts[0];
        BotListener listner = new BotListener();
        if (listner.getCommand(commandName) != null) {
            if (Config.loggingCMD && !Config.loggingALL) {
                if (!event.getAuthor().isBot()) {
                    if (event.getChannel().getType() == ChannelType.PRIVATE) {
                        System.out.println("[GravityBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannel().getType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw());
                    } else if (event.getChannel().getType() == ChannelType.TEXT) {
                        System.out.println("[GravityBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannel().getType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw() + ", Guild (Server): " + event.getGuild().getName());
                    }
                }
            }
        }
    }
}
