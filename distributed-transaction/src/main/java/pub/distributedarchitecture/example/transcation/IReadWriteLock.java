package pub.distributedarchitecture.example.transcation;

import org.springframework.data.redis.core.ValueOperations;

public interface IReadWriteLock {

    void readLock();

    void readUnlock();

    void writeLock();

    void writeUnlock();

}
