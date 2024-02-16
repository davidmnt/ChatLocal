import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PUERTO = 2020;
    private static final List<Socket> clientes = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor esperando conexiones...");

            while (true) {
                Socket socket = serverSocket.accept();
                clientes.add(socket);
                System.out.println("Nuevo cliente conectado: " + socket);

                // Crea un hilo para manejar las comunicaciones con el cliente
                Thread clienteThread = new Thread(new ManejadorCliente(socket));
                clienteThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase interna para manejar las comunicaciones con cada cliente
    static class ManejadorCliente implements Runnable {
        private final Socket socket;
        private BufferedReader entradaCliente;
        private PrintWriter salidaCliente;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                entradaCliente = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                salidaCliente = new PrintWriter(socket.getOutputStream(), true);

                String mensajeCliente;
                while ((mensajeCliente = entradaCliente.readLine()) != null) {
                    System.out.println("Mensaje recibido de " + socket + ": " + mensajeCliente);
                    enviarMensajeATodos(mensajeCliente);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Cerrar conexiones y eliminar cliente de la lista
                try {
                    clientes.remove(socket);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void enviarMensajeATodos(String mensaje) {
            for (Socket cliente : clientes) {
                if (cliente != socket) {
                    try {
                        PrintWriter salidaCliente = new PrintWriter(cliente.getOutputStream(), true);
                        salidaCliente.println(mensaje);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}