package pub.distributedarchitetrue.example.services;

import pub.distributedarchitecture.example.transcation.DistributedTransaction;

public class StorageService implements IStorageService {

    @Override
    public void update(long id, int num, DistributedTransaction tokens) throws Exception {

    }

    @Override
    public void rollback() {

    }
}
