
package com.lake.calcolatrice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        String host = "localhost";
        int porta = 2727;

        try (
                Socket socket = new Socket(host, porta);

                // Input Tastiera
                Scanner keyInput = new Scanner(
                        System.in,
                        StandardCharsets.UTF_8.name());

                // Lettura messaggi dal Server
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream(),
                                StandardCharsets.UTF_8));

                // Invio messaggi al Server
                PrintWriter output = new PrintWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream(),
                                StandardCharsets.UTF_8),
                        true);

        ) {

            System.out.printf("Connesso al Server %s:%d\n",
                    socket.getInetAddress().getHostAddress(),
                    socket.getPort());

            while (true) {

                // Primo numero
                System.out.print("\nPrimo numero (exit per uscire): ");
                String numero1 = keyInput.nextLine();

                if (numero1.equalsIgnoreCase("exit")) {
                    output.println("exit");
                    break;
                }

                // Operatore
                System.out.print("Operatore (+, -, *, /): ");
                String operatore = keyInput.nextLine();

                // Secondo numero
                System.out.print("Secondo numero: ");
                String numero2 = keyInput.nextLine();

                // Crea il messaggio da inviare
                String msg = numero1 + " " + operatore + " " + numero2;

                // Invia il messaggio al Server
                output.println(msg);

                // Riceve la risposta
                String response = input.readLine();

                if (response == null) {
                    System.out.println("Il Server ha chiuso la connessione");
                    break;
                }

                System.out.printf("\n[SERVER]: %s\n", response);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
