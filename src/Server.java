import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PUERTO = 2020;
    private static final List<PrintWriter> clientes = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor esperando conexiones...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + socket);

                // Flujo de salida hacia el cliente
                PrintWriter salidaCliente = new PrintWriter(socket.getOutputStream(), true);
                clientes.add(salidaCliente);

                // Crea un hilo para manejar las comunicaciones con el cliente
                Thread clienteThread = new Thread(new ManejadorCliente(socket, salidaCliente));
                clienteThread.start();

                // Crea un hilo adicional para escuchar mensajes del cliente y retransmitirlos a todos los demás clientes
                Thread retransmisorThread = new Thread(new Retransmisor(socket));
                retransmisorThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase interna para manejar las comunicaciones con cada cliente
    static class ManejadorCliente implements Runnable {
        private final Socket socket;
        private final PrintWriter salidaCliente;
        private BufferedReader entradaCliente;

        public ManejadorCliente(Socket socket, PrintWriter salidaCliente) {
            this.socket = socket;
            this.salidaCliente = salidaCliente;
        }

        @Override
        public void run() {
            try {
                entradaCliente = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String mensajeCliente;
                while ((mensajeCliente = entradaCliente.readLine()) != null) {
                    System.out.println("Mensaje recibido de " + socket + ": " + mensajeCliente);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Clase interna para retransmitir mensajes de un cliente a todos los demás clientes
    static class Retransmisor implements Runnable {
        private final Socket socket;
        private BufferedReader entradaCliente;

        public Retransmisor(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                entradaCliente = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String mensajeCliente;
                while ((mensajeCliente = entradaCliente.readLine()) != null) {
                    enviarMensajeATodos(mensajeCliente);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void enviarMensajeATodos(String mensaje) {
            System.out.println("Mensaje retransmitido a todos los clientes: " + mensaje);
            for (PrintWriter cliente : clientes) {
                cliente.println(socket.getInetAddress().getHostAddress() + ": " + mensaje);
            }
        }
    }
}