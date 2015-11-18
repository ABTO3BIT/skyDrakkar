//class version 1.4
package tools;
//--------------------------------HtmlInterface------------------------------------//
public interface HtmlInterface
{
  //[html]
  //html element type
  public static final int HTML_TYPE_SELECT=0;//<select ...>
  public static final int HTML_TYPE_DROPDOWN_LIST=1;//<select ...>
  public static final int HTML_TYPE_LISTBOX=2;//<select ...>
  public static final int HTML_TYPE_INPUT=10;//<input ...>
  public static final int HTML_TYPE_TEXT=11;//<input ...>
  public static final int HTML_TYPE_PASSWORD=12;//<input ...>
  public static final int HTML_TYPE_HIDDEN=13;//<input ...>
  public static final int HTML_TYPE_IMAGE=14;//<input ...>
  public static final int HTML_TYPE_CHECKBOX=15;//<input ...>
  public static final int HTML_TYPE_RADIO=16;//<input ...>
  public static final int HTML_TYPE_FILE=17;//<input ...>
  public static final int HTML_TYPE_BUTTON=18;//<input ...>
  public static final int HTML_TYPE_SUBMIT=19;//<input ...>
  public static final int HTML_TYPE_RESET=20;//<input ...>
  public static final int HTML_TYPE_TEXTAREA=21;//<textarea ...>
  public static final int HTML_TYPE_TABLE=22;//<table ...>
  public static final int HTML_TYPE_IMG=23;//<img ...>
  public static final int HTML_TYPE_A=24;//<a ...>
  public static final int HTML_TYPE_SQL=31;//<sql ...>
  public static final int HTML_TYPE_FORM=41;//<form ...>
  public static final int HTML_TYPE_HTML=91;//<html ...>
  //html element subtype
  public static final int HTML_SUBTYPE_OPTION=101;
  public static final int HTML_SUBTYPE_TR=102;
  public static final int HTML_SUBTYPE_TH=103;
  public static final int HTML_SUBTYPE_TD=104;
  public static final int HTML_SUBTYPE_DIV=105;
  public static final int HTML_SUBTYPE_B=106;
  //html name
  public static final String HTML_ELEMENT_COMMENT_START="!--";//start comment
  public static final String HTML_ELEMENT_COMMENT_FINISH="--";//finish comment
  public static final String HTML_ELEMENT_ID="id";
  public static final String HTML_ELEMENT_SCRIPT="script";//script element
  public static final String HTML_ELEMENT_HTML="html";//html
  public static final String HTML_ELEMENT_FORM="form";//form
  public static final String HTML_ELEMENT_FORM_NAME="name";//form element attribute name
  public static final String HTML_ELEMENT_FORM_ACTION="action";//form element attribute action
  public static final String HTML_ELEMENT_FORM_METHOD="method";//form element attribute method
  public static final String HTML_ELEMENT_FORM_METHOD_GET="get";//form element attribute value
  public static final String HTML_ELEMENT_SQL="sql";//element || attribute
  public static final String HTML_ELEMENT_SQL_FILE="file";//sql attribute
  public static final String HTML_ELEMENT_SQL_REF="ref";//sql attribute
  public static final String HTML_ELEMENT_SQL_FETCH="fetch";//sql attribute
  public static final String HTML_ELEMENT_SELECT="select";//element select
  public static final String HTML_ELEMENT_SELECT_OPTION="option";//select element <option>
  public static final String HTML_ELEMENT_SELECT_OPTION_VALUE="value";//select element attribute value
  public static final String HTML_ELEMENT_SELECT_OPTION_SELECTED="selected";//select element attribute selected
  public static final String HTML_ELEMENT_SELECT_SIZE="size";//select element attribute size
  public static final String HTML_ELEMENT_INPUT="input";//element input
  public static final String HTML_ELEMENT_INPUT_NAME="name";//attribute
  public static final String HTML_ELEMENT_INPUT_TYPE="type";//attribute
  public static final String HTML_ELEMENT_INPUT_SIZE="size";//attribute
  public static final String HTML_ELEMENT_INPUT_VALUE="value";//attribute
  public static final String HTML_ELEMENT_INPUT_TYPE_TEXT="text";//attribute value
  public static final String HTML_ELEMENT_INPUT_TYPE_PASSWORD="password";//attribute value
  public static final String HTML_ELEMENT_INPUT_TYPE_HIDDEN="hidden";//attribute value
  public static final String HTML_ELEMENT_INPUT_TYPE_IMAGE="image";//attribute value
  public static final String HTML_ELEMENT_INPUT_TYPE_CHECKBOX="checkbox";//attribute value
  public static final String HTML_ELEMENT_INPUT_TYPE_CHECKBOX_CHECKED="checked";//attribute
  public static final String HTML_ELEMENT_INPUT_TYPE_RADIO="radio";//attribute value
  public static final String HTML_ELEMENT_INPUT_TYPE_FILE="file";//attribute value
  public static final String HTML_ELEMENT_INPUT_TYPE_BUTTON="button";//attribute value
  public static final String HTML_ELEMENT_INPUT_TYPE_SUBMIT="submit";//attribute value
  public static final String HTML_ELEMENT_INPUT_TYPE_RESET="reset";//attribute value
  public static final String HTML_ELEMENT_TEXTAREA="textarea";//element textarea
  public static final String HTML_ELEMENT_TABLE="table";//element table
  public static final String HTML_ELEMENT_TABLE_VALIGN="valign";//table attribute valign
  public static final String HTML_ELEMENT_TABLE_HALIGN="halign";//table attribute halign
  public static final String HTML_ELEMENT_TABLE_VALIGN_TOP="top";//table attribute valign value top
  public static final String HTML_ELEMENT_TABLE_HALIGN_TOP="top";//table attribute halign value top
  public static final String HTML_ELEMENT_TABLE_VALIGN_CENTER="center";//table attribute valign value center
  public static final String HTML_ELEMENT_TABLE_HALIGN_CENTER="center";//table attribute halign value center
  public static final String HTML_ELEMENT_TABLE_TR="tr";//table element <tr>
  public static final String HTML_ELEMENT_TABLE_TH="th";//table element <th>
  public static final String HTML_ELEMENT_TABLE_TD="td";//table element <tr>
  public static final String HTML_ELEMENT_TABLE_COLS="cols";//table element <cols>
  public static final String HTML_ELEMENT_TABLE_CLASS="class";//table attribute class
  public static final String HTML_ELEMENT_TABLE_PAGE="page";//table attribute page
  public static final String HTML_ELEMENT_TABLE_ID="id";//table attribute id
  public static final String HTML_ELEMENT_TABLE_DIV="div";//table element <div>
  public static final String HTML_ELEMENT_TABLE_B="b";//table element <b>
  public static final String HTML_ELEMENT_TABLE_COLSPAN="colspan";//table attribute colspan
  public static final String HTML_ELEMENT_TABLE_ROWSPAN="rowspan";//table attribute rowspan
  public static final String HTML_ELEMENT_IMG="img";//element img
  public static final String HTML_ELEMENT_IMG_SRC="src";//img attribute src
  public static final String HTML_ELEMENT_IMG_ALT="alt";//img attribute alt
  public static final String HTML_ELEMENT_BR="br";//element br
  public static final String HTML_ELEMENT_A="a";//element a
  public static final String HTML_ELEMENT_A_HREF="href";//a attribute href
  public static final String HTML_ELEMENT_A_CLASS="class";//a attribute class
  public static final String HTML_ELEMENT_A_TITLE="title";//a attribute title
  public static final String HTML_ELEMENT_A_ONCLICK="onclick";//a attribute onclick
  public static final String HTML_ELEMENT_CLASS="class";//attribute class
  public static final String HTML_ELEMENT_TITLE="title";//attribute title
  public static final String HTML_ELEMENT_FONT="font";//element font
  public static final String HTML_ELEMENT_FONT_CLASS="class";//font attribute class
}