public class Main
{
	public static void main(String[] args)
	{
		Hilbert h = new Hilbert();
		Ulam u = new Ulam();
		Sequence sg = new Sequence();
		GOLGen gg = new GOLGen();
		
		int n = 4;
		int sequenceLength = (int)(Math.pow(4,n));
		int[][] trav = h.hilbert2n(n);
//		int n = 500;
//		int sequenceLength = (int)Math.pow(n<<1,2);
//		int[][] trav = u.getSpiral(n);
		
//		double epsilon = 2.5;//note that we use X^(1/epsilon) as our limit for checking
		long[] S = sg.nNaturals(sequenceLength);
		gg.GOLGollyBitvec(trav,"hilbert" + n + "NaturalsBitvec.rle",S,"B1357/S1357");
	}
}
