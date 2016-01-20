//class version 3.3
package sockets;
import java.io.*;
import java.net.*;
//---------------------------------TCPServerSocket---------------------------------//
public class TCPServerSocket
{
  private ServerSocket serverSocket=null;
  private InetAddress localHostAddress=null;
  private String localHost="";
  private int localPort=0;
  private int queue=50;
  private boolean open=false;
  //--------public----------
  public TCPServerSocket(){}
  public TCPServerSocket(int port){localPort=port;}
  public TCPServerSocket(int port,int queue){localPort=port;this.queue=queue;}
  public TCPServerSocket(String host,int port){localHost=host;localPort=port;}
  public TCPServerSocket(String host,int port,int queue){localHost=host;localPort=port;this.queue=queue;}
  //---is/get section---
  public ServerSocket getServerSocket(){return serverSocket;}
  public String getLocalHost(){return localHost;}
  public String getSocketLocalAddress(){return serverSocket.getInetAddress().toString();}
  public String getSocketLocalHostAddress(){return serverSocket.getInetAddress().getHostAddress();}
  public String getSocketLocalHostName(){return serverSocket.getInetAddress().getHostName();}
  public byte[] getSocketLocalByteAddress(){return serverSocket.getInetAddress().getAddress();}
  public int getLocalPort(){return localPort;}
  public int getSocketLocalPort(){return serverSocket.getLocalPort();}
  public int getQueue(){return queue;}
  public InetAddress getLocalHostAddress(){return localHostAddress;}
  public int getTimeout()//accept timeout
  {
    int timeout=0;
    try{timeout=serverSocket.getSoTimeout();}catch(IOException io_e){}
    return timeout;
  }
  public boolean isOpen(){return open;}
  //---set section---
  public void setServerSocket(ServerSocket server_socket){serverSocket=server_socket;}
  public void setLocalHost(String host){localHost=host;}
  public void setLocalPort(int port){localPort=port;}
  public void setLocalPort(String port){localPort=Integer.valueOf(port).intValue();}
  public void setQueue(int queue){this.queue=queue;}
  public void setTimeout(int timeout){try{serverSocket.setSoTimeout(timeout);}catch(IOException io_e){}}
  public void setOpen(boolean open){this.open=open;}
  /////////////////////////////////////////////////
  public Socket accept()
  {
    Socket accept_socket=null;
    try{
      accept_socket=serverSocket.accept();
    }catch(Exception io_e){}
    return accept_socket;
  }
  public boolean openServerSocket()
  {
    boolean is_open=false;
    try{
      if(localHost.trim().equals(""))serverSocket=new ServerSocket(localPort,queue);
      else{
        localHostAddress=InetAddress.getByName(localHost);//LocalHostAddress=InetAddress.getLocalHost();
        serverSocket=new ServerSocket(localPort,queue,localHostAddress);
      }
      is_open=true;
    }catch(IOException io_e){}
    return open=is_open;
  }
  public boolean open()
  {
    boolean is_open=false;
    try{
      localHostAddress=InetAddress.getByName(localHost);
      serverSocket=new ServerSocket(localPort,queue,localHostAddress);
      is_open=true;
    }catch(IOException io_e){}
    return open=is_open;
  }
  public boolean close()
  {
    boolean is_close=false;
    try{
      if(localHostAddress!=null)localHostAddress=null;
      if(serverSocket!=null){serverSocket.close();serverSocket=null;}
      is_close=true;
    }catch(Exception io_e){}
    open=!is_close;
    return is_close;
  }
}

