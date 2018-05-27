package gravity.gbot.commands.mod;

import gravity.gbot.Command;
import gravity.gbot.utils.GuildConfig;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;

public class SetRole implements Command {

    private final String USAGE = "setRole (@member) (@role)";

    private GuildConfig config = new GuildConfig();

    @Override
    public void execute(String[] args, GuildMessageReceivedEvent event) {
        String admincheck = config.isAdmin(event.getAuthor().getId(), event.getGuild().getId(), event.getJDA());
        if (admincheck == null) {
            event.getMessage().getChannel().sendMessage("You are not currently in the admin list").queue();
            return;
        }
        if (event.getChannel().getType() == ChannelType.PRIVATE | event.getChannel().getType() == ChannelType.GROUP) {
            event.getChannel().sendMessage("Sorry this is *Guild Only* Command").queue();
            return;
        }
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
        if (args.length > 3) {
            event.getChannel().sendMessage("Command usage Incorrect! Correct usage: " + USAGE).queue();
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
    public String cmdUsage() {
        return USAGE;
    }

    @Override
    public String cmdDesc() {
        return "Changes the specified member's role.";
    }

    @Override
    public String getAlias() {
        return "setrole";
    }

    @Override
    public String cmdType() {
        return "admin";
    }
}
