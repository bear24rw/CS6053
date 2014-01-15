/*****************************************************************************
  * RSA - Storage and operation meduim for working with RSA Secret Keys.
  ****************************************************************************/
/*
  Written By: Coleman Kane <cokane@cokane.org>

  Written For: Dr. John Franco, University of Cincinnati ECECS Dept.
  			   20-ECES-653: Network Security

  Copyright(c): 2003, by Coleman Kane

  $Id: RSA.java,v 1.1 2009/05/13 17:46:09 franco Exp $

  $Log: RSA.java,v $
  Revision 1.1  2009/05/13 17:46:09  franco
  Initial revision

  Revision 1.1.1.1  2008/04/17 00:44:24  franco


  Revision 1.1  2003/11/23 07:46:27  cokane
  Initial revision


*/
import java.lang.*;
import java.math.*;
import java.util.*;
import java.io.*;
import java.security.*;

public class RSA implements Serializable {
	/** Constants I'll need later */
	private static BigInteger FOUR = new BigInteger("4");
	private static BigInteger E = new BigInteger("65537");

	private Random r;
	private BigInteger q, p;
	String Description;
	Date created;

	public RSA() {
		Description = "RSA Keypair";
		created = new Date();
		gen();
	}

	/** Generate a new set of keys */
	public void gen() {
		r = new Random();
		p = new BigInteger(255, 100, r);
		do {
			p = new BigInteger(255, 100, r);
		} while(p.mod(RSA.FOUR).intValue() != 3);

		do {
			q = new BigInteger(255, 100, r);
		} while(q.mod(RSA.FOUR).intValue() != 3);
	}

	/** Returns a signature of m (m^d mod n) */
	public BigInteger signNum(BigInteger m) {
		BigInteger d = RSA.E.modInverse(
				q.subtract(BigInteger.ONE).multiply(
					p.subtract(BigInteger.ONE)));
		BigInteger n = p.multiply(q);

		return m.modPow(d, n);
	}

	/** Returns String signature of pt, converts pt to a biginteger */
	public String sign(String pt) {
		try {
			BigInteger m = new BigInteger(1, pt.getBytes("ISO-8859-1"));
			return new String(signNum(m).toByteArray(), "ISO-8859-1");
		} catch(UnsupportedEncodingException uex) {
			/** LATIN-1 not supported, you are screwed */
			return null;
		}
	}

	/** Returns a Public key to match this object */
	public PubRSA publicKey() {
		return new PubRSA(RSA.E, p.multiply(q));
	}
}
