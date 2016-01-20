//class version 3.5
package sockets;
import java.io.*;
import java.net.*;
//-------------------------------------TCPSocket-----------------------------------//
public class TCPSocket
{
  private Socket socket=null;
  private OutputStream sendStream=null;
  private InputStream receiveStream=null;
  private byte[] packetBuffer=null;
  private InetAddress localHostAddress=null,remoteHostAddress=null;
  private String localHost="",remoteHost="";
  private int localPort=0,remotePort=0;
  private boolean connect=false;
  private int receiveBufferSize=8192;//(8 Kbytes)
  //--------public----------
  public TCPSocket(){}
  public TCPSocket(Socket socket){this.socket=socket;}
  public TCPSocket(String host,int port){remoteHost=host;remotePort=port;}
  public TCPSocket(String local_host,int local_port,String remote_host,int remote_port)
  {
    localHost=local_host;localPort=local_port;
    remoteHost=remote_host;remotePort=remote_port;
  }
  //---is/get section---
  public Socket getSocket(){return socket;}
  public OutputStream getSendStream(){return sendStream;}
  public InputStream getReceiveStream(){return receiveStream;}
  public String getLocalHost(){return localHost;}
  public String getSocketLocalAddress(){return socket.getLocalAddress().toString();}
  public String getSocketLocalHostAddress(){return socket.getLocalAddress().getHostAddress();}
  public String getSocketLocalHostName(){return socket.getLocalAddress().getHostName();}
  public byte[] getSocketLocalByteAddress(){return socket.getLocalAddress().getAddress();}
  public String getRemoteHost(){return remoteHost;}
  public String getSocketRemoteAddress(){return socket.getInetAddress().toString();}
  public String getSocketRemoteHostAddress(){return socket.getInetAddress().getHostAddress();}
  public String getSocketRemoteHostName(){return socket.getInetAddress().getHostName();}
  public byte[] getSocketRemoteByteAddress(){return socket.getInetAddress().getAddress();}
  public int getLocalPort(){return localPort;}
  public int getSocketLocalPort(){return socket.getLocalPort();}
  public int getRemotePort(){return remotePort;}
  public int getSocketRemotePort(){return socket.getPort();}
  public InetAddress getLocalHostAddress(){return localHostAddress;}
  public InetAddress getRemoteHostAddress(){return remoteHostAddress;}
  public String getPacketToString()//get packetBuffer to string
  {
    String packet_string="";//null
    try{
      ByteArrayOutputStream packet_stream=new ByteArrayOutputStream();
      packet_stream.write(packetBuffer);
      packet_string=packet_stream.toString();
      packet_stream.close();packet_stream=null;
    }catch(IOException io_e){}
    return packet_string;
  }
  public byte[] getPacketBuffer(){return packetBuffer;}
  public int getTimeout()
  {
    int timeout=0;
    try{timeout=socket.getSoTimeout();}catch(IOException io_e){}
    return timeout;
  }
  public boolean getTcpNoDelay()
  {
    boolean is_no_delay=false;
    try{is_no_delay=socket.getTcpNoDelay();}catch(IOException io_e){}
    return is_no_delay;
  }
  public boolean isConnect(){return connect;}
  public int getReceiveBufferSize(){return receiveBufferSize;}
  //---set section---
  public void setSocket(Socket socket){this.socket=socket;}
  public void setSendStream(OutputStream stream){sendStream=stream;}
  public void setReceiveStream(InputStream stream){receiveStream=stream;}
  public void setLocalHost(String host){localHost=host;}
  public void setRemoteHost(String host){remoteHost=host;}
  public void setLocalPort(int port){localPort=port;}
  public void setLocalPort(String port){localPort=Integer.valueOf(port).intValue();}
  public void setRemotePort(int port){remotePort=port;}
  public void setRemotePort(String port){remotePort=Integer.valueOf(port).intValue();}
  public void setPacketBuffer(String string){packetBuffer=string.getBytes();}
  public void setPacketBuffer(byte[] buffer){packetBuffer=buffer;}
  public void setPacketBuffer(byte[] buffer,int start_index,int finish_index)
  {
    ByteArrayOutputStream byte_stream=null;
    try{
      byte_stream=new ByteArrayOutputStream();
      byte_stream.write(buffer,start_index,finish_index);
      packetBuffer=byte_stream.toByteArray();
      byte_stream.close();byte_stream=null;
    }catch(IOException io_e){}
  }
  public void setTimeout(int timeout){try{socket.setSoTimeout(timeout);}catch(IOException io_e){}}
  public void setTcpNoDelay(boolean no_delay){try{socket.setTcpNoDelay(no_delay);}catch(IOException io_e){}}
  public void setConnect(boolean connect){this.connect=connect;}
  public void setReceiveBufferSize(int size){receiveBufferSize=size;}
  /////////////////////////////////////////////////
  public boolean send()
  {
    boolean is_send=false;
    try{
      if(sendStream!=null){sendStream.write(packetBuffer);is_send=true;}
      else connect=false;
    }catch(IOException io_e){}
    return is_send;
  }
  public boolean send(String buffer){setPacketBuffer(buffer);return send();}
  public boolean send(byte[] buffer){setPacketBuffer(buffer);return send();}
  public boolean send(byte[] buffer,int start_index,int finish_index)
  {
    setPacketBuffer(buffer,start_index,finish_index);
    return send();
  }
  public String recvLine()
  {
    String line="";//null
    packetBuffer=null;
    try{
      ByteArrayOutputStream receive_stream=new ByteArrayOutputStream();
      byte read_byte;
      try{
        do{
          if(receiveStream==null){connect=false;break;}
          read_byte=(byte)receiveStream.read();
          if(read_byte==-1){connect=false;break;}
          if(read_byte!='\r'&&read_byte!='\n')receive_stream.write(read_byte);
        }while(read_byte!='\n');
      }catch(IOException io_e){}
      line=receive_stream.toString();
      packetBuffer=line.getBytes();
      receive_stream.close();receive_stream=null;
    }catch(IOException io_e){}
    return line;
  }
  public int recvBuffer(int buffer_size)
  {
    int read_bytes=0;
    packetBuffer=null;
    if(receiveStream==null){connect=false;return read_bytes;}
    packetBuffer=new byte[buffer_size];
    try{read_bytes=receiveStream.read(packetBuffer);}catch(IOException io_e){packetBuffer=null;}
    if(read_bytes==-1){packetBuffer=null;connect=false;}
    return read_bytes;
  }
  public int recvBuffer()
  {
    int read_bytes=0;
    packetBuffer=null;
    if(receiveStream==null){connect=false;return read_bytes;}
    packetBuffer=new byte[receiveBufferSize];
    try{read_bytes=receiveStream.read(packetBuffer);}catch(IOException io_e){packetBuffer=null;}
    if(read_bytes==-1){packetBuffer=null;connect=false;}
    return read_bytes;
  }
  public int recvAvailableBuffer()
  {
    int can_read_bytes=0,read_bytes=0;
    packetBuffer=null;
    if(receiveStream==null){connect=false;return read_bytes;}
    try{can_read_bytes=receiveStream.available();}catch(IOException io_e){}
    if(can_read_bytes==0){return read_bytes;}
    packetBuffer=new byte[can_read_bytes];
    try{read_bytes=receiveStream.read(packetBuffer);}catch(IOException io_e){packetBuffer=null;}
    if(read_bytes==-1){packetBuffer=null;connect=false;}
    return read_bytes;
  }
  public boolean recv(int buffer_size)
  {
    boolean is_receive=false;
    packetBuffer=null;
    try{
      int read_bytes,total_read_bytes=0,left_read_bytes;
      ByteArrayOutputStream receive_stream=new ByteArrayOutputStream();
      byte[] receive_bytes=new byte[buffer_size];
      try{
        do{
          if(receiveStream==null){connect=false;break;}
          read_bytes=receiveStream.read(receive_bytes);
          if(read_bytes==-1){connect=false;break;}
          receive_stream.write(receive_bytes,0,read_bytes);
          total_read_bytes+=read_bytes;
          left_read_bytes=buffer_size-total_read_bytes;
          receive_bytes=null;
          receive_bytes=new byte[left_read_bytes];
        }while(total_read_bytes<buffer_size);
      }catch(IOException io_e){}
      packetBuffer=receive_stream.toByteArray();
      if(packetBuffer!=null&&packetBuffer.length==buffer_size)is_receive=true;
      receive_stream.close();receive_stream=null;receive_bytes=null;
    }catch(IOException io_e){}
    return is_receive;
  }
  public boolean recv()
  {
    boolean is_receive=false;
    packetBuffer=null;
    try{
      int read_bytes;
      ByteArrayOutputStream receive_stream=new ByteArrayOutputStream();
      byte[] receive_bytes=new byte[receiveBufferSize];
      try{
        do{
          if(receiveStream==null){connect=false;break;}
          read_bytes=receiveStream.read(receive_bytes);
          if(read_bytes==-1){connect=false;break;}else if(read_bytes==0)break;
          receive_stream.write(receive_bytes,0,read_bytes);
          if(receiveStream!=null&&receiveStream.available()==0)break;// <!>
        }while(true);
      }catch(IOException io_e){}
      packetBuffer=receive_stream.toByteArray();
      if(packetBuffer!=null&&packetBuffer.length>0)is_receive=true;
      receive_stream.close();receive_stream=null;receive_bytes=null;
    }catch(IOException io_e){}
    return is_receive;
  }
  public boolean openSocket()
  {
    boolean is_open=false;
    try{
      if(socket==null){
        if(remoteHost.trim().equals(""))remoteHostAddress=InetAddress.getLocalHost();
        else remoteHostAddress=InetAddress.getByName(remoteHost);
        if(localHost.trim().equals(""))socket=new Socket(remoteHostAddress,remotePort);
        else{
          localHostAddress=InetAddress.getByName(localHost);//LocalHostAddress=InetAddress.getLocalHost();
          socket=new Socket(remoteHostAddress,remotePort,localHostAddress,localPort);
        }
      }
      sendStream=socket.getOutputStream();
      receiveStream=socket.getInputStream();
      packetBuffer=null;
      is_open=true;
    }catch(IOException io_e){}
    connect=is_open;
    return is_open;
  }
  public boolean open()
  {
    boolean is_open=false;
    try{
      if(socket==null){
        remoteHostAddress=InetAddress.getByName(remoteHost);
        socket=new Socket(remoteHostAddress,remotePort);
      }
      sendStream=socket.getOutputStream();
      receiveStream=socket.getInputStream();
      packetBuffer=null;
      is_open=true;
    }catch(IOException io_e){}
    connect=is_open;
    return is_open;
  }
  public boolean open(Socket socket){this.socket=socket;return open();}
  public boolean close()
  {
    boolean is_close=false;
    try{
      if(socket!=null){socket.close();socket=null;}
      if(sendStream!=null){sendStream.close();sendStream=null;}
      if(receiveStream!=null){receiveStream.close();receiveStream=null;}
      if(packetBuffer!=null)packetBuffer=null;
      if(remoteHostAddress!=null)remoteHostAddress=null;
      if(localHostAddress!=null)localHostAddress=null;
      is_close=true;
    }catch(IOException io_e){}
    connect=!is_close;
    return is_close;
  }
}
