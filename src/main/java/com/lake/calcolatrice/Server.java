package com.lake.calcolatrice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main(String[] args) {

        // Port number
        int porta = 2727;
        String homeString = String.format(
                "\n╔══════════════════════════════════════╗\n" +
                "║       CALCOLATRICE - SERVER          ║\n" +
                "║       Porta: %-24d║\n" +
                "╚══════════════════════════════════════╝\n",
                porta
        );

        // Creazione Socket sulla porta
        try (ServerSocket serverSocket = new ServerSocket(porta)) {

            System.out.print(homeString);

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> clientHandler(socket)).start();
            }

        } catch (Exception e) {
            System.out.println("[ERRORE SERVER] " + e.getMessage());
        }
    }

    public static void clientHandler(Socket socket) {
        String client = "";

        try {
            client = getInfoClientSocket(socket);

            System.out.printf("%n[+] Client connesso: %s%n", client);

            // Lettura dal Client
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );

            // Scrittura verso il Client
            PrintWriter output = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                    true
            );

            String msg;

            while ((msg = input.readLine()) != null) {

                // Comando di uscita
                if (msg.equalsIgnoreCase("exit")) {
                    break;
                }

                System.out.printf("[>] %-21s | %s%n", client, msg);

                // Divide il messaggio in 3 parti
                String[] dati = msg.trim().split("\\s+");

                if (dati.length != 3) {
                    output.println("Errore: operazione non valida");
                    continue;
                }

                try {
                    double numero1 = Double.parseDouble(dati[0]);
                    String operatore = dati[1];
                    double numero2 = Double.parseDouble(dati[2]);

                    double risultato = 0;
                    boolean valido = true;

                    switch (operatore) {
                        case "+":
                            risultato = numero1 + numero2;
                            break;

                        case "-":
                            risultato = numero1 - numero2;
                            break;

                        case "*":
                            risultato = numero1 * numero2;
                            break;

                        case "/":
                            if (numero2 == 0) {
                                output.println("Errore: divisione per zero");
                                valido = false;
                            } else {
                                risultato = numero1 / numero2;
                            }
                            break;

                        default:
                            output.println("Errore: operatore non valido");
                            valido = false;
                            break;
                    }

                    if (valido) {
                        output.println("Risultato: " + risultato);
                    }

                } catch (NumberFormatException e) {
                    output.println("Errore: numero non valido");
                }
            }

        } catch (Exception e) {
            System.out.println("[ERRORE CLIENT] " + e.getMessage());
        } finally {
            System.out.printf("[-] Connessione chiusa: %s%n", client);
        }
    }

    public static String getInfoClientSocket(Socket s) {
        return String.format("%s:%d",
                s.getInetAddress().getHostAddress(),
                s.getPort());
    }
}
