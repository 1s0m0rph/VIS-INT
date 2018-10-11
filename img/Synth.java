
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
//import java.awt.Color;
//import java.util.ArrayList;
public class Synth
{
	public Synth()
	{
		
	}
	
	String getTurtleHilbert(int order)
	{
		String A = "LBFRAFARFBL";
		String B = "RAFLBFBLFAR";
		String turtle = A;
		
		for(int o = 1; o < order; o++)
		{
			StringBuilder newTurtle = new StringBuilder();
			for(int i = 0; i < turtle.length(); i++)
			{
				if(turtle.charAt(i) == 'A')
				{
					//sub a
					newTurtle.append(A);
				}
				else if(turtle.charAt(i) == 'B')
				{
					//sub b
					newTurtle.append(B);
				}
				else
				{
					//no sub
					newTurtle.append(turtle.charAt(i));
				}
			}
			turtle = newTurtle.toString();
		}
		turtle = turtle.replace("A","");
		turtle = turtle.replace("B","");
		return turtle;
	}
	
	int[][] hilbert2n(int n)
	{
		if(n > 15)
		{
			throw new NumberFormatException("Hilbert curve dimension too big. hilbert2n creates enough curve for a 2^n x 2^n image.");
		}
		int dim = 1 << n;
		int c[][] = new int[(int)Math.pow(dim,2)][2];
		c[0][0] = 0;
		c[0][1] = 0;
		String turtle = getTurtleHilbert(n);
		int direction = 0;//down
		int idx = 1;
		
		for(int i = 0; i < turtle.length(); i++)
		{
			switch (turtle.charAt(i))
			{
				case 'F':
					switch(direction)
					{
						case 0:
							//down
							c[idx][0] = c[idx - 1][0];
							c[idx][1] = c[idx - 1][1] + 1;
							break;
						case 1:
							//left
							c[idx][0] = c[idx - 1][0] - 1;
							c[idx][1] = c[idx - 1][1];
							break;
						case 2:
							//up
							c[idx][0] = c[idx - 1][0];
							c[idx][1] = c[idx - 1][1] - 1;
							break;
						case 3:
							//right
							c[idx][0] = c[idx - 1][0] + 1;
							c[idx][1] = c[idx - 1][1];
					}
					idx++;
					break;
				case 'R':
					direction = (direction + 1) % 4;
					break;
				case 'L':
					direction = (direction - 1) % 4;
					if(direction < 0)
						direction = 4 + direction;
			}
		}
		
		return c;
	}
	
	Pixel[][] visintTurtleHistBin(long[] S, int sizex, int sizey, int pitch)
	{
		Pixel[][] img = new Pixel[sizex][sizey];
		for(int x = 0; x < sizex; x++)
		{
			for(int y = 0; y < sizey; y++)
			{
				img[x][y] = new Pixel();
			}
		}
		
		String[] strSeq = seqToStringA(S,3);
		
		Turtle turtle = new Turtle(sizex>>1,sizey>>1);
		int strAIdx = 0, strInteriorIdx = 0;
		for(long l : S)
		{
			if(turtle.x > 0 && turtle.y > 0 && turtle.x < sizex && turtle.y < sizey)
				img[turtle.x][turtle.y].incrementColors(pitch);
			int n = Integer.parseInt(Character.toString(strSeq[strAIdx].charAt(strInteriorIdx)));
			strInteriorIdx++;
			if(strInteriorIdx == strSeq[strAIdx].length())
			{
				strInteriorIdx = 0;
				strAIdx++;
			}
			
			switch(n)
			{
				case 0:
					//forward
					turtle.stepForward();
					break;
				case 1:
					//turn right
					turtle.turnRight();
					break;
				case 2:
					//turn left
					turtle.turnLeft();
			}
		}
		
		return img;
	}
	
	Pixel[][] visintHil2(long[] S, int base, int[][] order)
	{
		String[] strSeq = seqToStringA(S,base);
		int digCount = 0;
		for(String s : strSeq)
		{
			digCount += s.length();
		}
		int sequenceMaxLength = digCount/9;
		if(sequenceMaxLength < order.length)
			return null;
		
		int sizex = (int)(Math.sqrt(order.length));
		int sizey = sizex;
		Pixel[][] img = new Pixel[sizex][sizey];
		
		int strAIdx = 0, strInteriorIdx = 0;
		for (int[] xy : order)
		{
			int x = xy[0];
			int y = xy[1];
			
			img[x][y] = new Pixel();
			int[] temp = getNextNInStrSeq(strSeq,strAIdx,strInteriorIdx,base);
			int r = temp[0];
			strAIdx = temp[1];
			strInteriorIdx = temp[2];
			temp = getNextNInStrSeq(strSeq,strAIdx,strInteriorIdx,base);
			int g = temp[0];
			strAIdx = temp[1];
			strInteriorIdx = temp[2];
			temp = getNextNInStrSeq(strSeq,strAIdx,strInteriorIdx,base);
			int b = temp[0];
			strAIdx = temp[1];
			strInteriorIdx = temp[2];
			
			r &= 0xFF;
			g &= 0xFF;
			b &= 0xFF;
			
			img[x][y].setColors(r,g,b);
		}
		return img;
	}
	
	long max(long[] s)
	{
		long r = Long.MIN_VALUE;
		for(long l : s)
		{
			if (l > r)
				r = l;
		}
		return r;
	}
	
	long min(long[] s)
	{
		long r = Long.MAX_VALUE;
		for(long l : s)
		{
			if (l < r)
				r = l;
		}
		return r;
	}
	
	Pixel[][] visintHil1Color(long[] S, int[][] order, int normalization)
	{
		int numPixels = order.length;
		int sizex = (int)Math.sqrt(numPixels);
		if(numPixels > S.length)
		{
			System.out.println("visintHil: not enough sequence generated");
			System.exit(5);
		}
		int sizey = sizex;
		Pixel[][] img = new Pixel[sizex][sizey];
		double smax = (double)max(S);
		double smin = (double)min(S);
		
		int seqIdx = 0;
		for (int[] xy : order)
		{
			int x = xy[0];
			int y = xy[1];
			img[x][y] = new Pixel();
			double scale = 1.0;
			switch(normalization)
			{
				case 0:
					scale = 1.0 - ((double) seqIdx) / ((double) S.length);//growth-normalized
					break;
				case 1:
					scale = (((double) seqIdx) / (smax - smin)) + ((smin) / (smax - smin));//normalized
					break;
				case 2:
					scale = 255.0 * (((double) seqIdx) / (smax - smin)) + ((smin) / (smax - smin));//scale-normalized
					break;
			}
			int r = 0;
			if(normalization != -1)
				r = (int)((double)(S[seqIdx++] & Integer.MAX_VALUE) * scale);
			else
				r = (int)(S[seqIdx++] & (long)Integer.MAX_VALUE);
			int g = r;
			int b = g;
			
			r &= 0xFF;
			g &= 0xFF;
			b &= 0xFF;
			
			img[x][y].setColors(r,g,b);
		}
		return img;
	}
	
	Pixel[][] visintHil(long[] S, int[][] order)
	{
		int numPixels = order.length;
		int sizex = (int)Math.sqrt(numPixels);
		if(numPixels > (S.length / 3))
		{
			System.out.println("visintHil: not enough sequence generated");
			System.exit(5);
		}
		int sizey = sizex;
		Pixel[][] img = new Pixel[sizex][sizey];
		double smax = (double)max(S);
		double smin = (double)min(S);
		
		int seqIdx = 0;
		for (int[] xy : order)
		{
			int x = xy[0];
			int y = xy[1];
			img[x][y] = new Pixel();
//			double scale = 1.0 - ((double)seqIdx) / ((double)S.length);//growth-normalized
//			double scale = (((double) seqIdx) / (smax - smin)) + ((smin) / (smax - smin));//normalized
//			double scale = 255.0*(((double) seqIdx) / (smax - smin)) + ((smin) / (smax - smin));//scale-normalized
//			int r = (int)((double)(S[seqIdx++] & Integer.MAX_VALUE) * scale);
			int r = (int)(S[seqIdx++] & (long)Integer.MAX_VALUE);
			int g = r;
			int b = g;
			
			r &= 0xFF;
			g &= 0xFF;
			b &= 0xFF;
			
			img[x][y].setColors(r,g,b);
		}
		return img;
	}
	
	Pixel[][] visintHilDiff(long[] S1, long[] S2, int[][] order)
	{
		int numPixels = order.length;
		int sizex = (int)Math.sqrt(numPixels);
		if(numPixels > (S1.length / 3))
			System.exit(5);
		int sizey = sizex;
		Pixel[][] img = new Pixel[sizex][sizey];
		
		int seqIdx = 0;
		for (int[] xy : order)
		{
			int x = xy[0];
			int y = xy[1];
			img[x][y] = new Pixel();
			int r = (int)S1[seqIdx] - (int)S2[seqIdx++];
			int g = (int)S1[seqIdx] - (int)S2[seqIdx++];
			int b = (int)S1[seqIdx] - (int)S2[seqIdx++];
			
			r &= 0xFF;
			g &= 0xFF;
			b &= 0xFF;
			
			img[x][y].setColors(r,g,b);
		}
		return img;
	}
	
	Pixel[][] visint3(long[] S1, long[] S2, long[] S3, int[][] order)
	{
		if(S1.length != S2.length || S2.length != S3.length)
			System.exit(3);
		int numPixels = S1.length / 3;
		int sizex = (int)Math.sqrt(numPixels);
		int sizey = sizex;
		int sqx = 0;
		Pixel[][] img = new Pixel[sizex][sizey];
		for(int[] xy : order)
		{
			int x = xy[0];
			int y = xy[1];
			img[x][y] = new Pixel();
			int r = (int) (S1[sqx] & 0xFF);
			int g = (int) (S2[sqx] & 0xFF);
			int b = (int) (S3[sqx] & 0xFF);
			sqx++;
			img[x][y].setColors(r, g, b);
		}
		return img;
	}
	
	Pixel[][] black(int sizex, int sizey)
	{
		Pixel[][] img = new Pixel[sizex][sizey];
		for(int x = 0; x < sizex; x++)
		{
			for(int y = 0; y < sizey; y++)
			{
				img[x][y] = new Pixel();
				img[x][y].setColors(0,0,0);
			}
		}
		return img;
	}
	
	String[] seqToStringA(long[] sequence,int base)
	{
		String r[] = new String[(int)Math.ceil((double)sequence.length / 32.0)];
		
		for(int i = 0; i < r.length; i++)
		{
			StringBuilder temp = new StringBuilder();
			int bound = (i << 5) + 32;
			for(int j = i << 5; j < bound && j < sequence.length; j++)
			{
				temp.append(Long.toString(sequence[j],base));
			}
			r[i] = temp.toString();
		}
		return r;
	}
	
	int[] getNextNInStrSeq(String strSeq[], int arrayIdx, int strIdx, int base)
	{
		int width = (int)(Math.ceil(Math.log(255)/Math.log(base)));
		char[] cs = new char[width];
		for(int i = 0; i < width; i++)
		{
			cs[i] = strSeq[arrayIdx].charAt(strIdx++);
			if(strIdx == strSeq[arrayIdx].length())
			{
				strIdx = 0;
				arrayIdx++;
				if(arrayIdx == strSeq.length)
				{
					int cv = 0;
					for(int j = 0; j <= i; j++)
					{
						cv += Integer.parseInt(Character.toString(cs[j]),base);
					}
					return new int[]{cv,0,0};
				}
			}
		}
		int cv = Integer.parseInt(new String(cs),base);
		return new int[]{cv,arrayIdx,strIdx};
	}
	
	Pixel[][] visint2(long[] sequence,int base)
	{
		String[] strSeq = seqToStringA(sequence,base);
		int digCount = 0;
		for(String s : strSeq)
		{
			digCount += s.length();
		}
		int numPixels = digCount/9;
		
		int sizex = (int)(Math.sqrt(numPixels));
		int sizey = sizex;
		Pixel[][] img = new Pixel[sizex][sizey];
		
		int strAIdx = 0, strInteriorIdx = 0;
		
		for(int x = 0; x < sizex; x++)
		{
			for(int y = 0; y < sizey; y++)
			{
				img[x][y] = new Pixel();
				int[] temp = getNextNInStrSeq(strSeq,strAIdx,strInteriorIdx,base);
				int r = temp[0];
				strAIdx = temp[1];
				strInteriorIdx = temp[2];
				temp = getNextNInStrSeq(strSeq,strAIdx,strInteriorIdx,base);
				int g = temp[0];
				strAIdx = temp[1];
				strInteriorIdx = temp[2];
				temp = getNextNInStrSeq(strSeq,strAIdx,strInteriorIdx,base);
				int b = temp[0];
				strAIdx = temp[1];
				strInteriorIdx = temp[2];
				
				r &= 0xFF;
				g &= 0xFF;
				b &= 0xFF;
				
				img[x][y].setColors(r,g,b);
			}
		}
		return img;
	}
	
	Pixel[][] visint(long[] sequence)
	{
		int numPixels = sequence.length / 3;
		int sizex = (int)Math.sqrt(numPixels);
		int sizey = sizex;
		Pixel[][] img = new Pixel[sizex][sizey];
		
		int seqIdx = 0;
		for(int x = 0; x < sizex; x++)
		{
			for(int y = 0; y < sizey; y++)
			{
				img[x][y] = new Pixel();
				int r = (int)sequence[seqIdx++];
				int g = (int)sequence[seqIdx++];
				int b = (int)sequence[seqIdx++];
				
				r &= 0xFF;
				g &= 0xFF;
				b &= 0xFF;
				
				img[x][y].setColors(r,g,b);
			}
		}
		return img;
	}
	
	void writeToFile(Pixel[][] iar, String filename)
	{
		BufferedImage img = new BufferedImage(iar.length, iar[0].length, BufferedImage.TYPE_INT_RGB);
		int x = 0;
		for(Pixel[] row : iar)
		{
			int y = 0;
			for(Pixel p : row)
			{
				int r = p.getRed(), g = p.getGreen(), b = p.getBlue();
				int rgbint = r;
				rgbint = (rgbint << 8) + g;
				rgbint = (rgbint << 8) + b;
				img.setRGB(x,y,rgbint);
				y++;
			}
			x++;
		}
		
		File file = new File(filename + ".png");
		try{
		ImageIO.write(img, "png", file);}
		catch(Exception e){System.out.println("Something went wrong in Synth.writeToFile() having to do with " + e);}
	}
}