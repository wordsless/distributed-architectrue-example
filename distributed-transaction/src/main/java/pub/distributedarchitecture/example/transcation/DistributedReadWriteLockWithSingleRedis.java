package pub.distributedarchitecture.example.transcation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

public final class DistributedReadWriteLockWithSingleRedis extends MessageListenerAdapter implements IReadWriteLock{

    private IDistributedSynchronousLock dsLock;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DSLockWithTokenManagement management;

    private int readingReader = 0;

    private int writingWriter = 0;

    private int waitingWriter = 0;

    private boolean preferWriter = true;

    public DistributedReadWriteLockWithSingleRedis(final String race) {
        this.dsLock = management.get(race);
    }

    private void updateLocalVariables(final ValueOperations<String, String> operator) {
        String[] fields = operator.get("ReadWriteFields").split(":");
        readingReader = Integer.parseInt(fields[0]);
        writingWriter = Integer.parseInt(fields[1]);
        waitingWriter = Integer.parseInt(fields[2]);
        preferWriter  = Boolean.parseBoolean(fields[3]);
    }

    private void modifiedRemoteVariables(final ValueOperations<String, String> operator) {
        StringBuilder sb = new StringBuilder();
        sb.append(readingReader);
        sb.append(':');
        sb.append(writingWriter);
        sb.append(':');
        sb.append(waitingWriter);
        sb.append(':');
        sb.append(preferWriter);
        operator.set("ReadWriteFields", sb.toString());
    }

    private boolean tryReadLock(final ValueOperations<String, String> operator) throws InterruptedException {
        dsLock.lock();
        updateLocalVariables(operator);
        if(writingWriter > 0 || (preferWriter && waitingWriter > 0)) {
            dsLock.unlock();
            return false;
        }
        return true;
    }

    @Override
    public void readLock() {
        ValueOperations operator = stringRedisTemplate.opsForValue();
        try {
            while(!tryReadLock(operator)) {
                wait();
            }
            readingReader++;
            modifiedRemoteVariables(operator);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dsLock.unlock();
        }
    }

    @Override
    public void readUnlock() {
        ValueOperations operator = stringRedisTemplate.opsForValue();
        try {
            dsLock.lock();
            readingReader--;
            preferWriter = true;
            modifiedRemoteVariables(operator);
            notifyAll();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            dsLock.unlock();
        }
    }

    private boolean tryWriteLock(final ValueOperations<String, String> operator) throws InterruptedException {
        dsLock.lock();
        if(writingWriter > 0 || readingReader > 0) {
            dsLock.unlock();
            return false;
        }
        return true;
    }

    private boolean waitingWriterIncreaseAndTryGetWriteLock(final ValueOperations<String, String> operator) {
        try {
            dsLock.lock();
            updateLocalVariables(operator);
            waitingWriter++;
            if(writingWriter <= 0 && readingReader <= 0) {
                waitingWriter--;
                writingWriter++;
                return true;
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            modifiedRemoteVariables(operator);
            dsLock.unlock();
        }
        return false;
    }

    @Override
    public void writeLock() {
        ValueOperations operator = stringRedisTemplate.opsForValue();
        if(waitingWriterIncreaseAndTryGetWriteLock(operator))
            return;
        else {
            try {
                wait();
                while(!tryWriteLock(operator)) {
                    wait();
                    updateLocalVariables(operator);
                }
                waitingWriter--;
                writingWriter++;
                modifiedRemoteVariables(stringRedisTemplate.opsForValue());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                dsLock.unlock();
            }
        }
    }

    @Override
    public void writeUnlock() {
        try {
            dsLock.lock();
            writingWriter--;
            preferWriter = false;
            modifiedRemoteVariables(stringRedisTemplate.opsForValue());
            notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            dsLock.unlock();
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        notifyAll();
    }
}
