public class MyMemoryAllocation extends MemoryAllocation {

    //best fit, first fit or next fit
    String algorithm;
    MyLinkedList free_list;
    MyLinkedList used_list;

    int lastOffset = 0;
    int initSize = 0;

    public MyMemoryAllocation(int mem_size, String algorithm) {
        super(mem_size, algorithm);
        this.algorithm = algorithm;
        this.free_list = new MyLinkedList();
        this.used_list = new MyLinkedList();
        this.initSize = mem_size - 1;

        free_list.add(new Block(1, mem_size - 1));
    }

    @Override
    public int alloc(int size) {
        Block block = null;
        Block blockTemp = null;
        int removeIndex = -1;
        int k = 0;

        for (int i = 0; i < free_list.getSize(); i++) {
            block = free_list.get(i);
            if (size > block.size) {
                continue;
            }
            if (blockTemp == null) {
                blockTemp = block;
            }
            if ("FF".equals(this.algorithm)) {
                removeIndex = i;
                blockTemp = block;
                break;
            } else if ("BF".equals(this.algorithm)) {
                if (blockTemp.size < block.size) {
                    continue;
                } else if (blockTemp.size == block.size) {
                    if (removeIndex == -1) {
                        removeIndex = i;
                    }
                    continue;
                } else {
                    blockTemp = block;
                    removeIndex = i;
                    continue;
                }
            }

        }

        if ("NF".equals(this.algorithm)) {
            if (this.lastOffset == 0){
                k = -1;
            } else if (k == 0) {
                for (int j = 0; j < free_list.getSize(); j++) {
                    if (this.lastOffset == free_list.get(j).offset) {
                        k = j;
                        break;
                    }
                }
            }

            boolean isFromStart = true;
            for (int i = k + 1; i < free_list.getSize(); i++) {
                blockTemp = free_list.get(i);
                if (blockTemp.size < size){
                    continue;
                }
                isFromStart = false;
                removeIndex = i;
                break;
            }

            boolean isAllocation = false;
            if (isFromStart){
                for (int i = 0; i <= k; i++) {
                    blockTemp = free_list.get(i);
                    if (blockTemp.size < size){
                        continue;
                    }
                    isAllocation = true;
                    removeIndex = i;
                    break;
                }
            }

            if (!isAllocation && isFromStart){
                return 0;
            }
        }


        if (removeIndex == -1) {
            return 0;
        }

        block = blockTemp;

        int newBlockSize = block.size - size;
        int newBlockOffset = block.offset + size;
        Block block1 = new Block();
        block1.offset = block.offset;
        block1.size = size;
        used_list.add(block1);
        free_list.remove(removeIndex);

        if (newBlockSize > 0) {
            free_list.add(removeIndex, new Block(newBlockOffset, newBlockSize));
        } else if (free_list.getSize() > 0 && free_list.getNode(k) != null) {
            newBlockOffset = free_list.get(k).offset;
        }
        if (newBlockOffset > this.initSize){
            if (free_list.getSize() > 0){
                newBlockOffset = free_list.getHead().data.offset;
            } else if (free_list.getSize() == 0){
                newBlockOffset = 0;
            }
        }
        this.lastOffset = newBlockOffset;

        return block.offset;
    }

    @Override
    public void free(int addr) {
        Block block = null;
        int freeIndex = -1;
        for (int i = 0; i < used_list.getSize(); i++) {
            block = used_list.get(i);
            if (block.offset == addr){
                freeIndex = i;
                break;
            }
        }
        if (freeIndex == -1){
            System.err.println("can not find this offset:" + addr);
            return;
        }

//        int preIndex = getPreBlockIndex(block.offset);
//        int nextIndex = getNextBlockIndex(block.offset, block.size);



        used_list.remove(freeIndex);
        free_list.insertMayCompact(block);
//        this.lastOffset = free_list.insertMayCompact(block);

//
//        if (preIndex == -1 && nextIndex == -1){
//            free_list.add(block);
//            this.lastOffset = block.offset;
//        } else if (preIndex > -1 && nextIndex > -1){
//            Block preBlock = free_list.get(preIndex);
//            Block nextBlock = free_list.get(nextIndex);
//            preBlock.size = preBlock.size + block.size + nextBlock.size;
//            free_list.remove(nextIndex);
////            used_list.remove(freeIndex);
//            this.lastOffset = preBlock.offset;
//        } else if (preIndex > -1){
//            Block preBlock = free_list.get(preIndex);
//            preBlock.size = preBlock.size + block.size;
////            used_list.remove(freeIndex);
//            this.lastOffset = preBlock.offset;
//        } else if (nextIndex > -1){
//            Block nextBlock = free_list.get(nextIndex);
//            nextBlock.size = nextBlock.size + block.size;
//            nextBlock.offset = block.offset;
////            used_list.remove(freeIndex);
//            this.lastOffset = nextBlock.offset;
//        }
    }

//    private int getNextBlockIndex(int freeIndex, int size) {
//        int index = -1;
//        if (free_list.getSize() == 0){
//            return index;
//        }
//        for (int i = 0; i < free_list.getSize(); i++) {
//            if (free_list.get(i).offset == (freeIndex + size)) {
//                index = i;
//            }
//        }
//        return index;
//    }
//
//    private int getPreBlockIndex(int addr) {
//        int index = -1;
//        if (free_list.getSize() == 0){
//            return index;
//        }
//        for (int i = 0; i < free_list.getSize(); i++) {
//            if ((free_list.get(i).offset + free_list.get(i).size) == addr) {
//                index = i;
//            }
//        }
//        return index;
//    }

    @Override
    public int size() {
        int totalSize = 0;
        for (int i = 0; i < free_list.getSize(); i++) {
            totalSize += free_list.get(i).size;
        }
        return totalSize;
    }

    @Override
    public int max_size() {
        int maxSize = 0;
        for (int i = 0; i < free_list.getSize(); i++) {
            if (free_list.get(i).size > maxSize){
                maxSize = free_list.get(i).size;
            }
        }
        return maxSize;
    }

    @Override
    public void print() {
        String enterSymbol = "\n";
        StringBuilder sb = new StringBuilder();
        sb.append("algorithm : ").append(this.algorithm).append(enterSymbol);
        sb.append("free_list : ").append(enterSymbol);
        for (int i = 0; i < free_list.getSize(); i++) {
            sb.append(free_list.get(i).toString()).append(enterSymbol);
        }
        sb.append("used_list : ").append(enterSymbol);
        for (int i = 0; i < used_list.getSize(); i++) {
            sb.append(used_list.get(i).toString()).append(enterSymbol);
        }
        System.out.println(sb.toString());
    }
}
