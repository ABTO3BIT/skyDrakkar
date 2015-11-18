// class version 3.3
package sockets;

import java.io.*;
import java.net.*;

public class UDPSocket
{
  private DatagramSocket Socket=null;
  private DatagramPacket Packet=null;
  private byte[] PacketBuffer=null;
  private InetAddress LocalHostAddress=null,RemoteHostAddress=null;
  private String LocalHost="",RemoteHost="";
  private int LocalPort=0,RemotePort=0;
  private int PacketBufferSize=1024;
  private boolean Open=false;
  //--------public----------
  public UDPSocket(){}
  public UDPSocket(int port){LocalPort=port;}
  public UDPSocket(String host,int port){LocalHost=host;LocalPort=port;}
  public UDPSocket(String local_host,int local_port,String remote_host,int remote_port)
  {
    LocalHost=local_host;LocalPort=local_port;
    RemoteHost=remote_host;RemotePort=remote_port;
  }
  //---is/get section---
  public DatagramSocket getSocket(){return Socket;}
  public String getLocalHost(){return LocalHost;}
  public String getRemoteHost(){return RemoteHost;}
  public String getSocketLocalAddress(){return Socket.getLocalAddress().toString();}
  public String getSocketLocalHostAddress(){return Socket.getLocalAddress().getHostAddress();}
  public String getSocketLocalHostName(){return Socket.getLocalAddress().getHostName();}
  public byte[] getSocketLocalByteAddress(){return Socket.getLocalAddress().getAddress();}
  public String getPacketRemoteAddress(){return Packet.getAddress().toString();}
  public String getPacketRemoteHostAddress(){return Packet.getAddress().getHostAddress();}
  public String getPacketRemoteHostName(){return Packet.getAddress().getHostName();}
  public byte[] getPacketRemoteByteAddress(){return Packet.getAddress().getAddress();}
  public int getLocalPort(){ return LocalPort;}
  public int getSocketLocalPort(){return Socket.getLocalPort();}
  public int getRemotePort(){return RemotePort;}
  public int getPacketRemotePort(){return Packet.getPort();}
  public InetAddress getLocalHostAddress(){return LocalHostAddress;}
  public InetAddress getRemoteHostAddress(){return RemoteHostAddress;}
  public String getPacketToString()
  {
    String packet_string="";//null
    try{
      ByteArrayOutputStream packet_stream=new ByteArrayOutputStream();
      packet_stream.write(PacketBuffer,0,PacketBuffer.length);
      packet_string=packet_stream.toString();
      packet_stream.close();packet_stream=null;
    }catch(IOException io_e){}
    return packet_string;
  }
  public byte[] getPacketBuffer(){return PacketBuffer;}
  public byte[] getPacketData(){return Packet.getData();}
  public int getPacketBufferSize(){return PacketBufferSize;}
  public int getPacketLength(){return Packet.getLength();}
  public int getTimeout()
  {
    int timeout=0;
    try{timeout=Socket.getSoTimeout();}catch(IOException io_e){}
    return timeout;
  }
  public boolean isOpen(){return Open;}
  //---set section---
  public void setSocket(DatagramSocket socket){Socket=socket;}
  public void setLocalHost(String host){LocalHost=host;}
  public void setRemoteHost(String host){RemoteHost=host;}
  public void setLocalPort(int port){LocalPort=port;}
  public void setLocalPort(String port){LocalPort=Integer.valueOf(port).intValue();}
  public void setRemotePort(int port){RemotePort=port;}
  public void setRemotePort(String port){RemotePort=Integer.valueOf(port).intValue();}
  public void setPacketBuffer(String buffer){PacketBuffer=buffer.getBytes();}
  public void setPacketBuffer(byte[] buffer){PacketBuffer=buffer;}
  public void setPacketBufferSize(int size){PacketBufferSize=size;}
  public void setRemoteHostAddress(InetAddress address){RemoteHostAddress=address;}
  public InetAddress setRemoteHostAddress(String host)
  {
    RemoteHostAddress=null;
    RemoteHost=host;
    try{RemoteHostAddress=InetAddress.getByName(RemoteHost);}catch(UnknownHostException u_h_e){}
    return RemoteHostAddress;
  }
  public void setReply()
  {
    InetAddress address=Packet.getAddress();
    RemoteHost=address.getHostAddress();
    RemotePort=Packet.getPort();
  }
  public void setTimeout(int timeout){try{Socket.setSoTimeout(timeout);}catch(IOException io_e){}}
  /////////////////////////////////////////////////
  public boolean send()
  {
    boolean is_send=false;
    try{
      Packet.setAddress(RemoteHostAddress);
      Packet.setPort(RemotePort);
      Packet.setData(PacketBuffer);
      Packet.setLength(PacketBuffer.length);
      Socket.send(Packet);
      is_send=true;
    }catch(IOException io_e){}
    return is_send;
  }
  public boolean send(byte[] buffer)
  {
    boolean is_send=false;
    try{
      Packet.setAddress(RemoteHostAddress);
      Packet.setPort(RemotePort);
      Packet.setData(buffer);
      Packet.setLength(buffer.length);
      Socket.send(Packet);
      is_send=true;
    }catch(IOException io_e){}
    return is_send;
  }
  public boolean send(byte[] buffer,int index,int count)
  {
    boolean is_send=false;
    try{
      Packet.setAddress(RemoteHostAddress);
      Packet.setPort(RemotePort);
      Packet.setData(buffer,index,count);
      Packet.setLength(count);
      Socket.send(Packet);
      is_send=true;
    }catch(IOException io_e){}
    return is_send;
  }
  public boolean recv()
  {
    boolean is_receive=false;
    PacketBuffer=null;
    try{
      PacketBuffer=new byte[PacketBufferSize];
      Packet.setData(PacketBuffer);
      Packet.setLength(PacketBuffer.length);
      Socket.receive(Packet);
      is_receive=true;
    }catch(IOException io_e){}
    return is_receive;
  }
  public boolean openSocket()
  {
    boolean is_open=false;
    try{
      if(!LocalHost.trim().equals("")){
        LocalHostAddress=InetAddress.getByName(LocalHost);
        Socket=new DatagramSocket(LocalPort,LocalHostAddress);
      }
      else Socket=new DatagramSocket(LocalPort);
      PacketBufferSize=Socket.getReceiveBufferSize();
      PacketBuffer=new byte[0];
      Packet=new DatagramPacket(PacketBuffer,PacketBuffer.length);
      is_open=true;
    }catch(IOException io_e){}
    return Open=is_open;
  }
  public boolean openLocal()
  {
    boolean is_open=false;
    try{
      Socket=new DatagramSocket();
      PacketBufferSize=Socket.getReceiveBufferSize();
      PacketBuffer=new byte[0];
      Packet=new DatagramPacket(PacketBuffer,PacketBuffer.length);
      is_open=true;
    }catch(IOException io_e){}
    return Open=is_open;
  }
  public boolean open()
  {
    boolean is_open=false;
    try{
      LocalHostAddress=InetAddress.getByName(LocalHost);
      Socket=new DatagramSocket(LocalPort,LocalHostAddress);
      PacketBufferSize=Socket.getReceiveBufferSize();
      PacketBuffer=new byte[0];
      Packet=new DatagramPacket(PacketBuffer,PacketBuffer.length);
      is_open=true;
    }catch(IOException io_e){}
    return Open=is_open;
  }
  public boolean close()
  {
    boolean is_close=false;
    if(Socket!=null){Socket.close();Socket=null;}
    if(Packet!=null)Packet=null;
    if(PacketBuffer!=null){PacketBuffer=null;}
    if(RemoteHostAddress!=null)RemoteHostAddress=null;
    if(LocalHostAddress!=null)LocalHostAddress=null;
    is_close=true;
    Open=!is_close;
    return is_close;
  }
}
