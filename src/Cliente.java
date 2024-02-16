import java.io.*;
import java.net.Socket;

public class Cliente {
    static PrintWriter out = null;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 2020);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Hilo para leer mensajes del servidor
            Thread inputThread = new Thread(() -> {
                try {
                    String mensajeServidor;
                    while ((mensajeServidor = in.readLine()) != null) {
                        System.out.println("Servidor: " + mensajeServidor);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            inputThread.start();

            // Hilo para enviar mensajes al servidor
            Thread outputThread = new Thread(() -> {
                try {
                    BufferedReader entradaTeclado = new BufferedReader(new InputStreamReader(System.in));
                    String mensajeTeclado;
                    while ((mensajeTeclado = entradaTeclado.readLine()) != null) {
                        out.println(mensajeTeclado);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputThread.start();

            // Esperar a que los hilos terminen
            inputThread.join();
            outputThread.join();

            // Cerrar conexiones
            in.close();
            out.close();
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
