package com.lucaryholt.Main;

import com.lucaryholt.Handler.ClientHandler;

public class Main {

    public static void main(String[] args) {
        new ClientHandler(5000).start();
    }

}
