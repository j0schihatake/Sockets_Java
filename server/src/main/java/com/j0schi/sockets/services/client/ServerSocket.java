package com.j0schi.sockets.services.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
 * Класс реализует работу с клиентом в отдельном потоке.
 */
@Slf4j
@RequiredArgsConstructor
public class ServerSocket implements Runnable {

    private Socket socket;
    public String message;

    public ServerSocket(Socket clientSocket, String message) {
        this.socket = clientSocket;
        this.message = message;
    }

    @Override
    public void run() {
        try(BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            log.info("Получено сообщение от клиента: " + readMessage(input));
            sendMessage(message, output);
        }catch(Exception e){
            log.error(e.getMessage());
        }
    }

    /**
     * Чтение сообщения:
     * @return
     */
    public String readMessage(BufferedReader input){
        try {
            if(input.ready()) {
                return input.readLine();
            }
        }catch(Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * Отправка сообщения:
     * @param message
     */
    public void sendMessage(String message, BufferedWriter output){
        try{
            // Отправка сообщения:
            output.write(message);
            //output.flush();
        }catch(Exception e){
            log.error(e.getMessage());
        }
    }
}