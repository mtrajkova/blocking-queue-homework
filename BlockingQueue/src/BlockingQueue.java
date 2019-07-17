import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<E> {

    private final Lock lock = new ReentrantLock();
    private final Condition produceCond = lock.newCondition();
    private final Condition consumeCond = lock.newCondition();
    private LinkedList<E> list = new LinkedList<>();
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public BlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    public boolean add(E e) {
        lock.lock();
        try {
            if (list.size() == capacity) {
                throw new IllegalStateException();
            } else {
                list.add(e);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(Object o) {
        lock.lock();
        try {
            for (Object object : list) {
                if (o.equals(object)) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean offer(E e) {
        lock.lock();
        try {
            if (list.size() == capacity) {
                return false;
            } else {
                list.add(e);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (capacity == list.size()) {
                if (nanos <= 0L) {
                    return false;
                }
                try {
                    nanos = produceCond.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    produceCond.signal();
                    throw ie;
                }
            }
            list.add(e);
            consumeCond.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void put(E e) throws InterruptedException {
        lock.lock();
        try {
            try {
                while (list.size() == capacity) {
                    produceCond.await();
                }
            } catch (InterruptedException ie) {
                produceCond.signal();
                throw ie;
            }
            list.add(e);
            consumeCond.signal();
        } finally {
            lock.unlock();
        }
    }

    public E take() throws InterruptedException {
        lock.lock();
        try {
            try {
                while (list.size() == 0) {
                    consumeCond.await();
                }
            } catch (InterruptedException ie) {
                consumeCond.signal();
                throw ie;
            }
            E e = list.remove(0);
            produceCond.signal();
            return e;
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(Object o) {
        lock.lock();
        try {
            return list.removeFirstOccurrence(o);
        } finally {
            lock.unlock();
        }
    }

    public int remainingCapacity() {
        lock.lock();
        try {
            return capacity - list.size();
        } finally {
            lock.unlock();
        }
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (list.size() == 0) {
                if (nanos <= 0L) {
                    return null;
                }
                try {
                    nanos = consumeCond.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    consumeCond.signal();
                    throw ie;
                }
            }
            E e = list.remove(0);
            produceCond.signal();
            return e;
        } finally {
            lock.unlock();
        }
    }

    public LinkedList<E> getList(){
        return this.list;
    }

}