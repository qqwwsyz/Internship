public class MyNode<Block> {
    Block data;
    MyNode<Block> next;
    MyNode<Block> prev;

    public MyNode(Block item, MyNode<Block> next, MyNode<Block> prev) {
        this.data = item;
        this.next = next;
        this.prev = prev;
    }
}
