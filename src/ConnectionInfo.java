
public class ConnectionInfo {
	private int[] ip;
	private int port;
	
	public ConnectionInfo(String message){     
		message = message.replaceAll("[^0-9]+", " "); 
	    String[] tok = message.trim().split(" ");
	    ip = new int[4];
	    ip[0] = Integer.parseInt(tok[0]);
	    ip[1] = Integer.parseInt(tok[1]);
	    ip[2] = Integer.parseInt(tok[2]);
	    ip[3] = Integer.parseInt(tok[3]);
	    
	    port = 256 * Integer.parseInt(tok[4]) + Integer.parseInt(tok[5]);
	}
	
	public String getIp(){
		return ip[0]+"."+ip[1]+"."+ip[2]+"."+ip[3];
	}
	
	public int getPort(){
		return port;
	}
}
