package itmo.lab7.basic.moviecollection;

import itmo.lab7.server.ServerLogger;
import itmo.lab7.server.UdpServer;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * The abstract class {@code MHMap} is a wrapper for the {@code HashMap} class.
 * It provides a set of methods for working with the {@code HashMap} class.
 * It is inherited by the {@code MovieCol} class.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author dorlneylon
 * @see MovieCollection
 */
public abstract class MHMap<K, V> {
    private final ZonedDateTime initTime;
    /**
     * the map itself
     */
    private final HashMap<K, V> map;


    /**
     * Constructs a new {@code MHMap} object.
     */
    public MHMap() {
        this.map = new HashMap<>();
        this.initTime = ZonedDateTime.now();
    }

    /**
     * Inserts a value into the tree.
     *
     * @param value The value to insert.
     * @return True if the value was inserted, false otherwise.
     */
    public boolean insert(V value) {
        return this.insert(this.getKey(value), value);
    }

    /**
     * Inserts a key-value pair into the map.
     *
     * @param key   The key to be inserted.
     * @param value The value to be inserted.
     * @return True if the key-value pair was successfully inserted, false otherwise.
     */
    public boolean insert(K key, V value) {
        synchronized (this.map) {
            if (!this.map.containsKey(key)) {
                this.map.put(key, value);
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the key associated with the given value.
     *
     * @param value The value to retrieve the key for.
     * @return The key associated with the given value.
     */
    public abstract K getKey(V value);

    /**
     * Removes the element with the specified key from the map.
     *
     * @param key the key of the element to be removed
     * @return true if the element was removed, false otherwise
     */
    public boolean removeByKey(K key) {
        if (isKeyPresented(key)) {
            synchronized (this.map) {
                this.map.remove(key);
                return true;
            }
        }
        return false;
    }

    /**
     * get all the elements of the map in pairs ({@link K}, {@link V})
     */
    public Object[][] toArray() {
        Object[][] array = new Object[this.size()][2];
        int i = 0;
        synchronized (this.map) {
            for (K key : this.map.keySet()) {
                array[i][0] = key;
                array[i][1] = this.map.get(key);
                i++;
            }
        }
        return array;
    }


    /**
     * Removes the element with the specified value from the map.
     *
     * @param val The value of the element to be removed
     */
    public void removeByValue(V val) {
        this.removeByKey(this.getKey(val));
    }

    /**
     * Retrieves the value associated with the given key.
     *
     * @param key The key associated with the value to be retrieved.
     * @return The value associated with the given key.
     */
    public V get(K key) {
        synchronized (this.map) {
            return this.map.get(key);
        }
    }


    /**
     * Prints the elements of the collection in ascending order.
     *
     * @return A string representation of the elements in ascending order.
     */
    public abstract String printAscending();


    /**
     * Prints the elements of the collection in descending order.
     *
     * @return A string representation of the elements in descending order.
     */
    public abstract String printDescending();


    /**
     * Updates the value of the element with the specified key.
     *
     * @param key   the key of the element to update
     * @param value the new value of the element
     */
    public void update(K key, V value) {
        if (isKeyPresented(key)) {
            this.map.put(key, value);
            ServerLogger.getLogger().info("Element with key " + key + " was successfully updated");
        } else ServerLogger.getLogger().info("No such element with key " + key);
    }


    /**
     * Updates the value associated with the given key.
     *
     * @param value The value to update.
     */
    public void update(V value) {
        this.update(this.getKey(value), value);
    }


    /**
     * Returns the initial date of the object.
     *
     * @return the initial date of the object
     */
    public java.time.ZonedDateTime getInitDate() {
        return this.initTime;
    }


    /**
     * Removes all the mappings from this map.
     * The map will be empty after this call returns.
     */
    public void clear(String user) {
        for (K key : this.map.keySet()) {
            if (UdpServer.getDatabase().isUserEditor(user, Math.toIntExact((Long) key))) {
                this.map.remove(key);
            }
        }
    }


    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        synchronized (this.map) {
            return this.map.size();
        }
    }


    /**
     * Returns true if the map is empty, false otherwise.
     *
     * @return true if the map is empty, false otherwise
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * check if the map contains the key for the value
     */
    public abstract boolean contains(V value);


    /**
     * Checks if the given key is present in the map.
     *
     * @param key The key to check for.
     * @return true if the key is present in the map, false otherwise.
     */
    protected boolean isKeyPresented(K key) {
        synchronized (this.map) {
            return this.map.containsKey(key);
        }
    }


    /**
     * Returns true if this map maps one or more keys to the specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return true if this map maps one or more keys to the specified value
     */
    protected boolean containsValue(V value) {
        return this.map.containsValue(value);
    }

    /**
     * sort the values
     *
     * @return the sorted array of values
     */
    public abstract V[] getSortedMovies(boolean reverse);


    /**
     * Returns the map associated with this object.
     *
     * @return the map associated with this object
     */
    public HashMap<K, V> getMap() {
        return this.map;
    }

    /**
     * Returns an array containing the values of map, in
     * the order they are declared. This method may be used to iterate
     * over the values as follows:
     *
     * <pre>
     *   for (V v : V.values())
     *       System.out.println(v);
     *   </pre>
     *
     * @return an array containing the values of the map, in order they are declared
     */
    public abstract V[] values();


    /**
     * Returns a string containing information about the map.
     *
     * @return a string containing information about the map
     */
    public String info() {
        synchronized (this.map) {
            Class<?> a = this.map.isEmpty() ? null : this.map.entrySet().stream().toList().get(0).getKey().getClass();
            String keyTypeName = a == null ? "none" : a.getName();
            return "Type: " + this.getClass().getName() + "\n" + "Key type: " + keyTypeName + "\n" + "Date of initialization: " + this.getInitDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n" + "Number of elements: " + this.size();
        }
    }
}
