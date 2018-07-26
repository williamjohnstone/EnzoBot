package ml.enzodevelopment.enzobot.commands.mod;

import ml.enzodevelopment.enzobot.Command;
import ml.enzodevelopment.enzobot.CommandCategory;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetRole implements Command {

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.getChannel().sendMessage("Sorry I cant let you do that, For security reasons of course...").queue();
            return;
        }
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.getChannel().sendMessage("Sorry I don't have the permission to do that.").queue();
            return;
        }
        if (!event.getMember().canInteract(event.getMessage().getMentionedMembers().get(0))) {
            event.getChannel().sendMessage("You cant manage roles for this member").queue();
            return;
        }
        if (!event.getMember().canInteract(event.getGuild().getRoleById(args[2].replace("<@&", "").replace(">", "")))) {
            event.getChannel().sendMessage("You cant manage roles higher or equal to yourself").queue();
            return;
        }
        if (args.length > 3 ) {
            event.getChannel().sendMessage("Command usage Incorrect! Correct usage: " + getUsage()).queue();
        } else {
            try {
                event.getGuild().getController().addRolesToMember(event.getMessage().getMentionedMembers().get(0), event.getGuild().getRoleById(args[2].replace("<@&", "").replace(">", ""))).queue();
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " :white_check_mark: Roles assigned successfully.").queue();
            } catch (HierarchyException e) {
                event.getChannel().sendMessage("Sorry i can't set roles higher than my own.").queue();
            }
        }


    }

    @Override
    public String getUsage() {
        return "setRole (@member) (@role)";
    }

    @Override
    public String getDesc() {
        return "Changes the specified member's role.";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("setrole"));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }
}
