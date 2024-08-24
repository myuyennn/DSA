package priorityqueues;

// import com.sun.jdi.Value;

// import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
// import java.util.Objects;
// import java.util.PriorityQueue;

/**
 * @see ExtrinsicMinPQ
 */
public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code.
    static final int START_INDEX = 0;
    List<PriorityNode<T>> items;
    private Map<T, Integer> indexLib;
    private int size;

    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        indexLib = new HashMap<T, Integer>();
        size = 0;
    }

    // Here's a method stub that may be useful. Feel free to change or remove it, if you wish.
    // You'll probably want to add more helper methods like this one to make your code easier to read.

    /**
     * A helper method for swapping the items at two indices of the array heap.
     */
    private void swap(int a, int b) {
        PriorityNode<T> tempA = items.get(a);
        PriorityNode<T> tempB = items.get(b);
        items.set(a, tempB);
        items.set(b, tempA);
        indexLib.put(tempA.getItem(), b);
        indexLib.put(tempB.getItem(), a);
    }

    @Override
    public void add(T item, double priority) {
        if (indexLib.containsKey(item)) { // more efficient way to deal with dupes
            throw new IllegalArgumentException();
        }
        PriorityNode<T> newNode = new PriorityNode<T>(item, priority);
        items.add(size, newNode);
        size += 1;
        int currIndex = size - 1;
        indexLib.put(item, currIndex);
        percolateUp(currIndex);
    }

    @Override
    public boolean contains(T item) {
        return indexLib.containsKey(item);
    }

    @Override
    public T peekMin() {
        //throws if items list is empty, can't access empty array
        if (items.isEmpty()) {
            throw new NoSuchElementException();
        }
        PriorityNode<T> min = items.get(START_INDEX);
        return min.getItem();
    }

    @Override
    public T removeMin() {
        if (items.isEmpty()) {
            throw new NoSuchElementException();
        }
        PriorityNode<T> min = items.get(START_INDEX); //store min value for later return
        swap(START_INDEX, size - 1); //swaps with last node in the array
        items.remove(size - 1); // removes the last node in array (last node is now the minimum we want to remove)
        size -= 1;
        indexLib.remove(min.getItem()); // removes the min node from map
        percolateDown(START_INDEX);
        return min.getItem();
    }

    @Override
    public void changePriority(T item, double priority) {
        if (!indexLib.containsKey(item)) {
            throw new NoSuchElementException();
        }
        int index = indexLib.get(item);
        items.get(index).setPriority(priority);
        // call both to fix any violations of heap invariant
        percolateUp(index);
        percolateDown(index);
    }

    @Override
    public int size() {
        return size;
    }

    //helper method to percolate nodes up the tree to correct heap invariant
    // runs in o(logn)
    private void percolateUp(int currIndex) {
        if (currIndex > 0) { // Check if current node is not the root/ base case
            int parentIndex = (currIndex - 1) / 2; // Find the parent index
            // Compare current node with its parent
            if (items.get(currIndex).getPriority() < items.get(parentIndex).getPriority()) {
                swap(currIndex, parentIndex); // Swap if current node has higher priority
                percolateUp(parentIndex); // Recursive call with the parent index
            }
        }
    }


    //helper method to percolate nodes down the tree
    private void percolateDown(int parentIndex) {
        // sets the "smallest" node equal to parent, this would lay out foundation for base case
        int smallest = parentIndex;
        int rightChildIndex = 2 * parentIndex + 2;
        int leftChildIndex = 2 * parentIndex + 1;

            //following checks to see which of the 2 children of the parent is smaller

            // check if left child has lower priority than parent, will set "smallest" = left index if it is
            // checks index to size to see if it is inbound of heap array (checks if parent is not null)
            if (leftChildIndex < size && items.get(leftChildIndex).getPriority() < items.get(smallest).getPriority())
            {
                smallest = leftChildIndex;
            }

            // will set smallest = right index if it is smaller than left index or parent
            // checks index to size to see if it is inbound of heap
            if (rightChildIndex < size && items.get(rightChildIndex).getPriority() < items.get(smallest).getPriority())
            {
                smallest = rightChildIndex;
            }

            // base case, if smallest == parent, (there are no more children to compare/heap valid) recursion stops
            // If the smallest is not the parent (i.e., the parent is not following min heap property)
            if (smallest != parentIndex)
            {
                // Swap the parent with the smallest child
                swap(parentIndex, smallest);

                // Recursively percolate down
                percolateDown(smallest);
            }
        }
    }
