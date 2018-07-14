package ml.enzodevelopment.enzobot.utils.logging;

import ml.enzodevelopment.enzobot.utils.BotListener;
import ml.enzodevelopment.enzobot.utils.Config;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class MessageLogger {

    public void logMessage(GuildMessageReceivedEvent event, String BotPrefix) {

        String args[] = event.getMessage().getContentRaw().split(" +");
        if (Config.loggingALL && !event.getAuthor().isBot()) {
            sendLog(event);
        }

        String msg = event.getMessage().getContentRaw().toLowerCase();
        if (msg.startsWith(BotPrefix)) {
            msg = msg.substring(BotPrefix.length());
        }

        String[] parts = msg.split(" +");

        String commandName = parts[0];
        if (BotListener.getCommand(commandName) != null && Config.loggingCMD && !Config.loggingALL && !event.getAuthor().isBot()) {
            sendLog(event);
        }

    }
    private void sendLog(GuildMessageReceivedEvent event) {
        if (event.getChannel().getType() == ChannelType.PRIVATE) {
            System.out.println("[EnzoBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannel().getType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw());
        } else if (event.getChannel().getType() == ChannelType.TEXT) {
            System.out.println("[EnzoBot] Message Received, Channel: " + event.getMessage().getChannel().getName() + ", Channel Type: " + event.getChannel().getType() + ", Author: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ", Message: " + event.getMessage().getContentRaw() + ", Guild (Server): " + event.getGuild().getName());
        }
    }
}
