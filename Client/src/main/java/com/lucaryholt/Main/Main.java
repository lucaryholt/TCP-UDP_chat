package com.lucaryholt.Main;

import com.lucaryholt.Tool.ProjectVar;
import com.lucaryholt.UI.Console;
import com.lucaryholt.UI.GUI;

public class Main {

    public static void main(String[] args) {
        boolean guiBool = true;

        if(args.length != 0){
            for(String s : args){
                switch (s){
                    case "-nogui"   :   guiBool = false;
                                        break;
                    case "-tcp"     :   ProjectVar.connection = "tcp";
                                        System.out.println("tcp enabled...");
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

