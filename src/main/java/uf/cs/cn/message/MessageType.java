package uf.cs.cn.message;

//TODO : check if we can use ENUM here
public class MessageType {
    public static final byte CHOKE = 0;
    public static final byte UN_CHOKE = 1;
    public static final byte INTERESTED = 2;
    public static final byte NOT_INTERESTED = 3;
    public static final byte HAVE = 4;
    public static final byte BIT_FIELD = 5;
    public static final byte REQUEST = 6;
    public static final byte PIECE = 7;
}

