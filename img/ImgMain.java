public class ImgMain
{
	public static void main(String[] args)
	{
		Synth s = new Synth();
		Sequence sg = new Sequence();
		Hilbert h = new Hilbert();
		
		
		int n = 10;
		int sequenceLength = 3 * (int)(Math.pow(4,n));
		int[][] trav = h.hilbert2n(n);
		
		long m = 255;
		long[] fib = sg.nLinearRecurrenceModM(sequenceLength,m,1,1,1,1);
		long[] luc = sg.nLinearRecurrenceModM(sequenceLength,m,1,1,2,1);
		
		Pixel[][] img = s.visintHilDiff(fib,luc,trav);
		s.writeToFile(img,"hilbert" + n + "DiffLucFibMod" + m);
	}
}
