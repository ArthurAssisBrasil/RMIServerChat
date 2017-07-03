/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remoto;

import java.rmi.RemoteException;
import java.util.TreeMap;

/**
 *
 * @author cassiano
 */ 
public interface IUserChat extends java.rmi.Remote{
    public void deliverMsg(String senderUsrName, String msg, int id, Integer[] clockMatrix) throws RemoteException;
    public void updateUserList(TreeMap<String, IUserChat> userList) throws RemoteException;
    public int getID()  throws RemoteException;
    public Integer[] getClockVector() throws RemoteException;

    public int leaveRoom(String nome);
}
