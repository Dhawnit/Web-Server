import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.Method;

public class WebServer {

	public static void main(String[] args) throws IOException {

		String requestMessageLine;
		String fileName;
		ServerSocket listenSocket = new ServerSocket(9000);
		System.out.println("listening to port 9000\n");
		System.out.println("======================================================");
		Class<?> classObj = null;
		Object instance = null;

		while (true) {

			Socket connectionSocket = listenSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			requestMessageLine = inFromClient.readLine();

			if (requestMessageLine.startsWith("GET")) {

				System.out.println("FILE REQUEST INCOMING FROM BROWSER(GET)\n");
				System.out.println(requestMessageLine);
				StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);
				tokenizedLine.nextToken();
				fileName = tokenizedLine.nextToken();

				if (fileName.equals("/")) {

					String httpResponse = "File Name not provided";
					System.out.println("File Name not provided");
					outToClient.write(httpResponse.getBytes("UTF-8"));
					connectionSocket.close();
				}

				else {

					if (fileName.startsWith("/") == true)
						fileName = fileName.substring(1);

					System.out.println("File :" + fileName);

					try {

						File file = new File(fileName);
						int numOfBytes = (int) file.length();
						FileInputStream inFile = new FileInputStream(fileName);
						byte[] fileInBytes = new byte[numOfBytes];
						inFile.read(fileInBytes);

						outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
						if (fileName.endsWith(".jpg"))
							outToClient.writeBytes("Content-Type:image/jpeg\r\n");
						outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
						outToClient.writeBytes("\r\n");
						outToClient.write(fileInBytes, 0, numOfBytes);
						connectionSocket.close();
					} catch (Exception e) {
						String httpResponse = "File doesnot exists";
						System.out.println("File doesnot exists");
						outToClient.write(httpResponse.getBytes("UTF-8"));
						connectionSocket.close();
					}
				}

			} else if (requestMessageLine.startsWith("POST /FILE")) {

				System.out.println("FILE REQUEST INCOMING (POST)\n");
				System.out.println(requestMessageLine);

				StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);
				tokenizedLine.nextToken();
				fileName = tokenizedLine.nextToken();
				fileName = fileName.substring(1);
				System.out.println(fileName);
				try {

					File file = new File(fileName);
					int numOfBytes = (int) file.length();
					byte[] fileInBytes = new byte[numOfBytes];
					FileInputStream inFile = new FileInputStream(fileName);
					BufferedInputStream bis = new BufferedInputStream(inFile);
					bis.read(fileInBytes, 0, fileInBytes.length);
					OutputStream os = connectionSocket.getOutputStream();
					System.out.println("Sending " + fileName + "(" + fileInBytes.length + " bytes)");
					os.write(fileInBytes, 0, fileInBytes.length);
					os.flush();
					bis.close();
					os.close();
					connectionSocket.close();
				} catch (Exception e) {

					System.out.println("File doesnot exists");
					connectionSocket.close();
				}

			}

			else if (requestMessageLine.startsWith("POST /JAVA")) {

				System.out.println("JAVA REQUEST INCOMING (POST)\n");
				System.out.println(requestMessageLine);

				StringTokenizer tokenizedLineService = new StringTokenizer(requestMessageLine);
				tokenizedLineService.nextToken();
				String serviceName = tokenizedLineService.nextToken();
				serviceName = serviceName.substring(serviceName.lastIndexOf('/') + 1);

				// System.out.println(serviceName);

				StringBuilder raw = new StringBuilder();
				String temp;

				final String contentHeader = "Content-Length: ";
				int contentLength = 0;

				// Reading the header of post request
				while (!(temp = inFromClient.readLine()).equals("")) {
					raw.append('\n' + temp);
					if (temp.startsWith(contentHeader)) {
						contentLength = Integer.parseInt(temp.substring(contentHeader.length()));
					}
				}

				System.out.println(raw);
				System.out.println();

				// Reading the body of post request
				StringBuilder body = new StringBuilder();
				int c = 0;
				for (int i = 0; i < contentLength; i++) {
					c = inFromClient.read();
					body.append((char) c);
				}

				System.out.println(body);
				System.out.println();

				try {

					List<String> myList = new ArrayList<String>(Arrays.asList(body.toString().split("&")));
					List<String> parameters = new ArrayList<String>();
					String functionName = null;

					for (String string : myList) {

						// System.out.println(string);
						StringTokenizer tokenizedLine = new StringTokenizer(string, "=");

						if (tokenizedLine.nextToken().equals("func")) {
							functionName = tokenizedLine.nextToken();
							// System.out.println(functionName);
						} else {
							parameters.add(tokenizedLine.nextToken());
						}
					}

					URLClassLoader classLoader = new URLClassLoader(
							new URL[] { new URL("file:JAVA/" + serviceName + ".jar") });

					if (classObj == null) {		
						classObj = Class.forName(serviceName, true, classLoader);
						instance = classObj.newInstance();
					}
					Class<?>[] parameterTypes = {};

					for (Method m : classObj.getDeclaredMethods()) {
						if (m.getName().equals(functionName)) {
							parameterTypes = m.getParameterTypes();
						}
					}

					Method methodObj = classObj.getDeclaredMethod(functionName, parameterTypes);
					
					Object[] params = new Object[parameters.size()];

					for (int i = 0; i < parameters.size(); i++) {

						if (parameterTypes[i].getSimpleName().equals("String")) {
							params[i] = parameters.get(i);
						} else if (parameterTypes[i].getSimpleName().equals("int")) {
							params[i] = Integer.parseInt(parameters.get(i));
						} else if (parameterTypes[i].getSimpleName().equals("double")) {
							params[i] = Double.parseDouble(parameters.get(i));
						}
					}
					String result = (String) methodObj.invoke(instance, params);
					System.out.println(result);
					OutputStream out = connectionSocket.getOutputStream();
					ObjectOutputStream oout = new ObjectOutputStream(out);
					oout.writeObject(result);
					oout.close();
					connectionSocket.close();

				} catch (Exception e) {
					// e.printStackTrace();
					String result = "Something went wrong. Try agin with correct Function name or Parameters";
					System.out.println(result);
					OutputStream out = connectionSocket.getOutputStream();
					ObjectOutputStream oout = new ObjectOutputStream(out);
					oout.writeObject(result);
					oout.close();
					connectionSocket.close();
				}
			} else {
				System.out.println("Bad Request Message");
				connectionSocket.close();
			}
			System.out.println("======================================================");
		}

	}

}
