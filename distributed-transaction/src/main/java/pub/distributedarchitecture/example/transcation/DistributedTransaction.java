package pub.distributedarchitecture.example.transcation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DistributedTransaction {

    private Set<IReadWriteLock> locks = new TreeSet<>();

    public void begin(final Map<Long, String> tokens) {
        Collection<String> _tokens =  tokens.values();
        _tokens.forEach((token)->{
            IReadWriteLock lock = new DistributedReadWriteLockWithSingleRedis(token) ;
            lock.writeLock();
        });
    }

    public void commit(final Map<Long, String> tokens) {
        Collection<String> _tokens =  tokens.values();
        _tokens.forEach((token)->{
            IReadWriteLock lock = new DistributedReadWriteLockWithSingleRedis(token) ;
            lock.writeUnlock();
        });
    }

    public void redo() {

    }

    public void cancel() {

    }
}
