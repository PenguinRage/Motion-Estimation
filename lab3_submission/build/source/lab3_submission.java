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

PImage img_current;
PImage img_next;
PImage current_grey;
PImage next_grey;
String img_url_current;
String img_url_next;

int total_number_of_frame = 100;
int count = 0;
int load = 0;

int BLOCK_SIZE = 5;
int WIDTH = 640;
int HEIGHT = 360;

public void setup()
{
    
}

public void draw()
{
    // Check if all frames have used, prevent OutOfBoundException
    if (load < total_number_of_frame)
    {
        // Get pre-saved frame images directory
        img_current = loadImage(sketchPath("") + "bird/"+nf(load,4) + ".tif");
        img_next = loadImage(sketchPath("") + "bird/"+nf(load+1,4) + ".tif");
        current_grey = createImage(WIDTH, HEIGHT, RGB);
        next_grey = createImage(WIDTH, HEIGHT, RGB);

        // grey scale the images
        for(int h = 0; h < HEIGHT; h++)
        {
            for(int w = 0; w < WIDTH; w++)
            {
                int loc = w + h * WIDTH;

                float r1 = red(img_current.pixels[loc]) * 0.21267f;
                float g1 = green(img_current.pixels[loc]) * 0.715160f;
                float b1 = blue(img_current.pixels[loc]) * 0.072169f;
                int grey1 = color(r1 + g1 + b1);
                current_grey.pixels[loc] = grey1;

                float r2 = red(img_next.pixels[loc]) * 0.21267f;
                float g2 = green(img_next.pixels[loc]) * 0.715160f;
                float b2 = blue(img_next.pixels[loc]) * 0.072169f;
                int grey2 = color(r2 + g2 + b2);
                next_grey.pixels[loc] = grey2;
            }
        }

        // for every pair of frames
        for(int h = 0; h < HEIGHT - BLOCK_SIZE; h += BLOCK_SIZE)
        {
           for(int w = 0; w < WIDTH - BLOCK_SIZE; w += BLOCK_SIZE)
           {
               // for each of the block area in current frame
               int current_block[] = block_area_grid_index(w, h);
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
                        int next_block[] = block_area_grid_index(tmp_w, tmp_h);

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
                        img_current.pixels[best_match] = color(255);
                    }
                }
            }
        }
        // Set directory
        saveFrame(sketchPath("") + "bird_result/"+nf(load, 4) + ".tif");

        image(img_current, 0, 0);
        load++;
     }
 }


// for each block area, return the accordingly area pixel indexs on the frame
public int[] block_area_grid_index(int x, int y)
{
    int pos = 0;
    int grid_index[] = new int[BLOCK_SIZE * BLOCK_SIZE];
    for(int h = y; h < BLOCK_SIZE + y; h++)
    {
        for(int w = x; w < BLOCK_SIZE + x; w++)
        {
            grid_index[pos] = h * WIDTH + w;
            pos++;
        }
    }
    return grid_index;
}

// calculate ssd between two set of blocks
public double calc_ssd(int block1[], int block2[])
{
    double ssd = 0;
    for(int h = 0; h < BLOCK_SIZE * BLOCK_SIZE; h++)
    {
        // get grey value
        int grey1 = (int) red(current_grey.pixels[block1[h]]);
        int grey2 = (int) red(next_grey.pixels[block2[h]]);

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
  public void settings() {  size(640, 360); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "lab3_submission" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
