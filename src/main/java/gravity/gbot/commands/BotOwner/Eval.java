package gravity.gbot.commands.BotOwner;

import gravity.gbot.Command;
import gravity.gbot.utils.Config;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;

public class Eval implements Command {

    GuildConfig config = new GuildConfig();

    final String Usage = "Eval (Java/Groovy Code)";
    final String Desc = "Executes Java/Groovy Code and sends the output to a text channel.";
    final String Alias = "eval";
    private final String type = "owner";

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals("205056315351891969")) {
            event.getChannel().sendMessage("This Command is reserved for the bot owner.").queue();
        } else {

            ScriptEngine engine;
            ScriptEngineManager factory = new ScriptEngineManager();

            engine = factory.getEngineByName("groovy");


            try {
                engine.put("Config", Config.class);
                engine.put("event", event);
                Object out = engine.eval(event.getMessage().getContentRaw().replace(config.getPrefix(event.getGuild().getId()) + "eval ", ""));
                if (out != null) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Successfully Evaluated");
                    builder.setColor(Color.WHITE);
                    builder.setDescription(out.toString());
                    event.getChannel().sendMessage(builder.build()).queue();
                }
            } catch (ScriptException e) {
                event.getChannel().sendMessage(e.getMessage()).queue();
            }

        }
    }







    @Override
    public String cmdUsage() {
        return Usage;
    }

    @Override
    public String cmdDesc() {
        return Desc;
    }

    @Override
    public String getAlias() {
        return Alias;
    }

    @Override
    public String cmdType() {
        return type;
    }
}
