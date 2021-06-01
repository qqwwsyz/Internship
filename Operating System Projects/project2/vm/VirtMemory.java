import Storage.PhyMemory;

public class VirtMemory extends Memory {
    public static final int FRAME_SIZE = 64;
    public static final int MAX_WRITE = 32;
    private int memsize;
    private int countWriteBack;
    private MyPageTable myPageTable;

    public VirtMemory(int memsize, PhyMemory ram) {
        super(memsize, ram);
        this.memsize = memsize;
        myPageTable = new MyPageTable();

    }

    public VirtMemory() {
        this(64 * 1024, new PhyMemory());
    }

    private MyPageTable.PageTableEntry getEntry(int addr) throws InvalidAddressException {
        if (addr > this.memsize) {
            throw new InvalidAddressException();
        }
        int index = addr / FRAME_SIZE;
        int offset = addr % FRAME_SIZE;
        MyPageTable.PageTableEntry entry;
        try {
            entry = myPageTable.get(index);
            if (entry == null) {
                throw new PageFaultException();
            }
        } catch (PageFaultException e) {
            write_back();
            entry = myPageTable.put(index);
            getPhyMemory().load(entry.pfn, entry.vpn);
        }
        return entry;
    }

    @Override
    public void write(int addr, byte value) {
        try {
            int offset = addr % FRAME_SIZE;
            MyPageTable.PageTableEntry entry = getEntry(addr);
            getPhyMemory().write(entry.vpn + offset, value);
            entry.dirty = true;
            countWriteBack++;
            if (countWriteBack % MAX_WRITE == 0) {
                write_back();
                countWriteBack = 0;

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public byte read(int addr) {
        try {
            int offset = addr % FRAME_SIZE;
            MyPageTable.PageTableEntry entry = getEntry(addr);
            return getPhyMemory().read(entry.vpn + offset);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return -1;
        }
    }

    @Override
    void write_back() {
        for (MyPageTable.PageTableEntry en : myPageTable.getEntries()) {
            if (en.dirty) {
                getPhyMemory().store(en.pfn, en.vpn);
                en.dirty = false;
            }
        }
    }
}
