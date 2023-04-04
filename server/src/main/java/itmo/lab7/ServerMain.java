package itmo.lab7;

import itmo.lab7.basic.moviecollection.MovieCollection;
import itmo.lab7.server.UdpServer;
import itmo.lab7.utils.config.Config;
import itmo.lab7.xml.Xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class ServerMain {
    /**
     * Global variable with name of collection's xml file
     */
    public static String collectionFileName;
    private static Integer serverPort;

    static {
        Config config = null;
        try {
            config = new Config("server.scfg");
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find config file " + "server.scfg");
            System.exit(1);
        }
        collectionFileName = config.get("collection_file");
        if (collectionFileName == null) {
            // Setting up the default file name
            collectionFileName = "server.xml";
        }
        try {
            serverPort = Integer.parseInt(config.get("server_port"));
        } catch (NumberFormatException e) {
            // Setting up the default port
            serverPort = 5050;
        }
    }

    public static void main(String[] args) {
        MovieCollection collection;
        try {
            collection = new Xml(new File(collectionFileName)).newReader().parse();
        } catch (IOException e) {
            System.err.println("Unable to find collection file " + collectionFileName);
            System.err.println("New collection file will be created automatically after a few changes.");
            collection = new MovieCollection();
        }
        UdpServer server = new UdpServer(collection, serverPort);
        server.run();
    }
}