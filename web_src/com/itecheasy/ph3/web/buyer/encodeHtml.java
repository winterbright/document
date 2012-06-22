package com.itecheasy.ph3.web.buyer;

public class encodeHtml

{
	private String html = "<html> inp&ut html";
	public String getHtml()
	{
		return HTMLEncode(html);
	}

	public void setHtml(String html)
	{
		this.html = html;
	}

	private static String HTMLEncode(String text){ 
        if (text==null) 
        return ""; 
        StringBuffer results = null; 
        char[] orig = null; 
        int beg = 0,len=text.length(); 
        for (int i=0;i<len;i++)
        { 
              char c = text.charAt(i); 
              switch(c){ 
                    case 0: 
                    case '&': 
                    case '<': 
                    case '>': 
                          if (results == null){ 
                                orig = text.toCharArray(); 
                                results = new StringBuffer(len+10); 
                          } 
                          if (i>beg) results.append(orig,beg,i-beg); 
                          beg = i + 1; 
                          switch (c){ 
                                default : continue; 
                                case '&': results.append("&amp;"); break; 
                                case '<': results.append("&lt;"); break; 
                                case '>': results.append("&gt;"); break;
                          } 
                    break; 
              } //switch 
        }// for i 
        if (results == null) 
              return text; 
        results.append(orig,beg,len-beg); 
        return results.toString(); 
      }// HTMLEncode
       
	public static void main(String args[]){
		encodeHtml eh = new encodeHtml();
		System.out.println(eh.getHtml());
	}
	/*static String  htmEncode(String s)
	{
	        StringBuffer stringbuffer = new StringBuffer();
	        int j = s.length();
	        for(int i = 0; i < j; i++)
	        {
	            char c = s.charAt(i);
	            switch(c)
	            {
	            case 60: stringbuffer.append("&lt;"); break;
	            case 62: stringbuffer.append("&gt;"); break;
	            case 38: stringbuffer.append("&amp;"); break;
	            case 34: stringbuffer.append("&quot;"); break;
	            case 169: stringbuffer.append("&copy;"); break;
	            case 174: stringbuffer.append("&reg;"); break;
	            case 165: stringbuffer.append("&yen;"); break;
	            case 8364: stringbuffer.append("&euro;"); break;
	            case 8482: stringbuffer.append("&#153;"); break;
	            case 13:
	              if(i < j - 1 && s.charAt(i + 1) == 10)
	              {stringbuffer.append("<br>");
	               i++;
	              }
	              break;
	            case 32:
	              if(i < j - 1 && s.charAt(i + 1) == ' ')
	                {
	                  stringbuffer.append(" &nbsp;");
	                  i++;
	                  break;
	                }
	            default:
	                stringbuffer.append(c);
	                break;
	            }
	        }
	      return new String(stringbuffer.toString());
	}*/ 


}
