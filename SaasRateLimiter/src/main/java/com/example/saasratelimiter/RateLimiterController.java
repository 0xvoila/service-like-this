package com.example.saasratelimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public void register(@PathVariable String key, @PathVariable int count){
        Refill refill = Refill.intervally(count, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(count, refill);
        Bucket bucket = Bucket4j.builder().addLimit(limit).build();
        bucketList.put(key, bucket);
    }

    @GetMapping("/consume/{key}")
    public Boolean consume(@PathVariable String key){
        Boolean x = bucketList.get(key).tryConsume(1);
        System.out.println(x);
        return x;
    }


}