package itmo.lab7.connection;

import itmo.lab7.basic.utils.serializer.CommandSerializer;
import itmo.lab7.commands.Command;
import itmo.lab7.commands.CommandType;
import itmo.lab7.commands.Request;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static itmo.lab7.basic.utils.strings.StringUtils.toSnakeCase;

public class Authenticator {
    public static String authorize(Scanner scanner, Connector connector) throws Exception {
        while (true) {
            System.out.println("sign_up or sign_in using login:password without spaces");
            System.out.println("For example: sign_up login:password");
            System.out.print("Authorization: ");
            String requisites = scanner.nextLine();
            Pattern regex = Pattern.compile("(.+):(.+)$");
            Matcher matcher = regex.matcher(requisites.replace("sign_up ", "").replace("sign_in ", ""));
            if (!List.of("sign_up", "sign_in").contains(requisites.split(" ")[0])) {
                System.err.println("You are not yet authorized to use the application. Try again");
                continue;
            }
            if (!matcher.find()) {
                System.err.println("Wrong format. Try again.");
                continue;
            }
            String login = toSnakeCase(matcher.group(1));
            connector.send(CommandSerializer.serialize(new Request(new Command(CommandType.SERVICE, requisites))));
            String response = connector.receive();
            if (response.equals("OK")) {
                System.out.printf("Welcome, %s.\n", login);
                return login;
            }
            System.err.println(response);
        }
    }
}
