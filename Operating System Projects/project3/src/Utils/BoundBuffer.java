package Utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundBuffer <T> {
    //Please note the biggest difference between this BoundBuffer
    //and the one we demoed in class is <T>
    // implement member functions: deposit() and fetch()
    private final T[] buffer;
    private final int capacity;
    private int front;

    private int rear;
    private int count;
    private Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEMpty = lock.newCondition();

    public BoundBuffer (int capacity) {
        super();
        this.capacity = capacity;
        buffer = (T[]) new Object[capacity];
    }
    public void deposit(T data) throws InterruptedException{
        lock.lock();
        while(count == capacity) {
            notFull.await();
        }
        buffer[rear] = data;
        rear = (rear + 1) % capacity;
        count ++;
        System.out.println("thread" + Thread.currentThread().getId() + ", deposes " + data);
        notEMpty.signal();
        lock.unlock();

    }
    public T fetch() throws InterruptedException{
        lock.lock();
        while(count ==0) {
            notEMpty.await();

        }
        T result = buffer[front];
        front=(front + 1) % capacity;
        count--;
        notFull.signal();
        System.out.println("thread" + Thread.currentThread().getId() + ", deposes " + result);
        lock.unlock();
        return result;
    }
}