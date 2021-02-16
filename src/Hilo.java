import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Hilo extends Thread{
	
	 static HashMap<String,Socket> datosCliente = new HashMap<String,Socket>();
	 static ArrayList<Integer> arrayNumeros = new ArrayList<Integer>();
	 String nombre, mensaje, respuesta, ordenados = "";
	 Socket sock;
	 DataInputStream entrada;
	 DataOutputStream salida;
	 Character carac, charNumero;
	 int numero,coincide,aux;

	public Hilo(Socket sock) {
		this.sock = sock;
	}
	
	public void run() {
		Boolean parar = false;
		
		try {
			entrada = new DataInputStream(sock.getInputStream());
			nombre = entrada.readUTF();
			crearCliente(nombre, sock);
			System.out.println("El nick del cliente es "+nombre);
			respuesta = "El servidor dice: El nick del cliente es "+nombre;
			
			do {
				mensaje = entrada.readUTF();
				
				if(mensaje.toLowerCase().equals("parar")) {
					
					respuesta = "El servidor dice: El usuario "+nombre+" se va a borrar";
					System.out.println("El usuario "+nombre+" se va a borrar");
					parar = true;
					//Eliminar cliente del HashMap
					enviarMensaje(respuesta);
					borrarCliente(nombre);
				}
				else {
					rellenarArray();
					respuesta = "El servidor dice: El cliente "+nombre+" dice: "+mensaje;
					System.out.println("El cliente "+nombre+" dice: "+mensaje);
					enviarMensaje(respuesta);
				}
				
			}while(!parar);
			System.out.println("salimos");
			salida.close();
			entrada.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	synchronized public void enviarMensaje(String respuesta) {
		for(String name : datosCliente.keySet()) {
			try {
				salida = new DataOutputStream(datosCliente.get(name).getOutputStream());
				salida.writeUTF(respuesta);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	synchronized public void borrarCliente(String nombre) {
		datosCliente.remove(nombre);
	}
	
	synchronized public void crearCliente(String nombre,Socket sock) {
		datosCliente.put(nombre, sock);
		System.out.println(sock);
	}
	
	synchronized public void rellenarArray() {
		coincide = 0;
		System.out.println(arrayNumeros);
		if(arrayNumeros.size() < 5) 
		{
			for(int i=0; i<mensaje.length();i++) 
			{
				if(Character.isDigit(mensaje.charAt(i))) 
				{
					charNumero = mensaje.charAt(i);
					numero = Character.getNumericValue(charNumero);
					
					for(int j=0;j<arrayNumeros.size();j++) 
					{
						if(numero == arrayNumeros.get(j)) 
						{
							coincide++;
						}
					}
					if(coincide==0) 
					{
						arrayNumeros.add(numero);
					}
				}
			}
		}
		else
		{
			ordenados = "";
			System.out.println("Ya se han leido 5 numeros diferentes");
			for (int i = 0; i < arrayNumeros.size() - 1; i++) 
			{
	            for (int j = 0; j < arrayNumeros.size() - i - 1; j++) 
	            {
	                if (arrayNumeros.get(j + 1) < arrayNumeros.get(j)) 
	                {
	                    aux = arrayNumeros.get(j + 1);
	                    arrayNumeros.set(j + 1,arrayNumeros.get(j));
	                    arrayNumeros.set(j,aux);
	                }
	            }
	        }
			for(int i=0; i<arrayNumeros.size();i++) 
			{
				ordenados += " "+arrayNumeros.get(i);
			}
			respuesta = "El servidor dice: Ya se han leido 5 numero y son estos ordenados:"+ordenados;
			System.out.println("Ya se han leido 5 numero y son estos ordenados:"+ordenados);
			enviarMensaje(respuesta);
		}
	}
}
