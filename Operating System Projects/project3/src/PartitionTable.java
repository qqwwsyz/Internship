import java.util.Comparator;

public class PartitionTable {
    Partition[] partitions;

    public PartitionTable(int num) {
        this.partitions = new Partition[num];
        for (int i = 0; i < partitions.length; i++) {
            partitions[i] = new Partition();
        }
    }
}
