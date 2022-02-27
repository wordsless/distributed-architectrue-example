package pub.distributedarchitetrue.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
public class StorageServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(StorageServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
