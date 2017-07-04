/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverroomchat;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import remoto.IServerRoomChat;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import remoto.IRoomChat;

/**
 *
 * @author cassiano-ncc
 */
public class ServerRoomChat extends UnicastRemoteObject implements IServerRoomChat{
    
    static Registry registry;     
    String host = "localhost";
    static TreeMap<String, IRoomChat> roomList = new TreeMap<String, IRoomChat>();
    static ServerFrame srvFrame;
    public int ID = 0;
    
    public ServerRoomChat() throws RemoteException{
        super();
    }
    
    
    public static void main(String[] args) throws RemoteException {
       // System.setSecurityManager(new RMISecurityManager());
        String IPServer = JOptionPane.showInputDialog("Qual o IP do servidor?");
        ServerRoomChat server = new ServerRoomChat();
        registry = LocateRegistry.createRegistry(2020);           
        try {
            Naming.rebind("rmi://"+IPServer+"/servidor", server);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServerRoomChat.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Servidor criado!");
        srvFrame = new ServerFrame(roomList);
        srvFrame.setVisible(true);
    }

    @Override
    public TreeMap getRooms() {
        return roomList;
    }

    @Override
    public void createRoom(String roomName) throws RemoteException, AccessException {
        IRoomChat room = null;
        
        try{
            room = new RoomChat(roomName);
            this.registry.bind(roomName, room);
            
        } catch (AlreadyBoundException ex) {
            Logger.getLogger(ServerRoomChat.class.getName()).log(Level.SEVERE, null, ex);
        }
        roomList.put(roomName, room);
        System.out.println("Sala Criada:" + roomName);
        srvFrame.atualiza();       
    }
    
    public static void close(String sala){
       ServerRoomChat.roomList.remove(sala);
       srvFrame.atualiza();
    }
   
    
}