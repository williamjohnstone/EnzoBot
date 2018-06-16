package gravity.gbot.commands.basic;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class CoinFlipCommand implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        int outcome = (int) (Math.random() * 2 + 1);
        EmbedBuilder flipBuilder = new EmbedBuilder();
        flipBuilder.setTitle("Coin Flip");
        flipBuilder.setColor(Config.GBOT_BLUE);
        if  (outcome == 1) {
            flipBuilder.setDescription("You got Heads");
            flipBuilder.setImage("https://g-bot.tk/img/heads.png");
        } else if (outcome == 2) {
            flipBuilder.setDescription("You got Tails");
            flipBuilder.setImage("https://g-bot.tk/img/tails.png");
        } else {
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle("Error");
            error.setDescription("Invalid command usage!");
            error.setColor(Config.GBOT_BLUE);
            event.getChannel().sendMessage(error.build()).queue();
            return;
        }
        event.getChannel().sendMessage(flipBuilder.build()).queue();
    }

    @Override
    public String getUsage() {
        return "coinFlip";
    }

    @Override
    public String getDesc() {
        return "Flips a coin.";
    }

    @Override
    public String getAlias() {
        return "coinflip";
    }

    @Override
    public String getType() {
        return "public";
    }
}
