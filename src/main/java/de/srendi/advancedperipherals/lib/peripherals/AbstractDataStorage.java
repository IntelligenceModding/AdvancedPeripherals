package de.srendi.advancedperipherals.lib.peripherals;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractDataStorage {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    protected abstract DataComponentPatch getPatch();

    protected abstract void setPatch(DataComponentPatch patch);

    public final ReadView allocRead() {
        return this.new ReadView();
    }

    public final WriteView allocWrite() {
        return this.new WriteView();
    }

    public abstract class View implements AutoCloseable {
        private volatile boolean unlocked = false;

        protected View() {
            this.lock();
        }

        public DataComponentPatch getPatch() {
            this.checkLock();
            return AbstractDataStorage.this.getPatch();
        }

        public PatchedDataComponentMap getPatched() {
            return this.getPatched(DataComponentMap.EMPTY);
        }

        public PatchedDataComponentMap getPatched(DataComponentMap defaults) {
            return PatchedDataComponentMap.fromPatch(defaults, this.getPatch());
        }

        protected abstract void lock();

        public final void unlock() {
            if (this.unlocked) {
                return;
            }
            this.unlock0();
            this.unlocked = true;
        }

        protected abstract void unlock0();

        protected void checkLock() throws IllegalStateException {
            if (this.unlocked) {
                throw new IllegalStateException("Should not access after lock is released");
            }
        }

        @Override
        public void close() {
            this.unlock();
        }
    }

    public final class ReadView extends View {
        @Override
        protected void lock() {
            AbstractDataStorage.this.lock.readLock().lock();
        }

        @Override
        protected void unlock0() {
            AbstractDataStorage.this.lock.readLock().unlock();
        }

        public WriteView upgradeToWrite() {
            this.checkLock();
            this.unlock();
            return AbstractDataStorage.this.new WriteView();
        }
    }

    public final class WriteView extends View {
        public void setPatch(DataComponentPatch patch) {
            this.checkLock();
            AbstractDataStorage.this.setPatch(patch);
        }

        @Override
        protected void lock() {
            AbstractDataStorage.this.lock.writeLock().lock();
        }

        @Override
        protected void unlock0() {
            AbstractDataStorage.this.lock.writeLock().unlock();
        }
    }
}
