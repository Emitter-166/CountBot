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
    public static boolean actionType = false;
    public static String sendMessage = "Null";
    public static List<String> adminId = new ArrayList<>();
    public static MongoCollection collection;
    @Override
    public void onReady(ReadyEvent e){

        String uri = System.getenv("uri");
        MongoClientURI clientURI = new MongoClientURI(uri);
        MongoClient client = new MongoClient(clientURI);
        MongoDatabase database = client.getDatabase("count");
        collection = database.getCollection("count");
        sync("987664560599220264");
    }



    public static void set(String Id, String Key, Object value, boolean isAdd){
            updateDB(Id,"serverId", Key, value, isAdd);
    }

    public static void setUser(String Id, String Key, Object value, boolean isAdd){
            updateDB(Id, "userId",Key, value, isAdd );
    }


    public static Document get(String Id) throws InterruptedException {
        try{
            return (Document) collection.find(new Document("serverId", Id)).cursor().next();
        }catch (NoSuchElementException exception){
            createDB(Id);
            Thread.sleep(200);
            return (Document) collection.find(new Document("serverId", Id)).cursor().next();
        }
    }

    public static Document getUser(String Id) throws InterruptedException {
        try{
            return (Document) collection.find(new Document("userId", Id)).cursor().next();
        }catch (NoSuchElementException exception){
            createUserDB(Id);
            Thread.sleep(200);
            return (Document) collection.find(new Document("userId", Id)).cursor().next();
        }

    }

    public static boolean sync(String serverId){
        //it will put the values according to servers
        try{
            Document serverConfig = get(serverId);
            countingChannelId = (String)serverConfig.get("countingChannel");
            hasRewards = (boolean) serverConfig.get("hasRewards");
            amountCount = (Integer) serverConfig.get("beforeReward");
            actionType = (boolean) serverConfig.get("actionType");
            sendMessage = (String) serverConfig.get("sendMessage");
            adminId.clear();
            adminId.add((String) serverConfig.get("admins"));

            return true;
        }catch (NoSuchElementException | InterruptedException e){
            return false;
        }
    }



    private static void createDB(String Id){
        //server config
        Document document = new Document("serverId", Id)
                .append("countingChannel", "none")
                .append("hasRewards", false)
                .append("beforeReward", 0) //amount counts
                .append("admins", "0") //for admins and channels
                .append("actionType", false)  //false means send to channel
                .append("sendMessage", "null");

        collection.insertOne(document);

    }


    private static void createUserDB(String userId){
        //user config
        int defaultValue = 0;
        Document document = new Document("userId", userId)
                .append("counted", defaultValue);

        collection.insertOne(document);
    }



    //add user method
    //user database template
    //addupdate method


    private static void updateDB(String Id, String field,  String key, Object value, boolean isAdd){
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
            Document Updatedocument = new Document(key, (Integer) document.get(key) + Integer.parseInt((String) value));
            Bson updateKey = new Document("$set", Updatedocument);
            collection.updateOne(document, updateKey);
        }

    }

}