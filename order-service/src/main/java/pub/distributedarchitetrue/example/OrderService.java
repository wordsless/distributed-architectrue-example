package pub.distributedarchitetrue.example;

import pub.distributedarchitecture.example.transcation.DistributedTransaction;
import pub.distributedarchitetrue.example.repositories.Goods;
import pub.distributedarchitetrue.example.repositories.Order;
import pub.distributedarchitetrue.example.repositories.Signature;
import pub.distributedarchitetrue.example.services.IStorageService;

import java.util.*;

public class OrderService implements IOrderService {

    private IStorageService storage;

    @Override
    public Boolean createOrder(Order order) {
        //注册资源

        Random random = new Random();
        DistributedTransaction tx = new DistributedTransaction();
        Collection<Goods> goods = order.getItems();
        Map<Long, String> tokens = new TreeMap<>();
        random.setSeed(System.currentTimeMillis());
        tokens.put(order.getID(), order.getID()+":"+random.nextLong());
        long c = 1L;
        for(Goods g : goods) {
            random.setSeed(System.currentTimeMillis()+c);
            tokens.put(g.getID(), g.getID()+":"+random.nextLong());
            c++;
        }

        try {
            tx.begin(tokens);//加锁
            for(Goods g : goods) {
                storage.update(g.getID(), -1, tx);
            }
            tx.commit(tokens);//解锁
        } catch(Exception ex) {
            tx.cancel();//回滚
        }

        return null;
    }

    @Override
    public Boolean updateOrder(Order order) {
        return null;
    }

    @Override
    public Integer countOrder() {
        return null;
    }
}
