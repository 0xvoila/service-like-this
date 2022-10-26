package com.example.saasratelimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;

@RestController
class RateLimiterController {

    HashMap<String, Bucket> bucketList = new HashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(SaasRateLimiterApplication.class, args);
    }

    @GetMapping("/register/{key}/{count}")
    public void register(String key, int count){
        Bandwidth bandwidth = Bandwidth.simple(count, Duration.ofMinutes(1));
        Bucket bucket = Bucket4j.builder().addLimit(bandwidth).build();
        bucketList.put(key, bucket);
    }

    @GetMapping("/register/{key}")
    public Boolean consume(String key){

        return bucketList.get(key).tryConsume(1);
    }


}