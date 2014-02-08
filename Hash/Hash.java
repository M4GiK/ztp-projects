import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Project Praca domowa 02 – hashing.
 * Copyright Michał Szczygieł
 * Created at Oct 24, 2013.
 */

/**
 * 
 * Hash class, responsible for inserting, finding and deleting.
 * 
 * @author Michał Szczygieł <michal.szczygiel@wp.pl>
 * 
 */
public class Hash {
    /**
     * Count collision, updated when insert method is called.
     */
    private int collisionAmount = 0;

    /**
     * Variable contains data for hashes.
     */
    private ArrayList<LinkedList<String>> hashTable;

    /**
     * Constructor. Create hashTable for Hash class.
     * 
     * @param hashSize
     *            size of hash table
     */
    public Hash(int hashSize) {
        hashTable = new ArrayList<LinkedList<String>>(hashSize);

        for (int i = 0; i < hashSize; ++i) {
            hashTable.add(new LinkedList<String>());
        }
    }

    /**
     * Binary Search for Hash table with few modifications for hash tables.
     * Searches the specified list for the specified object using the binary
     * search algorithm. The list must be sorted into ascending order according
     * to the specified comparator (as by the sort(List, Comparator) method),
     * prior to making this call. If it is not sorted, the results are
     * undefined. If the list contains multiple elements equal to the specified
     * object, there is no guarantee which one will be found. This method runs
     * in log(n) time for a "random access" list (which provides
     * near-constant-time positional access). If the specified list does not
     * implement the RandomAccess interface and is large, this method will do an
     * iterator-based binary search that performs O(n) link traversals and O(log
     * n) element comparisons.
     * 
     * Parameters: list the list to be searched. key the key to be searched for.
     * c the comparator by which the list is ordered. A null value indicates
     * that the elements' natural ordering should be used. Returns: the index of
     * the search key, if it is contained in the list; otherwise, (-(insertion
     * point) - 1). The insertion point is defined as the point at which the key
     * would be inserted into the list: the index of the first element greater
     * than the key, or list.size() if all elements in the list are less than
     * the specified key. Note that this guarantees that the return value will
     * be >= 0 if and only if the key is found. Throws: ClassCastException - if
     * the list contains elements that are not mutually comparable using the
     * specified comparator, or the search key is not mutually comparable with
     * the elements of the list using this comparator.
     * 
     * 
     * @param list
     * 
     * @param item
     * 
     * @return the index of the search key
     */
    private int binarySearch(LinkedList<String> list, String word) {
        int lower = 0;
        int upper = list.size() - 1;

        while (lower <= upper) {
            int mid = (lower + upper) >>> 1;
            int comparison = list.get(mid).compareTo(word);
            collisionAmount++;

            if (comparison < 0) {
                lower = mid + 1;
            } else if (comparison > 0) {
                upper = mid - 1;
            } else {
                return mid;
            }
        }

        return -(lower + 1);
    }

    /**
     * Searching hash table.
     * 
     * @param word
     * 
     * @return true if hash contain word, false if not
     */
    public boolean find(String word) {
        LinkedList<String> hashChain = getHash(word);
        return hashChain.contains(word);
    }

    /**
     * Gets collision amount.
     * 
     * @return current collision count
     */
    public int getCollisionAmount() {
        return this.collisionAmount;
    }

    /**
     * Gets proper array list of hashes.
     * 
     * @param word
     * 
     * @return hash chain
     */
    private LinkedList<String> getHash(String word) {
        int hash = Math.abs(word.hashCode()) % hashTable.size();
        return hashTable.get(hash);
    }

    /**
     * Inserting to chain, if collision happened increments collision.
     * 
     * @param word
     * 
     */
    public void insert(String word) {

        LinkedList<String> chain = getHash(word);
        int index = binarySearch(chain, word);

        if (index < 0) {
            chain.add(-index - 1, word);
        }

    }

    /**
     * Removing string from hash table.
     * 
     * @param word
     * 
     */
    public void remove(String word) {
        LinkedList<String> chain = getHash(word);
        chain.remove(word);
    }
}
