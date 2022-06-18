package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.Setup.Database;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;


public class Counter extends ListenerAdapter {
    Object result;
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if((!e.getChannel().getId().equalsIgnoreCase(Database.countingChannelId))) return;
        User author = e.getAuthor();
        if(author.isBot()) return;

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
        try {
            result = scriptEngine.eval(String.format("%s", e.getMessage().getContentRaw()));

        } catch (ScriptException ex) {
            System.out.println("not an math expression");
            return;
        }

        try {
          int counted = (Integer) Database.getUser(author.getId()).get("counted");

            if(counted == ((Integer)result - 1)){
                e.getMessage().addReaction("✅").queue();
                Database.setUser(author.getId(), "counted", String.valueOf(1), true);

                //action will go here
                if(Database.hasRewards && ((Integer) Database.getUser(author.getId()).get("counted") % Database.amountCount) == 0){
                    if(Database.actionType){
                        EmbedBuilder actionEmbed = new EmbedBuilder()
                                .setTitle(String.format("%s passed counter reward amount!", author.getName()))
                                .setDescription(String.format("**%s**", Database.sendMessage) + "\n" +
                                        String.format("**user:** %s", author.getAsMention()));

                        Database.adminId.forEach(id -> Main.jda.openPrivateChannelById(id.split(" ")[0]).flatMap(privateChannel -> privateChannel.sendMessageEmbeds(actionEmbed.build())).queue());
                    }else{
                        //sending message to channel
                        e.getGuild().getTextChannelById(Database.adminId.get(0).split(" ")[0]).sendMessage(Database.sendMessage).queue();
                    }
                    e.getMessage().addReaction("\uD83C\uDF89").queue();
                }


            }else{
                e.getMessage().addReaction("⛔").queue();
                EmbedBuilder failureBuilder = new EmbedBuilder()
                        .setDescription(String.format("**Oops! you broke the counting chain at** `%s`, next number was: `%s`", counted, counted + 1))
                        .setColor(Color.BLACK);
                e.getMessage().replyEmbeds(failureBuilder.build()).queue();
                Database.setUser(author.getId(), "counted", 0, false);
            }

        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

    }

}
