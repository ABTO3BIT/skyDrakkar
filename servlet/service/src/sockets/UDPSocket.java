// class version 3.3
package sockets;

import java.io.*;
import java.net.*;

public class UDPSocket
{
  private DatagramSocket socket=null;
  private DatagramPacket packet=null;
  private byte[] packetBuffer=null;
  private InetAddress localHostAddress=null,remoteHostAddress=null;
  private String localHost="",remoteHost="";
  private int localPort=0,remotePort=0;
  private int packetBufferSize=1024;
  private boolean open=false;
  //--------public----------
  public UDPSocket(){}
  public UDPSocket(int port){localPort=port;}
  public UDPSocket(String host,int port){localHost=host;localPort=port;}
  public UDPSocket(String local_host,int local_port,String remote_host,int remote_port)
  {
    localHost=local_host;localPort=local_port;
    remoteHost=remote_host;remotePort=remote_port;
  }
  //---is/get section---
  public DatagramSocket getSocket(){return socket;}
  public String getLocalHost(){return localHost;}
  public String getRemoteHost(){return remoteHost;}
  public String getSocketLocalAddress(){return socket.getLocalAddress().toString();}
  public String getSocketLocalHostAddress(){return socket.getLocalAddress().getHostAddress();}
  public String getSocketLocalHostName(){return socket.getLocalAddress().getHostName();}
  public byte[] getSocketLocalByteAddress(){return socket.getLocalAddress().getAddress();}
  public String getPacketRemoteAddress(){return packet.getAddress().toString();}
  public String getPacketRemoteHostAddress(){return packet.getAddress().getHostAddress();}
  public String getPacketRemoteHostName(){return packet.getAddress().getHostName();}
  public byte[] getPacketRemoteByteAddress(){return packet.getAddress().getAddress();}
  public int getLocalPort(){ return localPort;}
  public int getSocketLocalPort(){return socket.getLocalPort();}
  public int getRemotePort(){return remotePort;}
  public int getPacketRemotePort(){return packet.getPort();}
  public InetAddress getLocalHostAddress(){return localHostAddress;}
  public InetAddress getRemoteHostAddress(){return remoteHostAddress;}
  public String getPacketToString()
  {
    String packet_string="";//null
    try{
      ByteArrayOutputStream packet_stream=new ByteArrayOutputStream();
      packet_stream.write(packetBuffer,0,packetBuffer.length);
      packet_string=packet_stream.toString();
      packet_stream.close();packet_stream=null;
    }catch(IOException io_e){}
    return packet_string;
  }
  public byte[] getPacketBuffer(){return packetBuffer;}
  public byte[] getPacketData(){return packet.getData();}
  public int getPacketBufferSize(){return packetBufferSize;}
  public int getPacketLength(){return packet.getLength();}
  public int getTimeout()
  {
    int timeout=0;
    try{timeout=socket.getSoTimeout();}catch(IOException io_e){}
    return timeout;
  }
  public boolean isOpen(){return open;}
  //---set section---
  public void setSocket(DatagramSocket socket){this.socket=socket;}
  public void setLocalHost(String host){localHost=host;}
  public void setRemoteHost(String host){remoteHost=host;}
  public void setLocalPort(int port){localPort=port;}
  public void setLocalPort(String port){localPort=Integer.valueOf(port).intValue();}
  public void setRemotePort(int port){remotePort=port;}
  public void setRemotePort(String port){remotePort=Integer.valueOf(port).intValue();}
  public void setPacketBuffer(String buffer){packetBuffer=buffer.getBytes();}
  public void setPacketBuffer(byte[] buffer){packetBuffer=buffer;}
  public void setPacketBufferSize(int size){packetBufferSize=size;}
  public void setRemoteHostAddress(InetAddress address){remoteHostAddress=address;}
  public InetAddress setRemoteHostAddress(String host)
  {
    remoteHostAddress=null;
    remoteHost=host;
    try{remoteHostAddress=InetAddress.getByName(remoteHost);}catch(UnknownHostException u_h_e){}
    return remoteHostAddress;
  }
  public void setReply()
  {
    InetAddress address=packet.getAddress();
    remoteHost=address.getHostAddress();
    remotePort=packet.getPort();
  }
  public void setTimeout(int timeout){try{socket.setSoTimeout(timeout);}catch(IOException io_e){}}
  /////////////////////////////////////////////////
  public boolean send()
  {
    boolean is_send=false;
    try{
      packet.setAddress(remoteHostAddress);
      packet.setPort(remotePort);
      packet.setData(packetBuffer);
      packet.setLength(packetBuffer.length);
      socket.send(packet);
      is_send=true;
    }catch(IOException io_e){}
    return is_send;
  }
  public boolean send(byte[] buffer)
  {
    boolean is_send=false;
    try{
      packet.setAddress(remoteHostAddress);
      packet.setPort(remotePort);
      packet.setData(buffer);
      packet.setLength(buffer.length);
      socket.send(packet);
      is_send=true;
    }catch(IOException io_e){}
    return is_send;
  }
  public boolean send(byte[] buffer,int index,int count)
  {
    boolean is_send=false;
    try{
      packet.setAddress(remoteHostAddress);
      packet.setPort(remotePort);
      packet.setData(buffer,index,count);
      packet.setLength(count);
      socket.send(packet);
      is_send=true;
    }catch(IOException io_e){}
    return is_send;
  }
  public boolean recv()
  {
    boolean is_receive=false;
    packetBuffer=null;
    try{
      packetBuffer=new byte[packetBufferSize];
      packet.setData(packetBuffer);
      packet.setLength(packetBuffer.length);
      socket.receive(packet);
      is_receive=true;
    }catch(IOException io_e){}
    return is_receive;
  }
  public boolean openSocket()
  {
    boolean is_open=false;
    try{
      if(!localHost.trim().equals("")){
        localHostAddress=InetAddress.getByName(localHost);
        socket=new DatagramSocket(localPort,localHostAddress);
      }
      else socket=new DatagramSocket(localPort);
      packetBufferSize=socket.getReceiveBufferSize();
      packetBuffer=new byte[0];
      packet=new DatagramPacket(packetBuffer,packetBuffer.length);
      is_open=true;
    }catch(IOException io_e){}
    return open=is_open;
  }
  public boolean openLocal()
  {
    boolean is_open=false;
    try{
      socket=new DatagramSocket();
      packetBufferSize=socket.getReceiveBufferSize();
      packetBuffer=new byte[0];
      packet=new DatagramPacket(packetBuffer,packetBuffer.length);
      is_open=true;
    }catch(IOException io_e){}
    return open=is_open;
  }
  public boolean open()
  {
    boolean is_open=false;
    try{
      localHostAddress=InetAddress.getByName(localHost);
      socket=new DatagramSocket(localPort,localHostAddress);
      packetBufferSize=socket.getReceiveBufferSize();
      packetBuffer=new byte[0];
      packet=new DatagramPacket(packetBuffer,packetBuffer.length);
      is_open=true;
    }catch(IOException io_e){}
    return open=is_open;
  }
  public boolean close()
  {
    boolean is_close=false;
    if(socket!=null){socket.close();socket=null;}
    if(packet!=null)packet=null;
    if(packetBuffer!=null){packetBuffer=null;}
    if(remoteHostAddress!=null)remoteHostAddress=null;
    if(localHostAddress!=null)localHostAddress=null;
    is_close=true;
    open=!is_close;
    return is_close;
  }
}
