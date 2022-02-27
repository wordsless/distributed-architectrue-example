package pub.distributedarchitecture.example.transcation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DSLockWithTokenManagement {

    private Map<String, IDistributedSynchronousLock> locks;

    private DSLockWithTokenManagement management = null;

    public DSLockWithTokenManagement() {
        locks = new ConcurrentHashMap<String, IDistributedSynchronousLock>();
    }

    public IDistributedSynchronousLock get(final String key) {
        if(locks.containsKey(key))
            return locks.get(key);
        else {
            IDistributedSynchronousLock dslock = new DistributedSynchronousLockWithSingleRedis(key);
            locks.put(key, dslock);
            return dslock;
        }
    }

    public void add(final String key) {
        if(locks.containsKey(key))
            return;
        else
            locks.put(key, new DistributedSynchronousLockWithSingleRedis(key));
    }
}
