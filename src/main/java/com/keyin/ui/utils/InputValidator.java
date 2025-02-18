//package com.keyin.ui.utils;
//
//public class InputValidator {
//    public static boolean isValidName(String name) {
//        return name != null && !name.trim().isEmpty() && name.length() <= 50;
//    }
//
//    public static boolean isValidCommand(String command) {
//        if (command == null || command.trim().isEmpty()) {
//            return false;
//        }
//
//        // Add valid commands here
//        return switch (command.toLowerCase()) {
//            case "status", "proceed", "help", "quit" -> true;
//            default -> false;
//        };
//    }
//}