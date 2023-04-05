package itmo.lab7.core;

import itmo.lab7.basic.utils.serializer.CommandSerializer;
import itmo.lab7.commands.*;
import itmo.lab7.connection.Authenthicator;
import itmo.lab7.connection.Connector;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static itmo.lab7.commands.CollectionValidator.getCollectionSize;

public class ClientCore {
    private final Connector connector;
    private final int chunkSize = 20;

    private String name = null;

    public ClientCore(InetAddress address, int port) {
        try {
            connector = new Connector(address, port);
            connector.setBufferSize(8192*8129);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        CollectionValidator.setConnector(connector);
    }

    public void run() {
        try {
//            this.name = Authenthicator.authorize(connector);
        } catch (Exception e) {
            System.err.println("Unable to authorize: " + e.getMessage());
            return;
        }
        Scanner scanner = new Scanner(System.in);
        String[] userInput;

        while (true) {
            System.out.print("Enter command: ");
            userInput = scanner.nextLine().split(" ");
            if (userInput.length < 1) continue;
            String[] args = Arrays.copyOfRange(userInput, 1, userInput.length);
            CommandType commandType = CommandUtils.getCommandType(userInput[0]);
            Request request = new Request(CommandFactory.createCommand(commandType, args), name);
            if (request.getCommand() == null) continue;

            try {
                connector.send(CommandSerializer.serialize(request));
                String response = connector.receive();

                if (List.of(CommandType.SHOW, CommandType.PRINT_ASCENDING, CommandType.PRINT_DESCENDING).contains(commandType)) {
                    int collectionSize = getCollectionSize();
                    if (collectionSize == 0) {
                        System.out.println(response);
                        continue;
                    }

                    for (int i = 0; i * chunkSize < collectionSize; i++) {
                        System.out.println(response);
                        if (i * chunkSize + chunkSize >= collectionSize) break;
                        System.out.print("Do you want to continue? (y/n): ");
                        if (!scanner.nextLine().equalsIgnoreCase("y")) break;
                        connector.send(CommandSerializer.serialize(new Command(CommandType.SHOW, chunkSize * (i + 1))));
                        response = connector.receive();
                    }
                    continue;
                }

                if (!response.isEmpty()) System.out.println(response);
            } catch (Exception e) {
                System.err.println("Unable to send/receive request/response to/from the server: " + e.getMessage());
            }

        }
    }
}
