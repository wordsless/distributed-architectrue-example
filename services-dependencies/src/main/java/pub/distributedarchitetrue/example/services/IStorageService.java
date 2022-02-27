package pub.distributedarchitetrue.example.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.distributedarchitecture.example.transcation.DistributedTransaction;

import java.util.Map;

@FeignClient
@Service
public interface IStorageService {

    @Transactional
    void update(final long id, final int num, final DistributedTransaction tokens) throws Exception;

    @Transactional
    void rollback();
}
