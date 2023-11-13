package com.j0schi.sockets.services.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class ClientServer implements Runnable {

    private final Socket clientSocket;
    public String message;

    public ClientServer(Socket clientSocket, String message) {
        this.clientSocket = clientSocket;
        this.message = message;
    }

    @Override
    public void run() {

        //  log.info("Подключился новый клиент!");

        try {
            try(BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
            ) {
                if (message != null && !message.equals("")) {

                    try {
                        if(input.ready()) {
                            String word = input.readLine();             // ждём пока клиент что-нибудь нам напишет
                            log.info("Клиент сообщил:" + word);
                        }

                        sendToClient(message, output);
                        message = null;
                    } catch (Exception e) {
                        log.error("Не удалось отправить сообщение: " + e.getMessage() + ": " + message);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Exception e: " + e.getMessage());
        }
    }

    public void sendToClient(String json, BufferedWriter out){
        if(clientSocket != null && out!= null && !clientSocket.isClosed()) {
            try {
                out.write(json);
                out.flush();
            } catch (IOException e) {
                log.error("SendToClient: e: " + e.getMessage());
            }
        }
    }
}