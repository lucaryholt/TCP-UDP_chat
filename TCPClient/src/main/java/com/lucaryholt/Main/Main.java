package com.lucaryholt.Main;

import com.lucaryholt.UI.Console;
import com.lucaryholt.UI.GUI;

public class Main {

    public static void main(String[] args) {
        boolean guiBool = false;

        if(args.length != 0){
            for(String s : args){
                switch (s){
                    case "-ui"  :   guiBool = true;
                                    break;
                }
            }
        }

        if(guiBool){
            new GUI();
        }else{
            new Console();
        }
    }

}

