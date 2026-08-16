package org.example.mainframe;
import org.example.controller.*;

public class Main {
    public static void main(String[] args) throws Exception {
        new LotteryController().startServer();
    }
}