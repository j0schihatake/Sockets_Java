package com.j0schi.sockets.services.server;

import com.j0schi.sockets.services.client.ServerSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class Server {

    public java.net.ServerSocket server;
    public Socket client;
    public String message;
    private InputStream inputStream;
    private OutputStream outputStream;

    public Server startServer() {

        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    //  InetAddress addr = InetAddress.getByName("127.0.0.1");
                    //  ServerSocket serverSocket = new ServerSocket(16000, 50, addr);
                    //  ws:localhost:16000
                    try(java.net.ServerSocket server = new java.net.ServerSocket(16000)) {
                        log.info("Waiting for clients to connect...");
                        while (true) {

                            client = server.accept();
                            log.info("Подключился клиент.");

                            /**
                             * Сервер получает и выводит сообщение от клиента,
                             * и отправляет ответ.
                             */
                            clientProcessingPool.submit(new ServerSocket(client, message));
                        }
                    }
                } catch (IOException e) {
                    log.error("Unable to process client request" + e.getMessage());
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
        return this;
    }

    /**
     * Реализация для связки с Unity3D:
     */
    private class ClientTask implements Runnable {
        private final Socket clientSocket;
        private BufferedReader in;
        private BufferedWriter out;

        private ClientTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {

            System.out.println("Got a client !");

            try {

                inputStream = client.getInputStream();
                outputStream = client.getOutputStream();

                //outputStream = new BufferedOutputStream(client.getOutputStream(), "UTF8");

                // in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                while(true){

                    if(message != null && !message.equals("")){
                        //String word = in.readLine(); // ждём пока клиент что-нибудь нам напишет
                        //System.out.println(word);

                        // не долго думая отвечает клиенту
                        try {
                            sendToClient(message);
                            message = null;
                            try {
                                Thread.sleep(1000);
                            }catch(Exception ex){}
                        }catch(Exception e){
                            log.error("Не удалось отправить сообщение: " + e.getMessage() + ": " +  message);
                        }
                        //out.flush(); // выталкиваем все из буфера
                    }
                }
            } catch (IOException e) {
                log.error("Exception e: " + e.getMessage());
            }finally {
                try {
                    in.close();
                    out.close();
                    if(clientSocket != null)
                        clientSocket.close();
                } catch (IOException e) {
                    log.error("Exception clientSocket e: " + e.getMessage());
                }
            }
        }

        public void sendToClient(String json){
            //log.info("sendToClient");
            if(client != null && outputStream != null) {
                try {
                    byte[] bytes = json.getBytes();
                    byte[] bytesSize = intToByteArray(json.length());
                    outputStream.write(bytesSize, 0, 4);
                    outputStream.write(bytes, 0, bytes.length);
                    outputStream.flush();
                } catch (IOException e) {
                    try {
                        Thread.sleep(1000);
                    }catch(Exception ex){}
                    log.error("SendToClient: e: " + e.getMessage());
                }
            }
        }
    }

    public static byte[] intToByteArray(int a)
    {
        byte[] ret = new byte[4];
        ret[0] = (byte) (a & 0xFF);
        ret[1] = (byte) ((a >> 8) & 0xFF);
        ret[2] = (byte) ((a >> 16) & 0xFF);
        ret[3] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }

    public void close() throws IOException {
        if(server!=null) server.close();
    }
}
