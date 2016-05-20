import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class SimpleFtpClient {

	private JFrame frame;
	private JTextField ipfield;
	private JTextField portfield;
	private JTextField command;
	private JTextField usernamefield;
	private JTextField passwordfield;
	protected Socket socket;
	protected BufferedReader in;
	protected DataOutputStream out;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimpleFtpClient window = new SimpleFtpClient();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SimpleFtpClient() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 466, 490);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		ipfield = new JTextField();
		ipfield.setBounds(26, 31, 116, 22);
		frame.getContentPane().add(ipfield);
		ipfield.setColumns(10);
		
		portfield = new JTextField();
		portfield.setBounds(180, 31, 116, 22);
		frame.getContentPane().add(portfield);
		portfield.setColumns(10);
		
		JButton btnConnect = new JButton("Connect");
		
		btnConnect.setBounds(322, 30, 97, 84);
		frame.getContentPane().add(btnConnect);
		
		JLabel lblNewLabel = new JLabel("Ip");
		lblNewLabel.setBounds(26, 13, 56, 16);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Port");
		lblNewLabel_1.setBounds(180, 13, 56, 16);
		frame.getContentPane().add(lblNewLabel_1);
		
		JTextPane ftpanel = new JTextPane();
		ftpanel.setBounds(26, 127, 394, 255);
		frame.getContentPane().add(ftpanel);
		
		JScrollPane scrollPane = new JScrollPane(ftpanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(26, 127, 394, 255);
		frame.getContentPane().add(scrollPane);
		
		final StyledDocument doc = ftpanel.getStyledDocument();
		
		command = new JTextField();
		command.setBounds(26, 395, 264, 30);
		frame.getContentPane().add(command);
		command.setColumns(10);
		
		JButton btnsend = new JButton("Send");
		btnsend.setBounds(302, 391, 134, 39);
		frame.getContentPane().add(btnsend);
		
		usernamefield = new JTextField();
		usernamefield.setBounds(26, 92, 116, 22);
		frame.getContentPane().add(usernamefield);
		usernamefield.setColumns(10);
		
		passwordfield = new JTextField();
		passwordfield.setBounds(180, 92, 116, 22);
		frame.getContentPane().add(passwordfield);
		passwordfield.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Username");
		lblNewLabel_2.setBounds(26, 73, 97, 16);
		frame.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Password");
		lblNewLabel_3.setBounds(180, 73, 56, 16);
		frame.getContentPane().add(lblNewLabel_3);
		
		btnsend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0){
				String commandtext = command.getText();
				
				final Command cmd = new Command(doc);
				cmd.ParseStream(commandtext);
				
				new Thread(new Runnable() {
			         public void run()
			         {
							executeCommand(cmd, socket, doc);
			         }
				}).start();

			}
		});
		
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					socket = new Socket(ipfield.getText(), Integer.parseInt(portfield.getText()));
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new DataOutputStream(socket.getOutputStream());
					
					new Thread(new Runnable() {
				         public void run()
				         {
								connectUser(usernamefield.getText(), passwordfield.getText(), socket, doc);
				         }
					}).start();

				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}
	
	private void executeCommand(Command cmd, Socket socket, StyledDocument doc) {
		switch(cmd.getCommand()){
			case "LIST":
				listCommand(cmd, socket, doc);
				break;
			case "STOR":
				storCommand(cmd, socket, doc);
				break;
			case "RETR":
				retrCommand(cmd, socket, doc);
				break;
			case "DELE":
				deleCommand(cmd, socket, doc);
				break;
			default:break;
		}
	}
	
	private void connectUser(String username, String password, Socket socket, StyledDocument doc){
		Response res = new Response(doc);
		res.Recv(in);
		
		if(res.getCode() == 220){
			Command cmd = new Command("USER", username, doc);
			cmd.Send(out);
		}
		
		res.Recv(in);
		if(res.getCode() == 331){
			Command cmd = new Command("PASS", password, doc);
			cmd.Send(out);
		}
		
		res.Recv(in);
	}
	
	private ConnectionInfo pasvCommand(Socket socket, StyledDocument doc){
		Response res = new Response(doc);
		ConnectionInfo ci = null;
		
		Command cmd = new Command("PASV", "", doc);
		cmd.Send(out);
		
		res.Recv(in);
		if(res.getCode() == 227){
			ci = new ConnectionInfo(res.getMessage());
		}
		
		return ci;
	}
	
	private void listCommand(Command cmd, Socket socket, StyledDocument doc){
		Response res = new Response(doc);
		ConnectionInfo ci = pasvCommand(socket, doc);

		cmd.Send(out);
		
		Socket sc = connectToServer(ci);
		if(sc != null){
			res.Recv(in);
			if(res.getCode() == 150){
				byte[] b = new byte[1024];
			    BufferedReader inn = null;
				try {
					inn = new BufferedReader(
				            new InputStreamReader(sc.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String userInput;
					while ((userInput = inn.readLine()) != null) {
						doc.insertString(doc.getLength(), userInput + "\n", null);
					}
				} catch (BadLocationException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		res.Recv(in);
		
	}
	
	private void deleCommand(Command cmd, Socket socket, StyledDocument doc){
		Response res = new Response(doc);
		
		cmd.Send(out);
		res.Recv(in);
	}
	
	private void retrCommand(Command cmd, Socket socket, StyledDocument doc){
		Response res = new Response(doc);
		ConnectionInfo ci = pasvCommand(socket, doc);

		cmd.Send(out);
		
		Socket sc = connectToServer(ci);
		
		try {
			saveFile(cmd, sc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		res.Recv(in);
		res.Recv(in);
	}
	
	private void storCommand(Command cmd, Socket socket, StyledDocument doc){
		Response res = new Response(doc);
		ConnectionInfo ci = pasvCommand(socket, doc);

		cmd.Send(out);
		
		Socket sc = connectToServer(ci);
		
		try {
			sendFile(cmd, sc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		res.Recv(in);
		res.Recv(in);
	}
	
	private void sendFile(Command cmd, Socket sock) throws IOException{
		File myFile = new File (cmd.getArgument());
        byte [] mybytearray  = new byte [(int)myFile.length()];
        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybytearray,0,mybytearray.length);
        OutputStream os = sock.getOutputStream();
        System.out.println("Sending " + cmd.getArgument() + "(" + mybytearray.length + " bytes)");
        os.write(mybytearray,0,mybytearray.length);
        os.flush();
        sock.close();
	}
	
	private void saveFile(Command cmd, Socket sock) throws IOException{
		File yourFile = new File(cmd.getArgument());
		if(!yourFile.exists()) {
		    yourFile.createNewFile();
		} 
		FileOutputStream outfile = new FileOutputStream(yourFile, false);
	    
		InputStream inps = sock.getInputStream();
		byte[] bytes = new byte[16*1024];

        int count;
        while ((count = inps.read(bytes)) > 0) {
        	outfile.write(bytes, 0, count);
        }
        
        outfile.close();
        inps.close();
        sock.close();
	}
	
	private Socket connectToServer(ConnectionInfo info){
		try {
			return new Socket(info.getIp(), info.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
