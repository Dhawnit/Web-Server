# Web Server
Implemented a Web Server with RPC Service, File Download service and view files on the Web Browser.


WebServer.java
```
1. Compile & Run WebServer.java
2. Server started: "listening to port 9000".
3. Server shows what type of requests coming from the client and sends the appropriate response to the client.
```

WebClient.java
```
1. Compile & Run the WebClient.java

2. A menu driven interface appears:
	
	1.  FILE
	2.  JAVA
	3.  EXIT

3. Select your context JAVA or FILE from the above options.

4. For FILE:
	
	Enter File Name: try.jpg
	Enter Host Name/Address: localhost
	Enter Port no: 9000

	POST request visible at Server: POST /FILE/try.jpg HTTP/1.0

	File downloaded at WebClient.

	OR
	==

	Write in browser: localhost:9000/FILE/try.jpg (GET REQUEST)

	File opens up in browser.


5. For JAVA:
	
	Enter Service Name: Stock
	Enter Function Name and Parameters as func=<method name> & <var name>=<value>: func=checkStockPrice&symbol=Microsoft
										       or
										       func=buy&symbol=Microsoft&qty=20
										       or
										       func=sell&symbol=Microsoft&qty=20
										       
	Enter Host Name/Address: localhost
	Enter Port no: 9000

	POST request visible at Server: (Simulated by Client Manually)

	POST /JAVA/Stock HTTP/1.0
	Content-Length: 37
	Content-Type: application/x-www-form-urlencoded
	
	func=checkStockPrice&symbol=Microsoft

	Response to Client:
	Stock :Microsoft Price: Rs 100
```

EXTENSION :
```
-Session management is incorporated in server. 

-checkOut() method would commit all the BUY and SELL transactions performed with the server.
	func=checkOut

-totalStocks(String symbol) method returns the total stocks bought(committed) for given stockSymbol.
	func=totalStocks&symbol=Microsoft
```
