package org.example;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.Setup.Database;
import java.awt.*;


public class Counter extends ListenerAdapter {
    Object result;
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getChannel().getType().equals(ChannelType.PRIVATE)) return;
        Database.sync(e.getGuild().getId());
        User author = e.getAuthor();
        //help commands
        if(e.getMessage().getContentRaw().equalsIgnoreCase(".help")){
            EmbedBuilder helpBuilder = new EmbedBuilder()
                    .setTitle("Help")
                    .addField("Commands", "" +
                            "`.counted` **see where you left off** \n" +
                            "`.counted userId` **see where a specific member left off**", false)
                    .setColor(Color.WHITE);
            e.getMessage().replyEmbeds(helpBuilder.build()).queue();
        }
        String args[] = e.getMessage().getContentRaw().split(" ");
        switch (args[0]){
            case ".counted":
                if(args.length == 1){
                    try {
                        EmbedBuilder countedBuilder = new EmbedBuilder()
                                .setColor(Color.BLACK)
                                .setDescription(String.format("**you have counted to %s**",Database.getUser(author.getId(), e.getGuild().getId(), e.getGuild().getId())));
                        e.getMessage().replyEmbeds(countedBuilder.build()).queue();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }else if(args.length == 2){
                    try {
                        EmbedBuilder countedBuilder = new EmbedBuilder()
                                .setColor(Color.BLACK)
                                .setDescription(String.format("**%s has count to %s**", e.getGuild().retrieveMemberById(args[1]).complete().getAsMention(),Database.getUser(args[1], e.getGuild().getId(), e.getGuild().getId())));
                        e.getMessage().replyEmbeds(countedBuilder.build()).queue();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
        }
        //checking if that's counting channel
        if((!e.getChannel().getId().equalsIgnoreCase(Database.countingChannelId))) return;
        if(author.isBot()) return;

        //expression evaluator and calculating the result
        try {
            String expression = e.getMessage().getContentRaw().replace(" ", "");
            result = (Math.floor(new DoubleEvaluator().evaluate(expression)));
        }catch (Exception exception){
            return;
        }

        try {
            int counted; //which int the member counted last time
            counted = Database.getUser(author.getId(), e.getGuild().getId(), e.getGuild().getId());
            if(counted == ((double)result - 1)){
                //checking if the message is correct
                e.getMessage().addReaction("???").queue();
                Database.setUser(author.getId(), e.getGuild().getId(), String.valueOf(1), true);

                //action will go here
                if(Database.hasRewards && (Database.getUser(author.getId(), e.getGuild().getId(), e.getGuild().getId()) % Database.amountCount) == 0){
                    //reward trigger
                    if(Database.actionType){
                        EmbedBuilder actionEmbed = new EmbedBuilder()
                                .setTitle(String.format("%s passed counter reward amount!", author.getName()))
                                .setDescription(String.format("**%s**", String.format(Database.sendMessage, author.getAsMention()) + "\n" +
                                        String.format("**user:** %s", author.getAsMention())));

                        Database.adminId.forEach(id -> Main.jda.openPrivateChannelById(id.split(" ")[0]).flatMap(privateChannel -> privateChannel.sendMessageEmbeds(actionEmbed.build())).queue());
                    }else{
                        //sending message to channel
                        e.getGuild().getTextChannelById(Database.adminId.get(0).split(" ")[0]).sendMessage(String.format(Database.sendMessage, author.getAsMention())).queue();
                    }
                    e.getMessage().addReaction("\uD83C\uDF89").queue();
                }


            }else{
                //actions when there is a wrong answer
                e.getMessage().addReaction("???").queue();
                EmbedBuilder failureBuilder = new EmbedBuilder()
                        .setDescription(String.format("**Oops! you broke the counting chain at** `%s`, **next number was:** `%s`", counted, counted + 1))
                        .setColor(Color.BLACK);
                e.getMessage().replyEmbeds(failureBuilder.build()).queue();
                Database.setUser(author.getId(), e.getGuild().getId(), 0, false);
            }

        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }




    }

}
