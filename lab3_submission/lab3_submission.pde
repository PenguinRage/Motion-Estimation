import processing.video.*;

Movie m;
PImage img_next;
PImage img_current;
PImage current_grey;
PImage next_grey;

int framenumber = 1;

String movie_file = "monkey.avi";


void setup() {
background(0,0,0); // background(color)

m = new Movie(this, sketchPath(movie_file)); // load up movie
size(1000, 700);

//play the movie one time, no looping
m.play();
}


void draw() {
  //current_grey = createImage(1000,700,RGB);
  //next_grey = createImage(1000,700,RGB);

  image(m,0,0);
  m.save(sketchPath("") + "monkey/"+nf(framenumber, 4) + ".tif");
  framenumber++;
}

void movieEvent(Movie m) {
  m.read();
}
