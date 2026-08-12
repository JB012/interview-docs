package com.interviewdocs;

import io.micronaut.runtime.Micronaut;
import io.github.cdimascio.dotenv.Dotenv;

public class Application {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );

        Micronaut.build(args)
             .mainClass(Application.class)
             .defaultEnvironments("prod")
             .start();
    }
}