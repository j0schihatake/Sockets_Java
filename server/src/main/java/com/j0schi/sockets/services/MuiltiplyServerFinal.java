package com.j0schi.sockets.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kim_hu
 */
public class MuiltiplyServerFinal {
    public static void main(String[] args) {
        MuiltiplyServerFinal server = new MuiltiplyServerFinal();
        server.service();
    }

    private void service() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(80);
            while (true) {
                Socket socket = server.accept();
                new Thread(new Task(socket)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(MuiltiplyServerFinal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MuiltiplyServerFinal.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (server != null) {
                    server.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(MuiltiplyServerFinal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class Task implements Runnable {

        private Socket client;

        public Task(Socket socket) {
            this.client = socket;
        }

        private void handleSocket() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream());
                String header = reader.readLine();
                if (header != null) {
                    new Thread(new HandleRead(client, reader, header)).start();
                    new Thread(new HandleWrite(client, out, header)).start();
                }
            } catch (IOException ex) {
                Logger.getLogger(MuiltiplyServerFinal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            handleSocket();
        }
    }

    class HandleWrite implements Runnable {

        private Socket client;
        private PrintWriter out;
        private String header;

        public HandleWrite(Socket client, PrintWriter out, String header) {
            this.client = client;
            this.out = out;
            this.header = header;
        }

        private void handleWrite() {
            while (true) {
                if (header.contains("HTTP")) {
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type:text/html;charset=utf-8");
                    out.println();
                    out.println("<head></head>");
                    out.println("<body>");
                    out.println("<h1>Hello Word!</h1>");
                    out.println("</body>");
                    out.flush();
                    break;
                } else {
                    Scanner scan = new Scanner(System.in);
                    String reply = scan.nextLine();
                    out.println(reply);
                    out.flush();
                    if (reply.equals("bye")) {
                        break;
                    }
                }
            }
            out.close();
            try {
                client.close();
            } catch (IOException ex) {
                Logger.getLogger(MuiltiplyServerFinal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            handleWrite();
        }
    }

    class HandleRead implements Runnable {

        private Socket client;
        private BufferedReader reader;
        private String header;

        public HandleRead(Socket client, BufferedReader reader, String header) {
            this.reader = reader;
            this.header = header;
            this.client = client;
        }

        private void handleRead() {
            try {
                System.out.println("the header from the client: " + header);
                while (true) {
                    if (!header.contains("HTTP")) {
                        String content = reader.readLine();
                        System.out.println("the content from the client: " + content);
                        if (content.equals("bye")) {
                            break;
                        }
                    } else {
                        String content = reader.readLine();
                        System.out.println("from client: " + content);
                        if (content.length() == 0) {
                            System.out.println("break");
                            break;
                        }
                    }
                }
                reader.close();
                client.close();
            } catch (IOException ex) {
                Logger.getLogger(MuiltiplyServerFinal.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                    client.close();
                } catch (IOException ex) {
                    Logger.getLogger(MuiltiplyServerFinal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        @Override
        public void run() {
            handleRead();
        }
    }
}