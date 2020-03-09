package securityteam.ece.uowm;

import java.util.Date;

public class Packet {
    private String Protocol;
    private String Source;
    private String Destination;
    private String Bytes;
    private String Port;
    private Date date;

    public Packet(String protocol, String source, String destination, String bytes, String port, Date date) {
        Protocol = protocol;
        Source = source;
        Destination = destination;
        Bytes = bytes;
        Port = port;
        this.date = date;
    }

    public Packet(String protocol, String source, String destination) {
        Protocol = protocol;
        Source = source;
        Destination = destination;
    }


    public Packet() {

    }

    String getProtocol(){
        return this.Protocol;
    }

    String getSource() {
        return this.Source;
    }
    String getDestination(){
        return this.Destination;
    }
}
