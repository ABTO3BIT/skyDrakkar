//class version 3.5
package sockets;
import java.io.*;
import java.net.*;
//-------------------------------------TCPSocket-----------------------------------//
public class TCPSocket
{
  private Socket Socket=null;
  private OutputStream SendStream=null;
  private InputStream ReceiveStream=null;
  private byte[] PacketBuffer=null;
  private InetAddress LocalHostAddress=null,RemoteHostAddress=null;
  private String LocalHost="",RemoteHost="";
  private int LocalPort=0,RemotePort=0;
  private boolean Connect=false;
  private int ReceiveBufferSize=8192;//(8 Kbytes)
  //--------public----------
  public TCPSocket(){}
  public TCPSocket(Socket socket){Socket=socket;}
  public TCPSocket(String host,int port){RemoteHost=host;RemotePort=port;}
  public TCPSocket(String local_host,int local_port,String remote_host,int remote_port)
  {
    LocalHost=local_host;LocalPort=local_port;
    RemoteHost=remote_host;RemotePort=remote_port;
  }
  //---is/get section---
  public Socket getSocket(){return Socket;}
  public OutputStream getSendStream(){return SendStream;}
  public InputStream getReceiveStream(){return ReceiveStream;}
  public String getLocalHost(){return LocalHost;}
  public String getSocketLocalAddress(){return Socket.getLocalAddress().toString();}
  public String getSocketLocalHostAddress(){return Socket.getLocalAddress().getHostAddress();}
  public String getSocketLocalHostName(){return Socket.getLocalAddress().getHostName();}
  public byte[] getSocketLocalByteAddress(){return Socket.getLocalAddress().getAddress();}
  public String getRemoteHost(){return RemoteHost;}
  public String getSocketRemoteAddress(){return Socket.getInetAddress().toString();}
  public String getSocketRemoteHostAddress(){return Socket.getInetAddress().getHostAddress();}
  public String getSocketRemoteHostName(){return Socket.getInetAddress().getHostName();}
  public byte[] getSocketRemoteByteAddress(){return Socket.getInetAddress().getAddress();}
  public int getLocalPort(){return LocalPort;}
  public int getSocketLocalPort(){return Socket.getLocalPort();}
  public int getRemotePort(){return RemotePort;}
  public int getSocketRemotePort(){return Socket.getPort();}
  public InetAddress getLocalHostAddress(){return LocalHostAddress;}
  public InetAddress getRemoteHostAddress(){return RemoteHostAddress;}
  public String getPacketToString()//get PacketBuffer to string
  {
    String packet_string="";//null
    try{
      ByteArrayOutputStream packet_stream=new ByteArrayOutputStream();
      packet_stream.write(PacketBuffer);
      packet_string=packet_stream.toString();
      packet_stream.close();packet_stream=null;
    }catch(IOException io_e){}
    return packet_string;
  }
  public byte[] getPacketBuffer(){return PacketBuffer;}
  public int getTimeout()
  {
    int timeout=0;
    try{timeout=Socket.getSoTimeout();}catch(IOException io_e){}
    return timeout;
  }
  public boolean getTcpNoDelay()
  {
    boolean is_no_delay=false;
    try{is_no_delay=Socket.getTcpNoDelay();}catch(IOException io_e){}
    return is_no_delay;
  }
  public boolean isConnect(){return Connect;}
  public int getReceiveBufferSize(){return ReceiveBufferSize;}
  //---set section---
  public void setSocket(Socket socket){Socket=socket;}
  public void setSendStream(OutputStream stream){SendStream=stream;}
  public void setReceiveStream(InputStream stream){ReceiveStream=stream;}
  public void setLocalHost(String host){LocalHost=host;}
  public void setRemoteHost(String host){RemoteHost=host;}
  public void setLocalPort(int port){LocalPort=port;}
  public void setLocalPort(String port){LocalPort=Integer.valueOf(port).intValue();}
  public void setRemotePort(int port){RemotePort=port;}
  public void setRemotePort(String port){RemotePort=Integer.valueOf(port).intValue();}
  public void setPacketBuffer(String string){PacketBuffer=string.getBytes();}
  public void setPacketBuffer(byte[] buffer){PacketBuffer=buffer;}
  public void setPacketBuffer(byte[] buffer,int start_index,int finish_index)
  {
    ByteArrayOutputStream byte_stream=null;
    try{
      byte_stream=new ByteArrayOutputStream();
      byte_stream.write(buffer,start_index,finish_index);
      PacketBuffer=byte_stream.toByteArray();
      byte_stream.close();byte_stream=null;
    }catch(IOException io_e){}
  }
  public void setTimeout(int timeout){try{Socket.setSoTimeout(timeout);}catch(IOException io_e){}}
  public void setTcpNoDelay(boolean no_delay){try{Socket.setTcpNoDelay(no_delay);}catch(IOException io_e){}}
  public void setConnect(boolean connect){Connect=connect;}
  public void setReceiveBufferSize(int size){ReceiveBufferSize=size;}
  /////////////////////////////////////////////////
  public boolean send()
  {
    boolean is_send=false;
    try{
      if(SendStream!=null){SendStream.write(PacketBuffer);is_send=true;}
      else Connect=false;
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
    PacketBuffer=null;
    try{
      ByteArrayOutputStream receive_stream=new ByteArrayOutputStream();
      byte read_byte;
      try{
        do{
          if(ReceiveStream==null){Connect=false;break;}
          read_byte=(byte)ReceiveStream.read();
          if(read_byte==-1){Connect=false;break;}
          if(read_byte!='\r'&&read_byte!='\n')receive_stream.write(read_byte);
        }while(read_byte!='\n');
      }catch(IOException io_e){}
      line=receive_stream.toString();
      PacketBuffer=line.getBytes();
      receive_stream.close();receive_stream=null;
    }catch(IOException io_e){}
    return line;
  }
  public int recvBuffer(int buffer_size)
  {
    int read_bytes=0;
    PacketBuffer=null;
    if(ReceiveStream==null){Connect=false;return read_bytes;}
    PacketBuffer=new byte[buffer_size];
    try{read_bytes=ReceiveStream.read(PacketBuffer);}catch(IOException io_e){PacketBuffer=null;}
    if(read_bytes==-1){PacketBuffer=null;Connect=false;}
    return read_bytes;
  }
  public int recvBuffer()
  {
    int read_bytes=0;
    PacketBuffer=null;
    if(ReceiveStream==null){Connect=false;return read_bytes;}
    PacketBuffer=new byte[ReceiveBufferSize];
    try{read_bytes=ReceiveStream.read(PacketBuffer);}catch(IOException io_e){PacketBuffer=null;}
    if(read_bytes==-1){PacketBuffer=null;Connect=false;}
    return read_bytes;
  }
  public int recvAvailableBuffer()
  {
    int can_read_bytes=0,read_bytes=0;
    PacketBuffer=null;
    if(ReceiveStream==null){Connect=false;return read_bytes;}
    try{can_read_bytes=ReceiveStream.available();}catch(IOException io_e){}
    if(can_read_bytes==0){return read_bytes;}
    PacketBuffer=new byte[can_read_bytes];
    try{read_bytes=ReceiveStream.read(PacketBuffer);}catch(IOException io_e){PacketBuffer=null;}
    if(read_bytes==-1){PacketBuffer=null;Connect=false;}
    return read_bytes;
  }
  public boolean recv(int buffer_size)
  {
    boolean is_receive=false;
    PacketBuffer=null;
    try{
      int read_bytes,total_read_bytes=0,left_read_bytes;
      ByteArrayOutputStream receive_stream=new ByteArrayOutputStream();
      byte[] receive_bytes=new byte[buffer_size];
      try{
        do{
          if(ReceiveStream==null){Connect=false;break;}
          read_bytes=ReceiveStream.read(receive_bytes);
          if(read_bytes==-1){Connect=false;break;}
          receive_stream.write(receive_bytes,0,read_bytes);
          total_read_bytes+=read_bytes;
          left_read_bytes=buffer_size-total_read_bytes;
          receive_bytes=null;
          receive_bytes=new byte[left_read_bytes];
        }while(total_read_bytes<buffer_size);
      }catch(IOException io_e){}
      PacketBuffer=receive_stream.toByteArray();
      if(PacketBuffer!=null&&PacketBuffer.length==buffer_size)is_receive=true;
      receive_stream.close();receive_stream=null;receive_bytes=null;
    }catch(IOException io_e){}
    return is_receive;
  }
  public boolean recv()
  {
    boolean is_receive=false;
    PacketBuffer=null;
    try{
      int read_bytes;
      ByteArrayOutputStream receive_stream=new ByteArrayOutputStream();
      byte[] receive_bytes=new byte[ReceiveBufferSize];
      try{
        do{
          if(ReceiveStream==null){Connect=false;break;}
          read_bytes=ReceiveStream.read(receive_bytes);
          if(read_bytes==-1){Connect=false;break;}else if(read_bytes==0)break;
          receive_stream.write(receive_bytes,0,read_bytes);
          if(ReceiveStream!=null&&ReceiveStream.available()==0)break;// <!>
        }while(true);
      }catch(IOException io_e){}
      PacketBuffer=receive_stream.toByteArray();
      if(PacketBuffer!=null&&PacketBuffer.length>0)is_receive=true;
      receive_stream.close();receive_stream=null;receive_bytes=null;
    }catch(IOException io_e){}
    return is_receive;
  }
  public boolean openSocket()
  {
    boolean is_open=false;
    try{
      if(Socket==null){
        if(RemoteHost.trim().equals(""))RemoteHostAddress=InetAddress.getLocalHost();
        else RemoteHostAddress=InetAddress.getByName(RemoteHost);
        if(LocalHost.trim().equals(""))Socket=new Socket(RemoteHostAddress,RemotePort);
        else{
          LocalHostAddress=InetAddress.getByName(LocalHost);//LocalHostAddress=InetAddress.getLocalHost();
          Socket=new Socket(RemoteHostAddress,RemotePort,LocalHostAddress,LocalPort);
        }
      }
      SendStream=Socket.getOutputStream();
      ReceiveStream=Socket.getInputStream();
      PacketBuffer=null;
      is_open=true;
    }catch(IOException io_e){}
    Connect=is_open;
    return is_open;
  }
  public boolean open()
  {
    boolean is_open=false;
    try{
      if(Socket==null){
        RemoteHostAddress=InetAddress.getByName(RemoteHost);
        Socket=new Socket(RemoteHostAddress,RemotePort);
      }
      SendStream=Socket.getOutputStream();
      ReceiveStream=Socket.getInputStream();
      PacketBuffer=null;
      is_open=true;
    }catch(IOException io_e){}
    Connect=is_open;
    return is_open;
  }
  public boolean open(Socket socket){Socket=socket;return open();}
  public boolean close()
  {
    boolean is_close=false;
    try{
      if(Socket!=null){Socket.close();Socket=null;}
      if(SendStream!=null){SendStream.close();SendStream=null;}
      if(ReceiveStream!=null){ReceiveStream.close();ReceiveStream=null;}
      if(PacketBuffer!=null)PacketBuffer=null;
      if(RemoteHostAddress!=null)RemoteHostAddress=null;
      if(LocalHostAddress!=null)LocalHostAddress=null;
      is_close=true;
    }catch(IOException io_e){}
    Connect=!is_close;
    return is_close;
  }
}
