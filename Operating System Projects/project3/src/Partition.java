import Utils.BoundBuffer;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Partition {
    private static final int MAX_SIZE = 50;
    private BoundBuffer<KV> boundBuffer;
    private LinkedList<KV> linkedList;
    private HashSet<String> keys;

    public Partition() {
        boundBuffer = new BoundBuffer<>(MAX_SIZE);
        linkedList = new LinkedList<>();
        keys = new HashSet<>();
    }

    public void deposit(KV data) throws InterruptedException {
        boundBuffer.deposit(data);
    }

    public KV fetch() throws InterruptedException {
        return boundBuffer.fetch();
    }

    public void insertSort(KV kv) {
        linkedList.addFirst(kv);
        keys.add((String) kv.key);
    }

    public HashSet<String> getKeys() {
        return keys;
    }

    public KV getAndRemove(Object key) {
        KV tar = null;
        for (KV kv : linkedList) {
            if (kv.key.equals(key)) {
                tar = kv;
                break;
            }
        }
        if (tar != null) {
            linkedList.remove(tar);
        }

        return tar;
    }
}
