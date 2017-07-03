/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remoto;

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.TreeMap;
/**
 *
 * @author cassiano
 */
public interface IServerRoomChat extends Remote{
    public TreeMap<String, IRoomChat> getRooms() throws RemoteException;
    public void createRoom(String roomName) throws RemoteException;
}
