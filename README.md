# Towerpower [![Release](https://img.shields.io/github/v/release/GraxCode/towerpower)](https://github.com/GraxCode/idiots-pgp/releases) [![Downloads](https://img.shields.io/github/downloads/GraxCode/idiots-pgp/total)](https://github.com/GraxCode/towerpower/releases)
A tool made in groovy to show the beauty of math. Visualizes the infinite exponentiation tower of each complex number on the plane:\
<p align="center">
    ![equation](res/power.png)\
</p>
This is also called tetration escape. Black means the equation exploded after some iterations.
Towerpower also has alternative modes with slightly modified equations, where you might find unexpected things, 
like a mandelbrot hidden in between the "sin" mode. Check out [`PaintPanel#findPointColor`]((src/main/groovy/me/nov/towerpower/ui/PaintPanel#L260) to find out what each mode does.
You can play with the settings to get even prettier results.
## Gallery

Here are some examples of images towerpower produced.

![ex1](res/ex1.png)
![ex2](res/ex2.png)
![ex3](res/ex3.png)
![ex4](res/ex4.png)
![ex5](res/ex5.png)
![ex6](res/ex6.png)
![ex7](res/ex7.png)
![ex8](res/ex8.png)
![ex9](res/ex9.png)
![ex10](res/ex10.png)

## Notice
The more you zoom in, the more inaccurate values will get, so don't be surprised with weird patterns.