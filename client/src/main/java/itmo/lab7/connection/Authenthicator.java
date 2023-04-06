package itmo.lab7.connection;

import itmo.lab7.basic.utils.serializer.CommandSerializer;
import itmo.lab7.commands.Command;
import itmo.lab7.commands.CommandType;
import itmo.lab7.commands.Request;

import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Authenthicator {
    public static String authorize(Scanner scanner, Connector connector) throws Exception {
        while (true) {
            System.out.println("sign_up or sign_in using login:password");
            String requisites = scanner.nextLine();
            Pattern regex = Pattern.compile("(.*):(.*)$");
            Matcher matcher = regex.matcher(requisites.replace("sign_up ", "").replace("sign_in ", ""));
            if (!matcher.find()) {
                System.out.println("Wrong format. Try again");
                continue;
            }
            String login = matcher.group(1);
            connector.send(CommandSerializer.serialize(new Request(new Command(CommandType.SERVICE, requisites))));
            String response = connector.receive();
            if (response.equals("\u001B[35mOK\u001B[0m")) {
                return login;
            } else {
                System.out.println("Something went wrong. Try again");
            }
        }
    }
}
