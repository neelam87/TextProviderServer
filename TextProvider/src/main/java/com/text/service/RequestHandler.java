/**
 * 
 */
package com.text.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.server.TextProvider.TextProviderServer;

/**
 * @author neel
 *
 */
public class RequestHandler extends Thread
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
    private Socket socket;
    private String filePath;
    public RequestHandler(Socket socket, String filePath)
    {
        this.socket = socket;
        this.filePath = filePath;
    }

    @Override
    public void run()
    {
        try
        {
        	LOGGER.info("Received a connection" );

            // Get input and output streams
            BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            PrintWriter out = new PrintWriter( socket.getOutputStream() );

            // Write out our header to the client
            out.flush();

            // Echo lines back to the client until the client closes the connection or we receive an empty line
            String line = in.readLine();
            while( line != null && line.length() > 0 )
            {
            	try {
            	if(Integer.valueOf(line)>0 && getLine(Integer.valueOf(line))!=null){
            		out.println(getLine(Integer.valueOf(line)));
            	}
            	else {
            		out.println("ERR");
            	}
            	}
        		catch( NoSuchElementException e )
                {
        			out.println("ERR");
                }
                out.flush();
                line = in.readLine();
            }

            // Close our connection
            in.close();
            out.close();
            socket.close();

            LOGGER.error("Connection closed" );
        }
        catch(Exception e)
        {
        	LOGGER.error(e.getMessage());
        }
    }
    
    
    private String getLine(Integer key) throws IOException {
		String extractedLine = null ;
		try{
			Stream<String> lines = Files.lines(Paths.get(filePath));
			extractedLine = lines.skip(key-1).findFirst().get();
			if(validateLines(extractedLine)) {
				lines.close();
		        return extractedLine;
			 }else {
				 lines.close();
				 LOGGER.error("Format is not correct");
			 }
			
		}catch(Exception e){
			LOGGER.error(e.getMessage());
		}
		
		return extractedLine;

	}

	private boolean validateLines(String line) {
		return CharMatcher.ascii().matchesAllOf(line);
	}

}
