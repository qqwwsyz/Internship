import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class MyMapReduce extends MapReduce {
    public static final String FORMAT = ".%02d";
    private MapperReducerClientAPI customMR;
    private int numPartitions;
    private PartitionTable pt;

    //	What is in a running instance of MapReduce?
    public void MREmit(Object key, Object value) {
        int index = (int) customMR.Partitioner(key, numPartitions);
        try {
            pt.partitions[index].deposit(new KV(key, value));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Object MRGetNext(Object key, int partition_number) {

        try {
            Partition p = pt.partitions[partition_number];
            return p.getAndRemove(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void MRRunHelper(String inputFileName,
                               MapperReducerClientAPI mapperReducerObj,
                               int num_mappers,
                               int num_reducers) {
        customMR = mapperReducerObj;
        numPartitions = num_reducers;
        pt = new PartitionTable(numPartitions);
        //fixed: launch mappers, main thread must join all mappers before
        // starting sorters and reducers
        for (int i = 0; i < num_mappers; i++) {
            int finalI = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // create map thread.
                    String flie = inputFileName + String.format(FORMAT, finalI);
                    mapperReducerObj.Map(flie);
                    addFinishedMappers();
                }
            });
            thread.start();
        }
        LOGGER.log(Level.INFO, "All Maps are completed");

        //Fixed: launch sorters and reducers. Each partition is assigned a sorter
        // and a reducer which works like a *pipeline* with mapper. Sorter[i] takes
        // over the kv list in the partition[i] and starts sorting, then mapper[i]
        // can start adding more to partition right away. Reducer[i] waits for
        // sorter to sort all kv pairs
        //Main thread waits for reducers to complete.
        Thread[] reducers = new Thread[num_reducers];
        for (int i = 0; i < num_reducers; i++) {
            int finalI = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Partition p = pt.partitions[finalI];
                    KV k = null;
                    try {
                        while ((k = p.fetch()) != null) {
                            p.insertSort(k);
                        }
                    } catch (InterruptedException e) {
                       System.out.println("all mapper finished");
                    }
                    for (String key : p.getKeys()) {
                        mapperReducerObj.Reduce(key, finalI);
                    }
                    addFinishedReducers();
                }
            });
            reducers[i]=thread;
            thread.start();
        }
        while (getFinishedMappers() != num_mappers) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < num_reducers; i++) {
            reducers[i].interrupt();
        }
        while (getFinishedReducers() != num_reducers) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOGGER.log(Level.INFO, "Execution of all maps and reduces took in seconds: {0}", (stopWatch.getElapsedTime()));
        teardown(inputFileName);
    }

    private int finishedMappers;
    private int finishedReducers;
    private ReentrantLock lock = new ReentrantLock();

    private void addFinishedMappers() {
        lock.lock();
        finishedMappers++;
        lock.unlock();
    }

    private int getFinishedMappers() {
        lock.lock();
        int data = finishedMappers;
        lock.unlock();
        return data;

    }

    private void addFinishedReducers() {
        lock.lock();
        finishedReducers++;
        lock.unlock();
    }

    private int getFinishedReducers() {
        lock.lock();
        int data = finishedReducers;
        lock.unlock();
        return data;
    }
}
