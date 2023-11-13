package com.j0schi.sockets.services.client;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class Client {

    public static Socket socket;

    public static String serverIp = "127.0.0.1";

    public static String oldMessage = "Новое сообщение";

    public static int serverPort = 16000;

    public static void main(String[] args){

        Client client = new Client();

        while(true){

            try{
                String response = client.send(oldMessage, Client.serverIp, Client.serverPort);
                //System.out.println(response);
                if(response != null && !response.equals(""))
                    oldMessage = response;
                log.info(response);

                Thread.sleep(1000);
            }catch(Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * Отправка сообщения на принтер.
     * @param message Unicode
     * @return
     */
    public String send(String message, String hostName, int port){

        String response = "";

        try(Socket socket = new Socket(hostName, port);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))){

            // Отправка сообщения:
            output.write(message);
            //output.flush();

            // Получение ответа:
            if(input.ready()) {
                response = input.readLine();
            }
        }catch(Exception e){
            log.error(e.getMessage());
        }
        log.info(response);
        return response;
    }
}
