ioio-easydriver
===============

This sample shows how to connect a Sparkfun IOIO board to an EasyDriver board.

This example is ported from Dan Thompson's blog, and modified to be run on the IOIO board using an Android application project for Eclipse.

See [com.ioioapp.ioioeasydriver.MainActivity](https://github.com/aalbino/ioio-easydriver/blob/master/IOIOEasyDriver/src/com/ioioapp/ioioeasydriver/MainActivity.java) for more info.


I hooked up a 9V battery as a power source for the Stepper motor, but only because I didn't have any 12V source I could readily use. 9V typically die really fast, so, use a 12V source if you can, and remember to adjust your EasyDriver to only provide the right amount of power to your stepper motor using the on-board potentiometer.

See this example in action at https://www.youtube.com/watch?v=4mkh98Ik1RY&feature=youtube_gdata_player
