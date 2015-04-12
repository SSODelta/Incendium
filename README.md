# Incendium

Incendium is a fractal animation program written in Java 7. It includes a both a core engine which is used to generate the actual fractals, and a stand-alone executable which enables a user to use this engine with a GUI. To download the GUI, head down to **Binaries** and download ```incendium-gui-vXXXX.jar```. If however, you just want to use the core engine, download ```incendium-core-vXXXX.jar``` and reference it as usual in your IDE of choice.

For the core engine, you will need to download all dependencies listed under **Core** and reference them as well if you want the program to work. If you're just downloading the GUI, all libraries will be packaged into the .jar file and you will only need to run it as ```java -jar incendium-core-vXXXX.jar```. 
 
# How to use

# License

The Incendium core, GUI and source code (excluding external libraries/programs) is released under a [CC BY 4.0](http://creativecommons.org/licenses/by/4.0/)-license.

# Binaries

JAR files:

 * [incendium-core-v1.0.0.jar](https://dl.dropboxusercontent.com/u/19633784/inc/incendium-core-v1.0.0.jar) (10.96 Kb)
 * [incendium-gui-v1.0.0.jar](https://dl.dropboxusercontent.com/u/19633784/inc/incendium-gui-v1.0.0.jar) (7.08 Mb)

# Dependencies

Incendium uses the following external libraries/programs:

**Core**

* [Symja - Algebra Library](https://bitbucket.org/axelclk/symja_android_library/wiki/Home)
* [amoeller - Automaton](http://www.brics.dk/automaton)

**Full**
 * [Apache Commons Exec](https://commons.apache.org/proper/commons-exec/)
 * [MJPEG Tools - png2yuv.exe](http://mjpeg.sourceforge.net/)
 * [WebM Project - vpxenc.exe](http://www.webmproject.org/docs/encoder-parameters/)
