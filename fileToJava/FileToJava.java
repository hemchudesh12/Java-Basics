package prep.fileToJava;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
class HtmlElement{
	String tag,content;
	HtmlElement(String tag,String content){
		this.tag=tag;
		this.content=content;
	}
}
public class FileToJava {

	public static void main(String[] args){
		// TODO Auto-generated method stub
		try {
			String html = Files.readString(Path.of("E:\\elite\\Eclipse\\prep\\src\\prep\\fileToJava\\index.html"));
			List<HtmlElement> ele = new ArrayList<>();
			Pattern p =Pattern.compile("<([a-zA-Z][a-zA-Z0-9]*)>([^<]*)</\\1>",Pattern.DOTALL);
			Matcher m = p.matcher(html);
			while(m.find()) {
				String content = m.group(2);
			    if (!content.isEmpty()) {
			        ele.add(new HtmlElement(m.group(1), content));
			    }
			}
			for(HtmlElement msg : ele) {
				System.out.println("Tag : "+msg.tag+"\nContent : "+msg.content);
			}
		}
		catch(Exception e) {
			System.out.println("Exception : "+e.getMessage());
		}
		
	}

}
