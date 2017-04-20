import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.video.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class lab3_submission extends PApplet {



Movie m;
PImage img_next;
PImage img_current;
PImage current_grey;
PImage next_grey;

int framenumber = 1;

String movie_file = "monkey.avi";


public void setup() {
background(0,0,0); // background(color)

m = new Movie(this, sketchPath(movie_file)); // load up movie


//play the movie one time, no looping
m.play();
}


public void draw() {
  //current_grey = createImage(1000,700,RGB);
  //next_grey = createImage(1000,700,RGB);

  image(m,0,0);
  m.save(sketchPath("") + "monkey/"+nf(framenumber, 4) + ".tif");
  framenumber++;
}

public void movieEvent(Movie m) {
  m.read();
}
  public void settings() { 
size(1000, 700); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "lab3_submission" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
