package com.dispatch_x12.services;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;





public class TestChatClass {
    private Socket socket;
    private boolean tryToReconnect = true;
    private Thread heartbeatThread;
    private long heartbeatDelayMillis = 5000;
    
    public void TestClassClient(final String server, final int port) {
        connect(server, port);
        heartbeatThread = new Thread() {
            public void run() {
                while (tryToReconnect) {
                    //send a test signal
                    try {
                        socket.getOutputStream().write(666);
                        sleep(heartbeatDelayMillis);
                    } catch (InterruptedException e) {
                        // You may or may not want to stop the thread here
                        // tryToReconnect = false;
                    } catch (IOException e) {
						Log.d("IOException on Thread","Server is offline");
                        connect(server, port);
                    }
                }
            };
        };
        heartbeatThread.start();
    }

    private void connect(String server, int port){
        try {
        	socket.setKeepAlive(true);
            socket = new Socket(server, port);
        } catch (UnknownHostException e) {
        	Log.d("UNKOWN", e.getMessage());
        } catch (IOException e) {
        	Log.d("IOConnect", e.getMessage());
        	         }
    }

    public void shutdown() {
        tryToReconnect = false;
    }
}