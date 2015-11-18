//class version 3.4
package tools;
import java.io.*;
import java.util.Vector;
//-------------------------------------Convert-------------------------------------//
public class Convert
{
  private final static String EMPTY_PORT="-1";
  private final static String HOST_PORT_DELIM=":";
  private final static String PARAM_VALUE_DELIM="=";
  private final static String VALUES_DELIM=";";
  public static int mod(int value,int div)
  {
    int i=value/div;
    return value-i*div;
  }
  public static byte[] toArray(int value)
  {
    return(new byte[]{(byte)(value>>24),(byte)(value>>16),(byte)(value>>8),(byte)value});
  }
  public static byte[] toArray(long value)
  {
    return(new byte[]{(byte)(int)(value>>56),(byte)(int)(value>>48),(byte)(int)(value>>40),(byte)(int)(value >> 32),(byte)(int)(value>>24),(byte)(int)(value>>16),(byte)(int)(value>>8),(byte)(int)value});
  }
  public static int toInt(byte array[])
  {
    int value=0;
    int i=0;
    for(int index=array.length;(i<4)&(index>0);i++)value+=(array[--index]&0xff)<<i*8;
    return value;
  }
  public static long toLong(byte array[])
  {
    long value=0L;
    int i=0;
    for(int index=array.length;(i<8)&(index>0);i++)value+=((long)array[--index]&255L)<<i*8;
    return value;
  }
  public static byte[] concat(byte[] array1,byte[] array2)
  {
    byte[] array=new byte[array1.length+array2.length];
    System.arraycopy(array1,0,array,0,array1.length);
    System.arraycopy(array2,0,array,array1.length,array2.length);
    return array;
  }
  public static String getLogin(String address)
  {
    String login=getParam(address,"@");
    if(login.equals(address))return "";
    return getParam(login,":");
  }
  public static String getPassword(String address)
  {
    String password=getParam(address,"@");
    if(password.equals(address))return "";
    return getValue(password,":");
  }
  public static String getServerAddress(String address)
  {
    if(address.indexOf("@")<0)return getParam(address,":");
    return getParam(getValue(address,"@"),":");
  }
  public static String getServerPort(String address)
  {
    if(address.indexOf("@")<0) return getValue(address,":");
    return getValue(getValue(address,"@"),":");
  }
  public static byte[] toByteArray(long[] long_array)
  {
    byte[] byte_array=null;
    ByteArrayOutputStream byte_stream=null;
    DataOutputStream data_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        data_stream=new DataOutputStream(byte_stream);
        int long_array_size=long_array.length;
        for(int i=0;i<long_array_size;i++)data_stream.writeLong(long_array[i]);
        byte_array=byte_stream.toByteArray();
      }finally{
        if(data_stream!=null){data_stream.close();data_stream=null;}
        if(byte_stream!=null){byte_stream.close();byte_stream=null;}
      }
    }catch(IOException io_e){}
    return byte_array;
  }
  public static byte[] toByteArray(int[] int_array)
  {
    byte[] byte_array=null;
    ByteArrayOutputStream byte_stream=null;
    DataOutputStream data_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        data_stream=new DataOutputStream(byte_stream);
        int int_array_size=int_array.length;
        for(int i=0;i<int_array_size;i++)data_stream.writeInt(int_array[i]);
        byte_array=byte_stream.toByteArray();
      }finally{
        if(data_stream!=null){data_stream.close();data_stream=null;}
        if(byte_stream!=null){byte_stream.close();byte_stream=null;}
      }
    }catch(IOException io_e){}
    return byte_array;
  }
  public static byte[] toByteArray(char[] char_array)
  {
    byte[] byte_array=null;
    ByteArrayOutputStream byte_stream=null;
    DataOutputStream data_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        data_stream=new DataOutputStream(byte_stream);
        int char_array_size=char_array.length;
        for(int i=0;i<char_array_size;i++)data_stream.writeChar(char_array[i]);
        byte_array=byte_stream.toByteArray();
      }finally{
        if(data_stream!=null){data_stream.close();data_stream=null;}
        if(byte_stream!=null){byte_stream.close();byte_stream=null;}
      }
    }catch(IOException io_e){}
    return byte_array;
  }
  public static byte[] toByteArray(double[] double_array)
  {
    byte[] byte_array=null;
    ByteArrayOutputStream byte_stream=null;
    DataOutputStream data_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        data_stream=new DataOutputStream(byte_stream);
        int double_array_size=double_array.length;
        for(int i=0;i<double_array_size;i++)data_stream.writeDouble(double_array[i]);
        byte_array=byte_stream.toByteArray();
      }finally{
        if(data_stream!=null){data_stream.close();data_stream=null;}
        if(byte_stream!=null){byte_stream.close();byte_stream=null;}
      }
    }catch(IOException io_e){}
    return byte_array;
  }
  public static byte[] toByteArray(float[] float_array)
  {
    byte[] byte_array=null;
    ByteArrayOutputStream byte_stream=null;
    DataOutputStream data_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        data_stream=new DataOutputStream(byte_stream);
        int float_array_size=float_array.length;
        for(int i=0;i<float_array_size;i++)data_stream.writeFloat(float_array[i]);
        byte_array=byte_stream.toByteArray();
      }finally{
        if(data_stream!=null){data_stream.close();data_stream=null;}
        if(byte_stream!=null){byte_stream.close();byte_stream=null;}
      }
    }catch(IOException io_e){}
    return byte_array;
  }
  public static byte[] toByteArray(short[] short_array)
  {
    byte[] byte_array=null;
    ByteArrayOutputStream byte_stream=null;
    DataOutputStream data_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        data_stream=new DataOutputStream(byte_stream);
        int short_array_size=short_array.length;
        for(int i=0;i<short_array_size;i++)data_stream.writeChar(short_array[i]);
        byte_array=byte_stream.toByteArray();
      }finally{
        if(data_stream!=null){data_stream.close();data_stream=null;}
        if(byte_stream!=null){byte_stream.close();byte_stream=null;}
      }
    }catch(IOException io_e){}
    return byte_array;
  }
  public static byte[] toByteArray(boolean[] boolean_array)
  {
    byte[] byte_array=null;
    ByteArrayOutputStream byte_stream=null;
    DataOutputStream data_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        data_stream=new DataOutputStream(byte_stream);
        int boolean_array_size=boolean_array.length;
        for(int i=0;i<boolean_array_size;i++)data_stream.writeBoolean(boolean_array[i]);
        byte_array=byte_stream.toByteArray();
      }finally{
        if(data_stream!=null){data_stream.close();data_stream=null;}
        if(byte_stream!=null){byte_stream.close();byte_stream=null;}
      }
    }catch(IOException io_e){}
    return byte_array;
  }
  public static short[] toShortArray(byte[] byte_array)
  {
    short[] short_array=null;
    ByteArrayInputStream byte_stream=null;
    DataInputStream data_stream=null;
    try{
      try{
        byte_stream=new ByteArrayInputStream(byte_array);
        data_stream=new DataInputStream(byte_stream);
        int byte_array_size=byte_array.length,short_array_size=byte_array_size/2;
        short_array=new short[short_array_size];
        for(int i=0;i<short_array_size;i++)short_array[i]=data_stream.readShort();
      }finally{
        if(data_stream!=null){data_stream.close();data_stream=null;}
        if(byte_stream!=null){byte_stream.close();byte_stream=null;}
      }
    }catch(IOException io_e){}
    return short_array;
  }
  public static int[] toIntArray(byte[] byte_array)
  {
    int[] int_array=null;
    ByteArrayInputStream byte_stream=null;
    DataInputStream data_stream=null;
    try{
      try{
        byte_stream=new ByteArrayInputStream(byte_array);
        data_stream=new DataInputStream(byte_stream);
        int byte_array_size=byte_array.length,int_array_size=byte_array_size/4;
        int_array=new int[int_array_size];
        for(int i=0;i<int_array_size;i++)int_array[i]=data_stream.readInt();
      }finally{
        if(data_stream!=null){data_stream.close();data_stream=null;}
        if(byte_stream!=null){byte_stream.close();byte_stream=null;}
      }
    }catch(IOException io_e){}
    return int_array;
  }
  public static long[] toLongArray(byte[] byte_array)
  {
    long[] long_array=null;
    ByteArrayInputStream byte_stream=null;
    DataInputStream data_stream=null;
    try{
      try{
        byte_stream=new ByteArrayInputStream(byte_array);
        data_stream=new DataInputStream(byte_stream);
        int byte_array_size=byte_array.length,long_array_size=byte_array_size/8;
        long_array=new long[long_array_size];
        for(int i=0;i<long_array_size;i++)long_array[i]=data_stream.readLong();
      }finally{
        if(data_stream!=null){data_stream.close();data_stream=null;}
        if(byte_stream!=null){byte_stream.close();byte_stream=null;}
      }
    }catch(IOException io_e){}
    return long_array;
  }
  public static String toString(byte[] byte_array)
  {
    String string="";//null
    ByteArrayOutputStream byte_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        byte_stream.write(byte_array);
        string=byte_stream.toString();
      }finally{if(byte_stream!=null){byte_stream.close();byte_stream=null;}}
    }catch(IOException io_e){}
    return string;
  }
  public static String toString(byte[] byte_array,int size)
  {
    String string="";//null
    ByteArrayOutputStream byte_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        byte_stream.write(byte_array,0,size);
        string=byte_stream.toString();
      }finally{if(byte_stream!=null){byte_stream.close();byte_stream=null;}}
    }catch(IOException io_e){}
    return string;
  }
  public static String toString(int[] int_array)
  {
    String string="";//null
    ByteArrayOutputStream byte_stream=null;
    DataOutputStream data_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        data_stream=new DataOutputStream(byte_stream);
        int int_array_size=int_array.length;
        for(int i=0;i<int_array_size;i++)data_stream.writeInt(int_array[i]);
        string=byte_stream.toString();
      }finally{
        if(data_stream!=null){data_stream.close();data_stream=null;}
        if(byte_stream!=null){byte_stream.close();byte_stream=null;}
      }
    }catch(IOException io_e){}
    return string;
  }
  public static String toString(long[] long_array)
  {
    String string="";//null
    ByteArrayOutputStream byte_stream=null;
    DataOutputStream data_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        data_stream=new DataOutputStream(byte_stream);
        int long_array_size=long_array.length;
        for(int i=0;i<long_array_size;i++)data_stream.writeLong(long_array[i]);
        string=byte_stream.toString();
      }finally{
        if(data_stream!=null){data_stream.close();data_stream=null;}
        if(byte_stream!=null){byte_stream.close();byte_stream=null;}
      }
    }catch(IOException io_e){}
    return string;
  }
  public static String toString(byte value)
  {
    Byte Value=new Byte(value);
    return Value.toString();//Byte.toString();
  }
  public static String toChar(byte value)
  {
    ByteArrayOutputStream byte_stream=new ByteArrayOutputStream();
    byte_stream.write(value);
    return byte_stream.toString();
  }
  public static String toString(int value)
  {
    Integer Value=new Integer(value);
    return Value.toString();//Integer.toString(value);
  }
  public static String toString(long value)
  {
    Long Value=new Long(value);
    return Value.toString();//Long.toString(value);
  }
  public static short toShortValue(String value)
  {
    Short Value=new Short(value);
    return Value.shortValue();
  }
  public static int toIntValue(String value)
  {
    Integer Value=new Integer(value);
    return Value.intValue();
  }
  public static long toLongValue(String value)
  {
    Long Value=new Long(value);
    return Value.longValue();
  }
  public static byte[] addByteArray(byte[] first_byte_array,byte[] second_byte_array)
  {
    byte[] byte_array=null;
    ByteArrayOutputStream byte_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        byte_stream.write(first_byte_array);
        byte_stream.write(second_byte_array);
        byte_array=byte_stream.toByteArray();
      }finally{if(byte_stream!=null){byte_stream.close();byte_stream=null;}}
    }catch(IOException io_e){}
    return byte_array;
  }
  public static byte[] addByteArray(byte[] first_byte_array,int fba_size,byte[] second_byte_array,int sba_size)
  {
    byte[] byte_array=null;
    ByteArrayOutputStream byte_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        byte_stream.write(first_byte_array,0,fba_size);
        byte_stream.write(second_byte_array,0,sba_size);
        byte_array=byte_stream.toByteArray();
      }finally{if(byte_stream!=null){byte_stream.close();byte_stream=null;}}
    }catch(IOException io_e){}
    return byte_array;
  }
  public static byte[] createByteArray(byte[] byte_array,int index,int count)
  {
    byte[] create_byte_array=null;
    ByteArrayOutputStream byte_stream=null;
    try{
      try{
        byte_stream=new ByteArrayOutputStream();
        byte_stream.write(byte_array,index,count);
        create_byte_array=byte_stream.toByteArray();
      }finally{if(byte_stream!=null){byte_stream.close();byte_stream=null;}}
    }catch(IOException io_e){}
    return create_byte_array;
  }
  public static long getFileLength(String filename)
  {
    File file=new File(filename);
    long length=file.length();
    file=null;
    return length;
  }
  public static char[] readCharArrayFromFile(String filename)
  {
    char buffer[]=null;
    int size=(int)Convert.getFileLength(filename);
    FileReader file=null;
    try{
      try{
        file=new FileReader(filename);
        buffer=new char[size];
        file.read(buffer);
      }finally{try{if(file!=null){file.close();file=null;}}catch(IOException io_e){}}
    }catch(IOException io_e){}
    return buffer;
  }
  public static char[] readCharArrayFromFile(String filename,String codepage)
  {
    char buffer[]=null;
    InputStream input_stream=null;
    InputStreamReader reader=null;
    File file=new File(filename);
    int size=(int)file.length();
    try{
      try{
        input_stream=new FileInputStream(file);
        reader=new InputStreamReader(input_stream,codepage);
        buffer=new char[size];
        reader.read(buffer);
      }
      finally{
        try{
          if(reader!=null){reader.close();reader=null;}
          if(input_stream!=null){input_stream.close();input_stream=null;}
        }catch(IOException io_e){}
      }
    }catch(IOException io_e){}
    return buffer;
  }
  public static byte[] readByteArrayFromFile(String filename)
  {
    byte buffer[]=null;
    int size=(int)Convert.getFileLength(filename);
    FileInputStream file=null;
    try{
      try{
        file=new FileInputStream(filename);
        buffer=new byte[size];
        file.read(buffer);
      }finally{try{if(file!=null){file.close();file=null;}}catch(IOException io_e){}}
    }catch(IOException io_e){}
    return buffer;
  }
  public static byte[] readFromFile(String filename)
  {
    byte buffer[]=null;
    FileInputStream file=null;
    try{
      try{
        file=new FileInputStream(filename);
        int size=file.available();
        buffer=new byte[size];
        file.read(buffer);
      }finally{try{if(file!=null){file.close();file=null;}}catch(IOException io_e){}}
    }catch(IOException io_e){}
    return buffer;
  }
  public static void writeToFile(String filename,byte[] buffer)
  {
    FileOutputStream file=null;
    try{
      try{
        file=new FileOutputStream(filename);
        file.write(buffer);
      }finally{try{if(file!=null){file.close();file=null;}}catch(IOException io_e){}}
    }catch(IOException io_e){}
  }
  public static void appendToFile(String filename,byte[] buffer)
  {
    FileOutputStream file=null;
    try{
      try{
        file=new FileOutputStream(filename,true);
        file.write(buffer);
      }finally{try{if(file!=null){file.close();file=null;}}catch(IOException io_e){}}
    }catch(IOException io_e){}
  }
  public static long getFileDatetime(String filename)
  {
    long datetime=0;
    File file=new File(filename);
    if(file.exists()&&file.isFile())datetime=file.lastModified();
    file=null;
    return datetime;
  }
  public static String getHost(String str)
  {
    String host=str;
    int host_index=host.indexOf(HOST_PORT_DELIM);
    if(host_index!=-1)host=str.substring(0,host_index);
    return host.trim();
  }
  public static String getPort(String str)
  {
    String port=EMPTY_PORT;
    int port_index=str.indexOf(HOST_PORT_DELIM);
    if(port_index!=-1)port=str.substring(port_index+HOST_PORT_DELIM.length());
    return port.trim();
  }
  public static String getParam(String str)
  {
    String param=str;
    int param_index=param.indexOf(PARAM_VALUE_DELIM);
    if(param_index!=-1)param=str.substring(0,param_index);
    return param.trim();
  }
  public static String getValue(String str)
  {
    String value="";
    int value_index=str.indexOf(PARAM_VALUE_DELIM);
    if(value_index!=-1)value=str.substring(value_index+PARAM_VALUE_DELIM.length());
    return value.trim();
  }
  public static String getParam(String str,String delim)
  {
    String param=str;
    int param_index=param.indexOf(delim);
    if(param_index!=-1)param=str.substring(0,param_index);
    return param.trim();
  }
  public static String getValue(String str,String delim)
  {
    String value="";
    int value_index=str.indexOf(delim),delim_size=delim.length();
    if(value_index!=-1)value=str.substring(value_index+delim_size);
    return value.trim();
  }
  public static Vector getValues(String str)
  {
    Vector values=new Vector();
    String value=null;
    int value_index;
    boolean need_break=false;
    while(!need_break){
      value_index=str.indexOf(VALUES_DELIM);
      if(value_index==-1){need_break=true;value=str;}
      else{
        value=str.substring(0,value_index);
        str=str.substring(value_index+VALUES_DELIM.length());
      }
      if(value!=null&&!value.equals(""))values.addElement((Object)value.trim());
    }
    return values;
  }
  public static Vector getValues(String str,String delim)
  {
    Vector values=new Vector();
    String value=null;
    int value_index,delim_size=delim.length();
    boolean need_break=false;
    while(!need_break){
      value_index=str.indexOf(delim);
      if(value_index==-1){need_break=true;value=str;}
      else{
        value=str.substring(0,value_index);
        str=str.substring(value_index+delim_size);
      }
      if(value!=null&&!value.equals(""))values.addElement((Object)value.trim());
    }
    return values;
  }
  public static void delay(long delay_time)
  {
    long current_time=System.currentTimeMillis();
    long wait_time=current_time+delay_time;
    while(wait_time>System.currentTimeMillis());
  }
  public static String removeNextLine(String str)
  {
    byte[]s=str.getBytes();
    int size=s.length;
    for(int i=0;i<size;i++)if(s[i]=='\r'||s[i]=='\n')s[i]=0x20;
    return toString(s);
  }
  public static String removeLastSubstring(String str,String substring)
  {
    int index=str.lastIndexOf(substring);
    if(index>-1){str=str.substring(0,index);}
    return str;
  }
  public static String removeFromAndReplace(String str,String removeFrom,String replace,String replaceTo)
  {
    int index=str.indexOf(replace);
    if(index>-1){
      index=str.indexOf(removeFrom,index+replace.length());
      if(index>-1)str=str.substring(0,index);
      str=str.replaceAll(replace,replaceTo);
    }
    return str;
  }
  public static String removeFromTo(String str,String removeFrom,String removeTo)
  {
    int index1=str.indexOf(removeFrom);
    int index2=str.indexOf(removeTo);
    int size=removeTo.length();
    if(index1>-1&&index2>-1){
      str=str.substring(0,index1)+str.substring(index2+size,str.length()-1);
    }
    return str;
  }
}