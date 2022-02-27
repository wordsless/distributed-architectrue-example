package pub.distributedarchitetrue.example.repositories;

import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class Order {

    private long ID;

    private Collection<Goods> items;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public Collection<Goods> getItems() {
        return items;
    }

    public void setItems(Collection<Goods> items) {
        this.items = items;
    }
}
