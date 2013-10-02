package common;

import java.util.LinkedList;
import java.util.Queue;

public class ThreadSafeProducerConsumerQueue {
    private int           size;
    private int           capacity;
    private Queue<Object> messageQueue;

    public ThreadSafeProducerConsumerQueue(int capacity) {
        this.size = 0;
        this.capacity = capacity;
        this.messageQueue = new LinkedList<>();
    }

    // Producer function
    public synchronized void pushMessage(Object message) {

        // full
        if (size >= capacity) {
            try {
                System.out.println("Message queue is full, discard");
                wait();
            } catch (Exception e) {
            }
        }

        // notified or not full
        this.messageQueue.add(message);

        // increase size
        size += 1;

        // new, notify all
        notifyAll();
    }

    // Consumer function
    public synchronized Object popMessage() {

        // empty
        if (size <= 0) {
            try {
                System.out.println("Message queue is empty, wait");
                wait();
            } catch (Exception e) {
            }
        }

        // remove
        Object message = this.messageQueue.remove();

        // decrease size of object
        size -= 1;

        // notify produces
        notifyAll();

        return message;
    }

    @Override
    public String toString() {
        return "ThreadSafeProducerConsumerQueue [size=" + size + ", capacity=" + capacity + ", messageQueue=" + messageQueue + "]";
    }
}
