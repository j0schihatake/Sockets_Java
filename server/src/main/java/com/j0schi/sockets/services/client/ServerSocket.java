package com.j0schi.sockets.services.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

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
     * Создание сокета:
     * @param hostName
     * @param port
     */
    public void init(String hostName, int port){
        try(Socket socket = new Socket(hostName, port);){
            this.socket = socket;
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    /**
     * Буферизованное чтение.
     * @param clientSocket
     */
    public String readClientMessage(Socket clientSocket){
        StringBuilder result = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){
            for(int chr = reader.read(); reader.ready(); chr = reader.read()){
                result.append((char) chr);
            }
        }catch(Exception e){
            log.error(e.getMessage());
        }
        return result.toString();
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
            output.flush();
        }catch(Exception e){
            log.error(e.getMessage());
        }
    }
}