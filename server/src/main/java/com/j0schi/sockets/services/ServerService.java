package com.j0schi.sockets.services;

import com.j0schi.sockets.services.server.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerService {

    private final Server socketServer;

    @EventListener(ApplicationReadyEvent.class)
    public void work(){

        socketServer.startServer();

        while(true){
            try {
                socketServer.message = getRandomMessage();
                Thread.sleep(1000);
            }catch(Exception ex){
                log.error(ex.getMessage());
            }
        }
    }

    /**
     * Метод генерирует случайное сообщение:
     * @return
     */
    public String getRandomMessage(){
        String result = "";

        int random = getRandomNumberUsingInts(0, 7);

        switch(random){
            case 0:
            case 1:
                result = "<?xml version=\"1.0\" encoding=\"utf-16\" standalone=\"no\"?>" +
                        "<PROTOCOL>" +
                        " <ANSWER Command=\"NULL\" Value=\"NO\" Message=\"Unknown command\" />" +
                        "</PROTOCOL>";
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                result = "<?xml version=\"1.0\" encoding=\"utf-16\" standalone=\"no\"?>" +
                        "<PROTOCOL>" +
                        "<EXCEPTION Message=\"xxxxxxxx\"/>" +
                        "</PROTOCOL>";
                break;
        }

        return result;
    }

    public int getRandomNumberUsingInts(int min, int max) {
        Random random = new Random();
        return random.ints(min, max)
                .findFirst()
                .getAsInt();
    }

}
