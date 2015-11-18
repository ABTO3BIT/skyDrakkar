//class version 3.3
package sockets;
import java.io.*;
import java.net.*;
//---------------------------------TCPServerSocket---------------------------------//
public class TCPServerSocket
{
  private ServerSocket ServerSocket=null;
  private InetAddress LocalHostAddress=null;
  private String LocalHost="";
  private int LocalPort=0;
  private int Queue=50;
  private boolean Open=false;
  //--------public----------
  public TCPServerSocket(){}
  public TCPServerSocket(int port){LocalPort=port;}
  public TCPServerSocket(int port,int queue){LocalPort=port;Queue=queue;}
  public TCPServerSocket(String host,int port){LocalHost=host;LocalPort=port;}
  public TCPServerSocket(String host,int port,int queue){LocalHost=host;LocalPort=port;Queue=queue;}
  //---is/get section---
  public ServerSocket getServerSocket(){return ServerSocket;}
  public String getLocalHost(){return LocalHost;}
  public String getSocketLocalAddress(){return ServerSocket.getInetAddress().toString();}
  public String getSocketLocalHostAddress(){return ServerSocket.getInetAddress().getHostAddress();}
  public String getSocketLocalHostName(){return ServerSocket.getInetAddress().getHostName();}
  public byte[] getSocketLocalByteAddress(){return ServerSocket.getInetAddress().getAddress();}
  public int getLocalPort(){return LocalPort;}
  public int getSocketLocalPort(){return ServerSocket.getLocalPort();}
  public int getQueue(){return Queue;}
  public InetAddress getLocalHostAddress(){return LocalHostAddress;}
  public int getTimeout()//accept timeout
  {
    int timeout=0;
    try{timeout=ServerSocket.getSoTimeout();}catch(IOException io_e){}
    return timeout;
  }
  public boolean isOpen(){return Open;}
  //---set section---
  public void setServerSocket(ServerSocket server_socket){ServerSocket=server_socket;}
  public void setLocalHost(String host){LocalHost=host;}
  public void setLocalPort(int port){LocalPort=port;}
  public void setLocalPort(String port){LocalPort=Integer.valueOf(port).intValue();}
  public void setQueue(int queue){Queue=queue;}
  public void setTimeout(int timeout){try{ServerSocket.setSoTimeout(timeout);}catch(IOException io_e){}}
  public void setOpen(boolean open){Open=open;}
  /////////////////////////////////////////////////
  public Socket accept()
  {
    Socket accept_socket=null;
    try{
      accept_socket=ServerSocket.accept();
    }catch(Exception io_e){}
    return accept_socket;
  }
  public boolean openServerSocket()
  {
    boolean is_open=false;
    try{
      if(LocalHost.trim().equals(""))ServerSocket=new ServerSocket(LocalPort,Queue);
      else{
        LocalHostAddress=InetAddress.getByName(LocalHost);//LocalHostAddress=InetAddress.getLocalHost();
        ServerSocket=new ServerSocket(LocalPort,Queue,LocalHostAddress);
      }
      is_open=true;
    }catch(IOException io_e){}
    return Open=is_open;
  }
  public boolean open()
  {
    boolean is_open=false;
    try{
      LocalHostAddress=InetAddress.getByName(LocalHost);
      ServerSocket=new ServerSocket(LocalPort,Queue,LocalHostAddress);
      is_open=true;
    }catch(IOException io_e){}
    return Open=is_open;
  }
  public boolean close()
  {
    boolean is_close=false;
    try{
      if(LocalHostAddress!=null)LocalHostAddress=null;
      if(ServerSocket!=null){ServerSocket.close();ServerSocket=null;}
      is_close=true;
    }catch(Exception io_e){}
    Open=!is_close;
    return is_close;
  }
}

