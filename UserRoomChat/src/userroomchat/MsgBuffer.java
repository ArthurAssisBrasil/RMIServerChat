/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userroomchat;

/**
 *
 * @author Arthur
 */
public class MsgBuffer {
    int idSender;
    Integer idMsg;
    String msg, senderName;
    
    MsgBuffer(int idSender, Integer idMsg, String senderName, String msg){       
        this.idSender = idSender;
        this.idMsg = idMsg;
        this.senderName = senderName;
        this.msg = msg;
    }
    
}
