package com.j0schi.sockets.services.client;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class Client {

    public static Socket socket;

    public static String serverIp = "127.0.0.1";

    public static int serverPort = 16000;

    public static void main(String[] args){

        Client client = new Client();

        while(true){

            try{
            /*
            if(socket == null || socket != null && socket.isClosed()){
                socket = new Socket(serverIp, serverPort);
            }
            */

            String response = client.send("Новое сообщение\n", "127.0.0.1", 16000);
            System.out.println(response);
            log.info(response);

            //client.sendMessage("Новое сообщение.", socket);

            //System.out.println("Сообщение с сервера: " + client.readMessage(socket));

            //Client client = new Client();
            //System.out.println("Сервер ответил: " + client.send("Новое сообщение от клиента.", "127.0.0.1", 16000));
            Thread.sleep(1000);
            }catch(Exception e){
                log.error(e.getMessage());
            }
        }
    }

    /**
     * Создание сокета:
     * @param hostName
     * @param port
     */
    public void init(String hostName, int port){
        try(Socket socket = new Socket(hostName, port);){
            Client.socket = socket;
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    /**
     * Отправка сообщения на принтер.
     * @param message Unicode
     * @return
     */
    public String send(String message, String hostName, int port){

        String response = "";

        Socket socket = null;
        BufferedReader input = null;
        BufferedWriter output = null;

        try{
            socket = new Socket(Client.serverIp, Client.serverPort);

            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            // Отправка сообщения:
            output.write(message);
            output.flush();

            response = readClientMessage(socket);
        }catch(Exception e){
            log.error(e.getMessage());
        }finally {
            try {
                if(output != null)
                    output.close();
                if(input != null)
                    input.close();
            }catch(Exception e){
                log.error(e.getMessage());
            }
        }
        log.info(response);
        return response;
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
    public String readMessage(Socket socket){
        String result = null;
        BufferedReader input = null;
        try{
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if(input.ready()) {
                result = input.readLine();
            }
        }catch(Exception e){
            log.error(e.getMessage());
        }finally {
            try {
                if (input != null) {
                    input.close();
                }
            }catch(Exception e){
                log.error(e.getMessage());
            }
        }
        return result;
    }

    /**
     * Отправка сообщения:
     * @param message
     */
    public void sendMessage(String message, Socket socket){
        BufferedWriter output = null;
        try{
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            output.write(message);
            output.flush();
        }catch(Exception e){
            log.error(e.getMessage());
        }finally {
            try {
                if(output != null)
                    output.close();
            }catch(Exception e){
                log.error(e.getMessage());
            }
        }
    }
}
