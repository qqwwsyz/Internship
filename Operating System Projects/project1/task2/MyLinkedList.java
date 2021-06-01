import java.util.Iterator;

public class MyLinkedList implements Iterator { //generic types are not required, you can just do MyLinkedList for blocks but Iterable is going to make your life easier.
    //in addition to other regular list member functions such as insert and delete: (split and consolidate blocks must be implemented at the level of linked list)

    private int size;
    private MyNode<Block> head;
    private MyNode<Block> tail;

    public boolean add(Block block) {
        addAtLast(block);
        return true;
    }

    private void addAtLast(Block block) {
        MyNode<Block> temp = tail;

        MyNode<Block> node = new MyNode<>(block, null, temp);
        tail = node;
        if (temp == null) {
            head = node;
        } else {
            temp.next = node;
        }
        size++;
    }

    public void add(int index, Block block) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException("out of size");
        }
        if (index == size) {
            addAtLast(block);
        } else {
            MyNode<Block> temp = getNode(index);
            addBeforeNode(block, temp);
        }
    }

    private void addBeforeNode(Block block, MyNode<Block> temp) {
        MyNode<Block> preNode = temp.prev;
        MyNode<Block> newNode = new MyNode<>(block, temp, preNode);
        if (preNode == null) {
            head = newNode;
        } else {
            preNode.next = newNode;
        }
        temp.prev = newNode;
        size++;
    }

    public MyNode<Block> getNode(int index) {
        MyNode temp = null;
        for (int i = 0; i <= index; i++) {
            if (i == 0){
                temp = this.head;
            } else if (i == index){
                temp = temp.next;
            } else {
                temp = temp.next;
            }
        }
        return temp;
    }

    public Block get(int index) {
        return getNode(index).data;
    }

    public Block remove(int index) {
        MyNode<Block> temp = getNode(index);
        Block block = temp.data;
        MyNode<Block> prevNode = temp.prev;
        MyNode<Block> nextNode = temp.next;

        if (prevNode == null) {
            head = nextNode;
        }else{
            prevNode.next = nextNode;
            temp.next = null;
        }

        if (nextNode == null) {
            tail = prevNode;
        }else{
            nextNode.prev = prevNode;
            temp.prev = null;
        }
        size--;
        temp.data = null;
        return block;
    }

    public void splitMayDelete() {

    }

    public int insertMayCompact(Block block) {
        int preIndex = getPreBlockIndex(block.offset);
        int nextIndex = getNextBlockIndex(block.offset, block.size);

        int lastOffset = 0;

        if (preIndex == -1 && nextIndex == -1){
            this.add(block);
            lastOffset = block.offset;
        } else if (preIndex > -1 && nextIndex > -1){
            Block preBlock = this.get(preIndex);
            Block nextBlock = this.get(nextIndex);
            preBlock.size = preBlock.size + block.size + nextBlock.size;
            this.remove(nextIndex);
            lastOffset = preBlock.offset;
        } else if (preIndex > -1){
            Block preBlock = this.get(preIndex);
            preBlock.size = preBlock.size + block.size;
            lastOffset = preBlock.offset;
        } else if (nextIndex > -1){
            Block nextBlock = this.get(nextIndex);
            nextBlock.size = nextBlock.size + block.size;
            nextBlock.offset = block.offset;
            lastOffset = nextBlock.offset;
        }

        return lastOffset;
    }

    private int getNextBlockIndex(int freeIndex, int size) {
        int index = -1;
        if (this.getSize() == 0){
            return index;
        }
        for (int i = 0; i < this.getSize(); i++) {
            if (this.get(i).offset == (freeIndex + size)) {
                index = i;
            }
        }
        return index;
    }

    private int getPreBlockIndex(int addr) {
        int index = -1;
        if (this.getSize() == 0){
            return index;
        }
        for (int i = 0; i < this.getSize(); i++) {
            if ((this.get(i).offset + this.get(i).size) == addr) {
                index = i;
            }
        }
        return index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public MyNode<Block> getHead() {
        return head;
    }

    public void setHead(MyNode<Block> head) {
        this.head = head;
    }

    public MyNode<Block> getTail() {
        return tail;
    }

    public void setTail(MyNode<Block> tail) {
        this.tail = tail;
    }

    public String toString() {
        return "MyLinkedList{" +
                "size=" + size +
                ", head=" + head +
                ", tail=" + tail +
                '}';
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Object next() {
        return null;
    }
}

