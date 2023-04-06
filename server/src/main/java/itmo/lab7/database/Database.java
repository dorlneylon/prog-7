package itmo.lab7.database;

import itmo.lab7.basic.baseclasses.Movie;
import itmo.lab7.basic.baseenums.MpaaRating;
import itmo.lab7.basic.moviecollection.MovieCollection;
import itmo.lab7.server.ServerLogger;
import itmo.lab7.utils.serializer.Serializer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.io.*;
import static itmo.lab7.server.UdpServer.collection;

/**
 * Database class for connecting to and interacting with a database.
 */
public class Database {
    private final Connection connection;

    /**
     * Constructor for Database class.
     *
     * @param url      The URL of the database to connect to.
     * @param user     The username for the database.
     * @param password The password for the database.
     * @throws SQLException If the connection to the database fails.
     */
    public Database(String url, String user, String password) throws SQLException {
        Connection connectionTry;
        try {
            connectionTry = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new SQLException("Unable to connect to database: ", e);
        }
        if (connectionTry == null) throw new SQLException("Unable to connect to database.");
        this.connection = connectionTry;
    }

    /**
     * Adds a new user to the database.
     *
     * @param login    The login of the new user.
     * @param password The password of the new user.
     * @return true if the user was successfully added, false otherwise.
     */

    public boolean addNewUser(String login, String password) {
        if (isUserExist(login)) return false;
        byte flags = 0; // last 2 digits are flags
        try {
            // Hash the password using SHA-256
            String encryptedPassword = Encryptor.encryptString(password);

            // Create a prepared statement to insert a new user into the users table
            PreparedStatement userStatement = connection.prepareStatement("INSERT INTO \"user\" (login, password) VALUES (?, ?)");
            // Set the login and password parameters of the prepared statement
            userStatement.setString(1, login);
            userStatement.setString(2, encryptedPassword);
            // If the user was successfully added, set the first flag to 1
            if (userStatement.executeUpdate() > 0) flags |= 1;

            // Create a prepared statement to insert a new user into the user_history table
            PreparedStatement historyStatement = connection.prepareStatement("INSERT INTO \"user_history\" (user_login) VALUES (?)");
            // Set the login and command_history parameters of the prepared statement
            historyStatement.setString(1, login);
            // If the user was successfully added, set the second flag to 1
            if (historyStatement.executeUpdate() > 0) flags |= 2;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to add new user " + e.getMessage());
        } finally {
            try {
                // If both flags are not set, delete the user from the users table
                if (flags != 3) {
                    String sql = "DELETE FROM \"user\" WHERE login = ?";
                    PreparedStatement pre = connection.prepareStatement(sql);
                    pre.setString(1, login);
                    pre.executeUpdate();
                }
            } catch (Exception finalException) {
                // Log any errors that occur
                ServerLogger.getLogger().log(Level.INFO, "Unable to delete user " + finalException.getMessage());
            }
        }
        // Return true if both flags are set, false otherwise
        return flags == 3;
    }


    /**
     * Checks if a user exists in the database
     *
     * @param login the user's login
     * @return true if the user exists, false otherwise
     */
    public boolean isUserExist(String login) {
        try {
            String sql = "SELECT * FROM \"user\" WHERE login = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setString(1, login);
            return pre.executeQuery().next();
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to check user " + e.getMessage());
        }
        return false;
    }

    /**
     * Adds a command to the user's command history.
     *
     * @param login   The user's login.
     * @param command The command to add to the user's history.
     * @return true if the command was added successfully, false otherwise.
     */
    public boolean addCommandToHistory(String login, String command) {
        try {
            // This update query works like sized stack.
            String sql = """
                    UPDATE "user_history"
                    SET command_history =
                            CASE
                                WHEN array_length(command_history, 1) = 7
                                    THEN command_history[2:7] || ARRAY [?]::TEXT[]
                                ELSE command_history || ARRAY [?]::TEXT[]
                                END
                    WHERE user_login = ?;""";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setString(1, command);
            pre.setString(2, command);
            pre.setString(3, login);
            return pre.executeUpdate() > 0;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to add command to history " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves the command history for the specified user.
     *
     * @param login The user's login.
     * @return An array of strings containing the user's command history.
     */
    public String[] getCommandHistory(String login) {
        try {
            String sql = "SELECT command_history FROM \"user_history\" WHERE user_login = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setString(1, login);
            ResultSet result = pre.executeQuery();
            if (result.next()) return (String[]) result.getArray(1).getArray();
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to get command history " + e.getMessage());
        }
        return new String[0];
    }

    /**
     * Checks if the user is registered in the database.
     *
     * @param login    The user's login.
     * @param password The user's password.
     * @return true if the user is registered, false otherwise.
     */
    public boolean userSignIn(String login, String password) {
        try {
            String hashedPassword = Encryptor.encryptString(password);
            String sql = "SELECT * FROM \"user\" WHERE login = ? AND password = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setString(1, login);
            pre.setString(2, hashedPassword);
            ResultSet result = pre.executeQuery();
            if (result.next()) return true;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to check user " + e.getMessage());
        }
        return false;
    }

    public boolean insertToCollection(String login, Movie movie) {
        try {
            String sql = "INSERT INTO \"collection\" (id, editor, movie, last_modified_date) VALUES (?, ?, ?, ?)";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setInt(1, Math.toIntExact(movie.getId()));
            pre.setString(2, login);
            Array array = connection.createArrayOf("BYTEA", new Object[]{Serializer.serialize(movie)});
            pre.setArray(3, array);
            pre.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return pre.executeUpdate() > 0;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to insert to collection " + e.getMessage());
        }
        return false;
    }

    public boolean removeByKey(Long key) {
        try {
            String sql = "DELETE FROM \"collection\" WHERE id = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setLong(1, key);
            return pre.executeUpdate() > 0;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to remove by key " + e.getMessage());
        }
        return false;
    }

    public boolean clearCollection() {
        try {
            String sql = "DELETE FROM \"collection\"";
            PreparedStatement pre = connection.prepareStatement(sql);
            return pre.executeUpdate() > 0;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to clear collection " + e.getMessage());
        }
        return false;
    }

    public void removeByMpaaRating(String username, MpaaRating mpaaRating) {
        try {
            String selectSql = "SELECT id FROM \"collection\" WHERE editor = ?";
            PreparedStatement selectPre = connection.prepareStatement(selectSql);
            selectPre.setString(1, username);
            ResultSet selectResult = selectPre.executeQuery();

            List<Integer> movieIds = new ArrayList<>();
            while (selectResult.next()) {
                movieIds.add(selectResult.getInt(1));
            }

            String deleteSql = "DELETE FROM \"collection\" WHERE id = ?";
            PreparedStatement deletePre = connection.prepareStatement(deleteSql);
            for (int id : movieIds) {
                Movie movie = getMovieById(username, id);
                if (movie != null && movie.getRating().equals(mpaaRating)) {
                    deletePre.setInt(1, id);
                    deletePre.executeUpdate();
                }
            }
        } catch (SQLException e) {
            ServerLogger.getLogger().log(Level.INFO, "Unable to remove by mpaa rating " + e.getMessage());
        }
    }

    // Возвращаем только те объекты, которые были созданы пользователем.
    private Movie getMovieById(String username, int id) throws SQLException {
        String sql = "SELECT movie FROM \"collection\" WHERE id = ? AND editor = ?";
        PreparedStatement pre = connection.prepareStatement(sql);
        pre.setInt(1, id);
        pre.setString(2, username);
        ResultSet result = pre.executeQuery();
        if (result.next()) {
            // Может, не работает.
            return (Movie) result.getArray(1).getArray();
        }
        return null;
    }

    public MovieCollection getCollection() {
        try {
            // Get all rows from the collection table
            String sql = "SELECT id, movie FROM \"collection\"";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Create a new MovieCollection object to hold the results
            MovieCollection collection = new MovieCollection();

            // Iterate over each row in the result set
            while (resultSet.next()) {
                // Get the id and byte array value of the movie column
                long id = resultSet.getLong("id");
                byte[] movieBytes = resultSet.getBytes("movie");

                // Deserialize the movie object from the byte array
                Movie movie = (Movie) deserialize(movieBytes);

                // Add the movie object to the collection
                collection.insert(id, movie);
            }

            // Return the populated MovieCollection object
            return collection;

        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to get collection " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private Object deserialize(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
