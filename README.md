Cube Simulators
===============
It's designed to be a collection of different type of cube puzzles for **speed** cubing via keyboard. It's based on JOGL, which is a java binding for OpenGL.

As of now, only NxNxN cubes (to be accurate, *2x2x2* up to *27x27x27*) are supported.

A timer has been added which runs as the WCA regulation. You have 15 seconds to inspect the cube after scrambling, and after that you would get a `+2` penalty. If the timer goes over 17 seoconds, you would get a `DNF`. 

How to run it
-------------
There are several ways to run this game.

###Option 1: Use Eclipse
 1. First, import/new a project from this folder.
 2. Run `oyyq.cube.simulator.Main` class as an application.
 3. Enjoy!

###Option 2: Use jdk by itself

 1. Make sure that the `gluegen-rt.jar`, `jogl-all.jar` and `CubeCalculator.jar` are in the build path.
 2. Make sure that the native libs for your platform are in the same folder of `gluten-rt.jar` and `jogl-all.jar`, respectively.
 3. Build and run `oyyq.cube.simulator.Main`.
 4. Enjoy!


Key Map
-------
The key map is almost the same as the default of [hi-games.net][], as in the list and table below.

 - `Esc` -> **Reset** the cube. When solving, give up and get a DNF.
 - `-` -> **Decrease** the cube size.
 - `+` or `=` -> **Increase** the cube size.
 - `Space` -> **Scramble** the cube when it's not scrambled, and **reset shifts** when solving.

|All|Of|The|Keys|That|Are|Used|In|The|Simulator|
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
|1|2|3 -> **left shift outwards**|4(**left shift inwards**)|5|6|7(**right shift inwards**)|8(**right shift outwards**)|9|0|
|q(**z'**)|w(**B**)|e(**L'**)|r(**Lw'**)|t(**x**)|y(**x**)|u(**Rw**)|i(**R**)|o(**B'**)|p(**z**)|
|a(**y'**)|s(**D**)|d(**L**)|f(**U'**)|g(**F'**)|h(**F**)|j(**U**)|k(**R'**)|l(**D'**)|;(**y**)|
|z|x(**Dw**)|c(**Uw'**)|v(**Lw**)|b(**x'**)|n(**x'**)|m(**Rw'**)|,(**Uw**)|.(**Dw'**)|/|

Known Issues
------------

 - The window size could be minimized when start up. It's rare, but happens.

To Do List
----------
 - [x] Timer
 - [ ] Menu and settings
 - [ ] NxMxP Cuboid
 - [ ] Megaminx
 - [ ] Pyraminx
 - [ ] Square-1
 - [ ] Skewb
 - [ ] Solver (Probably)
 - [ ] Replay (Maybe)

Q & A
----------------

>**Q: When I'm trying to run it, it crashes and the console complains that it can't load library _`what the hell is this`_.**

**A:** Make sure that `gluegen-rt-natives-{your os}-{your os arch}.jar` and `jogl-all-natives-{your os}-{your os arch}.jar` is in the classpath. For 32-bit systems the arch is normally `i586`, and for 64-bit systems it's usually `amd64`.

>**Q: I found a bug! When I just make a few moves from the initial state, And I just solve it (or reverse those moves), the timer just keeps silent!**

**A:** It's **DESIGNED** to be. It will only time when you actually solved a cube which is **scrambled by the game**. :)

>**Q: Oh I can't turn the cube!**

**A:** When you have **finished** a solve or get a `DNF`, you are not able to turn the cube until you **reset** it or **scramble** it again.

Credits
-------

I referenced to [this project][1] a lot, most part of OpenGL drawing are from that, thanks to [@xargsgrep][2] a lot!

[hi-games.net]: http://hi-games.net
[1]: https://github.com/xargsgrep/JOGLRubiksCube
[2]: https://github.com/xargsgrep