skyDrakkar framework
--------------------

skyDrakkar RESTful Web Services framework is a opensource framework for developing in %CENT language

skyDrakkar provides it's own %CENT API for RESTful Web services creation in html text files.
Easy connects to databases via jdbc drivers, additional functionallity easy provides in personal Java classes.

Goals of skyDrakkar project can be summarized in the following points:

- Provide %CENT APIs to create powerful RESTful Web services with personal classes in Java.
- Make it easy to create RESTful Web services without compilations and reloads.

skyDrakkar Web service "get_language.html" example:

http request: http://localhost/service/start?name=/json/get_language&user=customer&rows=1

http response: {"results":[{"session_id":"1","language":"ENGLISH","active":"1"}

html text file example
----------------------

    %writedata/json/param0%writedata
    %writedata/json/param1%writedata
    %if "param6" %if

      %set_param7=@this.getExtra("offset")%set_param
      %set_param8=@this.getExtra("rows")%set_param
      %if "param8" %if
        %if "param7" %if
          %invoke_methodthis.setParam(8,"LIMIT %param7%param,%param8%param")%invoke_method
        %elseif%elseif
          %invoke_methodthis.setParam(8,"LIMIT %param8%param")%invoke_method
        %endif%endif
      %endif%endif

      %begin_html%begin_html
      %set_param20=@this.getStringList("SELECT CONCAT('{',CHAR(34),'session_id',CHAR(34),':',CHAR(34),'%sessionid%sessionid',CHAR(34),',',
        CHAR(34),'language',CHAR(34),':',CHAR(34),REPLACE(REPLACE(language,CHAR(39),'&#39;'),CHAR(34),'&quot;'),CHAR(34),',',
        CHAR(34),'active',CHAR(34),':',CHAR(34),active,CHAR(34),'},')
        FROM language")%set_param
      %end_html%end_html

      %if "param20!=''" %if
        {"results":[%param20%param{}],"status":"SUCCESS"}
      %elseif%elseif
        %set_param21=@getServletParam().getSQLReturnType().toString()%set_param
        %if "param21=1" %if
          %set_param21=@getServletParam().getSQLErrorMessage().replaceAll("\x22","")%set_param
        %elseif%elseif
          %invoke_methodthis.setParam(21,"")%invoke_method
        %endif%endif
        {"results":[{"session_id":"%sessionid%sessionid","message":"Data not found","database_message":"%param21%param"}],"status":"ERROR"}
      %endif%endif
    %elseif%elseif
      {"results":[{"session_id":"%sessionid%sessionid","message":"Invalid username or password"}],"status":"ERROR"}
    %endif%endif