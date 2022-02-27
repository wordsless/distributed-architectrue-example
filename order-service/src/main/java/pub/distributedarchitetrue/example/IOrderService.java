package pub.distributedarchitetrue.example;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.distributedarchitetrue.example.repositories.Order;

@Service
@FeignClient(name="")
public interface IOrderService {

    @Transactional
    Boolean createOrder(final Order order);

    @Transactional
    Boolean updateOrder(final Order order);

    Integer countOrder();
}
