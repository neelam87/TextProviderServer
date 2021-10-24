/**
 * 
 */
package com.server.TextProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.text.service.RequestHandler;



/**
 * @author neel
 *
 */
public class TextProviderServer extends Thread
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TextProviderServer.class);
    private ServerSocket serverSocket;
    private String filePath;
    private boolean running = false;
    
    private static int PORT_NUMBER = 10322;

    public TextProviderServer( int port, String filePath)
    {
    	this.PORT_NUMBER = PORT_NUMBER;
    	this.filePath = filePath;
    }

    
    public static void main( String[] args )
    {
        if( args.length == 0 )
        {
        	LOGGER.error("Usage: SimpleSocketServer <port>");
            System.exit( 0 );
        }
    	
    	String filePath = args[0];
    	LOGGER.info("Start server on port: " + PORT_NUMBER );

        TextProviderServer server = new TextProviderServer(PORT_NUMBER, filePath);
        server.startServer();

        // Automatically shutdown in 1 minute
        long startTime = System.currentTimeMillis();
        LOGGER.info("Start time for execution---" +  (startTime/1000) + " seconds");
        try
        {
            Thread.sleep( 60000 );
        }
        catch( Exception e )
        {
        	LOGGER.error(e.getMessage());
        }
        LOGGER.info("End time for execution---" +  (System.currentTimeMillis()-startTime)/1000 +" seconds");

        server.stopServer();
    }
    
    public void startServer()
    {
        try
        {
            serverSocket = new ServerSocket( PORT_NUMBER);
            this.start();
        }
        catch (IOException e)
        {
        	LOGGER.error(e.getMessage());
        }	
    }

    public void stopServer()
    {
        running = false;
        this.interrupt();
    }

    @Override
    public void run()
    {
        running = true;
        while( running )
        {
            try
            {
                // Call accept() to receive the next connection
                Socket socket = serverSocket.accept();

                // Pass the socket to the RequestHandler thread for processing
                RequestHandler requestHandler = new RequestHandler(socket, filePath);
                requestHandler.start();
            
            }
            catch (IOException e)
            {
            	LOGGER.error(e.getMessage());
            }
        }
    }

    
}
