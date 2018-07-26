package ml.enzodevelopment.enzobot.commands.mod;

import ml.enzodevelopment.enzobot.objects.command.Command;
import ml.enzodevelopment.enzobot.objects.command.CommandCategory;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RevokeAccessCommand implements Command {
    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {

    }

    @Override
    public String getUsage() {
        return "remove (@User or @Role) (#Channel)";
    }

    @Override
    public String getDesc() {
        return "Removes a user's or role's permission to access the specified channel.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("removeaccess", "revokeaccess", "remove"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }
}
