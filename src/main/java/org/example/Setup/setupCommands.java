package org.example.Setup;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;

import java.awt.*;
import java.util.Objects;

public class setupCommands extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        User author = e.getAuthor();
        //checking all perms
        if(author.isBot()) return;
        if(!(e.getMember().hasPermission(Permission.ADMINISTRATOR))) return;
        String[] args = e.getMessage().getContentRaw().split(" ");

        switch (args[0]){
            //help commands
            case "=advanceHelp":
                EmbedBuilder helpBuilder = new EmbedBuilder()
                        .setTitle("Help")
                        .setColor(Color.BLACK)
                        .addField("Commands: ",  "" +
                                "ㅤ\n" +
                                "`=countingChannel` **set counting channel** \n" +
                                "ㅤ\n" +
                                "`=hasRewards true/false` **if true, actions below this message will be executed** \n" +
                                "ㅤ\n" +
                                "`=countAmount amount` **amount members have to count before reward** \n" +
                                "ㅤ\n" +
                                "`=actionMessage message` **What message to send when members reaches count amount(for action, not to send message to the user, below this line)**\n" +
                                "ㅤ\n" +
                                "`=actionType (DM/channel)` **send message to a channel or to the admins** \n" +
                                "ㅤ\n" +
                                "`=actionChannel` **Channel to send message when count amount is crossed** \n" +
                                "ㅤ\n" +
                                "`=admin adminUserId` **Admin to DM** \n" +
                                "ㅤ\n" +
                                "`=clear` **clear server settings** \n" +
                                "ㅤ\n", false)
                        .addField("Conclusion", "ㅤ\n" +
                                "`=countingChannel` the channel this command is executed will be set as counting channel \n" +
                                "ㅤ\n" +
                                "`=hasRewards` this will define if there is any actions needs to be taken when member passes certain amount(s) \n" +
                                "ㅤ\n" +
                                "`=countAmount` it will set how much a member have to count in order for the reward (aka how much a member have to count to in order to trigger " +
                                "an action) \n" +
                                "ㅤ\n" +
                                "`=actionMessage` when action is triggered, it will send a message to a channel or admins dm, this is what message to send. this message must have a parameter name: " +
                                "`%s`, the place you put it will be used as the users mention, for example if you do `=actionMessage .give %s 100` and I counts to reward amount, this message " +
                                "will be sent to action channel / admin: **.give <@671016674668838952> 100** \n" +
                                "ㅤㅤ\n" +
                                "`=actionType` it will define if the action message will be sent to dm or to channel \n" +
                                "ㅤ\n" +
                                "`=actionChannel` channel to send action message\n" +
                                "ㅤ\n" +
                                "`=admin` same as above, admins listed here will be messaged when actionType is set to DM \n" +
                                "ㅤ\n", false)

                        .addField("Modes:" , "\n" +
                                        "1.`DM mode` **it will dm an admin when someone counts to reward amount** \n" +
                                        "to enable this, you must set `=admins admin to DM` and `actionType DM` \n" +
                                        "ㅤㅤㅤㅤ\n" +
                                        "2. `Channel mode` **it will send action message to a specified channel of the server** \n" +
                                        "to enable this, you must set `=actionChannel` <--(use this command on the channel you want the message to be sent), and set `actionType channel`", false);

                                e.getMessage().replyEmbeds(helpBuilder.build())
                                        .mentionRepliedUser(false)
                                        .queue();
                                break;

            case "=help":
                EmbedBuilder simpleHelp = new EmbedBuilder()
                        .addField("Commands", "`=countingChannel` **it will set the channel this command is used in as counting channel** \n" +
                                "**do** `=advanceHelp` **to see all the features of this bot**", false)
                        .setColor(Color.WHITE);
                e.getMessage().replyEmbeds(simpleHelp.build()).queue();
                break;

            //setup commands
            case"=countingChannel":
                e.getMessage().reply("`counting channel set!`").queue();
                Database.set(e.getGuild().getId(), "countingChannel", e.getChannel().getId(), false);
                break;

            case"=hasRewards":
                e.getMessage().reply("`has rewards set!`").queue();
                Database.set(e.getGuild().getId(), "hasRewards", Boolean.valueOf(args[1]), false);
                break;

            case"=countAmount":
                e.getMessage().reply("`amount to count for rewards set!`").queue();
                Database.set(e.getGuild().getId(), "beforeReward", Integer.parseInt(args[1]), false);
                break;

            case"=actionMessage":
                e.getMessage().reply("`action message set!`").queue();
                StringBuilder message = new StringBuilder();
                    for(int i  = 1; i < args.length; i++){
                        message.append(args[i] + " ");
                    } //implement the member id feature
                Database.set(e.getGuild().getId(), "sendMessage", message.toString(), false);
                break;

            case"=actionType":
                e.getMessage().reply("`action type set!`").queue();
                boolean actionType = Objects.equals(args[1], "DM");
                Database.set(e.getGuild().getId(), "actionType", actionType, false);
                break;

            case"=actionChannel":
                e.getMessage().reply("`action channel to send action message set!` \n" +
                        "**Note: do** `=advanceHelp` **and take a look at Modes for more info**").queue();
                Database.set(e.getGuild().getId(), "admins", e.getChannel().getId(), false);
                break;

            case"=admin":
                e.getMessage().reply("`Admin set!`\n" +
                        "**Note: do** `=advanceHelp` **and take a look at Modes for more info**").queue();
                StringBuilder adminIds = new StringBuilder();
                for(int i  = 1; i < args.length; i++){
                    adminIds.append(args[i] + " ");
                }
                Database.set(e.getGuild().getId(), "admins", adminIds.toString(), false);
                break;

            case"=clear":
                e.getMessage().reply("`settings for this server cleared!, =help for more info`").queue();
                Database.collection.deleteOne(new Document("serverId", e.getGuild().getId()));
                break;
        }

    }
}
