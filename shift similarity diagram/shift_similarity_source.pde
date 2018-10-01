/*
  Processing.js code for "Shift similarity diagram" of primes mod n

  Written by Sebastian Bozlee on 9/28/2018
  Email: sebastian dot bozlee AT colorado dot edu 
*/

// Global variables
int size_o_screen = 800;
int modulus = 3;
int frames_mouse_held = 0;
int animation_delay_frames = 5;
int mouse_is_over = false;
int[] primes;
PImage img;

int text_ascent;
int text_descent;
int text_padding = 3;

void setupPrimes(int num_primes)
{
    primes = new int[num_primes];
    int insertion_point = 0;
    
    for (int n = 2; insertion_point < num_primes; n++)
    {
        bool n_looks_prime = true;
        for (int i = 0; i < insertion_point; i++)
        {
            if (n % primes[i] == 0)
            {
                // Failed the divisibility test. Try next n.
                n_looks_prime = false;
                continue;
            }
        }
        
        if (n_looks_prime)
        {
            // Passed the divisibility test. Record n.
            primes[insertion_point] = n;
            insertion_point++;
        }
    }
}

// Setup the Processing Canvas
void setup()
{
    size( size_o_screen, size_o_screen );
    frameRate( 15 );
  
    PFont fontA = loadFont("courier");
    textFont(fontA, 14);
	text_ascent = textAscent();
	text_descent = textDescent();
  
    setupPrimes(2*size_o_screen);
  
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
	for (int x = 0; x < size_o_screen; x++)
    {
        for (int y = 0; y < size_o_screen; y++)
        {
            if ((primes[x + y] - primes[x]) % modulus == 0)
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

void textBox(String s, int x, int y, int r, int g, int b)
{
	fill(0);
	rect(x - text_padding, y - text_ascent - text_padding, textWidth(s) + 2*text_padding, text_ascent + text_descent + 2*text_padding);
	fill(r, g, b);
	text(s, x, y);
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
        if ((frames_mouse_held == 0 || frames_mouse_held >= animation_delay_frames) && modulus > 3)
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
	
	textBox("shift similarity of primes mod m = " + modulus, size_o_screen - 350, size_o_screen - 25, 255, 255, 255);
	
	if (mouse_is_over)
	{
		textBox("(" + primes[mouseX] + ", " + primes[mouseX + mouseY] + ") -> ("
		+ primes[mouseX] % modulus + ", " + primes[mouseX + mouseY] % modulus + ")",
		mouseX + 8, mouseY + 8, 255, 255, 255);
	}
}


