package com.lucaryholt.Main;

import com.lucaryholt.Handler.ChatHandler;
import com.lucaryholt.Handler.UIHandler;

public class Main {

    public static void main(String[] args) {
        new UIHandler();
        new ChatHandler().chatLoop();
    }

}

