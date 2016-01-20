package service;
//----------------------------------------------------------------------------//
/*exclusive version of service web project*/
//Xatisa->is a java project for html pages by templates creates
//client send(sql query) and recv (html page) :
//client->sql query->database(oracle by default)->database data->html template->html page->client
//circulating data from database to client :
//... ->database data->html template->html page->client->sql query->database(oracle by default)-> ...
//----------------------------------------------------------------------------//
//import javax.servlet.*;//->
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
//import javax.servlet.ServletContext;//not used
import javax.servlet.ServletException;
//import javax.servlet.http.*;//->
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
//import xls.lib.*//->
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//cell
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Hyperlink;
//json
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
//java io
//import java.io.*;
import java.io.IOException;
//import java.io.PrintWriter;//not used
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
//import java.io.OutputStreamWriter;//not used
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.DataOutputStream;
//import java.io.InputStreamReader;//not used
//import java.net.*;//->
import java.net.Socket;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
//import java.sql.*;//->
import java.sql.Driver;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.sql.CallableStatement;
//import java.util.*;//->
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Iterator;//not used
//import java.util.Collection;//timeout blacklist control
//import java.util.Set;//timeout blacklist control
//import java.util.concurrent.ConcurrentHashMap ->
//java.util.concurrent.ConcurrentHashMap concurrent_hash_map=new java.util.concurrent.ConcurrentHashMap();
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Locale;
//import java.lang.reflect.*;//->
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.HashMap;
//import java.util.Map;//not used
//import sun.misc.*;//->
//import sun.misc.BASE64Encoder;//use native class
//import sun.misc.BASE64Decoder;//use native class
//tools->
import tools.Convert;
import tools.HtmlParse;
//sockets->
//import sockets.TCPSocket;//not used
import sockets.TCPServerSocket;
//[Local]address->TCPServer(not used), [Local]timeout->SessionSleep, [database]timeout->TrashRemoving, [database]Cookie->cookieTimeout
//DatabaseType == ORACLE (set for all conditions) -> CANCELED!
//DatabaseType auto_definition in initial
//Convert.readCharArrayFromFile(...) Warning->used not thread safe
//Servlet charset=windows->1251 completed ...
//Recommendations:
//1.<sql="SELECT ..." fetch="1..10"> fetch->fecth records from index1 to index2, such as 1..10, 1-10 (check^)
//2.To pages add title (to <a href="..." title="go to page">) from "pagetitle" keyword (pages mechanismus organization) (check^)
//3.Add ini parameters of code page converter (Such as: CodePage=Cp1251). For decode cyrillic param and pagetitle. (check^)
//LAST ACTION HERO:
//->add users blacklist mechanism
//->add table pages parameters from html template
//->add set/get manager's cookies
//->add database log
//->add database user
//->add page parameters substitutions from <table page="..." tag attribute
//->add live sessions mechanism ...
// ... today ending users black list mechanism ...
//need to add url pages mechanism (such as->)
//in servlet url
//url= ... ?name=http://autozvit.com/index.html&str1= ...
//in init file [service]
//Trash = http://autozvit.com/project/service_trash/
//Pages = http://autozvit.com/project/service_pages/
//Templates = http://autozvit.com/project/service_templates/
//Files = http://autozvit.com/project/service_files/
//DO IT ...
//----------------------------------------------------------------------------//
//Thinkining->
//active log mechanism ? (statistics of connections)
//1.sessions count statistic
//2.address count statistic
//3.users count statistic
//4.blacklist users count statistic (invalid login informations)

//X.need to use database_driver,database_type param (for connections to other database by driver and type in one template)

//----------------------------------------------------------------------------//
//tools class DatabaseSession(databaseConnection saving) for databaseSessions pool
//WARNING! <Database> object seved in sessions pool.
//This is will got to memory lick on many users sessions ...
//--------------------------------connection----------------------------------//
class DatabaseSession
{
  DatabaseConnection databaseConnection=null;
  Object database=null;//allways null for default connection,other database connections(type==1,3)
  int type=0;//1-default database connection 2-log database connection 3-other database connections
  String name=null;
  long sessionID=0;
  long timeout=0;//session timeout live
}
//----------------------------------------------------------------------------//
//tools class InvalidLogin for bad login (invalid username or password)
//----------------------------------------------------------------------------//
class InvalidLogin{
  int count;//bad login count
  long timeout;//timeout for bad login
  String address;//bad login address
}
//--------------------------------manager-------------------------------------//
//WARNING! All debug string is comment
//for uncomment-> "if(debug)" && "if(manager.isDebug())" (replace all "//if(debug)" for "if(debug)")
public class Manager extends HttpServlet implements Interface,tools.Interface
{
  //////////////////////////////////////////////////////////////////////////////
  public static final int CLASSES_VERSION=65;
  private long sessionID=0,lastSessionID=sessionID;
  private Vector sessions=null;//server sessions
  private Session sessionsControl=null,servicesControl=null/*,netListener=null*/;
  private Hashtable defaultDatabaseSessions=null;//default database sessions (1-for primary,secondary address 2-log database)
  private Hashtable databaseSessions=null;//database sessions
  //private Hashtable clientSessions=null;//client sessions
  private int localTimeout=0,databaseTimeout=0;
  private int databaseSessionsCount=0,databaseSessionsTimeout=0;
  private Vector blackList=null;//users black list (all users in list are blocking)
  private Vector addressBlackList=null;//address blacklist (all address in list are blocking)
  private Hashtable timeoutBlackList=null;//candidates to timeout blacklist (join invalid login or password entered). Blacklist clear by timeout
  private int databaseBlackListCount=0,databaseBlackListTimeout=0;
  private Initial initial=null;
  private Log log=null;
  private PageFormat pageFormat=null;
  private String systemCodepage=null,localCodepage=null,localContentType=null,initialFilepath=null,databaseLog=null;
  private boolean databasePassword=false;
  private Vector databaseUsersList=null;
  private boolean debug=false,servletLog=false,log2Database=false/*flag for operative use*/;
  private String servletFilepath=null;
  private int databaseCookie=TIMEOUT_DATABASE_COOKIE/*database_cookie_timeout*/;
  private byte[] enigmaData=null;
  //[is/set/get]
  public long getSessionID(){return sessionID;}
  //public long getLastSessionID(){return lastSessionID;}
  public synchronized long getNewSessionID(){lastSessionID++;notifyAll();return lastSessionID;}
  public Vector getSessions(){return sessions;}
  public Hashtable getDefaultDatabaseSessions(){return defaultDatabaseSessions;}
  public Hashtable getDatabaseSessions(){return databaseSessions;}
  //public Hashtable getClientSessions(){return ClientSessions;}
  public int getDatabaseSessionsCount(){return databaseSessionsCount;}
  public int getDatabaseSessionsTimeout(){return databaseSessionsTimeout;}
  public Vector getBlackList(){return blackList;}
  public Vector getAddressBlackList(){return addressBlackList;}
  public Hashtable getTimeoutBlackList(){return timeoutBlackList;}
  public int getDatabaseBlackListCount(){return databaseBlackListCount;}
  public int getDatabaseBlackListTimeout(){return databaseBlackListTimeout;}
  public Initial getInitial(){return initial;}
  public Log getLog(){return log;}
  public PageFormat getPageFormat(){return pageFormat;}
  public String getLogFilepathWithDate(String filepath){
    String ret_val=EMPTY;
    Date date=new Date();
    int day,month,year;
    if(filepath!=null){
      day=date.getDate();
      month=1+date.getMonth();
      year=1900+date.getYear();
      ret_val=filepath+DOWN+day+DOWN+month+DOWN+year;
    }
    return ret_val;
  }
  public int getLocalTimeout(){return localTimeout;}
  public int getDatabaseTimeout(){return databaseTimeout;}
  public String getSystemCodepage(){return systemCodepage;}
  public String getLocalCodepage(){return localCodepage;}
  public String getLocalContentType(){return localContentType;}
  public String getInitialFilepath(){return initialFilepath;}
  public String getDatabaseLog(){return databaseLog;}
  public boolean isDebug(){return debug;}
  public boolean isLog2Database(){return log2Database;}
  public String getServletFilepath(){return servletFilepath;}
  public int getDatabaseCookie(){return databaseCookie;}
  public boolean getDatabasePassword(){return databasePassword;}
  public Vector getDatabaseUsersList(){return databaseUsersList;}
  public byte[] getEnigmaData(){return enigmaData;}
  public void setDebug(boolean value){debug=value;}
  public void setDefaultDatabaseSessions(Hashtable value){defaultDatabaseSessions=value;}
  public void setLog2Database(boolean value){log2Database=value;}
  //////////////////////////////////////////////////////////////////////////////
  //[constructor]
  public Manager(){}
  public Manager(String servlet_filepath){servletFilepath=servlet_filepath;}
  //Initialize global variables
  @Override
  public void init() throws ServletException
  {
    Locale.setDefault(Locale.ENGLISH);
    systemCodepage=System.getProperty("file.encoding");
    try{//if class(init)->execute from non web server
      //windows path: "C:/Program Files/Apache Software Foundation/Tomcat 6.0/webapps/service/"
      if(servletFilepath==null){servletFilepath=getServletContext().getRealPath(EMPTY);servletLog=true;}
    }catch(Exception e){servletFilepath=EMPTY;}
    //[server started]
    //if(debug)System.out.println("<server started>");
    /*init file seek*/
    String str=null,filepath=null;//for all strings down
    File file;//for all files down
    try{
      filepath=servletFilepath+FILEPATH_WEB_INF+FILEPATH_INI;
      file=new File(filepath);
      if(file.exists()&&file.isFile())str=filepath;
      else{
        filepath=servletFilepath+FILEPATH_INI;
        file=new File(filepath);
        if(file.exists()&&file.isFile())str=filepath;
      }
    }catch(Exception e){}
    /*init file*/
    //if(debug)System.out.println("<init file>");
    initial=new Initial();
    if(str==null||!initial.open(str,this)){
      System.out.println(getMessageByID(ERROR_INITIAL_NOT_FOUND)+SPACE+filepath);
      return;
    }else initialFilepath=str;
    if(initial.getLocalDebug()!=null&&initial.getLocalDebug().equalsIgnoreCase(ON))debug=true;
    if(initial.getLocalCodepage()!=null)localCodepage=initial.getLocalCodepage();else localCodepage=RESPONSE_CODEPAGE;
    if(initial.getLocalContentType()!=null)localContentType=initial.getLocalContentType();else localContentType=CONTENT_TYPE_TEXT_HTML;
    if(initial.getDatabasePassword()!=null){
      if(initial.getDatabasePassword().equalsIgnoreCase(ON)){
        databasePassword=true;
        enigmaData=new byte[36];
        byte[] b1={15,29,30,1,116,50};
        byte[] b2={28,86,66,69,82,1};
        enigmaData[6]=17;enigmaData[7]=51;enigmaData[8]=101;enigmaData[9]=109;enigmaData[10]=96;enigmaData[11]=54;
        enigmaData[18]=5;enigmaData[19]=7;enigmaData[20]=19;enigmaData[21]=18;enigmaData[22]=39;enigmaData[23]=91;
        enigmaData[24]=72;enigmaData[25]=60;enigmaData[26]=43;enigmaData[27]=11;enigmaData[28]=76;enigmaData[29]=45;
        System.arraycopy(b2,0,enigmaData,17,6);
        enigmaData[30]=89;enigmaData[31]=31;enigmaData[32]=6;enigmaData[33]=91;enigmaData[34]=9;enigmaData[35]=36;
        System.arraycopy(b1,0,enigmaData,0,6);
      }
      else if(initial.getDatabasePassword().equalsIgnoreCase(OFF))databasePassword=false;
      else{
        databasePassword=true;
        enigmaData=initial.getDatabasePassword().getBytes();
      }
    }
    localTimeout=TIMEOUT_SESSION_SLEEP;
    if(initial.getLocalTimeout()!=null&&initial.getLocalTimeout().length()>0)
      try{localTimeout=Convert.toIntValue(initial.getLocalTimeout());}catch(Exception e){}
    databaseTimeout=TIMEOUT_DATABASE_FILES;
    if(initial.getDatabaseTimeout()!=null&&initial.getDatabaseTimeout().length()>0)
      try{databaseTimeout=Convert.toIntValue(initial.getDatabaseTimeout());}catch(Exception e){}
    /*file log*/
    //if(debug)System.out.println("<file log>");
    str=null;
    if(initial.getLocalLog()!=null&&(initial.getLocalLog().equalsIgnoreCase(ON)||initial.getLocalLog().equals(EMPTY)))//default filename open
      str=servletFilepath+FILEPATH_LOG;
    else if(initial.getLocalLog()!=null&&(initial.getLocalLog().toLowerCase().endsWith(POINT+EXTENSION_FILE_LOG)))//filename from ini open
      str=initial.getLocalLog();//local_log may be a log filepath with ".log" ends (/log/service.log)
    log=new Log();//log->file
    //if(debug)System.out.println("<before file log open>");
    if(log.open(str,this)){//is debug ?
      log.write("---------------------------------------------------------------\r\n");//debug
      log.write(LOCAL_NAME+LOCAL_VERSION+NEXT_LINE);//debug
      log.write("---------------------------------------------------------------\r\n");//debug
      log.write(sessionID,INFO_LOG_ACTIVATED,initial.getLocalLog().equalsIgnoreCase(ON)?FILENAME_LOG:initial.getLocalLog());
    }
    //if(debug)System.out.println("<after file log open>");
    //Log.write(systemCodepage+NEXT_LINE);
    //Log.write(System.getProperties().toString()+NEXT_LINE);
    log.write(sessionID,INFO_INITIAL_ACTIVATED,filepath);
    /*database log*/
    //if(debug)System.out.println("<database log>");
    databaseLog=initial.getDatabaseLog();//address from ini
    if(databaseLog==null||databaseLog.equals(EMPTY)){
    try{//address from file
      str=null;
      filepath=servletFilepath+FILEPATH_WEB_INF+FILEPATH_LOG_INI;
      file=new File(filepath);
      if(file.exists()&&file.isFile())str=filepath;
      else{
        filepath=servletFilepath+FILEPATH_LOG_INI;
        file=new File(filepath);
        if(file.exists()&&file.isFile())str=filepath;
      }
      if(str!=null&&!str.equals(EMPTY)){
        databaseLog=Convert.toString(Convert.readFromFile(str));//log->database
      }
    }catch(Exception e){}
    }//if
    /*start seek database users*/
    //if(debug)System.out.println("<database users>");
    databaseUsersList=initial.getDatabaseUsersList();
    if(databaseUsersList==null||databaseUsersList.size()==0){//if database users list empty->seek users file
    try{
      str=null;
      if(initial.getDatabaseUsers()!=null&&initial.getDatabaseUsers().length()>0){//users filename from ini
        file=new File(initial.getDatabaseUsers());
        if(file.exists()&&file.isFile())str=initial.getDatabaseUsers();
      }//if
      else{//default users filename
        filepath=servletFilepath+FILEPATH_WEB_INF+FILEPATH_USERS;
        file=new File(filepath);
        if(file.exists()&&file.isFile())str=filepath;
        else{
          filepath=servletFilepath+FILEPATH_USERS;
          file=new File(filepath);
          if(file.exists()&&file.isFile())str=filepath;
        }//else
      }//else
      if(str!=null&&!str.equals(EMPTY)){
        log.write(sessionID,INFO_DATABASE_USERS_ACTIVATED,filepath);
        BufferedReader file_stream=new BufferedReader(new FileReader(str));
        while(true){
          str=file_stream.readLine();
          if(str==null)break;
          else if(str.length()>0){
            if(databaseUsersList==null)databaseUsersList=new Vector();
            databaseUsersList.add(str.trim());
            //Log.write("add user(for password encoder)->"+str+"\r\n");
          }//else if
        }//while(true)
      }//if
    }catch(Exception e){}
    }//if
    /*end seek database users*/
    /*start seek blacklist users and addresses*/
    //if(debug)System.out.println("<blacklist users and addresses>");
    try{
      str=null;
      if(initial.getDatabaseBlackList()!=null&&initial.getDatabaseBlackList().length()>0){//blacklist filename from ini
        file=new File(initial.getDatabaseBlackList());
        if(file.exists()&&file.isFile())str=initial.getDatabaseBlackList();
      }//if
      else{//default blacklist filename
        filepath=servletFilepath+FILEPATH_WEB_INF+FILEPATH_BLACKLIST;
        file=new File(filepath);
        if(file.exists()&&file.isFile())str=filepath;
        else{
          filepath=servletFilepath+FILEPATH_BLACKLIST;
          file=new File(filepath);
          if(file.exists()&&file.isFile())str=filepath;
        }//else
      }//else
      if(str!=null&&!str.equals(EMPTY)){
        log.write(sessionID,INFO_DATABASE_BLACKLIST_ACTIVATED,filepath);
        BufferedReader file_stream=new BufferedReader(new FileReader(str));
        while(true){
          str=file_stream.readLine();
          if(str==null)break;
          else if(str.startsWith(BLI_PARAM_USER)){
            str=Convert.getValue(str);
            if(str!=null&&str.length()>0){
              if(blackList==null)blackList=new Vector();
              blackList.add(str);
              //Log.write("add user(for blacklist)->"+str+"\r\n");
            }//if
          }//else if
          else if(str.startsWith(BLI_PARAM_ADDRESS)){
            str=Convert.getValue(str);
            if(str!=null&&str.length()>0){
              if(addressBlackList==null)addressBlackList=new Vector();
              addressBlackList.add(str);
              //Log.write("add address(for blacklist)->"+str+"\r\n");
            }//if
          }//else if
        }//while(true)
      }//if
    }catch(Exception e){}
    /*end seek blacklist users and addresses*/
    //database connection (log) sessionID==
    //if(debug)System.out.println("<database connection(log)>");
    if(databaseLog!=null&&databaseLog.length()>0){
      LogDatabase log_database=null;
      DatabaseConnection log_connection=new DatabaseConnection();
      log_connection.open(this,sessionID,databaseLog);
      if(log_connection.isOpened()){//log database connection
        log_database=new LogDatabase(log_connection);
        if(defaultDatabaseSessions==null)defaultDatabaseSessions=new Hashtable();
        DatabaseSession database_session=new DatabaseSession();
        database_session.database=log_database;
        database_session.databaseConnection=log_connection;
        database_session.type=SESSION_TYPE_LOG_DATABASE_CONNECTION;
        database_session.name=SESSION_NAME_LOG_DATABASE_CONNECTION;
        database_session.sessionID=sessionID;
        database_session.timeout=System.currentTimeMillis();
        defaultDatabaseSessions.put(SESSION_KEY_LOG_DATABASE_CONNECTION,database_session);
        log2Database=true;/*flag for operative use*/
        log.write(sessionID,INFO_DATABASE_CONNECTION_ESTABLISHED,log_connection.getConnectionAddress());
      }
    }
    //database connection (default) sessionID==0
    //if(debug)System.out.println("<database connection(default)>");
    if((initial.getDatabasePrimaryAddress()!=null&&initial.getDatabasePrimaryAddress().length()>0)||
       (initial.getDatabaseSecondaryAddress()!=null&&initial.getDatabaseSecondaryAddress().length()>0)){
      DatabaseConnection connection=new DatabaseConnection();
      connection.open(this,sessionID);
      if(connection.isOpened()){//default database connection
        if(defaultDatabaseSessions==null)defaultDatabaseSessions=new Hashtable();
        DatabaseSession database_session=new DatabaseSession();
        database_session.databaseConnection=connection;
        database_session.type=SESSION_TYPE_DEFAULT_DATABASE_CONNECTION;
        database_session.name=SESSION_NAME_DEFAULT_DATABASE_CONNECTION;
        database_session.sessionID=sessionID;
        database_session.timeout=System.currentTimeMillis();
        defaultDatabaseSessions.put(SESSION_KEY_DEFAULT_DATABASE_CONNECTION,database_session);
        log.write(sessionID,INFO_DATABASE_CONNECTION_ESTABLISHED,connection.getConnectionAddress());
      }
    }
    if(initial.getDatabaseCookie()!=null&&initial.getDatabaseCookie().length()>0)
      try{databaseCookie=Convert.toIntValue(initial.getDatabaseCookie());}catch(Exception e){databaseCookie=TIMEOUT_DATABASE_COOKIE;}
    /*page part*/
    //if(debug)System.out.println("<page part>");

    //service not ready (read format param from templates in href,table,input,img_src,select such as: <table format="">)
    //PageFormat=new pageFormat(this);

    //[sessions activated]
    //if(debug)System.out.println("<sessions activated>");
    sessions=new Vector();
    Session session;
    //[Net listener]
    //session=new Session();
    //session.open(this,SESSION_TYPE_NET_LISTENER,this.getNewSessionID());
    //session.start();//session.yield();//wait for session starts
    //Sessions.add(session);
    //NetListener=session;
    //[sessions control]
    //if(debug)System.out.println("<sessions control>");
    session=new Session();
    session.open(this,SESSION_TYPE_SESSIONS_CONTROL,this.getNewSessionID());
    session.start();//session.yield();//wait for session starts
    sessions.add(session);
    sessionsControl=session;
    //[Services control]
    //if(debug)System.out.println("<services control>");
    if(initial.getServiceStartsList()!=null){
      session=new Session();
      session.open(this,SESSION_TYPE_SERVICES_CONTROL,this.getNewSessionID());
      session.start();//session.yield();//wait for session starts
      sessions.add(session);
      servicesControl=session;
    }
    //[Users database sessions pool]
    //if(debug)System.out.println("<users database sessions pool>");
    if(initial.getDatabaseSessionsCount()!=null&&initial.getDatabaseSessionsCount().length()>0){
      databaseSessions=new Hashtable();
      try{databaseSessionsCount=Convert.toIntValue(initial.getDatabaseSessionsCount());}catch(Exception e){databaseSessionsCount=COUNT_DATABASE_SESSIONS;}
      if(initial.getDatabaseSessionsTimeout()!=null&&initial.getDatabaseSessionsTimeout().length()>0)
        try{databaseSessionsTimeout=Convert.toIntValue(initial.getDatabaseSessionsTimeout());}catch(Exception e){databaseSessionsTimeout=TIMEOUT_DATABASE_SESSIONS;}
      else databaseSessionsTimeout=TIMEOUT_DATABASE_SESSIONS;
    }
    //[Users database timeout blacklist]
    //if(debug)System.out.println("<users database timeout blacklist>");
    if(initial.getDatabaseBlackListCount()!=null&&initial.getDatabaseBlackListCount().length()>0){
      timeoutBlackList=new Hashtable();
      try{databaseBlackListCount=Convert.toIntValue(initial.getDatabaseBlackListCount());}catch(Exception e){databaseBlackListCount=COUNT_DATABASE_BLACKLIST;}
      if(initial.getDatabaseBlackListTimeout()!=null&&initial.getDatabaseBlackListTimeout().length()>0)//timeout blacklist users
        try{databaseBlackListTimeout=Convert.toIntValue(initial.getDatabaseBlackListTimeout());}catch(Exception e){databaseBlackListTimeout=TIMEOUT_DATABASE_BLACKLIST;}
    }
    //[server activated]
    log.write(sessionID,INFO_SERVER_ACTIVATED,servletFilepath);
    //if(debug)System.out.println("<server activated>");
    if(servletLog)log("activated servlet service <skyDrakkar>");// >>>
  }
  //Process the HTTP Get request
  //servlet?name=...&param0=...&param1=...&param3=...
  //parameters : name,param<#>,...
  @Override
  public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
  {
    ClientSession client_session=new ClientSession(this,request,response);
    //ClientSessions.put(client_session.getSessionID(),client_session);
  }
  //Process the HTTP Post request
  @Override
  public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
  {
    doGet(request,response);
  }
  //Process the HTTP Put request
  @Override
  public void doPut(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
  {
    doGet(request,response);
  }
  //Process the HTTP Delete request
  @Override
  public void doDelete(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
  {
    doGet(request,response);
  }
  //Clean up resources
  @Override
  public void destroy()
  {
    //[server deactivated]
    if(blackList!=null){blackList.clear();blackList=null;}
    if(addressBlackList!=null){addressBlackList.clear();addressBlackList=null;}
    if(timeoutBlackList!=null){//InvalidLogin elements kills
      /*InvalidLogin inv_login;
      for(Enumeration e=timeoutBlackList.elements();e.hasMoreElements();){
        inv_login=(InvalidLogin)e.nextElement();
        if(inv_login!=null)inv_login=null;
      }*/
      timeoutBlackList.clear();timeoutBlackList=null;
    }
    if(sessions!=null){
      //stop critical sessions before killing other sessions
      if(sessionsControl!=null){sessionsControl.close();sessionsControl=null;}
      if(servicesControl!=null){servicesControl.close();servicesControl=null;}
      //if(NetListener!=null){NetListener.close();NetListener=null;}
      Session session;
      for(Enumeration e=sessions.elements();e.hasMoreElements();){
        session=(Session)e.nextElement();
        if(session!=null){session.close();session=null;}
        //try{session.join();}catch(InterruptedException i_e){}//=realized wait in session.close()
      }
      sessions.clear();sessions=null;
    }
    if(databaseSessions!=null){//database sessions
      DatabaseSession database_session;
      for(Enumeration e=databaseSessions.elements();e.hasMoreElements();){
        database_session=(DatabaseSession)e.nextElement();
        if(database_session!=null&&database_session.databaseConnection!=null){
          database_session.databaseConnection.close();
          if(log!=null)log.write(database_session.sessionID,INFO_DATABASE_CONNECTION_CLOSED,database_session.databaseConnection.getConnectionLogin());
          database_session.databaseConnection=null;
          database_session.database=null;database_session=null;
        }
      }
      databaseSessions.clear();databaseSessions=null;
    }
    if(defaultDatabaseSessions!=null){//default database sessions
      DatabaseSession database_session;
      for(Enumeration e=defaultDatabaseSessions.elements();e.hasMoreElements();){
        database_session=(DatabaseSession)e.nextElement();
        if(database_session!=null&&database_session.databaseConnection!=null){
          database_session.databaseConnection.close();
          if(log!=null)log.write(database_session.sessionID,INFO_DATABASE_CONNECTION_CLOSED,database_session.databaseConnection.getConnectionLogin());
          database_session.databaseConnection=null;
          database_session.database=null;database_session=null;
        }
      }
      defaultDatabaseSessions.clear();defaultDatabaseSessions=null;
    }
    if(log!=null){log.write(sessionID,INFO_SERVER_DEACTIVATED);}
    //This manually deregisters JDBC drivers(which prevents memory leaks)
    Enumeration<Driver> drivers=DriverManager.getDrivers();
    Driver driver;
    while(drivers.hasMoreElements()){
      driver=drivers.nextElement();
      try{
        DriverManager.deregisterDriver(driver);
        if(servletLog)log("Success deregistering jdbc driver servlet service <skyDrakkar>: "+driver.toString());
      }catch(SQLException e){if(servletLog)log("Error deregistering jdbc driver servlet service <skyDrakkar>: "+driver.toString());}
      driver=null;
    }
    if(log!=null){log.flush();log.close();log=null;}
    if(initial!=null){initial.close();initial=null;}
    if(enigmaData!=null)enigmaData=null;
    if(servletLog)log("deactivated servlet service <skyDrakkar>");// <<<
  }
  //////////////////////////////////////////////////////////////////////////////
  //[interface tools]
  public String getMessageByID(int id){return MessageList.getText(id);}
  public String getLogMessageByID(int id){return getLogMessageByID(id,null);}
  public String getLogMessageByID(int id,String value)
  {
    String ret_val=getMessageByID(id);
    if(id>=INFO_START_ID&&id<=INFO_FINISH_ID)ret_val=MESSAGE_INFO+ret_val;
    if(id>=WARNING_START_ID&&id<=WARNING_FINISH_ID)ret_val=MESSAGE_WARNING+ret_val;
    if(id>=ERROR_START_ID&&id<=ERROR_FINISH_ID)ret_val=MESSAGE_ERROR+ret_val;
    if(id>=DEBUG_START_ID&&id<=DEBUG_FINISH_ID)ret_val=MESSAGE_DEBUG+ret_val;
    if(value!=null)ret_val+=MESSAGE_DELIM_VALUE+value;
    return ret_val;
  }
  public int getLogTypeByID(int id)
  {
    int ret_val=0;
    if(id>=INFO_START_ID&&id<=INFO_FINISH_ID)ret_val=MESSAGE_TYPE_INFO;
    if(id>=WARNING_START_ID&&id<=WARNING_FINISH_ID)ret_val=MESSAGE_TYPE_WARNING;
    if(id>=ERROR_START_ID&&id<=ERROR_FINISH_ID)ret_val=MESSAGE_TYPE_ERROR;
    if(id>=DEBUG_START_ID&&id<=DEBUG_FINISH_ID)ret_val=MESSAGE_TYPE_DEBUG;
    return ret_val;
  }
  //////////////////////////////////////////////////////////////////////////////
}
class ClientSession implements Interface,tools.Interface
{
  private Manager manager=null;
  private HttpServletRequest request=null;
  private HttpServletResponse response=null;
  private HtmlResponse htmlResponse=null;
  private long sessionID=-1;
  public HttpServletRequest getRequest(){return request;}
  public HttpServletResponse getResponse(){return response;}
  public long getSessionID(){return sessionID;}
  private boolean isParamNumber(String str){
    //search param number end
    int size=str.length();
    char ch;
    boolean ret_val=size>5?true:false;
    for(int i=5;i<size;i++){
      ch=str.charAt(i);
      if(ch<'0'||ch>'9'){//not in [30..39]->'0'..'9'
        ret_val=false;break;
      }
    }
    return ret_val;
  }
  public ClientSession(Manager manager,HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
  {
    this.manager=manager;
    this.request=request;
    this.response=response;
    long session_id=manager.getNewSessionID();
    //session_id
    sessionID=session_id;//session_id used in next functions(log)
    String request_codepage=request.getCharacterEncoding();//allways empty?
    if(request_codepage==null||request_codepage.equals(EMPTY))request_codepage=REQUEST_CODEPAGE;
    //Manager.getLog().write("request_codepage="+request_codepage+"\r\n");
    String login=null,password=null,database_name=null,database_type=null,database_driver=null,name=null;
    String cookie_name=null,cookie_value=null,cookie_timeout=null;
    String hide_cookie_name=null,hide_cookie_value=null,hide_cookie_timeout=null,hide_cookie_keyword=null;
    byte[] buf=null;
    Vector buf_list=null;
    BufferParam buf_param;
    boolean blacklist_blocking=false/*blocking user or address in blacklist*/,content_failed=false/*empty file buffer in content*/,cookie_failed=false;/*empty cookie information (and password) in query*/
    boolean connected=false;/*connected to database or special service*/
    Hashtable filename_table=new Hashtable();
    String s;//temp
    int index,size,ind;//temp
    //servlet param init
    //if(debug)System.out.println("<servlet param init>");
    ServletParam sp=new ServletParam();
    sp.secure=request.isSecure()?1:0;
    sp.request=request;
    sp.response=response;
    sp.paramIndex=0;
    sp.paramList=new Vector();
    sp.extraList=new HashMap();
    sp.paramTypeList=new Vector();//type
    sp.sqlList=new Vector();
    //request data
    //if(debug)System.out.println("<request data>");
    //if(debug)System.out.println("->getRequestURL");
    sp.requestURL=request.getRequestURL().toString();
    //if(debug)System.out.println("->getQueryString");
    sp.queryString=request.getQueryString();if(sp.queryString==null)sp.queryString=EMPTY;
    //start session
    //if(debug)System.out.println("<start session>");
    //Log.write(request.getLocalAddr()+" "+request.getLocalName()+" "+request.getRemoteAddr()+" "+request.getRemoteHost()+" "+request.getRemoteUser()+" "+request.getServerName());
    manager.getLog().write(session_id,INFO_SESSION_ACTIVATED,request.getMethod()+MESSAGE_DELIM_SUBVALUES+sp.requestURL+MESSAGE_DELIM_SUBVALUES+sp.queryString+
              MESSAGE_DELIM_SUBVALUES+request.getRemoteHost()+MESSAGE_DELIM_SUBVALUES+request.getRemoteAddr());
    //(request.getContentType()!=null?MESSAGE_DELIM_SUBVALUES+request.getContentType():EMPTY)+
    //(request.getContentLength()>0?MESSAGE_DELIM_SUBVALUES+request.getContentLength():EMPTY));
    //request.setCharacterEncoding(REQUEST_CODEPAGE);
    //response.setCharacterEncoding(localCodepage);
    //if(debug)System.out.println("->getRemoteAddr");
    sp.remoteAddr=request.getRemoteAddr();
    //if(debug)System.out.println("->getRemoteHost");
    sp.remoteHost=request.getRemoteHost();
    //processing html ...
    //if(debug)System.out.println("<processing html>");
    response.setContentType(manager.getLocalContentType());
    //for templates writes in localCodepage
    //PrintWriter out=new PrintWriter(new OutputStreamWriter(response.getOutputStream(),localCodepage),true);
    //PrintWriter out=response.getWriter();
    //for services pages writes bytes as s.getBytes(localCodepage);
    ServletOutputStream out_stream=response.getOutputStream();
    //address blacklist analize
    //if(debug)System.out.println("<address blacklist>");
    if(manager.getAddressBlackList()!=null&&manager.getAddressBlackList().size()>0){
      for(Enumeration e=manager.getAddressBlackList().elements();e.hasMoreElements();){
        s=(String)e.nextElement();
        if(s!=null&&s.equals(sp.remoteAddr)){
          blacklist_blocking=true;
          manager.getLog().write(session_id,ERROR_BLACKLIST_BLOCKING_BY_ADDRESS,sp.remoteAddr);
          break;/*end of analize*/
        }//if
      }//for
    }//if
    //error pages (only blacklist blocking)
    //if(debug)System.out.println("<blacklist blocking>");
    if(blacklist_blocking){//blocking user or address in blacklist
      s=manager.getInitial().getServicePages();//ini service pages dir
      if(s!=null&&!s.endsWith(LOCAL_DELIM_2))s+=LOCAL_DELIM_2;
      out_stream.write(Convert.readFromFile(s!=null?s+FILENAME_BLACKLIST_BLOCKING:manager.getServletFilepath()+FILEPATH_BLACKLIST_BLOCKING));
      //if(debug)System.out.println("<finish session> <-blacklist blocking");
      this.finishSession(session_id,filename_table,buf_list,sp);
      return;//finishSession
    }
    //request content
    //if(debug)System.out.println("<request content>");
    if(request.getContentType()!=null&&request.getContentType().startsWith(CONTENT_TYPE_APP_JSON)){
      buf=this.readFromStream(request.getInputStream(),request.getContentLength());
      String str_buf=URLDecoder.decode(new String(buf),AJAX_CODEPAGE);//decode from ajax UTF-8
      String pa,va;
      StringTokenizer st=new StringTokenizer(str_buf,AND);
      while(st.hasMoreTokens()){//seek parts of request param in POST
        s=st.nextToken();
        pa=Convert.getParam(s);va=Convert.getValue(s);
        if(pa.startsWith(SERVICE_LOGIN)||pa.startsWith(SERVICE_LOGIN_)){//name="login.."||"str1.."
          login=va;sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_LOGIN_/*str1*/+EQUAL+login;
        }
        else if(pa.startsWith(SERVICE_PASSWORD)||pa.startsWith(SERVICE_PASSWORD_)){//name="password.."||"str2.."
          password=va;sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_PASSWORD_/*str2*/+EQUAL+password;
        }
        else if(pa.startsWith(SERVICE_DATABASE)||pa.startsWith(SERVICE_DATABASE_)){//name="database.."||"str3.."
          database_name=va;sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_DATABASE_/*str3*/+EQUAL+database_name;
        }
        else if(pa.startsWith(SERVICE_TYPE)||pa.startsWith(SERVICE_TYPE_)){//name="type.."||"str4.."
          database_type=va;sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_TYPE_/*str4*/+EQUAL+database_type;
        }
        else if(pa.startsWith(SERVICE_DRIVER)||pa.startsWith(SERVICE_DRIVER_)){//name="driver.."||"str5.."
          database_driver=va;sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_DRIVER_/*str5*/+EQUAL+database_driver;
        }
        else if(pa.startsWith(SERVICE_NAME)||pa.startsWith(SERVICE_NAME_)){//name="name.."||"str0.."
          name=va;sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_NAME_/*str0*/+EQUAL+name;
        }
        else if(pa.startsWith(SERVICE_PARAM)&&isParamNumber(pa)){//name="param#"
          index=-1;
          s=pa.substring(pa.indexOf(SERVICE_PARAM)+SERVICE_PARAM.length());
          try{index=Convert.toIntValue(s);}catch(Exception e){}
          if(index!=-1){//normal converting
            //set param
            if(index>=sp.paramList.size())sp.paramList.setSize(index+1);
            sp.paramList.setElementAt(va,index);
            //set param_type
            if(index>=sp.paramTypeList.size())sp.paramTypeList.setSize(index+1);
            sp.paramTypeList.setElementAt(SERVICE_PARAM_TYPE_STRING,index);
            size=va.length();
            manager.getLog().write(session_id,INFO_SERVICE_PARAM_REGISTERED,SERVICE_PARAM+index+MESSAGE_DELIM_SUBVALUES+(size>LENGTH_LOG_VALUE?size:EQUAL+va));
          }
        }
        else if(pa.equalsIgnoreCase(SERVICE_SQL)||pa.startsWith(SERVICE_SQL)){//name="sql"||"sql.."
          s=va;
          sp.sqlList.add(s);
          size=va.length();
          manager.getLog().write(session_id,INFO_SERVICE_PARAM_REGISTERED,SERVICE_SQL+MESSAGE_DELIM_SUBVALUES+(size>LENGTH_LOG_VALUE?size:EQUAL+va));
        }
        else if(pa.startsWith(SERVICE_COOKIE_NAME)){
          cookie_name=va;
        }
        else if(pa.startsWith(SERVICE_COOKIE_VALUE)){
          cookie_value=va;
        }
        else if(pa.startsWith(SERVICE_COOKIE_TIMEOUT)){
          cookie_timeout=va;
        }
        else if(pa.startsWith(SERVICE_HIDE_COOKIE_NAME)){
          hide_cookie_name=va;
        }
        else if(pa.startsWith(SERVICE_HIDE_COOKIE_VALUE)){
          hide_cookie_value=va;
        }
        else if(pa.startsWith(SERVICE_HIDE_COOKIE_TIMEOUT)){
          hide_cookie_timeout=va;
        }
        else if(pa.startsWith(SERVICE_HIDE_COOKIE_KEYWORD)){
          hide_cookie_keyword=va;
        }
        else{//save all other content data as extra
          sp.extraList.put(pa,va);
          size=va.length();
          manager.getLog().write(session_id,INFO_SERVICE_CONTENT_REGISTERED,pa+MESSAGE_DELIM_SUBVALUES+(size>LENGTH_LOG_VALUE?size:EQUAL+va));
        }
      }
      buf=null;
    }
    else if(request.getContentLength()>0){
      buf=this.readFromStream(request.getInputStream(),request.getContentLength());
      buf_list=this.parseBuf(buf);
      //parse content parameters
      //if(debug)System.out.println("<parse content parameters>");
      if(buf_list!=null&&buf_list.size()>0){
      for(int i=0;i<buf_list.size();i++){
        buf_param=(BufferParam)buf_list.get(i);
        if(buf_param!=null&&buf_param.name!=null){//registering param
          //always replace login,password,database_name,name on str1,str2,str3,str0
          if(buf_param.name.startsWith(SERVICE_LOGIN)||buf_param.name.startsWith(SERVICE_LOGIN_)){//name="login.."||"str1.."
            if(buf_param.data!=null){login=Convert.toString(buf_param.data);sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_LOGIN_/*str1*/+EQUAL+login;}
          }
          else if(buf_param.name.startsWith(SERVICE_PASSWORD)||buf_param.name.startsWith(SERVICE_PASSWORD_)){//name="password.."||"str2.."
            if(buf_param.data!=null){password=Convert.toString(buf_param.data);sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_PASSWORD_/*str2*/+EQUAL+password;}
          }
          else if(buf_param.name.startsWith(SERVICE_DATABASE)||buf_param.name.startsWith(SERVICE_DATABASE_)){//name="database.."||"str3.."
            if(buf_param.data!=null){database_name=Convert.toString(buf_param.data);sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_DATABASE_/*str3*/+EQUAL+database_name;}
          }
          else if(buf_param.name.startsWith(SERVICE_TYPE)||buf_param.name.startsWith(SERVICE_TYPE_)){//name="type.."||"str4.."
            if(buf_param.data!=null){database_type=Convert.toString(buf_param.data);sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_TYPE_/*str4*/+EQUAL+database_type;}
          }
          else if(buf_param.name.startsWith(SERVICE_DRIVER)||buf_param.name.startsWith(SERVICE_DRIVER_)){//name="type.."||"str5.."
            if(buf_param.data!=null){database_driver=Convert.toString(buf_param.data);sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_DRIVER_/*str5*/+EQUAL+database_driver;}
          }
          else if(buf_param.name.startsWith(SERVICE_NAME)||buf_param.name.startsWith(SERVICE_NAME_)){//name="name.."||"str0.."
            if(buf_param.data!=null){name=Convert.toString(buf_param.data);sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_NAME_/*str0*/+EQUAL+name;}
          }
          else if(buf_param.name.startsWith(SERVICE_PARAM)&&isParamNumber(buf_param.name)){//name="param#"
            index=-1;
            s=buf_param.name.substring(buf_param.name.indexOf(SERVICE_PARAM)+SERVICE_PARAM.length());
            try{index=Convert.toIntValue(s);}catch(Exception e){}
            if(index!=-1){//normal converting
              if(buf_param.data!=null&&buf_param.filename!=null&&buf_param.filename.length()>0){//file buffer
                if(buf_param.data.length!=0){
                  if(index>=sp.paramList.size())sp.paramList.setSize(index+1);
                  sp.paramList.setElementAt(buf_param.data,index);
                  //sp.BufferParam=buf_param;//last file buffer
                  if(index>=sp.paramTypeList.size())sp.paramTypeList.setSize(index+1);
                  sp.paramTypeList.setElementAt(SERVICE_PARAM_TYPE_BUFFER,index);
                  //Manager.getLog().write("FileBuffer_setBytes(param)#"+index);
                  ind=buf_param.filename.lastIndexOf(LOCAL_DELIM);
                  if(ind==-1)ind=buf_param.filename.lastIndexOf(LOCAL_DELIM_2);
                  if(ind!=-1)buf_param.filename=buf_param.filename.substring(ind+1);
                  filename_table.put(buf_param.name,buf_param.filename);
                  manager.getLog().write(session_id,INFO_SERVICE_PARAM_REGISTERED,SERVICE_PARAM+index+MESSAGE_DELIM_SUBVALUES+buf_param.filename+MESSAGE_DELIM_SUBVALUES+buf_param.contentType+MESSAGE_DELIM_SUBVALUES+buf_param.data.length);
                }
                else{content_failed=true;manager.getLog().write(session_id,ERROR_CONTENT_FAILED,buf_param.filename);break;}
              }
              else if(buf_param.data!=null&&(buf_param.filename==null||buf_param.filename.length()==0)){//string field
                s=Convert.toString(buf_param.data);
                if(index>=sp.paramList.size())sp.paramList.setSize(index+1);
                sp.paramList.setElementAt(s,index);
                if(index>=sp.paramTypeList.size())sp.paramTypeList.setSize(index+1);
                sp.paramTypeList.setElementAt(SERVICE_PARAM_TYPE_STRING,index);
                sp.queryString+=(sp.queryString.length()>0?REQUEST_DELIM:EMPTY)+SERVICE_PARAM+Convert.toString(index)+EQUAL+s;
                manager.getLog().write(session_id,INFO_SERVICE_PARAM_REGISTERED,SERVICE_PARAM+index+EQUAL+s);
              }
            }
          }
          //file [ save file buffer, save filename to database_name :) ]
          else if(buf_param.name.startsWith(SERVICE_FILE)){//name="file#"
            if(buf_param.data!=null&&buf_param.filename!=null&&buf_param.filename.length()>0){//save file to disk
              if(buf_param.data.length>0){
                ind=buf_param.filename.lastIndexOf(LOCAL_DELIM);
                if(ind==-1)ind=buf_param.filename.lastIndexOf(LOCAL_DELIM_2);
                //remove filepath -> filename add session_id (session_id_Filename.ext)
                if(ind!=-1)buf_param.filename=Convert.toString(session_id)+DOWN+buf_param.filename.substring(ind+1);
                else buf_param.filename=Convert.toString(session_id)+DOWN+buf_param.filename;
                s=manager.getInitial().getServiceFiles();//ini service files dir
                if(s!=null&&!s.endsWith(LOCAL_DELIM_2))s+=LOCAL_DELIM_2;
                String filepath=(s!=null?s:manager.getServletFilepath()+FILEPATH_SERVICE_FILES)+buf_param.filename;
                //Convert.writeToFile(filepath,buf_param.data);
                FileOutputStream file;
                try{
                  file=new FileOutputStream(filepath);
                  file.write(buf_param.data);
                  file.close();file=null;
                }catch(IOException io_e){manager.getLog().write(session_id,WARNING_DATA_NOT_FOUND,io_e.toString());}
                //save filepath to database_name ...
                filename_table.put(buf_param.name,((s!=null)?this.toURL(s):URL_SERVICE_FILES)+buf_param.filename);
                manager.getLog().write(session_id,INFO_WRITE_FILE,buf_param.name+MESSAGE_DELIM_SUBVALUES+buf_param.filename+MESSAGE_DELIM_SUBVALUES+buf_param.data.length);
              }
              else{content_failed=true;manager.getLog().write(session_id,ERROR_CONTENT_FAILED,buf_param.filename);break;}
            }
          }
          //sql (insert,update or delete query before processing html...)
          else if(buf_param.name.equalsIgnoreCase(SERVICE_SQL)||buf_param.name.startsWith(SERVICE_SQL)){//name="sql"||"sql.."
            //sp.sql=Convert.toString(buf_param.data);
            s=Convert.toString(buf_param.data);
            sp.sqlList.add(s);
            manager.getLog().write(session_id,INFO_SERVICE_PARAM_REGISTERED,SERVICE_SQL+MESSAGE_DELIM_SUBVALUES+s);
          }
          else if(buf_param.name.startsWith(SERVICE_COOKIE_NAME)&&buf_param.data!=null){
            cookie_name=Convert.toString(buf_param.data);
          }
          else if(buf_param.name.startsWith(SERVICE_COOKIE_VALUE)&&buf_param.data!=null){
            cookie_value=Convert.toString(buf_param.data);
          }
          else if(buf_param.name.startsWith(SERVICE_COOKIE_TIMEOUT)&&buf_param.data!=null){
            cookie_timeout=Convert.toString(buf_param.data);
          }
          else if(buf_param.name.startsWith(SERVICE_HIDE_COOKIE_NAME)&&buf_param.data!=null){
            hide_cookie_name=Convert.toString(buf_param.data);
          }
          else if(buf_param.name.startsWith(SERVICE_HIDE_COOKIE_VALUE)&&buf_param.data!=null){
            hide_cookie_value=Convert.toString(buf_param.data);
          }
          else if(buf_param.name.startsWith(SERVICE_HIDE_COOKIE_TIMEOUT)&&buf_param.data!=null){
            hide_cookie_timeout=Convert.toString(buf_param.data);
          }
          else if(buf_param.name.startsWith(SERVICE_HIDE_COOKIE_KEYWORD)&&buf_param.data!=null){
            hide_cookie_keyword=Convert.toString(buf_param.data);
          }
          else{//save all other content data as extra
            sp.extraList.put(buf_param.name,buf_param.data);
            manager.getLog().write(session_id,INFO_SERVICE_CONTENT_REGISTERED,buf_param.name+(buf_param.data!=null?MESSAGE_DELIM_SUBVALUES+buf_param.data.length:EMPTY)+MESSAGE_DELIM_SUBVALUES+buf_param.contentType);
          }
        }
      }
      //reparsing param#
      //[ for database_name saving filename of file buffer :) ]
      for(int i=0;i<sp.paramList.size();i++){
        if(sp.paramTypeList.get(i)!=null&&((String)sp.paramTypeList.get(i)).equals(SERVICE_PARAM_TYPE_STRING)){
          s=(String)sp.paramList.get(i);
          if(filename_table.containsKey(s)){//update param value for filename
            sp.paramList.setElementAt(filename_table.get(s),i);
            manager.getLog().write(session_id,INFO_SERVICE_PARAM_REGISTERED,s+MESSAGE_DELIM_SUBVALUES+filename_table.get(s));
          }
        }
      }//for
      }//if
      else sp.content=new String(buf);
      buf=null;
    }
    //parse request parameters
    //if(debug)System.out.println("<parse request parameters>");
    //if(debug)System.out.println("->getParameterMap");
    //save all request parameters as extra
    String par_name;
    Enumeration par_names=request.getParameterNames();
    for(;par_names.hasMoreElements();){
      par_name=(String)par_names.nextElement();
      s=(String)request.getParameter(par_name);
      s=new String(s.getBytes(request_codepage),manager.getLocalCodepage());//default: decode ISO-8859-1 -> Cp1251
      sp.extraList.put(par_name,s);
    }
    //[parse paramlist]
    Object[] objects=request.getParameterMap().keySet().toArray();
    size=objects.length;
    for(int i=0;i<size;i++){
      s=(String)objects[i];
      if(s.startsWith(SERVICE_PARAM)&&isParamNumber(s)){//param#=
        index=-1;
        s=s.substring(s.indexOf(SERVICE_PARAM)+SERVICE_PARAM.length());
        try{index=Convert.toIntValue(s);}catch(Exception e){}
        if(index!=-1){//normal converting
          s=request.getParameter(SERVICE_PARAM+index);
          s=new String(s.getBytes(request_codepage),manager.getLocalCodepage());//default: decode ISO-8859-1 -> Cp1251
          //registering param
          if(index>=sp.paramList.size())sp.paramList.setSize(index+1);
          //set param# if null in content
          if(sp.paramList.elementAt(index)==null){
            sp.paramList.setElementAt(s,index);
            if(index>=sp.paramTypeList.size())sp.paramTypeList.setSize(index+1);
            sp.paramTypeList.setElementAt(SERVICE_PARAM_TYPE_STRING,index);
            manager.getLog().write(session_id,INFO_SERVICE_PARAM_REGISTERED,SERVICE_PARAM+index+EQUAL+s);
          }
        }
      }
    }
    objects=null;
    //[parse sqllist]
    //sql (insert,update or delete query before processing html...)
    /*s=request.getParameter(SERVICE_SQL);//only one sql query -> first in list use
    if(s!=null){
      sp.sqlList.add(s);//sp.sql=s;
      log.write(session_id,INFO_SERVICE_PARAM_REGISTERED,SERVICE_SQL+MESSAGE_DELIM_SUBVALUES+s);
    }*/
    //if(debug)System.out.println("->getParameterValues");
    String[] sql=request.getParameterValues(SERVICE_SQL);
    if(sql!=null){
      for(int i=0;i<sql.length;i++){
        s=sql[i];
        sp.sqlList.add(s);//sp.sql=s;
        manager.getLog().write(session_id,INFO_SERVICE_PARAM_REGISTERED,SERVICE_SQL+MESSAGE_DELIM_SUBVALUES+s);
      }
    }
    //if(debug)System.out.println("->getParameter");
    //rowcount
    sp.rowCount=request.getParameter(SERVICE_ROW_COUNT);
    //pagefunction
    sp.pageOnclick=request.getParameter(SERVICE_PAGE_ONCLICK);
    //pagehref
    sp.pageHref=request.getParameter(SERVICE_PAGE_HREF);
    //pagecount
    sp.pageCount=request.getParameter(SERVICE_PAGE_COUNT);
    //pagenumber
    sp.pageNumber=request.getParameter(SERVICE_PAGE_NUMBER);
    //pagestyle
    sp.pageStyle=request.getParameter(SERVICE_PAGE_STYLE);
    //pagetype
    sp.pageType=request.getParameter(SERVICE_PAGE_TYPE);
    //pagemarker
    sp.pageMarker=request.getParameter(SERVICE_PAGE_MARKER);
    //pageparam
    sp.pageParam=request.getParameter(SERVICE_PAGE_PARAM);
    //pagetitle
    sp.pageTitle=request.getParameter(SERVICE_PAGE_TITLE);
    if(sp.pageTitle!=null&&sp.pageTitle.length()>0)sp.pageTitle=new String(sp.pageTitle.getBytes(request_codepage),manager.getLocalCodepage());//default: decode ISO-8859-1 -> Cp1251
    //pageclass
    sp.pageClass=request.getParameter(SERVICE_PAGE_CLASS);
    //pagefolder
    sp.pageCurrentClass=request.getParameter(SERVICE_PAGE_CURRENT_CLASS);
    //pageanchor
    sp.pageAnchor=request.getParameter(SERVICE_PAGE_ANCHOR);
    //pageprev
    sp.pagePrev=request.getParameter(SERVICE_PAGE_PREV);
    //pagenext
    sp.pageNext=request.getParameter(SERVICE_PAGE_NEXT);
    //sqlnumber
    sp.sqlNumber=request.getParameter(SERVICE_SQL_NUMBER);
    //sqlignore
    sp.sqlIgnore=request.getParameter(SERVICE_SQL_IGNORE);
    //[normal cookie]
    //cookiename
    if(cookie_name==null)sp.cookieName=request.getParameter(SERVICE_COOKIE_NAME);
    //cookievalue
    if(cookie_value==null)sp.cookieValue=request.getParameter(SERVICE_COOKIE_VALUE);
    //cookietimeout
    if(cookie_timeout==null)sp.cookieTimeout=request.getParameter(SERVICE_COOKIE_TIMEOUT);
    //[secret cookie]
    //hidecookiename
    if(hide_cookie_name==null)sp.hideCookieName=request.getParameter(SERVICE_HIDE_COOKIE_NAME);
    //hidecookievalue
    if(hide_cookie_value==null)sp.hideCookieValue=request.getParameter(SERVICE_HIDE_COOKIE_VALUE);
    //hidecookietimeout
    if(hide_cookie_timeout==null)sp.hideCookieTimeout=request.getParameter(SERVICE_HIDE_COOKIE_TIMEOUT);
    //hidecookiekeyword
    if(hide_cookie_keyword==null)sp.hideCookieKeyword=request.getParameter(SERVICE_HIDE_COOKIE_KEYWORD);
    //set login,password,database_name,name if null in content
    if(login==null){//login
      login=request.getParameter(SERVICE_LOGIN);//login
      if(login==null)login=request.getParameter(SERVICE_LOGIN_);//str1
    }
    if(password==null){//password
      password=request.getParameter(SERVICE_PASSWORD);//password
      if(password==null)password=request.getParameter(SERVICE_PASSWORD_);//str2
    }
    if(database_name==null){//database_name
      database_name=request.getParameter(SERVICE_DATABASE);//database_name
      if(database_name==null)database_name=request.getParameter(SERVICE_DATABASE_);//str3
    }
    if(database_type==null){//database_type
      database_type=request.getParameter(SERVICE_TYPE);//database_type
      if(database_type==null)database_type=request.getParameter(SERVICE_TYPE_);//str4
    }
    if(database_driver==null){//database_driver
      database_driver=request.getParameter(SERVICE_DRIVER);//database_driver
      if(database_driver==null)database_driver=request.getParameter(SERVICE_DRIVER_);//str5
    }
    if(name==null){//name
      name=request.getParameter(SERVICE_NAME);//name
      if(name==null)name=request.getParameter(SERVICE_NAME_);//str0
    }
    if(name!=null)sp.name=name;else sp.name=EMPTY;
    //(user) blacklist analize
    //if(debug)System.out.println("<user blacklist>");
    /*information -> user stay blocking if good password checking after bad!*/
    if(login!=null){//login(user)=not be a null!
      if(manager.getBlackList()!=null&&manager.getBlackList().size()>0){
        for(Enumeration e=manager.getBlackList().elements();e.hasMoreElements();){
          s=(String)e.nextElement();
          if(s!=null&&s.equalsIgnoreCase(login)){
            blacklist_blocking=true;
            manager.getLog().write(session_id,ERROR_BLACKLIST_BLOCKING_BY_USERNAME,login);
            break;/*end of analize*/
          }//if
        }//for
      }//if
      //(timeout) blacklist analize
      if(manager.getTimeoutBlackList()!=null&&manager.getTimeoutBlackList().size()>0){//from time to time list updating (removing elements by control session)
        InvalidLogin inv_login=(InvalidLogin)manager.getTimeoutBlackList().get(login);
        if(inv_login!=null){//client in list ...
          if(inv_login.count>=manager.getDatabaseBlackListCount()){
            manager.getLog().write(session_id,ERROR_TIMEOUT_BLACKLIST_BLOCKING,login);
            blacklist_blocking=true;
          }//if
        }//if
      }//if
    }//if login!=null
    //error pages (only blacklist blocking)
    //if(debug)System.out.println("<blacklist blocking>");
    if(blacklist_blocking){//blocking user in (blacklist or timeout blacklist)
      s=manager.getInitial().getServicePages();//ini service pages dir
      if(s!=null&&!s.endsWith(LOCAL_DELIM_2))s+=LOCAL_DELIM_2;
      out_stream.write(Convert.readFromFile(s!=null?s+FILENAME_BLACKLIST_BLOCKING:manager.getServletFilepath()+FILEPATH_BLACKLIST_BLOCKING));
      //if(debug)System.out.println("<finish session> <-blacklist blocking");
      this.finishSession(session_id,filename_table,buf_list,sp);
      return;//finishSession
    }
    if(sp.name.length()==0){//name not found (not connecting, not html processing)
      //goto default page name
      s=manager.getInitial().getServiceTemplates();//ini service templates dir
      if(s!=null&&!s.endsWith(LOCAL_DELIM_2))s+=LOCAL_DELIM_2;
      out_stream.write(Convert.readFromFile((s!=null?s:manager.getServletFilepath()+FILEPATH_SERVICE_TEMPLATES)+FILENAME_DEFAULT_PAGE));
    }
    else{//sp.name.length()>0
    //special service pages(service_index,service_login,service_start)
    if(sp.name.startsWith(FILENAME_PREFIX_SERVICE_PAGES))sp.service=true;
    //cookies ...
    //if(debug)System.out.println("<cookies>");
    if(password==null||password.length()==0){//read cookies
      try{
        Cookie[] cookies=request.getCookies();
        Cookie cookie;
        if(cookies!=null){
          //if(debug)System.out.println("->cookies.length");
          int cookies_size=cookies.length;
          for(int i=0;i<cookies_size;i++){
            cookie=cookies[i];
            //if(debug)System.out.println("->cookie.getName");
            if(cookie!=null&&cookie.getName().equalsIgnoreCase(login)){//WARNING! equalsIgnoreCase not work with symbol "@" ?
              //if(debug)System.out.println("->BASE64Decoder");
              byte[] dec_data=new /*sun.misc.*/BASE64Decoder().decodeBuffer(cookie.getValue());//BASE64->replace by native code
              dec_data=this.encodeData(login,dec_data);//decoding data & password from cookie value
              Date cookie_date=new Date();
              ByteArrayOutputStream byte_stream=null;
              byte_stream=new ByteArrayOutputStream();
              byte_stream.write(dec_data,8,dec_data.length-8);
              password=byte_stream.toString();
              long l=0L;
              int i1=0;
              for(int i2=8;(i1<8)&(i2>0);i1++)l+=((long)dec_data[--i2]&255L)<<i1*8;//byte[] to long
              //Log.write("Time long value(get from cookie)="+l+"\r\n");
              cookie_date.setTime(l);
              byte_stream.close();byte_stream=null;
              int database_cookie_timeout=(manager.getDatabaseCookie()==-1?TIMEOUT_DATABASE_COOKIE:manager.getDatabaseCookie());
              if(System.currentTimeMillis()-l>database_cookie_timeout){//get from browser
                cookie_failed=true;password=null;
                manager.getLog().write(session_id,INFO_COOKIE_FAILED,login+OPEN+cookie_date.toString()+CLOSE);
              }
              else manager.getLog().write(session_id,INFO_COOKIE_ESTABLISHED,login+OPEN+cookie_date.toString()+CLOSE);
            }
          }
        }
      }catch(IOException io_e){}
    }//if
    else{//write cookies
      try{
        long l=System.currentTimeMillis();
        //Log.write("Time long value(put to cookie)="+l+"\r\n");
        byte[] l_array=new byte[]{(byte)(int)(l>>56),(byte)(int)(l>>48),(byte)(int)(l>>40),(byte)(int)(l>>32),(byte)(int)(l>>24),(byte)(int)(l>>16),(byte)(int)(l>>8),(byte)(int)l};//long to byte[]
        ByteArrayOutputStream byte_stream=null;
        byte_stream=new ByteArrayOutputStream();
        byte_stream.write(l_array);
        byte_stream.write(password.getBytes());
        //if(debug)System.out.println("->BASE64Encoder");
        String enc_data=new /*sun.misc.*/BASE64Encoder().encode(this.encodeData(login,byte_stream.toByteArray()));//BASE64->replace by native code
        Cookie cookie=new Cookie(login,enc_data);//login name as cookie name,encoding password as cookie value
        int database_cookie_timeout=(manager.getDatabaseCookie()==-1?manager.getDatabaseCookie():(int)manager.getDatabaseCookie()/1000);
        //cookie.setMaxTime(-1);//remove cookie if browser exit
        cookie.setMaxAge(database_cookie_timeout);//set to browser database_cookie_timeout (in sec)
        //if(debug)System.out.println("->response.addCookie");
        response.addCookie(cookie);
        byte_stream.close();byte_stream=null;
        manager.getLog().write(session_id,INFO_COOKIE_CREATED,login+OPEN+enc_data+CLOSE);
      }catch(IOException io_e){}
    }//else
    //by database_name connecting ...
    //if(debug)System.out.println("<database connecting>");
    DatabaseConnection connection=null;
    Database database=null;
    boolean need_disconnect=false;
    if(!cookie_failed&&!content_failed){//cookie and content success
      if(login!=null&&password!=null){//connection from http_param(str1,str2) or http_cookie
        if(login.length()>0&&password.length()>0){//login && password length>0
          if(sp.service){//no database
            //if(debug)System.out.println("<service(not connect)>");
            if(login.equals(manager.getInitial().getLocalUser())&&password.equals(manager.getInitial().getLocalPassword())){//seek for local user(as admin)
              connected=true;
            }
          }//if service (no database)
          else{//goto database (no service)
          //seek database connection in pool
          if(manager.getDatabaseSessions()!=null&&manager.getDatabaseSessionsCount()>0){//seek database session
            //if(debug)System.out.println("<seek database session>");
            DatabaseSession database_session;
            for(Enumeration e=manager.getDatabaseSessions().elements();e.hasMoreElements();){//sessions list
              database_session=(DatabaseSession)e.nextElement();
              if(database_session!=null&&database_session.databaseConnection!=null&&!database_session.databaseConnection.isClosed()){
                //may be a database name equals too?(_session.databaseConnection.getConnectionDatabase().equals(database))
                if(database_session.databaseConnection.getConnectionLogin().equals(login)&&database_session.databaseConnection.getConnectionPassword().equals(password)){
                  database_session.timeout=System.currentTimeMillis();//long live rock'n'roll ...
                  connection=database_session.databaseConnection;
                  if(connection!=null&&connection.isOpened()){
                    database=new Database(manager,connection);
                    connected=true;
                  }
                }//if
              }//if
            }//for
          }//if databaseSessions&databaseSessionsCount>0
          if(connection==null){//no database session
            //if(debug)System.out.println("<no database session>");
            //need to use database_driver,database_type param
            if(database_driver==null||database_driver.length()==0){
              database_driver=manager.getInitial().getDatabaseDriver();
            }
            if(database_type==null||database_type.length()==0){
              database_type=manager.getInitial().getDatabaseType();
            }
            int database_number=0;
            if(database_name==null||database_name.length()==0){
              if(manager.getInitial().getDatabasePrimaryAddress()!=null&&manager.getInitial().getDatabasePrimaryAddress().length()>0){database_name=Convert.getValue(manager.getInitial().getDatabasePrimaryAddress(),DOG);database_number=1;}
              else if(manager.getInitial().getDatabaseSecondaryAddress()!=null&&manager.getInitial().getDatabaseSecondaryAddress().length()>0){database_name=Convert.getValue(manager.getInitial().getDatabaseSecondaryAddress(),DOG);database_number=2;}
            }//if
            boolean save_session=false;
            connection=new DatabaseConnection();
            //if(debug)System.out.println("<open database connection>");
            connection.open(manager,session_id,database_driver,database_type,database_name,login,password);
            if(connection.isOpened()){
              database=new Database(manager,connection);
              connected=true;save_session=true;
              manager.getLog().write(session_id,INFO_DATABASE_CONNECTION_ESTABLISHED,connection.getConnectionAddress()+OPEN+login+CLOSE);
            }//if
            else if(database_number==1){//may be a second database address correct ...
              if(manager.getInitial().getDatabaseSecondaryAddress()!=null&&manager.getInitial().getDatabaseSecondaryAddress().length()>0){database_name=Convert.getValue(manager.getInitial().getDatabaseSecondaryAddress(),DOG);database_number=2;}
              if(database_number==2){//second database address present
                connection.open(manager,session_id,database_driver,database_type,database_name,login,password);
                if(connection.isOpened()){
                  database=new Database(manager,connection);
                  connected=true;save_session=true;
                  manager.getLog().write(session_id,INFO_DATABASE_CONNECTION_ESTABLISHED,connection.getConnectionAddress()+OPEN+login+CLOSE);
                }//if isOpened
                else{connection=null;database=null;}//not opened
              }//if
            }//else
            if(save_session){//save session to pool
              if(manager.getDatabaseSessions()!=null&&manager.getDatabaseSessions().size()<=manager.getDatabaseSessionsCount()){//add database connection to pool
                DatabaseSession database_session=new DatabaseSession();
                database_session.databaseConnection=connection;
                database_session.type=SESSION_TYPE_USER_DATABASE_CONNECTION;
                database_session.name=SESSION_NAME_USER_DATABASE_CONNECTION;
                database_session.sessionID=session_id;
                database_session.timeout=System.currentTimeMillis();
                manager.getDatabaseSessions().put(login,database_session);//Hashtable synchronized
                manager.getLog().write(database_session.sessionID,INFO_DATABASE_SESSION_SAVED,database_session.databaseConnection.getConnectionLogin());
              }
              else need_disconnect=true;/*no more cell found (limited by database_sessions_count)*/
            }
          }//if connection
          }//else goto database (no service)
        }//if login.length()>0&&password.length()>0
      }//if login!=null&&password!=null
      else if(login==null&&password==null&&!sp.service){//connection from init_param(PrimaryAddress,SecondaryAddress)
        if(sp.name.startsWith(FILENAME_PREFIX_ADMIN_PAGES)){//admin_mode
        Database default_database=null;
        DatabaseConnection default_connection=null;
        Hashtable default_sessions=manager.getDefaultDatabaseSessions();
        DatabaseSession database_session=null;
        if(default_sessions!=null)database_session=(DatabaseSession)default_sessions.get(SESSION_KEY_DEFAULT_DATABASE_CONNECTION);
        if(database_session!=null)default_connection=database_session.databaseConnection;
        //default_connection.getConnection().isClosed() <SQLException needed>
        boolean is_closed,is_valid=true;
        try{
          is_closed=(
                  default_connection==null||default_connection.getConnection()==null||default_connection.isClosed()||
                  default_connection.getConnection().isClosed());
        }catch(SQLException sql_e){is_closed=true;}
        /*boolean */
        /*try{//modern variations of connection test (from 1.6 ver.)
          is_valid=default_connection.getConnection().isValid(TIMEOUT_DATABASE_CONNECTION_IS_VALID);
        }catch(Exception e){}//for old drivers*/
        //database valid connection
        //Log.write("Close(default)connection:"+Boolean.toString(is_closed));
        if(!is_closed&&default_connection!=null&&manager.getInitial().getDatabaseValidConnection()!=null&&manager.getInitial().getDatabaseValidConnection().length()>0){
          is_valid=default_connection.validConnection(manager.getInitial().getDatabaseValidConnection());
          //Log.write("Valid(default)connection:"+Boolean.toString(is_valid));
        }
        if(is_closed||!is_valid){//connection failed
          //default database reconnection
          default_connection=new DatabaseConnection();
          default_connection.open(manager,session_id);
          if(default_connection.isOpened()){
            default_database=new Database(manager,default_connection);//new database class object
            if(database_session!=null){database_session.databaseConnection=default_connection;}
            else{
              if(manager.getDefaultDatabaseSessions()==null)manager.setDefaultDatabaseSessions(new Hashtable());
              database_session=new DatabaseSession();
              database_session.databaseConnection=default_connection;
              database_session.type=SESSION_TYPE_DEFAULT_DATABASE_CONNECTION;
              database_session.name=SESSION_NAME_DEFAULT_DATABASE_CONNECTION;
              database_session.sessionID=manager.getSessionID();//SessionID==0
              database_session.timeout=System.currentTimeMillis();
              manager.getDefaultDatabaseSessions().put(SESSION_KEY_DEFAULT_DATABASE_CONNECTION,database_session);//Hashtable synchronized
            }
            manager.getLog().write(session_id,INFO_DATABASE_RECONNECTION,default_connection.getConnectionLogin());
          }//if isOpened
          else{default_connection=null;default_database=null;}//not opened
        }
        connection=default_connection;database=default_database;/*not need used(allways new database class object)database.setBlocked();for defaut database connections>1*/
        if(connection!=null&&connection.isOpened()){
          if(database==null)database=new Database(manager,connection);//new database class object
          connected=true;
        }
        }//if admin_mode (name==admin_)
        else{//for context default_connection
          connection=new DatabaseConnection();
          connection.open(manager,session_id);
          if(connection.isOpened()){//default database connection
            database=new Database(manager,connection);
            need_disconnect=true;connected=true;
            manager.getLog().write(sessionID,INFO_DATABASE_CONNECTION_ESTABLISHED,connection.getConnectionAddress());
          }
        }
      }//else login==null&&password==null
    }//if no cookie||content error
    //error pages
    //if(debug)System.out.println("<error pages>");
    if(content_failed){//content error
      s=manager.getInitial().getServicePages();//ini service pages dir
      if(s!=null&&!s.endsWith(LOCAL_DELIM_2))s+=LOCAL_DELIM_2;
      out_stream.write(Convert.readFromFile(s!=null?s+FILENAME_CONTENT_FAILED:manager.getServletFilepath()+FILEPATH_CONTENT_FAILED));
    }
    else if(cookie_failed){//cookie error
      s=manager.getInitial().getServicePages();//ini service pages dir
      if(s!=null&&!s.endsWith(LOCAL_DELIM_2))s+=LOCAL_DELIM_2;
      out_stream.write(Convert.readFromFile(s!=null?s+FILENAME_COOKIE_FAILED:manager.getServletFilepath()+FILEPATH_COOKIE_FAILED));
    }
    else if(!connected){//connection error of failed
      //place login to timeout blacklist (by error code->invalid username or password)
      //TimeoutBlackList (candidates) increment pointer
      //if(debug)System.out.println("<blacklist candidate>");
      if(manager.getDatabaseBlackListCount()>0){
        if(sp.service||(manager.getInitial().getDatabaseType().equalsIgnoreCase(DATABASE_TYPE_ORACLE)&&connection!=null&&
           connection.getErrorCode()==ERROR_CODE_DATABASE_ORACLE_INVALID_USERNAME_OR_PASSWORD)/*||
          (initial.getDatabaseType().equalsIgnoreCase(DATABASE_TYPE_MYSQL)&&connection!=null&&
           connection.getErrorCode()==ERROR_CODE_DATABASE_MYSQL_INVALID_USERNAME_OR_PASSWORD)*/){
          if(login!=null){//login(user)=not be a null!
            InvalidLogin inv_login=(InvalidLogin)manager.getTimeoutBlackList().get(login);
            if(inv_login==null){//client not in list ...
              inv_login=new InvalidLogin();
              inv_login.count=0;
              manager.getTimeoutBlackList().put(login,inv_login);//add a new candidate(Hashtable synchronized)
              manager.getLog().write(session_id,INFO_SAVED_IN_TIMEOUT_BLACKLIST,login);
            }//if
            inv_login.count++;
            inv_login.timeout=System.currentTimeMillis();
            inv_login.address=sp.remoteAddr;//for active log
          }//if login!=null
        }//if service || database_type
      }//if
      s=manager.getInitial().getServicePages();//ini service pages dir
      if(s!=null&&!s.endsWith(LOCAL_DELIM_2))s+=LOCAL_DELIM_2;
      out_stream.write(Convert.readFromFile(s!=null?s+FILENAME_CONNECTION_FAILED:manager.getServletFilepath()+FILEPATH_CONNECTION_FAILED));
    }
    else{//connection success(with no error)
      //if(debug)System.out.println("<connection succes>");
      Cookie cookie;
      if(sp.cookieName!=null&&sp.cookieValue!=null){//normal cookie
        if(sp.cookieTimeout==null)sp.cookieTimeout="-1";//browser close cookie timeout
        cookie=new Cookie(sp.cookieName,sp.cookieValue);
        cookie.setMaxAge(Convert.toIntValue(sp.cookieTimeout));//set to browser cookie_timeout (in sec)
        response.addCookie(cookie);
      }
      if(sp.hideCookieName!=null&&sp.hideCookieValue!=null){//secret cookie
        if(sp.hideCookieTimeout==null)sp.hideCookieTimeout="-1";//browser close hide cookie timeout
        String coo_key=(sp.hideCookieKeyword!=null&&sp.hideCookieKeyword.length()>0)?sp.hideCookieKeyword:HIDE_COOKIE_KEYWORD;
        String coo_val=new /*sun.misc.*/BASE64Encoder().encode(this.encodeData(coo_key,sp.hideCookieValue.getBytes()));//BASE64->replace by native code
        cookie=new Cookie(sp.hideCookieName,coo_val);
        cookie.setMaxAge(Convert.toIntValue(sp.hideCookieTimeout));//set to browser cookie_timeout (in sec)
        response.addCookie(cookie);
      }
      if(connection!=null){
        sp.login=connection.getConnectionLogin();
        sp.password=connection.getConnectionPassword();
        sp.database=connection.getConnectionDatabase();
        sp.databaseDriver=connection.getConnectionDriver();
        sp.databaseType=connection.getConnectionType();
      }
      //activation of
      htmlResponse=new HtmlResponse(session_id,/*out,*/out_stream,manager,this,database);
      if(sp.sqlIgnore!=null&&(sp.sqlIgnore.equalsIgnoreCase(SERVICE_TRUE)||sp.sqlIgnore.equalsIgnoreCase(SERVICE_YES)))
        htmlResponse.setSQLIgnore(true);//false is default
      //extremally session failed ... exception(exclusive) doctor's for connection close normally
      //if(debug)System.out.println("<html response>");
      try{htmlResponse.writeData(sp);}catch(Exception e){manager.getLog().write(session_id,ERROR_SESSION_FAILED,e.toString());}
      htmlResponse=null;
      /*not need used(allways new database class object)database.setUnblocked();for defaut database connections>1*/
    }
    database=null;
    if(need_disconnect){//disconnect
      if(connection!=null){
        connection.close();connection=null;
        manager.getLog().write(session_id,INFO_DATABASE_CONNECTION_CLOSED);
      }
    }
    }//else name found (sp.name.length()>0)
    //if(debug)System.out.println("<finish session> <-html response");
    this.finishSession(session_id,filename_table,buf_list,sp);
    /*
    //clear vectors and buffers
    filename_table.clear();filename_table=null;
    if(buf_list!=null){buf_list.clear();buf_list=null;}
    sp.paramList.clear();sp.paramList=null;
    sp.extraList.clear();sp.extraList=null;
    sp.paramTypeList.clear();sp.paramTypeList=null;
    sp.sqlList.clear();sp.sqlList=null;
    sp=null;
    //finish session
    log.write(session_id,INFO_SESSION_DEACTIVATED);
    */
  }
  private void finishSession(long session_id,Hashtable filename_table,Vector buf_list,ServletParam sp)
  {
    //clear vectors and buffers
    filename_table.clear();filename_table=null;
    if(buf_list!=null){buf_list.clear();buf_list=null;}
    sp.paramList.clear();sp.paramList=null;
    sp.extraList.clear();sp.extraList=null;
    sp.paramTypeList.clear();sp.paramTypeList=null;
    sp.sqlList.clear();sp.sqlList=null;
    sp=null;
    //finish session
    manager.getLog().write(session_id,INFO_SESSION_DEACTIVATED);
  }
  private int findIndex(byte[] buf,int from_index,byte[] find_buf)
  {
    int ret_val=-1;
    boolean find_index;
    if(buf!=null&&find_buf!=null){
      int buf_size=buf.length,find_buf_size=find_buf.length;
      for(int i=from_index;i<buf_size;i++){
        if((buf_size-i)>find_buf_size){//for end of buf left optimal bytes
          find_index=true;
          for(int j=0;j<find_buf_size;j++){
            if(buf[i+j]!=find_buf[j]){find_index=false;break;}
          }
          if(find_index){ret_val=i;break;}
        }
      }
    }
    return ret_val;
  }
  private byte[] readFromStream(ServletInputStream stream,int length)
  {
    byte[] buf=new byte[length];
    int ind=0,read_length,sleep_count=0,max_sleep_count=1000;//1 sec max wait between data portions
    try{while(ind<length){
      read_length=stream.read(buf,ind,SIZE_BUFFER_READ/*stream.available()*/);
      if(read_length==-1){buf=null;break;}//data failed
      else if(read_length==0){
        try{Thread.sleep(TIMEOUT_DATA_WAIT);}catch(InterruptedException i_e){}
        sleep_count++;
        //Convert.appendToFile(servletFilepath+LOCAL_DELIM+"sleep_count.txt",(Convert.toString(sleep_count)+NEXT_LINE).getBytes());
        if(sleep_count==max_sleep_count)break;
      }
      else{
        ind+=read_length;
        //Convert.appendToFile(servletFilepath+LOCAL_DELIM+"read_length.txt",(Convert.toString(read_length)+NEXT_LINE).getBytes());
        sleep_count=0;
      }
    }}catch(IOException io_e){}
    //if(buf!=null)Convert.writeToFile(servletFilepath+"/buf",buf);
    //Log.write("READ BUFFER SIZE="+Convert.toString(ind)+"\r\n");
    return buf;
  }
  //buf example:
  /*
  -----------------------------265001916915724
  content-Disposition: form-data; name="param8"; filename="1111.JPG"
  content-type: image/jpeg

  123445666768758768908648753159871234

  -----------------------------265001916915724
  content-Disposition: form-data; name="param9"

  param8
  -----------------------------265001916915724--
  */
  private Vector parseBuf(byte[] buf)//return Vector of BufferParam
  {
    Vector ret_val=null;
    if(buf!=null){
      BufferParam buf_param=null;
      byte[] content_buf=null;
      String str=EMPTY,s;//temp
      String content_type=null,content_disposition=null;
      String filename=null,name=null;
      String id=null;//content identifier
      int ind=0,index,index_begin,size;
      while(ind<buf.length){
        if(buf[ind]==CODE_RETURN);//code 0x0D
        else if(buf[ind]==CODE_NEXT){//code 0x0A
          if(id==null)id=str;//content identifier (first string)
          else if(str.equals(id));//content delimiter
          else if(str.startsWith(CONTENT_TYPE))content_type=Convert.getValue(str,DOUBLE_UP_POINT);//content type
          else if(str.startsWith(CONTENT_DISPOSITION)){//content disposition
            content_disposition=Convert.getValue(str,DOUBLE_UP_POINT);
            Vector v=Convert.getValues(content_disposition);
            for(int i=0;i<v.size();i++){
              s=(String)v.get(i);
              if(s.startsWith(CONTENT_DISPOSITION_FILENAME)){//filename
                filename=Convert.getValue(s);
                filename=filename.replaceAll(DOUBLE_UPPER,EMPTY);//trim "
              }
              if(s.startsWith(CONTENT_DISPOSITION_NAME)){//name
                name=Convert.getValue(s);
                name=name.replaceAll(DOUBLE_UPPER,EMPTY);//trim "
              }
            }
          }
          else if(str.length()==0&&content_disposition!=null){//content data
            index_begin=++ind;
            //Log.write("BEGIN INDEX="+Convert.toString(index_begin)+"\r\n");
            index=this.findIndex(buf,index_begin,id.getBytes());
            if(index!=-1){
              size=index-index_begin-2;//-2 for code 0x0D 0x0A left
              //Log.write("INDEX="+Convert.toString(index)+" SIZE="+Convert.toString(size)+"\r\n");
              content_buf=new byte[size];
              new ByteArrayInputStream(buf,index_begin,size).read(content_buf,0,size);
              //System.arraycopy(buf,index_begin,content_buf,0,size);
              //Log.write("END COPY\r\n");
            }
            //if(data[ind+1]=="-"&&data[ind+2]=="-")break;//must be end of buf[]
            if(ret_val==null)ret_val=new Vector();
            buf_param=new BufferParam();
            buf_param.contentType=content_type;
            buf_param.filename=filename;
            buf_param.name=name;
            buf_param.data=(byte[])content_buf;
            ret_val.add(buf_param);
            //Convert.writeToFile(servletFilepath+"/bufdata"+Convert.toString(ind),buf_param.data);
            content_type=null;content_disposition=null;
            filename=null;name=null;
            content_buf=null;
            ind=index+id.length();
          }
          str=EMPTY;
        }
        else str+=(char)buf[ind];//not work in russian context(need base64encode?)
        ind++;
      }
    }
    return ret_val;
  }
  public byte[] encodeData(String encode,byte[] data)//hide and unhide data
  {
    if(encode==null||encode.length()==0||data==null||data.length==0)return data;
    int encode_size=encode.length(),data_size=data.length;
    byte[] e=encode.getBytes();
    for(int i=0;i<data_size;i++)data[i]^=e[i%encode_size];
    return data;
  }
  public String encodeData(String encode,String data)//hide and unhide data
  {
    if(encode==null||encode.length()==0||data==null||data.length()==0)return data;
    int encode_size=encode.length(),data_size=data.length();
    byte[] e=encode.getBytes(),d=data.getBytes();
    for(int i=0;i<data_size;i++)d[i]^=e[i%encode_size];
    return new String(d);
  }
  public String toURL(String str)
  {
    String url=str;
    int index=str.indexOf(LOCAL_SYSTEM);//thinking about this ... (str is value of service.ini)
    if(index>-1)url=str.substring(index);
    url=LOCAL_DELIM+url.replace(LOCAL_DELIM_2,LOCAL_DELIM);
    return url;
  }
  //in GeocoderByAddress presents moderner version of getPage()
  //version use tesis that content_length == -1
  //This version use tesis that content_length may be a positive value
  //may be move to->clientSession
  public byte[] getPage(long session_id,String page_url,int data_wait_count)//by http url connection
  {
    byte[] page=null;
    int length,recv_length=0,avail_length,count=0;
    boolean wait_failed=false;
    try{
      URL url=new URL(page_url);
      try{
        InputStream connection_stream;
        ByteArrayOutputStream data_stream=null;
        byte[] data;
        HttpURLConnection http_connection;
        http_connection=(HttpURLConnection)url.openConnection();
        http_connection.setRequestProperty("User-Agent",LOCAL_NAME+LOCAL_DELIM+LOCAL_VERSION);
        http_connection.connect();
        length=http_connection.getContentLength();
        /*
        String str="Url="+page_url+NEXT_LINE;
        str+="ResponseCode="+http_connection.getResponseCode()+SPACE+http_connection.getResponseMessage()+NEXT_LINE;
        str+="Header="+http_connection.getHeaderFields().values().toString()+NEXT_LINE;
        str+="content="+http_connection.getContentType()+NEXT_LINE;
        str+="Length="+length;
        if(debug)System.out.println("<"+str+">");
        */
        if(http_connection.getResponseCode()>=200&&http_connection.getResponseCode()<300){
          connection_stream=http_connection.getInputStream();
          data_stream=new ByteArrayOutputStream();
          do{
            avail_length=connection_stream.available();
            if(avail_length>0){
              data=new byte[avail_length];
              avail_length=connection_stream.read(data);
              data_stream.write(data,0,avail_length);
              recv_length+=avail_length;
              data=null;
            }
            else{
              if(length==-1)break;//if length==-1 is a bad code ->
	            //if(debug)System.out.println("<http data wait>");
              try{Thread.sleep(manager.getLocalTimeout());}catch(InterruptedException i_e){}//waiting for data ...
              if(++count==data_wait_count){wait_failed=true;break;}//count try again failed ...
            }
          }while(recv_length<length);
          if(wait_failed)manager.getLog().write(session_id,ERROR_CONTENT_FAILED,page_url);
          page=data_stream.toByteArray();
          manager.getLog().write(session_id,INFO_READ_RESPONSE,page_url+MESSAGE_DELIM_SUBVALUES+length);
        }//if
        http_connection.disconnect();http_connection=null;
        if(data_stream!=null){data_stream.close();data_stream=null;}
      }catch(IOException io_e){}
    }catch(MalformedURLException mu_e){manager.getLog().write(session_id,ERROR_INVALID_REQUEST,page_url+SPACE+mu_e.toString());}
    return page;
  }
}
////////////////////////////////////////////////////////////////////////////////
//ServiceSession
class Session extends Thread implements Interface
{
  private Manager manager=null;
  private int type=0;
  private long sessionID=0;
  private Socket socket=null;
  private boolean closed=false;
  private boolean needClose=false;
  private long startTime=0;
  //[constructor]
  public Session(){}
  public void open(Manager manager,int type,long session_id)
  {
    this.manager=manager;
    this.type=type;
    sessionID=session_id;
    startTime=System.currentTimeMillis();
  }
  public void open(Manager manager,int type,long session_id,Socket socket)
  {
    this.manager=manager;
    this.type=type;
    sessionID=session_id;
    this.socket=socket;
    startTime=System.currentTimeMillis();
  }
  public void close()
  {
    needClose=true;
    //interrupt();
    while(!closed)try{sleep(TIMEOUT_SESSION_SLEEP);}catch(InterruptedException i_e){}
  }
  @Override
  public void run()
  {
    closed=false;
    needClose=false;
    int timeout=manager.getLocalTimeout(),timeout_database_files=manager.getDatabaseTimeout();
    try{
    switch(type){
    case SESSION_TYPE_NET_LISTENER:{
      String address=manager.getInitial().getLocalAddress();
      address.trim();
      manager.getLog().write(sessionID,INFO_SESSION_ACTIVATED,SESSION_NAME_NET_LISTENER+MESSAGE_DELIM_SUBVALUES+address);
      while(address!=null&&!needClose){
      try{
        if(address.length()==0){manager.getLog().write(sessionID,WARNING_INVALID_LOCAL_ADDRESS,address);break;}
        String host=Convert.getHost(address),port=Convert.getPort(address);
        host.trim();port.trim();
        if(host.length()==0){manager.getLog().write(sessionID,WARNING_INVALID_LOCAL_HOST);break;}
        if(port.length()==0){manager.getLog().write(sessionID,WARNING_INVALID_LOCAL_PORT);break;}
        //TCPServerSocket server=new TCPServerSocket(host,Convert.toIntValue(port));
        TCPServerSocket server=new TCPServerSocket();
        server.setLocalHost(host);
        server.setLocalPort(port);
        if(!server.open()){manager.getLog().write(sessionID,WARNING_ADDRESS_NOT_FOUND,address);break;}
        server.setTimeout(TIMEOUT_SERVER_SOCKET_LISTEN);
        //currentThread().setName(SESSION_NAME_NET_LISTENER);
        Socket socket;
        Session session;
        while(!needClose){
          //if(manager.isDebug())System.out.print(DEBUG_NET_LISTENER_POINT);
          if((socket=server.accept())!=null){//new session user connection
            try{socket.setSoTimeout(TIMEOUT_SOCKET_LISTEN);}catch(SocketException s_e){}
            try{
              while(!needClose&&Runtime.getRuntime().freeMemory()<SIZE_FREE_MEMORY){//free memory wait
                manager.getLog().write(sessionID,WARNING_SESSION_WAITING,socket.getInetAddress().getHostAddress());
                System.runFinalization();//run delete on classes
                System.gc();//clear trash
              }
              session=new Session();
              session.open(manager,SESSION_TYPE_USER_CONNECTION,manager.getNewSessionID(),socket);
              session.start();
              manager.getSessions().add(session);
            }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString());}
          }
        }
        server.close();
        server=null;
        break;
      }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString());}
      }
      manager.getLog().write(sessionID,INFO_SESSION_DEACTIVATED,SESSION_NAME_NET_LISTENER);
      break;
    }
    case SESSION_TYPE_USER_CONNECTION:{
      manager.getLog().write(sessionID,INFO_SESSION_ACTIVATED,SESSION_NAME_USER_CONNECTION+MESSAGE_DELIM_SUBVALUES+socket.getInetAddress().getHostAddress()+MESSAGE_DELIM_HOST_PORT+Convert.toString(socket.getPort()));
      InvokeMethod im=new InvokeMethod();
      String s=null;
      Object o;
      o=im.start(s);
      if(o!=null&&o instanceof UserConnection){
        manager.getLog().write(sessionID,INFO_SERVICE_STARTED,s);
        ((UserConnection)o).setSocket(socket);
        ((UserConnection)o).start();
        while(!needClose){
          try{Thread.sleep(timeout);}catch(InterruptedException i_e){}//sleep sometimes
        }
        ((UserConnection)o).close();
      }
      manager.getLog().write(sessionID,INFO_SESSION_DEACTIVATED,SESSION_NAME_USER_CONNECTION);
      break;
    }
    case SESSION_TYPE_SERVICES_CONTROL:{
      int service_count=manager.getInitial().getServiceStartsList().size();
      if(service_count>0){
        manager.getLog().write(sessionID,INFO_SESSION_ACTIVATED,SESSION_NAME_SERVICES_CONTROL+MESSAGE_DELIM_SUBVALUES+service_count);
        Vector v=manager.getInitial().getServiceStartsList();
        InvokeMethod im=new InvokeMethod();
        boolean is_start;
        String s,time_s,start_time,finish_time;
        Date date;
        int start_hours,start_minutes,finish_hours,finish_minutes;
        int current_hours,current_minutes;
        Object o;
        int i;
        while(!needClose){
          try{Thread.sleep(timeout);}catch(InterruptedException i_e){}//sleep sometimes
          try{
          // execution and control service from [service]->Start name parameters
          for(Enumeration e=v.elements();e.hasMoreElements();){
            s=(String)e.nextElement();
            is_start=true;
            i=s.indexOf('|');
            if(i>0){//time start (01:00-10:30|...)
              time_s=s.substring(0,i).trim();
              s=s.substring(i+1);//kill start time substring (01:00-10:30|start string)
              if(time_s.length()==11){//01:00-10:30
                start_time=time_s.substring(0,5);//time_s.substring(0,tims_s.indexOf('-')).trim();
                finish_time=time_s.substring(6);//time_s.substring(tims_s.indexOf('-')+1).trim();
                start_hours=new Integer(start_time.substring(0,2)).intValue();//start_time.substring(0,start_time.indexOf(':')).trim();
                start_minutes=new Integer(start_time.substring(3)).intValue();//start_time.substring(start_time.indexOf(':')+1).trim();
                finish_hours=new Integer(finish_time.substring(0,2)).intValue();//finish_time.substring(0,finish_time.indexOf(':')).trim();
                finish_minutes=new Integer(finish_time.substring(3)).intValue();//finish_time.substring(finish_time.indexOf(':')+1).trim();
                date=new Date();
                current_hours=date.getHours();
                current_minutes=date.getMinutes();
                if(/*jump to 24 and jump from 0 -> 23:00-03:00*/
                  (start_hours>finish_hours&&
 ((current_hours>start_hours&&current_hours<24)||(current_hours<finish_hours&&current_hours>=0))
 )||
                   /*normal interval -> 00:30-03:00*/
                  (start_hours<finish_hours&&
 (current_hours>start_hours&&current_hours<finish_hours)
 )||
                  /*minutes in control hour -> 23:00 - 00:30 or 23:15-23:30*/
                  (start_hours!=finish_hours&&
 ((current_hours==start_hours&&current_minutes>=start_minutes)||
                        (current_hours==finish_hours&&current_minutes<=finish_minutes))
 )||
                  (start_hours==finish_hours&&
 (current_hours==start_hours&&current_minutes>=start_minutes&&current_minutes<=finish_minutes))
                );//if
                else is_start=false;
              }
            }
            if(is_start){
              /*if(manager.isDebug())
              manager.getLog().write("---current time="+Integer.toString(current_hours)+":"+Integer.toString(current_minutes)+
                                     "|start time="+Integer.toString(start_hours)+":"+Integer.toString(start_minutes)+
                                     "|finish time="+Integer.toString(finish_hours)+":"+Integer.toString(finish_minutes)+"---\r\n");
              */
              o=im.start(s);
              if(o!=null&&o instanceof String)manager.getLog().write(sessionID,INFO_SERVICE_MESSAGE,s+MESSAGE_DELIM_SUBVALUES+(String)o);
            }
            if(needClose)break;
            try{Thread.sleep(timeout);}catch(InterruptedException i_e){}//sleep sometimes
          }//for
          }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString());}
        }//while
      }//if service_count>0
      manager.getLog().write(sessionID,INFO_SESSION_DEACTIVATED,SESSION_NAME_SERVICES_CONTROL);
      break;
    }
    case SESSION_TYPE_SESSIONS_CONTROL:{
      String str;
      Session session;
      Date date;
      long currentTime;
      int current_hours,current_minutes;
      Log log;
      boolean day_log=false;
      manager.getLog().write(sessionID,INFO_SESSION_ACTIVATED,SESSION_NAME_SESSIONS_CONTROL);
      while(!needClose&&manager.getSessions()!=null){try{
        //if(manager.isDebug())System.out.print(DEBUG_SESSIONS_CONTROL_POINT);
        log=manager.getLog();
        //log file search ...
        if(log!=null&&log.getFile()!=null){
          date=new Date();
          current_hours=date.getHours();
          current_minutes=date.getMinutes();
          if(!day_log&&current_hours==0&&current_minutes==0){
            log.close();
            str=log.getFilepath();
            log.open(str,manager);
            day_log=true;
          }
          else if(day_log&&current_hours==0&&current_minutes>0)day_log=false;
        }
        //died sessions search ...
        manager.getSessions().trimToSize();
        for(Enumeration e=manager.getSessions().elements();e.hasMoreElements();){
          session=(Session)e.nextElement();
          //check died sessions ...
          if(session!=null&&!session.isAlive()&&session.isClosed()){manager.getSessions().remove((Object)session);session=null;}
        }
        //[user database sessions]
        //user database connections control
        if(manager.getDatabaseSessions()!=null&&manager.getDatabaseSessionsCount()>0){//seek database session
          DatabaseSession database_session;
          boolean remove_session;
          for(Enumeration e=manager.getDatabaseSessions().elements();e.hasMoreElements();){//sessions list
            database_session=(DatabaseSession)e.nextElement();
            if(database_session!=null&&database_session.databaseConnection!=null){
              remove_session=false;
              if(database_session.databaseConnection.isClosed())remove_session=true;
              else{
                if(System.currentTimeMillis()-manager.getDatabaseSessionsTimeout()>database_session.timeout){
                  database_session.databaseConnection.close();
                  manager.getLog().write(database_session.sessionID,INFO_DATABASE_CONNECTION_CLOSED,database_session.databaseConnection.getConnectionLogin());
                  remove_session=true;
                }
              }
              if(remove_session){//remove session from pool
                manager.getDatabaseSessions().remove(database_session.databaseConnection.getConnectionLogin());//remove by login
                manager.getLog().write(database_session.sessionID,INFO_DATABASE_SESSION_REMOVED,database_session.databaseConnection.getConnectionLogin());
                database_session.databaseConnection=null;database_session.database=null;database_session=null;
              }
            }//if
          }//for
        }//if databaseSessions&databaseSessionsCount>0
        //[default database sessions]
        //need reconnect to database(default) ...
        // ... -> no recconnect here(user connecting itself)
        //need reconnect to database(log) ...
        if(manager.getDatabaseLog()!=null&&manager.getDatabaseLog().length()>0){//log->database
          LogDatabase log_database=null;
          DatabaseConnection log_connection=null;
          Hashtable default_sessions=manager.getDefaultDatabaseSessions();
          DatabaseSession database_session=null;
          if(default_sessions!=null)database_session=(DatabaseSession)default_sessions.get(SESSION_KEY_LOG_DATABASE_CONNECTION);
          if(database_session!=null)log_connection=database_session.databaseConnection;
          if(log_connection==null||log_connection.getConnection()==null||log_connection.getConnection().isClosed()){//connection failed
            //log database reconnection
            log_connection=new DatabaseConnection();
            log_connection.open(manager,manager.getSessionID()/*sessionID==0*/,manager.getDatabaseLog());
            if(log_connection.isOpened()){
              log_database=new LogDatabase(log_connection);
              if(database_session!=null){database_session.database=log_database;database_session.databaseConnection=log_connection;}
              else{
                if(manager.getDefaultDatabaseSessions()==null){
                  default_sessions=new Hashtable();
                  manager.setDefaultDatabaseSessions(default_sessions);
                }
                database_session=new DatabaseSession();
                database_session.database=log_database;
                database_session.databaseConnection=log_connection;
                database_session.type=SESSION_TYPE_LOG_DATABASE_CONNECTION;
                database_session.name=SESSION_NAME_LOG_DATABASE_CONNECTION;
                database_session.sessionID=manager.getSessionID();//SessionID==0
                default_sessions.put(SESSION_KEY_LOG_DATABASE_CONNECTION,database_session);
                manager.setLog2Database(true);/*flag for operative use*/
              }
              manager.getLog().write(sessionID,INFO_DATABASE_RECONNECTION,log_connection.getConnectionLogin());
            }
          }
        }//if
        //timeout blacklist control
        if(manager.getTimeoutBlackList()!=null&&manager.getTimeoutBlackList().size()>0){//seek ends timeout users
           InvalidLogin inv_login;
           /*work by collection->identical work for code(2 enum) see down
           for(Collection c=manager.getTimeoutBlackList().values();c.iterator().hasNext();){
             inv_login=(InvalidLogin)c.iterator().next();
             if(inv_login!=null&&(System.currentTimeMillis()-manager.getDatabaseBlackListTimeout()>inv_login.timeout)){
               c.iterator().remove();//so changes to the Hashtable are reflected in the Collection, and vice-versa
               inv_login=null;
             }//if
           }//for
           */
           Enumeration keys=manager.getTimeoutBlackList().keys();
           Enumeration values=manager.getTimeoutBlackList().elements();
           while(keys.hasMoreElements()&&values.hasMoreElements()){
             inv_login=(InvalidLogin)values.nextElement();
             str=(String)keys.nextElement();
             if(inv_login!=null&&(System.currentTimeMillis()-manager.getDatabaseBlackListTimeout()>inv_login.timeout)){
               manager.getTimeoutBlackList().remove(str);
               manager.getLog().write(sessionID,INFO_REMOVED_FROM_TIMEOUT_BLACKLIST,str);
               inv_login=null;
             }
           }//while
        }//if
        //trash old files search ...
        currentTime=System.currentTimeMillis();
        if(startTime<currentTime){//Systemtime not change
          str=manager.getInitial().getServiceTrash();//ini service trash dir
          if(str==null)str=manager.getServletFilepath()+FILEPATH_SERVICE_TRASH;
          else if(!str.endsWith(LOCAL_DELIM_2))str+=LOCAL_DELIM_2;
          File dir=new File(str);
          String[] files=dir.list();
          if(dir.isDirectory()&&files!=null){
            int files_count=files.length;
            File file;
            for(int i=0;i<files_count;i++){
              file=new File(str+files[i]);
              if(file.isFile()){//check very old files ...
                if(currentTime-file.lastModified()>timeout_database_files)file.delete();//remove old file
              }
              //else ... go to directory
              file=null;
              if(needClose)break;
            }
          }
          dir=null;files=null;
        }
        if(timeout>0)try{sleep(timeout);}catch(InterruptedException i_e){}
      }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString());}
      }//while
      manager.getLog().write(sessionID,INFO_SESSION_DEACTIVATED,SESSION_NAME_SESSIONS_CONTROL);
      break;
    }//case
    }//switch
    }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_FAILED,e.toString());}
    closed=true;
  }
  public int getType(){return type;}
  public long getSessionID(){return sessionID;}
  public boolean isClosed(){return closed;}
  public boolean isNeedClose(){return needClose;}
  public void setNeedClose(boolean need_close){needClose=need_close;}
  public Socket getSocket(){return socket;}
  public long getStartTime(){return startTime;}
}
//for used in [Local] group parameters as address=server.com:1001|package.Class.method(..)
//need realized start function with local parameters initialized such as: UDPConnection.open(String str);
abstract class UserConnection extends Thread
{
  private Socket socket=null;
  //abstract run();//inside main work->executing by start();
  abstract void close();//finish of run() method work
  public void setSocket(Socket socket){this.socket=socket;}
  public Socket getSocket(){return socket;}
}
////////////////////////////////////////////////////////////////////////////////
//[tools classes]
//--------------------------database connection-------------------------------//
//database connection to ...
class DatabaseConnection implements Interface,tools.Interface
{
  //oracle.jdbc.driver.OracleDriver
  //jdbc:oracle:thin:@OEM-1547:1521:ORCL
  //com.mysql.jdbc.Driver
  //jdbc:mysql://localhost:3306/database
  private Manager manager=null;
  private Connection connection=null;//reserved for registrations
  private String connectionAddress=null;
  private String connectionLogin=null;
  private String connectionPassword=null;
  private String connectionDatabase=null;
  private String connectionType=null;
  private String connectionDriver=null;
  private boolean opened=false;
  private int errorCode=0;
  //[get]
  public Connection getConnection(){return connection;}
  public String getConnectionAddress(){return connectionAddress;}
  public String getConnectionLogin(){return connectionLogin;}
  public String getConnectionPassword(){return connectionPassword;}
  public String getConnectionDatabase(){return connectionDatabase;}
  public String getConnectionType(){return connectionType;}
  public String getConnectionDriver(){return connectionDriver;}
  public boolean isOpened(){return opened;}
  public int getErrorCode(){return errorCode;}
  public boolean isClosed(){
    boolean ret_val=true;
    try{if(connection!=null)ret_val=connection.isClosed();}catch(SQLException sql_e){errorCode=sql_e.getErrorCode();}
    return ret_val;
  }
  //[constructor]
  public DatabaseConnection(){}
  private boolean isEnigma(String username)
  {
    boolean ret_val=manager.getDatabasePassword();//instruction for all users
    if(manager.getDatabaseUsersList()==null)return ret_val;//critical section (not users found)
    String user,s1,s2;
    int i1;
    int size=manager.getDatabaseUsersList().size();
    for(int i=0;i<size;i++){
      user=(String)manager.getDatabaseUsersList().get(i);
      if(user!=null&&user.length()>0){
        s1=null;s2=null;
        i1=user.indexOf(DOUBLE_UP_POINT);
        if(i1>0){
          s1=user.substring(0,i1).trim();
          if(user.length()-1>i1)s2=user.substring(i1+1).trim();
        }else s1=user;
        //Manager.getLog().write("is enigma user->"+username+" record->"+s1+"|"+s2+"\r\n");
        if(s1!=null&&s1.equalsIgnoreCase(username)){//instruction for this user
          if(s2!=null&&s2.equalsIgnoreCase(ON))return ret_val=true;//enigma
          else return ret_val=false;//no enigma
        }//if
      }//if
    }//for
    return ret_val;
  }
  private String getEnigma(String str)
  {
    byte[] data=str.getBytes();
    byte[] enigma_data=manager.getEnigmaData();
    int data_size=data.length,enigma_size=enigma_data.length;
    for(int i=0;i<data_size;i++)data[i]^=enigma_data[i%enigma_size];
    return new String(data);
  }
  public void open(Manager manager,long session_id)
  {
    this.manager=manager;
    String address1=this.manager.getInitial().getDatabasePrimaryAddress();
    String address2=this.manager.getInitial().getDatabaseSecondaryAddress();
    String database_driver=this.manager.getInitial().getDatabaseDriver();
    String database_type=this.manager.getInitial().getDatabaseType();
    String database=(address1!=null&&address1.length()>0)?Convert.getValue(address1,DELIM_DATABASE):null;
    String username=(address1!=null&&address1.length()>0)?Convert.getParam((Convert.getParam(address1,DELIM_DATABASE)),DELIM_USER_PASSWORD):null;
    String password=(address1!=null&&address1.length()>0)?Convert.getValue((Convert.getParam(address1,DELIM_DATABASE)),DELIM_USER_PASSWORD):null;
    this.open(manager,session_id,database_driver,database_type,database,username,password);
    if(this.isOpened()){connectionAddress=address1;connectionLogin=username;connectionPassword=password;connectionDatabase=database;connectionType=database_type;connectionDriver=database_driver;}
    else{
      database=(address2!=null&&address2.length()>0)?Convert.getValue(address2,DELIM_DATABASE):null;
      username=(address2!=null&&address2.length()>0)?Convert.getParam((Convert.getParam(address2,DELIM_DATABASE)),DELIM_USER_PASSWORD):null;
      password=(address2!=null&&address2.length()>0)?Convert.getValue((Convert.getParam(address2,DELIM_DATABASE)),DELIM_USER_PASSWORD):null;
      this.open(manager,session_id,database_driver,database_type,database,username,password);
      if(this.isOpened()){connectionAddress=address2;connectionLogin=username;connectionPassword=password;connectionDatabase=database;connectionType=database_type;connectionDriver=database_driver;}
    }
  }
  public void open(Manager manager,long session_id,String address)
  {
    this.manager=manager;
    String database_driver=this.manager.getInitial().getDatabaseDriver();
    String database_type=this.manager.getInitial().getDatabaseType();
    String database=(address!=null&&address.length()>0)?Convert.getValue(address,DELIM_DATABASE):null;
    String username=(address!=null&&address.length()>0)?Convert.getParam((Convert.getParam(address,DELIM_DATABASE)),DELIM_USER_PASSWORD):null;
    String password=(address!=null&&address.length()>0)?Convert.getValue((Convert.getParam(address,DELIM_DATABASE)),DELIM_USER_PASSWORD):null;
    this.open(manager,session_id,database_driver,database_type,database,username,password);
    if(this.isOpened()){connectionAddress=address;connectionLogin=username;connectionPassword=password;connectionDatabase=database;connectionType=database_type;connectionDriver=database_driver;}
  }
  public void open(Manager manager,long session_id,String database_driver,String database_type,String database,String username,String password)
  {
    this.manager=manager;
    opened=false;
    //driver has been registered if then was uregistered ...
    //DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());//load prepared oracle driver
    //DriverManager.registerDriver(new com.mysql.jdbc.Driver());//load prepared mysql driver
    //if(manager.isDebug())System.out.println("->Class.forName");
    try{Class.forName(database_driver);}catch(ClassNotFoundException class_not_found_e){this.manager.getLog().write(session_id,ERROR_DRIVER_NOT_FOUND,database_driver+MESSAGE_DELIM_SUBVALUES+class_not_found_e.getLocalizedMessage());}
    try{
      if(database!=null&&database.length()>0&&username!=null&&username.length()>0&&password!=null&&password.length()>0){
        if(database_type.equalsIgnoreCase(DATABASE_TYPE_ORACLE)){//connect to oracle
          //enigma
          //if(manager.isDebug())System.out.println("->isEnigma");
          if(this.isEnigma(username))username=this.getEnigma(username);
          //if(manager.isDebug())System.out.println("->DriverManager.getConnection");
          connection=DriverManager.getConnection(JDBC_ORACLE+database,username,password);
        }
        else if(database_type.equalsIgnoreCase(DATABASE_TYPE_MYSQL)){//connect to mysql
          //enigma
          if(this.isEnigma(username))username=this.getEnigma(username);
          connection=DriverManager.getConnection(JDBC_MYSQL+database,username,password);
        }
        else connection=DriverManager.getConnection(database,username,password);
      }
      else{//default connection by address string
        connection=DriverManager.getConnection(database);
      }
      if(connection!=null){opened=true;connectionAddress=database;connectionLogin=username;connectionPassword=password;connectionDatabase=database;connectionType=database_type;connectionDriver=database_driver;}
      else this.manager.getLog().write(session_id,ERROR_DATABASE_CONNECTION_FAILED,database);
    }catch(SQLException sql_e){this.manager.getLog().write(session_id,WARNING_DATABASE_CONNECTION_NOT_ESTABLISHED,database+MESSAGE_DELIM_SUBVALUES+sql_e.getLocalizedMessage());errorCode=sql_e.getErrorCode();}
  }
  public boolean validConnection(String sql)
  {
    boolean ret_val=false;
    Statement stmt=null;
    try{
      stmt=connection.createStatement();
      if(stmt!=null)stmt.executeQuery(sql);
      ret_val=true;
    }catch(SQLException sql_e){}
    finally{
      try{if(stmt!=null)stmt.close();stmt=null;}catch(SQLException sql_e){}
    }
    return ret_val;
  }
  public void close()
  {
    try{
      if(connection!=null){connection.close();connection=null;connectionAddress=null;}
    }catch(SQLException sql_e){errorCode=sql_e.getErrorCode();}
    opened=false;
  }
}
//--------------------------------database------------------------------------//
//get and put data from database ...
class Database implements Interface,tools.Interface
{
  private Manager manager=null;
  private DatabaseConnection connection=null;
  private boolean differenceCodepage=false;
  //private int errorCode=0;//not used in class
  private int columnCount=0;//result of fetch columns set after getGridData(...)
  public int getColumnCount(){return columnCount;}
  private boolean htmlCode=false;//replace specific symbols("'<>) for html code from database
  public void setHtmlCode(boolean html_code){htmlCode=html_code;}
  //public int getErrorCode(){return errorCode;}//not used in class
  //[constructor]
  public Database(Manager manager,DatabaseConnection connection)
  {
    this.manager=manager;
    this.connection=connection;
    differenceCodepage=this.manager.getSystemCodepage().equalsIgnoreCase(this.manager.getLocalCodepage())?false:true;
  }
  private Vector getParamIndex(ServletParam sp)//array of param index (?0) (if index not found->EMPTY)
  {
    Vector ret_val=new Vector();//return array of param index
    String str;
    int index,last_index,i=0,val,length,sql_type,ret_index;//==-1 returning not found
    sp.returnParamIndex=-1;sp.reparsedSQL=null;
    if(sp.sql.startsWith(SQL_SELECT)||sp.sql.startsWith(SQL_SELECT_UPPER_CASE))sql_type=1;
    else if(sp.sql.startsWith(SQL_INSERT)||sp.sql.startsWith(SQL_INSERT_UPPER_CASE))sql_type=2;
    else sql_type=0;
    //Warning not use empty text '' in SELECT '' FROM DUAL -> use blank space in SELECT ' ' FROM DUAL
    StringTokenizer st=new StringTokenizer(sp.sql,UPPER);//seek parts of SELECT 'text', ...
    while(st.hasMoreTokens()){//seek parts of sql, empty parts lefts(such as '')!
      str=st.nextToken().trim();
      length=str.length();
      val=i%2;
      index=0;
      //Manager.getLog().write("sql PART="+str);
      if(val==0){//part not between '...'
        //seek phrase index
        if(sql_type==1){//select
          if((ret_index=str.indexOf(SQL_INTO))==-1)ret_index=str.indexOf(SQL_INTO_UPPER_CASE);
        }
        else if(sql_type==2){//insert
         if((ret_index=str.indexOf(SQL_RETURNING))==-1)ret_index=str.indexOf(SQL_RETURNING_UPPER_CASE);
         if(ret_index!=-1){//returning into
           if((ret_index=str.lastIndexOf(SQL_INTO))==-1)ret_index=str.lastIndexOf(SQL_INTO_UPPER_CASE);
         }
        }
        else ret_index=-1;//other
        while((index=str.indexOf('?',index))!=-1){
          index++;
          if(index<length){
            //[0..9]->(0x30..0x39)
            if(str.charAt(index)<0x30&&str.charAt(index)>0x39){//not number at position
              ret_val.add(EMPTY);
            }//if
            else{//number after '?'
              last_index=index;
              do{
                last_index++;
              }while(last_index<length&&str.charAt(last_index)>=0x30&&str.charAt(last_index)<=0x39);//number at position
              ret_val.add(str.substring(index,last_index));
              index=last_index;
            }//else
          }//if index<length
          else ret_val.add(EMPTY);
          if(ret_index!=-1&&index>ret_index&&sp.returnParamIndex==-1){//return param into phrase
            for(int ind=ret_index+4/*left into*/;ind<index;ind++){//seek return param
              if(str.charAt(ind)=='?'){//return param
                try{
                  sp.returnParamIndex=Convert.toIntValue((String)ret_val.get(ret_val.size()-1))/*last array index*/;
                }catch(Exception e){}
                break;
              }
              else if(str.charAt(ind)!=' ')break;
            }
          }
        }//while
      }//if
      i++;
    }
    //changes for ver. later jdbc1.4 (Oracle Thin driver)
    //no more used ?1,?2,?3->need to left numbers in oracle ver.jdbc5,jdbc6 after jdbc14
    if(ret_val.size()>0){//reparsing sql
      String rep_sql=sp.sql/*reparsed sql*/;
      int l=rep_sql.length(),len;
      for(Enumeration e=ret_val.elements();e.hasMoreElements();){
        str=(String)e.nextElement();
        //Manager.getLog().write("?_index="+str+"\r\n");
        length=str.length();
        if(length>0){
          str="?"+str;index=rep_sql.indexOf(str);
          if(index!=1){
            len=index+length;
            rep_sql=rep_sql.substring(0,index+1)+(len+1<l?rep_sql.substring(len+1):EMPTY);
          }
        }
      }
      sp.reparsedSQL=rep_sql;
    }
    return ret_val;
  }
  //1S->one string value
  public String getStringValue(long session_id,ServletParam sp)
  {
    String ret_val=null;
    PreparedStatement prestmt=null;
    Statement stmt=null;
    ResultSet rset=null;
    Vector sql_param=sp.paramList;
    Vector param_index=this.getParamIndex(sp);
    String sql=(sp.reparsedSQL!=null?sp.reparsedSQL:sp.sql),param;
    int param_count=param_index.size()/*,record_count=0*/;
    sp.sqlReturnType=SQL_UNKNOWN_TYPE;//set no type(unknown)
    sp.sqlErrorMessage=null;
    if(differenceCodepage)
      try{sql=new String(sql.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}//decode ISO-8859-1 -> Cp1251
    try{
      if(param_count>0&&sql_param.size()>=param_count){
        prestmt=connection.getConnection().prepareStatement(sql);
        prestmt.clearParameters();
        for(int i=0;i<param_count;i++){
          if(param_index.get(i).equals(EMPTY)){param=(String)sql_param.get(sp.paramIndex);sp.paramIndex++;}
          else param=(String)sql_param.get(Convert.toIntValue((String)param_index.get(i)));
          prestmt.setString(i+1,param);
        }
        rset=prestmt.executeQuery();
      }
      else{
        stmt=connection.getConnection().createStatement();
        rset=stmt.executeQuery(sql);
      }
      if(rset.next()){
        ret_val=rset.getString(1);
        if(ret_val!=null){
          if(!htmlCode)ret_val=ret_val.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
          try{ret_val=new String(ret_val.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
        }
        else ret_val=EMPTY;
        /*record_count++;*/
      }
      manager.getLog().write(session_id,INFO_READ_DATA,sp.sql+MESSAGE_DELIM_SUBVALUES+EQUAL+ret_val);
    }catch(SQLException sql_e){
      manager.getLog().write(session_id,ERROR_SQL_QUERY_FAILED,sp.sql+MESSAGE_DELIM_SUBVALUES+sql_e.getErrorCode()+MESSAGE_DELIM_SUBVALUES+sql_e.getLocalizedMessage());
      sp.sqlReturnType=SQL_ERROR_TYPE;//set error type
      sp.sqlErrorMessage=sql_e.getLocalizedMessage();
      try{sp.sqlErrorMessage=new String(sp.sqlErrorMessage.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
      if(!htmlCode)sp.sqlErrorMessage=sp.sqlErrorMessage.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
    }
    finally{
      try{if(rset!=null)rset.close();rset=null;if(prestmt!=null)prestmt.close();prestmt=null;if(stmt!=null)stmt.close();stmt=null;}catch(SQLException sql_e){}
    }
    return ret_val;
  }
  //1B(S)->one bytes value as string
  public String getBytesValueAsBase64String(long session_id,ServletParam sp)
  {
    String ret_val=null;
    byte[] bytes_value=null;
    PreparedStatement prestmt=null;
    Statement stmt=null;
    ResultSet rset=null;
    Vector sql_param=sp.paramList;
    Vector param_index=this.getParamIndex(sp);
    String sql=(sp.reparsedSQL!=null?sp.reparsedSQL:sp.sql),param;
    int param_count=param_index.size()/*,record_count=0*/;
    sp.sqlReturnType=SQL_UNKNOWN_TYPE;//set no type(unknown)
    sp.sqlErrorMessage=null;
    if(differenceCodepage)
      try{sql=new String(sql.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}//decode ISO-8859-1 -> Cp1251
    try{
      if(param_count>0&&sql_param.size()>=param_count){
        prestmt=connection.getConnection().prepareStatement(sql);
        prestmt.clearParameters();
        for(int i=0;i<param_count;i++){
          if(param_index.get(i).equals(EMPTY)){param=(String)sql_param.get(sp.paramIndex);sp.paramIndex++;}
          else param=(String)sql_param.get(Convert.toIntValue((String)param_index.get(i)));
          prestmt.setString(i+1,param);
        }
        rset=prestmt.executeQuery();
      }
      else{
        stmt=connection.getConnection().createStatement();
        rset=stmt.executeQuery(sql);
      }
      if(rset.next()){
        bytes_value=rset.getBytes(1);
        if(bytes_value!=null){
          ret_val=new /*sun.misc.*/BASE64Encoder().encode(bytes_value);//BASE64->replace by native code
        }
        /*record_count++;*/
      }
      manager.getLog().write(session_id,INFO_READ_DATA,sp.sql+(bytes_value!=null?MESSAGE_DELIM_SUBVALUES+EQUAL+bytes_value.length:EMPTY));
    }catch(SQLException sql_e){
      manager.getLog().write(session_id,ERROR_SQL_QUERY_FAILED,sp.sql+MESSAGE_DELIM_SUBVALUES+sql_e.getErrorCode()+MESSAGE_DELIM_SUBVALUES+sql_e.getLocalizedMessage());
      sp.sqlReturnType=SQL_ERROR_TYPE;//set error type
      sp.sqlErrorMessage=sql_e.getLocalizedMessage();
      try{sp.sqlErrorMessage=new String(sp.sqlErrorMessage.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
      if(!htmlCode)sp.sqlErrorMessage=sp.sqlErrorMessage.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
    }
    finally{
      try{if(rset!=null)rset.close();rset=null;if(prestmt!=null)prestmt.close();prestmt=null;if(stmt!=null)stmt.close();stmt=null;}catch(SQLException sql_e){}
    }
    return ret_val;
  }
  //tools
  private byte[] getBytesFromStream(InputStream i_s){
    byte[] ret_val=null;
    ByteArrayOutputStream b_s=null;
    DataOutputStream d_s=null;
    byte[] b=new byte[SIZE_BUFFER_READ];
    int b_size;
    try{
      b_s=new ByteArrayOutputStream();
      d_s=new DataOutputStream(b_s);
      while(true){
        b_size=i_s.read(b);
        if(b_size>0)d_s.write(b,0,b_size);
        else if(b_size==0)try{Thread.sleep(TIMEOUT_DATA_WAIT);}catch(InterruptedException i_e){}//sleep->wait data
        else if(b_size==-1)break;
      }
      ret_val=b_s.toByteArray();
      if(d_s!=null)d_s.close();
      if(b_s!=null)b_s.close();
    }catch(IOException io_e){}
    d_s=null;b_s=null;b=null;
    return ret_val;
  }
  //1B(S)->BLOB as string
  public String getBytesAsBase64String(long session_id,ServletParam sp)
  {
    String ret_val=null;
    byte[] bytes=null;
    PreparedStatement prestmt=null;
    Statement stmt=null;
    ResultSet rset=null;
    Vector sql_param=sp.paramList;
    Vector param_index=this.getParamIndex(sp);
    String sql=(sp.reparsedSQL!=null?sp.reparsedSQL:sp.sql),param;
    int param_count=param_index.size()/*,record_count=0*/;
    sp.sqlReturnType=SQL_UNKNOWN_TYPE;//set no type(unknown)
    sp.sqlErrorMessage=null;
    if(differenceCodepage)
      try{sql=new String(sql.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}//decode ISO-8859-1 -> Cp1251
    try{
      if(param_count>0&&sql_param.size()>=param_count){
        prestmt=connection.getConnection().prepareStatement(sql);
        prestmt.clearParameters();
        for(int i=0;i<param_count;i++){
          if(param_index.get(i).equals(EMPTY)){param=(String)sql_param.get(sp.paramIndex);sp.paramIndex++;}
          else param=(String)sql_param.get(Convert.toIntValue((String)param_index.get(i)));
          prestmt.setString(i+1,param);
        }
        rset=prestmt.executeQuery();
      }
      else{
        stmt=connection.getConnection().createStatement();
        rset=stmt.executeQuery(sql);
      }
      if(rset.next()){
        java.sql.Blob blob=(java.sql.Blob)rset.getObject(1);
        //oracle.sql.BLOB blob=(oracle.sql.BLOB)rset.getObject(1);
        if(blob!=null){
          InputStream input_stream=blob.getBinaryStream(/*1L-for oracleBLOB*/);
          bytes=getBytesFromStream(input_stream);
        }
        else{
          InputStream input_stream=rset.getBinaryStream(1);
          if(input_stream!=null)bytes=getBytesFromStream(input_stream);
        }
        if(bytes!=null){
          ret_val=new /*sun.misc.*/BASE64Encoder().encode(bytes);//BASE64->replace by native code
        }
        /*record_count++;*/
      }
      manager.getLog().write(session_id,INFO_READ_DATA,sp.sql+(bytes!=null?MESSAGE_DELIM_SUBVALUES+EQUAL+bytes.length:EMPTY));
    }catch(SQLException sql_e){
      manager.getLog().write(session_id,ERROR_SQL_QUERY_FAILED,sp.sql+MESSAGE_DELIM_SUBVALUES+sql_e.getErrorCode()+MESSAGE_DELIM_SUBVALUES+sql_e.getLocalizedMessage());
      sp.sqlReturnType=SQL_ERROR_TYPE;//set error type
      sp.sqlErrorMessage=sql_e.getLocalizedMessage();
      try{sp.sqlErrorMessage=new String(sp.sqlErrorMessage.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
      if(!htmlCode)sp.sqlErrorMessage=sp.sqlErrorMessage.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
    }
    finally{
      try{if(rset!=null)rset.close();rset=null;if(prestmt!=null)prestmt.close();prestmt=null;if(stmt!=null)stmt.close();stmt=null;}catch(SQLException sql_e){}
    }
    return ret_val;
  }
  //2S->two string cols
  //col_count=1 -> get only first col values
  //col_count=3 -> get 3 columns values (for radio&checkbox)
  public Vector getStringList(long session_id,ServletParam sp,int col_count)
  {
    Vector ret_val=null;
    PreparedStatement prestmt=null;
    Statement stmt=null;
    ResultSet rset=null;
    StringListItem item;
    Vector sql_param=sp.paramList;
    Vector param_index=this.getParamIndex(sp);
    String sql=(sp.reparsedSQL!=null?sp.reparsedSQL:sp.sql),param;
    int param_count=param_index.size(),record_count=0;
    sp.sqlReturnType=SQL_UNKNOWN_TYPE;//set no type(unknown)
    sp.sqlErrorMessage=null;
    if(differenceCodepage)
      try{sql=new String(sql.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}//decode ISO-8859-1 -> Cp1251
    try{
      ret_val=new Vector();
      if(param_count>0&&sql_param.size()>=param_count){
        prestmt=connection.getConnection().prepareStatement(sql);
        prestmt.clearParameters();
        for(int i=0;i<param_count;i++){
          if(param_index.get(i).equals(EMPTY)){param=(String)sql_param.get(sp.paramIndex);sp.paramIndex++;}
          else param=(String)sql_param.get(Convert.toIntValue((String)param_index.get(i)));
          prestmt.setString(i+1,param);
        }
        rset=prestmt.executeQuery();
      }
      else{
        stmt=connection.getConnection().createStatement();
        rset=stmt.executeQuery(sql);
      }
      while(rset.next()){
        item=new StringListItem();
        item.id=rset.getString(1);
        if(col_count==2||col_count==3){
          item.value=rset.getString(2);
          if(col_count==3)item.value2=rset.getString(3);
        }
        if(item.id!=null){
          if(!htmlCode)item.id=item.id.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
          try{item.id=new String(item.id.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
        }else item.id=EMPTY;
        if(item.value!=null){
          if(!htmlCode)item.value=item.value.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
          try{item.value=new String(item.value.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
        }else item.value=EMPTY;
        if(item.value2!=null){
          if(!htmlCode)item.value2=item.value2.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
          try{item.value2=new String(item.value2.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
        }else item.value2=EMPTY;
        ret_val.add(item);record_count++;
      }
      manager.getLog().write(session_id,INFO_READ_DATA,sp.sql+MESSAGE_DELIM_SUBVALUES+record_count);
    }catch(SQLException sql_e){
      manager.getLog().write(session_id,ERROR_SQL_QUERY_FAILED,sp.sql+MESSAGE_DELIM_SUBVALUES+sql_e.getErrorCode()+MESSAGE_DELIM_SUBVALUES+sql_e.getLocalizedMessage());
      sp.sqlReturnType=SQL_ERROR_TYPE;//set error type
      sp.sqlErrorMessage=sql_e.getLocalizedMessage();
      try{sp.sqlErrorMessage=new String(sp.sqlErrorMessage.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
      if(!htmlCode)sp.sqlErrorMessage=sp.sqlErrorMessage.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
    }
    finally{
      try{if(rset!=null)rset.close();rset=null;if(prestmt!=null)prestmt.close();prestmt=null;if(stmt!=null)stmt.close();stmt=null;}catch(SQLException sql_e){}
    }
    return ret_val;
  }
  //2SB->2 cols: string, binary
  public Vector getBytesList(long session_id,ServletParam sp)
  {
    Vector ret_val=null;
    PreparedStatement prestmt=null;
    Statement stmt=null;
    ResultSet rset=null;
    BytesListItem item;
    Vector sql_param=sp.paramList;
    Vector param_index=this.getParamIndex(sp);
    String sql=(sp.reparsedSQL!=null?sp.reparsedSQL:sp.sql),param;
    int param_count=param_index.size(),record_count=0;
    sp.sqlReturnType=SQL_UNKNOWN_TYPE;//set no type(unknown)
    sp.sqlErrorMessage=null;
    if(differenceCodepage)
      try{sql=new String(sql.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}//decode ISO-8859-1 -> Cp1251
    try{
      ret_val=new Vector();
      if(param_count>0&&sql_param.size()>=param_count){
        prestmt=connection.getConnection().prepareStatement(sql);
        prestmt.clearParameters();
        for(int i=0;i<param_count;i++){
          if(param_index.get(i).equals(EMPTY)){param=(String)sql_param.get(sp.paramIndex);sp.paramIndex++;}
          else param=(String)sql_param.get(Convert.toIntValue((String)param_index.get(i)));
          prestmt.setString(i+1,param);
        }
        rset=prestmt.executeQuery();
      }
      else{
        stmt=connection.getConnection().createStatement();
        rset=stmt.executeQuery(sql);
      }
      while(rset.next()){
        item=new BytesListItem();
        item.id=rset.getString(1);
        item.value=rset.getBytes(2);
        if(item.id!=null){
          if(!htmlCode)item.id=item.id.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
          try{item.id=new String(item.id.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
        }else item.id=EMPTY;
        ret_val.add(item);record_count++;
      }
      manager.getLog().write(session_id,INFO_READ_DATA,sp.sql+MESSAGE_DELIM_SUBVALUES+record_count);
    }catch(SQLException sql_e){
      manager.getLog().write(session_id,ERROR_SQL_QUERY_FAILED,sp.sql+MESSAGE_DELIM_SUBVALUES+sql_e.getErrorCode()+MESSAGE_DELIM_SUBVALUES+sql_e.getLocalizedMessage());
      sp.sqlReturnType=SQL_ERROR_TYPE;//set error type
      sp.sqlErrorMessage=sql_e.getLocalizedMessage();
      try{sp.sqlErrorMessage=new String(sp.sqlErrorMessage.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
      if(!htmlCode)sp.sqlErrorMessage=sp.sqlErrorMessage.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
    }
    finally{
      try{if(rset!=null)rset.close();rset=null;if(prestmt!=null)prestmt.close();prestmt=null;if(stmt!=null)stmt.close();stmt=null;}catch(SQLException sql_e){}
    }
    return ret_val;
  }
  //? cols (strings or binaries)
  public GridData getGridData(long session_id,ServletParam sp,int sql_number,int page_number,int row_count,int from_index)
  {
    GridData gd=new GridData();
    GridDataItem gdi;
    Vector cols=null,rows=null;
    PreparedStatement prestmt=null;
    Statement stmt=null;
    ResultSet rset=null;
    String str;
    byte[] bytes;
    Vector sql_param=sp.paramList;
    Vector param_index=this.getParamIndex(sp);
    String sql=(sp.reparsedSQL!=null?sp.reparsedSQL:sp.sql),param;
    int param_count=param_index.size(),record_count=0;
    sp.sqlReturnType=SQL_UNKNOWN_TYPE;//set no type(unknown)
    sp.sqlErrorMessage=null;
    columnCount=0;
    if(differenceCodepage)
      try{sql=new String(sql.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}//decode ISO-8859-1 -> Cp1251
    try{
      cols=new Vector();
      if(param_count>0&&sql_param.size()>=param_count){
        prestmt=connection.getConnection().prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        prestmt.clearParameters();
        for(int i=0;i<param_count;i++){
          if(param_index.get(i).equals(EMPTY)){param=(String)sql_param.get(sp.paramIndex);sp.paramIndex++;}
          else param=(String)sql_param.get(Convert.toIntValue((String)param_index.get(i)));
          prestmt.setString(i+1,param);
        }
        rset=prestmt.executeQuery();
      }
      else{
        stmt=connection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        rset=stmt.executeQuery(sql);
      }
      //col count
      ResultSetMetaData md=rset.getMetaData();
      int col_count=md.getColumnCount();
      columnCount=col_count;
      //col type
      int[] col_type=new int[col_count];
      for(int i=0;i<col_count;i++)col_type[i]=md.getColumnType(i+1);
      //row count
      if(rset.last())gd.totalRowCount=rset.getRow();
      //Manager.getLog().write("sql INDEX="+Convert.toString(sp.sqlIndex)+" sql NUMBER="+Convert.toString(sqlNumber)+"\r\n");
      if(from_index>-1){if(from_index==0)rset.beforeFirst();else rset.absolute(from_index);}
      else{if(sql_number==sp.sqlIndex&&page_number>1)rset.absolute((page_number-1)*row_count);else rset.beforeFirst();}
      //fetch data
      while(rset.next()){
        rows=new Vector();
        for(int i=0;i<col_count;i++){
          //get as bytes
          //java.sql.Types->getBytes()=all BINARY && some LONG types
          if(col_type[i]==Types.BINARY||col_type[i]==Types.VARBINARY/*small BINARY types*/){
            bytes=rset.getBytes(i+1);
            gdi=new GridDataItem();
            gdi.type=ITEM_TYPE_BYTES;
            gdi.bytesValue=bytes;
            rows.add(gdi);
          }//if
          else if(col_type[i]==Types.BLOB||/*big BINARY types*/
                  col_type[i]==Types.LONGVARBINARY/*||col_type[i]==Types.LONGVARCHAR*//*big LONG types*/){
            bytes=null;
            InputStream i_s=rset.getBinaryStream(i+1);
            //if(i_s==null&&rset.getBlob(i+1)!=null)i_s=rset.getBlob(i+1).getBinaryStream();//may be blob ...
            if(i_s!=null){
              //Manager.getLog().write("BINARY DATA RECV\r\n");
              bytes=getBytesFromStream(i_s);//REUSE IT
              /*ByteArrayOutputStream b_s=null;
              DataOutputStream d_s=null;
              byte[] b=new byte[SIZE_BUFFER_READ];
              int b_size;
              try{
                b_s=new ByteArrayOutputStream();
                d_s=new DataOutputStream(b_s);
                while(true){
                  b_size=i_s.read(b);
                  if(b_size>0)d_s.write(b,0,b_size);
                  else if(b_size==0)try{Thread.sleep(TIMEOUT_DATA_WAIT);}catch(InterruptedException i_e){}//sleep->wait data
                  else if(b_size==-1)break;
                }
                bytes=b_s.toByteArray();
                if(d_s!=null)d_s.close();
                if(b_s!=null)b_s.close();
              }catch(IOException io_e){}
              d_s=null;b_s=null;b=null;*/
            }else bytes=rset.getBytes(i+1);
            gdi=new GridDataItem();
            gdi.type=ITEM_TYPE_BYTES;
            gdi.bytesValue=bytes;
            rows.add(gdi);
          }//else if
          else{//get as string
            str=rset.getString(i+1);
            if(!rset.wasNull()){
              if(!htmlCode)str=str.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
              try{str=new String(str.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
            }else str=EMPTY;
            gdi=new GridDataItem();
            gdi.type=ITEM_TYPE_STRING;
            gdi.stringValue=str;
            rows.add(gdi);
          }
        }
        cols.add(rows);record_count++;
        if((sql_number==sp.sqlIndex||from_index>-1)&&row_count>0&&record_count==row_count)break;
      }
      col_type=null;
      manager.getLog().write(session_id,INFO_READ_DATA,sp.sql+MESSAGE_DELIM_SUBVALUES+record_count);
    }catch(SQLException sql_e){
      manager.getLog().write(session_id,ERROR_SQL_QUERY_FAILED,sp.sql+MESSAGE_DELIM_SUBVALUES+sql_e.getErrorCode()+MESSAGE_DELIM_SUBVALUES+sql_e.getLocalizedMessage());
      sp.sqlReturnType=SQL_ERROR_TYPE;//set error type
      sp.sqlErrorMessage=sql_e.getLocalizedMessage();
      try{sp.sqlErrorMessage=new String(sp.sqlErrorMessage.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
      if(!htmlCode)sp.sqlErrorMessage=sp.sqlErrorMessage.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
    }
    finally{
      try{if(rset!=null)rset.close();rset=null;if(prestmt!=null)prestmt.close();prestmt=null;if(stmt!=null)stmt.close();stmt=null;}catch(SQLException sql_e){}
    }
    gd.items=cols;
    return gd;
  }
  public GridData getGridData(long session_id,ServletParam sp,int sql_number,int page_number,int row_count)
  {
    return this.getGridData(session_id,sp,sql_number,page_number,row_count,-1);
  }
  public int executeSQL(long session_id,ServletParam sp)//SQLType, sqlReturnType, sqlErrorMessage execution here
  {
    int ret_val=-1;
    PreparedStatement prestmt=null;
    CallableStatement calstmt=null;
    byte[] bytes_param;
    Vector sql_param=sp.paramList;
    Vector sql_param_type=sp.paramTypeList;
    Vector param_index=this.getParamIndex(sp);
    String sql=(sp.reparsedSQL!=null?sp.reparsedSQL:sp.sql),param,index_str,temp;
    boolean oracle_database_type=manager.getInitial().getDatabaseType().equalsIgnoreCase(DATABASE_TYPE_ORACLE);
    //boolean mysql_database_type=manager.getInitial().getDatabaseType().equalsIgnoreCase(DATABASE_TYPE_MYSQL);
    boolean sql_insert_returning=false,sql_select_returning=false;
    int param_count=param_index.size(),index,return_param_index=sp.returnParamIndex;
    boolean pl_sql=false;
    sp.sqlReturnType=SQL_UNKNOWN_TYPE;//set no type(unknown)
    sp.sqlErrorMessage=null;
    if(differenceCodepage)
      try{sql=new String(sql.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}//decode ISO-8859-1 -> Cp1251
    if((sql.startsWith(SQL_INSERT)||sql.startsWith(SQL_INSERT_UPPER_CASE))&&
            (sql.contains(SQL_RETURNING)||sql.contains(SQL_RETURNING_UPPER_CASE))){
             sql_insert_returning=true;
    }
    else if((sql.startsWith(SQL_SELECT)||sql.startsWith(SQL_SELECT_UPPER_CASE))&&
       (sql.contains(SQL_INTO)||sql.contains(SQL_INTO_UPPER_CASE))){
        sql_select_returning=true;
        //remove into after select (this operation not allowed in sql)
        if((index=sql.indexOf(SQL_INTO))==-1)index=sql.indexOf(SQL_INTO_UPPER_CASE);
        temp=sql.substring(0,index);
        if((index=sql.indexOf(SQL_FROM))==-1)index=sql.indexOf(SQL_FROM_UPPER_CASE);
        if(index!=-1)temp+=sql.substring(index);
        sql=temp;
    }
    try{
      //Manager.getLog().write("RETURN INDEX="+Convert.toString(return_param_index)+" PARAM COUNT="+Convert.toString(param_count)+" sql PARAM COUNT="+Convert.toString(sql_param.size())+"\r\n");
      if(sql_param.size()>=param_count){
        if(/*sql_insert_returning||sql_select_returning*/
           sql.startsWith(SQL_SELECT)||sql.startsWith(SQL_SELECT_UPPER_CASE)||
           sql.startsWith(SQL_UPDATE)||sql.startsWith(SQL_UPDATE_UPPER_CASE)||
           sql.startsWith(SQL_INSERT)||sql.startsWith(SQL_INSERT_UPPER_CASE)||
           sql.startsWith(SQL_DELETE)||sql.startsWith(SQL_DELETE_UPPER_CASE)){//SQL statement
          if(oracle_database_type&&sql_insert_returning)prestmt=(oracle.jdbc.OraclePreparedStatement)connection.getConnection().prepareStatement(sql);
          else prestmt=connection.getConnection().prepareStatement(sql);
          prestmt.clearParameters();
          //Manager.getLog().write("AFTER PREPARE STMT\r\n");
          for(int i=0;i<param_count;i++){
            index_str=(String)param_index.get(i);
            if(index_str!=null&&index_str.length()>0)index=Convert.toIntValue(index_str);//index present
            else{index=sp.paramIndex;sp.paramIndex++;}//index not found
            if(((String)sql_param_type.get(index)).equals(SERVICE_PARAM_TYPE_STRING)){
              param=(String)sql_param.get(index);
              //Manager.getLog().write("PARAM "+index+"="+param+"\r\n");
              if(return_param_index==index){
                if(oracle_database_type&&sql_insert_returning)((oracle.jdbc.OraclePreparedStatement)prestmt).registerReturnParameter(i+1,oracle.jdbc.OracleTypes.VARCHAR);
              }
              else{
                prestmt.setString(i+1,param);
                //Manager.getLog().write("setString(param)#"+index);
              }
            }
            else if(((String)sql_param_type.get(index)).equals(SERVICE_PARAM_TYPE_BUFFER)){
              bytes_param=(byte[])sql_param.get(index);
              if(return_param_index==index){//not return binary data
                //if(oracle_database_type&&sql_insert_returning)((oracle.jdbc.OraclePreparedStatement)prestmt).registerReturnParameter(i+1,oracle.jdbc.OracleTypes.BLOB);
              }
              else{
                prestmt.setBytes(i+1,bytes_param);
                //Manager.getLog().write("setBytes(param)#"+index);
                //prestmt.setObject(i+1,bytes_param);/*object as bytes array*/
                //Manager.getLog().write("setObject(param)#"+index);
              }
            }
          }
          //Manager.getLog().write("AFTER BIND VAR\r\n");
          ret_val=prestmt.executeUpdate();
          if((sql_param.size()>0&&(sql_insert_returning||sql_select_returning))||/*do if returning*/
             (sp.sqlType==SQL_MESSAGE_TYPE||sp.sqlType==SQL_ERROR_MESSAGE_TYPE)/*do if sql message page*/){
              ResultSet rset=null;//ResultSet rset=((oracle.jdbc.OraclePreparedStatement)prestmt).getGeneratedKeys();//return id
              if(oracle_database_type&&sql_insert_returning)rset=((oracle.jdbc.OraclePreparedStatement)prestmt).getReturnResultSet();
              else rset=prestmt.getResultSet();//sql_select_returning || sql message page
              if(rset!=null&&rset.next()){
                if(sql_insert_returning||sql_select_returning){//returning
                  if(return_param_index>-1&&return_param_index<sql_param.size()){
                    temp=rset.getString(1);
                    if(!htmlCode)temp=temp.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
                    try{temp=new String(temp.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
                    sp.paramList.setElementAt(temp,return_param_index);
                    sp.paramTypeList.setElementAt(SERVICE_PARAM_TYPE_STRING,return_param_index);
                    //Manager.getLog().write("RETURN DATA="+temp+" INDEX="+return_param_index+"\r\n");
                  }
                }
                else{//sql message type
                  if(sp.sqlType==SQL_MESSAGE_TYPE||sp.sqlType==SQL_ERROR_MESSAGE_TYPE){//sql message page(wait for return data)
                    temp=rset.getString(1);
                    sp.sqlReturnType=SQL_MESSAGE_TYPE;//set message type
                    if(!htmlCode)temp=temp.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
                    try{temp=new String(temp.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
                    sp.sqlErrorMessage=temp;
                    manager.getLog().write(session_id,INFO_SQL_MESSAGE,sp.sql+MESSAGE_DELIM_SUBVALUES+EQUAL+temp);
                  }//if
                }//else
              }
              try{if(rset!=null)rset.close();rset=null;}catch(SQLException sql_e){}
          }//if
        }
        else{//PL/SQL statement
          pl_sql=true;
          if(oracle_database_type)calstmt=(oracle.jdbc.OracleCallableStatement)connection.getConnection().prepareCall(sql);
          else calstmt=connection.getConnection().prepareCall(sql);
          calstmt.clearParameters();
          for(int i=0;i<param_count;i++){
            index_str=(String)param_index.get(i);
            if(index_str!=null&&index_str.length()>0)index=Convert.toIntValue(index_str);//index present
            else{index=sp.paramIndex;sp.paramIndex++;}//index not found
            if(((String)sql_param_type.get(index)).equals(SERVICE_PARAM_TYPE_STRING)){
              param=(String)sql_param.get(index);
              calstmt.setString(i+1,param);
              //Manager.getLog().write("plSQL_setString(param)#"+index);
            }
            else if(((String)sql_param_type.get(index)).equals(SERVICE_PARAM_TYPE_BUFFER)){
              bytes_param=(byte[])sql_param.get(index);
              calstmt.setBytes(i+1,bytes_param);
              //Manager.getLog().write("plSQL_setBytes(param)#"+index);
            }
          }
          ret_val=calstmt.executeUpdate();
        }//else
        connection.getConnection().commit();
      }//if
      if(ret_val>0||pl_sql)manager.getLog().write(session_id,INFO_UPDATE_REQUEST,sp.sql+MESSAGE_DELIM_SUBVALUES+Convert.toString(ret_val));
      else manager.getLog().write(session_id,ERROR_INVALID_REQUEST,sp.sql);
    }catch(SQLException sql_e){
      manager.getLog().write(session_id,ERROR_SQL_QUERY_FAILED,sp.sql+MESSAGE_DELIM_SUBVALUES+sql_e.getErrorCode()+MESSAGE_DELIM_SUBVALUES+sql_e.getLocalizedMessage());
      sp.sqlReturnType=SQL_ERROR_TYPE;//set error type
      sp.sqlErrorMessage=sql_e.getLocalizedMessage();
      try{sp.sqlErrorMessage=new String(sp.sqlErrorMessage.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
      if(!htmlCode)sp.sqlErrorMessage=sp.sqlErrorMessage.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
    }//catch
    finally{
      try{if(prestmt!=null)prestmt.close();prestmt=null;if(calstmt!=null)calstmt.close();calstmt=null;}catch(SQLException sql_e){}
    }
    return ret_val;
  }
  public String executePLSQL(long session_id,ServletParam sp)//%if (String)executePLSQL %if
  {
    String ret_val=null;
    CallableStatement calstmt=null;
    byte[] bytes_param;
    Vector sql_param=sp.paramList;
    Vector sql_param_type=sp.paramTypeList;
    Vector param_index=this.getParamIndex(sp);
    String sql=(sp.reparsedSQL!=null?sp.reparsedSQL:sp.sql),param=null,index_str;
    boolean oracle_database_type=manager.getInitial().getDatabaseType().equalsIgnoreCase(DATABASE_TYPE_ORACLE);
    //boolean mysql_database_type=manager.getInitial().getDatabaseType().equalsIgnoreCase(DATABASE_TYPE_MYSQL);
    int param_count=param_index.size(),index;
    sp.sqlReturnType=SQL_UNKNOWN_TYPE;//set no type(unknown)
    sp.sqlErrorMessage=null;
    if(differenceCodepage)
      try{sql=new String(sql.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}//decode ISO-8859-1 -> Cp1251
    try{
      if(sql_param.size()>=param_count){
        if(oracle_database_type)calstmt=(oracle.jdbc.OracleCallableStatement)connection.getConnection().prepareCall(sql);
        else calstmt=connection.getConnection().prepareCall(sql);
        calstmt.clearParameters();
        for(int i=0;i<param_count;i++){
          index_str=(String)param_index.get(i);
          if(index_str!=null&&index_str.length()>0)index=Convert.toIntValue(index_str);//index present
          else{index=sp.paramIndex;sp.paramIndex++;}//index not found
          if(((String)sql_param_type.get(index)).equals(SERVICE_PARAM_TYPE_STRING)){
            param=(String)sql_param.get(index);
            calstmt.setString(i+1,param);
            //Manager.getLog().write("plSQL_setString(param)#"+index);
          }
          else if(((String)sql_param_type.get(index)).equals(SERVICE_PARAM_TYPE_BUFFER)){
            bytes_param=(byte[])sql_param.get(index);
            calstmt.setBytes(i+1,bytes_param);
            //Manager.getLog().write("plSQL_setBytes(param)#"+index);
          }
        }
        //calstmt.registerOutParameter(1,Types.VARCHAR);/*invalid column index*/
        ret_val=Convert.toString(calstmt.executeUpdate());//calstmt.execute();
        try{
          ResultSet rset=calstmt.getResultSet();
          if(rset!=null&&rset.next())param=rset.getString(1);//param=calstmt.getString(1);/*invalid column index*/
          try{if(rset!=null)rset.close();rset=null;}catch(SQLException sql_e){}
          //Manager.getLog().write("RETURN PARAM="+param+"\r\n");
          if(param!=null){
            try{param=new String(param.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
            ret_val=param;
          }
        }catch(Exception e){/*manager.getLog().write("FAILED RETURN PARAM:"+e.toString()+"\r\n");*/}
        connection.getConnection().commit();
      }
      if(ret_val!=null)manager.getLog().write(session_id,INFO_UPDATE_REQUEST,sp.sql+MESSAGE_DELIM_SUBVALUES+EQUAL+ret_val);
      else manager.getLog().write(session_id,ERROR_INVALID_REQUEST,sp.sql);
    }catch(SQLException sql_e){
      manager.getLog().write(session_id,ERROR_SQL_QUERY_FAILED,sp.sql+MESSAGE_DELIM_SUBVALUES+sql_e.getErrorCode()+MESSAGE_DELIM_SUBVALUES+sql_e.getLocalizedMessage());
      sp.sqlReturnType=SQL_ERROR_TYPE;//set error type
      sp.sqlErrorMessage=sql_e.getLocalizedMessage();
      try{sp.sqlErrorMessage=new String(sp.sqlErrorMessage.getBytes(manager.getSystemCodepage()));}catch(UnsupportedEncodingException ue_e){}
      if(!htmlCode)sp.sqlErrorMessage=sp.sqlErrorMessage.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
    }
    finally{
      try{if(calstmt!=null)calstmt.close();calstmt=null;}catch(SQLException sql_e){}
    }
    return ret_val;
  }
}
class LogDatabase implements Interface,tools.Interface
{
  private DatabaseConnection connection=null;
  private String sqlQuery="INSERT INTO log(type,message) VALUES(?,?)";
  //[constructor]
  public LogDatabase(DatabaseConnection connection){this.connection=connection;}
  //log database
  public void writeLog(int message_type,String message)
  {
    PreparedStatement prestmt=null;
    try{
      prestmt=connection.getConnection().prepareStatement(sqlQuery);
      prestmt.clearParameters();
      prestmt.setInt(1,message_type);
      prestmt.setString(2,message);
      prestmt.execute();
      connection.getConnection().commit();
    }catch(SQLException sql_e){}
    finally{
      try{if(prestmt!=null)prestmt.close();prestmt=null;}catch(SQLException sql_e){}
    }
  }
}
//-------------------------------------param----------------------------------//
//string list data ...
class GridData
{
  int totalRowCount=0;
  Vector items=null;
}
class GridDataItem
{
  int type=0;
  String stringValue=null;
  byte[] bytesValue=null;
}
class StringListItem
{
  String id=null;
  String value=null;
  String value2=null;
}
class BytesListItem
{
  String id=null;
  byte[] value=null;
}
class Tag
{
  int type=0;
  int sqlIndex=-1;
  int totalRowCount=-1;
  String data=null;
  String pages=null;
}
//servlet parameters ...
//Param type List Vector: "string", "buffer"
//Param List Vector:
//string param -> as String
//buffer param -> as byte[]
class ServletParam
{
  //request,response
  HttpServletRequest request=null;
  HttpServletResponse response=null;
  //content
  String content=null;
  //connection
  String databaseDriver=null;
  String databaseType=null;
  //page data
  String name=null;
  String login=null;
  String password=null;
  String database=null;//DatabaseName
  String sql=null;
  String reparsedSQL=null;
  Vector sqlList=null;
  int paramIndex=0;
  int sqlIndex=0;
  int returnParamIndex=-1;//returning index
  //BufferParam bufferParam=null;//last file buffer
  Vector paramList=null;
  Vector paramTypeList=null;//used for execute sql
  HashMap extraList=null;
  String rowCount=null;
  String pageOnclick=null;
  String pageHref=null;
  String pageCount=null;
  String pageNumber=null;
  String pageStyle=null;
  String pageType=null;
  String pageMarker=null;
  String pageParam=null;
  String pageTitle=null;
  String pageClass=null;
  String pageCurrentClass=null;
  String pageAnchor=null;
  String pagePrev=null;
  String pageNext=null;
  String sqlNumber=null;
  String sqlIgnore=null;
  //request data
  String requestURL=null;
  String queryString=null;
  String remoteAddr=null;
  String remoteHost=null;
  //error and message
  int sqlType=0;//!-sql error page(1) @-sql message page(2) !@ or @!-sql error message page(3)
  int sqlReturnType=0;//0-return unknown_type 1-return error_type 2-return message_type
  String sqlErrorMessage=null;
  //cookie
  String cookieName=null;
  String cookieValue=null;
  String cookieTimeout=null;
  //secret cookie
  String hideCookieName=null;
  String hideCookieValue=null;
  String hideCookieTimeout=null;
  String hideCookieKeyword=null;
  //service
  boolean service=false;
  int secure=0;/*0-non secure 1-secure connection*/
  //[functions]
  //set
  public void setSQLType(int value){sqlType=value;}
  public void setSQLReturnType(int value){sqlReturnType=value;}
  public void setSQLErrorMessage(String value){sqlErrorMessage=value;}
  public void setCookieName(String value){cookieName=value;}
  public void setCookieValue(String value){cookieValue=value;}
  public void setCookieTimeout(String value){cookieTimeout=value;}
  public void setHideCookieName(String value){cookieName=value;}
  public void setHideCookieValue(String value){hideCookieValue=value;}
  public void setHideCookieTimeout(String value){hideCookieTimeout=value;}
  public void setHideCookieKeyword(String value){hideCookieKeyword=value;}
  //get
  public String getName(){return name;}
  public String getLogin(){return login;}
  public String getPassword(){return password;}
  public String getDatabase(){return database;}
  public Vector getParamList(){return paramList;}
  public Vector getParamTypeList(){return paramTypeList;}
  public HashMap getExtraList(){return extraList;}
  public String getRequestURL(){return requestURL;}
  public String getQueryString(){return queryString;}
  public String getRemoteAddr(){return remoteAddr;}
  public String getRemoteHost(){return remoteHost;}
  public int getSQLType(){return sqlType;}
  public int getSQLReturnType(){return sqlReturnType;}
  public String getSQLErrorMessage(){return sqlErrorMessage;}
  public String getSQLFormattedErrorMessage(String removeFrom,String replace,String replaceTo){
    String str=tools.Convert.removeFromAndReplace(sqlErrorMessage,removeFrom,replace,replaceTo);
    return tools.Convert.removeNextLine(str);
  }
  public String getCookieName(){return cookieName;}
  public String getCookieValue(){return cookieValue;}
  public String getCookieTimeout(){return cookieTimeout;}
  public String getHideCookieName(){return hideCookieName;}
  public String getHideCookieValue(){return hideCookieValue;}
  public String getHideCookieTimeout(){return hideCookieTimeout;}
  public String getHideCookieKeyword(){return hideCookieKeyword;}
  public int getSecure(){return secure;}
  public HttpServletRequest getRequest(){return request;}
  public HttpServletResponse getResponse(){return response;}
  public String getContent(){return content;}
}
//buffer data received from user
class BufferParam
{
  String contentType;
  String filename;
  String name;
  byte[] data;
}
//--------------------------------tools---------------------------------------//
//package class for invoke_method in service_template (static service functions)
//such as %invoke_methodservice.Tools.<public method>(<args>)%invoke_method
//recomendations for functions: strings convertions, replacings substrings and etc.
//Real examles:
/*
%set_param5=@service.Tools.encode("%param4%param","UTF-8")%set_param
<br>%param5%param
%set_param6=@service.Tools.toUTF8("%param4%param")%set_param
<br>%param6%param
 */
class Tools
{
  public static String replace(String str,String str1,String str2){String s=new String(str);return s.replace(str1,str2);}
  public static String replaceAll(String str,String str1,String str2){String s=new String(str);return s.replaceAll(str1,str2);}
  public static String value(String str){return str;}
  public static String encode(String str,String codepage) throws UnsupportedEncodingException{//url encoding
    String ret_val;
    ret_val=URLEncoder.encode(str,codepage);
    return ret_val;
  }
  public static String decode(String str,String codepage) throws UnsupportedEncodingException{//url encoding
    String ret_val;
    ret_val=URLDecoder.decode(str,codepage);
    return ret_val;
  }
  public static String decodeBASE64(String str){
    byte[] dec_data=new /*sun.misc.*/BASE64Decoder().decodeBuffer(str.getBytes());//BASE64->replace by native code
    return (new String(dec_data));
  }
  public static String encodeBASE64(String str){
    String enc_data=new /*sun.misc.*/BASE64Encoder().encode(str.getBytes());//BASE64->replace by native code
    return enc_data;
  }
  //test
  public static String toUTF8(String str) throws UnsupportedEncodingException{
    String ret_val;
    ret_val=new String(str.getBytes("UTF-8"));
    return ret_val;
  }
  //test
  public static String fromUTF8(String str) throws UnsupportedEncodingException{
    String ret_val;
    ret_val=new String(str.getBytes(),"UTF-8");
    return ret_val;
  }
  //test
  public static String fromPageToPage(String str,String from_codepage,String to_codepage) throws UnsupportedEncodingException{
    String ret_val;
    ret_val=new String(str.getBytes(to_codepage),from_codepage);//changing place of codepages
    return ret_val;
  }
}
//------------------------------invoke_method---------------------------------//
class InvokeMethod implements tools.Interface
{
  public Object start(String s)throws Exception//"class1.classN.method(args)"
  {
    return invoke_method(s);
  }
  //java.lang.String.replaceAll(str1,str2).toString(); -> (two and more numbers methods called)
  private int getIndex(String str,char code,int start_index)
  {
    int ret_val=-1,size=str.length();
    boolean text=false;
    if(start_index==-1)return ret_val;
    for(int i=start_index;i<size;i++){
      if(str.charAt(i)==CODE_DOUBLE_UPPER){
        if(!text)text=true;else text=false;//text or no text
      }
      else if(str.charAt(i)==code){
        if(!text){ret_val=i;break;}
      }
    }
    return ret_val;
  }
  //last index of <code> with (...) ignore: ->method(arg1,arg2,(java.lang.Object)arg3, ...)
  private int getLastIndex(String str,char code,int start_index)
  {
    int ret_val=-1,size=str.length(),count=0;
    boolean text=false;
    if(start_index==-1)return ret_val;
    for(int i=start_index;i<size;i++){
      if(!text&&count==0&&str.charAt(i)==code){ret_val=i;break;}
      else if(str.charAt(i)==CODE_DOUBLE_UPPER){
        if(!text)text=true;else text=false;//text or no text
      }
      else if(str.charAt(i)==CODE_OPEN_){if(!text)count++;}
      else if(str.charAt(i)==CODE_CLOSE_){if(!text&&count>0)count--;}
    }
    return ret_val;
  }
  //get args vector with (...) ignore
  private Vector getArgs(String args_name)//(arg1,arg2,(java.lang.Object)arg3, ...)
  {
    Vector ret_val;
    String str=null;
    ret_val=new Vector();
    int size=args_name.length(),index=0,count=0;
    boolean text=false;
    //Manager.getLog().write("args name="+args_name+" size="+size+"\r\n");
    for(int i=0;i<size;i++){
      if(args_name.charAt(i)==CODE_DOUBLE_UPPER){
        if(!text)text=true;else text=false;//text or no text
      }
      else if(args_name.charAt(i)==CODE_OPEN_){if(!text)count++;}
      else if(args_name.charAt(i)==CODE_CLOSE_){if(!text&&count>0)count--;}
      else if(args_name.charAt(i)==CODE_COMA){
        if(!text&&count==0){ret_val.add(args_name.substring(index,i).trim());index=i+1;}
      }
    }
    if(index<size)str=args_name.substring(index).trim();
    if(str!=null&&str.length()>0)ret_val.add(str);//from index add one last arg
    return ret_val;
  }
  //invoke_method scrypt:
  //"class1.classN.method(args)"
  private Object invoke_method(String s)throws Exception
  {
    Object o=null;
    Field f;
    Class c;
    Object o_c;//Object of Class c;
    String class_name,method_name,args_name,str0=s.trim(),str1=str0,str2=null;
    int index1,index2,index3;
    index2=str1.indexOf(CODE_OPEN_);
    index3=this.getLastIndex(str1,CODE_CLOSE_,index2+1);
    if(index3+1<=str1.length()){
      str0=str1.substring(0,index2).trim();//string without ()
      str2=str1.substring(index3+1).trim();//string after ()
      str1=str1.substring(0,index3+1).trim();//string with ()
    }
    index1=str0.lastIndexOf(CODE_POINT);
    class_name=str1.substring(0,index1);
    c=Class.forName(class_name);
    o_c=c.newInstance();
    do{
      method_name=str1.substring(index1+1,index2);
      args_name=str1.substring(index2+1,index3);
      o=this.invoke(args_name,method_name,c,o_c);//to execute declared_method with args
      if(o==null||str2==null)break;
      c=o.getClass();
      o_c=o;
      str1=str2;
      index2=str1.indexOf(CODE_OPEN_);
      index3=this.getLastIndex(str1,CODE_CLOSE_,index2+1);
      str2=null;
      if(index2==-1||index3==-1)return o==null?EMPTY:o;
      if(index3+1<=str1.length()){
        str0=str1.substring(0,index2).trim();//string without ()
        str2=str1.substring(index3+1).trim();//string after ()
        str1=str1.substring(0,index3+1).trim();//string with ()
      }
      index1=str0.lastIndexOf(CODE_POINT);
    }while(true);
    return o==null?EMPTY:o;
  }
  private Object invoke(String args_name,String method_name,Class c,Object o)throws Exception
  {
    Object ret_val=null;
    Class arg_type[];
    Object arg_value[];
    String token,class_name;
    Vector args=this.getArgs(args_name);
    int token_index,index,size=args.size();
    arg_type=new Class[size];
    arg_value=new Object[size];
    for(token_index=0;token_index<size;token_index++){
      token=(String)args.elementAt(token_index);
      if(token.startsWith(DOUBLE_UPPER)){token=token.replaceAll(DOUBLE_UPPER,EMPTY);arg_type[token_index]=java.lang.String.class;arg_value[token_index]=token;}//String
      else if(token.startsWith(UPPER)){token=token.replaceAll(UPPER,EMPTY);arg_type[token_index]=char.class;arg_value[token_index]=token.charAt(0);}//char
      else if(token.equals(TRUE)||token.equals(FALSE)){arg_type[token_index]=boolean.class;arg_value[token_index]=java.lang.Boolean.parseBoolean(token);}//boolean
      else if(token.indexOf(POINT)!=-1){
        Class object_class=null;
        index=-1;
        if(token.startsWith(OPEN)){//object converter-> (java.lang.Object)...
          index=this.getIndex(token,CODE_CLOSE_,1);
          if(index!=-1){
            class_name=token.substring(1,index);//remove ( & )
            object_class=Class.forName(class_name);
          }
        }
        token=token.substring(index+1);
        if(token.startsWith(DOUBLE_UPPER)){//string "..."
          token=token.replaceAll(DOUBLE_UPPER,EMPTY);//remove " & "
          arg_type[token_index]=object_class;arg_value[token_index]=token;//Object
        }
        else if(token.indexOf(OPEN)!=-1){arg_type[token_index]=java.lang.Object.class;arg_value[token_index]=this.invoke_method(token);}//recursive call
        else{arg_type[token_index]=float.class;arg_value[token_index]=java.lang.Float.parseFloat(token);}//float
      }
      else{arg_type[token_index]=int.class;arg_value[token_index]=java.lang.Integer.parseInt(token);}//int
    }
    ret_val=c.getDeclaredMethod(method_name,arg_type).invoke(o,arg_value);
    arg_type=null;arg_value=null;c=null;
    return ret_val;
  }
}
//--------------------------------html parse----------------------------------//
class PageParam implements Interface
{
  int rowCount=-1;//rows count for page
  int pageType=0;//0->up 1->down 2->updown
  int pageCount=-1;//page count on this page
  int pageNumber=-1;//page number of this page
  int sqlNumber=-1;//sql number for page(count, number, ...)
  public void substParam(ServletParam sp)
  {
    if(sp.rowCount!=null&&sp.rowCount.length()>0){//row count on page
      try{rowCount=Convert.toIntValue(sp.rowCount);}catch(Exception e){}
    }
    if(sp.pageType!=null){
      if(sp.pageType.equalsIgnoreCase(SERVICE_PAGE_TYPE_UP))pageType=0;
      else if(sp.pageType.equalsIgnoreCase(SERVICE_PAGE_TYPE_DOWN))pageType=1;
      else if(sp.pageType.equalsIgnoreCase(SERVICE_PAGE_TYPE_UPDOWN))pageType=2;
    }
    if(sp.pageCount!=null&&sp.pageCount.length()>0){
      try{pageCount=Convert.toIntValue(sp.pageCount);}catch(Exception e){}
    }
    if(sp.pageNumber!=null&&sp.pageNumber.length()>0){
      try{pageNumber=Convert.toIntValue(sp.pageNumber);}catch(Exception e){}
    }
    if(sp.sqlNumber!=null&&sp.sqlNumber.length()>0){
      try{sqlNumber=Convert.toIntValue(sp.sqlNumber);}catch(Exception e){}
    }
  }
}
//hidden input parameter forms from sql query
//<input type="hidden" sql="SELECT 'name',get_user(type) FROM user WHERE login_name=UPPER('%login%login')">
//set sql number by # for this sql query (only for sql queries on page, exclusive Table columns "col" and operands %if ... %if)
//"#SELECT * FROM table"
//"!SELECT * FROM table" -> go to sql error page on any error
//"@SELECT * FROM table" -> go to sql message page on return value
//"!@SELECT * FROM table" -> go to sql error or message page
//Warning! Not recomended used %if %elseif %endif with html tags combination <...> in one string
//         Not used %if %elseif %endif inside html tags <...> with sql queries before or after

/*
localCodepage codepage of servlet files(pages,templates)
systemCodepage codepage of operation system(System.getProperty("file.encoding"))
DatabaseCodepage codepage of database(tables,views data) = localCodepage
---
file reads from disk or data from database as bytes(no need convert) and write to response
file reads from disk as chars in systemCodepage(convert to localCodepage->readCharArrayFromFile)
data reads from database as strings in DatabaseCodepage(reads strings as systemCodepage)
*/
class HtmlResponse extends HtmlParse implements Interface
{
  private long sessionID/*local session_id*/,bufferNumber=0;
  //private PrintWriter outWriter;//write string as platform system codepage /*NOT USED IN THIS VERSION*/
  private ServletOutputStream outStream;//write byte as original file codepage /*DATA STREAM TO USER*/
  private Manager manager;
  private ClientSession clientSession;
  private Database database;
  private ServletParam servletParam;//for public methods used
  private boolean htmlCode=true;//pointer down if <script> action
  private boolean sqlIgnore=false;//ignore sql files list
  public ClientSession getClientSession(){return clientSession;}
  public void setSQLIgnore(boolean value){sqlIgnore=value;}
  //[constructor]
  public HtmlResponse(long session_id,/*PrintWriter out_stream,*/ServletOutputStream out_stream,Manager manager,ClientSession client_session,Database database){sessionID=session_id;/*Out=out_stream;*/outStream=out_stream;this.manager=manager;clientSession=client_session;this.database=database;}
  public void writeData(ServletParam sp)
  {
    String str;//data string without tags
    int text_type=0;//1-with double upper ("text") 2-with upper ('text') 3-comments (/*text*/)
    boolean percent=false,comment=false/*text=false*/;
    boolean /*param_replace=false,*/param_encode=false,param_quot=false,param_html=false,param_not_quot=false;//for encode()->(<a href="...">)
    char[] data=null;//file buffer read
    char data_ch;//for char[] data
    boolean[] data_left;/*for %if_%if html data left*/
    int data_left_ind=0,if_level=0;/*for data left index and if else level*/
    int status=0,last_status=0;//1-open (<) 2-open close (</) 3-close (>)
    int index=0,percent_index,last_percent_index;
    int type;
    String sql,cols,file,ref,selected_value/*,selected_value_by_name*/,fetch;
    boolean is_tag=false;//'<','</','>' opens or closes tags presents
    Tag tag;//class for saving type,data,pages of tags->th,td,option,tr,textarea,select,table
    Vector tag_data=new Vector();//open tag data
    int tag_ind=-1,total_row_count=-1;//all rows count of sql query
    servletParam=sp;//servlet parameters for class.methods(getCookie,setCookie)
    PageParam page_param=new PageParam();//integer param of servletParam
    try{//IOException
    if(!sqlIgnore){
      for(int i=0;i<sp.sqlList.size();i++){
        sp.sql=(String)sp.sqlList.get(i);
        if(sp.sql!=null&&sp.sql.length()>0){//sql query execute
          if(sp.sql.endsWith(EXTENSION_FILE_SQL)){//today is no sql query -> is a sql query file ...
            str=manager.getInitial().getServiceTemplates();//ini service templates dir
            if(str!=null&&!str.endsWith(LOCAL_DELIM_2))str+=LOCAL_DELIM_2;
            byte[] filedata=Convert.readFromFile((str!=null?str:manager.getServletFilepath()+FILEPATH_SERVICE_TEMPLATES)+sp.sql);//sql query from file
            if(filedata!=null){
              str=Convert.toString(filedata);
              str=str.replaceAll(NEXT_LINE,SPACE);
              //error and message !-error(1) @-message(2) !@ or @!-error message(3)
              str=str.trim();
              if(str.startsWith(SQL_ERROR_MESSAGE)){sp.sqlType=SQL_ERROR_MESSAGE_TYPE;str=str.substring(2);}
              else if(str.startsWith(SQL_MESSAGE_ERROR)){sp.sqlType=SQL_ERROR_MESSAGE_TYPE;str=str.substring(2);}
              else if(str.startsWith(SQL_ERROR)){sp.sqlType=SQL_ERROR_TYPE;str=str.substring(1);}
              else if(str.startsWith(SQL_MESSAGE)){sp.sqlType=SQL_MESSAGE_TYPE;str=str.substring(1);}
              //param# substitutions by number (%param<number>%param)
              while((percent_index=str.indexOf(Interface.PERCENT_PARAM))!=-1){//%param1%param
                if((last_percent_index=str.indexOf(Interface.PERCENT_PARAM,percent_index+6))!=-1){
                  String substr=(String)sp.paramList.get(Convert.toIntValue(str.substring(percent_index+6,last_percent_index).trim()));
                  str=str.substring(0,percent_index)+substr+str.substring(last_percent_index+6);
                }
              }
              //remoteaddr substitutions
              while((percent_index=str.indexOf(Interface.PERCENT_REMOTEADDR))!=-1){//%remoteaddr%remoteaddr
                if((last_percent_index=str.indexOf(Interface.PERCENT_REMOTEADDR,percent_index+11))!=-1){
                  String substr=sp.remoteAddr;
                  str=str.substring(0,percent_index)+substr+str.substring(last_percent_index+11);
                }
              }
              //remotehost substitutions
              while((percent_index=str.indexOf(Interface.PERCENT_REMOTEHOST))!=-1){//%remotehost%remotehost
                if((last_percent_index=str.indexOf(Interface.PERCENT_REMOTEHOST,percent_index+11))!=-1){
                  String substr=sp.remoteHost;
                  str=str.substring(0,percent_index)+substr+str.substring(last_percent_index+11);
                }
              }
              sp.sql=str.trim();str=EMPTY;//not # for sql query
              //move executeSQL to here ... (for execution only sql files on server)
              database.executeSQL(sessionID,sp);
              //seek error or message found
              if((sp.sqlType==SQL_ERROR_MESSAGE_TYPE||sp.sqlType==SQL_ERROR_TYPE||sp.sqlType==SQL_MESSAGE_TYPE)&&sp.sqlErrorMessage!=null){
                String service_page=null;
                str=manager.getInitial().getServicePages();//ini service pages dir
                if(str!=null&&!str.endsWith(LOCAL_DELIM_2))str+=LOCAL_DELIM_2;
                if(sp.sqlReturnType==SQL_ERROR_TYPE)service_page=Convert.toString(Convert.readFromFile(str!=null?str+FILENAME_SQL_ERROR:manager.getServletFilepath()+FILEPATH_SQL_ERROR));
                else if(sp.sqlReturnType==SQL_MESSAGE_TYPE)service_page=Convert.toString(Convert.readFromFile(str!=null?str+FILENAME_SQL_MESSAGE:manager.getServletFilepath()+FILEPATH_SQL_MESSAGE));
                if(service_page!=null){
                  if(sp.sqlReturnType==SQL_ERROR_TYPE){
                    service_page=service_page.replaceAll(PERCENT_SQL_ERROR+PERCENT_SQL_ERROR,sp.sqlErrorMessage);
                    do{//do all scrypts
                      if((percent_index=service_page.indexOf(Interface.PERCENT_INVOKE_METHOD))!=-1){//%invoke_methodjava.lang.String.valueOf(1)%invoke_method
                        if((last_percent_index=service_page.indexOf(Interface.PERCENT_INVOKE_METHOD,percent_index+14))!=-1){
                          service_page=service_page.substring(0,percent_index)+this.invoke_method(service_page.substring(percent_index+14,last_percent_index))+service_page.substring(last_percent_index+14);
                        }
                      }
                    }while(percent_index!=-1);
                  }
                  else if(sp.sqlReturnType==SQL_MESSAGE_TYPE){
                    service_page=service_page.replaceAll(PERCENT_SQL_MESSAGE+PERCENT_SQL_MESSAGE,sp.sqlErrorMessage);
                  }
                  outStream.write(service_page.getBytes(manager.getLocalCodepage()));//->local codepage
                }
                return;
              }
            }//if(filedata!=null)
            else manager.getLog().write(sessionID,ERROR_SERVICE_NOT_FOUND,sp.sql);
          }//if '.sql'->end of name
          else if(sp.sql.endsWith(EXTENSION_FILE_HTML)){
            String name=sp.name;
            this.setSQLIgnore(true);
            sp.name=sp.sql.substring(0,sp.sql.length()-5);/*.html*/this.writeData(sp);sp.name=name;
            this.setSQLIgnore(false);
          }//if '.html'->end of name
          else manager.getLog().write(sessionID,ERROR_SERVICE_NOT_FOUND,sp.sql);
          //Database.executeSQL(sessionID,sp);// move to ^
        }
      }
      sp.sqlList.clear();//remove executed sql
    }//if !sqlIgnore
    //text servlet param value -> to number
    page_param.substParam(sp);
    str=sp.service?manager.getInitial().getServicePages():manager.getInitial().getServiceTemplates();//ini service(pages or templates) dir
    /*set option to initial param, but a big risk to name="http://autozvit.com/files/index.html may be use for other server trolling"*/
    /*str="http://server.org/templates/"*/
    if(str!=null&&str.startsWith(REQUEST_HTTP)){
      if(!str.endsWith(LOCAL_DELIM))str+=LOCAL_DELIM;
      /*get file by url: data=manager.getPage(...)*/
      byte[] page=clientSession.getPage(sessionID,str+sp.name+POINT+EXTENSION_FILE_HTML,1);
      if(page!=null)data=new String(page,manager.getLocalCodepage()).toCharArray();
    }
    else{//read file in buffer->data
      if(str!=null&&!str.endsWith(LOCAL_DELIM_2))str+=LOCAL_DELIM_2;
      /*other variant->get file by filesystem: data=Convert.readCharArrayFromFile(...)*/
      //use system codepage
      //data=Convert.readCharArrayFromFile((str!=null?str:manager.getServletFilepath()+(sp.service?FILEPATH_SERVICE_PAGES:FILEPATH_SERVICE_TEMPLATES))
      //    +sp.name+POINT+EXTENSION_FILE_HTML);//not thread safe
      //use local codepage(good for Android UTF-8 file encoding)
      data=Convert.readCharArrayFromFile((str!=null?str:manager.getServletFilepath()+(sp.service?FILEPATH_SERVICE_PAGES:FILEPATH_SERVICE_TEMPLATES))
          +sp.name+POINT+EXTENSION_FILE_HTML,manager.getLocalCodepage());//not thread safe
      //data=Convert.readByteArrayFromFile(...);//not thread safe
    }
    if(data==null){
      manager.getLog().write(sessionID,ERROR_SERVICE_REGISTRATION_FAILED,sp.name);
      str=manager.getInitial().getServicePages();//ini service pages dir
      if(str!=null&&!str.endsWith(LOCAL_DELIM_2))str+=LOCAL_DELIM_2;
      outStream.write(Convert.readFromFile(str!=null?str+FILENAME_TEMPLATE_NOT_FOUND:manager.getServletFilepath()+FILEPATH_TEMPLATE_NOT_FOUND));
      return;
    }
    manager.getLog().write(sessionID,INFO_SERVICE_REGISTERED,sp.name);
    str=EMPTY;
    data_left=new boolean[SIZE_DATA_LEFT_ARRAY];
    data_left[data_left_ind]=false;//allways false->data_left[0]
    while(index<data.length){
      //read char data
      data_ch=data[index];
      //scan char code
      if(data_ch==CODE_PERCENT){percent=true;str+=data_ch;}//special scrypt can be found(%)
      // add comments as /**/ and "text in 'text'" and 'text'
      else if(data_ch==CODE_DOUBLE_UPPER){//"text"
        if(text_type==0)text_type=1;//text_type==0(no text)
        else if(text_type==1)text_type=0;//text_type==1(text with double_upper)
        str+=data_ch;//double upper write to str
      }//if "
      else if(data_ch==CODE_UPPER){//'text'
        if(text_type==0)text_type=2;//text_type==0(no text)
        else if(text_type==2)text_type=0;//text_type==2(text with upper)
        str+=data_ch;
      }//if '
      else if(data_ch==CODE_SLASH&&data[index+1]==CODE_STAR){//open comment
        if(text_type==0)text_type=3;
        str+=data_ch;str+=data[index+1];index++;
      }//if /*
      else if(data_ch==CODE_STAR&&data[index+1]==CODE_SLASH){//close comment
        if(text_type==3)text_type=0;
        str+=data_ch;str+=data[index+1];index++;
      }//if */
      else if(text_type==0/*!text*/&&!data_left[data_left_ind]){//no text and no data left
        //scan char code
        if(data_ch==CODE_OPEN){//'<'||'</'
          is_tag=true;
          if(data[index+1]==CODE_SLASH){status=2;++index;/*slash left*/}//'</'
          else status=1;//'<'
        }
        else if(data_ch==CODE_CLOSE){is_tag=true;status=3;}//'>'
        else if(data_ch==CODE_NEXT)str+=SPACE;//add space, data continue on next string
        else if(data_ch==CODE_RETURN);//no action
        else str+=data_ch;//all others
      }//if !text && !data_left
      else if(text_type>0/*text*/||data_left[data_left_ind])str+=data_ch;//all others
      //---macro substitutions--- (%%)
      /*
      special % scrypt logic:
      1."%if" logic seek;
      2.if no <data left> seek->functions (write data,file;set param,invoke method);
      3.set->substitutions and ->process_logic variable;
      */
      if(percent/*str.contains(PERCENT)*/){
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /*if scrypt rule
        ...
        %if condition_1 %if
          <html>
        %elseif%elseif
          <html>
          ...
          %if condition_N %if
            <html>
          %elseif%elseif
            <html>
          %endif%endif
            ...
          <html>
        %endif%endif
        ...
        */
        //!SCRYPT!
        //if scrypt(%if<boolean>%if)
        if((percent_index=str.indexOf(Interface.PERCENT_IF))!=-1){//%if...%if
          if((last_percent_index=str.indexOf(Interface.PERCENT_IF,percent_index+3))!=-1){
            String s=data_left[data_left_ind]?EMPTY:str.substring(0,percent_index);//prev data_left
            if_level++;
            if(!data_left[data_left_ind]){
              String exp=str.substring(percent_index+3,last_percent_index).trim();//inner string %if_%if
              data_left_ind++;
              data_left[data_left_ind]=!if_scrypt(sessionID,sp,exp);//get data_left
            }
            //Manager.getLog().write("/////////IF//////////if_level="+if_level+" data_left_ind="+data_left_ind+"("+str+")"+" prev="+s+"\r\n");
            if(!htmlCode){if(status==1){outStream.write(CODE_OPEN);status=0;}last_status=0;/*allways is_tag=false;*/}
            outStream.write(s.trim().getBytes(manager.getLocalCodepage()));//->local codepage
            str=EMPTY;percent=false;
          }
        }
        //elseif scrypt(%elseif%elseif)
        else if((percent_index=str.indexOf(Interface.PERCENT_ELSEIF))!=-1){//%elseif%elseif
          if((last_percent_index=str.indexOf(Interface.PERCENT_ELSEIF,percent_index+7))!=-1){
            String s=data_left[data_left_ind]?EMPTY:str.substring(0,percent_index);//prev data_left
            if(if_level==data_left_ind){
              data_left[data_left_ind]=!data_left[data_left_ind];//reverse value
            }
            //Manager.getLog().write("/////////IFELSE//////if_level="+if_level+" data_left_ind="+data_left_ind+" prev="+s+"\r\n");
            if(!htmlCode){if(status==1){outStream.write(CODE_OPEN);status=0;}last_status=0;/*allways is_tag=false;*/}
            outStream.write(s.trim().getBytes(manager.getLocalCodepage()));//->local codepage
            str=EMPTY;percent=false;
          }
        }
        //endif scrypt(%endif%endif)
        else if((percent_index=str.indexOf(Interface.PERCENT_ENDIF))!=-1){//%endif%endif
          if((last_percent_index=str.indexOf(Interface.PERCENT_ENDIF,percent_index+6))!=-1){
            String s=data_left[data_left_ind]?EMPTY:str.substring(0,percent_index);//prev data_left
            if(if_level==data_left_ind&&data_left_ind>0)data_left_ind--;//%if_%if ends
            if(if_level>0)if_level--;
            //Manager.getLog().write("/////////ENDIF///////if_level="+if_level+" data_left_ind="+data_left_ind+" prev="+s+"\r\n");
            if(!htmlCode){if(status==1){outStream.write(CODE_OPEN);status=0;}last_status=0;/*allways is_tag=false;*/}
            outStream.write(s.trim().getBytes(manager.getLocalCodepage()));//->local codepage
            str=EMPTY;percent=false;
          }
        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(!data_left[data_left_ind]){//no data left (and macros too ...)
          //!FUNCTION!
          //write file by filename (%writefile<header.html>%writefile)
          if((percent_index=str.indexOf(Interface.PERCENT_WRITEFILE))!=-1){//%writefileheader.html%writefile
            if((last_percent_index=str.indexOf(Interface.PERCENT_WRITEFILE,percent_index+10))!=-1){
              String filename=str.substring(percent_index+10,last_percent_index).trim(),filepath=manager.getInitial().getServiceTemplates();//ini service templates dir
              byte[] filedata;
              if(filename.startsWith(REQUEST_HTTP)){/*http path to file(from filename)*/
                filedata=clientSession.getPage(sessionID,filename,1);
              }else if(filepath!=null&&filepath.startsWith(REQUEST_HTTP)){/*http path to file(from filepath)*/
                if(!filepath.endsWith(LOCAL_DELIM))filepath+=LOCAL_DELIM;
                filedata=clientSession.getPage(sessionID,filepath+filename,1);
              }else{/*no http path to file*/
                if(filepath!=null&&!filepath.endsWith(LOCAL_DELIM_2))filepath+=LOCAL_DELIM_2;
                filedata=Convert.readFromFile((filepath!=null?filepath:manager.getServletFilepath()+FILEPATH_SERVICE_TEMPLATES)+filename);//not thread safe
              }
              str=str.substring(0,percent_index)+str.substring(last_percent_index+10);//move ^
              outStream.write(str.getBytes(manager.getLocalCodepage()));str=EMPTY;//write first str(not write in early ver.)
              if(filedata!=null){outStream.write(filedata);filedata=null;}//and filedata second
              else manager.getLog().write(sessionID,ERROR_SERVICE_NOT_FOUND,filename);
              percent=false;
            }
          }
          //write data as writeData(...) (%writedata<menu>%writedata)
          else if((percent_index=str.indexOf(Interface.PERCENT_WRITEDATA))!=-1){//%writedatamenu%writedata
            if((last_percent_index=str.indexOf(Interface.PERCENT_WRITEDATA,percent_index+10))!=-1){
              String name=sp.name;
              sp.name=str.substring(percent_index+10,last_percent_index).trim();
              str=str.substring(0,percent_index)+str.substring(last_percent_index+10);//move ^
              outStream.write(str.getBytes(manager.getLocalCodepage()));str=EMPTY;//write first str(not write in early ver.)
              this.writeData(sp);//and data second
              sp.name=name;
              percent=false;
            }
          }
          //set param(%set_param#_%set_param)
          else if((percent_index=str.indexOf(Interface.PERCENT_SET_PARAM))!=-1){//%set_param#_%set_param
            if((last_percent_index=str.indexOf(Interface.PERCENT_SET_PARAM,percent_index+10))!=-1){
              String s;//execution->(java method as invoke_method or sql query)
              char ch;
              int i,j,str_size=str.length(),param_num;
              for(i=percent_index+10;i<str_size;i++){
                ch=str.charAt(i);
                if(ch<'0'||ch>'9')break;//not in [30..39]->'0'..'9'
              }
              param_num=Convert.toIntValue(str.substring(percent_index+10,i).trim());
              for(;i<str_size;i++){//left from left '=' and ' ' and '"'
                ch=str.charAt(i);
                if(ch!=CODE_EQUAL&&ch!=CODE_EMPTY&&ch!=CODE_DOUBLE_UPPER)break;//not in [3D,20,"]->'=',' ','"'
              }
              for(j=last_percent_index;j>0;j--){//left from right ' ' and '"'
                ch=str.charAt(j-1);
                if(ch!=CODE_EMPTY&&ch!=CODE_DOUBLE_UPPER)break;//not in [20,"]
              }
              s=str.substring(i,j);
              //s=this.paramSubst(s);//param# substitutions in string (%param<number>%param)
              if(s.startsWith(INVOKE_METHOD)){//invoke_method
                s=(String)this.invoke_method(s.substring(1).trim());//s[0] left
              }
              else{//SQL query
                sp.sqlIndex++;sp.sql=s;//for %set_param#_%set_param sql index incrementing
                s=database.getStringValue(sessionID,sp);
              }
              //set param&param_type
              if(param_num>=sp.paramList.size())sp.paramList.setSize(param_num+1);
              sp.paramList.setElementAt(s,param_num);
              if(param_num>=sp.paramTypeList.size())sp.paramTypeList.setSize(param_num+1);
              sp.paramTypeList.setElementAt(SERVICE_PARAM_TYPE_STRING,param_num);
              str=str.substring(0,percent_index)+str.substring(last_percent_index+10);
              percent=false;
            }
          }
          //invoke method(%invoke_method<java class method>%invoke_method)
          else if((percent_index=str.indexOf(Interface.PERCENT_INVOKE_METHOD))!=-1){//%invoke_methodjava.lang.String.valueOf(1)%invoke_method
            if((last_percent_index=str.indexOf(Interface.PERCENT_INVOKE_METHOD,percent_index+14))!=-1){
              //s=str.substring(percent_index+14,last_percent_index);s=this.paramSubst(s);//param# substitutions in string (%param<number>%param)
              str=str.substring(0,percent_index)+this.invoke_method(str.substring(percent_index+14,last_percent_index))+str.substring(last_percent_index+14);
              percent=false;
            }
          }
          //!SUBSTITUTIONS!
          //param# substitutions by number (%param<number>%param)
          if((percent_index=str.indexOf(Interface.PERCENT_PARAM))!=-1){//%param1%param
            if((last_percent_index=str.indexOf(Interface.PERCENT_PARAM,percent_index+6))!=-1){
              String substr=(String)sp.paramList.get(Convert.toIntValue(str.substring(percent_index+6,last_percent_index).trim()));
              if(param_encode){if(!is_encoded(substr))substr=URLEncoder.encode(substr);}//URL encode
              else if(param_quot)substr=substr.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
              str=str.substring(0,percent_index)+substr+str.substring(last_percent_index+6);
              percent=false;
            }
          }
          //name substitutions (%name%name)
          else if((percent_index=str.indexOf(Interface.PERCENT_NAME))!=-1){//%name%name
            if((last_percent_index=str.indexOf(Interface.PERCENT_NAME,percent_index+5))!=-1){
              str=str.substring(0,percent_index)+LOCAL_NAME+str.substring(last_percent_index+5);
              percent=false;
            }
          }
          //version substitutions (%version%version)
          else if((percent_index=str.indexOf(Interface.PERCENT_VERSION))!=-1){//%version%version
            if((last_percent_index=str.indexOf(Interface.PERCENT_VERSION,percent_index+8))!=-1){
              str=str.substring(0,percent_index)+LOCAL_VERSION+str.substring(last_percent_index+8);
              percent=false;
            }
          }
          //login substitutions (%login%login)
          else if((percent_index=str.indexOf(Interface.PERCENT_LOGIN))!=-1){//%login%login
            if((last_percent_index=str.indexOf(Interface.PERCENT_LOGIN,percent_index+6))!=-1){
              str=str.substring(0,percent_index)+sp.login+str.substring(last_percent_index+6);
              percent=false;
            }
          }
          //password substitutions (%password%password)
          else if((percent_index=str.indexOf(Interface.PERCENT_PASSWORD))!=-1){//%password%password
            if((last_percent_index=str.indexOf(Interface.PERCENT_PASSWORD,percent_index+9))!=-1){
              str=str.substring(0,percent_index)+sp.password+str.substring(last_percent_index+9);
              percent=false;
            }
          }
          //database substitutions (%database%database)
          else if((percent_index=str.indexOf(Interface.PERCENT_DATABASE))!=-1){//%database%database
            if((last_percent_index=str.indexOf(Interface.PERCENT_DATABASE,percent_index+9))!=-1){
              str=str.substring(0,percent_index)+sp.database+str.substring(last_percent_index+9);
              percent=false;
            }
          }
          //remoteaddr substitutions (%remoteaddr%remoteaddr)
          else if((percent_index=str.indexOf(Interface.PERCENT_REMOTEADDR))!=-1){//%remoteaddr%remoteaddr
            if((last_percent_index=str.indexOf(Interface.PERCENT_REMOTEADDR,percent_index+11))!=-1){
              str=str.substring(0,percent_index)+sp.remoteAddr+str.substring(last_percent_index+11);
              percent=false;
            }
          }
          //remotehost substitutions (%remotehost%remotehost)
          else if((percent_index=str.indexOf(Interface.PERCENT_REMOTEHOST))!=-1){//%remotehost%remotehost
            if((last_percent_index=str.indexOf(Interface.PERCENT_REMOTEHOST,percent_index+11))!=-1){
              str=str.substring(0,percent_index)+sp.remoteHost+str.substring(last_percent_index+11);
              percent=false;
            }
          }
          //sessionid substitutions (%sessionid%sessionid)
          else if((percent_index=str.indexOf(Interface.PERCENT_SESSIONID))!=-1){//%sessionid%sessionid
            if((last_percent_index=str.indexOf(Interface.PERCENT_SESSIONID,percent_index+10))!=-1){
              str=str.substring(0,percent_index)+sessionID+str.substring(last_percent_index+10);
              percent=false;
            }
          }
          //servicetrash substitutions (%servicetrash%servicetrash)
          else if((percent_index=str.indexOf(Interface.PERCENT_SERVICETRASH))!=-1){//%servicetrash%servicetrash
            if((last_percent_index=str.indexOf(Interface.PERCENT_SERVICETRASH,percent_index+13))!=-1){
              String st=manager.getInitial().getServiceTrash();//ini service trash dir
              if(st!=null&&!st.endsWith(LOCAL_DELIM_2))st+=LOCAL_DELIM_2;
              st=(st!=null)?clientSession.toURL(st):URL_SERVICE_TRASH;
              str=str.substring(0,percent_index)+st+str.substring(last_percent_index+13);
              percent=false;
            }
          }
          //!PROCESS LOGIC!
          /*
          //param begin replace (%begin_replace%begin_replace) for database.sql->invoke this.replaceMecro()
          else if((percent_index=str.indexOf(Interface.PERCENT_BEGIN_REPLACE))!=-1){//%begin_replace%begin_replace
            if((last_percent_index=str.indexOf(Interface.PERCENT_BEGIN_REPLACE,percent_index+13))!=-1){
              str=str.substring(0,percent_index)+str.substring(last_percent_index+13);
              param_replace=true;database.setReplace(param_replace);percent=false;
            }
          }
          //param end replace (%end_replace%end_replace)
          else if((percent_index=str.indexOf(Interface.PERCENT_END_REPLACE))!=-1){//%end_replace%end_replace
            if((last_percent_index=str.indexOf(Interface.PERCENT_END_REPLACE,percent_index+11))!=-1){
              str=str.substring(0,percent_index)+str.substring(last_percent_index+11);
              param_replace=false;database.setReplace(param_replace);percent=false;
            }
          }*/
          //param begin encode (%begin_encode%begin_encode)
          else if((percent_index=str.indexOf(Interface.PERCENT_BEGIN_ENCODE))!=-1){//%begin_encode%begin_encode
            if((last_percent_index=str.indexOf(Interface.PERCENT_BEGIN_ENCODE,percent_index+13))!=-1){
              str=str.substring(0,percent_index)+str.substring(last_percent_index+13);
              param_encode=true;percent=false;
            }
          }
          //param end encode (%end_encode%end_encode)
          else if((percent_index=str.indexOf(Interface.PERCENT_END_ENCODE))!=-1){//%end_encode%end_encode
            if((last_percent_index=str.indexOf(Interface.PERCENT_END_ENCODE,percent_index+11))!=-1){
              str=str.substring(0,percent_index)+str.substring(last_percent_index+11);
              param_encode=false;percent=false;
            }
          }
          //param begin quot (%begin_quot%begin_quot)
          else if((percent_index=str.indexOf(Interface.PERCENT_BEGIN_QUOT))!=-1){//%begin_quot%begin_quot
            if((last_percent_index=str.indexOf(Interface.PERCENT_BEGIN_QUOT,percent_index+11))!=-1){
              str=str.substring(0,percent_index)+str.substring(last_percent_index+11);
              param_quot=true;percent=false;
            }
          }
          //param end quot (%end_quot%end_quot)
          else if((percent_index=str.indexOf(Interface.PERCENT_END_QUOT))!=-1){//%end_quot%end_quot
            if((last_percent_index=str.indexOf(Interface.PERCENT_END_QUOT,percent_index+9))!=-1){
              str=str.substring(0,percent_index)+str.substring(last_percent_index+9);
              param_quot=false;percent=false;
            }
          }
          //param begin html (%begin_html%begin_html)
          else if((percent_index=str.indexOf(Interface.PERCENT_BEGIN_HTML))!=-1){//%begin_html%begin_html
            if((last_percent_index=str.indexOf(Interface.PERCENT_BEGIN_HTML,percent_index+11))!=-1){
              str=str.substring(0,percent_index)+str.substring(last_percent_index+11);
              param_html=true;database.setHtmlCode(param_html);percent=false;
            }
          }
          //param end html (%end_html%end_html)
          else if((percent_index=str.indexOf(Interface.PERCENT_END_HTML))!=-1){//%end_html%end_html
            if((last_percent_index=str.indexOf(Interface.PERCENT_END_HTML,percent_index+9))!=-1){
              str=str.substring(0,percent_index)+str.substring(last_percent_index+9);
              param_html=false;database.setHtmlCode(param_html);percent=false;
            }
          }
          //param begin not quot (%begin_not_quot%begin_not_quot)
          else if((percent_index=str.indexOf(Interface.PERCENT_BEGIN_NOT_QUOT))!=-1){//%begin_not_quot%begin_not_quot
            if((last_percent_index=str.indexOf(Interface.PERCENT_BEGIN_NOT_QUOT,percent_index+15))!=-1){
              str=str.substring(0,percent_index)+str.substring(last_percent_index+15);
              param_not_quot=true;percent=false;
            }
          }
          //param end not quot (%end_not_quot%end_not_quot)
          else if((percent_index=str.indexOf(Interface.PERCENT_END_NOT_QUOT))!=-1){//%end_not_quot%end_not_quot
            if((last_percent_index=str.indexOf(Interface.PERCENT_END_NOT_QUOT,percent_index+13))!=-1){
              str=str.substring(0,percent_index)+str.substring(last_percent_index+13);
              param_not_quot=false;percent=false;
            }
          }
        }//if !data_left
      }//if PERCENT
      else if(data_left[data_left_ind]){str=EMPTY;percent=false;}
      //---macro substitutions--- (%%)
      if(is_tag/*last_status!=status*/){//is_tag|status change
        str=str.trim();
        //str contains code/*if(str.length()>0)*/
        switch(last_status){
        case 0://data before first <...
          outStream.write(str.getBytes(manager.getLocalCodepage()));//->local codepage
          if(status==3)outStream.write(CODE_CLOSE);//...> abnormal(may be script)
          str=EMPTY;percent=false;break;
        case 1://open <...
          switch(status){
          case 1://data between <...<
          case 2://data between <...</
            outStream.write((CODE_OPEN+str).getBytes(manager.getLocalCodepage()));//->local codepage
            str=EMPTY;percent=false;break;
          case 3://data between <...>
            if(!comment){
            if(str.startsWith(HTML_ELEMENT_SCRIPT)){//if <script ...>
              outStream.write((CODE_OPEN+str+CODE_CLOSE).getBytes(manager.getLocalCodepage()));//->local codepage
              htmlCode=false;
              str=EMPTY;percent=false;
            }
            else if(htmlCode){//if htmlCode
            if(str.startsWith(HTML_ELEMENT_COMMENT_START)){//comment==false
              outStream.write((CODE_OPEN+str+CODE_CLOSE).getBytes(manager.getLocalCodepage()));//->local codepage
              if(!str.endsWith(HTML_ELEMENT_COMMENT_FINISH))comment=true;
              str=EMPTY;percent=false;
            }
            else{
            //process data, sql can found
            //if(manager.isDebug())manager.getLog().write(sessionID,DEBUG_PARSE_ATTRIBUTES,str);
            Vector v=this.getFields_(str);//v->all fields of tag
            //if(manager.isDebug())manager.getLog().write(sessionID,DEBUG_PARSE_ATTRIBUTE_VALUE,v.toString());
            tag=null;cols=null;
            type=this.getType(v);sql=this.getSQL_(v);file=this.getSQLFile_(v);ref=this.getSQLRef_(v);
            if(file!=null&&(file.startsWith(SQL_SELECT)||file.startsWith(SQL_SELECT_UPPER_CASE))){//get filename from database
              sp.sqlIndex++;sp.sql=file;
              //file=database.getStringValue(sessionID,sp);//only one value got(but may be a list?)
              StringListItem item;
              Vector list=database.getStringList(sessionID,sp,1);
              file=EMPTY;
              for(int i=0;i<list.size();i++){
                item=(StringListItem)list.elementAt(i);
                file+=item.id+POINT_COMA;//list by point-coma: filename1;filename2; ... filenameN;
              }
            }
            if(type==HTML_SUBTYPE_TH||/*add tag for some <tag>...</tag> html objects*/
              type==HTML_SUBTYPE_TD||
              type==HTML_SUBTYPE_OPTION||
              type==HTML_SUBTYPE_TR||
              type==HTML_TYPE_SELECT||/*type==HTML_TYPE_DROPDOWN_LIST||type==HTML_TYPE_LISTBOX||*/
              type==HTML_TYPE_TEXTAREA||
              type==HTML_TYPE_TABLE){tag=new Tag();tag.type=type;tag_data.add(tag);tag_ind++;}
            if(type==HTML_TYPE_TABLE||type==HTML_TYPE_SQL){
              String page=this.getTablePage_(v);//page="..." for <table page="..."> <sql="" page="...">
              if(page!=null){
                this.pageParamSubst(page);
                //text servlet param value -> to number
                page_param.substParam(sp);
                str=this.removeTemplate(str,HTML_ELEMENT_TABLE_PAGE);//remove page
              }
              if(type==HTML_TYPE_TABLE)cols=this.getTableCols_(v);//cols="..."
            }
            if(cols!=null)str=this.removeTemplate(str,HTML_ELEMENT_TABLE_COLS);//remove cols
            if(sql!=null){
              str=this.removeTemplate(str,HTML_ELEMENT_SQL);//remove sql
              if(tag!=null){
                if(type==HTML_SUBTYPE_TH||type==HTML_SUBTYPE_TD){//7. <td sql="..."> <th sql="...">
                  sp.sqlIndex++;if(sql.startsWith(SQL_NUMBER)){page_param.sqlNumber=sp.sqlIndex;sql=sql.substring(1);}sp.sql=sql;
                  GridData gd=database.getGridData(sessionID,sp,page_param.sqlNumber,page_param.pageNumber,page_param.rowCount);
                  Vector items=gd.items;
                  total_row_count=gd.totalRowCount;
                  tag.data=this.getGridData(items,null,null,sp);
                  if(items!=null){items.clear();items=null;}//subitems remove?
                  gd=null;
                }
                else if(type==HTML_SUBTYPE_OPTION){//2. <option sql="...">
                  sp.sqlIndex++;if(sql.startsWith(SQL_NUMBER)){page_param.sqlNumber=sp.sqlIndex;sql=sql.substring(1);}sp.sql=sql;
                  tag.data=database.getStringValue(sessionID,sp);
                }
                else if(type==HTML_SUBTYPE_TR){//6. <tr sql="...">
                  StringListItem item;
                  sp.sqlIndex++;if(sql.startsWith(SQL_NUMBER)){page_param.sqlNumber=sp.sqlIndex;sql=sql.substring(1);}sp.sql=sql;
                  Vector list=database.getStringList(sessionID,sp,1);
                  tag.data=EMPTY;
                  for(int i=0;i<list.size();i++){
                    item=(StringListItem)list.elementAt(i);
                    //html code from file->table_tr.html
                    tag.data+=CODE_OPEN+HTML_ELEMENT_TABLE_TD+CODE_CLOSE;//<td>
                    tag.data+=item.id;
                    tag.data+=CODE_OPEN+SLASH+HTML_ELEMENT_TABLE_TD+CODE_CLOSE+NEXT_LINE;//</td>/r/n;
                  }
                }
                else if(type==HTML_TYPE_SELECT/*||type==HTML_TYPE_DROPDOWN_LIST||type==HTML_TYPE_LISTBOX*/){//1. <select sql="...">
                  StringListItem item;
                  selected_value=this.getSelected(v);//not remove "selected=..." from str!
                  //selected_value_by_name=this.getSelectedByName(v);
                  /*selected value from sql SELECT*/
                  if(selected_value!=null&&(selected_value.startsWith(SQL_SELECT)||selected_value.startsWith(SQL_SELECT_UPPER_CASE))){
                    sp.sqlIndex++;sp.sql=selected_value;
                    selected_value=database.getStringValue(sessionID,sp);
                  }
                  sp.sqlIndex++;if(sql.startsWith(SQL_NUMBER)){page_param.sqlNumber=sp.sqlIndex;sql=sql.substring(1);}sp.sql=sql;
                  Vector list=database.getStringList(sessionID,sp,2);
                  tag.data=EMPTY;
                  //Manager.getLog().write("start SELECT\r\n");
                  //other way to list items->
                  //Object[] items=list.toArray();
                  //int items_size=items.length;
                  //for(int i=0;i<items_size;i++){
                  //item=(StringListItem)items[i];...}
                  for(int i=0;i<list.size();i++){
                    item=(StringListItem)list.get(i);//item=(StringListItem)list.elementAt(i);
                    //Manager.getLog().write(item.value+"\r\n");
                    //html code from file->select_option.html
                    tag.data+=CODE_OPEN+HTML_ELEMENT_SELECT_OPTION+SPACE;//<option_
                    //@selected->selected.html
                    if(selected_value!=null&&selected_value.equals(item.id))tag.data+=HTML_ELEMENT_SELECT_OPTION_SELECTED+SPACE;//selected_
                    //if(selected_value_by_name!=null&&selected_value_by_name.equals(item.value))tag.data+=HTML_ELEMENT_SELECT_OPTION_SELECTED+SPACE;//selected_by_name_
                    tag.data+=HTML_ELEMENT_SELECT_OPTION_VALUE+EQUAL+DOUBLE_UPPER+item.id+DOUBLE_UPPER+CODE_CLOSE+item.value;//value="...">...
                    tag.data+=CODE_OPEN+SLASH+HTML_ELEMENT_SELECT_OPTION+CODE_CLOSE+NEXT_LINE;//</option>/r/n;
                  }
                  //Manager.getLog().write("stop SELECT\r\n");
                }
                else if(type==HTML_TYPE_TEXTAREA){//4. <textarea sql="...">
                  sp.sqlIndex++;if(sql.startsWith(SQL_NUMBER)){page_param.sqlNumber=sp.sqlIndex;sql=sql.substring(1);}sp.sql=sql;
                  GridData gd=database.getGridData(sessionID,sp,page_param.sqlNumber,page_param.pageNumber,page_param.rowCount);
                  Vector items=gd.items;
                  total_row_count=gd.totalRowCount;
                  tag.data=this.getGridData(items,null,null,sp);
                  if(items!=null){items.clear();items=null;}//subitems remove?
                  gd=null;
                }
                else if(type==HTML_TYPE_TABLE){//5. <table sql="...">
                  sp.sqlIndex++;if(sql.startsWith(SQL_NUMBER)){page_param.sqlNumber=sp.sqlIndex;sql=sql.substring(1);}sp.sql=sql;
                  GridData gd=database.getGridData(sessionID,sp,page_param.sqlNumber,page_param.pageNumber,page_param.rowCount);
                  Vector items=gd.items;
                  total_row_count=gd.totalRowCount;
                  if(cols==null)tag.data=this.getTableColumns(items,database.getColumnCount());
                  else tag.data=this.getTableColumns(items,cols,sp,param_not_quot);
                  if(items!=null){items.clear();items=null;}//subitems remove?
                  gd=null;
                }
                str=CODE_OPEN+str+CODE_CLOSE;
              }//if tag
              else{//tag==null
                if(type==HTML_TYPE_TEXT||type==HTML_TYPE_PASSWORD||type==HTML_TYPE_HIDDEN|| //3. <input sql="...">
                  type==HTML_TYPE_CHECKBOX||type==HTML_TYPE_RADIO||type==HTML_TYPE_FILE||
                  type==HTML_TYPE_BUTTON||type==HTML_TYPE_SUBMIT||type==HTML_TYPE_RESET){
                  StringListItem item;
                  sp.sqlIndex++;if(sql.startsWith(SQL_NUMBER)){page_param.sqlNumber=sp.sqlIndex;sql=sql.substring(1);}sp.sql=sql;
                  boolean col_3=(type==HTML_TYPE_CHECKBOX||type==HTML_TYPE_RADIO);
                  Vector list=col_3?database.getStringList(sessionID,sp,3):database.getStringList(sessionID,sp,2);
                  int i=0,ind1,ind2;
                  String r_str,all_str=EMPTY;
                  while(i<list.size()){
                    r_str=str;
                    item=(StringListItem)list.elementAt(i);
                    ind1=r_str.indexOf(HTML_ELEMENT_INPUT_NAME);
                    //html code from file->input_name.html (warning! code without first space)
                    if(ind1==-1)r_str+=SPACE+HTML_ELEMENT_INPUT_NAME+EQUAL+DOUBLE_UPPER+item.id+DOUBLE_UPPER;//add name
                    else{//replace name
                      ind2=r_str.indexOf(SPACE,ind1);
                      if(ind2==-1)ind2=r_str.length();
                      //html code from file->input_name.html
                      r_str=r_str.replaceFirst(r_str.substring(ind1,ind2),HTML_ELEMENT_INPUT_NAME+EQUAL+DOUBLE_UPPER+item.id+DOUBLE_UPPER);
                    }
                    ind1=r_str.indexOf(HTML_ELEMENT_INPUT_VALUE);
                    //html code from file->input_value.html (warning! code without first space)
                    if(ind1==-1)r_str+=SPACE+HTML_ELEMENT_INPUT_VALUE+EQUAL+DOUBLE_UPPER+item.value+DOUBLE_UPPER;//add value
                    else{//replace value
                      ind2=r_str.indexOf(SPACE,ind1);
                      if(ind2==-1)ind2=r_str.length();
                      //html code from file->input_value.html
                      r_str=r_str.replaceFirst(r_str.substring(ind1,ind2),HTML_ELEMENT_INPUT_VALUE+EQUAL+DOUBLE_UPPER+item.value+DOUBLE_UPPER);
                    }
                    all_str+=CODE_OPEN+r_str+CODE_CLOSE+(col_3?item.value2:EMPTY)+NEXT_LINE;
                    i++;
                  }
                  str=all_str;
                }
                else if(type==HTML_TYPE_SQL){//8. <sql="...">
                  sp.sqlIndex++;if(sql.startsWith(SQL_NUMBER)){page_param.sqlNumber=sp.sqlIndex;sql=sql.substring(1);}sp.sql=sql;
                  if(sql.startsWith(SQL_SELECT)||sql.startsWith(SQL_SELECT_UPPER_CASE)){
                    int from_index=-1,to_index=-1;
                    fetch=this.getSQLFetch(v);
                    if(fetch!=null){//patch fetch (fetch="1..10")
                      if((percent_index=fetch.indexOf(POINT))!=-1){//(1..10)
                        last_percent_index=fetch.lastIndexOf(POINT);
                        try{
                          from_index=Convert.toIntValue(fetch.substring(0,percent_index));
                          to_index=Convert.toIntValue(fetch.substring(last_percent_index+1));
                        }catch(Exception e){}
                      }
                    }
                    GridData gd;
                    if(from_index>-1&&to_index>-1)gd=database.getGridData(sessionID,sp,page_param.sqlNumber,page_param.pageNumber,to_index-from_index,from_index);
                    else gd=database.getGridData(sessionID,sp,page_param.sqlNumber,page_param.pageNumber,page_param.rowCount);
                    Vector items=gd.items;
                    total_row_count=gd.totalRowCount;
                    if(items!=null){
                      //write data to xls file->
                      if(file!=null&&file.endsWith(POINT+EXTENSION_FILE_XLS)){this.writeToXls(items,file,ref,FILEPATH_SERVICE_TRASH);str=EMPTY;/*no data view*/}
                      else str=this.getGridData(items,file,ref,sp);
                      items.clear();items=null;//subitems remove?
                    }
                    gd=null;
                  }
                  else{//such as: !@SELECT ... FROM DUAL
                    //error and message !-error(1) @-message(2) !@ or @!-error message(3)
                    //Warning! Work only message(2) or error message(3) (for get return message from sql)
                    if(sp.sql.startsWith(SQL_ERROR_MESSAGE)){sp.sqlType=SQL_ERROR_MESSAGE_TYPE;sp.sql=sp.sql.substring(2);}
                    else if(sp.sql.startsWith(SQL_MESSAGE_ERROR)){sp.sqlType=SQL_ERROR_MESSAGE_TYPE;sp.sql=sp.sql.substring(2);}
                    else if(sp.sql.startsWith(SQL_ERROR)){sp.sqlType=SQL_ERROR_TYPE;sp.sql=sp.sql.substring(1);}
                    else if(sp.sql.startsWith(SQL_MESSAGE)){sp.sqlType=SQL_MESSAGE_TYPE;sp.sql=sp.sql.substring(1);}
                    database.executeSQL(sessionID,sp);
                  }
                }
                else if(type==HTML_TYPE_IMG||type==HTML_TYPE_IMAGE){//9. <img src="...">
                  BytesListItem item;
                  sp.sqlIndex++;if(sql.startsWith(SQL_NUMBER)){page_param.sqlNumber=sp.sqlIndex;sql=sql.substring(1);}sp.sql=sql;
                  Vector list=database.getBytesList(sessionID,sp);
                  int i=0,ind1,ind2;
                  String r_str,all_str=EMPTY,url;
                  while(i<list.size()){
                    r_str=str;
                    item=(BytesListItem)list.elementAt(i);
                    if(item.value!=null&&item.value.length>0){//file buffer length>0
                      url=this.saveAsImageFile(item.value,item.id.length()==0?null:item.id);
                      ind1=r_str.indexOf(HTML_ELEMENT_IMG_SRC);
                      //html code from file->img_src.html (warning! code without first space)
                      if(ind1==-1)r_str+=SPACE+HTML_ELEMENT_IMG_SRC+EQUAL+DOUBLE_UPPER+url+DOUBLE_UPPER;//add src
                      else{//replace src
                        ind2=r_str.indexOf(SPACE,ind1);
                        if(ind2==-1)ind2=r_str.length();
                        //html code from file->img_src.html
                        r_str=r_str.replaceFirst(r_str.substring(ind1,ind2),HTML_ELEMENT_IMG_SRC+EQUAL+DOUBLE_UPPER+url+DOUBLE_UPPER);
                      }
                    }
                    all_str+=CODE_OPEN+r_str+CODE_CLOSE+NEXT_LINE;
                    i++;
                  }
                  str=all_str;
                }
                else str=CODE_OPEN+str+CODE_CLOSE;//other type
              }//else tag==null
            }//if sql
            else str=CODE_OPEN+str+CODE_CLOSE;//sql==null
            v.clear();v=null;//clear all fields vector
            //add pages only to tags(th,td,textarea,table,sql)
            if(page_param.sqlNumber==sp.sqlIndex&&page_param.pageCount>0&&page_param.rowCount>0&&total_row_count>page_param.rowCount){
              String pages;
              if(tag!=null){//<th><td><textarea><table> up part of pages(up,updown option)
                if(tag.data!=null){
                  tag.sqlIndex=sp.sqlIndex;tag.totalRowCount=total_row_count;
                  pages=this.getPages(sp,page_param.pageNumber,page_param.pageCount,page_param.rowCount,total_row_count,param_not_quot);
                  tag.pages=pages;
                  if(page_param.pageType==0||page_param.pageType==2)str=pages+str;//up,updown
                  total_row_count=-1;
                }
              }
              else{//<sql> for output stream write all(pages and data)
                pages=this.getPages(sp,page_param.pageNumber,page_param.pageCount,page_param.rowCount,total_row_count,param_not_quot);
                if(page_param.pageType==0)str=pages+str;//up
                else if(page_param.pageType==1)str=str+pages;//down
                else if(page_param.pageType==2)str=pages+str+pages;//updown
                total_row_count=-1;
              }
            }
            if(str.length()>0)outStream.write(str.getBytes(manager.getLocalCodepage()));//->local codepage
            str=EMPTY;percent=false;
            }//if comment start
            }//if htmlCode
            else{//all other case
              outStream.write((CODE_OPEN+str+CODE_CLOSE).getBytes(manager.getLocalCodepage()));//->local codepage
              str=EMPTY;percent=false;
            }
            }//if !comment
            else{//comment
              outStream.write((CODE_OPEN+str+CODE_CLOSE).getBytes(manager.getLocalCodepage()));//->local codepage
              if(str.endsWith(HTML_ELEMENT_COMMENT_FINISH))comment=false;
              str=EMPTY;percent=false;
            }
          }//switch
          break;
        case 2://open close </...
          switch(status){
          case 1://data between </...<
          case 2://data between </...</
            outStream.write((CODE_OPEN+SLASH+str).getBytes(manager.getLocalCodepage()));//->local codepage
            str=EMPTY;percent=false;break;
          case 3://data between </...>
            if(!comment){
            if(htmlCode){//if htmlCode
            //process data, sql not found
            String pages=null;
            //if(manager.isDebug())manager.getLog().write(sessionID,DEBUG_PARSE_ATTRIBUTES,str);
            Vector v=this.getFields_(str);//v->all fields of tag
            //if(manager.isDebug())manager.getLog().write(sessionID,DEBUG_PARSE_ATTRIBUTE_VALUE,v.toString());
            type=this.getType(v);
            if((type==HTML_SUBTYPE_TH||/*parse tag for some <tag>...</tag> html objects*/
              type==HTML_SUBTYPE_TD||
              type==HTML_SUBTYPE_OPTION||
              type==HTML_SUBTYPE_TR||
              type==HTML_TYPE_TEXTAREA||
              type==HTML_TYPE_SELECT||/*type==HTML_TYPE_DROPDOWN_LIST||type==HTML_TYPE_LISTBOX||*/
              type==HTML_TYPE_TABLE)&&tag_ind<tag_data.size()&&tag_ind>-1){
                tag=(Tag)tag_data.get(tag_ind);
                if(type==tag.type&&tag.data!=null){
                  if(page_param.sqlNumber==tag.sqlIndex&&page_param.pageCount>0&&page_param.rowCount>0&&tag.totalRowCount>page_param.rowCount){
                    pages=tag.pages;/*total_row_count=-1;*/tag.sqlIndex=-1;tag.totalRowCount=-1;
                  }
                  if(tag.data.length()>0)outStream.write(tag.data.getBytes(manager.getLocalCodepage()));//->local codepage
                  tag.data=null;tag.pages=null;
                }
                tag_data.remove(tag_ind);tag_ind--;tag_data.trimToSize();//kill tag of any type
            }
            v.clear();v=null;//clear all fields vector
            outStream.write((CODE_OPEN+SLASH+str+CODE_CLOSE).getBytes(manager.getLocalCodepage()));//->local codepage
            //down part of pages(down,updown option)
            if(pages!=null){if(page_param.pageType==1||page_param.pageType==2)outStream.write(pages.getBytes(manager.getLocalCodepage()));}//->local codepage
            str=EMPTY;percent=false;
            }//if htmlCode
            else{//all other case
              outStream.write((CODE_OPEN+SLASH+str+CODE_CLOSE).getBytes(manager.getLocalCodepage()));//->local codepage
              if(str.startsWith(HTML_ELEMENT_SCRIPT))htmlCode=true;//if </script>
              str=EMPTY;percent=false;
            }
            }//if !comment
            else{//comment
              outStream.write((CODE_OPEN+SLASH+str+CODE_CLOSE).getBytes(manager.getLocalCodepage()));//->local codepage
              if(str.endsWith(HTML_ELEMENT_COMMENT_FINISH))comment=false;
              str=EMPTY;percent=false;
            }
          }//switch
          break;
        case 3://close >...
          switch(status){
          case 1://data between >...<
          case 2://data between >...</
            outStream.write(str.getBytes(manager.getLocalCodepage()));//->local codepage
            str=EMPTY;percent=false;break;
          case 3://data between >...>
            outStream.write((str+CODE_CLOSE).getBytes(manager.getLocalCodepage()));//->local codepage
            if(comment&&str.endsWith(HTML_ELEMENT_COMMENT_FINISH))comment=false;
            str=EMPTY;percent=false;
          }//switch
        }//switch last_status
        last_status=status;
        is_tag=false;
      }//if is_tag|status
      index++;
    }//while
    data_left=null;
    str=str.trim();
    //write data after ...
    if(str.length()>0)outStream.write(str.getBytes(manager.getLocalCodepage()));//->local codepage
    outStream.flush();
    }catch(IOException io_e){}
    data=null;data_left=null;
    tag_data.clear();tag_data=null;//clear all tags data
    page_param=null;
  }
  //------------------------------public----------------------------------------
  public ServletParam getServletParam(){return servletParam;}
  public String getParam(int number)
  {
    String str;
    if(number<servletParam.paramList.size()){
      Object object=servletParam.paramList.get(number);
      if(object!=null&&object instanceof byte[]){
        str=new /*sun.misc.*/BASE64Encoder().encode((byte[])object);
        return str;
      }
      else if(object!=null&&object instanceof String)return (String)object;
    }
    return null;
  }
  public String getParamType(int number)//return "string"||"buffer"
  {
    if(number<servletParam.paramTypeList.size()){
      return(String)servletParam.paramTypeList.get(number);
    }
    return null;
  }
  public String getParamSize(int number)
  {
    byte[] data;
    String str;
    if(number<servletParam.paramList.size()){
      Object object=servletParam.paramList.get(number);
      if(object!=null&&object instanceof byte[]){data=(byte[])object;return Integer.toString(data.length);}
      else if(object!=null&&object instanceof String){str=(String)object;return Integer.toString(str.length());}
    }
    return null;
  }
  /*
  public String getParamName(int number)
  {
    BufferParam buf_param=null;
    if(number<ServletParam.BufferParamList.size())buf_param=(BufferParam)servletParam.BufferParamList.get(number);
    if(buf_param!=null)return buf_param.name;else return EMPTY;
  }
  public String getParamFilename(int number)
  {
    BufferParam buf_param=null;
    if(number<ServletParam.BufferParamList.size())buf_param=(BufferParam)servletParam.BufferParamList.get(number);
    if(buf_param!=null)return buf_param.filename;else return EMPTY;
  }
  public String getParamContentType(int number)
  {
    BufferParam buf_param=null;
    if(number<ServletParam.BufferParamList.size())buf_param=(BufferParam)servletParam.BufferParamList.get(number);
    if(buf_param!=null)return buf_param.contentType;else return EMPTY;
  }*/
  public void setParam(int number,String value)
  {
    if(number>=servletParam.paramList.size())servletParam.paramList.setSize(number+1);
    servletParam.paramList.setElementAt(value,number);
    if(number>=servletParam.paramTypeList.size())servletParam.paramTypeList.setSize(number+1);
    servletParam.paramTypeList.setElementAt(SERVICE_PARAM_TYPE_STRING,number);
  }
  //setParamFromFile==setParamFromTrashFile
  public String setParamFromFile(int number,String filepath)//read file from service_trash
  {
    return this.setParam(number,filepath,2);
  }
  public String setParamFromServiceFile(int number,String filepath)//read file from service_files
  {
    return this.setParam(number,filepath,1);
  }
  public String setParamFromTrashFile(int number,String filepath)//read file from service_trash
  {
    return this.setParam(number,filepath,2);
  }
  public String setParam(int number,String filepath,int file_type)//read file from file_type 1-service_files 2-service_trash
  {
    String sessionID_filename,filename=filepath;
    String str=null;
    if(file_type==1)str=manager.getInitial().getServiceFiles();//service_files dir
    else if(file_type==2)str=manager.getInitial().getServiceTrash();//service_trash dir
    if(str==null){
      str=manager.getServletFilepath();
      if(file_type==1)str+=FILEPATH_SERVICE_FILES;//service_files dir
      else if(file_type==2)str+=FILEPATH_SERVICE_TRASH;//service_trash dir
    }else if(!str.endsWith(LOCAL_DELIM_2))str+=LOCAL_DELIM_2;
    int index=filepath.lastIndexOf(LOCAL_DELIM);
    if(index!=-1){// ...path/sessionId.filename.ext
      sessionID_filename=filepath.substring(++index);
      index=sessionID_filename.indexOf(POINT);
      if(index!=-1)filename=sessionID_filename.substring(++index);
    }
    else sessionID_filename=filepath;//filename
    str+=sessionID_filename;
    if(number>=servletParam.paramList.size())servletParam.paramList.setSize(number+1);
    servletParam.paramList.setElementAt(Convert.readByteArrayFromFile(str),number);
    if(number>=servletParam.paramTypeList.size())servletParam.paramTypeList.setSize(number+1);
    servletParam.paramTypeList.setElementAt(SERVICE_PARAM_TYPE_BUFFER,number);
    //Manager.getLog().write("FromFile_setBytes(param)#"+number);
    return filename;
  }
  public void saveAsFile(int number,String filename)//save param by number to outStream(filename is full filepath)
  {
    if(number<servletParam.paramList.size()){
      Object object=servletParam.paramList.get(number);
      if(object!=null&&object instanceof byte[])Convert.writeToFile(filename,(byte[])object);
      else if(object!=null&&object instanceof String){
        Convert.writeToFile(filename,((String)object).getBytes());
      }
    }
  }
  //removeFile==RemoveTrashFile
  public void removeFile(String filename)
  {
    this.removeFile(filename,2);
  }
  public void removeServiceFile(String filename)
  {
    this.removeFile(filename,1);
  }
  public void removeTrashFile(String filename)
  {
    this.removeFile(filename,2);
  }
  public void removeFile(String filename,int file_type)//remove file by file_type 1-service_files 2-service_trash
  {
    String str=null;
    if(file_type==1)str=manager.getInitial().getServiceFiles();//service_files dir
    else if(file_type==2)str=manager.getInitial().getServiceTrash();//service_trash dir
    if(str==null){
      str=manager.getServletFilepath();
      if(file_type==1)str+=FILEPATH_SERVICE_FILES;//service_files dir
      else if(file_type==2)str+=FILEPATH_SERVICE_TRASH;//service_trash dir
    }else if(!str.endsWith(LOCAL_DELIM_2))str+=LOCAL_DELIM_2;
    str+=filename;
    File file=new File(str);
    if(file.exists()&&file.isFile())file.delete();
    file=null;
  }
  public String getServiceFilesFilepath()
  {
    return this.getFilepath(1);
  }
  public String getServiceTrashFilepath()
  {
    return this.getFilepath(2);
  }
  public String getFilepath(int file_type)//filepath to: 1-service_files 2-service_trash
  {
    String str=null;
    if(file_type==1)str=manager.getInitial().getServiceFiles();//service_files dir
    else if(file_type==2)str=manager.getInitial().getServiceTrash();//service_trash dir
    if(str==null){
      str=manager.getServletFilepath();
      if(file_type==1)str+=FILEPATH_SERVICE_FILES;//service_files dir
      else if(file_type==2)str+=FILEPATH_SERVICE_TRASH;//service_trash dir
    }else if(!str.endsWith(LOCAL_DELIM_2))str+=LOCAL_DELIM_2;
    return str;
  }
  //public methods for invoke_method in service_template
  //such as %invoke_methodthis.<public method>(<args>)%invoke_method
  //%param#%param subst in str
  public String paramSubst(String str)//%param#%param substitutions
  {
    String ret_val=str;
    int percent_index,last_percent_index;
    while((percent_index=str.indexOf(Interface.PERCENT_PARAM))!=-1){//%param1%param
      if((last_percent_index=str.indexOf(Interface.PERCENT_PARAM,percent_index+6))!=-1){
        String substr=(String)servletParam.paramList.get(Convert.toIntValue(str.substring(percent_index+6,last_percent_index).trim()));
        ret_val=str.substring(0,percent_index)+substr+str.substring(last_percent_index+6);
      }
    }
    return ret_val;
  }
  //get_cookie
  public String getCookie(String name)
  {
    return this.getHideCookie(name,null);
  }
  //get_hide_cookie
  public String getHideCookie(String name,String keyword)
  {
    String ret_val=null;
    try{
    Cookie[] cookies=servletParam.request.getCookies();
    Cookie cookie;
    if(cookies!=null){
      int cookies_size=cookies.length;
      for(int i=0;i<cookies_size;i++){
        cookie=cookies[i];
        if(cookie!=null&&cookie.getName().equals(name)){//equalsIgnoreCase not work on email address->("test@ukr.net")
          if(keyword==null||keyword.length()==0)ret_val=cookie.getValue();
          else{
            byte[] dec_data=new /*sun.misc.*/BASE64Decoder().decodeBuffer(cookie.getValue());//BASE64->replace by native code
            ret_val=new String(clientSession.encodeData(keyword,dec_data));
          }
        }
      }
    }
    //cookie not found, seek cookie in servlet param
    if(ret_val==null){
      if((keyword==null||keyword.length()==0)&&servletParam.cookieName!=null)ret_val=servletParam.cookieValue;
      else if(keyword!=null&&keyword.length()>0&&servletParam.hideCookieName!=null)ret_val=servletParam.hideCookieValue;
    }
    }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString());}
    return ret_val;
  }
  public String getHideCookie(String name)
  {
    return this.getHideCookie(name,
            (servletParam.hideCookieKeyword!=null&&servletParam.hideCookieKeyword.length()>0)?
             servletParam.hideCookieKeyword:manager.HIDE_COOKIE_KEYWORD);
  }
  public String getCookiesList()
  {
    String ret_val=EMPTY;
    try{
    Cookie[] cookies=servletParam.request.getCookies();
    Cookie cookie;
    if(cookies!=null){
      ret_val="{";
      int cookies_size=cookies.length;
      for(int i=0;i<cookies_size;i++){
        cookie=cookies[i];
        if(cookie!=null){
          ret_val+="\""+cookie.getName()+"\"=\""+cookie.getValue()+"\"";
          if(i<cookies_size-1)ret_val+=",";
        }
      }
      ret_val+="}";
    }
    }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString());}
    return ret_val;
  }
  //set_cookie
  //Warning! Set cookies on the top of the service_template (first string)
  //%invoke_methodthis.setCookie("TEST","test","3600")%invoke_method
  public void setCookie(String name,String value,String timeout)
  {
    this.setHideCookie(name,value,timeout,null);
  }
  public void setHideCookie(String name,String value,String timeout,String keyword)
  {
    try{
    String coo_val;
    if(keyword==null||keyword.length()==0)coo_val=value;
    else{
      String enc_data=new String(clientSession.encodeData(keyword,value.getBytes()));
      coo_val=new /*sun.misc.*/BASE64Encoder().encode(enc_data);//BASE64->replace by native code
    }
    Cookie cookie=new Cookie(name,coo_val);
    cookie.setMaxAge(Convert.toIntValue(timeout));//set to browser cookie_timeout (in sec)
    servletParam.response.addCookie(cookie);
    }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString());}
  }
  public void setHideCookie(String name,String value,String timeout)
  {
    this.setHideCookie(name,value,timeout,
            (servletParam.hideCookieKeyword!=null&&servletParam.hideCookieKeyword.length()>0)?
             servletParam.hideCookieKeyword:manager.HIDE_COOKIE_KEYWORD);
  }
  //tools
  public Manager getManager(){return manager;}
  public String getStringValue(String value)
  {
    servletParam.sqlIndex++;servletParam.sql=value;
    return database.getStringValue(sessionID,servletParam);
  }
  public String getBytesValueAsBase64String(String value,String substr)
  {
    String ret_val=EMPTY,base64string;
    servletParam.sqlIndex++;servletParam.sql=value;
    base64string=database.getBytesValueAsBase64String(sessionID,servletParam);
    if(base64string!=null){
      if(substr!=null&&substr.length()>0)ret_val=String.format(substr,base64string);
      else ret_val=base64string;
    }
    return ret_val;
  }
  public String getBytesAsBase64String(String value,String substr)
  {
    String ret_val=EMPTY,base64string;
    servletParam.sqlIndex++;servletParam.sql=value;
    base64string=database.getBytesAsBase64String(sessionID,servletParam);
    if(base64string!=null){
      if(substr!=null&&substr.length()>0)ret_val=String.format(substr,base64string);
      else ret_val=base64string;
    }
    return ret_val;
  }
  public String getStringList(String value)//2S
  {
    String ret_val=EMPTY;
    StringListItem item;
    servletParam.sqlIndex++;servletParam.sql=value;
    Vector list=database.getStringList(sessionID,servletParam,1);
    Enumeration e=list.elements();
    while(e.hasMoreElements()){
      item=(StringListItem)e.nextElement();
      ret_val+=item.id;
    }
    return ret_val;
  }
  public String getExtraList()
  {
    return servletParam.extraList.toString();
  }
  public String getExtra(String name)
  {
    return (String)servletParam.extraList.get(name);
  }
  public String getExtraAsString(String name)
  {
    return (String)servletParam.extraList.get(name);
  }
  public byte[] getExtraAsBytes(String name)
  {
    return (byte[])servletParam.extraList.get(name);
  }
  //------------------------------private---------------------------------------
  //page parameters parse (rowcount,page...) and substitutions
  //param="rowcount=10;pagecount=10; ... "
  private void pageParamSubst(String param)
  {
    String str;
    StringTokenizer st=new StringTokenizer(param,POINT_COMA);//=Convert.getValues(param,POINT_COMA);
    while(st.hasMoreTokens()){
      str=st.nextToken().trim();
      if(str.startsWith(SERVICE_ROW_COUNT)){servletParam.rowCount=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_ONCLICK)){servletParam.pageOnclick=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_HREF)){servletParam.pageHref=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_COUNT)){servletParam.pageCount=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_NUMBER)){if(servletParam.pageNumber==null)servletParam.pageNumber=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_STYLE)){servletParam.pageStyle=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_TYPE)){servletParam.pageType=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_MARKER)){if(servletParam.pageMarker==null)servletParam.pageMarker=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_PARAM)){servletParam.pageParam=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_TITLE)){servletParam.pageTitle=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_CLASS)){servletParam.pageClass=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_CURRENT_CLASS)){servletParam.pageCurrentClass=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_ANCHOR)){servletParam.pageAnchor=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_PREV)){servletParam.pagePrev=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_PAGE_NEXT)){servletParam.pageNext=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_SQL_NUMBER)){servletParam.sqlNumber=Convert.getValue(str);}
      else if(str.startsWith(SERVICE_SQL_IGNORE)){servletParam.sqlIgnore=Convert.getValue(str);}
    }
  }
  //java.lang.String.replaceAll(str1,str2).toString(); -> (two and more numbers methods called)
  private int getIndex(String str,char code,int start_index)
  {
    int ret_val=-1,size=str.length();
    boolean text=false;
    if(start_index==-1)return ret_val;
    for(int i=start_index;i<size;i++){
      if(str.charAt(i)==CODE_DOUBLE_UPPER){
        if(!text)text=true;else text=false;//text or no text
      }
      else if(str.charAt(i)==code){
        if(!text){ret_val=i;break;}
      }
    }
    return ret_val;
  }
  //last index of <code> with (...) ignore: ->method(arg1,arg2,(java.lang.Object)arg3, ...)
  private int getLastIndex(String str,char code,int start_index)
  {
    int ret_val=-1,size=str.length(),count=0;
    boolean text=false;
    if(start_index==-1)return ret_val;
    for(int i=start_index;i<size;i++){
      if(!text&&count==0&&str.charAt(i)==code){ret_val=i;break;}
      else if(str.charAt(i)==CODE_DOUBLE_UPPER){
        if(!text)text=true;else text=false;//text or no text
      }
      else if(str.charAt(i)==CODE_OPEN_){if(!text)count++;}
      else if(str.charAt(i)==CODE_CLOSE_){if(!text&&count>0)count--;}
    }
    return ret_val;
  }
  //get args vector with (...) ignore
  private Vector getArgs(String args_name)//(arg1,arg2,(java.lang.Object)arg3, ...)
  {
    Vector ret_val;
    String str=null;
    ret_val=new Vector();
    int size=args_name.length(),index=0,count=0;
    boolean text=false;
    //Manager.getLog().write("args name="+args_name+" size="+size+"\r\n");
    for(int i=0;i<size;i++){
      if(args_name.charAt(i)==CODE_DOUBLE_UPPER){
        if(!text)text=true;else text=false;//text or no text
      }
      else if(args_name.charAt(i)==CODE_OPEN_){if(!text)count++;}
      else if(args_name.charAt(i)==CODE_CLOSE_){if(!text&&count>0)count--;}
      else if(args_name.charAt(i)==CODE_COMA){
        if(!text&&count==0){ret_val.add(args_name.substring(index,i).trim());index=i+1;}
      }
    }
    if(index<size)str=args_name.substring(index).trim();
    if(str!=null&&str.length()>0)ret_val.add(str);//from index add one last arg
    return ret_val;
  }
  //invoke_method scrypt:
  //1."class1.classN.method(args)"
  //2."this.method(args)" this is a pointer of the htmlResponse class object (public methods invoke used)
  //3."this.class1.classN.method(args)"
  //4."this.class1.classN.field"
  private Object invoke_method(String s)
  {
    Object o=null;
    try{
      Field f;
      Class c;
      Object o_c;//Object of Class c;
      String class_name,method_name,args_name,str0=s.trim(),str1=str0,str2=null;
      int index1,index2,index3;
      index2=str1.indexOf(CODE_OPEN_);
      //Manager.getLog().write("getLastIndex(args)="+str1+","+CODE_CLOSE_+","+index2+"\r\n");
      index3=this.getLastIndex(str1,CODE_CLOSE_,index2+1);
      //Manager.getLog().write("getLastIndex()="+index3+"\r\n");
      if(index2==-1||index3==-1){//4."this.class1.classN.field"
        if(str1.startsWith(THIS))str1=str1.substring(5);
        /*sub-class parsing class1...classN*/
        c=this.getClass();o_c=this;
        StringTokenizer st=new StringTokenizer(str1,POINT);
        while(st.hasMoreTokens()){//class & class object seek
          f=c.getDeclaredField(st.nextToken().trim());
          o=f.get(o_c);
          //for next step parsing(class1...classN)
          c=f.getType();o_c=o;
        }
        st=null;
        return o;
      }
      if(index3+1<=str1.length()){
        str0=str1.substring(0,index2).trim();//string without ()
        str2=str1.substring(index3+1).trim();//string after ()
        str1=str1.substring(0,index3+1).trim();//string with ()
      }
      index1=str0.lastIndexOf(CODE_POINT);
      if(index1==-1)class_name=EMPTY;//without "this" as class_name(this.getCookie(<cookie_name>) as getCookie(<cookie_name>))
      else class_name=str1.substring(0,index1);
      //Manager.getLog().write("class for name="+class_name+"\r\n");
      //seek if "this" (object) declared ...
      if(class_name.equals(EMPTY)){//<method_name>(<args>)
        c=this.getClass();o_c=this;
      }
      else if(class_name.startsWith(THIS)){//this.<method_name>(<args>) || this.<class_name1>.<class_nameN>.<method_name>(<args>)
        if(class_name.equals(THIS)){//2."this.method(args)"
          c=this.getClass();o_c=this;
        }
        else{//3."this.class1.classN.method(args)"
          /*sub-class parsing class1...classN*/
          c=this.getClass();o_c=this;
          StringTokenizer st=new StringTokenizer(class_name.substring(5),POINT);
          while(st.hasMoreTokens()){//class & class object seek
            f=c.getDeclaredField(st.nextToken().trim());
            o=f.get(o_c);
            //for next step parsing(class1...classN)
            c=f.getType();o_c=o;
          }
          st=null;
        }
      }
      else{//1."class1.classN.method(args)"
        c=Class.forName(class_name);
        //Manager.getLog().write("class name="+c.getName()+"\r\n");
        //Manager.getLog().write("class name string="+c.toString()+"\r\n");
        o_c=c.newInstance();
        //Manager.getLog().write("class instance="+o_c.toString()+"\r\n");
        //Manager.getLog().write("next substring="+str2+"\r\n");
      }
      do{
        method_name=str1.substring(index1+1,index2);
        //Manager.getLog().write("method name="+method_name+"\r\n");
        args_name=str1.substring(index2+1,index3);
        //Manager.getLog().write("args name="+args_name+"\r\n");
        o=this.invoke(args_name,method_name,c,o_c);//to execute declared_method with args
        if(o==null||str2==null)break;
        //Manager.getLog().write("object="+o.toString()+"\r\n");
        //Manager.getLog().write("object class name="+o.getClass().getName()+"\r\n");
        //Manager.getLog().write("object class name string="+o.getClass().toString()+"\r\n");
        c=o.getClass();
        o_c=o;
        str1=str2;
        index2=str1.indexOf(CODE_OPEN_);
        //Manager.getLog().write("point1\r\n");
        //index3=str1.indexOf(CODE_CLOSE_,index2);
        index3=this.getLastIndex(str1,CODE_CLOSE_,index2+1);
        //Manager.getLog().write("point2\r\n");
        str2=null;
        if(index2==-1||index3==-1)return o==null?EMPTY:o;
        if(index3+1<=str1.length()){
          //Manager.getLog().write("point3\r\n");
          str0=str1.substring(0,index2).trim();//string without ()
          //Manager.getLog().write("point4\r\n");
          str2=str1.substring(index3+1).trim();//string after ()
          //Manager.getLog().write("point5\r\n");
          str1=str1.substring(0,index3+1).trim();//string with ()
        }
        //Manager.getLog().write("point6\r\n");
        index1=str0.lastIndexOf(CODE_POINT);
        //Manager.getLog().write("next substring="+str2+"\r\n");
      }while(true);
    }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString());/*manager.getLog().write("invoke_method exception="+e.toString()+"\r\n");*/}
    return o==null?EMPTY:o;
  }
  private Object invoke(String args_name,String method_name,Class c,Object o)
  {
    Object ret_val=null;
    try{
      Class arg_type[];
      Object arg_value[];
      String token,class_name;
      Vector args=this.getArgs(args_name);
      int token_index,index,size=args.size();
      arg_type=new Class[size];
      arg_value=new Object[size];
      //Manager.getLog().write("args size="+size+"\r\n");
      for(token_index=0;token_index<size;token_index++){
        token=(String)args.elementAt(token_index);
        //Manager.getLog().write("args ["+token_index+"]="+token+"\r\n");
        if(token.startsWith(DOUBLE_UPPER)){token=token.replaceAll(DOUBLE_UPPER,EMPTY);arg_type[token_index]=java.lang.String.class;arg_value[token_index]=token;}//String
        else if(token.startsWith(UPPER)){token=token.replaceAll(UPPER,EMPTY);arg_type[token_index]=char.class;arg_value[token_index]=token.charAt(0);}//char
        else if(token.equals(TRUE)||token.equals(FALSE)){arg_type[token_index]=boolean.class;arg_value[token_index]=java.lang.Boolean.parseBoolean(token);}//boolean
        else if(token.indexOf(POINT)!=-1){
          Class object_class=null;
          index=-1;
          if(token.startsWith(OPEN)){//object converter-> (java.lang.Object)...
            index=this.getIndex(token,CODE_CLOSE_,1);
            if(index!=-1){
              class_name=token.substring(1,index);//remove ( & )
              object_class=Class.forName(class_name);
            }
          }
          token=token.substring(index+1);
          if(token.startsWith(DOUBLE_UPPER)){//string "..."
            token=token.replaceAll(DOUBLE_UPPER,EMPTY);//remove " & "
            arg_type[token_index]=object_class;arg_value[token_index]=token;//Object
          }
          else if(token.indexOf(OPEN)!=-1){arg_type[token_index]=java.lang.Object.class;arg_value[token_index]=this.invoke_method(token);}//recursive call
          else{arg_type[token_index]=float.class;arg_value[token_index]=java.lang.Float.parseFloat(token);}//float
        }
        else{arg_type[token_index]=int.class;arg_value[token_index]=java.lang.Integer.parseInt(token);}//int
      }
      ret_val=c.getDeclaredMethod(method_name,arg_type).invoke(o,arg_value);
      arg_type=null;arg_value=null;c=null;
    }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString());/*manager.getLog().write("invoke exception="+e.toString()+"\r\n");*/}
    return ret_val;
  }
  /*
  1.if scrypt boolean conditions:
  %if "(SELECT name FROM table WHERE id=1)" %if (execute sql query)
  %if "(CALL ora_function())" %if (execute oracle function)
  2.if reserved words(login,password,database,param#) without percent symbol(%) using:
  %if "login='system'" %if
  %if "param1>1" %if
  3.if using reserved operations symbols:
  for numbers and strings->
  '=' equals values
  '!=' '<>' not equals values
  only for numbers->
  '>' more then
  '<' less then
  '>=' more or equals
  '<=' less or equals
  */
  /*
  <!---IF SCRYPT DEMO--->
  <!---execute: CALL get_name(), SELECT get_name() FROM DUAL--->

  BEFORE IF<br> %if (CALL get_name()) %if AFTER IF<br>

  SUCCESS: EXECUTED FUNCTION FROM PAGE CONTEXT !<br>

  BEFORE ELSEIF<br> %elseif%elseif AFTER ELSEIF<br>

  ERROR: EXECUTE FAILED FROM PAGE CONTEXT !<br>

  BEFORE ENDIF<br> %endif%endif AFTER ENDIF<br>

  <!---IF SCRYPT DEMO--->
  */
  //Warning! If uses sql scrypt as operand use (...)
  //Warning! If uses specific symbols (<>) use " ... "
  //(such as %if "(SELECT number FROM table)>0" %if)
  private class operand
  {
    String value=null;
    int index=0;
  }
  private operand get_operand(long session_id,ServletParam sp,String str,operand o)
  {
    int size,count,i;
    char ch;
    if(str.startsWith(OPEN)){//plsql
      //search plsql close
      size=str.length();
      count=1;//open count
      for(i=1;i<size;i++){
        if(str.charAt(i)==CODE_OPEN_)count++;
        else if(str.charAt(i)==CODE_CLOSE_)count--;
        if(count==0)break;
      }
      str=str.substring(1,i);
      sp.sqlIndex++;sp.sql=str;//for %if_%if sql index incrementing
      str=database.executePLSQL(session_id,sp);
    }
    else if(str.startsWith(UPPER)){//const string
      //search string end
      size=str.length();
      for(i=1;i<size;i++){
        if(str.charAt(i)==CODE_UPPER)break;
      }
      str=str.substring(1,i);
    }
    else if(str.startsWith(SERVICE_LOGIN)){str=sp.login;i=5;}
    else if(str.startsWith(SERVICE_PASSWORD)){str=sp.password;i=8;}
    else if(str.startsWith(SERVICE_DATABASE)){str=sp.database;i=8;}
    else if(str.startsWith(SERVICE_PARAM)){
      //search param number end
      size=str.length();
      for(i=5;i<size;i++){
        ch=str.charAt(i);
        if(ch<'0'||ch>'9')break;//not in [30..39]->'0'..'9'
      }
      str=(String)sp.paramList.get(Convert.toIntValue(str.substring(5,i).trim()));
    }
    else{//const string or number
      //search string or number end
      size=str.length();
      for(i=0;i<size;i++){
        if(str.charAt(i)==CODE_EXCLAIM)break;//!
        else if(str.charAt(i)==CODE_MORE)break;//>
        else if(str.charAt(i)==CODE_LESS)break;//<
        else if(str.charAt(i)==CODE_EQUAL)break;//=
      }
      str=str.substring(0,i);
    }
    o.value=str;o.index=i;
    return o;
  }
  private boolean if_scrypt(long session_id,ServletParam sp,String str)
  {
    boolean ret_val=false;
    String left_operand=null,right_operand=null,sub_str=null,s;
    int index,size,str_size,type=0;//0-unknown 1-'=' 2-'>' 3-'<' 4-'>=' 5-'<=' 6-'!=' '<>'
    int dupper_index=-1,last_dupper_index=-1;
    operand o=null;
    //allways OR between positions in "..1.." "..2.." "..3.."
    //example: "(SELECT id FROM table WHERE id=1)=1" "param0='get_name'" "login='user'"
    //example: "(SELECT system.main() FROM DUAL)>0" || "param0>0" || "login='user'"
    //example: "(SELECT system.main() FROM DUAL)>0" | "param0>0&login='user'"
    //WARNING! must use allways DOUBLE_UPPER if specific tags present (such as: '<','>')
    str_size=str.length();
    while(dupper_index<str_size-1&&!ret_val/*this is a OR condition*/){
      dupper_index=str.indexOf(DOUBLE_UPPER,dupper_index+1);
      last_dupper_index=str.indexOf(DOUBLE_UPPER,dupper_index+1);
      //Manager.getLog().write("dupper index="+dupper_index+" last duper="+last_dupper_index+"\r\n");
      if(sub_str==null&&dupper_index==-1){//str without double upper
        sub_str=str;dupper_index=str_size;
      }
      else{//str with double upper
        if(dupper_index==-1||dupper_index>=str_size-1||last_dupper_index==-1)break;
        sub_str=str.substring(dupper_index+1,last_dupper_index).trim();
        dupper_index=last_dupper_index;
      }
      //Manager.getLog().write("sub_str="+sub_str+"\r\n");
      StringTokenizer st=new StringTokenizer(sub_str,AND/*"&"*/);//and operator between operands (" ... & ... ")
      while(st.hasMoreTokens()){//seek parts of str
        s=st.nextToken().trim();
        //Manager.getLog().write("sub_str(s)="+s+"\r\n");
        //operand search
        o=new operand();
        //Manager.getLog().write("EXPRESSION="+s+"\r\n");
        this.get_operand(session_id,sp,s,o);
        left_operand=o.value;index=o.index;
        //Manager.getLog().write("LEFT_OPERAND="+o.value+"\r\n");
        //operation type search
        size=s.length();
        //Manager.getLog().write("INDEX="+index+"\r\n");
        for(;index<size;index++){
          if(index<size-1){
            if(s.charAt(index)==CODE_EQUAL){type=1;s=s.substring(index+1);break;}
            else if(s.charAt(index)==CODE_MORE&&s.charAt(index+1)!=CODE_EQUAL){type=2;s=s.substring(index+1);break;}
            else if(s.charAt(index)==CODE_LESS&&s.charAt(index+1)!=CODE_EQUAL&&s.charAt(index+1)!=CODE_MORE){type=3;s=s.substring(index+1);break;}
            else if(index<size-2){
              if(s.charAt(index)==CODE_MORE&&s.charAt(index+1)==CODE_EQUAL){type=4;s=s.substring(index+2);break;}
              if(s.charAt(index)==CODE_LESS&&s.charAt(index+1)==CODE_EQUAL){type=5;s=s.substring(index+2);break;}
              if(s.charAt(index)==CODE_EXCLAIM&&s.charAt(index+1)==CODE_EQUAL){type=6;s=s.substring(index+2);break;}
              if(s.charAt(index)==CODE_LESS&&s.charAt(index+1)==CODE_MORE){type=6;s=s.substring(index+2);break;}
            }//if size-2
          }//if size-1
        }//for
        //Manager.getLog().write("OPERATION_TYPE="+type+"\r\n");
        //Manager.getLog().write("EXPRESSION="+s+"\r\n");
        if(type>0&&type<=6){//operand search
          this.get_operand(session_id,sp,s.trim(),o);
          //Manager.getLog().write("RIGTH_OPERAND="+o.value+"\r\n");
          right_operand=o.value;o=null;
        }
        ret_val=false;
        //compare operands
        if(type==0){// ...
          if(left_operand!=null&&left_operand.length()>0)ret_val=true;
        }
        else if(type==1){//=
          if(left_operand!=null&&right_operand!=null&&left_operand.equalsIgnoreCase(right_operand))ret_val=true;
        }
        else if(type==6){//!=
          if(left_operand!=null&&right_operand!=null&&!left_operand.equalsIgnoreCase(right_operand))ret_val=true;
        }
        else if(type>1&&type<=5){//others
          try{
            int left=Convert.toIntValue(left_operand);
            int rigth=Convert.toIntValue(right_operand);
            if(type==2)ret_val=left>rigth;//>
            else if(type==3)ret_val=left<rigth;//<
            else if(type==4)ret_val=left>=rigth;//>=
            else if(type==5)ret_val=left<=rigth;//<=
          }catch(Exception e){}
        }
        o=null;
        if(!ret_val)break;//conditions false
      }//while
      st=null;
    }//while
    return ret_val;
  }
  private boolean is_encoded(String value)
  {
    boolean ret_val=false,latin=true/*all latin symbols,no(!)plus,minus,down_umpersand*//*,plus=false*/;
    byte[] b=value.getBytes();
    int size=b.length;
    for(int i=0;i<size;i++){
      if((b[i]>=0x41&&b[i]<=0x5A)||(b[i]>=0x61&&b[i]<=0x7A)||b[i]==0x2D||b[i]==5F||(b[i]>=0x30&&b[i]<=0x39));//'a..z'||'A..Z'||'-'||'_' 0..9
      else{/*if(b[i]==0x2B)plus=true;else*/ latin=false;}//plus is fake in rowid actions: may be(AAABBBCCC+abc)
      if(b[i]=='%'&&i+2<size){
        if((b[i+1]>=0x30&&b[i+1]<=0x39)||(b[i+1]>=0x41&&b[i+1]<=0x46))
          if((b[i+2]>=0x30&&b[i+2]<=0x39)||(b[i+2]>=0x41&&b[i+2]<=0x46)){ret_val=true;break;}
      }
    }
    if(!ret_val&&latin/*&&plus*/)ret_val=true;
    return ret_val;
  }
  //not_quot - not replace symbols quots
  //encode() calling from getPages() (page url <a href="..."> encoding), getTableColumns (column <a href="..."> encoding)
  private String encode(String str,boolean not_quot)//encode url string (with html specifiers encoding->QUOT,APOS,LT,GT->",',<,>)
  {
    int index=0;
    //Manager.getLog().write("START_ENCODED="+str+"\r\n");
    String s=str,ret_val=str,pv,param,value;
    boolean is_quest=s.contains(QUEST);//url?query
    if(is_quest)s=Convert.getValue(s,QUEST);//s=query
    if(s!=null&&s.length()>0){
      //replace specific to normal
      s=s.replaceAll(QUOT,DOUBLE_UPPER).replaceAll(APOS,UPPER).replaceAll(LT,OPEN_TAG).replaceAll(GT,CLOSE_TAG);
      Vector v=Convert.getValues(s,REQUEST_DELIM);
      if(is_quest)ret_val=Convert.getParam(str,QUEST)+QUEST;//ret_val=url?
      else ret_val=EMPTY;//ret_val=''
      for(int i=0;i<v.size();i++){
        pv=(String)v.get(i);
        if(pv!=null&&pv.length()>0){
          param=Convert.getParam(pv);
          value=Convert.getValue(pv);
          if(value!=null&&value.length()>0){
            if(!is_encoded(value)){//replace normal to specifiers (before encode)
              if(!not_quot)value=value.replaceAll(DOUBLE_UPPER,QUOT).replaceAll(UPPER,APOS).replaceAll(OPEN_TAG,LT).replaceAll(CLOSE_TAG,GT);
              value=URLEncoder.encode(value);//URL encode
            }
          }else value=EMPTY;
          if(index==0)ret_val+=param+EQUAL+value;
          else ret_val+=REQUEST_DELIM+param+EQUAL+value;
          index++;
        }
      }
      v.clear();v=null;
      //Manager.getLog().write("FINISH_ENCODED="+ret_val+"\r\n");
    }
    return ret_val;
  }
  private String replaceMacro(String str,ServletParam sp)//replace macro (%login,%password,%database) & param
  {
    int percent_index,last_percent_index;
    //login substitutions (%login%login)
    if((percent_index=str.indexOf(Interface.PERCENT_LOGIN))!=-1){//%login%login
      if((last_percent_index=str.indexOf(Interface.PERCENT_LOGIN,percent_index+6))!=-1){
        str=str.substring(0,percent_index)+sp.login+str.substring(last_percent_index+6);
      }
    }
    //password substitutions (%password%password)
    if((percent_index=str.indexOf(Interface.PERCENT_PASSWORD))!=-1){//%password%password
      if((last_percent_index=str.indexOf(Interface.PERCENT_PASSWORD,percent_index+9))!=-1){
        str=str.substring(0,percent_index)+sp.password+str.substring(last_percent_index+9);
      }
    }
    //database substitutions (%database%database)
    if((percent_index=str.indexOf(Interface.PERCENT_DATABASE))!=-1){//%database%database
      if((last_percent_index=str.indexOf(Interface.PERCENT_DATABASE,percent_index+9))!=-1){
        str=str.substring(0,percent_index)+sp.database+str.substring(last_percent_index+9);
      }
    }
    //param# substitutions by number (%param<number>%param)
    while(true){
      if((percent_index=str.indexOf(Interface.PERCENT_PARAM))!=-1){//%param1%param
        if((last_percent_index=str.indexOf(Interface.PERCENT_PARAM,percent_index+6))!=-1){
          String substr=(String)sp.paramList.get(Convert.toIntValue(str.substring(percent_index+6,last_percent_index).trim()));
          //always encode for a href
          //Manager.getLog().write("REPLACE_MACRO(param#"+str.substring(percent_index+6,last_percent_index).trim()+")="+substr+"\r\n");
          if(!is_encoded(substr))substr=URLEncoder.encode(substr);//URL encode
          str=str.substring(0,percent_index)+substr+str.substring(last_percent_index+6);
        }
      }
      else break;
    }//while percent param presents
    return str;
  }
  private String saveAsImageFile(byte[] data,String name)//synchronized ?
  {
    return saveAsFile(data,name,EXTENSION_FILE_JPG,FILEPATH_SERVICE_TRASH);
  }
  private String saveAsFile(byte[] data,String name,String extension,String path)//synchronized ?
  {
    //filename format: <SessionID_BufferNumber>
    String str,filename,filepath,url;
    if(name==null||name.length()==0){
      filename=Convert.toString(sessionID)+DOWN+Convert.toString(++bufferNumber);
      if(extension!=null&&extension.length()>0)filename+=POINT+extension.trim();
    }else filename=name.trim();
    str=manager.getInitial().getServiceTrash();//ini service trash dir
    if(str!=null&&!str.endsWith(LOCAL_DELIM_2))str+=LOCAL_DELIM_2;
    filepath=(str!=null?str:manager.getServletFilepath()+path)+filename;
    url=((str!=null)?clientSession.toURL(str):URL_SERVICE_TRASH)+filename;//URLEncoder.encode(filename);//URL encode
    if(data!=null){
      //Convert.writeToFile(filepath,data);
      FileOutputStream file=null;
      try{
        file=new FileOutputStream(filepath);
        file.write(data);
        if(file!=null){file.close();file=null;}
      }catch(IOException io_e){manager.getLog().write(sessionID,WARNING_DATA_NOT_FOUND,io_e.toString());}
    }
    return url;
  }
  private String removeTemplate(String data,String template)
  {
    String ret_val=EMPTY,str,str2;
    StringTokenizer st=new StringTokenizer(data,SPACE);
    while(st.hasMoreTokens()){
      str=st.nextToken().trim();
      if(!str.startsWith(template)){
        if(ret_val.length()>0)ret_val+=SPACE;
        ret_val+=str;
      }
      else{
        int i=str.indexOf(CODE_EQUAL);//seek "="
        if(i==-1);
        else if(str.charAt(++i)==CODE_DOUBLE_UPPER){
          while(!str.endsWith(DOUBLE_UPPER)&&st.hasMoreTokens()){str2=st.nextToken().trim();str+=SPACE+str2;}
        }
      }
    }
    return ret_val;
  }
  private String getGridData(Vector list,String file,String ref,ServletParam sp)
  {
    String str=EMPTY,sub_str,s=EMPTY,url,title=EMPTY,filename;
    int index;
    Vector sub_list;
    GridDataItem gdi;
    for(int i=0;i<list.size();i++){
      sub_list=(Vector)list.elementAt(i);
      for(int j=0;j<sub_list.size();j++){
        //grid start
        gdi=(GridDataItem)sub_list.elementAt(j);
        sub_str=EMPTY;
        if(gdi.type==ITEM_TYPE_STRING){
          s=gdi.stringValue;
          if(ref!=null&&ref.startsWith(HTML_ELEMENT_A)&&/*a href reference and url from database*/
          j+1<sub_list.size()&&((GridDataItem)sub_list.elementAt(j+1)).type==ITEM_TYPE_STRING){//next value is url
            url=((GridDataItem)sub_list.elementAt(j+1)).stringValue;
            url=this.replaceMacro(url,sp);//replace macro (%login,%password,%database)
            if(j+2<sub_list.size()&&((GridDataItem)sub_list.elementAt(j+2)).type==ITEM_TYPE_STRING){//2next value is title
              title=((GridDataItem)sub_list.elementAt(j+2)).stringValue;j++;}else title=EMPTY;
            //html code from file->a_href.html
            sub_str+=CODE_OPEN+ref/*HTML_ELEMENT_A*/+SPACE+HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER+url+DOUBLE_UPPER+
            //@title->title.html
            (title.length()>0?SPACE+HTML_ELEMENT_A_TITLE+EQUAL+DOUBLE_UPPER+title+DOUBLE_UPPER:EMPTY)+CODE_CLOSE;//<a href="...">
            sub_str+=(s.length()>0?s:EMPTY)+CODE_OPEN+SLASH+HTML_ELEMENT_A+CODE_CLOSE;//...</a>
            if(file!=null)sub_str+=file;//"file" <a>...</a> delim
            j++;
          }
          if(ref!=null&&ref.equals(PERCENT))sub_str=this.replaceMacro(s,sp)+SPACE;//replace macro (%login,%password,%database)
          else if(file==null)sub_str=s+SPACE;
        }
        else if(gdi.type==ITEM_TYPE_BYTES){//reference to bytes data (a,form,img) and url to saved file
          if(file!=null){//file->(file_list: filename1;filename2; ... filenameN;)
            index=file.indexOf(POINT_COMA);
            if(index!=-1){//filename="filename1" file="filename2; ... filenameN;"
              filename=file.substring(0,index);
              if(index+1<file.length())file=file.substring(index+1);
              else file=EMPTY;
            }else filename=file;
            if(gdi.bytesValue!=null&&gdi.bytesValue.length>0)url=this.saveAsFile(gdi.bytesValue,null,filename,FILEPATH_SERVICE_TRASH);
            else url=EMPTY;
            // -> ...
            if(ref.startsWith(HTML_ELEMENT_A)){
              sub_str=CODE_OPEN+ref/*HTML_ELEMENT_A*/+SPACE+HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER+url+DOUBLE_UPPER+CODE_CLOSE;//<a href="...">
              sub_str+=(s.length()>0?s:EMPTY)+CODE_OPEN+SLASH+HTML_ELEMENT_A+CODE_CLOSE;//...</a>
            }
            else if(ref.startsWith(HTML_ELEMENT_FORM)){
              sub_str=CODE_OPEN+ref/*HTML_ELEMENT_FORM*/+SPACE+HTML_ELEMENT_FORM_ACTION+EQUAL+
              DOUBLE_UPPER+url+DOUBLE_UPPER+SPACE+HTML_ELEMENT_FORM_METHOD+EQUAL+
              DOUBLE_UPPER+HTML_ELEMENT_FORM_METHOD_GET+DOUBLE_UPPER+CODE_CLOSE;//<form action="..." method="get">
              sub_str+=CODE_OPEN+HTML_ELEMENT_INPUT+SPACE+HTML_ELEMENT_INPUT_TYPE+EQUAL+
              DOUBLE_UPPER+HTML_ELEMENT_INPUT_TYPE_SUBMIT+DOUBLE_UPPER+SPACE+HTML_ELEMENT_INPUT_VALUE+EQUAL+
              DOUBLE_UPPER+(s.length()>0?s:EMPTY)+DOUBLE_UPPER+CODE_CLOSE;//<input type="submit" value="...">
              sub_str+=CODE_OPEN+SLASH+HTML_ELEMENT_FORM+CODE_CLOSE;//</form>
            }
            else if(ref.startsWith(HTML_ELEMENT_IMG)){
              sub_str=CODE_OPEN+HTML_ELEMENT_A+SPACE+HTML_ELEMENT_A_HREF+EQUAL+
              DOUBLE_UPPER+(s.length()>0?s:EMPTY)+DOUBLE_UPPER+CODE_CLOSE;//<a href="...">...
              sub_str+=(url.length()>0?CODE_OPEN+ref/*HTML_ELEMENT_IMG*/+SPACE+HTML_ELEMENT_IMG_SRC+EQUAL+DOUBLE_UPPER+url+DOUBLE_UPPER+SPACE+//...<img src="...">
              HTML_ELEMENT_IMG_ALT+EQUAL+DOUBLE_UPPER+DOUBLE_UPPER+CODE_CLOSE:s)+
              CODE_OPEN+SLASH+HTML_ELEMENT_A+CODE_CLOSE;//...</a>
            }
          }//if file!=null
          else{//image file *.jpg
            if(gdi.bytesValue!=null&&gdi.bytesValue.length>0){//file buffer length>0
              url=this.saveAsImageFile(gdi.bytesValue,null);
              sub_str=CODE_OPEN+HTML_ELEMENT_IMG+SPACE+HTML_ELEMENT_IMG_SRC+EQUAL+DOUBLE_UPPER+url+DOUBLE_UPPER+SPACE+
              HTML_ELEMENT_IMG_ALT+EQUAL+DOUBLE_UPPER+DOUBLE_UPPER+CODE_CLOSE;
            }
          }
        }
        str+=sub_str;
        //grid end
      }
      str+=NEXT_LINE;
    }
    return str;
  }
  private String getTableColumns(Vector list,String cols,ServletParam sp,boolean not_quot)
  {
    String str=EMPTY,sub_str=EMPTY,url;
    Vector sub_list;
    GridDataItem gdi;
    Vector table_cols=new Vector();
    StringTokenizer st=new StringTokenizer(cols,POINT_COMA);//=Convert.getValues(cols,POINT_COMA);
    while(st.hasMoreTokens())table_cols.add(st.nextToken().trim());
    String table_col;
    StringListItem item;
    Vector table_select=new Vector(),v;
    for(int i=0;i<table_cols.size();i++){
      table_col=(String)table_cols.get(i);
      if(table_col.startsWith(SQL_SELECT)||table_col.startsWith(SQL_SELECT_UPPER_CASE)){
        /*sp.sqlIndex;not increments[pages clears]*/sp.sql=table_col;
        table_select.add(database.getStringList(sessionID,sp,2));//get select sql data
      }
      else table_select.add(null);
    }
    if(list.size()==0){//empty first table row
      str+=CODE_OPEN+HTML_ELEMENT_TABLE_TR+SPACE+HTML_ELEMENT_TABLE_VALIGN+EQUAL+DOUBLE_UPPER+HTML_ELEMENT_TABLE_VALIGN_CENTER+DOUBLE_UPPER+CODE_CLOSE+NEXT_LINE;
      //<td colspan="0">&nbsp;</td>
      str+=CODE_OPEN+HTML_ELEMENT_TABLE_TD+SPACE+HTML_ELEMENT_TABLE_COLSPAN+EQUAL+DOUBLE_UPPER+table_cols.size()+DOUBLE_UPPER+CODE_CLOSE;
      str+=NBSP;
      str+=CODE_OPEN+SLASH+HTML_ELEMENT_TABLE_TD+CODE_CLOSE;
      str+=CODE_OPEN+SLASH+HTML_ELEMENT_TABLE_TR+CODE_CLOSE+NEXT_LINE;
    }
    else for(int i=0;i<list.size();i++){
      str+=CODE_OPEN+HTML_ELEMENT_TABLE_TR+SPACE+HTML_ELEMENT_TABLE_VALIGN+EQUAL+DOUBLE_UPPER+HTML_ELEMENT_TABLE_VALIGN_CENTER+DOUBLE_UPPER+CODE_CLOSE+NEXT_LINE;
      sub_list=(Vector)list.elementAt(i);
      for(int j=0;j<sub_list.size();j++){
        sub_str=CODE_OPEN+HTML_ELEMENT_TABLE_TD+CODE_CLOSE;
        if(j<table_cols.size())table_col=(String)table_cols.get(j);else table_col=EMPTY;
        //Manager.getLog().write("TABLE_COL="+table_col+"\r\n");
        if(table_col.length()==0)sub_str+=(String)sub_list.elementAt(j);
        else if(table_col.startsWith(HTML_ELEMENT_SELECT)){//<select>...</select> 2 cols
          gdi=(GridDataItem)sub_list.elementAt(j);//1 col
          if(gdi.type==ITEM_TYPE_STRING){
            String s=gdi.stringValue,s_gdi=EMPTY;
            //Manager.getLog().write("SELECT_S="+s+"\r\n");
            v=(Vector)table_select.get(j);
            if(j+1<sub_list.size()){
              GridDataItem gdi_temp=(GridDataItem)sub_list.elementAt(j+1);//2 col
              if(gdi_temp.type==ITEM_TYPE_STRING)s_gdi=gdi_temp.stringValue;
              j=j+1;
            }
            //html code from file->select.html
            sub_str+=CODE_OPEN+/*table_col*/HTML_ELEMENT_SELECT+SPACE+HTML_ELEMENT_ID+EQUAL+DOUBLE_UPPER+HTML_ELEMENT_SELECT+s_gdi+DOUBLE_UPPER+CODE_CLOSE;//<select id="selectId">
            for(int z=0;z<v.size();z++){
              item=(StringListItem)v.elementAt(z);
              //html code from file->select_option.html
              sub_str+=CODE_OPEN+HTML_ELEMENT_SELECT_OPTION+SPACE;//<option_
              //Manager.getLog().write("SELECT_ITEM_ID="+item.id+"\r\n");
              //@selected->selected.html
              if(item.id.equalsIgnoreCase(s))sub_str+=HTML_ELEMENT_SELECT_OPTION_SELECTED+SPACE;
              sub_str+=HTML_ELEMENT_SELECT_OPTION_VALUE+EQUAL+DOUBLE_UPPER+item.id+DOUBLE_UPPER+CODE_CLOSE+item.value;
              sub_str+=CODE_OPEN+SLASH+HTML_ELEMENT_SELECT_OPTION+CODE_CLOSE+NEXT_LINE;//</option>;
            }
            sub_str+=CODE_OPEN+SLASH+HTML_ELEMENT_SELECT+CODE_CLOSE;//</select>
          }
        }
        else if(table_col.startsWith(HTML_ELEMENT_INPUT_TYPE_TEXT)){//<input type="text" value="...">
          //grid start
          gdi=(GridDataItem)sub_list.elementAt(j);
          if(gdi.type==ITEM_TYPE_STRING){//<input type="text" value="...">
            sub_str+=CODE_OPEN+HTML_ELEMENT_INPUT+SPACE+HTML_ELEMENT_INPUT_TYPE+EQUAL+
            DOUBLE_UPPER+HTML_ELEMENT_INPUT_TYPE_TEXT+DOUBLE_UPPER+SPACE+
            HTML_ELEMENT_INPUT_VALUE+EQUAL+DOUBLE_UPPER+gdi.stringValue+DOUBLE_UPPER+SPACE+
            HTML_ELEMENT_INPUT_SIZE+EQUAL+DOUBLE_UPPER+gdi.stringValue.length()+DOUBLE_UPPER+CODE_CLOSE;
          }
          else if(gdi.type==ITEM_TYPE_BYTES){//<img src="...">
            if(gdi.bytesValue!=null&&gdi.bytesValue.length>0){//file buffer length>0
              url=this.saveAsImageFile(gdi.bytesValue,null);
              sub_str+=CODE_OPEN+HTML_ELEMENT_IMG+SPACE+HTML_ELEMENT_IMG_SRC+EQUAL+DOUBLE_UPPER+url+DOUBLE_UPPER+SPACE+
              HTML_ELEMENT_IMG_ALT+EQUAL+DOUBLE_UPPER+DOUBLE_UPPER+CODE_CLOSE;
            }
          }
          //grid end
        }
        else if(table_col.startsWith(HTML_ELEMENT_INPUT_TYPE_CHECKBOX)){//<input type="checkbox" value="..." checked>
          gdi=(GridDataItem)sub_list.elementAt(j);
          if(gdi.type==ITEM_TYPE_STRING){//if value!="" checked
            String s=gdi.stringValue;
            sub_str+=CODE_OPEN+HTML_ELEMENT_INPUT+SPACE+HTML_ELEMENT_INPUT_TYPE+EQUAL+
            DOUBLE_UPPER+HTML_ELEMENT_INPUT_TYPE_CHECKBOX+DOUBLE_UPPER+SPACE+
            HTML_ELEMENT_INPUT_VALUE+EQUAL+DOUBLE_UPPER+s+DOUBLE_UPPER+SPACE+
            ((s.equals(EMPTY))?EMPTY:HTML_ELEMENT_INPUT_TYPE_CHECKBOX_CHECKED)+CODE_CLOSE;
          }
        }
        else if(table_col.startsWith(HTML_ELEMENT_INPUT_TYPE_IMAGE)){//<input type="image">
          //grid start
          gdi=(GridDataItem)sub_list.elementAt(j);
          if(gdi.type==ITEM_TYPE_STRING)sub_str+=gdi.stringValue;
          else if(gdi.type==ITEM_TYPE_BYTES){
            if(gdi.bytesValue!=null&&gdi.bytesValue.length>0){//file buffer length>0
              url=this.saveAsImageFile(gdi.bytesValue,null);
              sub_str+=CODE_OPEN+HTML_ELEMENT_INPUT+SPACE+HTML_ELEMENT_INPUT_TYPE+EQUAL+DOUBLE_UPPER+HTML_ELEMENT_INPUT_TYPE_IMAGE+DOUBLE_UPPER+SPACE+
              HTML_ELEMENT_IMG_SRC+EQUAL+DOUBLE_UPPER+url+DOUBLE_UPPER+CODE_CLOSE;
            }
          }
        }
        else if(table_col.startsWith(HTML_ELEMENT_IMG)){//<img src="...">
          //grid start
          gdi=(GridDataItem)sub_list.elementAt(j);
          if(gdi.type==ITEM_TYPE_STRING)sub_str+=gdi.stringValue;
          else if(gdi.type==ITEM_TYPE_BYTES){
            if(gdi.bytesValue!=null&&gdi.bytesValue.length>0){//file buffer length>0
              url=this.saveAsImageFile(gdi.bytesValue,null);
              sub_str+=CODE_OPEN+table_col/*HTML_ELEMENT_IMG*/+SPACE+HTML_ELEMENT_IMG_SRC+EQUAL+DOUBLE_UPPER+url+DOUBLE_UPPER+SPACE+
              HTML_ELEMENT_IMG_ALT+EQUAL+DOUBLE_UPPER+DOUBLE_UPPER+CODE_CLOSE;
            }
          }
          //grid end
        }
        else if(table_col.startsWith(HTML_ELEMENT_A)){//<a href="...">...</a> 2 cols
          //grid start
          gdi=(GridDataItem)sub_list.elementAt(j);//1 col
          gdi.stringValue=this.replaceMacro(gdi.stringValue,sp);//replace macro (%login,%password,%database)
          sub_str+=CODE_OPEN+table_col/*HTML_ELEMENT_A*/+SPACE+HTML_ELEMENT_A_HREF+EQUAL+
          DOUBLE_UPPER+(gdi.stringValue.startsWith(HTML_ELEMENT_JAVASCRIPT)?gdi.stringValue:this.encode(gdi.stringValue,not_quot)/*URL encode*/)+DOUBLE_UPPER+CODE_CLOSE;
          if(j+1<sub_list.size()){//size control
            gdi=(GridDataItem)sub_list.elementAt(j+1);//2 col
            if(gdi.type==ITEM_TYPE_STRING)sub_str+=gdi.stringValue;
            else if(gdi.type==ITEM_TYPE_BYTES){
              if(gdi.bytesValue!=null&&gdi.bytesValue.length>0){//file buffer length>0
                url=this.saveAsImageFile(gdi.bytesValue,null);
                sub_str+=CODE_OPEN+HTML_ELEMENT_IMG+SPACE+HTML_ELEMENT_IMG_SRC+EQUAL+DOUBLE_UPPER+url+DOUBLE_UPPER+SPACE+
                HTML_ELEMENT_IMG_ALT+EQUAL+DOUBLE_UPPER+DOUBLE_UPPER+CODE_CLOSE;
              }
            }
            j=j+1;
          }
          sub_str+=CODE_OPEN+SLASH+HTML_ELEMENT_A+CODE_CLOSE;
          //grid end
        }
        else if(table_col.startsWith(HTML_ELEMENT_FORM)){//<form ...> ... </form> 3 cols
          //grid start
          gdi=(GridDataItem)sub_list.elementAt(j);//1 col
          String ext=gdi.stringValue;//may be a full path
          if(ext.startsWith(HTML_ELEMENT_JAVASCRIPT)||ext.contains(POINT)){//</form> 2 cols (action="javascript:"||action=path)
            sub_str+=CODE_OPEN+table_col/*HTML_ELEMENT_FORM*/+SPACE+HTML_ELEMENT_FORM_ACTION+EQUAL+
            DOUBLE_UPPER+ext+DOUBLE_UPPER+SPACE+HTML_ELEMENT_FORM_METHOD+EQUAL+
            DOUBLE_UPPER+HTML_ELEMENT_FORM_METHOD_GET+DOUBLE_UPPER+CODE_CLOSE;
            if(j+1<sub_list.size()){
              gdi=(GridDataItem)sub_list.elementAt(j+1);//2 col
              sub_str+=CODE_OPEN+HTML_ELEMENT_INPUT+SPACE+HTML_ELEMENT_INPUT_TYPE+EQUAL+
              DOUBLE_UPPER+HTML_ELEMENT_INPUT_TYPE_SUBMIT+DOUBLE_UPPER+SPACE+HTML_ELEMENT_INPUT_VALUE+EQUAL+
              DOUBLE_UPPER+gdi.stringValue+DOUBLE_UPPER+CODE_CLOSE;//<input type="submit" value="...">
            }
            j=j+1;
          }
          else if(j+2<sub_list.size()){//size control
            gdi=(GridDataItem)sub_list.elementAt(j+2);//3 col
            if(gdi.type==ITEM_TYPE_BYTES){//<form action="..." method="get">
              if(gdi.bytesValue!=null&&gdi.bytesValue.length>0)url=this.saveAsFile(gdi.bytesValue,null,ext,FILEPATH_SERVICE_TRASH);
              else url=EMPTY;
              sub_str+=CODE_OPEN+table_col/*HTML_ELEMENT_FORM*/+SPACE+HTML_ELEMENT_FORM_ACTION+EQUAL+
              DOUBLE_UPPER+url+DOUBLE_UPPER+SPACE+HTML_ELEMENT_FORM_METHOD+EQUAL+
              DOUBLE_UPPER+HTML_ELEMENT_FORM_METHOD_GET+DOUBLE_UPPER+CODE_CLOSE;
            }
            gdi=(GridDataItem)sub_list.elementAt(j+1);//2 col
            sub_str+=CODE_OPEN+HTML_ELEMENT_INPUT+SPACE+HTML_ELEMENT_INPUT_TYPE+EQUAL+
            DOUBLE_UPPER+HTML_ELEMENT_INPUT_TYPE_SUBMIT+DOUBLE_UPPER+SPACE+HTML_ELEMENT_INPUT_VALUE+EQUAL+
            DOUBLE_UPPER+gdi.stringValue+DOUBLE_UPPER+CODE_CLOSE;//<input type="submit" value="...">
            j=j+2;
          }
          sub_str+=CODE_OPEN+SLASH+HTML_ELEMENT_FORM+CODE_CLOSE;//</form>
          //grid end
        }
        else{
          //grid start
          gdi=(GridDataItem)sub_list.elementAt(j);
          if(gdi.type==ITEM_TYPE_STRING){
            if(table_col.equals(PERCENT))sub_str+=this.replaceMacro(gdi.stringValue,sp);//replace macro (%login,%password,%database)
            else sub_str+=gdi.stringValue;
          }
          else if(gdi.type==ITEM_TYPE_BYTES){
            if(gdi.bytesValue!=null&&gdi.bytesValue.length>0){//file buffer length>0
              url=this.saveAsImageFile(gdi.bytesValue,null);
              sub_str+=CODE_OPEN+HTML_ELEMENT_IMG+SPACE+HTML_ELEMENT_IMG_SRC+EQUAL+DOUBLE_UPPER+url+DOUBLE_UPPER+SPACE+
              HTML_ELEMENT_IMG_ALT+EQUAL+DOUBLE_UPPER+DOUBLE_UPPER+CODE_CLOSE;
            }
          }
          //grid end
        }
        sub_str+=CODE_OPEN+SLASH+HTML_ELEMENT_TABLE_TD+CODE_CLOSE;
        str+=sub_str;
      }
      str+=CODE_OPEN+SLASH+HTML_ELEMENT_TABLE_TR+CODE_CLOSE+NEXT_LINE;
    }
    table_cols.clear();table_cols=null;
    table_select.clear();table_select=null;
    return str;
  }
  private String getTableColumns(Vector list,int col_count)
  {
    String str=EMPTY,sub_str=EMPTY,url;
    Vector sub_list;
    GridDataItem gdi;
    if(list.size()==0){//empty first table row
      str+=CODE_OPEN+HTML_ELEMENT_TABLE_TR+SPACE+HTML_ELEMENT_TABLE_VALIGN+EQUAL+DOUBLE_UPPER+HTML_ELEMENT_TABLE_VALIGN_CENTER+DOUBLE_UPPER+CODE_CLOSE+NEXT_LINE;
      //<td colspan="0">&nbsp;</td>
      str+=CODE_OPEN+HTML_ELEMENT_TABLE_TD+SPACE+HTML_ELEMENT_TABLE_COLSPAN+EQUAL+DOUBLE_UPPER+col_count+DOUBLE_UPPER+CODE_CLOSE;
      str+=NBSP;
      str+=CODE_OPEN+SLASH+HTML_ELEMENT_TABLE_TD+CODE_CLOSE;
      str+=CODE_OPEN+SLASH+HTML_ELEMENT_TABLE_TR+CODE_CLOSE+NEXT_LINE;
    }
    else for(int i=0;i<list.size();i++){
      str+=CODE_OPEN+HTML_ELEMENT_TABLE_TR+SPACE+HTML_ELEMENT_TABLE_VALIGN+EQUAL+DOUBLE_UPPER+HTML_ELEMENT_TABLE_VALIGN_CENTER+DOUBLE_UPPER+CODE_CLOSE+NEXT_LINE;
      sub_list=(Vector)list.elementAt(i);
      for(int j=0;j<sub_list.size();j++){
        sub_str=CODE_OPEN+HTML_ELEMENT_TABLE_TD+CODE_CLOSE;
        //grid start
        gdi=(GridDataItem)sub_list.elementAt(j);
        if(gdi.type==ITEM_TYPE_STRING)sub_str+=gdi.stringValue;
        else if(gdi.type==ITEM_TYPE_BYTES){
          if(gdi.bytesValue!=null&&gdi.bytesValue.length>0){//file buffer length>0
            url=this.saveAsImageFile(gdi.bytesValue,null);
            sub_str+=CODE_OPEN+HTML_ELEMENT_IMG+SPACE+HTML_ELEMENT_IMG_SRC+EQUAL+DOUBLE_UPPER+url+DOUBLE_UPPER+SPACE+
            HTML_ELEMENT_IMG_ALT+EQUAL+DOUBLE_UPPER+DOUBLE_UPPER+CODE_CLOSE;
          }
        }
        //grid end
        sub_str+=CODE_OPEN+SLASH+HTML_ELEMENT_TABLE_TD+CODE_CLOSE;
        str+=sub_str;
      }
      str+=CODE_OPEN+SLASH+HTML_ELEMENT_TABLE_TR+CODE_CLOSE+NEXT_LINE;
    }
    return str;
  }
  //QueryString->"name=value&&pagemarker=1&&pagenumber=1" || queryString->"name=value&pagemarker=1&pagenumber=1"
  private String removeFromQueryString(String query_str,String remove_str)//remove string with "&&" or "&"
  {
    int index_1=query_str.indexOf(remove_str+EQUAL),index_2;
    if(index_1>-1){
      int query_length=query_str.length();
      index_2=query_str.indexOf(AND,index_1);//"&"
      if(index_2==-1){//remove_str on query_str end
        while(index_1>0&&query_str.charAt(--index_1)=='&');//move index_1 to begin and left '&'
        query_str=query_str.substring(0,index_1+1);//remove
      }
      else{ //remove_str on middle of query_str
        index_2++;
        while(index_2<query_length&&query_str.charAt(index_2)=='&')index_2++;//move index_2 to end and left '&'
        query_str=(index_1>0?query_str.substring(0,index_1):EMPTY)+(index_2<query_length?query_str.substring(index_2):EMPTY);
      }
    }
    return query_str;
  }
  private String getPages(ServletParam sp,int page_number,int page_count,int row_count,int total_row_count,boolean not_quot)
  {
    String ret_val=EMPTY;
    String query_str=this.encode(sp.queryString,not_quot)/*URL encode*/;
    String page_func=sp.pageOnclick;//if use->onClick
    String page_href=sp.pageHref;//if use function->set href=pageHref
    String page_prev=sp.pagePrev,page_next=sp.pageNext;//prev&next folder text
    boolean is_marker=false,is_title=false,is_class=false,is_current_class=false,is_anchor=false;
    int page_marker=-1,page_start_number,count;
    if(page_number>0)query_str=this.removeFromQueryString(query_str,SERVICE_PAGE_NUMBER);//remove 'pagenumber' if exists
    else page_number=1;
    try{if(sp.pageMarker!=null&&sp.pageMarker.length()>0){is_marker=true;page_marker=Convert.toIntValue(sp.pageMarker);}}catch(Exception e){}
    if(sp.pageTitle!=null&&sp.pageTitle.length()>0)is_title=true;
    if(sp.pageClass!=null&&sp.pageClass.length()>0)is_class=true;//if true->VERT_SLASH left
    if(sp.pageCurrentClass!=null&&sp.pageCurrentClass.length()>0)is_current_class=true;
    if(sp.pageAnchor!=null&&sp.pageAnchor.length()>0)is_anchor=true;
    if(is_marker){
      query_str=this.removeFromQueryString(query_str,SERVICE_PAGE_MARKER);//remove 'pagemarker' if exists
      if(page_marker<=0)page_marker=1;
      page_start_number=page_marker;
    }
    else page_start_number=page_number;
    query_str=this.removeFromQueryString(query_str,SERVICE_SQL);//remove 'sql' if exist
    int total_pages_count=total_row_count/row_count+(total_row_count%row_count>0?1:0),last_page_number=/*!*/page_start_number+page_count-1;
    ret_val+=(is_current_class?EMPTY:VERT_SLASH)+SPACE;
    if(/*!*/page_start_number>1){//set link prev pages
      int page_index=/*!*/page_start_number-page_count;
      if(page_index<1)page_index=1;
      ret_val+=CODE_OPEN+HTML_ELEMENT_A+SPACE+(sp.pageParam!=null?sp.pageParam+SPACE:EMPTY)+
      (page_func!=null?HTML_ELEMENT_A_ONCLICK+EQUAL+DOUBLE_UPPER+page_func+OPEN+UPPER:HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER)+
      sp.requestURL+REQUEST_SYMBOL+query_str+REQUEST_DELIM+
      (is_marker?SERVICE_PAGE_MARKER+EQUAL+Convert.toString(page_index)+REQUEST_DELIM:EMPTY)+
      SERVICE_PAGE_NUMBER+EQUAL+Convert.toString(page_index)+
      (is_anchor?REQUEST_ANCHOR+sp.pageAnchor:EMPTY)+
      (page_func!=null?UPPER+CLOSE+DOUBLE_UPPER+SPACE+HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER+(page_href!=null?page_href:EMPTY)+DOUBLE_UPPER:DOUBLE_UPPER)+
      (is_class?SPACE+HTML_ELEMENT_A_CLASS+EQUAL+DOUBLE_UPPER+sp.pageClass+DOUBLE_UPPER:EMPTY)+
      (is_title?SPACE+HTML_ELEMENT_A_TITLE+EQUAL+DOUBLE_UPPER+sp.pageTitle+DOUBLE_UPPER:EMPTY)+CODE_CLOSE+
      (page_prev!=null?page_prev:PREV_2_LT)+CODE_OPEN+SLASH+HTML_ELEMENT_A+CODE_CLOSE+SPACE;
    }
    //STYLE Numeric: | << 1 2 >> |
    if(sp.pageStyle==null||sp.pageStyle.equalsIgnoreCase(SERVICE_PAGE_STYLE_NUMERIC)){
      for(int i=/*!*/page_start_number;i<page_number;i++){//link pages
        ret_val+=CODE_OPEN+HTML_ELEMENT_A+SPACE+(sp.pageParam!=null?sp.pageParam+SPACE:EMPTY)+
        (page_func!=null?HTML_ELEMENT_A_ONCLICK+EQUAL+DOUBLE_UPPER+page_func+OPEN+UPPER:HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER)+
        sp.requestURL+REQUEST_SYMBOL+query_str+REQUEST_DELIM+
        (is_marker?SERVICE_PAGE_MARKER+EQUAL+Convert.toString(page_start_number)+REQUEST_DELIM:EMPTY)+
        SERVICE_PAGE_NUMBER+EQUAL+Convert.toString(i)+
        (is_anchor?REQUEST_ANCHOR+sp.pageAnchor:EMPTY)+
        (page_func!=null?UPPER+CLOSE+DOUBLE_UPPER+SPACE+HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER+(page_href!=null?page_href:EMPTY)+DOUBLE_UPPER:DOUBLE_UPPER)+
        (is_class?SPACE+HTML_ELEMENT_A_CLASS+EQUAL+DOUBLE_UPPER+sp.pageClass+DOUBLE_UPPER:EMPTY)+
        (is_title?SPACE+HTML_ELEMENT_A_TITLE+EQUAL+DOUBLE_UPPER+sp.pageTitle+DOUBLE_UPPER:EMPTY)+CODE_CLOSE+
        Convert.toString(i)+CODE_OPEN+SLASH+HTML_ELEMENT_A+CODE_CLOSE+SPACE;
      }
      ret_val+=(is_current_class?CODE_OPEN+HTML_ELEMENT_FONT+SPACE+HTML_ELEMENT_FONT_CLASS+EQUAL+DOUBLE_UPPER+sp.pageCurrentClass+DOUBLE_UPPER+CODE_CLOSE+Convert.toString(page_number)+CODE_OPEN+SLASH+HTML_ELEMENT_FONT+CODE_CLOSE:
                Convert.toString(page_number))+SPACE;//view pages
      int num=last_page_number-total_pages_count>0?total_pages_count:last_page_number;
      for(int i=(page_number+1);i<=num;i++){//link pages
        ret_val+=CODE_OPEN+HTML_ELEMENT_A+SPACE+(sp.pageParam!=null?sp.pageParam+SPACE:EMPTY)+
        (page_func!=null?HTML_ELEMENT_A_ONCLICK+EQUAL+DOUBLE_UPPER+page_func+OPEN+UPPER:HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER)+
        sp.requestURL+REQUEST_SYMBOL+query_str+REQUEST_DELIM+
        (is_marker?SERVICE_PAGE_MARKER+EQUAL+Convert.toString(page_start_number)+REQUEST_DELIM:EMPTY)+
        SERVICE_PAGE_NUMBER+EQUAL+Convert.toString(i)+
        (is_anchor?REQUEST_ANCHOR+sp.pageAnchor:EMPTY)+
        (page_func!=null?UPPER+CLOSE+DOUBLE_UPPER+SPACE+HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER+(page_href!=null?page_href:EMPTY)+DOUBLE_UPPER:DOUBLE_UPPER)+
        (is_class?SPACE+HTML_ELEMENT_A_CLASS+EQUAL+DOUBLE_UPPER+sp.pageClass+DOUBLE_UPPER:EMPTY)+
        (is_title?SPACE+HTML_ELEMENT_A_TITLE+EQUAL+DOUBLE_UPPER+sp.pageTitle+DOUBLE_UPPER:EMPTY)+CODE_CLOSE+
        Convert.toString(i)+CODE_OPEN+SLASH+HTML_ELEMENT_A+CODE_CLOSE+SPACE;
      }
    }
    //STYLE Secuence: | << 1..100 101..200 >> |
    else if(sp.pageStyle.equalsIgnoreCase(SERVICE_PAGE_STYLE_SEQUENCE)){
      int val,last_val;
      for(int i=/*!*/page_start_number;i<page_number;i++){//link pages
        val=i*row_count;
        ret_val+=CODE_OPEN+HTML_ELEMENT_A+SPACE+(sp.pageParam!=null?sp.pageParam+SPACE:EMPTY)+
        (page_func!=null?HTML_ELEMENT_A_ONCLICK+EQUAL+DOUBLE_UPPER+page_func+OPEN+UPPER:HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER)+
        sp.requestURL+REQUEST_SYMBOL+query_str+REQUEST_DELIM+
        (is_marker?SERVICE_PAGE_MARKER+EQUAL+Convert.toString(page_start_number)+REQUEST_DELIM:EMPTY)+
        SERVICE_PAGE_NUMBER+EQUAL+Convert.toString(i)+
        (is_anchor?REQUEST_ANCHOR+sp.pageAnchor:EMPTY)+
        (page_func!=null?UPPER+CLOSE+DOUBLE_UPPER+SPACE+HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER+(page_href!=null?page_href:EMPTY)+DOUBLE_UPPER:DOUBLE_UPPER)+
        (is_class?SPACE+HTML_ELEMENT_A_CLASS+EQUAL+DOUBLE_UPPER+sp.pageClass+DOUBLE_UPPER:EMPTY)+
        (is_title?SPACE+HTML_ELEMENT_A_TITLE+EQUAL+DOUBLE_UPPER+sp.pageTitle+DOUBLE_UPPER:EMPTY)+CODE_CLOSE+
        Convert.toString(val-row_count+1)+DOUBLE_POINT+Convert.toString(val)+
        CODE_OPEN+SLASH+HTML_ELEMENT_A+CODE_CLOSE+SPACE;
      }
      val=page_number*row_count;
      if(val>total_row_count)last_val=total_row_count;else last_val=val;
      ret_val+=(is_current_class?CODE_OPEN+HTML_ELEMENT_FONT+SPACE+HTML_ELEMENT_FONT_CLASS+EQUAL+DOUBLE_UPPER+sp.pageCurrentClass+DOUBLE_UPPER+CODE_CLOSE+Convert.toString(val-row_count+1)+DOUBLE_POINT+Convert.toString(last_val)+CODE_OPEN+SLASH+HTML_ELEMENT_FONT+CODE_CLOSE:
                Convert.toString(val-row_count+1)+DOUBLE_POINT+Convert.toString(last_val))+SPACE;//view pages
      int num=last_page_number-total_pages_count>0?total_pages_count:last_page_number;
      for(int i=(page_number+1);i<=num;i++){//link pages
        val=i*row_count;
        if(val>total_row_count)last_val=total_row_count;else last_val=val;
        ret_val+=CODE_OPEN+HTML_ELEMENT_A+SPACE+(sp.pageParam!=null?sp.pageParam+SPACE:EMPTY)+
        (page_func!=null?HTML_ELEMENT_A_ONCLICK+EQUAL+DOUBLE_UPPER+page_func+OPEN+UPPER:HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER)+
        sp.requestURL+REQUEST_SYMBOL+query_str+REQUEST_DELIM+
        (is_marker?SERVICE_PAGE_MARKER+EQUAL+Convert.toString(page_start_number)+REQUEST_DELIM:EMPTY)+
        SERVICE_PAGE_NUMBER+EQUAL+Convert.toString(i)+
        (is_anchor?REQUEST_ANCHOR+sp.pageAnchor:EMPTY)+
        (page_func!=null?UPPER+CLOSE+DOUBLE_UPPER+SPACE+HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER+(page_href!=null?page_href:EMPTY)+DOUBLE_UPPER:DOUBLE_UPPER)+
        (is_class?SPACE+HTML_ELEMENT_A_CLASS+EQUAL+DOUBLE_UPPER+sp.pageClass+DOUBLE_UPPER:EMPTY)+
        (is_title?SPACE+HTML_ELEMENT_A_TITLE+EQUAL+DOUBLE_UPPER+sp.pageTitle+DOUBLE_UPPER:EMPTY)+CODE_CLOSE+
        Convert.toString(val-row_count+1)+DOUBLE_POINT+Convert.toString(last_val)+
        CODE_OPEN+SLASH+HTML_ELEMENT_A+CODE_CLOSE+SPACE;
      }
    }
    count=/*!*/page_start_number+page_count;
    if(count<=total_pages_count){//set link next pages
      ret_val+=CODE_OPEN+HTML_ELEMENT_A+SPACE+(sp.pageParam!=null?sp.pageParam+SPACE:EMPTY)+
      (page_func!=null?HTML_ELEMENT_A_ONCLICK+EQUAL+DOUBLE_UPPER+page_func+OPEN+UPPER:HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER)+
      sp.requestURL+REQUEST_SYMBOL+query_str+REQUEST_DELIM+
      (is_marker?SERVICE_PAGE_MARKER+EQUAL+Convert.toString(count)+REQUEST_DELIM:EMPTY)+
      SERVICE_PAGE_NUMBER+EQUAL+Convert.toString(count)+
      (is_anchor?REQUEST_ANCHOR+sp.pageAnchor:EMPTY)+
      (page_func!=null?UPPER+CLOSE+DOUBLE_UPPER+SPACE+HTML_ELEMENT_A_HREF+EQUAL+DOUBLE_UPPER+(page_href!=null?page_href:EMPTY)+DOUBLE_UPPER:DOUBLE_UPPER)+
      (is_class?SPACE+HTML_ELEMENT_A_CLASS+EQUAL+DOUBLE_UPPER+sp.pageClass+DOUBLE_UPPER:EMPTY)+
      (is_title?SPACE+HTML_ELEMENT_A_TITLE+EQUAL+DOUBLE_UPPER+sp.pageTitle+DOUBLE_UPPER:EMPTY)+CODE_CLOSE+
      (page_next!=null?page_next:NEXT_2_GT)+CODE_OPEN+SLASH+HTML_ELEMENT_A+CODE_CLOSE+SPACE;
    }
    ret_val+=(is_current_class?EMPTY:VERT_SLASH)+NEXT_LINE;
    return ret_val;
  }
  //file=filename('file.xls') ref=pointer('sheet_name;1;12')->name of sheet;col;row
  //ref=pointer('sheet_name;!1;!12')->name of sheet;!col;!row (!->pointer for repeat(copy) col or row data and add new)
  private void cellParamCopy(Cell source,Cell dest)
  {
    try{
      Comment comment=source.getCellComment();
      String formula=source.getCellType()==Cell.CELL_TYPE_FORMULA?source.getCellFormula():null;
      CellStyle style=source.getCellStyle();
      int type=source.getCellType();
      Hyperlink hyperlink=source.getHyperlink();
      if(comment!=null)dest.setCellComment(comment);
      if(formula!=null)dest.setCellFormula(formula);
      if(style!=null)dest.setCellStyle(style);
      dest.setCellType(type);
      if(hyperlink!=null)dest.setHyperlink(hyperlink);
      dest.getRow().setHeight(source.getRow().getHeight());//height
    }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString());}
  }
  //JSON/XLS data converting
  //json structure binding to xls template file
  //json: { "header_name":"some_name",
  //        "header_date":"some_date",
  //        "data":[ {"1":"data_1", "2":data_2, ...}, {...} ],
  //        "footer_name":"some_name",
  //        "footer_date":"some_date" }
  //xls:     header_name header_date
  //         data
  //         footer_name footer_date
  //replace position in xls template of json data
  //save in temp file or return a binary BASE64 data
  public void getXlsAsBase64String(String name,String file,String ref){//return xls as BASE64 string
    //code here
  }
  public void writeJsonToXls(String name,String file,String ref){//return xls filepath
    //code here
  }
  //write json data to xls file
  //sent json data to module where calling writeToXls
  /*json data format:
  [
    [
      [{"param1"="value1"}],[{"param2"="value2"}],[{...}]
    ],
    [
      [{"param1"="value1"}],[{"param2"="value2"}],[{...}]
    ],
    [...]
  ]
  */
  //name is extra param of json data
  public void writeToXls(String name,String file,String ref){
    Vector list=new Vector(),sub_list;
    String data=this.getExtra(name),s;
    Iterator i1,i2;
    try{
      JSONParser parser=new JSONParser();
      JSONArray jsonArray=(JSONArray)parser.parse(data),jsonSubArray;
      i1=jsonArray.listIterator();
      while(i1.hasNext()){
        jsonSubArray=(JSONArray)i1.next();
        i2=jsonSubArray.listIterator();
        sub_list=new Vector();
        while(i2.hasNext()){
           s=((JSONObject)((JSONArray)i2.next()).toArray()[0]).values().toArray()[0].toString();
           GridDataItem gdi=new GridDataItem();
           gdi.stringValue=s;
           gdi.type=ITEM_TYPE_STRING;
           //Manager.getLog().write(NEXT_LINE+s+NEXT_LINE);
           sub_list.add(gdi);
        }
        list.add(sub_list);
      }
    }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString());}
    this.writeToXls(list,file,ref,FILEPATH_SERVICE_TRASH);
  }
  private void writeToXls(Vector list,String file,String ref,String path)//write to xls file
  {
    FileOutputStream xls_file;
    InputStream template_file=null;
    File f;
    Workbook wb=null;
    Sheet sheet=null;
    PrintSetup print;
    String str,filename,filepath,sheetname=null,col=null,row=null;
    int row_num=0,col_num=0;
    boolean col_copy=false,row_copy=false;
    Row r;//xls row
    Cell c;//xls cell
    //ref (3 position)
    StringTokenizer st=new StringTokenizer(ref,POINT_COMA);//=Convert.getValues(ref,POINT_COMA);
    if(st.hasMoreTokens())sheetname=st.nextToken().trim();//1
    if(st.hasMoreTokens()){col=st.nextToken().trim();if(col.contains(DELIM_XLS_POS_COPY)){col=col.replaceAll(DELIM_XLS_POS_COPY,EMPTY);col_copy=true;}}//2
    if(st.hasMoreTokens()){row=st.nextToken().trim();if(row.contains(DELIM_XLS_POS_COPY)){row=row.replaceAll(DELIM_XLS_POS_COPY,EMPTY);row_copy=true;}}//3
    st=null;
    if(col!=null&&col.length()>0)try{col_num=Convert.toIntValue(col);}catch(Exception e){}
    if(row!=null&&row.length()>0)try{row_num=Convert.toIntValue(row);}catch(Exception e){}
    //new filename format: <filename_SessionID.file_ext>
    //old killed foramt: filename=Convert.toString(sessionID)+DOWN+file;
    int ind=file.lastIndexOf(LOCAL_DELIM_2);//kill subdirs if exists
    if(ind==-1)ind=file.lastIndexOf(LOCAL_DELIM);
    if(ind>-1)filename=file.substring(ind+1);
    else filename=file;
    ind=filename.lastIndexOf(POINT);//add session_id before file_ext
    if(ind>-1)filename=filename.substring(0,ind)+DOWN+Convert.toString(sessionID)+filename.substring(ind);
    else filename=filename+DOWN+Convert.toString(sessionID);
    //open xls result
    str=manager.getInitial().getServiceTrash();//ini service trash dir
    if(str!=null&&!str.endsWith(LOCAL_DELIM_2))str+=LOCAL_DELIM_2;
    filepath=(str!=null?str:manager.getServletFilepath()+path)+filename;
    //open xls template
    str=manager.getInitial().getServiceTemplates();//ini service templates dir
    if(str!=null&&!str.endsWith(LOCAL_DELIM_2))str+=LOCAL_DELIM_2;
    str=(str!=null)?(str+file):(manager.getServletFilepath()+FILEPATH_SERVICE_TEMPLATES+file);//str->template xls filepath
    //file seek
    try{
      f=new File(filepath);
      if(f.exists()&&f.isFile())template_file=new FileInputStream(filepath);//<-service_trash filepath
      else{
        f=new File(str);
        if(f.exists()&&f.isFile())template_file=new FileInputStream(str);//<-service_templates filepath
      }
      try{
        if(template_file!=null&&template_file.available()>0)wb=WorkbookFactory.create(template_file);
      }catch(InvalidFormatException if_e){manager.getLog().write(sessionID,ERROR_INVALID_FORMAT,file);}
    }catch(IOException io_e){}
    f=null;
    //create xls data(book,sheet)
    if(wb==null)wb=new HSSFWorkbook();
    if(sheetname!=null)sheet=wb.getSheet(sheetname);
    if(sheet==null)sheet=wb.createSheet(sheetname!=null?sheetname:EMPTY);
    //int sheet_index=wb.getSheetIndex(sheet);
    print=sheet.getPrintSetup();
    print.setLandscape(true);
    sheet.setFitToPage(true);
    sheet.setHorizontallyCenter(true);
    //write data to sheet
    Vector sub_list;
    GridDataItem gdi;
    /*if(col_copy){}//copy right col to right*/
    if(row_copy){//copy footer after data
      int num1=row_num+1,num2=sheet.getLastRowNum(),pos=list.size()-1;
      try{
        if(num2>=num1)sheet.shiftRows(num1,num2,pos);
        //Manager.getLog().write("SHIFT_ROWS: num1="+num1+" num2="+num2+"\r\n");
      }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString()+SPACE+num1+SPACE+num2);}
    }
    for(int i=0;i<list.size();i++){
      sub_list=(Vector)list.elementAt(i);
      r=sheet.getRow(row_num+i);
      if(r==null)r=sheet.createRow(row_num+i);
      for(int j=0;j<sub_list.size();j++){
        gdi=(GridDataItem)sub_list.elementAt(j);
        c=r.getCell(col_num+j);
        if(c==null)c=r.createCell(col_num+j);
        //copy cell->style
        if(row_copy&&!col_copy&&i!=0){this.cellParamCopy(sheet.getRow(row_num).getCell(col_num+j),c);}/*cell(..,row_num)->style for all next rows*/
        else if(!row_copy&&col_copy&&j!=0){this.cellParamCopy(sheet.getRow(row_num+i).getCell(col_num),c);}/*cell(col_num,..)->style for all next cols*/
        else if(row_copy&&col_copy&&(i!=0||j!=0)){this.cellParamCopy(sheet.getRow(row_num).getCell(col_num),c);}/*cell(col_num,row_num)->style for all*/
        //end of copy
        try{
          if(gdi.type==ITEM_TYPE_STRING){c.setCellValue(gdi.stringValue);}
          else if(gdi.type==ITEM_TYPE_BYTES&&gdi.bytesValue!=null&&gdi.bytesValue.length>0){//only jpeg image
            int pict_id=wb.addPicture(gdi.bytesValue,Workbook.PICTURE_TYPE_JPEG);
            Drawing drawing=sheet.createDrawingPatriarch();
            CreationHelper helper=wb.getCreationHelper();
            ClientAnchor anchor=helper.createClientAnchor();
            anchor.setCol1(col_num+j);anchor.setRow1(row_num+i);
            drawing.createPicture(anchor,pict_id).resize();
          }//else if
        }catch(Exception e){manager.getLog().write(sessionID,WARNING_SESSION_EXCEPTION,e.toString());}
      }//for
    }//for
    //write to xls file
    try{
      xls_file=new FileOutputStream(filepath);
      wb.write(xls_file);
      if(xls_file!=null){xls_file.close();xls_file=null;}
    }catch(IOException io_e){manager.getLog().write(sessionID,WARNING_DATA_NOT_FOUND,io_e.toString());}
  }
}
//--------------------------------pagedata-------------------------------------//
//starts page data from service page files ...
//UNDER CONSTRUCTION
class PageFormat implements Interface,tools.Interface,tools.HtmlInterface
{
  private Manager manager=null;
  private String aHref=null;
  private String imgSrc=null;
  private String inputName=null;
  private String inputValue=null;
  private String selectOption=null;
  private String selected=null;
  private String tableTr=null;
  private String title=null;
  public PageFormat(Manager manager)
  {
    this.manager=manager;
  }
  //thinking about String.format in another parts
  //<a href="%" title="%">%</a>
  public String getAHref(String format,String param1,String param2,String param3)//href,view href,title
  {
    return String.format(format,param1,param2,param3);
  }
  //src="%"
  public String getImgSrc(String format,String param)
  {
    return String.format(format,param);
  }
  //name="%"
  public String getInputName(String format,String param)
  {
    return String.format(format,param);
  }
  //value="%"
  public String getInputValue(String format,String param)
  {
    return String.format(format,param);
  }
  //<option selected value="%">%</option>
  public String getSelectOption(String format,String param1,String param2,String param3)//value,view option,option selected
  {
    return String.format(format,param1,param2,param3);
  }
  //<td>%</td>
  public String getTableTr(String format,String param)
  {
    return String.format(format,param);
  }
  //title="%"
  public String getTitle(String format,String param)
  {
    return String.format(format,param);
  }
}
//--------------------------------initial-------------------------------------//
//starts data from initial file ...
class Initial implements Interface,tools.Interface
{
  private Manager manager=null;
  private boolean changed=false;//set TRUE if ini param changed by servlet
  private String localCodepage=null;
  private String localContentType=null;
  private String localAddress=null;
  private String localUser=null;
  private String localPassword=null;
  private String localLog=null;
  private String localDebug=null;
  private String localTimeout=null;
  private String databasePrimaryAddress=null;
  private String databaseSecondaryAddress=null;
  private String databaseLog=null;
  private String databaseDriver=null;
  private String databaseType=null;
  private String databaseCookie=null;
  private String databaseTimeout=null;
  private String databasePassword=null;
  private Vector databaseUsersList=null;/*users list for password encoding*/
  private String databaseUsers=null;/*users filepath*/
  private String databaseSessionsCount=null;
  private String databaseSessionsTimeout=null;
  private String databaseBlackList=null;
  private String databaseBlackListCount=null;
  private String databaseBlackListTimeout=null;
  private String databaseValidConnection=null;
  private String serviceTemplates=null;
  private String servicePages=null;
  private String serviceTrash=null;
  private String serviceFiles=null;
  private Vector serviceStartsList=null;
  //[set/get]
  public void setChanged(boolean value){changed=value;}
  public void setLocalCodepage(String value){localCodepage=value;}
  public void setLocalContentType(String value){localContentType=value;}
  public void setLocalAddress(String value){localAddress=value;}
  public void setLocalUser(String value){localUser=value;}
  public void setLocalPassword(String value){localPassword=value;}
  public void setLocalLog(String value){localLog=value;}
  public void setLocalDebug(String value){localDebug=value;}
  public void setLocalTimeout(String value){localTimeout=value;}
  public void setDatabasePrimaryAddress(String value){databasePrimaryAddress=value;}
  public void setDatabaseSecondaryAddress(String value){databaseSecondaryAddress=value;}
  public void setDatabaseLog(String value){databaseLog=value;}
  public void setDatabaseDriver(String value){databaseDriver=value;}
  public void setDatabaseCookie(String value){databaseCookie=value;}
  public void setDatabaseTimeout(String value){databaseTimeout=value;}
  public void setDatabasePassword(String value){databasePassword=value;}
  public void setDatabaseUsersList(Vector users_list){databaseUsersList=users_list;}
  public void setDatabaseUsers(String users){databaseUsers=users;}
  public void setDatabaseSessionsCount(String value){databaseSessionsCount=value;}
  public void setDatabaseSessionsTimeout(String value){databaseSessionsTimeout=value;}
  public void setDatabaseBlackList(String value){databaseBlackList=value;}
  public void setDatabaseBlackListTimeout(String value){databaseBlackListTimeout=value;}
  public void setDatabaseValidConnection(String value){databaseValidConnection=value;}
  public void setServiceTemplates(String value){serviceTemplates=value;}
  public void setServicePages(String value){servicePages=value;}
  public void setServiceTrash(String value){serviceTrash=value;}
  public void setServiceFiles(String value){serviceFiles=value;}
  public void setServiceStartsList(Vector starts_list){serviceStartsList=starts_list;}
  public String getLocalCodepage(){return localCodepage;}
  public String getLocalContentType(){return localContentType;}
  public String getLocalAddress(){return localAddress;}
  public String getLocalUser(){return localUser;}
  public String getLocalPassword(){return localPassword;}
  public String getLocalLog(){return localLog;}
  public String getLocalDebug(){return localDebug;}
  public String getLocalTimeout(){return localTimeout;}
  public String getDatabasePrimaryAddress(){return databasePrimaryAddress;}
  public String getDatabaseSecondaryAddress(){return databaseSecondaryAddress;}
  public String getDatabaseLog(){return databaseLog;}
  public String getDatabaseDriver(){return databaseDriver;}
  public String getDatabaseType(){return databaseType;}
  public String getDatabaseCookie(){return databaseCookie;}
  public String getDatabaseTimeout(){return databaseTimeout;}
  public String getDatabasePassword(){return databasePassword;}
  public Vector getDatabaseUsersList(){return databaseUsersList;}
  public String getDatabaseUsers(){return databaseUsers;}
  public String getDatabaseSessionsCount(){return databaseSessionsCount;}
  public String getDatabaseSessionsTimeout(){return databaseSessionsTimeout;}
  public String getDatabaseBlackList(){return databaseBlackList;}
  public String getDatabaseBlackListCount(){return databaseBlackListCount;}
  public String getDatabaseBlackListTimeout(){return databaseBlackListTimeout;}
  public String getDatabaseValidConnection(){return databaseValidConnection;}
  public String getServiceTemplates(){return serviceTemplates;}
  public String getServicePages(){return servicePages;}
  public String getServiceTrash(){return serviceTrash;}
  public String getServiceFiles(){return serviceFiles;}
  public Vector getServiceStartsList(){return serviceStartsList;}
  //[constructor]
  public Initial(){}
  public boolean open(String filename,Manager manager)
  {
    boolean ret_val=false;
    this.manager=manager;
    changed=false;
    //get ini parameters
    String line,param,value;
    int group_index=-1;
    try{
      BufferedReader file_stream=new BufferedReader(new FileReader(filename));
      ret_val=true;
      line=file_stream.readLine();
      while(line!=null){
        line=line.trim();
        if(line.startsWith(INI_GROUP_NAME_OPEN)&&line.endsWith(INI_GROUP_NAME_CLOSE)){//group name
          line=line.substring(1,line.length()-1);
          if(line.equalsIgnoreCase(IGN_LOCAL))group_index=IGI_LOCAL;
          //else if(line.equalsIgnoreCase(IGN_REMOTE))group_index=IGI_REMOTE;
          else if(line.equalsIgnoreCase(IGN_DATABASE))group_index=IGI_DATABASE;
          else if(line.equalsIgnoreCase(IGN_SERVICE))group_index=IGI_SERVICE;
        }
        else while(!line.equals(EMPTY)){//group param
          param=Convert.getParam(line);
          value=Convert.getValue(line);
          if(group_index==IGI_LOCAL){
            //if(param.startsWith(COMMENT)){LocalComments+=line+NEXT_LINE;break;}
            if(param.equalsIgnoreCase(IPN_LOCAL_CODEPAGE)){localCodepage=value;break;}
            if(param.equalsIgnoreCase(IPN_LOCAL_CONTENT_TYPE)){localContentType=value;break;}
            if(param.equalsIgnoreCase(IPN_LOCAL_ADDRESS)){localAddress=value;break;}
            if(param.equalsIgnoreCase(IPN_LOCAL_USER)){localUser=value;break;}
            if(param.equalsIgnoreCase(IPN_LOCAL_PASSWORD)){localPassword=value;break;}
            if(param.equalsIgnoreCase(IPN_LOCAL_LOG)){localLog=value;break;}
            if(param.equalsIgnoreCase(IPN_LOCAL_DEBUG)){localDebug=value;break;}
            if(param.equalsIgnoreCase(IPN_LOCAL_TIMEOUT)){localTimeout=value;break;}
            break;
          }
          if(group_index==IGI_DATABASE){
            //if(param.startsWith(COMMENT)){DatabaseComments+=line+NEXT_LINE;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_PRIMARY_ADDRESS)){databasePrimaryAddress=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_SECONDARY_ADDRESS)){databaseSecondaryAddress=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_LOG)){databaseLog=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_DRIVER)){databaseDriver=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_COOKIE)){databaseCookie=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_TIMEOUT)){databaseTimeout=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_PASSWORD)){databasePassword=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_USER)){
              if(databaseUsersList==null)databaseUsersList=new Vector();
              databaseUsersList.add(value);break;
            }
            if(param.equalsIgnoreCase(IPN_DATABASE_USERS)){databaseUsers=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_SESSIONS_COUNT)){databaseSessionsCount=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_SESSIONS_TIMEOUT)){databaseSessionsTimeout=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_BLACKLIST)){databaseBlackList=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_BLACKLIST_COUNT)){databaseBlackListCount=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_BLACKLIST_TIMEOUT)){databaseBlackListTimeout=value;break;}
            if(param.equalsIgnoreCase(IPN_DATABASE_VALID_CONNECTION)){databaseValidConnection=value;break;}
            break;
          }
          if(group_index==IGI_SERVICE){
            //if(param.startsWith(COMMENT)){ServiceComments+=line+NEXT_LINE;break;}
            if(param.equalsIgnoreCase(IPN_SERVICE_TEMPLATES)){serviceTemplates=value;break;}
            if(param.equalsIgnoreCase(IPN_SERVICE_PAGES)){servicePages=value;break;}
            if(param.equalsIgnoreCase(IPN_SERVICE_TRASH)){serviceTrash=value;break;}
            if(param.equalsIgnoreCase(IPN_SERVICE_FILES)){serviceFiles=value;break;}
            if(param.equalsIgnoreCase(IPN_SERVICE_START)||param.equalsIgnoreCase(IPN_SERVICE_INVOKE)){
              if(serviceStartsList==null)serviceStartsList=new Vector();
              serviceStartsList.add(value);break;
            }
            break;
          }
          //if(param.startsWith(COMMENT))HeaderComments+=line+NEXT_LINE;
          break;
        }//end while
        line=file_stream.readLine();
      }
      file_stream.close();file_stream=null;
      //DATABASE_TYPE auto_definition
      if(databaseDriver!=null){
        if(databaseDriver.contains(DATABASE_TYPE_ORACLE))databaseType=DATABASE_TYPE_ORACLE;
        else if(databaseDriver.contains(DATABASE_TYPE_MYSQL))databaseType=DATABASE_TYPE_MYSQL;
      }
    }catch(IOException io_e){}//end get ini parameters
    return ret_val;
  }
  public void close()
  {
    if(changed){//save param in new ini file
      String str=EMPTY,s/*temp string*/;
      str+=INI_GROUP_NAME_OPEN+IGN_LOCAL+INI_GROUP_NAME_CLOSE+NEXT_LINE;
      str+=IPN_LOCAL_CODEPAGE+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(localCodepage==null?EMPTY:localCodepage)+NEXT_LINE;
      str+=IPN_LOCAL_CONTENT_TYPE+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(localContentType==null?EMPTY:localContentType)+NEXT_LINE;
      str+=IPN_LOCAL_ADDRESS+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(localAddress==null?EMPTY:localAddress)+NEXT_LINE;
      str+=IPN_LOCAL_USER+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(localUser==null?EMPTY:localUser)+NEXT_LINE;
      str+=IPN_LOCAL_PASSWORD+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(localPassword==null?EMPTY:localPassword)+NEXT_LINE;
      str+=IPN_LOCAL_LOG+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(localLog==null?EMPTY:localLog)+NEXT_LINE;
      str+=IPN_LOCAL_DEBUG+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(localDebug==null?EMPTY:localDebug)+NEXT_LINE;
      str+=IPN_LOCAL_TIMEOUT+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(localTimeout==null?EMPTY:localTimeout)+NEXT_LINE;
      str+=NEXT_LINE;
      str+=INI_GROUP_NAME_OPEN+IGN_DATABASE+INI_GROUP_NAME_CLOSE+NEXT_LINE;
      str+=IPN_DATABASE_PRIMARY_ADDRESS+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databasePrimaryAddress==null?EMPTY:databasePrimaryAddress)+NEXT_LINE;
      str+=IPN_DATABASE_SECONDARY_ADDRESS+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databaseSecondaryAddress==null?EMPTY:databaseSecondaryAddress)+NEXT_LINE;
      str+=IPN_DATABASE_DRIVER+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databaseDriver==null?EMPTY:databaseDriver)+NEXT_LINE;
      str+=IPN_DATABASE_LOG+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databaseLog==null?EMPTY:databaseLog)+NEXT_LINE;
      str+=IPN_DATABASE_COOKIE+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databaseCookie==null?EMPTY:databaseCookie)+NEXT_LINE;
      str+=IPN_DATABASE_TIMEOUT+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databaseTimeout==null?EMPTY:databaseTimeout)+NEXT_LINE;
      str+=IPN_DATABASE_PASSWORD+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databasePassword==null?EMPTY:databasePassword)+NEXT_LINE;
      if(databaseUsersList!=null&&databaseUsersList.size()>0){
        Enumeration e=databaseUsersList.elements();
        while(e.hasMoreElements()){
          s=(String)e.nextElement();
          str+=IPN_DATABASE_USER+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(s==null?EMPTY:s)+NEXT_LINE;
        }
      }
      str+=IPN_DATABASE_USERS+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databaseUsers==null?EMPTY:databaseUsers)+NEXT_LINE;
      str+=IPN_DATABASE_SESSIONS_COUNT+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databaseSessionsCount==null?EMPTY:databaseSessionsCount)+NEXT_LINE;
      str+=IPN_DATABASE_SESSIONS_TIMEOUT+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databaseSessionsTimeout==null?EMPTY:databaseSessionsTimeout)+NEXT_LINE;
      str+=IPN_DATABASE_BLACKLIST+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databaseBlackList==null?EMPTY:databaseBlackList)+NEXT_LINE;
      str+=IPN_DATABASE_BLACKLIST_COUNT+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databaseBlackListCount==null?EMPTY:databaseBlackListCount)+NEXT_LINE;
      str+=IPN_DATABASE_BLACKLIST_TIMEOUT+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databaseBlackListTimeout==null?EMPTY:databaseBlackListTimeout)+NEXT_LINE;
      str+=IPN_DATABASE_VALID_CONNECTION+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(databaseValidConnection==null?EMPTY:databaseValidConnection)+NEXT_LINE;
      str+=NEXT_LINE;
      str+=INI_GROUP_NAME_OPEN+IGN_SERVICE+INI_GROUP_NAME_CLOSE+NEXT_LINE;
      str+=IPN_SERVICE_TEMPLATES+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(serviceTemplates==null?EMPTY:serviceTemplates)+NEXT_LINE;
      str+=IPN_SERVICE_PAGES+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(servicePages==null?EMPTY:servicePages)+NEXT_LINE;
      str+=IPN_SERVICE_TRASH+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(serviceTrash==null?EMPTY:serviceTrash)+NEXT_LINE;
      str+=IPN_SERVICE_FILES+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(serviceFiles==null?EMPTY:serviceFiles)+NEXT_LINE;
      if(serviceStartsList!=null&&serviceStartsList.size()>0){
        Enumeration e=serviceStartsList.elements();
        while(e.hasMoreElements()){
          s=(String)e.nextElement();
          str+=IPN_SERVICE_START+SPACE+INI_DELIM_PARAM_VALUE+SPACE+(s==null?EMPTY:s)+NEXT_LINE;
        }
      }
      Convert.writeToFile(manager.getServletFilepath()+FILEPATH_INI,str.getBytes());
    }
    if(databaseUsersList!=null){databaseUsersList.clear();databaseUsersList=null;}
    if(serviceStartsList!=null){serviceStartsList.clear();serviceStartsList=null;}
  }
}
//-----------------------------------log--------------------------------------//
//logger data to ...
class Log implements Interface,tools.Interface//NEED TO CHANGE LOG CLASS
{
  private FileOutputStream outStream=null;
  private Manager manager=null;
  private String filepath=null;
  //[get]
  public FileOutputStream getFile(){return outStream;}
  public String getFilepath(){return filepath;}
  //[constructor]
  public Log(){}
  public void flush()
  {
    if(outStream!=null)try{outStream.flush();}catch(IOException io_e){}
  }
  public synchronized boolean open(String filepath,Manager manager)
  {
    boolean ret_val=false;
    this.manager=manager;
    this.filepath=filepath;
    String str=this.manager.getLogFilepathWithDate(this.filepath);
    try{
      if(str!=null&&str.length()>0){outStream=new FileOutputStream(str,true);ret_val=true;}
    }catch(Exception e){System.out.println(this.manager.getMessageByID(ERROR_LOG_OPEN_FAILED)+SPACE+e.toString());}
    notifyAll();
    return ret_val;
  }
  public synchronized void close()
  {
    try{
      if(outStream!=null){outStream.close();outStream=null;}//file close
    }catch(Exception e){System.out.println(e.toString());}
    notifyAll();
  }
  private void writeLogDatabase(int message_type,String message)
  {
    try{
      DatabaseSession database_session=(DatabaseSession)manager.getDefaultDatabaseSessions().get(SESSION_KEY_LOG_DATABASE_CONNECTION);
      if(database_session!=null)((LogDatabase)database_session.database).writeLog(message_type,message);
    }catch(Exception e){}
  }
  public synchronized void write(long session_id,int id)
  {
    String message=manager.getLogMessageByID(id);
    int message_type=manager.getLogTypeByID(id);
    String str=new Date().toString()+message+MESSAGE_DELIM_SUBVALUES+SESSION_NUMBER+Convert.toString(session_id)+NEXT_LINE;
    try{
      if(outStream!=null)outStream.write(str.getBytes());
      if(manager.getDefaultDatabaseSessions()!=null)this.writeLogDatabase(message_type,str);
      //if(manager.isDebug())System.out.println(str);
    }catch(Exception e){System.out.println(manager.getMessageByID(ERROR_LOG_WRITE_FAILED)+SPACE+e.toString());}
    notifyAll();
  }
  public synchronized void write(long session_id,int id,String value)
  {
    String message=manager.getLogMessageByID(id);
    int message_type=manager.getLogTypeByID(id);
    String str=new Date().toString()+message+MESSAGE_DELIM_SUBVALUES+SESSION_NUMBER+Convert.toString(session_id)+MESSAGE_DELIM_SUBVALUES+value+NEXT_LINE;
    try{
      if(outStream!=null)outStream.write(str.getBytes());
      if(manager.isLog2Database()&&manager.getDefaultDatabaseSessions()!=null)this.writeLogDatabase(message_type,str);
      //if(manager.isDebug())System.out.println(str);
    }catch(Exception e){System.out.println(manager.getMessageByID(ERROR_LOG_WRITE_FAILED)+SPACE+e.toString());}
    notifyAll();
  }
  public synchronized void write(String value)//specific data write
  {
    try{
      if(outStream!=null)outStream.write(value.getBytes());
      //if(manager.isDebug())System.out.println(value);
    }catch(Exception e){System.out.println(manager.getMessageByID(ERROR_LOG_WRITE_FAILED)+SPACE+e.toString());}
    notifyAll();
  }
}
//--------------------------------message-------------------------------------//
//messages to log ...
final class MessageList implements Interface,tools.Interface
{
  public static String getText(int id)
  {
    String ret_val=EMPTY;
    switch(id){
      //[info]
      case INFO_DEVELOPER_DESCRIPTION:ret_val="[ ABTO3BIT software company 2006 - 2016 c ]";break;
      case INFO_SERVER_DESCRIPTION:ret_val="[ <skyDrakkar> 2.5 dragonFire release 20.01.2016 ]";break;
      case INFO_SERVER_LOADING:ret_val="Loading >>>";break;
      case INFO_SERVER_UNLOADING:ret_val="Unloading <<<";break;
      case INFO_SERVER_SHUTDOWN:ret_val="^";break;
      case INFO_SERVER_ACTIVATED:ret_val="Server activated";break;
      case INFO_SERVER_DEACTIVATED:ret_val="Server deactivated";break;
      case INFO_DATABASE_CONNECTION_CLOSED:ret_val="Database connection closed";break;
      case INFO_DATABASE_CONNECTION_ESTABLISHED:ret_val="Database connection established";break;
      case INFO_DATABASE_RECONNECTION:ret_val="Database reconnection";break;
      case INFO_SERVICE_PARAM_REGISTERED:ret_val="Service parameter registered";break;
      case INFO_SERVICE_REGISTERED:ret_val="Service registered";break;
      case INFO_SERVICE_CONTENT_REGISTERED:ret_val="Service content registered";break;
      case INFO_SESSION_ACTIVATED:ret_val="Session activated";break;
      case INFO_SESSION_DEACTIVATED:ret_val="Session deactivated";break;
      case INFO_INITIAL_ACTIVATED:ret_val="Initial activated";break;
      case INFO_LOG_ACTIVATED:ret_val="Log activated";break;
      case INFO_DATABASE_USERS_ACTIVATED:ret_val="Database users activated";break;
      case INFO_DATABASE_BLACKLIST_ACTIVATED:ret_val="Database blacklist activated";break;
      case INFO_WRITE_REQUEST:ret_val="Write request to database";break;
      case INFO_UPDATE_REQUEST:ret_val="Update request from database";break;
      case INFO_READ_RESPONSE:ret_val="Read response from server";break;
      case INFO_READ_DATA:ret_val="Read data from database";break;
      case INFO_WRITE_FILE:ret_val="Write file to disk";break;
      case INFO_COOKIE_CREATED:ret_val="Cookie created";break;
      case INFO_COOKIE_ESTABLISHED:ret_val="Cookie established";break;
      case INFO_COOKIE_FAILED:ret_val="Cookie failed";break;
      case INFO_DATABASE_SESSION_SAVED:ret_val="Database session saved";break;
      case INFO_DATABASE_SESSION_REMOVED:ret_val="Database session removed";break;
      case INFO_SAVED_IN_TIMEOUT_BLACKLIST:ret_val="Database user saved in timeout blacklist";break;
      case INFO_REMOVED_FROM_TIMEOUT_BLACKLIST:ret_val="Database user removed from timeout blacklist";break;
      case INFO_SERVICE_MESSAGE:ret_val="Service information message";break;
      case INFO_SERVICE_STARTED:ret_val="Service started";break;
      case INFO_SQL_MESSAGE:ret_val="SQL information message";break;
      //[warning]
      case WARNING_INVALID_LOCAL_ADDRESS:ret_val="Invalid local address";break;
      case WARNING_INVALID_LOCAL_HOST:ret_val="Invalid local hostname or ip address";break;
      case WARNING_INVALID_LOCAL_PORT:ret_val="Invalid local port number";break;
      case WARNING_ADDRESS_NOT_FOUND:ret_val="Address not found or invalid";break;
      case WARNING_SESSION_FAILED:ret_val="Session failed and stop execution";break;
      case WARNING_SESSION_WAITING:ret_val="Session waiting for execution";break;
      case WARNING_SESSION_EXCEPTION:ret_val="Session exception found";break;
      case WARNING_RESPONSE_NOT_FOUND:ret_val="Response not found";break;
      case WARNING_DATA_NOT_FOUND:ret_val="Data not found";break;
      case WARNING_DATABASE_CONNECTION_NOT_ESTABLISHED:ret_val="Database connection not established";break;
      case WARNING_DATABASE_CONNECTION_WAITING:ret_val="Database connection waiting ...";break;
      //[error]
      case ERROR_INITIAL_NOT_FOUND:ret_val="Initialize file not found";break;
      case ERROR_DATABASE_CONNECTION_FAILED:ret_val="Database connection failed";break;
      case ERROR_SERVICE_REGISTRATION_FAILED:ret_val="Service registration failed";break;
      case ERROR_LOG_OPEN_FAILED:ret_val="Log opening failed";break;
      case ERROR_LOG_WRITE_FAILED:ret_val="Log writing failed";break;
      case ERROR_DRIVER_NOT_FOUND:ret_val="Database access driver not found";break;
      case ERROR_SERVICE_NOT_FOUND:ret_val="Service not found";break;
      case ERROR_INVALID_FORMAT:ret_val="Invalid data format";break;
      case ERROR_SQL_QUERY_FAILED:ret_val="SQL query to database failed";break;
      case ERROR_INVALID_REQUEST:ret_val="Invalid request data";break;
      case ERROR_CONTENT_FAILED:ret_val="Content data failed";break;
      case ERROR_SQL_MESSAGE:ret_val="SQL error message";break;
      case ERROR_SESSION_FAILED:ret_val="Session extremally failed";break;
      case ERROR_BLACKLIST_BLOCKING_BY_ADDRESS:ret_val="Blocking database user by address in blacklist";break;
      case ERROR_BLACKLIST_BLOCKING_BY_USERNAME:ret_val="Blocking database user by username in blacklist";break;
      case ERROR_TIMEOUT_BLACKLIST_BLOCKING:ret_val="Blocking database user by timeout blacklist";break;
      //[debug]
      //case DEBUG_CONSOLE_WRITE_REQUEST:ret_val="Write debug request from console";break;
      case DEBUG_PARSE_ATTRIBUTE_VALUE:ret_val="Parse attribute value";break;
      case DEBUG_PARSE_ATTRIBUTES:ret_val="Parse attributes";break;
    }
    return ret_val;
  }
}
////////////////////////////////////////////////////////////////////////////////
/*
BASE64Encoder/BASE64Decoder
*/
class BASE64Encoder
{
  public final static String encode(byte[] d)
  {
    if (d == null) return null;
    byte data[] = new byte[d.length+2];
    System.arraycopy(d, 0, data, 0, d.length);
    byte dest[] = new byte[(data.length/3)*4];

    // 3-byte to 4-byte conversion
    for (int sidx = 0, didx=0; sidx < d.length; sidx += 3, didx += 4)
    {
      dest[didx]   = (byte) ((data[sidx] >>> 2) & 077);
      dest[didx+1] = (byte) ((data[sidx+1] >>> 4) & 017 |
                  (data[sidx] << 4) & 077);
      dest[didx+2] = (byte) ((data[sidx+2] >>> 6) & 003 |
                  (data[sidx+1] << 2) & 077);
      dest[didx+3] = (byte) (data[sidx+2] & 077);
    }

    // 0-63 to ascii printable conversion
    for (int idx = 0; idx <dest.length; idx++)
    {
      if (dest[idx] < 26)     dest[idx] = (byte)(dest[idx] + 'A');
      else if (dest[idx] < 52)  dest[idx] = (byte)(dest[idx] + 'a' - 26);
      else if (dest[idx] < 62)  dest[idx] = (byte)(dest[idx] + '0' - 52);
      else if (dest[idx] < 63)  dest[idx] = (byte)'+';
      else            dest[idx] = (byte)'/';
    }

    // add padding
    for (int idx = dest.length-1; idx > (d.length*4)/3; idx--)
    {
      dest[idx] = (byte)'=';
    }
    return new String(dest);
  }
  public final static String encode(String s) {
    return encode(s.getBytes());
  }
}
class BASE64Decoder
{
  public final static byte[] decodeBuffer(String str)
  {
    if (str == null)  return  null;
    byte data[] = str.getBytes();
    return decodeBuffer(data);
  }
  public final static byte[] decodeBuffer(byte[] data)
  {
    int tail = data.length;
    while (data[tail-1] == '=')  tail--;
    byte dest[] = new byte[tail - data.length/4];

    // ascii printable to 0-63 conversion
    for (int idx = 0; idx <data.length; idx++)
    {
      if (data[idx] == '=')    data[idx] = 0;
      else if (data[idx] == '/') data[idx] = 63;
      else if (data[idx] == '+') data[idx] = 62;
      else if (data[idx] >= '0'  &&  data[idx] <= '9')
        data[idx] = (byte)(data[idx] - ('0' - 52));
      else if (data[idx] >= 'a'  &&  data[idx] <= 'z')
        data[idx] = (byte)(data[idx] - ('a' - 26));
      else if (data[idx] >= 'A'  &&  data[idx] <= 'Z')
        data[idx] = (byte)(data[idx] - 'A');
    }

    // 4-byte to 3-byte conversion
    int sidx, didx;
    for (sidx = 0, didx=0; didx < dest.length-2; sidx += 4, didx += 3)
    {
      dest[didx]   = (byte) ( ((data[sidx] << 2) & 255) |
              ((data[sidx+1] >>> 4) & 3) );
      dest[didx+1] = (byte) ( ((data[sidx+1] << 4) & 255) |
              ((data[sidx+2] >>> 2) & 017) );
      dest[didx+2] = (byte) ( ((data[sidx+2] << 6) & 255) |
              (data[sidx+3] & 077) );
    }
    if (didx < dest.length)
    {
      dest[didx]   = (byte) ( ((data[sidx] << 2) & 255) |
              ((data[sidx+1] >>> 4) & 3) );
    }
    if (++didx < dest.length)
    {
      dest[didx]   = (byte) ( ((data[sidx+1] << 4) & 255) |
              ((data[sidx+2] >>> 2) & 017) );
    }
    return dest;
  }
}