import java.io.*;
import java.net.*;
import java.util.*;

public class WebClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		while (true) {

			try {
				BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
				BufferedWriter wr = null;
				String hostName, context, fileName;
				int port;

				System.out.print("\n\nEnter your choice:\n\n1.\tFILE\n2.\tJAVA\n3.\tEXIT\n\nYour Choice: ");
				int choice = Integer.parseInt(input.readLine());

				// System.out.println(choice);

				if (choice == 1) {

					int FILE_SIZE = 103309999; // should be more than file size.
					context = "FILE";
					System.out.print("Enter File Name: ");
					fileName = input.readLine();
					System.out.print("Enter Host Name/Address: ");
					hostName = input.readLine();
					System.out.print("Enter Port no: ");
					port = Integer.parseInt(input.readLine());
					//System.out.println(fileName + " " + hostName + " " + port);

					InetAddress addr = InetAddress.getByName(hostName);
					Socket socket = new Socket(addr, port);
					wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
					wr.write("POST /" + context + "/" + fileName + " HTTP/1.0\r\n");
					wr.write("Content-Length: " + 0 + "\r\n");
					wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
					wr.write("\r\n");
					wr.flush();
					
					// receive file
					InputStream is = null;
					FileOutputStream fos = null;
					BufferedOutputStream bos = null;
					try {
						int bytesRead;
						int current = 0;
						byte[] mybytearray = new byte[FILE_SIZE];
						is = socket.getInputStream();
						fos = new FileOutputStream("Downloads/" + fileName);
						bos = new BufferedOutputStream(fos);
						bytesRead = is.read(mybytearray, 0, mybytearray.length);
						current = bytesRead;

						do {
							bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
							if (bytesRead >= 0)
								current += bytesRead;
						} while (bytesRead > -1);

						bos.write(mybytearray, 0, current);
						bos.flush();

						System.out.println("\nFile " + fileName + " downloaded (" + current + " bytes read)");
						fos.close();
						bos.close();
						wr.close();
						socket.close();
						
					} catch (Exception e) {
						System.out.println("\nFile " + fileName + " doesnot exists");
						fos.close();
						bos.close();
						wr.close();
						socket.close();
					}

				} else if (choice == 2) {

					context = "JAVA";
					System.out.print("Enter Service Name: ");
					String serviceName = input.readLine();
					System.out.print("Enter Function Name and Parameters as func=<method name> & <var name>=<value>: ");
					String body = input.readLine();

					System.out.print("Enter Host Name/Address: ");
					hostName = input.readLine();
					System.out.print("Enter Port no: ");
					port = Integer.parseInt(input.readLine());

					InetAddress addr = InetAddress.getByName(hostName);
					Socket socket = new Socket(addr, port);
					wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
					wr.write("POST /" + context + "/" + serviceName + " HTTP/1.0\r\n");
					wr.write("Content-Length: " + body.length() + "\r\n");
					wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
					wr.write("\r\n");

					// Send Parameters
					wr.write(body);
					wr.flush();

					// Get response
					// BufferedReader rd = new BufferedReader(new
					// InputStreamReader(socket.getInputStream()));
					// String line;

					// while ((line = rd.readLine()) != null) {
					// System.out.println(line);
					// }
					// rd.close();

					InputStream in = socket.getInputStream();
					ObjectInputStream oin = new ObjectInputStream(in);
					String stringFromServer = (String) oin.readObject();
					System.out.println();
					System.out.println(stringFromServer);

					// FileWriter writer = new FileWriter("received.txt");
					// writer.write(stringFromServer);

					in.close();
					wr.close();
					socket.close();
					// writer.close();

				} else if (choice == 3) {
					break;
				} else
					System.out.println("Invalid Choice.\n");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
