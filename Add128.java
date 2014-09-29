import java.util.*;

public class Add128 implements SymCipher{

	byte [] key;

	//creates random key of bytes
	public Add128(){
		key = new byte[128];
		Random rand = new Random();
		rand.nextBytes(key);
	}

	public Add128(byte [] thekey){
		key = thekey;
	}

	//returns key
	public byte [] getKey(){
		return key;
	}

	//encodes string by adding each key[i] value to string.charAt(i)
	//key wraps to begginging if string is longer than key
	public byte [] encode (String S){

		System.out.println("The original message is: " + S);

		byte [] stringToByte = new byte [S.length()];

		System.out.print("The corresponding array of bytes is: ");

		for(int i = 0; i<S.length();i++)
			stringToByte[i] = (byte) S.charAt(i);

		for(int i = 0; i < stringToByte.length; i++)
			System.out.print(stringToByte[i] + " ");
		System.out.println();

		int keyPos = 0;

		//trys to add key to text, if entire key was used starts at beggining of key 
		for(int i = 0; i<stringToByte.length; i++){
			try{
				stringToByte[i] = (byte)(stringToByte[i] + key[keyPos]);
				keyPos++;
			}

			catch(ArrayIndexOutOfBoundsException e){
				keyPos = 0;
				stringToByte[i] = (byte)(stringToByte[i] + key[keyPos]);
				keyPos++;
			}
		}

		System.out.print("The encrypted array of bytes is: ");

		for(int i = 0; i<stringToByte.length;i++)
			System.out.print(stringToByte[i] + " " );
		System.out.println();

		return stringToByte;
	}

	//decodes array of bytes by subtracting key from the given array of bytes
	public String decode (byte [] bytes){

		System.out.print("The encrypted array of bytes recieved is: " );

		for(int i = 0; i< bytes.length; i++)
			System.out.print(bytes[i] + " ");
		System.out.println();

		int keyPos = 0; 

		for(int i = 0; i<bytes.length; i++){
			try{
				bytes[i] = (byte)(bytes[i] - key[keyPos]);
				keyPos++;
			}

			catch(ArrayIndexOutOfBoundsException e){
				keyPos = 0;
				bytes[i] = (byte)(bytes[i] - key[keyPos]);
				keyPos++;
			}
		}

		System.out.print("The decrypted array of bytes is: ");

		for(int i = 0; i<bytes.length;i++)
			System.out.print(bytes[i] + " ");
		System.out.println();

		String decoded = new String(bytes);

		System.out.println("The corresponding string is: " + decoded);
		return decoded;
	}
}