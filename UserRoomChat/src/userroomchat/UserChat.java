/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userroomchat;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import remoto.IUserChat;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import remoto.*;

/**
 *
 * @author cassiano, arthur
 */
public class UserChat extends UnicastRemoteObject implements IUserChat {

    static String usrName;
    static IServerRoomChat iServer;
    static Registry registry;
    public static TreeMap<String, IRoomChat> roomList;
    public TreeMap<String, IUserChat> userList = null;
    public Integer ID = null;
    static UserFrame userFrame;
    public static String IPServer;
    ArrayList<MsgBuffer> buffer = new ArrayList();
    Integer[][] clockMatrix = new Integer[20][20];

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        IPServer = JOptionPane.showInputDialog("Qual o IP do servidor?");
        System.out.println(IPServer);
        try {
            registry = LocateRegistry.getRegistry(IPServer, 2020);
            iServer = (IServerRoomChat) registry.lookup("Servidor");
            roomList = iServer.getRooms();
        } catch (Exception e) {
            System.out.println("erro:" + e);
            e.printStackTrace();
        }
        userFrame = new UserFrame(roomList, iServer, IPServer);
        userFrame.setVisible(true);
    }

    UserChat() throws RemoteException, AlreadyBoundException {
        usrName = JOptionPane.showInputDialog("Qual o nome do usuário?");
        while("Room".equals(usrName)){
            JOptionPane.showMessageDialog(null, "Nome Inválido!");
            usrName = JOptionPane.showInputDialog("Qual o nome do usuário?");
        }
        registry.bind(usrName, this);
    }

    @Override
    public void deliverMsg(String senderName, String msg, int id, Integer[] clockMatrix) {
        this.clockMatrix[id] = clockMatrix;
        if (!"Room".equals(senderName)) {
            this.clockMatrix[this.ID][id]++;
            buffer.add(new MsgBuffer(id, this.clockMatrix[this.ID][id], senderName, msg));
        }
        int cont = 0;
        for (int i = 0; i < clockMatrix.length; i++) {
            if (this.clockMatrix[id][i] > this.clockMatrix[this.ID][i]) {
                cont++;
            }
        }
        System.out.println("cont = " + cont);
        while (cont > 0) { // espera ordem causal          
            cont = 0;
            for (int i = 0; i < clockMatrix.length; i++) {
                if (this.clockMatrix[id][i] > this.clockMatrix[this.ID][i]) {
                    cont++;
                }
            }
        }       
        for (int j = 0; j < clockMatrix.length; j++) {
            cont = -1;
            while (cont < 0) {
                cont = 0;
                int menorIDMsgBuffer = menorIDMsgBuffer(j);
                for (int i = 0; i < clockMatrix.length; i++) {
                    if (this.clockMatrix[i][j] != null) {
                        if (this.clockMatrix[i][j] < menorIDMsgBuffer) {
                            cont++;
                        }
                    }
                }
                if (cont == 0) {
                    //tira do buffer a mensagem estabilizada do idSender j                    
                    for (int k = 0; k < buffer.size(); k++) {
                        if (buffer.get(k).idSender == j && buffer.get(k).idMsg == menorIDMsgBuffer) {
                            buffer.remove(k);
                            cont--;
                        }
                    }
                }
            }
        }
        userFrame.deliverToGUI(senderName, msg);
        System.out.println("Buffer de " + this.usrName + ": ");
        for (int i = 0; i < buffer.size(); i++) {
            System.out.println(buffer.get(i).senderName + ": " + buffer.get(i).msg + " idSender: " + buffer.get(i).idSender + " idMsg: " + buffer.get(i).idMsg);
        }
        if (buffer.isEmpty()) {
            System.out.println("buffer vazio");
        }
        System.out.println("Matriz de " + this.usrName);
        for (int i = 0; i < clockMatrix.length; i++) {
            for (int j = 0; j < clockMatrix.length; j++) {
                System.out.print(this.clockMatrix[i][j] + " ");
            }
            System.out.println("");
        }
    }

    @Override
    public void updateUserList(TreeMap<String, IUserChat> userList) throws RemoteException { 
        if (this.userList != null && (this.userList.size() > 1)) {
            System.out.println("entrou no if, buffer de " + this.usrName);
            while (!buffer.isEmpty()) {                         // estabiliza mensagens
                Set<String> lista = this.userList.keySet();
                for (String nome : lista) {
                    System.out.println("Mensagem de estabilização de " + nome + " para " + usrName);
                    System.out.println("Vetor enviado: ");
                    for (int i = 0; i < clockMatrix.length; i++) {
                        System.out.print(this.userList.get(nome).getClockVector()[i]);
                    }
                    System.out.println("");
                    this.deliverMsg("Room", "Estabilização de mensagens.", this.userList.get(nome).getID(), this.userList.get(nome).getClockVector());
                }
            }
            System.out.println("Saiu do while, buffer de " + this.usrName);
        }
        this.userList = userList;
    }

    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public Integer[] getClockVector() {
        return this.clockMatrix[this.ID];
    }

    public int menorIDMsgBuffer(int idSender) {
        int menorID = Integer.MAX_VALUE;
        for (int i = 0; i < this.buffer.size(); i++) {
            if (buffer.get(i).idSender == idSender) {
                if (buffer.get(i).idMsg < menorID) {
                    menorID = buffer.get(i).idMsg;
                }
            }
        }
        if (menorID == Integer.MAX_VALUE) {
            return -1;
        } else {
            return menorID;
        }
    }
}
