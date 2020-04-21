package com.lucaryholt.UI;

import com.lucaryholt.Service.MessageService;

import java.util.Scanner;

public class Console {

    private MessageService mS = new MessageService();

    public void start(){
        Scanner sc = new Scanner(System.in);

        System.out.println("What port?");
        int port = sc.nextInt();

        mS.start(port);
    }

}
