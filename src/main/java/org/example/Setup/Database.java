package org.example.Setup;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Database extends ListenerAdapter {
    public static String countingChannelId = "null";
    public static boolean hasRewards = false;
    public static int amountCount = 0;
    public static boolean sendType = false;
    public static String sendMessage = "Null";
    public static String emoji = ":white_check_mark:";
    public static List<String> adminId = new ArrayList<>();
    public static MongoCollection collection;
    @Override
    public void onReady(ReadyEvent e){

        String uri = System.getenv("uri");
        MongoClientURI clientURI = new MongoClientURI(uri);
        MongoClient client = new MongoClient(clientURI);
        MongoDatabase database = client.getDatabase("count");
        collection = database.getCollection("count");

    }



    public static void set(String Id, String Key, String value, boolean isAdd){
            updateDB(Id,"serverId", Key, value, isAdd);
    }

    public static void setUser(String Id, String Key, String value, boolean isAdd){
            updateDB(Id, "userId",Key, value, isAdd );
    }


    public static Document get(String Id){
        return (Document) collection.find(new Document("serverId", Id)).cursor().next();
    }

    public static Document getUser(String Id){
        return (Document) collection.find(new Document("userId", Id)).cursor().next();
    }

    public static boolean sync(String serverId){
        //it will put the values according to servers
        try{
            Document serverConfig = get(serverId);
            countingChannelId = (String)serverConfig.get("countingChannel");
            hasRewards = (boolean) serverConfig.get("hasRewards");
            amountCount = (Integer) serverConfig.get("beforeReward");
            sendType = (boolean) serverConfig.get("sendType");
            sendMessage = (String) serverConfig.get("sendMessage");
            emoji = (String) serverConfig.get("emoji");
            adminId.clear();
            adminId.add((String) serverConfig.get("emoji"));

            return true;
        }catch (NoSuchElementException e){
            return false;
        }
    }



    private static void createDB(String Id){
        //server config
        Document document = new Document("serverId", Id)
                .append("countingChannel", "none")
                .append("hasRewards", false)
                .append("beforeReward", 0)
                .append("admins", "0")
                .append("sendType", false)  //false means send to channel
                .append("sendMessage", "null")
                .append("emoji", ":sparkles:");

        collection.insertOne(document);

    }


    private static void createUserDB(String userId){
        //user config
        Document document = new Document("userId", userId)
                .append("counted", 0);
        collection.insertOne(document);
    }



    //add user method
    //user database template
    //addupdate method


    private static void updateDB(String Id, String field,  String key, String value, boolean isAdd){
        //for server
        Document document = null;
        try{
            document = (Document) collection.find(new Document(field, Id)).cursor().next();
        }catch (NoSuchElementException exception){
            if(field.equalsIgnoreCase("serverId")){
                createDB(Id);
            }else{
                createUserDB(Id);
            }
        }

        if(!isAdd){
            Document Updatedocument = new Document(key, value);
            Bson updateKey = new Document("$set", Updatedocument);
            collection.updateOne(document, updateKey);
        }else{
            Document Updatedocument = new Document(key, document.get("key") + value);
            Bson updateKey = new Document("$set", Updatedocument);
            collection.updateOne(document, updateKey);
        }

    }

}