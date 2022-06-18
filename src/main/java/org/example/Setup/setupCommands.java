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
        if(author.isBot()) return;
        if(!(e.getMember().hasPermission(Permission.ADMINISTRATOR))) return;
        String[] args = e.getMessage().getContentRaw().split(" ");

        switch (args[0]){
            case "=help":
                EmbedBuilder helpBuilder = new EmbedBuilder()
                        .setTitle("Help")
                        .setColor(Color.BLACK)
                        .addField("Commands: ",  "" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=countingChannel` **set counting channel** \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=hasRewards true/false` **if true, actions below this message will be executed** \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=countAmount amount` **amount to count before reward** \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=actionMessage message` **What message to send when members reaches count amount(for action, not to the user, below this line)**\n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=actionType (DM/channel)` **send message to a channel or to the admins** \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=actionChannel` **Channel to send message when count amount is crossed** \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=admins adminUserIds (multiple can be added)` **Admins to DM** \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=clear` **clear server settings** \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n", false)
                        .addField("Conclusion", "" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ \n" +
                                "`=countingChannel` the channel this command is executed will be set as counting channel \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=hasRewards` this will define if there is any actions needs to be taken when member passes certain amount(s) \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=countAmount` it will set how much a member have to count in order for the reward (aka how much a member have to count to in order to trigger " +
                                "an action) \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=actionMessage` when action is triggered, it will send a message to a channel or admins dm, this is what message to send. this message must have a parameter called" +
                                "`%s`, the place you put it will be used as the users mention, for example if you do `=actionMessage .give %s 100` and I counts to reward amount, this message" +
                                "will be sent to action channel: **.give <@671016674668838952> 100" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=actionType` it will define if the action message will be sent to dm or to admins \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=actionChannel` channel to send action message if action type is set to channel \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=admins` same as above, admins listed here will be messaged when action gets triggered if actionType is set to DM \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                "`=clear` will clear every config about the server from database \n" +
                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n", false)
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


            case"=countingChannel":
                e.getMessage().reply("`counting channel set!`").queue();
                Database.set(e.getGuild().getId(), "countingChannel", e.getChannel().getId(), false);
                Database.sync(e.getGuild().getId());
                break;

            case"=hasRewards":
                e.getMessage().reply("`has rewards set!`").queue();
                Database.set(e.getGuild().getId(), "hasRewards", Boolean.valueOf(args[1]), false);
                Database.sync(e.getGuild().getId());
                break;

            case"=countAmount":
                e.getMessage().reply("`amount to count for rewards set!`").queue();
                Database.set(e.getGuild().getId(), "beforeReward", Integer.parseInt(args[1]), false);
                Database.sync(e.getGuild().getId());
                break;

            case"=actionMessage":
                e.getMessage().reply("`action message set!`").queue();
                StringBuilder message = new StringBuilder();
                    for(int i  = 1; i < args.length; i++){
                        message.append(args[i] + " ");
                    } //implement the member id feature
                Database.set(e.getGuild().getId(), "sendMessage", message.toString(), false);
                Database.sync(e.getGuild().getId());
                break;

            case"=actionType":
                e.getMessage().reply("`action type set!`").queue();
                boolean actionType = Objects.equals(args[1], "DM");
                Database.set(e.getGuild().getId(), "actionType", actionType, false);
                Database.sync(e.getGuild().getId());
                break;

            case"=actionChannel":
                e.getMessage().reply("`action channel to send action message set!` \n" +
                        "**Note: Don't set Admin if you are willing to use this instead of DMs, it will corrupt the database**").queue();
                Database.set(e.getGuild().getId(), "admins", e.getChannel().getId(), false);
                Database.sync(e.getGuild().getId());
                break;

            case"=admins":
                e.getMessage().reply("`Admins set! remember this has over written action channel if you have set one` \n").queue();
                StringBuilder adminIds = new StringBuilder();
                for(int i  = 1; i < args.length; i++){
                    adminIds.append(args[i] + " ");
                }
                Database.set(e.getGuild().getId(), "admins", adminIds.toString(), false);
                Database.sync(e.getGuild().getId());
                break;

            case"=clear":
                e.getMessage().reply("`settings for this server cleared!, =help for more info`").queue();
                Database.collection.deleteOne(new Document("serverId", e.getGuild().getId()));
                Database.sync(e.getGuild().getId());
                break;
        }

    }
}
