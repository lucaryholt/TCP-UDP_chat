package com.lucaryholt.Main;

import com.lucaryholt.Handler.ChatHandler;
import com.lucaryholt.Handler.UIHandler;

public class Main {

    public static void main(String[] args) {
        boolean ui = false;

        if(args.length != 0){
            for(String s : args){
                switch (s){
                    case "-ui"  :   new UIHandler();
                                    ui = true;
                                    break;
                }
            }
        }


        new ChatHandler(ui).chatLoop();
    }

}

