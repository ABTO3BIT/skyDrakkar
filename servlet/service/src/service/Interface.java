package service;
public interface Interface
{
  public static final int CLASS_VERSION=82;
  //public static final boolean DEBUG=false;
  //[local]
  public static final String LOCAL_SYSTEM="service";
  public static final String LOCAL_NAME="<skyDrakkar>";
  public static final String LOCAL_VERSION="2.1 dragonFire";//compiled version
  public static final String LOCAL_DELIM="/";
  public static final String LOCAL_DELIM_2=System.getProperty("file.separator");
  //[request&response]
  public static final String REQUEST_DELIM="&";
  public static final String REQUEST_ANCHOR="#";//anchor
  public static final String REQUEST_CODEPAGE="ISO-8859-1";
  public static final String RESPONSE_CODEPAGE="Cp1251";//default local codepage Windows-1251
  public static final String AJAX_CODEPAGE="UTF-8";
  public static final String REQUEST_HTTP="http://";
  //[default service dir]
  public static final String SERVICE_TEMPLATES="service_templates";//dir name
  public static final String SERVICE_PAGES="service_pages";//dir name
  public static final String SERVICE_TRASH="service_trash";//dir name
  public static final String SERVICE_FILES="service_files";//dir name
  //[resource]
  //public static final String URL_SERVICE_TEMPLATES=SERVICE_TEMPLATES+LOCAL_DELIM;//true path servlet make
  //public static final String URL_SERVICE_PAGES=SERVICE_PAGES+LOCAL_DELIM;//true path servlet make
  public static final String URL_SERVICE_TRASH=SERVICE_TRASH+LOCAL_DELIM;
  public static final String URL_SERVICE_FILES=SERVICE_FILES+LOCAL_DELIM;
  //[filename prefix]
  public static final String FILENAME_PREFIX_SERVICE_PAGES="service_";
  public static final String FILENAME_PREFIX_ADMIN_PAGES="admin_";
  //[filename]
  public static final String FILENAME_INI="service.ini";
  public static final String FILENAME_LOG="service.log";
  public static final String FILENAME_LOG_INI="service_log.ini";
  public static final String FILENAME_USERS="service_users.ini";
  public static final String FILENAME_BLACKLIST="service_blacklist.ini";
  public static final String FILENAME_COOKIE_FAILED="cookie_failed.html";
  public static final String FILENAME_CONNECTION_FAILED="connection_failed.html";
  public static final String FILENAME_CONTENT_FAILED="content_failed.html";
  public static final String FILENAME_TEMPLATE_NOT_FOUND="template_not_found.html";
  public static final String FILENAME_BLACKLIST_BLOCKING="blacklist_blocking.html";
  public static final String FILENAME_SQL_ERROR="sql_error.html";
  public static final String FILENAME_SQL_MESSAGE="sql_message.html";
  public static final String FILENAME_DEFAULT_PAGE="index.html";
  //[filepath]
  public static final String FILEPATH_INI=LOCAL_DELIM_2+FILENAME_INI;
  public static final String FILEPATH_LOG=LOCAL_DELIM_2+FILENAME_LOG;
  public static final String FILEPATH_LOG_INI=LOCAL_DELIM_2+FILENAME_LOG_INI;
  public static final String FILEPATH_USERS=LOCAL_DELIM_2+FILENAME_USERS;
  public static final String FILEPATH_BLACKLIST=LOCAL_DELIM_2+FILENAME_BLACKLIST;
  public static final String FILEPATH_SERVICE_TEMPLATES=LOCAL_DELIM_2+SERVICE_TEMPLATES+LOCAL_DELIM_2;
  public static final String FILEPATH_SERVICE_PAGES=LOCAL_DELIM_2+SERVICE_PAGES+LOCAL_DELIM_2;
  public static final String FILEPATH_SERVICE_TRASH=LOCAL_DELIM_2+SERVICE_TRASH+LOCAL_DELIM_2;
  public static final String FILEPATH_SERVICE_FILES=LOCAL_DELIM_2+SERVICE_FILES+LOCAL_DELIM_2;
  public static final String FILEPATH_COOKIE_FAILED=LOCAL_DELIM_2+SERVICE_PAGES+LOCAL_DELIM_2+FILENAME_COOKIE_FAILED;
  public static final String FILEPATH_CONNECTION_FAILED=LOCAL_DELIM_2+SERVICE_PAGES+LOCAL_DELIM_2+FILENAME_CONNECTION_FAILED;
  public static final String FILEPATH_CONTENT_FAILED=LOCAL_DELIM_2+SERVICE_PAGES+LOCAL_DELIM_2+FILENAME_CONTENT_FAILED;
  public static final String FILEPATH_TEMPLATE_NOT_FOUND=LOCAL_DELIM_2+SERVICE_PAGES+LOCAL_DELIM_2+FILENAME_TEMPLATE_NOT_FOUND;
  public static final String FILEPATH_BLACKLIST_BLOCKING=LOCAL_DELIM_2+SERVICE_PAGES+LOCAL_DELIM_2+FILENAME_BLACKLIST_BLOCKING;
  public static final String FILEPATH_SQL_ERROR=LOCAL_DELIM_2+SERVICE_PAGES+LOCAL_DELIM_2+FILENAME_SQL_ERROR;
  public static final String FILEPATH_SQL_MESSAGE=LOCAL_DELIM_2+SERVICE_PAGES+LOCAL_DELIM_2+FILENAME_SQL_MESSAGE;
  public static final String FILEPATH_WEB_INF=LOCAL_DELIM_2+"WEB-INF";
  //[file extension]
  public static final String EXTENSION_FILE_HTML="html";
  public static final String EXTENSION_FILE_JPG="jpg";
  public static final String EXTENSION_FILE_SQL="sql";
  public static final String EXTENSION_FILE_LOG="log";
  public static final String EXTENSION_FILE_XLS="xls";
  //[database]
  public static final String JDBC_ORACLE="jdbc:oracle:thin:@";
  public static final String JDBC_MYSQL="jdbc:mysql://";//mysql is oracle commercial product now
  public static final String DATABASE_TYPE_ORACLE="oracle";
  public static final String DATABASE_TYPE_MYSQL="mysql";//mysql is oracle commercial product now
  public static final int ERROR_CODE_DATABASE_ORACLE_INVALID_USERNAME_OR_PASSWORD=1017;
  //[count]
  public static final int COUNT_DEFAULT_DATABASE_SESSIONS=1;//live connections for default users(no identified,by primary,seconfary address)
  public static final int COUNT_DATABASE_SESSIONS=64;//live connections for other users(identified by login,password)
  public static final int COUNT_DATABASE_BLACKLIST=3;//number of blacklist enter
  //[length && size]
  public static final int LENGTH_LOG_MESSAGE=256;
  public static final int LENGTH_LOG_SUBMESSAGE=240;
  public static final int SIZE_BUFFER_READ=8192;
  public static final int SIZE_DATA_LEFT_ARRAY=32;
  public static final int SIZE_FREE_MEMORY=32*1024;//32 Κα
  //[sql]
  public static final String SQL_SELECT="select";
  public static final String SQL_SELECT_UPPER_CASE="SELECT";
  public static final String SQL_UPDATE="update";
  public static final String SQL_UPDATE_UPPER_CASE="UPDATE";
  public static final String SQL_DELETE="delete";
  public static final String SQL_DELETE_UPPER_CASE="DELETE";
  public static final String SQL_INSERT="insert";
  public static final String SQL_INSERT_UPPER_CASE="INSERT";
  public static final String SQL_RETURNING="returning";
  public static final String SQL_RETURNING_UPPER_CASE="RETURNING";
  public static final String SQL_INTO="into";
  public static final String SQL_INTO_UPPER_CASE="INTO";
  public static final String SQL_FROM="from";
  public static final String SQL_FROM_UPPER_CASE="FROM";
  public static final int ITEM_TYPE_STRING=1;
  public static final int ITEM_TYPE_BYTES=2;
  public static final int SQL_UNKNOWN_TYPE=0;
  public static final int SQL_ERROR_TYPE=1;
  public static final int SQL_MESSAGE_TYPE=2;
  public static final int SQL_ERROR_MESSAGE_TYPE=3;
  public static final String SQL_ERROR="!";
  public static final String SQL_MESSAGE="@";
  public static final String SQL_ERROR_MESSAGE="!@";
  public static final String SQL_MESSAGE_ERROR="@!";
  public static final String SQL_NUMBER="#";
  //[servlet]
  //servlet param
  public static final String SERVICE_LOGIN="login";
  public static final String SERVICE_LOGIN_="str1";
  public static final String SERVICE_PASSWORD="password";
  public static final String SERVICE_PASSWORD_="str2";
  public static final String SERVICE_DATABASE="database";
  public static final String SERVICE_DATABASE_="str3";
  public static final String SERVICE_TYPE="type";
  public static final String SERVICE_TYPE_="str4";
  public static final String SERVICE_DRIVER="driver";
  public static final String SERVICE_DRIVER_="str5";
  public static final String SERVICE_NAME="name";
  public static final String SERVICE_NAME_="str0";
  public static final String SERVICE_PARAM="param";
  public static final String SERVICE_FILE="file";
  public static final String SERVICE_SQL="sql";//sql file name (*.sql)
  public static final String SERVICE_ROW_COUNT="rowcount";
  public static final String SERVICE_PAGE_ONCLICK="pageonclick";
  public static final String SERVICE_PAGE_HREF="pagehref";
  public static final String SERVICE_PAGE_COUNT="pagecount";
  public static final String SERVICE_PAGE_NUMBER="pagenumber";
  public static final String SERVICE_PAGE_STYLE="pagestyle";
  public static final String SERVICE_PAGE_TYPE="pagetype";
  public static final String SERVICE_PAGE_MARKER="pagemarker";
  public static final String SERVICE_PAGE_PARAM="pageparam";
  public static final String SERVICE_PAGE_TITLE="pagetitle";
  public static final String SERVICE_PAGE_CLASS="pageclass";
  public static final String SERVICE_PAGE_CURRENT_CLASS="pagecurrentclass";
  public static final String SERVICE_PAGE_ANCHOR="pageanchor";
  public static final String SERVICE_PAGE_PREV="pageprev";
  public static final String SERVICE_PAGE_NEXT="pagenext";
  //<prev_folder> <all_folders> <current_folder> <all_folders> <next_folder>
  //public static final String SERVICE_PAGE_FOLDER="pagefolder";//template for current page folder (<font class='button'>%%</font>)
  //public static final String SERVICE_PAGE_PREV_FOLDER="pageprevfolder";//template for prev page folder
  //public static final String SERVICE_PAGE_NEXT_FOLDER="pagenextfolder";//template for next page folder
  //public static final String SERVICE_PAGE_ALL_FOLDERS="pageallfolders";//template for all other page folders
  public static final String SERVICE_SQL_NUMBER="sqlnumber";
  public static final String SERVICE_SQL_IGNORE="sqlignore";
  public static final String SERVICE_COOKIE_NAME="cookiename";
  public static final String SERVICE_COOKIE_VALUE="cookievalue";
  public static final String SERVICE_COOKIE_TIMEOUT="cookietimeout";
  public static final String SERVICE_HIDE_COOKIE_NAME="hidecookiename";
  public static final String SERVICE_HIDE_COOKIE_VALUE="hidecookievalue";
  public static final String SERVICE_HIDE_COOKIE_TIMEOUT="hidecookietimeout";
  public static final String SERVICE_HIDE_COOKIE_KEYWORD="hidecookiekeyword";
  //page style value
  public static final String SERVICE_PAGE_STYLE_NUMERIC="numeric";
  public static final String SERVICE_PAGE_STYLE_SEQUENCE="secuence";
  public static final String SERVICE_PAGE_TYPE_UP="up";
  public static final String SERVICE_PAGE_TYPE_DOWN="down";
  public static final String SERVICE_PAGE_TYPE_UPDOWN="updown";
  //public static final String SERVICE_PAGE_MARKER_STATIC="static";
  //public static final String SERVICE_PAGE_MARKER_FIRST="first";//default
  public static final String SERVICE_TRUE="true";
  public static final String SERVICE_FALSE="false";
  public static final String SERVICE_YES="yes";
  public static final String SERVICE_NO="no";
  //param type value
  public static final String SERVICE_PARAM_TYPE_STRING="string";//String
  public static final String SERVICE_PARAM_TYPE_BUFFER="buffer";//byte[]
  //[substitutions]
  public static final String PERCENT_PARAM="%param";
  public static final String PERCENT_NAME="%name";
  public static final String PERCENT_VERSION="%version";
  public static final String PERCENT_LOGIN="%login";
  public static final String PERCENT_PASSWORD="%password";
  public static final String PERCENT_DATABASE="%database";
  public static final String PERCENT_REMOTEADDR="%remoteaddr";
  public static final String PERCENT_REMOTEHOST="%remotehost";
  public static final String PERCENT_SESSIONID="%sessionid";
  public static final String PERCENT_SERVICETRASH="%servicetrash";
  public static final String PERCENT_WRITEFILE="%writefile";
  public static final String PERCENT_WRITEDATA="%writedata";
  //[substitutions direction]
  //public static final String PERCENT_BEGIN_REPLACE="%begin_replace";
  //public static final String PERCENT_END_REPLACE="%end_replace";
  public static final String PERCENT_BEGIN_ENCODE="%begin_encode";
  public static final String PERCENT_END_ENCODE="%end_encode";
  public static final String PERCENT_BEGIN_QUOT="%begin_quot";
  public static final String PERCENT_END_QUOT="%end_quot";
  public static final String PERCENT_BEGIN_HTML="%begin_html";
  public static final String PERCENT_END_HTML="%end_html";
  public static final String PERCENT_BEGIN_NOT_QUOT="%begin_not_quot";
  public static final String PERCENT_END_NOT_QUOT="%end_not_quot";
  //[substitutions conditions]
  public static final String PERCENT_IF="%if";
  public static final String PERCENT_ELSEIF="%elseif";
  public static final String PERCENT_ENDIF="%endif";
  //[set param]
  public static final String PERCENT_SET_PARAM="%set_param";
  public static final String PERCENT_INVOKE_METHOD="%invoke_method";
  //[invoke]
  public static final String INVOKE_METHOD="@";
  public static final String THIS="this";
  //[error and message]
  public static final String PERCENT_SQL_ERROR="%sql_error";
  public static final String PERCENT_SQL_MESSAGE="%sql_message";
  //[content]
  public static final String CONTENT_TYPE="Content-Type";
  public static final String CONTENT_DISPOSITION="Content-Disposition";
  public static final String CONTENT_DISPOSITION_FILENAME="filename";
  public static final String CONTENT_DISPOSITION_NAME="name";
  public static final String CONTENT_TYPE_TEXT_HTML="text/html; charset=windows-1251";//default local content type
  public static final String CONTENT_TYPE_IMAGE_PJPEG="image/pjpeg";
  public static final String CONTENT_TYPE_APP_XWWW="application/x-www-form-urlencoded";
  public static final String CONTENT_TYPE_APP_JSON="application/json";
  //[delim]
  public static final String DELIM_DOG="@";
  public static final String DELIM_DATABASE=DELIM_DOG;
  public static final String DELIM_SUBVALUES=":";
  public static final String DELIM_HOST_PORT=DELIM_SUBVALUES;
  public static final String DELIM_USER_PASSWORD=DELIM_SUBVALUES;
  public static final String DELIM_XLS_POS_COPY="!";
  //[message]
  public static final String MESSAGE_DELIM_VALUE="=";
  public static final String MESSAGE_DELIM_SUBVALUES="|";
  public static final String MESSAGE_DELIM_HOST_PORT=":";
  public static final String MESSAGE_DELIM_USER_PASSWORD=":";
  public static final String MESSAGE_INFO="|Info|";
  public static final String MESSAGE_WARNING="|Warning|";
  public static final String MESSAGE_ERROR="|Error|";
  public static final String MESSAGE_DEBUG="|Debug|";
  //sys log
  public static final String SYS_LOG_MESSAGE_DELIM=": ";
  public static final String SYS_LOG_MESSAGE_INFO="LOG_INFO";
  public static final String SYS_LOG_MESSAGE_WARNING="LOG_WARNING";
  public static final String SYS_LOG_MESSAGE_ERROR="LOG_ERR";
  public static final String SYS_LOG_MESSAGE_DEBUG="LOG_DEBUG";
  public static final int SYS_LOG_REMOTE_PORT=514;
  public static final int SYS_LOG_LOCAL_PORT=10514;
  //message type
  public static final int MESSAGE_TYPE_INFO=1;
  public static final int MESSAGE_TYPE_WARNING=2;
  public static final int MESSAGE_TYPE_ERROR=3;
  public static final int MESSAGE_TYPE_DEBUG=4;
  //[info id]
  public static final int INFO_START_ID=0;
  public static final int INFO_FINISH_ID=99;
  public static final int INFO_DEVELOPER_DESCRIPTION=0;
  public static final int INFO_SERVER_DESCRIPTION=1;
  public static final int INFO_SERVER_LOADING=2;
  public static final int INFO_SERVER_UNLOADING=3;
  public static final int INFO_SERVER_SHUTDOWN=4;
  public static final int INFO_SERVER_ACTIVATED=5;
  public static final int INFO_SERVER_DEACTIVATED=6;
  public static final int INFO_DATABASE_CONNECTION_CLOSED=10;
  public static final int INFO_DATABASE_CONNECTION_ESTABLISHED=11;
  public static final int INFO_DATABASE_RECONNECTION=12;
  public static final int INFO_SERVICE_PARAM_REGISTERED=13;
  public static final int INFO_SERVICE_REGISTERED=14;
  public static final int INFO_SERVICE_CONTENT_REGISTERED=15;
  public static final int INFO_SESSION_ACTIVATED=21;
  public static final int INFO_SESSION_DEACTIVATED=31;
  public static final int INFO_INITIAL_ACTIVATED=41;
  public static final int INFO_LOG_ACTIVATED=42;
  public static final int INFO_DATABASE_USERS_ACTIVATED=43;
  public static final int INFO_DATABASE_BLACKLIST_ACTIVATED=44;
  public static final int INFO_WRITE_REQUEST=51;
  public static final int INFO_UPDATE_REQUEST=52;
  public static final int INFO_READ_RESPONSE=53;
  public static final int INFO_READ_DATA=54;
  public static final int INFO_WRITE_FILE=55;
  public static final int INFO_COOKIE_CREATED=56;
  public static final int INFO_COOKIE_ESTABLISHED=57;
  public static final int INFO_COOKIE_FAILED=58;
  public static final int INFO_DATABASE_SESSION_SAVED=90;
  public static final int INFO_DATABASE_SESSION_REMOVED=91;
  public static final int INFO_SAVED_IN_TIMEOUT_BLACKLIST=92;
  public static final int INFO_REMOVED_FROM_TIMEOUT_BLACKLIST=93;
  public static final int INFO_SERVICE_MESSAGE=94;//service information after finished (activated by invoke_method)
  public static final int INFO_SERVICE_STARTED=95;//service started (activated by invoke_method)
  public static final int INFO_SQL_MESSAGE=96;
  //[warning id]
  public static final int WARNING_START_ID=100;
  public static final int WARNING_FINISH_ID=999;
  public static final int WARNING_INVALID_LOCAL_ADDRESS=101;
  public static final int WARNING_INVALID_LOCAL_HOST=102;
  public static final int WARNING_INVALID_LOCAL_PORT=103;
  public static final int WARNING_ADDRESS_NOT_FOUND=104;
  public static final int WARNING_SESSION_FAILED=105;
  public static final int WARNING_SESSION_WAITING=106;
  public static final int WARNING_SESSION_EXCEPTION=107;
  public static final int WARNING_RESPONSE_NOT_FOUND=211;
  public static final int WARNING_DATA_NOT_FOUND=212;
  public static final int WARNING_DATABASE_CONNECTION_NOT_ESTABLISHED=900;
  public static final int WARNING_DATABASE_CONNECTION_WAITING=901;
  //[error id]
  public static final int ERROR_START_ID=1000;
  public static final int ERROR_FINISH_ID=1999;
  public static final int ERROR_INITIAL_NOT_FOUND=1001;
  public static final int ERROR_DATABASE_CONNECTION_FAILED=1002;
  public static final int ERROR_SERVICE_REGISTRATION_FAILED=1003;
  public static final int ERROR_LOG_OPEN_FAILED=1004;
  public static final int ERROR_LOG_WRITE_FAILED=1005;
  public static final int ERROR_DRIVER_NOT_FOUND=1010;
  public static final int ERROR_SERVICE_NOT_FOUND=1011;
  public static final int ERROR_INVALID_FORMAT=1012;
  public static final int ERROR_SQL_QUERY_FAILED=1013;//ERROR_REQUEST_FAILED
  public static final int ERROR_INVALID_REQUEST=1014;
  public static final int ERROR_CONTENT_FAILED=1015;
  public static final int ERROR_SQL_MESSAGE=1016;
  public static final int ERROR_SESSION_FAILED=1017;
  public static final int ERROR_BLACKLIST_BLOCKING_BY_ADDRESS=1018;
  public static final int ERROR_BLACKLIST_BLOCKING_BY_USERNAME=1019;
  public static final int ERROR_TIMEOUT_BLACKLIST_BLOCKING=1020;
  //[debug id]
  public static final int DEBUG_START_ID=10000;
  public static final int DEBUG_FINISH_ID=10999;
  //public static final int DEBUG_CONSOLE_WRITE_REQUEST=10001;
  public static final int DEBUG_PARSE_ATTRIBUTE_VALUE=10100;
  public static final int DEBUG_PARSE_ATTRIBUTES=10101;
  //[debug sessions]
  public static final String DEBUG_NET_LISTENER_POINT=".NET_LISTENER_POINT";
  public static final String DEBUG_SESSIONS_CONTROL_POINT=".SESSIONS_CONTROL_POINT";
  //[session type]
  public static final int SESSION_TYPE_UNKNOWN=0;
  public static final int SESSION_TYPE_SERVLET=1;
  public static final int SESSION_TYPE_NET_LISTENER=2;
  public static final int SESSION_TYPE_USER_CONNECTION=3;
  public static final int SESSION_TYPE_SESSIONS_CONTROL=4;
  public static final int SESSION_TYPE_SERVICES_CONTROL=5;
  //session type->live database connection
  public static final int SESSION_TYPE_DEFAULT_DATABASE_CONNECTION=10;
  public static final int SESSION_TYPE_LOG_DATABASE_CONNECTION=11;
  public static final int SESSION_TYPE_USER_DATABASE_CONNECTION=12;
  //[session name]
  public static final String SESSION_NUMBER="#";
  public static final String SESSION_NAME_SERVLET="<skyDrakkar> servlet";
  public static final String SESSION_NAME_NET_LISTENER="Net Listener";
  public static final String SESSION_NAME_USER_CONNECTION="User connection";
  public static final String SESSION_NAME_SESSIONS_CONTROL="Sessions control";
  public static final String SESSION_NAME_SERVICES_CONTROL="Services control";
  public static final String SESSION_NAME_LOG_DATABASE_CONNECTION="Log database connection";
  public static final String SESSION_NAME_DEFAULT_DATABASE_CONNECTION="Default database connection";
  public static final String SESSION_NAME_USER_DATABASE_CONNECTION="User database connection";
  //[session key]
  public static final String SESSION_KEY_LOG_DATABASE_CONNECTION="log";
  public static final String SESSION_KEY_DEFAULT_DATABASE_CONNECTION="def";
  //[ini]
  //ini group index
  public static final int IGI_UNKNOWN=0;
  public static final int IGI_LOCAL=1;
  public static final int IGI_DATABASE=2;
  public static final int IGI_SERVICE=3;
  //ini group name
  public static final String IGN_LOCAL="Local";
  public static final String IGN_DATABASE="Database";
  public static final String IGN_SERVICE="Service";
  //ini param name
  //[local]
  public static final String IPN_LOCAL_CODEPAGE="Codepage";
  public static final String IPN_LOCAL_CONTENT_TYPE="ContentType";
  public static final String IPN_LOCAL_ADDRESS="Address";
  public static final String IPN_LOCAL_USER="User";
  public static final String IPN_LOCAL_PASSWORD="Password";
  public static final String IPN_LOCAL_LOG="Log";
  public static final String IPN_LOCAL_DEBUG="Debug";
  public static final String IPN_LOCAL_TIMEOUT="Timeout";
  //[database]
  public static final String IPN_DATABASE_PRIMARY_ADDRESS="PrimaryAddress";
  public static final String IPN_DATABASE_SECONDARY_ADDRESS="SecondaryAddress";
  public static final String IPN_DATABASE_LOG="Log";
  public static final String IPN_DATABASE_DRIVER="Driver";
  public static final String IPN_DATABASE_COOKIE="Cookie";
  public static final String IPN_DATABASE_TIMEOUT="Timeout";
  public static final String IPN_DATABASE_PASSWORD="Password";
  public static final String IPN_DATABASE_USER="User";
  public static final String IPN_DATABASE_USERS="Users";
  public static final String IPN_DATABASE_SESSIONS_COUNT="SessionsCount";
  public static final String IPN_DATABASE_SESSIONS_TIMEOUT="SessionsTimeout";
  public static final String IPN_DATABASE_BLACKLIST="BlackList";
  public static final String IPN_DATABASE_BLACKLIST_COUNT="BlackListCount";
  public static final String IPN_DATABASE_BLACKLIST_TIMEOUT="BlackListTimeout";
  public static final String IPN_DATABASE_VALID_CONNECTION="ValidConnection";
  //[service]
  public static final String IPN_SERVICE_TEMPLATES="Templates";
  public static final String IPN_SERVICE_PAGES="Pages";
  public static final String IPN_SERVICE_TRASH="Trash";
  public static final String IPN_SERVICE_FILES="Files";
  public static final String IPN_SERVICE_START="Start";
  public static final String IPN_SERVICE_INVOKE="Invoke";
  //ini param value
  //[ini code]
  public static final String INI_GROUP_NAME_OPEN="[";
  public static final String INI_GROUP_NAME_CLOSE="]";
  public static final String INI_DELIM_DEFAULT=":";
  public static final String INI_DELIM_HOST_PORT=":";
  public static final String INI_DELIM_USER_PASSWORD=":";
  public static final String INI_DELIM_PARAM_VALUE="=";
  public static final String INI_DELIM_IP_ADDRESS=".";
  public static final String INI_SLASH="/";
  public static final String INI_DOUBLE_SLASH="//";
  public static final String INI_ANY="*";
  //[blacklist ini]
  public static final String BLI_PARAM_USER="User";
  public static final String BLI_PARAM_ADDRESS="Address";
  //[pagedata files]
  public static final String PAGEDATA_FILE_A_HREF="a_href.html";
  public static final String PAGEDATA_FILE_IMG_SRC="img_src.html";
  public static final String PAGEDATA_FILE_INPUT_NAME="input_name.html";
  public static final String PAGEDATA_FILE_INPUT_VALUE="input_value.html";
  public static final String PAGEDATA_FILE_SELECT_OPTION="select_option.html";
  public static final String PAGEDATA_FILE_SELECTED="selected.html";
  public static final String PAGEDATA_FILE_TABLE_TR="table_tr.html";
  public static final String PAGEDATA_FILE_TITLE="title.html";

  //[timeout]
  public static final int TIMEOUT_SESSION_CLOSE_WAIT=1000;//1 sec
  public static final int TIMEOUT_SESSION_SLEEP=1000;//1 sec
  public static final int TIMEOUT_SERVER_SOCKET_LISTEN=1000;//1 sec
  public static final int TIMEOUT_SOCKET_LISTEN=1000;//1 sec
  public static final int TIMEOUT_DATABASE_FILES=60000;//60 sec
  public static final int TIMEOUT_DATA_WAIT=1;//1 milisec
  public static final int TIMEOUT_DATABASE_COOKIE=86400000;//60000(milisec)*60(min)*24(hour)=86400000(day)
  public static final int TIMEOUT_DATABASE_SESSIONS=60000;//1 min
  public static final int TIMEOUT_DATABASE_BLACKLIST=3600000;//30 min
  public static final int TIMEOUT_DATABASE_CONNECTION_IS_VALID=1000;//1 sec
  //[default key]
  public static final String HIDE_COOKIE_KEYWORD="01Aa02Bb03Cc04Dd";
}