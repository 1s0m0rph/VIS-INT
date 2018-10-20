/*
  Processing.js code for "Shift similarity diagram" of primes mod n

  Written by Sebastian Bozlee on 9/28/2018
  Email: sebastian dot bozlee AT colorado dot edu 
*/

// Global variables
int size_o_screen = 800;
int frames_mouse_held = 0;
int animation_delay_frames = 5;
int mouse_is_over = false;
PImage img;

int modulus = 3;
int[] sequence;

int text_ascent;
int text_descent;
int text_padding = 3;

// Perhaps bad style, but I don't think we have enum types. Let's pretend.
int ENUM_PRIME_SEQ = 0;
int ENUM_FIBONACCI_SEQ = 1;
int ENUM_LUCAS_SEQ = 2;
int ENUM_RECAMAN_SEQ = 3;
int ENUM_THUE_MORSE_SEQ = 4;
int ENUM_EDS_SEQ = 5;

// Hope to eventually make this variable accessible from the UI.
int which_sequence = ENUM_EDS_SEQ;

// ---------------------
//   Utility functions
// ---------------------

boolean isPrime(int n)
{
    if (n <= 1)
        return false;

    for (int i = 2; i*i <= n; i++)
    {
        if (n % i == 0)
            return false;
    }

    return true;
}

// Returns the multiplicative inverse of n modulo m between 1 and m,
// Assumes m is prime and n has an inverse.
int inverseModulo(int n, int m)
{
   int n_inv = 1;
   while ((n_inv * n) % m != 1)
       n_inv++;
   
   return n_inv;
}

// Creates a table of inverses modulo m for fast computation.
// Assumes m is prime.
int[] createInverseTable(int m)
{
	int[] inv = new int[m];
	
	inv[0] = -1; // Not using that spot.
	for (int i = 1; i < m; i++)
		inv[i] = inverseModulo(i, m);
	
	return inv;
}

// Draws a textbox with string s, upper left coordinate (x,y),
// and background color (r,g,b).
void textBox(String s, int x, int y, int r, int g, int b)
{
    fill(0);
    rect(x - text_padding, y - text_ascent - text_padding, textWidth(s) + 2*text_padding, text_ascent + text_descent + 2*text_padding);
    fill(r, g, b);
    text(s, x, y);
}

// Some sequences need to be recomputed when the modulus changes,
// while others do not. The first ones are computed during "setup"
// while latter are computed during an "update."

// ----------------------------------
//   Code for setting up sequences. 
// ----------------------------------

// Sets up whatever sequence we've chosen for this run.
// Responsible for knowing which sequences need setup.
void setupSequence(int num_terms, int m)
{
	sequence = new int[num_terms];
	
	switch (which_sequence)
	{
		case ENUM_PRIME_SEQ:
            setupPrimes();
			break;
		
		case ENUM_FIBONACCI_SEQ:
		    updateFibonacci(m);
			break;
			
        case ENUM_LUCAS_SEQ:
		    updateLucas(m);
			break;
			
		case ENUM_RECAMAN_SEQ:
		    setupRecaman();
			break;
			
	    case ENUM_THUE_MORSE_SEQ:
		    updateThueMorse(m);
			break;
			
		case ENUM_EDS_SEQ:
		    updateEDS(m);
			break;
	}
}

void setupPrimes()
{
    int insertion_point = 0;
    
    for (int n = 2; insertion_point < sequence.length; n++)
    {
        boolean n_looks_prime = true;
        for (int i = 0; i < insertion_point; i++)
        {
            if (n % sequence[i] == 0)
            {
                // Failed the divisibility test. Try next n.
                n_looks_prime = false;
                continue;
            }
        }
        
        if (n_looks_prime)
        {
            // Passed the divisibility test. Record n.
            sequence[insertion_point] = n;
            insertion_point++;
        }
    }
}

// Credit to Daniel
void setupRecaman()
{
    // 0th bit of seen[0] is i=0
    // Adjusted size to accommodate triangular numbers - don't think it's necessary,
    // but I can't think of a better bound on the Recaman sequence.
    long seen[] = new long[(sequence.length*(sequence.length+1) + 127)/128];
    

    for(int i = 0; i < sequence.length; i++)
    {
    	sequence[i] = -1;
    	seen[i] = 0;
    }
    sequence[0] = 0;
    seen[0] = 1;
    
    for(int i = 1; i < sequence.length; i++)
    {
    	sequence[i] = sequence[i-1] - i;
    	if(sequence[i] < 0)
    	{
    		sequence[i] = sequence[i-1] + i;
    		long bit = sequence[i] & 0x3F;
    		int pos = (int)(sequence[i] >> 6);
    		seen[pos] |= 1 << bit;
    	}
    	else
    	{
    		long bit = sequence[i] & 0x3F;//n % 64
    		int pos = (int)(sequence[i] >> 6);//n / 64
    		if(((seen[(int)pos] >> bit) & 1) != 0)
    		{
    			sequence[i] = sequence[i-1] + i;
    			bit = sequence[i] & 0x3F;
    			pos = (int)(sequence[i] >> 6);
    			seen[pos] |= 1 << bit;
    		}
    		else
    		{
    			seen[pos] |= 1 << bit;
    		}
    	}
    }
}

// --------------------------------
//   Code for updating sequences.
// --------------------------------

// Update sequence if necessary.
void updateSequence(int m)
{
	switch (which_sequence)
	{
		case ENUM_PRIME_SEQ:
		    // Nothing to do.
			break;
		
		case ENUM_FIBONACCI_SEQ:
		    updateFibonacci(m);
			break;
			
        case ENUM_LUCAS_SEQ:
		    updateLucas(m);
			break;
			
		case ENUM_RECAMAN_SEQ:
		    // Nothing to do.
			break;
			
	    case ENUM_THUE_MORSE_SEQ:
		    updateThueMorse(m);
			break;
			
		case ENUM_EDS_SEQ:
		    updateEDS(m);
			break;
	}
}

void updateFibonacci(int m)
{  
    sequence[0] = 0;
    sequence[1] = 1;
    
    for (int i = 2; i < sequence.length; i++)
    {
    	sequence[i] = (sequence[i - 1] + sequence[i - 2]) % m;
    }
}

void updateLucas(int m)
{
    sequence[0] = 2;
    sequence[1] = 1;
    
    for (int i = 2; i < sequence.length; i++)
    {
    	sequence[i] = (sequence[i - 1] + sequence[i - 2]) % m;
    }
}

void updateEDS(int modulus)
{
    if (!isPrime(modulus))
    {
        for (int i = 0; i < sequence.length; i++)
            sequence[i] = 0;
        return;
    }

    sequence[0] = 1;
    sequence[1] = 1;
    sequence[2] = -1;
    sequence[3] = 1;

	int[] inv = createInverseTable(modulus);
	
	// The general formula is
	// W_{n + m} W_{n - m} W_r^2 = W_{n + r} W_{n - r} W_m^2 - W_{m + r} W_{m - r} W_n^2
	// where r < m < n
    for (int i = 5; i < sequence.length + 1; i++)
    {
		boolean found_relation = false;
        
		// Iterate over all triples (r, n, m) so that n + m = i and r < m < n until one "works."
		int r;
		int m;
		int n;
		for (r = 1; r < i - r && !found_relation; r++)
		{
			for (m = r + 1; m < i - m && !found_relation; m++)
			{
				n = i - m;
				
				int denominator = (sequence[n - m - 1] * sequence[r - 1] * sequence[r - 1]) % modulus;
				
				// Fix for non-mathy modular behavior (negative results when modding a negative number.)
				denominator = (denominator + modulus) % modulus;
				
				if (denominator != 0)
				{
					// Again, the formula is:
					// W_{n + m} W_{n - m} W_r^2 = W_{n + r} W_{n - r} W_m^2 - W_{m + r} W_{m - r} W_n^2
					// Here we've solved for W_{n + m} a.k.a. sequence[i - 1].
					// The reason for the -1s everywhere is that the math formulas are 1-indexed while Processing.js is 0-indexed.
					sequence[i - 1] = ( ( sequence[n + r - 1] * sequence[n - r - 1] * sequence[m - 1] * sequence[m - 1]
									- sequence[m + r - 1] * sequence[m - r - 1] * sequence[n - 1] * sequence[n - 1] )
									* inv[denominator] )
									% modulus;
					
					// Non-mathy mod fix.
					sequence[i - 1] = (sequence[i - 1] + modulus) % modulus;
					
					/* if (i < 15)
					{
						println("i : " + i + ", W : " + sequence[i - 1] + ", d : " + denominator + ", mod " + modulus + ", r: " + r + ", m: " + m + ", n: " + n);
					}*/
					found_relation = true;
				}
			}
	    }
    }
}

// Now is a generalized Thue-Morse, i.e.
// nth term is the sum of the base-m digits of n mod m. 
void updateThueMorse(int m)
{
    sequence[0] = 0;
    for (int i = 1; i < sequence.length; i++)
    {
		int q = i;
		int sum = 0;
		while (q != 0)
		{
    	    int r = q % m;
			q = (q - r) / m;
			sum += r;
        }
		sequence[i] = sum % m;
    }
}

// Setup the Processing Canvas
void setup()
{
    size( size_o_screen, size_o_screen );
    frameRate( 15 );
  
    PFont fontA = loadFont("times");
    textFont(fontA, 14);
    text_ascent = textAscent();
    text_descent = textDescent();
  
    setupSequence(2*size_o_screen, modulus);
  
    // allocate image ahead of time - reduces lag
    img = createImage(size_o_screen, size_o_screen, RGB);
    update_img();
}

void mouseOver()
{
    mouse_is_over = true;
}

void mouseOut()
{
    mouse_is_over = false;
}

void update_img()
{
    updateSequence(modulus);
    
    for (int x = 0; x < size_o_screen; x++)
    {
        for (int y = 0; y < size_o_screen; y++)
        {
            if ((sequence[x + y] - sequence[x]) % modulus == 0)
            {
                img.pixels[size_o_screen*y + x] = color(255);
            }
            else
            {
                img.pixels[size_o_screen*y + x] = color(0);
            }
        }
    }
}



// Main draw loop
void draw()
{
    // Faster to draw to an image first than directly on the screen.
    
    // Logic for whether to change modulus, update the image if necessary.
    if (mousePressed && (mouseButton == LEFT))
    {
    	if (frames_mouse_held == 0 || frames_mouse_held >= animation_delay_frames)
    	{
            modulus++;
    		update_img();
    	}
    	
    	frames_mouse_held++;
    }
    else if (mousePressed && (mouseButton == RIGHT))
    {
        if ((frames_mouse_held == 0 || frames_mouse_held >= animation_delay_frames) && modulus > 2)
        {
            modulus--;
    		update_img();
    	}
    	
    	frames_mouse_held++;
    }
    else
    {
    	frames_mouse_held = 0;
    }
    
    // Display the image
    image(img, 0, 0);
    
    textBox("shift similarity of sequence mod m = " + modulus, size_o_screen - 350, size_o_screen - 25, 255, 255, 255);
    
    /*if (mouse_is_over)
    {
    	textBox("(" + sequence[mouseX] + ", " + sequence[mouseX + mouseY] + ") -> ("
    	+ sequence[mouseX] % modulus + ", " + sequence[mouseX + mouseY] % modulus + ")",
    	mouseX + 8, mouseY + 8, 255, 255, 255);
    }*/
}


