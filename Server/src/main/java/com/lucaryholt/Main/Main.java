package com.lucaryholt.Main;

import com.lucaryholt.Tool.ProjectVar;
import com.lucaryholt.UI.Console;

public class Main {

    public static void main(String[] args) {
        if(args.length != 0){
            for(String s : args){
                switch (s){
                    case "-tcp" :   ProjectVar.connection = "tcp";
                                    System.out.println("tcp enabled...");
                                    break;
                }
            }
        }

        new Console().start();
    }

}
