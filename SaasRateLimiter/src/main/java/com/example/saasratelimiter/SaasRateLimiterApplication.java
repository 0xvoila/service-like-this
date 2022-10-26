package com.example.saasratelimiter;



import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Duration;
import java.util.HashMap;

@SpringBootApplication
public class SaasRateLimiterApplication {

    HashMap<String, Bucket> bucketList = new HashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(SaasRateLimiterApplication.class, args);
    }


}
