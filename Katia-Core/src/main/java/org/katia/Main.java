package org.katia;

public class Main {
    public static void main(String[] args) {
        Utils.initialize();
        Logger.log(Logger.Type.INFO, "Test");
        Logger.logToFile("test.log");
    }
}