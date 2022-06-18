package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.example.Setup.Database;
import org.example.Setup.setupCommands;

import javax.security.auth.login.LoginException;

public class Main {
    public static JDA jda;
    public static void main(String[] args) throws LoginException {

        jda = JDABuilder.createLight(System.getenv("token"))
                .addEventListeners(new Database())
                .addEventListeners(new setupCommands())
                .addEventListeners(new Counter())
                .build();
    }
}