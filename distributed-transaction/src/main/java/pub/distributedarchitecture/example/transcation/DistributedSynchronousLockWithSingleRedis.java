package pub.distributedarchitecture.example.transcation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class DistributedSynchronousLockWithSingleRedis implements IDistributedSynchronousLock {

    public static final Logger logger = LoggerFactory.getLogger(DistributedSynchronousLockWithSingleRedis.class);

    public static final String SYNCHRONOUS_LOCK = "SYNCHRONOUS-LOCK";

    public static final long TIMEOUT = 1000L;

    @Autowired
    private StringRedisTemplate lockingRedisTemplate;

    private String token;

    private Boolean didGetLock = new Boolean(true), hasGotLock = new Boolean(false);

    private Timer watchdog;

    private final Object localLocking = new Object();

    public final MessageListenerAdapter expireListener = new MessageListenerAdapter() {

        @Override
        public void onMessage(Message message, byte[] pattern) {
            logger.info("EXPIRE: "+message.toString());
            if(message.toString().equals(token)) {
                synchronized (localLocking) {
                    hasGotLock = false;
                    didGetLock = true;
                }
            }
        }
    };

    public final MessageListenerAdapter deleteListener = new MessageListenerAdapter() {

        @Override
        public void onMessage(Message message, byte[] pattern) {
            logger.info("DELETE: "+message.toString());
            if(message.toString().equals(token)) {
                synchronized (localLocking) {
                    hasGotLock = false;
                    didGetLock = true;
                }
            }
        }
    };

    public DistributedSynchronousLockWithSingleRedis(final String signature) {
        this.watchdog = new Timer();
        this.token = SYNCHRONOUS_LOCK +":"+signature;
    }

    public void lock() throws InterruptedException {
        ValueOperations<String, String> ops = lockingRedisTemplate.opsForValue();
        synchronized (localLocking) {
            while(!hasGotLock) {
                //是否可以抢锁
                if(didGetLock) {
                    //没有抢到
                    if(!ops.setIfAbsent(token, "true", TIMEOUT, TimeUnit.MILLISECONDS)) {
                        didGetLock = false;
                        Thread.sleep(1L);
                    } else {
                        //抢到了，跳出自旋
                        hasGotLock = true;
                        //添加watchdog，防止锁在工作期间被释放
                        watchdog.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                if(hasGotLock)
                                    lockingRedisTemplate.expire(token, Duration.ofMillis(TIMEOUT));
                            }
                        }, TIMEOUT/2);
                    }
                } else {
                    //不可以抢锁
                    Thread.sleep(1L);
                }
            }
        }
    }

    public void unlock() {
        lockingRedisTemplate.delete(token);
    }
}
