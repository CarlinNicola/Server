/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author nicola.carlin
 */

import com.sun.media.sound.SF2Layer;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

public class Server extends Thread
{

    private ServerSocket server;
    private ServerForm sF;
    public static ArrayList <Socket>cl;
    private int i;
    public static Messaggi m;    
    public static ArrayList<PrintStream> out = new ArrayList<PrintStream>();
    public static ArrayList<String> utenti = new ArrayList<String>();
      
        
    public Server(ServerForm serv) throws Exception
    {
        server = new ServerSocket(4000); 
        sF = serv;
        cl = new ArrayList<Socket>();
        i = 0;
        
        this.start();               
        
    };
    
    
    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                Socket client = server.accept();
                
                
                this.cl.add(client);                                
                i++;               
                Connect c = new Connect(this, client, i);                
            }
            catch(Exception e)
            {
                
            }
        }        
    };
    
    public String getTxtMessa()
    {
        return sF.getMessaggi();
    }
    
    public void setTxtMessa(String t)
    {
        sF.setMessaggi(t);
    }
    
    public void addUser(String u)
    {
        sF.addUsers(u);
    }
    
    public void RemUser(String u)
    {
        sF.removeUsers(u);
    } 
    
    public String getUsers()
    {
        return sF.getUsers();
    }
    
    
}

    
class Connect extends Thread
{
    BufferedReader in = null;
    private static Socket client;    
    private String username;
    private String password;
    private int i;
    private Server s;
    private boolean showIp = false;
    private String[] array;
    private String user = "";
    
    public Connect()
    {}

    public Connect (Server s, Socket cl, int i)
    {
        this.s = s;
        client = cl;
        this.i = i - 1;
                
        
        try
        {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            s.out.add(new PrintStream(client.getOutputStream(), true));
            username = in.readLine();            
            password = in.readLine();            
            checkIp();
            
            s.utenti.add(user);           
            s.addUser(user);   
            
            for(int y = 0; y < s.out.size(); y++)
            {
                s.out.get(y).println(s.getUsers());
            }
                                    
            s.setTxtMessa(s.getTxtMessa() + "" + user + " si è connesso\n");
            
            for(int y = 0; y < s.out.size(); y++)
            {
                s.out.get(y).println("" + getUser() + " si è connesso");
            }
        }
        catch(Exception el)
        {
            
            try
            {
                client.close();                    
            }
            catch(Exception ell)
            {
                System.out.println(ell.getMessage());
            }

            return;
            
        }
          
        

        this.start();
    }
    
    @Override
    public void run()
    {
        s.m = new Messaggi(this, s, in, client, i, showIp);
        s.m.start();
    }
        
    
    public String getMess()
    {
        return s.getTxtMessa();
    }  
    
    public void setMess(String testo)
    {
        s.setTxtMessa(testo);
    } 
    
    public String getUser()
    {
        return user;
    }
    
    public void RemUser(String u)
    {
        s.RemUser(u);
    }
    
    public void checkIp()
    {
        array = username.split(" ");
        if(array[array.length -1 ].equals("check"))
        {
            showIp = true;
        }

        for(int y = 0; y < array.length; y++)
        {                
            if(!array[y].equals("check") && !array[y].equals("uncheck"))
            {
                user = user + array[y] + " ";
            }

        }        
    }
    
}
