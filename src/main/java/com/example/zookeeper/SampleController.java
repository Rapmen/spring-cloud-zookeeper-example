package com.example.zookeeper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@EnableFeignClients
public class SampleController {

    @Value("${spring.application.name:testZookeeperApp}")
    private String appName;

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private DiscoveryClient discovery;

    @Autowired
    private Environment env;

    @Autowired
    private AppClient appClient;

    @Autowired(required = false)
    private Registration registration;

    @Autowired
    private RestTemplate rest;

    @FeignClient("testZookeeperApp")
    interface AppClient {
        @RequestMapping(path = "/hi", method = RequestMethod.GET)
        String hi();

    }

    @RequestMapping("/")
    public ServiceInstance lb() {
        return this.loadBalancer.choose(this.appName);
    }

    @RequestMapping("/hi")
    public String hi() {
        return "Hello World! from " + this.registration;
    }

    @RequestMapping("/self")
    public String self() {
        return this.appClient.hi();
    }

    @RequestMapping("/myenv")
    public String env(@RequestParam("prop") String prop) {
        return this.env.getProperty(prop, "Not Found");
    }

    public String rt() {
        return this.rest.getForObject("http://" + this.appName + "/hi", String.class);
    }
}
