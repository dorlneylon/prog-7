package itmo.lab7;

import itmo.lab7.basic.moviecollection.MovieCollection;
import itmo.lab7.database.Database;
import itmo.lab7.server.UdpServer;
import itmo.lab7.utils.config.Config;
import itmo.lab7.xml.Xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;


public class ServerMain {
    /**
     * Global variable with name of collection's xml file
     */
    public static String collectionFileName;
    private static Integer serverPort;

    private static final String url;
    private static final String user;
    private static final String password;

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
        url = config.get("db_url");
        user = config.get("user");
        password = config.get("password");
    }

    public static void main(String[] args) {
        MovieCollection collection;
//        try {
//            collection = new Xml(new File(collectionFileName)).newReader().parse();
//        } catch (IOException e) {
//            System.err.println("Unable to find collection file " + collectionFileName);
//            System.err.println("New collection file will be created automatically after a few changes.");
//            collection = new MovieCollection();
//        }
        Database db = null;

        try {
            db = new Database(url, user, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        collection = db.getCollection();

                // db.addNewUser("kxrxh", "kxrxh");
//        System.out.println(db.userSignIn("kxrxh", "kxrxh"));

//        db.addCommandToHistory("kxrxh", "INSERT");
//        db.addCommandToHistory("kxrxh", "ADD");
//        System.out.println(Arrays.toString(db.getCommandHistory("kxrxh")));
        UdpServer server = new UdpServer(db, collection, serverPort);
        server.run();
    }
}