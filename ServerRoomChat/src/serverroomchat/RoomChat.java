/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverroomchat;

import remoto.*;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author cassiano-ncc
 */
public class RoomChat extends UnicastRemoteObject implements IRoomChat {

    String roomName;
    static int ID = 0;
    static IUserChat obj;
    public TreeMap<String, IUserChat> userList = new TreeMap();

    RoomChat(String nome) throws RemoteException {
        roomName = nome;
    }

    @Override
    public int joinRoom(String username, IUserChat localObjRef) throws RemoteException, AccessException {
        this.userList.put(username, localObjRef);
        System.out.println("ENTROU: " + username);
        Set<String> lista = userList.keySet();
        for (String nome : lista) {
            userList.get(nome).updateUserList(this.userList);
        }
        return ID++;
    }

    @Override
    public void leaveRoom(String usrName) throws RemoteException {
        userList.remove(usrName);
        /* precisa fazer isso pra dar certo
                usr.userList = null;
                usr.room = null;
                usr.ID = null;
                usr.clockMatrix = new Integer[20][20];
                usr.buffer.removeAll(usr.buffer);
                usr.usrFrame.setTitle(usr.usrName);
         */
        Set<String> lista = userList.keySet();
        for (String nome : lista) {
            userList.get(nome).updateUserList(this.userList);
        }
    }

    @Override
    public void closeRoom() throws RemoteException {
        while (!userList.isEmpty()) {
            Set<String> lista = userList.keySet();
            for (String nome : lista) {
                //String msg = "Sala Fechada";
                //int userID = userList.get(nome).getID();
                //userList.get(nome).deliverMsg(nome, msg, userID, userList.get(nome).getClockVector());
                leaveRoom(nome);

            }
        }

        ServerRoomChat.close(roomName);

    }
}
