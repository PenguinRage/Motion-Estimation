PImage current;
PImage next;
PImage grey_current;
PImage grey_next;

int total_number_of_frames = 700;
int frame_number = 0;

// Global Macro variables
int WIDTH;
int HEIGHT;
int BLOCK_SIZE = 5;

void setup()
{
  // Load the first pre-saved image
  current = loadImage(sketchPath("") + "monkey/"+nf(frame_number,4) + ".tif");
  // Specify size
  WIDTH = current.width;
  HEIGHT = current.height;

  grey_current = createImage(WIDTH, HEIGHT, RGB);

  // Greyscale first image
  float r,g,b;
  color grey;

  for(int h = 0; h < HEIGHT; h++)
  {
    for(int w = 0; w < WIDTH; w++)
    {
      int loc = h * WIDTH + w;

      r = red(current.pixels[loc]) * 0.21267;
      g = green(current.pixels[loc]) * 0.715160;
      b = blue(current.pixels[loc]) * 0.072169;
      grey = color(r + g + b);
      grey_current.pixels[loc] = grey;
    }
  }

  surface.setSize(WIDTH, HEIGHT);
}

void draw()
{
  // Check if all frames have used, prevent OutOfBoundException
  if (frame_number < total_number_of_frames)
  {
    // Get the next images
    next = loadImage(sketchPath("") + "monkey/"+nf(frame_number + 1,4) + ".tif");
    grey_next = createImage(WIDTH, HEIGHT, RGB);

    float r,g,b;
    color grey;

    // grey scale the images
    for(int h = 0; h < HEIGHT; h++)
    {
      for(int w = 0; w < WIDTH; w++)
      {
        int loc = w + h * WIDTH;

        r = red(next.pixels[loc]) * 0.21267;
        g = green(next.pixels[loc]) * 0.715160;
        b = blue(next.pixels[loc]) * 0.072169;
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
        int current_block[] = macroblock_areas(w, h);
        //int current_center_x = w + BLOCK_SIZE / 2;
        //int current_center_y = h + BLOCK_SIZE / 2;
        double min_ssd = -1;

        // from all other block areas in next frame, find best match
        int best_match = 0;
        //int best_match_x = 0;
        //int best_match_y = 0;
        for(int tmp_h = 0; tmp_h < HEIGHT - BLOCK_SIZE; tmp_h += BLOCK_SIZE)
        {
          for(int tmp_w = 0; tmp_w < WIDTH - BLOCK_SIZE; tmp_w += BLOCK_SIZE)
          {
            // for each of the other block area in next frame
            color next_block[] = macroblock_areas(tmp_w, tmp_h);

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
              //best_match_x = tmp_w + BLOCK_SIZE / 2;
              //best_match_y = tmp_h + BLOCK_SIZE / 2;
            }
            else if(tmp < min_ssd)         // if has smaller ssd, set to min_ssd
            {
              min_ssd = tmp;
              best_match = (tmp_h + BLOCK_SIZE / 2) * WIDTH + (tmp_w + BLOCK_SIZE / 2);
              //best_match_x = tmp_w + BLOCK_SIZE / 2;
              //best_match_y = tmp_h + BLOCK_SIZE / 2;
            }

            // highlight the best match
            //stroke(255);
            //line(current_center_x, current_center_y, best_match_x, best_match_y);
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
double calc_ssd(int block1[], int block2[])
{
  double ssd = 0;
  for(int h = 0; h < BLOCK_SIZE * BLOCK_SIZE; h++)
  {
    // get grey value
    int grey1 = (int) red(grey_current.pixels[block1[h]]);
    int grey2 = (int) red(grey_next.pixels[block2[h]]);

    int diff = grey1 - grey2;

    // if difference is too small, don't highlight
    if(grey1 > 50) // For lighter backgrounds >, for darker backgrounds <
    {
      return -10;
    }
    ssd += diff * diff;
  }
  ssd = Math.sqrt(ssd);
  return ssd;
}

// for each block area, return the accordingly area pixel indexs on the frame
color[] macroblock_areas(int x, int y)
{
  color block[] = new color[BLOCK_SIZE * BLOCK_SIZE];
  int pos = 0;
  // Copy the pixels to bloc
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
