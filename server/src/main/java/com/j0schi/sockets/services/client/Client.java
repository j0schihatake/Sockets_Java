package com.j0schi.sockets.services.client;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

@Slf4j
public class Client {

    public static void main(String[] args){

        while(true){

            try{
                Client client = new Client();
                System.out.println("Сервер ответил: " + client.send("Новое сообщение от клиента.", "127.0.0.1", 16000));
                Thread.sleep(1000);
            }catch(Exception e){
                System.out.println(e.getMessage());
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
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));){

            // Отправка сообщения:
            output.write(message);
            output.flush();

            // Получение ответа:
            response = input.readLine();

        }catch(Exception e){
            log.error(e.getMessage());
        }
        return response;
    }
}
