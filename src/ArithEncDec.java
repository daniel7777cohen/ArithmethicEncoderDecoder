//DanielCohen 203850029
//BenEfrat 305773806

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.BitSet;
public class ArithEncDec  {

	public static void Encoder(String[] inPath,String[] outPath) throws IOException 
	{
		//BigDecial is used for representing long digits frequencies
		//A loop to run over all possible strings
		for(int y=0;y<outPath.length;y++)
		{
			if(inPath[y]!=null)
			{	
				int how_many_dif_symbols = 0,currentChar,counter=0;
				int[] freqNum = new int[10];
				BigDecimal fileLength ,tempR,tempWValue,freqValue,cArrayValue;
				BigDecimal L_ = new BigDecimal("0");
				BigDecimal R_ = new BigDecimal("1");
				BigDecimal W_ = new BigDecimal("1");
				BigDecimal zero = new BigDecimal("0");
				BigDecimal one = new BigDecimal("1.0");
				BigDecimal cArray_[] = new BigDecimal[10];
				BitSet code = new BitSet();
				String fileString = inPath[y];
				System.out.println("to be encoded - "+fileString);
				while(fileString.charAt(fileString.length()-1)== 10)//removes space
					fileString = fileString.substring(0,fileString.length()-1);
				//probability array
				BigDecimal[] freq_ = new BigDecimal[10];
				BigDecimal temper ;
				//initialize freq_ (BigDecimal array)
				for(int i=0;i<freq_.length;i++)
					freq_[i] = new BigDecimal("0");
				for (int i = 0; i < fileString.length(); i++)
				{  
					freqNum [fileString.charAt(i)-48]++;//freqNum holds appearance number for each symbol
					freq_[fileString.charAt(i)-48]=freq_[fileString.charAt(i)-48].add(one);
				}
				double length = fileString.length();
				fileLength = new BigDecimal(length);
				//create probabilities
				for(int i=0;i<10;i++)
				{	
					if(freqNum[i]!= 0)
					{
						temper = freq_[i];
						temper = temper.divide(fileLength,2, RoundingMode.HALF_EVEN);
						freq_[i] = temper;
					}
				}

				for(int i=0;i<10;i++)
				{
					if (freqNum[i]!=0)
					{
						how_many_dif_symbols++;// hold numbs of different symbols at input. will prevent writing unnecessary information to decoding
					}

				}
				for(int i=0;i<cArray_.length;i++)
				{
					cArray_[i] = zero;
				}
				//array for probabilities till a specific char(not included) 
				for(int i=1;i<10;i++)
				{
					cArray_[i] = cArray_[i].add(cArray_[i-1]);
					cArray_[i] = cArray_[i].add(freq_[i-1]);
				}

				for(int i=0;i<10;i++)

					while(counter< fileString.length())
					{   
						currentChar = fileString.charAt(counter) - 48;	
						tempR = R_;
						tempR = tempR.subtract(L_);
						W_= tempR;
						//W = R-L;
						//L = L + (W * cArray[currentChar]);
						cArrayValue = cArray_[currentChar];
						L_ = L_.add(W_.multiply(cArrayValue));
						//R = L + (W * freq[currentChar]);
						freqValue = freq_[currentChar];
						tempWValue = W_;
						R_ = (L_);
						R_ = R_.add(tempWValue.multiply(freqValue));
						counter++;
					}

				//Scaling Algo

				boolean keep_working = true;
				int C = 0;
				String tag = "";

				while (keep_working)
				{

					if (R_.compareTo(new BigDecimal("0.5") ) < 0){
						tag += "0";
						L_ = L_.add(L_);
						R_ = R_.add(R_);
						continue;
					}

					if (L_.compareTo(new BigDecimal("0.5") ) > 0){
						tag += "1";
						//L = 2 * (L - 0.5);
						L_ = L_.subtract(new BigDecimal("0.5"));
						L_ = L_.add(L_);

						//R = 2 * (R - 0.5);
						R_ = R_.subtract(new BigDecimal("0.5"));
						R_ = R_.add(R_);
						continue;
					}

					//if ( (L > 0.25  &&  L <= 0.5) && (R >= 0.5  &&  R < 0.75) )
					if((L_.compareTo(new BigDecimal("0.25") ) > 0 && L_.compareTo(new BigDecimal("0.5")) <=0 )&& (R_.compareTo(new BigDecimal("0.5")) >=0&& R_.compareTo(new BigDecimal("0.75"))<0) )
					{
						C = C + 1;
						//L = 2 * (L - 0.25)
						L_ = L_.subtract(new BigDecimal("0.25"));
						L_ = L_.add(L_);

						//R = 2 * (R - 0.25)
						R_ = R_.subtract(new BigDecimal("0.25"));
						R_ = R_.add(R_);
						continue;
					}

					//	if (L <= 0.5  &&  R >= 0.75)
					if(L_.compareTo(new BigDecimal("0.5")) <=0 && R_.compareTo(new BigDecimal("0.75")) >=0 )
					{
						C = C + 1;
						tag += "1";
						for (int i=0 ; i<C ; i++)
							tag += "0";
						keep_working = false;
						continue;
					}

					//	if (L <= 0.25  &&  R >= 0.5)
					if( L_.compareTo(new BigDecimal("0.25")) <=0 && R_.compareTo(new BigDecimal("0.5")) >=0)
					{
						C = C + 1;
						tag += "0";
						for (int i=0 ; i<C ; i++)
							tag +="1";
						keep_working = false;
						continue;
					}
				}


				String binaryTag = tag;
				// binaryTag to bitset representation ( name is code ) 
				for(int i =0;i<binaryTag.length();i++)
				{
					if(binaryTag.charAt(i)=='0')
					{
						code.set(i,false);}
					else {
						code.set(i,true);
					}
				}
				//make input length to boolean representation,in 16 bits
				boolean [] sizeBoolean = toBinary(fileString.length(),16);
				//get first 8 bit of sizeBoolean( size Boolean is boolean representation of the size of original file)
				int number=0;
				number =getBits(number,7,0,sizeBoolean,-1);
				//get second 8 bit of sizeBoolean( size Boolean is boolean representation of the size of original file)
				int number1=0;
				number1 = getBits(number1,15,0,sizeBoolean,7);
				byte first = (byte) number;
				byte first_ = reverse(first);//reverse is the correct representation for our final string
				byte second = (byte) number1;
				byte second_ = reverse(second);
				byte[] finalEncoded = code.toByteArray();// byte of encoded string 
				byte[] x = new byte[3+(how_many_dif_symbols*4)+how_many_dif_symbols];//creates a byte which contains - 16bits of input length, 8 bits of different symbols,8 bits of index(the symbol itself),32 bits of representing how many times the last symbol occurs
				x[0] = first_;//first 8 bits for length
				x[1] = second_;//second 8 bits of length (total 16 bits representation
				byte dif_sym = (byte)how_many_dif_symbols;
				byte dif_sym_ = reverse(dif_sym);
				x[2] = dif_sym_;//different symbols
				int counter_for_byte_array=3;
				for(int i=0;i<10;i++)
				{
					if(freqNum[i]!=0)
					{

						byte indexer = (byte) i;
						byte indexer_ = reverse(indexer);
						boolean[] temp = toBinary(freqNum[i],32);//temp hold boolean representation of a specific appearance
						byte[] numArray = toByteArray(temp);//numArray holds byte[] representation of temp
						x[counter_for_byte_array]= indexer_;// assign to x["some counter"] indexer which is the symbol itself
						counter_for_byte_array++;
						x[counter_for_byte_array]= numArray[0];//assign to x["somecounter++"] numArray[0] which is first 8 bit of representation for appearance
						counter_for_byte_array++;
						x[counter_for_byte_array]= numArray[1];////assign to x["somecounter++"] numArray[0] which is second 8 bit of representation for appearance
						counter_for_byte_array++;
						x[counter_for_byte_array] = numArray[2];//'' '' '' third
						counter_for_byte_array++;
						x[counter_for_byte_array] = numArray[3];//'''''''' fourth
						counter_for_byte_array++;
					}
				}
				byte[] combined = new byte[x.length + finalEncoded.length]; //combines 2 arrays to be a byte which contains - 16bits of input length, 8 bits of different symbols,8 bits of index(the symbol itself),32 bits of representing how many times the last symbol occurs,final binary tag representation without 0.
				System.arraycopy(x,0,combined,0         ,x.length);
				System.arraycopy(finalEncoded,0,combined,x.length,finalEncoded.length);
				outPath[y] = "";
				BitSet DecompressBitSet = BitSet.valueOf(combined);// bitset of combined
				for(int i =0;i<DecompressBitSet.length();i++)
				{
					if(DecompressBitSet.get(i))
					{
						outPath[y] += "1";
					}

					else {
						outPath[y] += "0";
					}
				}
				System.out.println("outPath[y] is "+outPath[y]);

			}


		}

	}

	//get bits (0/1) from a specific range of a boolean array and return the int value representation
	private static int getBits(int number, int count, int c, boolean[] sizeBoolean, int range) {
		// TODO Auto-generated method stub
		while(count>range)
		{
			if(sizeBoolean[count])
			{ 
				number += Math.pow(2, c);
				c++;
				count--;
			}
			else {
				c++;
				count--;
			}

		} 
		return number;
	}

	public static byte[] EncoderWithArray(String[] inPath, String[] outPath) throws IOException 
	{		
		int y =0;
		int how_many_dif_symbols = 0,currentChar,counter=0;
		int[] freqNum = new int[10];
		BigDecimal fileLength ,tempR,tempWValue,freqValue,cArrayValue;
		BigDecimal L_ = new BigDecimal("0");
		BigDecimal R_ = new BigDecimal("1");
		BigDecimal W_ = new BigDecimal("1");
		BigDecimal zero = new BigDecimal("0");
		BigDecimal one = new BigDecimal("1.0");
		BigDecimal cArray_[] = new BigDecimal[10];
		String fileString = inPath[y];
		System.out.println(fileString);
		while(fileString.charAt(fileString.length()-1)== 10)//removes space
			fileString = fileString.substring(0,fileString.length()-1);
		//Probability array
		BigDecimal[] freq_ = new BigDecimal[10];
		BigDecimal temper ;
		//initialize freq_ (bigdecimal array)
		for(int i=0;i<freq_.length;i++)
			freq_[i] = new BigDecimal("0");
		for (int i = 0; i < fileString.length(); i++)
		{  
			freqNum [fileString.charAt(i)-48]++;
			freq_[fileString.charAt(i)-48]=freq_[fileString.charAt(i)-48].add(one);
		}
		double length = fileString.length();
		fileLength = new BigDecimal(length);
		for(int i=0;i<10;i++)
		{
			temper = freq_[i];
			temper = temper.divide(fileLength,2, RoundingMode.HALF_EVEN);
			freq_[i] = temper;
		}
		for(int i=0;i<10;i++)
		{
			if (freqNum[i]!=0)
			{
				how_many_dif_symbols++;
			}

		}
		for(int i=0;i<cArray_.length;i++)
		{
			cArray_[i] = zero;
		}
		//array for probabilities till a specific char(not included) 
		for(int i=1;i<10;i++)
		{
			cArray_[i] = cArray_[i].add(cArray_[i-1]);
			cArray_[i] = cArray_[i].add(freq_[i-1]);
		}
		for(int i=0;i<10;i++)

			while(counter< fileString.length())
			{   
				currentChar = fileString.charAt(counter) - 48;	
				tempR = R_;
				tempR = tempR.subtract(L_);
				W_= tempR;
				//W = R-L;
				//L = L + (W * cArray[currentChar]);
				cArrayValue = cArray_[currentChar];
				L_ = L_.add(W_.multiply(cArrayValue));
				//R = L + (W * freq[currentChar]);
				freqValue = freq_[currentChar];
				tempWValue = W_;
				R_ = (L_);
				R_ = R_.add(tempWValue.multiply(freqValue));
				counter++;
			}

		boolean keep_working = true;
		int C = 0;
		String tag = "";

		//Scaling
		while (keep_working)
		{

			if (R_.compareTo(new BigDecimal("0.5") ) < 0){
				tag += "0";
				L_ = L_.add(L_);
				R_ = R_.add(R_);
				continue;
			}

			if (L_.compareTo(new BigDecimal("0.5") ) > 0){
				tag += "1";
				//L = 2 * (L - 0.5);
				L_ = L_.subtract(new BigDecimal("0.5"));
				L_ = L_.add(L_);

				//R = 2 * (R - 0.5);
				R_ = R_.subtract(new BigDecimal("0.5"));
				R_ = R_.add(R_);
				continue;
			}

			//if ( (L > 0.25  &&  L <= 0.5) && (R >= 0.5  &&  R < 0.75) )
			if((L_.compareTo(new BigDecimal("0.25") ) > 0 && L_.compareTo(new BigDecimal("0.5")) <=0 )&& (R_.compareTo(new BigDecimal("0.5")) >=0&& R_.compareTo(new BigDecimal("0.75"))<0) )
			{
				C = C + 1;
				//L = 2 * (L - 0.25)
				L_ = L_.subtract(new BigDecimal("0.25"));
				L_ = L_.add(L_);

				//R = 2 * (R - 0.25)
				R_ = R_.subtract(new BigDecimal("0.25"));
				R_ = R_.add(R_);
				continue;
			}

			//	if (L <= 0.5  &&  R >= 0.75)
			if(L_.compareTo(new BigDecimal("0.5")) <=0 && R_.compareTo(new BigDecimal("0.75")) >=0 )
			{
				C = C + 1;
				tag += "1";
				for (int i=0 ; i<C ; i++)
					tag += "0";
				keep_working = false;
				continue;
			}

			//	if (L <= 0.25  &&  R >= 0.5)
			if( L_.compareTo(new BigDecimal("0.25")) <=0 && R_.compareTo(new BigDecimal("0.5")) >=0)
			{
				C = C + 1;
				tag += "0";
				for (int i=0 ; i<C ; i++)
					tag +="1";
				keep_working = false;
				continue;
			}
		}

		String binaryTag = tag;
		BitSet code = new BitSet(binaryTag.length());

		for(int i =0;i<binaryTag.length();i++)
		{
			if(binaryTag.charAt(i)=='0')
			{
				code.set(i,false);}
			else {
				code.set(i,true);
			}
		}
		boolean [] sizeBoolean = toBinary(fileString.length(),16);
		//get first 8 bit of sizeBoolean( size Boolean is boolean representation of the size of original file)
		int number=0;
		number = getBits(0,7,0,sizeBoolean,-1);
		//get second 8 bit of sizeBoolean( size Boolean is boolean representation of the size of original file)
		int number1=0;
		number1 = getBits(0,15,0,sizeBoolean,7);
		byte first = (byte) number;
		byte first_ = reverse(first);
		byte second = (byte) number1;
		byte second_ = reverse(second);
		byte[] finalEncoded = code.toByteArray();// byte of encoded
		byte[] x = new byte[3+(how_many_dif_symbols*4)+how_many_dif_symbols];
		x[0] = first_;
		x[1] = second_;
		byte dif_sym = (byte)how_many_dif_symbols;
		byte dif_sym_ = reverse(dif_sym);
		x[2] = dif_sym_;
		int counter_for_byte_array=3;
		for(int i=0;i<10;i++)
		{
			if(freqNum[i]!=0)
			{

				byte indexer = (byte) i;
				byte indexer_ = reverse(indexer);
				boolean[] temp = toBinary(freqNum[i],32);
				byte[] numArray = toByteArray(temp);
				x[counter_for_byte_array]= indexer_;
				counter_for_byte_array++;

				x[counter_for_byte_array]= numArray[0];
				counter_for_byte_array++;
				x[counter_for_byte_array]= numArray[1];
				counter_for_byte_array++;
				x[counter_for_byte_array] = numArray[2];
				counter_for_byte_array++;
				x[counter_for_byte_array] = numArray[3];
				counter_for_byte_array++;
			}
		}

		byte[] combined = new byte[x.length + finalEncoded.length];
		System.arraycopy(x,0,combined,0         ,x.length);
		System.arraycopy(finalEncoded,0,combined,x.length,finalEncoded.length);
		BitSet DecompressBitSet = BitSet.valueOf(combined);// bitset of the binary file
		outPath[y] = "";
		for(int i =0;i<DecompressBitSet.length();i++)
		{
			if(DecompressBitSet.get(i))
			{
				outPath[y] += "1";
			}

			else {
				outPath[y]+= "0";
			}
		}
		System.out.println("outPath[y] is "+outPath[y]);

		return combined;//combined holds - 16bits of input length, 8 bits of different symbols,8 bits of index(the symbol itself),32 bits of representing how many times the last symbol occurs,final binary tag representation without 0.



	}		




	static byte[] toByteArray(boolean[] bools) {
		BitSet bits = new BitSet(bools.length);
		for (int i = 0; i < bools.length; i++) {
			if (bools[i]) {
				bits.set(i);
			}			
		}
		byte[] bytes = bits.toByteArray();
		return bytes;
	}

	public static byte reverse(byte x) {
		byte b = 0;
		for (int i = 0; i < 8; ++i) {
			b<<=1;
			b|=( x &1);
			x>>=1;
		}
		return b;
	}

	private static boolean[] toBinary(int number, int base) {
		final boolean[] ret = new boolean[base];
		for (int i = 0; i < base; i++) {
			ret[base - 1 - i] = (1 << i & number) != 0;
		}
		return ret;
	}

	public static void Decoder(String[] inPath,String[] outPath) throws IOException 
	{		
		for(int y=0;y<inPath.length;y++)
		{
			if(inPath[y]!= null)
			{
				System.out.println("starting decoding for "+inPath[y]);
				int index_for_freq;
				int appearance;
				int index_counter_for_method ;
				int k = 23;
				int dif_sym=0;
				int fileLength=0;
				int index = 15;
				int c = 0;
				BigDecimal zero = new BigDecimal("0");
				BigDecimal[] cArray_ = new BigDecimal[10];
				BigDecimal[] freq_ = new BigDecimal[10];
				BigDecimal sum_of_binary_;
				BigDecimal temper_freq;
				BigDecimal fileLength_ ;
				int counter =0;
				BigDecimal L_ = new BigDecimal("0");
				BigDecimal R_ = new BigDecimal("1");
				BigDecimal W_ = new BigDecimal("1");
				BigDecimal temp2,temp3;;//used for making operations on BigDecimal values, at restoring original string(higher bound and lower)
				BigDecimal two;
				BigDecimal one_zero;
				//initialize freq_ (bigdecimal array)
				for(int i=0;i<freq_.length;i++)
					freq_[i] = new BigDecimal("0");

				BitSet newbitset = new BitSet(inPath[y].length());//hold a bitset in a size of encoded string, and initialize it afterwards
				for(int i=0;i<inPath[y].length();i++)
				{
					if(inPath[y].charAt(i)=='1')
					{
						newbitset.set(i);
					}
					else
					{
						newbitset.set(i,false);
					}
				}	

				//get first 16 bit of representation of the size of original file)

				while(index>-1)
				{
					if(newbitset.get(index))
					{
						fileLength += Math.pow(2, c);
						c++;
						index--;
					}
					else {
						c++;
						index--;
					}
				} 

				//get 3rd 8 bits which represent different symbols amount in original input
				dif_sym=0;
				index = 23;
				c = 0;
				while(index>15)
				{
					if(newbitset.get(index))
					{
						dif_sym += Math.pow(2, c);
						c++;
						index--;
					}
					else {
						c++;
						index--;
					}
				} 
				// in order to continue processing after 3 first bytes  
				k = 23;

				//this loops runs as the number of different symbols in original input
				//it collects and assigns to the specific symbol at a new frequency array , the number of appearance it had at original input
				for(int i =0;i<dif_sym;i++)
				{
					index_for_freq=0;
					index_counter_for_method = k+8;
					c = 0;
					while(index_counter_for_method>k)
					{
						if(newbitset.get(index_counter_for_method))
						{
							index_for_freq += Math.pow(2, c);//calculate which symbol we want to update later at out new frequency array
							c++;
							index_counter_for_method--;
						}
						else {
							c++;
							index_counter_for_method--;
						}			
					} 
					k+=9;
					appearance =0;
					c=0;

					for(int x=31;x>=0;x--)
					{
						if(newbitset.get(k+x))
						{
							appearance += Math.pow(2, c);//calculate appearance
							c++;
						}else
						{
							c++;
						}
					}
					k+=31;//for next while loop
					freq_[index_for_freq] = new BigDecimal(appearance);//assigns the exact appearance to the symbol
				}		
				k++;//for continuing indexing the stream

				fileLength_ = new BigDecimal(fileLength);
				//divide the appearance with the original input length will give us the exact probabilities we need
				for(int i=0;i<10;i++)
				{
					temper_freq = freq_[i];
					temper_freq = temper_freq.divide(fileLength_,2, RoundingMode.HALF_EVEN);//helps scaling
					freq_[i] = temper_freq;
				}
				sum_of_binary_= new BigDecimal("0");
				c=1;

				//calculate tag(binary tag) from bitset, from counter k
				// oneORzero/2^1 + oneORzero/2^2 + oneORzero/2^3 .....
				for(int i = k;i<newbitset.length();i++)
				{
					if(newbitset.get(i))
					{
						one_zero = new BigDecimal("1");
						two = new BigDecimal("2");
						two = two.pow(c);//2^c
						one_zero = one_zero.divide(two);// (1/2^c)
						sum_of_binary_ = sum_of_binary_.add(one_zero);
					}
					else 
					{
						one_zero = new BigDecimal("0");
					}

					c++;
				}

				//create cArray
				for(int i=0;i<10;i++)
					cArray_[i] = zero;
				for(int i=1;i<10;i++)
				{
					cArray_[i] = cArray_[i].add(cArray_[i-1]);
					cArray_[i] = cArray_[i].add(freq_[i-1]);
				}
				//decoding algorithm 
				outPath[y] = ""; 


				while(counter< fileLength)
				{
					W_= R_.subtract(L_);
					for(int i=0;i<10;i++)
					{

						temp3 = L_.add(W_.multiply(cArray_[i]));//L+(W*cArray[i])
						temp2 = L_.add(W_.multiply(cArray_[i].add(freq_[i])));//L+W*(cArray[i] +freq
						if(temp3.compareTo(sum_of_binary_)<=0 && temp2.compareTo(sum_of_binary_)==1)
							//if((double)( L+W*cArray[i]) <= sum_of_binary && sum_of_binary< (double)(L+W*(cArray[i]+ freq[i])))
						{

							L_ = L_.add(W_.multiply(cArray_[i]));//L = L+W*cArray[i]
							R_ = (L_);
							R_ = R_.add(W_.multiply(freq_[i]));
							outPath[y] +=i;

						}

					}
					counter++;
				}



			}

		}		
	}

	public static void DecoderWithArray(String[] inPath,String[] outPath, byte[] encodedbyte) throws IOException 
	{		

		System.out.println("inPath[0] is "+inPath[0]);
		int index_for_freq;
		int appearance;
		int index_counter_for_method ;
		int k = 23;
		int dif_sym=0;
		int fileLength=0;
		int index = 15;
		int c = 0;
		BigDecimal zero = new BigDecimal("0");
		BigDecimal[] cArray_ = new BigDecimal[10];
		BigDecimal[] freq_ = new BigDecimal[10];
		BigDecimal sum_of_binary_;
		BigDecimal temper_freq;
		BigDecimal fileLength_ ;
		int counter =0;
		BigDecimal L_ = new BigDecimal("0");
		BigDecimal R_ = new BigDecimal("1");
		BigDecimal W_ = new BigDecimal("1");
		BigDecimal temp2 ;
		BigDecimal temp3 ;
		BigDecimal two;
		BigDecimal one_zero;
		//initialize freq_ (bigdecimal array)
		for(int i=0;i<freq_.length;i++)
			freq_[i] = new BigDecimal("0");
		BitSet newbitset = BitSet.valueOf(encodedbyte);//encoded byte was the encoded byte [] representation

		// now the whole proccess will be using that encodedbyte.. with a bitset called newbitset

		//get first 16 bit of boolean representation of the size of original file)
		while(index>-1)
		{
			if(newbitset.get(index))
			{
				fileLength += Math.pow(2, c);
				c++;
				index--;
			}
			else {
				c++;
				index--;
			}
		} 
		dif_sym=0;
		index = 23;
		c = 0;
		while(index>15)
		{
			if(newbitset.get(index))
			{
				dif_sym += Math.pow(2, c);
				c++;
				index--;
			}
			else {
				c++;
				index--;
			}
		} 
		// in order to continue processing after 3 first bytes  
		k = 23;
		//this loops runs as the number of different symbols in original input
		for(int i =0;i<dif_sym;i++)
		{
			index_for_freq=0;
			index_counter_for_method = k+8;//(31)
			c = 0;
			while(index_counter_for_method>k)
			{
				if(newbitset.get(index_counter_for_method))
				{
					index_for_freq += Math.pow(2, c);
					c++;
					index_counter_for_method--;
				}
				else {
					c++;
					index_counter_for_method--;
				}			
			} 
			k+=9;
			appearance =0;
			c=0;
			for(int x=31;x>=0;x--)
			{
				if(newbitset.get(k+x))
				{
					appearance += Math.pow(2, c);
					c++;
				}else
				{
					c++;
				}
			}
			k+=31;//for next while loop
			freq_[index_for_freq] = new BigDecimal(appearance);
		}		
		k++;

		fileLength_ = new BigDecimal(fileLength);
		for(int i=0;i<10;i++)
		{
			temper_freq = freq_[i];
			temper_freq = temper_freq.divide(fileLength_,2, RoundingMode.HALF_EVEN);
			freq_[i] = temper_freq;
		}
		sum_of_binary_= new BigDecimal("0");
		c=1;
		for(int i = k;i<newbitset.length();i++)
		{
			if(newbitset.get(i))
			{
				one_zero = new BigDecimal("1");

			}
			else 
			{
				one_zero = new BigDecimal("0");
			}
			two = new BigDecimal("2");
			two = two.pow(c);//2^c
			one_zero = one_zero.divide(two);// (1/2^c)
			sum_of_binary_ = sum_of_binary_.add(one_zero);
			c++;
		}

		for(int i=0;i<10;i++)
			cArray_[i] = zero;
		for(int i=1;i<10;i++)
		{
			cArray_[i] = cArray_[i].add(cArray_[i-1]);
			cArray_[i] = cArray_[i].add(freq_[i-1]);
		}

		outPath[0] = "";
		//restore original string
		while(counter< fileLength)
		{
			W_= R_.subtract(L_);
			for(int i=0;i<10;i++)
			{

				temp3 = L_.add(W_.multiply(cArray_[i]));//L+(W*cArray[i])
				temp2 = L_.add(W_.multiply(cArray_[i].add(freq_[i])));//L+W*(cArray[i] +freq
				if(temp3.compareTo(sum_of_binary_)<=0 && temp2.compareTo(sum_of_binary_)==1)
					//if((double)( L+W*cArray[i]) <= sum_of_binary && sum_of_binary< (double)(L+W*(cArray[i]+ freq[i])))
				{

					L_ = L_.add(W_.multiply(cArray_[i]));//L = L+W*cArray[i]
					R_ = (L_);
					R_ = R_.add(W_.multiply(freq_[i]));
					outPath[0] +=i;

				}

			}
			counter++;
			;}

	}

}
