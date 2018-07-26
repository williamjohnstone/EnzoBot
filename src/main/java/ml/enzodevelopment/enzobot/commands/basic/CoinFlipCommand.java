package ml.enzodevelopment.enzobot.commands.basic;

import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.CommandCategory;
import ml.enzodevelopment.enzobot.utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoinFlipCommand implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        int outcome = (int) (Math.random() * 2 + 1);
        EmbedBuilder flipBuilder = new EmbedBuilder();
        flipBuilder.setTitle("Coin Flip");
        flipBuilder.setColor(Config.ENZO_BLUE);
        if  (outcome == 1) {
            flipBuilder.setDescription("You got Heads");
            flipBuilder.setImage("https://bot.enzodevelopment.ml/img/heads.png");
        } else if (outcome == 2) {
            flipBuilder.setDescription("You got Tails");
            flipBuilder.setImage("https://bot.enzodevelopment.ml/img/tails.png");
        } else {
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle("Error");
            error.setDescription("Invalid command usage!");
            error.setColor(Config.ENZO_BLUE);
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
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("coinflip", "coin"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MAIN;
    }
}
