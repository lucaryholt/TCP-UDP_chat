package com.lucaryholt.Tool;

public class ConsolePrinter {

    private String reset = "\u001B[0m";
    private String blue = "\u001B[34m";
    private String yellow = "\u001B[33m";

    public void receivedMessage(String text){
        System.out.println(yellow + text + reset);
    }

}
