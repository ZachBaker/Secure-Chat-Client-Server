/* CS 1501
   Primitive chat client. 
   This client connects to a server so that messages can be typed and forwarded
   to all other clients.  Try it out in conjunction with ImprovedChatServer.java.
   You will need to modify / update this program to incorporate the secure elements
   as specified in the Assignment sheet.  Note that the PORT used below is not the
   one required in the assignment -- be sure to change that and also be sure to
   check on the location of the server program regularly (it may change).
*/
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.*;
public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    public static final int PORT = 8765;

    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
	Socket connection;

    ObjectOutputStream tempWriter;
    ObjectInputStream tempReader;

    BigInteger E,N,Key;
    String cipher;

    SymCipher cipherMethod;

    public SecureChatClient ()
    {
        try {

        serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
        InetAddress addr =
                InetAddress.getByName(serverName);

        connection = new Socket(addr, PORT);   // Connect to server with new
                                                       // Socket
        tempWriter = new ObjectOutputStream(connection.getOutputStream()); tempWriter.flush();

        tempReader = new ObjectInputStream(connection.getInputStream());

        E = new BigInteger (tempReader.readObject().toString());
        System.out.println("E is: " + E.toString());
        N = new BigInteger (tempReader.readObject().toString());
        System.out.println("N is: " + N.toString());
        cipher = (String) tempReader.readObject();
        System.out.print("Cipher method is: ");

        if(cipher.equals("Add")){
            cipherMethod = new Add128(); 
            System.out.print("Add128"); 
            System.out.println();
        }

        else if(cipher.equals("Sub")){
            cipherMethod = new Substitute(); 
            System.out.print("Substitute"); 
            System.out.println();
        }

        byte [] theKey = cipherMethod.getKey();

        System.out.println("The symmetric key is:");

        for(int i = 0 ; i< theKey.length; i++)
            System.out.print(theKey[i] + " ");
        System.out.println();

        Key = new BigInteger(1,cipherMethod.getKey());      //Convert key into BigInteger, ensuring positive

        Key = Key.modPow(E,N);                              //RSA encrypt Cipher Key

        tempWriter.writeObject(Key);                        //Send RSA encypted key
        tempWriter.flush();

        myName = JOptionPane.showInputDialog(this, "Enter your user name: ");

        byte [] encryptedName = cipherMethod.encode(myName);

        tempWriter.writeObject(encryptedName);
        tempWriter.flush();

        this.setTitle(myName);      // Set title to identify chatter

        Box b = Box.createHorizontalBox();  // Set up graphical environment for
        outputArea = new JTextArea(8, 30);  // user
        outputArea.setEditable(false);
        b.add(new JScrollPane(outputArea));

        outputArea.append("Welcome to the Chat Group, " + myName + "\n");

        inputField = new JTextField("");  // This is where user will type input
        inputField.addActionListener(this);

        prompt = new JLabel("Type your messages below:");
        Container c = getContentPane();

        c.add(b, BorderLayout.NORTH);
        c.add(prompt, BorderLayout.CENTER);
        c.add(inputField, BorderLayout.SOUTH);

        Thread outputThread = new Thread(this);  // Thread is to receive strings
        outputThread.start();                    // from Server

		addWindowListener(
                new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    { 

                        String CC = new String("CLIENT CLOSING");

                        try
                        {
                        tempWriter.writeObject(CC);
                        tempWriter.flush();
                        System.exit(0);
                        }

                        catch(Exception Ernie)
                        {
                            System.out.println("Problem with client");
                        }
                        
                    }
                }
            );

        setSize(500, 200);
        setVisible(true);

        }
        catch (Exception e)
        {
            System.out.println("Problem starting client!");
        }
    }

    public void run()
    {
        while (true)
        {
             try {
                byte [] currMsgE = (byte []) tempReader.readObject();
                String currMsg = cipherMethod.decode(currMsgE);
                outputArea.append(currMsg+"\n");
             }
             catch (Exception e)
             {
                System.out.println(e +  ", closing client!");
                break;
             }
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e)
    {
        String currMsg = e.getActionCommand();


        if(!currMsg.equals("CLIENT CLOSING")){
            byte [] currEncryptedMessage = cipherMethod.encode(myName + ": " + currMsg);
            try{
                tempWriter.writeObject(currEncryptedMessage);
                tempWriter.flush();
            }

            catch(Exception ernie){
                System.out.println("Problem with client!");
            }

            inputField.setText("");
        }

        else{
            byte [] currEncryptedMessage = cipherMethod.encode(currMsg);

            try{
                tempWriter.writeObject(currEncryptedMessage);
                tempWriter.flush();
            }

            catch(Exception ernie){
                System.out.println("Problem with client!");
            }
        }

    }

    public static void main(String [] args)
    {
         SecureChatClient JR = new SecureChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}

