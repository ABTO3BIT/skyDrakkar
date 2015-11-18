//class version 1.1
package tools;
import java.util.Vector;
import java.util.StringTokenizer;
//------------------------------------HtmlParse------------------------------------//
public class HtmlParse implements Interface,HtmlInterface
{  
  public void clearAllList(){}
  public void parse(byte[] data,int list_type){}
  public int getType_(Vector v)
  {
    int ret_val=0;
    if(v==null||v.size()==0)return ret_val;
    String str=(String)v.get(0);
    if(str.startsWith(HTML_ELEMENT_SQL))ret_val=HTML_TYPE_SQL;
    else if(str.startsWith(HTML_ELEMENT_SELECT)){
      //boolean name_size=false;//seek subtype of type select (not used subtypes)
      //for(int i=1;i<v.size();i++){//"size" substring search
      //  if(((String)v.get(i)).startsWith(HTML_ELEMENT_SELECT_SIZE)){name_size=true;break;}
      //}
      //if(!name_size)ret_val=HTML_TYPE_DROPDOWN_LIST;
      //else ret_val=HTML_TYPE_LISTBOX;
      ret_val=HTML_TYPE_SELECT;
    }
    else if(str.startsWith(HTML_ELEMENT_INPUT)){
      for(int i=1;i<v.size();i++){
        String s=(String)v.get(i);
        if(s.startsWith(HTML_ELEMENT_INPUT_TYPE)){//seek input type
          String pv=Convert.getValue(s);
          if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_TEXT))ret_val=HTML_TYPE_TEXT;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_PASSWORD))ret_val=HTML_TYPE_PASSWORD;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_HIDDEN))ret_val=HTML_TYPE_HIDDEN;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_IMAGE))ret_val=HTML_TYPE_IMAGE;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_CHECKBOX))ret_val=HTML_TYPE_CHECKBOX;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_RADIO))ret_val=HTML_TYPE_RADIO;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_FILE))ret_val=HTML_TYPE_FILE;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_BUTTON))ret_val=HTML_TYPE_BUTTON;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_SUBMIT))ret_val=HTML_TYPE_SUBMIT;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_RESET))ret_val=HTML_TYPE_RESET;
        }
      }
    }
    else if(str.startsWith(HTML_ELEMENT_TEXTAREA))ret_val=HTML_TYPE_TEXTAREA;
    else if(str.startsWith(HTML_ELEMENT_TABLE))ret_val=HTML_TYPE_TABLE;
    else if(str.startsWith(HTML_ELEMENT_IMG))ret_val=HTML_TYPE_IMG;
    else if(str.startsWith(HTML_ELEMENT_A))ret_val=HTML_TYPE_A;
    else if(str.startsWith(HTML_ELEMENT_SELECT_OPTION))ret_val=HTML_SUBTYPE_OPTION;
    else if(str.startsWith(HTML_ELEMENT_TABLE_TR))ret_val=HTML_SUBTYPE_TR;
    else if(str.startsWith(HTML_ELEMENT_TABLE_TH))ret_val=HTML_SUBTYPE_TH;
    else if(str.startsWith(HTML_ELEMENT_TABLE_TD))ret_val=HTML_SUBTYPE_TD;
    return ret_val;
  }  
  public Vector getFields_(String data)
  {
    Vector ret_val=new Vector();
    String str,str2;
    StringTokenizer s=new StringTokenizer(data,SPACE);    
    while(s.hasMoreTokens()){
      str=s.nextToken().trim();
      //if exist left param and right value, as param="value" or param=value
      int i=str.indexOf(CODE_EQUAL);//seek "="
      if(i==-1||i==str.length()-1);//not or end
      else if(str.charAt(++i)==CODE_DOUBLE_UPPER){
        while(!str.endsWith(DOUBLE_UPPER)&&s.hasMoreTokens()){str2=s.nextToken().trim();str+=SPACE+str2;}
        str=str.replaceAll(DOUBLE_UPPER,EMPTY);//trim "
        str=str.trim();        
      }
      ret_val.add(str);
    }
    return ret_val;
  }
  public String getSQL_(Vector v)
  {
    String ret_val=null,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_SQL))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }
  public String getSQLFile_(Vector v)
  {
    String ret_val=null,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_SQL_FILE))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }
  public String getSQLRef_(Vector v)
  {
    String ret_val=null,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_SQL_REF))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }
  public String getTableCols_(Vector v)
  {
    String ret_val=null,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_TABLE_COLS))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }
  public String getTablePage_(Vector v)
  {
    String ret_val=null,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_TABLE_PAGE))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }    
  //--------------------------------------------------------------------------
  public int getType(Vector v)//get html tag type
  {
    int ret_val=0;
    if(v==null||v.size()==0)return ret_val;
    String str=(String)v.get(0);
    if(str.startsWith(HTML_ELEMENT_HTML))ret_val=HTML_TYPE_HTML;
    else if(str.startsWith(HTML_ELEMENT_FORM))ret_val=HTML_TYPE_FORM;
    else if(str.startsWith(HTML_ELEMENT_SQL))ret_val=HTML_TYPE_SQL;
    else if(str.startsWith(HTML_ELEMENT_SELECT)){//"size" substring search
      return ret_val=HTML_TYPE_SELECT;
      //boolean name_size=false;
      //for(int i=1;i<v.size();i++){
      //  if(((String)v.get(i)).startsWith(HTML_ELEMENT_SELECT_SIZE))name_size=true;
      //}
      //if(name_size)ret_val=HTML_TYPE_LISTBOX;else ret_val=HTML_TYPE_DROPDOWN_LIST;*/
    }
    else if(str.startsWith(HTML_ELEMENT_INPUT)){
      ret_val=HTML_TYPE_INPUT;//if type not found
      for(int i=1;i<v.size();i++){
        String s=(String)v.get(i);
        if(s.startsWith(HTML_ELEMENT_INPUT_TYPE)){//seek input type
          String pv=Convert.getValue(s);
          if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_TEXT))ret_val=HTML_TYPE_TEXT;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_PASSWORD))ret_val=HTML_TYPE_PASSWORD;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_HIDDEN))ret_val=HTML_TYPE_HIDDEN;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_IMAGE))ret_val=HTML_TYPE_IMAGE;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_CHECKBOX))ret_val=HTML_TYPE_CHECKBOX;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_RADIO))ret_val=HTML_TYPE_RADIO;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_FILE))ret_val=HTML_TYPE_FILE;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_BUTTON))ret_val=HTML_TYPE_BUTTON;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_SUBMIT))ret_val=HTML_TYPE_SUBMIT;
          else if(pv.equalsIgnoreCase(HTML_ELEMENT_INPUT_TYPE_RESET))ret_val=HTML_TYPE_RESET;
        }
      }
    }
    else if(str.startsWith(HTML_ELEMENT_TEXTAREA))ret_val=HTML_TYPE_TEXTAREA;
    else if(str.startsWith(HTML_ELEMENT_TABLE))ret_val=HTML_TYPE_TABLE;
    else if(str.startsWith(HTML_ELEMENT_IMG))ret_val=HTML_TYPE_IMG;
    else if(str.startsWith(HTML_ELEMENT_A))ret_val=HTML_TYPE_A;
    else if(str.startsWith(HTML_ELEMENT_SELECT_OPTION))ret_val=HTML_SUBTYPE_OPTION;
    else if(str.startsWith(HTML_ELEMENT_TABLE_TR))ret_val=HTML_SUBTYPE_TR;
    else if(str.startsWith(HTML_ELEMENT_TABLE_TH))ret_val=HTML_SUBTYPE_TH;
    else if(str.startsWith(HTML_ELEMENT_TABLE_TD))ret_val=HTML_SUBTYPE_TD;
    else if(str.startsWith(HTML_ELEMENT_TABLE_DIV))ret_val=HTML_SUBTYPE_DIV;
    else if(str.startsWith(HTML_ELEMENT_TABLE_B))ret_val=HTML_SUBTYPE_B;
    return ret_val;
  }  
  public Vector getFields(String data)//get html tag fields
  {
    Vector ret_val=new Vector();    
    String str,str2;
    StringTokenizer s=new StringTokenizer(data,SPACE);
    while(s.hasMoreTokens()) {
      str=s.nextToken().trim();
      //if exist left param and right value, as param="value" or param='value' or param=value
      int i=str.indexOf(CODE_EQUAL);
      if(str.indexOf(CODE_DOUBLE_UPPER)==i+1){//param="value"
        int i2=str.indexOf(CODE_DOUBLE_UPPER,i+2);
        if(i2>=i+2&&i2<str.length()-1)str=str.substring(0,i2);//param="value1"param="value2"->param=value1
        else while(!str.endsWith(DOUBLE_UPPER)&&s.hasMoreTokens()){str2=s.nextToken().trim();str+=SPACE+str2;}//param="value1 value2"->param=value1 value2
        str=str.replaceAll(DOUBLE_UPPER,EMPTY).replaceAll(UPPER,EMPTY).trim();//trim "
      }
      else if(str.indexOf(CODE_UPPER)==i+1){//param='value'
        int i2=str.indexOf(CODE_UPPER,i+2);
        if(i2>=i+2&&i2<str.length()-1)str=str.substring(0,i2);//param='value1'param='value2'->param=value1
        else while(!str.endsWith(UPPER)&&s.hasMoreTokens()){str2=s.nextToken().trim();str+=SPACE+str2;}//param='value1 value2'->param=value1 value2
        str=str.replaceAll(UPPER,EMPTY).trim();//trim '
      }
      ret_val.add(str);//param=value
    }
    return ret_val;
  }  
  public String getClass(Vector v)//get a class value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_CLASS))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }  
  public String getTitle(Vector v)//get a title value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_TITLE))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }  
  public String getSQL(Vector v)//get SQL value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_SQL))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }
  public String getSQLFile(Vector v)//get SQL file value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_SQL_FILE))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }
  public String getSQLRef(Vector v)//get SQL ref value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_SQL_REF))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }
  public String getSQLFetch(Vector v)//get SQL fetch value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_SQL_FETCH))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }
  public String getAHref(Vector v)//get a href value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_A_HREF))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }  
  public String getAClass(Vector v)//get a class value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_A_CLASS))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }  
  public String getATitle(Vector v)//get a title value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_A_TITLE))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }  
  public String getInputName(Vector v)//get input name value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_INPUT_NAME))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }  
  public String getInputValue(Vector v)//get input value value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_INPUT_VALUE))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }  
  public String getFormName(Vector v)//get form name value
  {
    String ret_val=EMPTY,str;               
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_FORM_NAME))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }  
  public String getFormAction(Vector v)//get form action value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_FORM_ACTION))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }
  public String getTableCols(Vector v)
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_TABLE_COLS))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }
  public String getTableClass(Vector v)//get table class value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_TABLE_CLASS))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }
  public String getTableId(Vector v)//get table id value
  {
    String ret_val=EMPTY,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_TABLE_ID))ret_val=Convert.getValue(str);
    }
    return ret_val;
  }
  public String getSelected(Vector v)//get select selected value (<select ... selected="1">)
  {
    String ret_val=null,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_SELECT_OPTION_SELECTED)){ret_val=Convert.getValue(str);break;}
    }
    return ret_val;
  }
  /*public String getValue(Vector v)//get select value (<select ... value="SELECT ...">)
  {
    String ret_val=null,str;
    if(v==null||v.size()==0)return ret_val;
    for(int i=0;i<v.size();i++){
      str=(String)v.get(i);
      if(str.startsWith(HTML_ELEMENT_SELECT_OPTION_VALUE)){ret_val=Convert.getValue(str);break;}
    }
    return ret_val;
  }*/
}