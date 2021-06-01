public class HashTable {
    private final Integer[] keys;
    private final MyPageTable.PageTableEntry[] values;
    private int size;
    private int len;

    public HashTable(int len) {
        keys =  new Integer[len];
        values = new MyPageTable.PageTableEntry[len];
        size = 0;
        this.len = len;
    }

    private int hash(Integer key) {
        return key.hashCode() % len;
    }

    public int getIndex(Integer k) {

        int index = hash(k);
        for (int i = 0; i < len; i++) {
            int cur = (index + i) % len;
            if (keys[cur]!=null&&keys[cur].equals(k)) {
                return cur;
            }
        }
        return -1;
    }

    public synchronized MyPageTable.PageTableEntry get(Integer key) {
        int index = getIndex(key);
        if (index < 0) {
            return null;
        }
        return values[index];
    }

    public synchronized MyPageTable.PageTableEntry put(Integer key, MyPageTable.PageTableEntry value) {
        int hash = hash(key);
        int index = getIndex(key);
        if (index < 0) {
            for (int i = 0; i < len; i++) {
                int cur = (hash + i) % len;
                if (keys[cur] == null) {

                    keys[cur] = key;
                    values[cur] = value;
                    size++;
                    return value;
                }
            }
            throw new OutOfMemoryError("hashMap is full!!");
        } else {
            values[index] = value;
            return value;
        }
    }

    public synchronized boolean remove(Integer key) {
        int index = getIndex(key);
        if (index < 0) {
            return false;
        } else {
            keys[index] = null;
            values[index] = null;
            size--;
            return true;
        }
    }
}
