package pub.distributedarchitecture.example.transcation;

public interface IDistributedSynchronousLock {

    void lock() throws InterruptedException;

    void unlock();
}
