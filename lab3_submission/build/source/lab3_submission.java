import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class lab3_submission extends PApplet {

PImage current;
PImage next;
PImage grey_current;
PImage grey_next;

int total_number_of_frames = 700;
int frame_number = 0;

// Global Macro variables
int WIDTH;
int HEIGHT;
int BLOCK_SIZE = 9;

public void setup()
{
  // Load the first pre-saved image
  current = loadImage(sketchPath("") + "monkey/"+nf(frame_number,4) + ".tif");
  // Specify size
  WIDTH = current.width;
  HEIGHT = current.height;

  grey_current = createImage(WIDTH, HEIGHT, RGB);

  // Greyscale first image
  float r,g,b;
  int grey;

  for(int h = 0; h < HEIGHT; h++)
  {
    for(int w = 0; w < WIDTH; w++)
    {
      int loc = h * WIDTH + w;

      r = red(current.pixels[loc]) * 0.21267f;
      g = green(current.pixels[loc]) * 0.715160f;
      b = blue(current.pixels[loc]) * 0.072169f;
      grey = color(r + g + b);
      grey_current.pixels[loc] = grey;
    }
  }

  surface.setSize(WIDTH, HEIGHT);
}

public void draw()
{
  // Check if all frames have used, prevent OutOfBoundException
  if (frame_number < total_number_of_frames)
  {
    // Get the next images
    next = loadImage(sketchPath("") + "monkey/"+nf(frame_number + 1,4) + ".tif");
    grey_next = createImage(WIDTH, HEIGHT, RGB);

    float r,g,b;
    int grey;

    // grey scale the images
    for(int h = 0; h < HEIGHT; h++)
    {
      for(int w = 0; w < WIDTH; w++)
      {
        int loc = w + h * WIDTH;

        r = red(next.pixels[loc]) * 0.21267f;
        g = green(next.pixels[loc]) * 0.715160f;
        b = blue(next.pixels[loc]) * 0.072169f;
        grey = color(r + g + b);
        grey_next.pixels[loc] = grey;
      }
    }

    // for every pair of frames
    for(int h = 0; h < HEIGHT - BLOCK_SIZE; h += BLOCK_SIZE)
    {
      for(int w = 0; w < WIDTH - BLOCK_SIZE; w += BLOCK_SIZE)
      {
        // for each of the block area in current frame
        int current_block[] = calc_macroblock_areas(w, h);

        double min_ssd = -1;

        // from all other block areas in next frame, find best match
        int best_match = 0;

        for(int tmp_h = 0; tmp_h < HEIGHT - BLOCK_SIZE; tmp_h += BLOCK_SIZE)
        {
          for(int tmp_w = 0; tmp_w < WIDTH - BLOCK_SIZE; tmp_w += BLOCK_SIZE)
          {
            // for each of the other block area in next frame
            int next_block[] = calc_macroblock_areas(tmp_w, tmp_h);

            // calulate ssd between each current block area and each next block area
            double tmp = calc_ssd(current_block, next_block);
            if(tmp == -10)
            {
              continue;
            }
            // then to find the minimum ssd, the best match
            if(min_ssd == -1)              // if first time calculate ssd
            {
              min_ssd = tmp;
              best_match = (tmp_h + BLOCK_SIZE / 2) * WIDTH + (tmp_w + BLOCK_SIZE / 2);
            }
            else if(tmp < min_ssd)         // if has smaller ssd, set to min_ssd
            {
              min_ssd = tmp;
              best_match = (tmp_h + BLOCK_SIZE / 2) * WIDTH + (tmp_w + BLOCK_SIZE / 2);
            }

            // highlight the best match
            current.pixels[best_match] = color(255);
          }
        }
      }
    }
    // Show image for display
    image(current, 0, 0);
    // Set directory
    saveFrame(sketchPath("") + "monkey_result/"+nf(frame_number, 4) + ".tif");
    current = next;
    grey_current = grey_next;
    frame_number++;
  }
  else
  {
    exit();
  }
}



// calculate ssd between two set of blocks
public double calc_ssd(int block1[], int block2[])
{
  double ssd = 0;
  for(int h = 0; h < BLOCK_SIZE * BLOCK_SIZE; h++)
  {
    // get grey value
    int current_grey = (int) red(grey_current.pixels[block1[h]]);
    int next_grey = (int) red(grey_next.pixels[block2[h]]);

    int diff = current_grey - next_grey;

    // if difference is too small, don't highlight
    if(current_grey > 50)
    {
      return -10;
    }
    ssd += diff * diff;
  }
  ssd = Math.sqrt(ssd);
  return ssd;
}

// for each block area, return the accordingly area pixel indexs on the frame
public int[] calc_macroblock_areas(int x, int y)
{
  int block[] = new int[BLOCK_SIZE * BLOCK_SIZE];
  int pos = 0;
  for(int h = y; h < BLOCK_SIZE + y; h++)
  {
    for(int w = x; w < BLOCK_SIZE + x; w++)
    {
      block[pos] = h * WIDTH + w;
      pos++;
    }
  }
  return block;
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "lab3_submission" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
