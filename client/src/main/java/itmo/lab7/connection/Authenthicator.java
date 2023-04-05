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
    public static String authorize(Connector connector) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("sign_up or sign_in using login:password");
            String requisites = scanner.nextLine();
            Pattern regex = Pattern.compile("(.*):(.*)$");
            Matcher matcher = regex.matcher(requisites);
            if (!matcher.find()) {
                System.out.println("Wrong format. Try again");
                continue;
            }
            String login = matcher.group(1);
            connector.send(CommandSerializer.serialize(new Request(new Command(CommandType.SERVICE, "sign_up %s".formatted(requisites)))));
            if (connector.receive().equals("OK")) {
                scanner.close();
                return login;
            } else {
                System.out.println("Something went wrong. Try again");
            }
        }
    }
}
