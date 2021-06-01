public class Block {
    int offset;
    int size;

    public Block(){

    }

    public Block(int offset, int size){
        this.offset = offset;
        this.size = size;
    }

    public boolean is_adjacent(Block other) {
        if (other.offset == (this.offset + this.size) || this.offset - (other.offset + other.size) == 1) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "Block{" +
                "offset=" + offset +
                ", size=" + size +
                '}';
    }
}
