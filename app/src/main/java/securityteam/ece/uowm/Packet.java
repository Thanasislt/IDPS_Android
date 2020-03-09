package securityteam.ece.uowm;

import java.util.Date;

public class Packet {
    private int count;
    private String Protocol;
    private String Source;
    private String Destination;
    private String Bytes;
    private String Port;
    private Date date;

    public Packet(int count,String protocol, String source, String destination, String bytes, String port, Date date) {
        this.count = count;
        Protocol = protocol;
        Source = source;
        Destination = destination;
        Bytes = bytes;
        Port = port;
        this.date = date;
    }

    public Packet(int count,String protocol, String source, String destination) {
        this.count = count;
        Protocol = protocol;
        Source = source;
        Destination = destination;
    }


    public Packet() {

    }

    public int getCount() {
        return count;
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
