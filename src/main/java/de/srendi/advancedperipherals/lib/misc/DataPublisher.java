package de.srendi.advancedperipherals.lib.misc;

import com.google.common.collect.EvictingQueue;
import de.srendi.advancedperipherals.common.util.Pair;

import java.util.function.Consumer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataPublisher<T> {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final EvictingQueue<Pair<Long, T>> buffer;
    private long lastID = 0;

    public DataPublisher(int size) {
        this.buffer = EvictingQueue.create(size);
    }

    public long getLastID() {
        this.lock.readLock().lock();
        try {
            return this.lastID;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void add(T data) {
        this.lock.writeLock().lock();
        try {
            this.buffer.add(Pair.of(++this.lastID, data));
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public long traverse(long lastConsumedID, Consumer<? super T> consumer) {
        this.lock.readLock().lock();
        try {
            for (Pair<Long, T> data : this.buffer) {
                long id = data.left();
                if (id <= lastConsumedID) {
                    continue;
                }
                consumer.accept(data.right());
                lastConsumedID = id;
            }
        } finally {
            this.lock.readLock().unlock();
        }
        return lastConsumedID;
    }
}
