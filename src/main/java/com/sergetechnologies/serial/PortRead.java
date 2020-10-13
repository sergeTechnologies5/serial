package com.sergetechnologies.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class PortRead {
    private SerialPort userPort;

    @Autowired
    RestTemplate restTemplate;

    public void initialize() {
        SerialPort ports[] = SerialPort.getCommPorts();
        int i = 1;
        //User port selection
        log.info("{}", "COM Ports available on machine");
        for (SerialPort port : ports) {
            //iterator to pass through port array
            log.info("{}", i++ + ": " + port.getSystemPortName()); //print windows com ports
        }
        userPort = SerialPort.getCommPort("ttyUSB0");
        userPort.setBaudRate(4800);
        //Initializing port
        userPort.openPort();
        if (userPort.isOpen()) {
            log.info("{}", "Port initialized!");
            //timeout not needed for event based reading
            //userPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        } else {
            log.info("{}", "Port not available");
            return;
        }

        userPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                byte[] byteArray = event.getReceivedData();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < byteArray.length; ++i)
                    stringBuilder.append((char) byteArray[i]);
                String data = stringBuilder.toString();
                restTemplate.postForEntity("http://localhost:9090/data", data, String.class);

            }
        });
    }

    public void write(String data) {
        if (userPort != null) {
            userPort.writeBytes(data.getBytes(), data.length());
            log.info("SENT TO ARDUINO {} ", data);
        }
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}