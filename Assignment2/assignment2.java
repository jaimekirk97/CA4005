import java.io.*;
import java.util.*;
import java.math.*;
import java.nio.file.*;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

public class assignment2
{
	private static final Charset UTF_8 = StandardCharsets.UTF_8;

	public static void main(String[] args) throws Exception
	{
		String file = (args[0]);
		BigInteger p,q,phiN,n;
		BigInteger e = new BigInteger("65537");
		while(true)
		{
			p = BigInteger.probablePrime(512, new Random());
			q = BigInteger.probablePrime(512, new Random());

			n = p.multiply(q);
			writeFile("Modulus.txt", n.toString(16));
			BigInteger i = new BigInteger("1");
			phiN = (n.subtract(i).multiply(q.subtract(i)));
		
			if(isRelPrime(phiN,e) == true);
				{
					break;
				}	
		}
		BigInteger d = mupInverse(e,phiN);
		BigInteger testd = e.modInverse(phiN);
		byte[] fileByte = readFile(file);
		byte[] digest = shaKey(fileByte);

		BigInteger fileInt = new BigInteger(fileByte);
		
		BigInteger result = CRT(p,q,d,fileInt);
		BigInteger test = fileInt.modPow(d,n);
		System.out.println(result.toString(16)+ "\n");
	}

	public static BigInteger gcd(BigInteger e, BigInteger phiN)
	{
		BigInteger t;
		while(!phiN.equals(BigInteger.ZERO))
		{
			t = e;
			e = phiN;
			phiN = t.mod(phiN);
		}
		return e;
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

	public static BigInteger[] extendGCD(BigInteger e, BigInteger phiN)
	{
		BigInteger x = new BigInteger("0");
		BigInteger y = new BigInteger("1");
		BigInteger u = new BigInteger("1");
		BigInteger v = new BigInteger("0");
		while(!e.equals(BigInteger.ZERO))
		{
			BigInteger q = phiN.divide(e);
			BigInteger r = phiN.mod(e);
			BigInteger m = x.subtract(u.multiply(q));
			BigInteger n = y.subtract(v.multiply(q));
			phiN = e;
			e = r;
			x = u;
			y = v;
			u = m;
			v = n;
		}
		return new BigInteger[]{phiN,x,y};
	}

	public static boolean isRelPrime(BigInteger e, BigInteger phiN)
	{
		return (gcd(e,phiN).equals(BigInteger.ONE));
	}

	public static BigInteger mupInverse(BigInteger e, BigInteger phiN)
	{
		BigInteger[] inv = extendGCD(e, phiN);
		BigInteger a = inv[0];
		BigInteger b = inv[1];

		if(!a.equals(BigInteger.ONE))
		{
			return null;
		}
		else
		{
			return b.mod(phiN);
		}
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

    public static BigInteger CRT(BigInteger p, BigInteger q, BigInteger d, BigInteger file)
    {
    	BigInteger a = file.modPow(d,p);
    	BigInteger b = file.modPow(d,q);

    	BigInteger mupInv = mupInverse(q,p);

    	BigInteger ans = b.add(q.multiply(mupInv.multiply(a.subtract(b)).mod(p)));

    	return ans;
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