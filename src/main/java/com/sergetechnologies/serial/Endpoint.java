package com.sergetechnologies.serial;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping
@Slf4j
public class Endpoint {

    private PortRead portRead;
    @Autowired
    RestTemplate restTemplate;
    Endpoint(PortRead portRead) {
        this.portRead = portRead;
    }

    @RequestMapping("add/{id}")
    public ResponseEntity<String> addFinger(@PathVariable("id") String id) throws InterruptedException {
        portRead.write("#");
        Thread.sleep(100);
        portRead.write(id);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @RequestMapping("data")
    public ResponseEntity<Void> data(@RequestBody String data) {
        if (data.contains("#")) {
            restTemplate.postForEntity("http://192.168.43.188:5000/relay/one", data, String.class);
            System.out.println("Finger Found  activating relay two");
        }
        System.out.print(data);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping("face")
    public ResponseEntity<Void> faceID(@RequestBody String data) {
        User user = new User();
        user.setPassword("8973");
        user.setUsername(data);
        user.setStatus("Search Finger");
        restTemplate.postForEntity("http://192.168.43.188:5000/start", user, User.class);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping("pass")
    public ResponseEntity<Void> password(@ModelAttribute User data) {
        if (data.getStatus().equalsIgnoreCase("Found")){
            restTemplate.postForEntity("http://192.168.43.188:5000/relay/two", data, String.class);
            System.out.println("Pin Correct activating relay two");
        }else {
            System.out.println("Pin Incorrect");
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
