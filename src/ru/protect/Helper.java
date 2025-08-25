package ru.protect;

import ru.levinov.managment.Managment;
import ru.levinov.util.UserProfile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class Helper {


    public static void startHello() throws IOException {
        Managment.USER_PROFILE = new UserProfile("levin1337", "Dev");
    }
}
