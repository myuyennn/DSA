package maps;

// import java.sql.Array;
// import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 5;
    private int size;
    private int capacity;
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    SimpleEntry<K, V>[] entries;

    // You may add extra fields or helper methods though!

    /**
     * Constructs a new ArrayMap with default initial capacity.
     */
    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Constructs a new ArrayMap with the given initial capacity (i.e., the initial
     * size of the internal array).
     *
     * @param initialCapacity the initial capacity of the ArrayMap. Must be > 0.
     */
    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
        size = 0;
        capacity = initialCapacity;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        Iterator<Map.Entry<K, V>> iter = new ArrayMapIterator<>(entries);
        Map.Entry<K, V> temp;
        K tempKey;

        while (iter.hasNext())
        {
            temp = iter.next();
            tempKey = temp.getKey();
            if (tempKey == key || Objects.equals(tempKey, key)) {
                return temp.getValue();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        SimpleEntry<K, V> insert = new SimpleEntry<>(key, value);
        double lambda = (double) size / (double) capacity;
        if (lambda > 0.75)
        {
            capacity = capacity * 2;
            SimpleEntry<K, V>[] resize = createArrayOfEntries(capacity);
            for (int i = 0; i < size; i++)
            {
                resize[i] = entries[i];
            }
            entries = resize;
        }
        Iterator<Map.Entry<K, V>> iter = new ArrayMapIterator<>(entries);
        Map.Entry<K, V> temp;

        while (iter.hasNext())
        {
            temp = iter.next();
            if ((temp.getKey() != null) && Objects.equals(temp.getKey(), key) || temp.getKey() == key)
            {
                V old = temp.getValue();
                temp.setValue(value);
                return old;
            }
        }
        entries[size] = insert;
        size += 1;
        return null;
    }

    @Override
    public V remove(Object key) {
        Iterator<Map.Entry<K, V>> iter = new ArrayMapIterator<>(entries);
        Map.Entry<K, V> temp;
        int tracker = -1;
        K tempKey;
        V value;

        while (iter.hasNext())
        {
            temp = iter.next();
            tracker += 1;
            tempKey = temp.getKey();

            if (tempKey == key || Objects.equals(tempKey, key)) {

                value = temp.getValue();
                if (!(iter.hasNext()))
                {
                    entries[size - 1] = null;
                    size -= 1;
                    return value;
                }
                else
                {
                    entries[tracker] = entries[size - 1];
                    entries[size - 1] = null;
                    size -= 1;
                    return value;
                }
            }
        }
        return null;
    }

    @Override
    public void clear() {
        this.entries = createArrayOfEntries(capacity);
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        Iterator<Map.Entry<K, V>> iter = new ArrayMapIterator<>(entries);
        Map.Entry<K, V> temp;
        while (iter.hasNext())
        {
            temp = iter.next();
            K currKey = temp.getKey();
            if (currKey == key || Objects.equals(currKey, key))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: You may or may not need to change this method, depending on whether you
        // add any parameters to the ArrayMapIterator constructor.
        return new ArrayMapIterator<>(this.entries);
    }

    /*
    // Doing so will give you a better string representation for assertion errors the debugger.
    @Override
    public String toString() {
        return super.toString();
    }*/

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        // You may add more fields and constructor parameters

        private int index;
        public ArrayMapIterator(SimpleEntry<K, V>[] entries) {
            this.entries = entries;
            index = 0;
        }

        @Override
        public boolean hasNext() {
            if (entries[index] == null)
            {
                return false;
            }
            return true;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            SimpleEntry<K, V> currentPair = entries[index];
            index += 1;
            return currentPair;
        }


        public int getIndex()
        {
            return index;
        }
    }
}
