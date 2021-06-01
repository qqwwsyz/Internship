/****************************************************
*Citation
*  Unique-You
* Date 05/03/2018
* 虚拟内存，页表，快表，多级页表，倒排页表
* Code version Java
* Availability: https://blog.csdn.net/qq_22238021/article/details/80176099
* 
****************************************************/
import java.util.LinkedList;

public class MyPageTable {
    private static final int INITIAL_SIZE = 256;
    private final LinkedList<PageTableEntry> entries = new LinkedList<>();
    private final HashTable hashTable = new HashTable(INITIAL_SIZE);

    public PageTableEntry get(int index) {
        return hashTable.get(index);
    }

    public PageTableEntry put(int index) {
        PageTableEntry entry = new PageTableEntry();
        entry.dirty = false;
        if (entries.size() < INITIAL_SIZE) {
            entry.vpn = entries.size() * VirtMemory.FRAME_SIZE;
            entry.pfn = index;
            hashTable.put(index, entry);
            entries.offer(entry);
        } else {
            PageTableEntry last = entries.poll();
            hashTable.remove(last.pfn);
            last.pfn = index;
            last.dirty = false;
            hashTable.put(index, last);
            entries.offer(last);
            entry = last;
        }
        return entry;
    }

    public LinkedList<PageTableEntry> getEntries() {
        return entries;
    }

    public static class PageTableEntry {
        int vpn;
        int pfn;
        boolean dirty;
    }
}
