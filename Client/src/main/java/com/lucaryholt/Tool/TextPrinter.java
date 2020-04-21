package com.lucaryholt.Tool;

public class TextPrinter {

    private String reset = "\u001B[0m";
    private String blue = "\u001B[34m";
    private String yellow = "\u001B[33m";

    public String receivedMessage(String text){
        return yellow + text + reset;
    }

}
