# Towerpower [![Release](https://img.shields.io/github/v/release/GraxCode/towerpower)](https://github.com/GraxCode/idiots-pgp/releases) [![Downloads](https://img.shields.io/github/downloads/GraxCode/idiots-pgp/total)](https://github.com/GraxCode/towerpower/releases)
A tool made in groovy to show the beauty of math. Visualizes the infinite exponentiation tower of each complex number on the plane:\

![equation](res/power.png)\

This is also called tetration escape. Black means the equation exploded after some iterations.
Towerpower also has alternative modes with slightly modified equations, where you might find unexpected things, 
like a mandelbrot hidden in between the "sin" mode. Check out [PaintPanel#findPointColor](src/main/groovy/me/nov/towerpower/ui/PaintPanel.groovy#L260) to find out what each mode does.
You can play with the settings to get even prettier results.
## Gallery

Here are some examples of images towerpower produced.

| | | |
|:-------------------------:|:-------------------------:|:-------------------------:|
| <img width="1024" src="res/ex1.png"> Tetration escape while also changing the base | <img width="1024" src="res/ex2.png"> A mandelbrot hidden in the "sin" mode |<img width="1024" src="res/ex3.png"> The "exp" mode |
| <img width="1024" src="res/ex4.png"> Interesting structures in the "sin" mode | <img width="1024" src="res/ex5.png"> Interesting structures in the "sin" mode | <img width="1024" src="res/ex6.png"> Tetration escape patterns |
| <img width="1024" src="res/ex7.png"> Tetration escape patterns | <img width="1024" src="res/ex8.png"> Tetration escape patterns | <img width="1024" src="res/ex9.png"> Tetration escape patterns |
## Notice
The more you zoom in, the more inaccurate values will get, so don't be surprised with weird patterns.