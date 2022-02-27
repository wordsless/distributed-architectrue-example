package pub.distributedarchitetrue.example.repositories;

import org.springframework.stereotype.Repository;

@Repository
public class Goods {

    private long ID;

    private int num;

    private String label;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
