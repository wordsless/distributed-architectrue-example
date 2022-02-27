package pub.distributedarchitetrue.example.repositories;

import org.springframework.stereotype.Repository;

@Repository
public class Signature<T> {

    private String token;

    private long id;

    private T vector;

    public Signature() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public T getVector() {
        return vector;
    }

    public void setVector(T vector) {
        this.vector = vector;
    }
}
