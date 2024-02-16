import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Cliente {

    static Scanner s = new Scanner(System.in);



    static PrintWriter out = null;

    public static void main(String[] args) {



        try {
            Socket socket = new Socket("localhost", 2020);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Flujo de entrada del teclado
            BufferedReader entradaTeclado = new BufferedReader(new InputStreamReader(System.in));

            // Leer mensajes del teclado y enviarlos al servidor
            String mensajeTeclado;
            while ((mensajeTeclado = entradaTeclado.readLine()) != null) {
                out.println(mensajeTeclado);
                System.out.println("Servidor: " + in.readLine());
            }

            // Cerrar conexiones
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
