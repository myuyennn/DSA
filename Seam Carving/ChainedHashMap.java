package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 0.75;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 1;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 10;

    private int chainCount;

    private double loadFactor;

    private int capacity;

    private int size;

    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    AbstractIterableMap<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!

    /**
     * Constructs a new ChainedHashMap with default resizing load factor threshold,
     * default initial chain count, and default initial chain capacity.
     */
    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    /**
     * Constructs a new ChainedHashMap with the given parameters.
     *
     * @param resizingLoadFactorThreshold the load factor threshold for resizing. When the load factor
     *                                    exceeds this value, the hash table resizes. Must be > 0.
     * @param initialChainCount the initial number of chains for your hash table. Must be > 0.
     * @param chainInitialCapacity the initial capacity of each ArrayMap chain created by the map.
     *                             Must be > 0.
     */
    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        this.chains = this.createArrayOfChains(initialChainCount);
        chainCount = initialChainCount;
        loadFactor = resizingLoadFactorThreshold;
        capacity = chainInitialCapacity;
        size = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        int hashCode;
        if (Objects.equals(key, null)) {
            hashCode = 0;
        }
        hashCode = Math.abs(Objects.hashCode(key)) % chains.length;
        if (chains[hashCode] == null)
        {
            return null;
        }
        return chains[hashCode].get(key);
    }

    @Override
    public V put(K key, V value) {
        int hashCode;
        int rehash;
        V oldValue;

        // Resize needed
        if (((double) size / (double) chains.length) >= loadFactor)
        {
            AbstractIterableMap<K, V>[] resize = createArrayOfChains(chains.length * 2);
            ChainedHashMapIterator<K, V> oldChain = new ChainedHashMapIterator<>(chains);
            while (oldChain.hasNext())
            {
                Map.Entry<K, V> temp = oldChain.next();
                rehash = Math.abs(Objects.hashCode(temp.getKey())) % resize.length;

                if (Objects.equals(resize[rehash], null))
                {
                    AbstractIterableMap<K, V> newChain = createChain(capacity);
                    newChain.put(temp.getKey(), temp.getValue());
                    resize[rehash] = newChain;
                    chainCount += 1;
                }
                resize[rehash].put(temp.getKey(), temp.getValue());
            }
            chains = resize;
        }
        // No resize
        hashCode = Math.abs(Objects.hashCode(key)) % chains.length;
        if (chains[hashCode] == null)
        {
            AbstractIterableMap<K, V> newArrayMap = createChain(capacity);
            oldValue = newArrayMap.put(key, value);
            if (oldValue == null)
            {
                size += 1;
            }
            chains[hashCode] = newArrayMap;
            chainCount += 1;
        }
        else
        {
            oldValue = chains[hashCode].put(key, value);
            if (oldValue == null)
            {
                size += 1;
            }
        }
        return oldValue;
    }

    @Override
    public V remove(Object key) {
        int hashCode = Math.abs(Objects.hashCode(key)) % chains.length;
        if (chains[hashCode] == null)
        {
            return null;
        }
        V val = chains[hashCode].remove(key);
        if (val != null) {
            size -= 1;
        }
        return val;
    }

    @Override
    public void clear() {
        this.chains = createArrayOfChains(this.chainCount);
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        int hashCode = Math.abs(Objects.hashCode(key)) % chains.length;
        if (chains[hashCode] != null)
        {
            Iterator<Map.Entry<K, V>> currentArrayMap = chains[hashCode].iterator();
            while (currentArrayMap.hasNext())
            {
                if (Objects.equals(currentArrayMap.next().getKey(), key))
                {
                    return true;
                }
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
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    /*
    // Doing so will give you a better string representation for assertion errors the debugger.
    @Override
    public String toString() {
        return super.toString();
    }*/

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;
        // You may add more fields and constructor parameters
        private int index;

        private Iterator<Map.Entry<K, V>> currentArrayMapIterator;

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains)
        {
            this.chains = chains;
            index = -1;
            getNewIterator();
        }

        @Override
        public boolean hasNext()
        {
            return index < chains.length;
        }

        public void getNewIterator()
        {
            index++;

            while (index < chains.length && (chains[index] == null || !chains[index].iterator().hasNext()))
            {
                index++;
            }
            if (index < chains.length)
            {
                currentArrayMapIterator = chains[index].iterator();
            }

        }
        @Override
        public Map.Entry<K, V> next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            Entry<K, V> temp = currentArrayMapIterator.next();
            if (!currentArrayMapIterator.hasNext())
            {
                getNewIterator();
            }
            return temp;
        }
    }
}
