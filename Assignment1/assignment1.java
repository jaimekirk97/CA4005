import java.io.*;
import java.util.*;
import java.math.*;
import java.nio.file.*;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.Charset;
import java.util.Base64;

public class assignment1
{
	private static final Charset UTF_8 = StandardCharsets.UTF_8;

	static BigInteger p = new BigInteger("b59dd79568817b4b9f6789822d22594f376e6a9abc0241846de426e5dd8f6eddef00b465f38f509b2b18351064704fe75f012fa346c5e2c442d7c99eac79b2bc8a202c98327b96816cb8042698ed3734643c4c05164e739cb72fba24f6156b6f47a7300ef778c378ea301e1141a6b25d48f1924268c62ee8dd3134745cdf7323",16);

	static BigInteger g = new BigInteger("44ec9d52c8f9189e49cd7c70253c2eb3154dd4f08467a64a0267c9defe4119f2e373388cfa350a4e66e432d638ccdc58eb703e31d4c84e50398f9f91677e88641a2d2f6157e2f4ec538088dcf5940b053c622e53bab0b4e84b1465f5738f549664bd7430961d3e5a2e7bceb62418db747386a58ff267a9939833beefb7a6fd68",16);

	static BigInteger a = new BigInteger("5af3e806e0fa466dc75de60186760516792b70fdcd72a5b6238e6f6b76ece1f1b38ba4e210f61a2b84ef1b5dc4151e799485b2171fcf318f86d42616b8fd8111d59552e4b5f228ee838d535b4b987f1eaf3e5de3ea0c403a6c38002b49eade15171cb861b367732460e3a9842b532761c16218c4fea51be8ea0248385f6bac0d",16);
	
	public static void main(String[] args) throws Exception
	{
		String file = (args[0]);
		try
		{
			BigInteger b = new BigInteger("4560748903815023246774956146043680099154081930868361916459274555712282314043058007647602039800810066290146523130805561253631661300275477012404371456225300333821927324009686685107941244702136032365186945470480616577203698016873460470548646659234770864727637963559976459859396733542980996093717822462029144640");

			BigInteger shared_pub = keyGenerator(g,p,b);
			BigInteger shared_sec = keyGenerator(a,p,b);

			String pubHex = shared_pub.toString(16);
			String s = shared_sec.toString();
			byte[] shaK = shaKey(s.getBytes(UTF_8));
			String keyHex = byteToHex(shaK);

			SecureRandom random = new SecureRandom();
			byte[] ivBytes = new byte[16];
			random.nextBytes(ivBytes);
			byte[] input = readFile(file);
			String result = encrypt(keyHex, ivBytes, input);
			writeFile("DH.txt", pubHex);
			writeFile("IV.txt", byteToHex(ivBytes));
			System.out.println(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static byte[] shaKey(byte[] sharedKey)
	{
		MessageDigest md = null;
        try 
        {
            md = MessageDigest.getInstance("SHA-256");
        } 
        catch (Exception e)
		{
			e.printStackTrace();
		}
        byte[] result = md.digest(sharedKey);
        return result;
	}

	public static BigInteger keyGenerator(BigInteger a, BigInteger b, BigInteger c)
	{
		BigInteger y = new BigInteger("1");
		for (int i = c.bitLength() - 1; i >= 0; i--)
		{
			y = y.multiply(y).mod(b);
			if (c.testBit(i))
			{
				y = y.multiply(a).mod(b);
			}
		}
		return y;
	}

	public static String byteToHex(byte[] byteList) 
	{
		StringBuilder sb = new StringBuilder();
      	for(byte b : byteList) 
      	{
         	sb.append(String.format("%02x", b));
      	}
      return sb.toString();
	}

	public static String encrypt(String key, byte[] iv, byte[] msg) throws Exception
	{
		byte[] bytesOfKey = key.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = md.digest(bytesOfKey);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

        final byte[] resultBytes = cipher.doFinal(msg);
        
        return byteToHex(resultBytes);
	}

	public static byte[] readFile(String file) throws Exception 
	{
        File input = new File(file);
        int lengthInput = (int) input.length();
        byte[] fileAsBytes = new byte[lengthInput];
        FileInputStream fInStream = new FileInputStream(input);
        fInStream.read(fileAsBytes);
        fInStream.close();
        return fileAsBytes;
    }

    public static void writeFile(String file, String output) throws Exception 
	{
		try
		{
			FileWriter myWriter = new FileWriter(file);
      		myWriter.write(output);
      		myWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    }
}
