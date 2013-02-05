package arduino;

public class BinaryFunctions {

	
	public static byte setBitOfByte(byte x, byte bit, boolean value) {

		boolean[] b = byteToBoolArray(x);
		b[bit] = value;
		x = boolArrayToByte(b);
		return x;

	}

	public static boolean getBitOfByte(byte x, int bit) {

		return byteToBoolArray(x)[bit];
		
	}
	
	public static boolean getBitFromInt(int n, int bitwanted){
		int mask = 1 << (bitwanted-1);
		int maskedN = n & mask;
		int theBit = maskedN >> bitwanted-1;
		
		if(theBit == 0) return false;
		
		return true;
	}
	

	public static boolean[] byteToBoolArray(byte x) {
		boolean[] b = new boolean[8];
		for (int i = 0; i < 8; i++) {
			int p = (int) Math.pow(2,i);
			b[i] = ((x & p) == p) ? true : false;
		}	
		return b;
	}
	
	public static boolean[] intToBoolArray(int x) {
		boolean[] b = new boolean[32];
		for (int i = 0; i < 32; i++) {
			int p = (int) Math.pow(2,i);
			b[i] = ((x & p) == p) ? true : false;
		}	
		return b;
	}


	public static byte boolArrayToByte(boolean[] b) { //Little endian
		byte x = 0;
	for (int i = 0; i < b.length; i++) {
		if (b[i] == true) {
			x = (byte) (x + (byte) Math.pow(2,i));
		}
	}
	return x;
	}

	/*
		public boolean[][] byteArrayToBoolDoubleArray(byte[] a) { //Little Endian
			boolean[][] b = new boolean[a.length][8];	
			for (int i=0; i < a.length; i++ ) {
				for (int j = 0; j < 8; j++) {
					int p = (int) Math.pow(2,j);
					b[i][j] = ((a[i] & p) == p) ? true : false;
				}		
			}
			return b;
		}
	 */

	//internetten cakma
	public static byte[] hexStringToByteArray(String str) 
	{
		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = (byte) Integer.parseInt(str.substring(2*i, 2*i + 2), 16);
		}
		return bytes;
	}

	


}



