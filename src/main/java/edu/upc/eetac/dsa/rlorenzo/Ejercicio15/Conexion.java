package edu.upc.eetac.dsa.rlorenzo.Ejercicio15;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Conexion extends Thread {

	static String jugador1, jugador2;
	static String apuesta1, apuesta2;
	DataInputStream leer;
	DataOutputStream escribir;
	static Socket[] sc = new Socket[2];
	static int cont = 0;

	// Constructor.
	public Conexion(Socket aSocket) {

		// Asocia el Socket(this) con el del cliente que conecta.
		sc[cont] = aSocket;
		// partida.numJugador = 0;

		try {
			// Creamos los flujos de entrada y salida de datos, y lo se los
			// asociamos al socket cliente.
			escribir = new DataOutputStream(sc[cont].getOutputStream());
			leer = new DataInputStream(sc[cont].getInputStream());

		} catch (IOException ex) {

			System.out.println("Error en conexion: " + ex.getMessage());
		}

		// Lanzamos el m�todo run.
		this.start();
	}

	public void run() {
		try {

			String mensajeEnviado;
			String mensajeRecibido = null;
			String[] linea;
			mensajeEnviado = "Te has conectado al servidor";
			escribir.writeUTF(mensajeEnviado);

			mensajeRecibido = leer.readUTF();
			linea = mensajeRecibido.split(" ");

			while (mensajeRecibido != null) {

				switch (linea[0]) {

				case "PLAY":
					if (cont == 0) {

						jugador1 = linea[1];
						mensajeEnviado = "WAIT";
						escribir.writeUTF(mensajeEnviado);
						cont++;

					}

					else if (cont == 1) {
						jugador2 = linea[1];
						mensajeEnviado = "VERSUS " + jugador1;
						escribir.writeUTF(mensajeEnviado);

						escribir = new DataOutputStream(sc[0].getOutputStream());
						mensajeEnviado = "VERSUS " + jugador2;
						escribir.writeUTF(mensajeEnviado);

						// Mensaje YOUR BET
						mensajeEnviado = "YOUR BET";
						escribir.writeUTF(mensajeEnviado);

						// Mensaje WAIT BET
						escribir = new DataOutputStream(sc[1].getOutputStream());
						mensajeEnviado = "WAIT BET";

						escribir.writeUTF(mensajeEnviado);
						cont = 0;
					}

					break;

				case "MY":
					if (cont == 0) {

						mensajeEnviado = "BET OF " + jugador1 + " " + linea[2];
						apuesta1 = linea[2];
						escribir = new DataOutputStream(sc[0].getOutputStream());
						escribir.writeUTF(mensajeEnviado);

						escribir = new DataOutputStream(sc[1].getOutputStream());
						escribir.writeUTF(mensajeEnviado);

						mensajeEnviado = "YOUR BET";
						escribir.writeUTF(mensajeEnviado);

						// Primer jugador en espera
						escribir = new DataOutputStream(sc[0].getOutputStream());
						mensajeEnviado = "WAIT BET";
						escribir.writeUTF(mensajeEnviado);
						cont++;
					}

					else if (cont == 1) {
						mensajeEnviado = "BET OF " + jugador2 + " " + linea[2];
						apuesta2 = linea[2];
						escribir = new DataOutputStream(sc[0].getOutputStream());
						escribir.writeUTF(mensajeEnviado);
						escribir = new DataOutputStream(sc[1].getOutputStream());
						escribir.writeUTF(mensajeEnviado);

						int monedas1, monedas2;
						linea = apuesta1.split("/");

						monedas1 = Integer.parseInt(linea[0]);
						linea = apuesta2.split("/");
						monedas2 = Integer.parseInt(linea[0]);

						if (monedas1 > monedas2) {
							mensajeEnviado = "WINNER";
							escribir = new DataOutputStream(
									sc[0].getOutputStream());
							escribir.writeUTF(mensajeEnviado);

						} else if (monedas1 < monedas2) {
							mensajeEnviado = "WINNER";
							escribir = new DataOutputStream(
									sc[1].getOutputStream());
							escribir.writeUTF(mensajeEnviado);

						}

						else if (monedas1 == monedas2) {

							mensajeEnviado = "NONE";
							escribir = new DataOutputStream(
									sc[0].getOutputStream());
							escribir.writeUTF(mensajeEnviado);
							escribir = new DataOutputStream(
									sc[1].getOutputStream());
							escribir.writeUTF(mensajeEnviado);
						}

						mensajeRecibido = null;

					}
					break;

				}
				mensajeRecibido = leer.readUTF();
				linea = mensajeRecibido.split(" ");
			}

			sc[0].close();
			sc[1].close();
			escribir.close();
			leer.close();

		} catch (IOException e) {

			System.out.println(e.getMessage());
		}

		catch (Exception e) {
			System.out.println(e.getMessage());

		}
	}
}