/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nicola
 */
public class Messaggi extends Thread{

    private Connect c;
    private Server s;
    private BufferedReader in;
    private Socket cl;
    private int i;
    private boolean showIp;
    
    public Messaggi(Connect c, Server s, BufferedReader in, Socket cl, int i, boolean sI) 
    {
        this.c = c;  
        this.s = s;  
        this.in = in;
        this.cl = cl;
        this.i = i;
        this.showIp = sI;
    }   
    
    
    @Override
    public void run() {
        
        String testo;
        String aggiungi = "";
        boolean disconnetti = false;
        while(disconnetti == false)
        {              
                       
            try {
                
                String message = in.readLine();
                //Controlla se indica disconnessione
                if(message.equals("Disconnetti"))
                {
                    disconnetti = true;
                    s.cl.remove(i);
                    c.setMess("" + c.getMess() + c.getUser() + " si è disconnesso\n");
                    
                    for(int i = 0; i < s.out.size(); i++)
                    {
                        s.out.get(i).println("" + c.getUser() + " si è disconnesso");
                        s.out.get(i).println(s.getUsers());
                    }
                    
                    c.RemUser(c.getUser());
                    try {
                        sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Messaggi.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    s.out.get(i).close();
                    in.close();
                    cl.close();
                }
                else
                {
                    if(message.equals("Private message"))
                    {
                        String utente=in.readLine();
                        
                        int y = 0;
                        boolean trovato = false;
                        
                        while(y < s.utenti.size() && trovato == false)
                        {
                            if(s.utenti.get(y).equals(utente))
                            {
                                trovato = true;
                            }
                            else
                            {
                                y++;
                            }
                        }
                        
                        message = in.readLine();
                        
                        //Stampa messaggio a video sul server e lo invia a tutti i client
                        testo = c.getMess();

                        if(showIp == true)
                        {
                            String[] array = cl.getInetAddress().toString().split("/");
                            aggiungi = c.getUser() + "[" + array[1] + "]" + ": " + message;
                        }
                        else
                        {
                            aggiungi = c.getUser() + ": " + message;
                        }

                        c.setMess(testo + "" + aggiungi + "\n");

                        s.out.get(y).println(aggiungi);
                        //RIATTIVARE, disattivato solo per mostrare private message
                        //s.out.get(i).println(aggiungi);
                                                
                    }
                    else
                    {
                        //Stampa messaggio a video sul server e lo invia a tutti i client
                        testo = c.getMess();

                        if(showIp == true)
                        {
                            String[] array = cl.getInetAddress().toString().split("/");
                            aggiungi = c.getUser() + "[" + array[1] + "]" + ": " + message;
                        }
                        else
                        {
                            aggiungi = c.getUser() + ": " + message;
                        }

                        c.setMess(testo + "" + aggiungi + "\n");

                        for(int i = 0; i < s.out.size(); i++)
                        {
                            s.out.get(i).println(aggiungi);
                        }
                    }
                    
                    
                }
                
                
                
            } catch (IOException ex) {
                Logger.getLogger(Messaggi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }   
      
    
}
