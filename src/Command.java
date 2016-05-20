import java.awt.Color;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class Command {
	private String command;
	private String argument;
	private SimpleAttributeSet keyWord;
	private StyledDocument _sd;
	
	public Command(String _command, String _argument, StyledDocument sd){
		setArgument(_argument);
		setCommand(_command);
		_sd = sd;
		
		COnfigStyle();
	}

	private void COnfigStyle() {
		keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.RED);
		StyleConstants.setBold(keyWord, true);
	}
	
	public Command(StyledDocument sd){
		_sd = sd;
		COnfigStyle();
	}

	public String getArgument() {
		return argument;
	}

	private void setArgument(String argument) {
		this.argument = argument;
	}

	public String getCommand() {
		return command;
	}

	private void setCommand(String command) {
		this.command = command;
	}
	
	public void ParseStream(String stream){
		Pattern pattern = Pattern.compile(" ");
		Matcher matcher = pattern.matcher(stream);
		if (matcher.find()) {
			command = stream.substring(0, matcher.start());
			argument = stream.substring(matcher.end());
		}else{
			command = stream;
		}
		
	}
	
	public void SendToConsole(){
		try {
			_sd.insertString(_sd.getLength(), command + " " + argument + "\n", keyWord);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Send(DataOutputStream pw){
		try {
			String commandline = command + " " + argument;
			pw.write(commandline.getBytes());
			pw.flush();
			SendToConsole();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
