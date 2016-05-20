import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class Response {
	private int code;
	private String message;
	private SimpleAttributeSet keyWord;
	private StyledDocument sd;
	
	public Response(int _code, String _message, StyledDocument _sd){
		setCode(_code);
		setMessage(_message);
		
		sd = _sd;
		ConfigStyle();
	}

	private void ConfigStyle() {
		keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.BLUE);
		StyleConstants.setBold(keyWord, true);
	}

	public Response(StyledDocument _sd) {
		sd = _sd;
		ConfigStyle();
	}

	public String getMessage() {
		return message;
	}

	private void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	private void setCode(int code) {
		this.code = code;
	}
	
	public void ParseStream(String stream){
		Pattern pattern = Pattern.compile(" ");
		Matcher matcher = pattern.matcher(stream);
		if (matcher.find()) {
			code = Integer.parseInt(stream.substring(0, matcher.start()));
			message = stream.substring(matcher.end());
		}
		
	}
	
	public void SendToConsole(){
		try {
			sd.insertString(sd.getLength(), code + " " + message + "\n", keyWord);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Recv(BufferedReader br){
		try {
			ParseStream(br.readLine());
			SendToConsole();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
