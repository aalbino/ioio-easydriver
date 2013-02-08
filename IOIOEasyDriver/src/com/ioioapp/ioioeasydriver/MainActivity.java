package com.ioioapp.ioioeasydriver;

// This stuff is imported for us when a new Android application is created.
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

//Let's import our resources. We need them for this example.
import com.ioioapp.ioioeasydriver.R;
import com.ioioapp.ioioeasydriver.MainActivity.Looper;

// We choose to go forward or reverse according to the selected radio button
import android.widget.RadioButton;
import android.widget.RadioGroup;

// We log stuff in this example... so yeah
import android.util.Log;

// Import our IOIO library stuff
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

/**
 * Dan Thompson's EasyDriver example, ported to 
 * Android Java for the SparkFun IOIO by myself (Alex Albino, Femtoduino.com).
 * 
 * You will need to download the IOIOLibraries (IOIOLib, and IOIOLibBT), and
 * remember to include the ioiolib.jar and ioiolibbt.jar files in your /libs/
 * folder in any future projects you create. For example, See the SPIMotor/libs/
 * directory.
 * 
 * Remember to enable "Developer Tools" on your android device
 * 
 * I used the following setup:
 * 
 * 
 *    ....Android phone to IOIO board over Bluetooth 
 *    (remember to pair your phone with your IOIO, 
 *    default bluetooth pin is usually 4545)
 * 
 * 
 * Pin on IOIO Board		Pin on EasyDriver Board
 * ------------------------------------------------
 * 
 * GND						GND
 * 2						STEP
 * 3						DIR
 * 4						MS1
 * 5						MS2
 * 6						SLEEP
 * 
 * 
 * Pin on STM100 Stepper	Pin on EasyDriver Board
 * --------------------------------------------------------
 * Coil 1, Wire 1			A(first hole)
 * Coil 1, Wire 2			A(second hole)
 * 
 * Coil 2, Wire 1			B(first hole) 
 * Coil 2, Wire 2			B(second hole)
 * 
 * 
 * 
 * Licensed under the GNU GPL v3 or higher. See http://www.gnu.org/licenses/gpl.html
 * (Basically, free to use/distribute/modify, use at your own risk, etc...)
 * 
 * ENJOY! :-)
 * 
 *  - Alex Albino, February 8, 2013
 *    SF Bay Area
 *    
 *    Join us at meetup.com/Arduino-Bay/ for meet-ups with hands-on examples!
 *
 */
public class MainActivity extends IOIOActivity { // NOTE! We are extending IOIOActivity, not Activity.

	// Let's make variables to reference our UI controls.
	public RadioGroup rgrpDirection;
	public RadioButton rdoBack;
	public RadioButton rdoForward;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// ...Get those references to our radio buttons.
		rdoBack = (RadioButton) findViewById(R.id.radioButton1);
		rdoForward = (RadioButton) findViewById(R.id.radioButton2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}


	
	/**
	 * Our Looper class.  
	 * This handles pin status checking and what not.
	 * 
	 * Code ported from Dan Thompson's Blog:
	 * http://danthompsonsblog.blogspot.com/2010/05/easydriver-42-tutorial.html
	 */
	public class Looper extends BaseIOIOLooper {
		private DigitalOutput pinStep;
		private DigitalOutput pinDir;
		private DigitalOutput pinMS1;
		private DigitalOutput pinMS2;
		private DigitalOutput pinSleep;
		
		@Override
		protected void setup() throws ConnectionLostException {
			
			// Let's initialize the pins as digital output.
			pinStep = ioio_.openDigitalOutput(2);
			pinDir = ioio_.openDigitalOutput(3);
			
			pinMS1 = ioio_.openDigitalOutput(4);
			pinMS2 = ioio_.openDigitalOutput(5);
			
			pinSleep = ioio_.openDigitalOutput(6);
			
		}
		@Override
		public void loop() throws ConnectionLostException {
			int modeType = 1;	// This number increases by multiples of 2 each through the while loop..
								// ..to identify our step mode type.
			
			while (modeType <= 8) {	// loops the following block of code 4 times before repeating .
				
				
				// DIR - Forward or Backward
				if(rdoBack.isChecked()) {	// Set the direction change LOW (false) to HIGH (true) to go in opposite direction
					pinDir.write(false);
				} else {
					pinDir.write(true);
				}
				
				// MS1
				pinMS1.write((boolean)this.MS1_MODE(modeType));	// Set the state of MS1 based on the returned value from our custom MS1_MODE() switch statement
				
				// MS2
				pinMS2.write((boolean)this.MS2_MODE(modeType));	// Set the state of MS2 based on the returned value from our custom MS2_MODE() switch statement
				
				// SLEEP
				pinSleep.write(true);	// Set the SLEEP mode to AWAKE (represented by HIGH, boolean true)
				
				int i = 0;	// Set the counter variable.
							// Iterate for 200, then 400, then 800, then 1600 steps.
							// Then reset to 200 and start again.
				
				while (i < (modeType * 200)) {
				
					// STEP
					/**
					 * Generate a 500Hz Square wave
					 */
					pinStep.write(false);	// This LOW (false) to HIGH (true) change is what creates the..
					pinStep.write(true);	// .."Rising Edge" so the easydriver knows when to step. 
					this.timeout(1600/(modeType * 200));	// This delay time determines the speed of the stepper motor.
															// Delay shortens from 1600 to 800 to 400 to 200 then resets
					
					i++;
				}
				modeType = modeType * 2;	// Multiply the current modeType value by 2 and make the result the new value for modeType.
											// This will make the modeType variable count 1,2,4,8 each time we pass through the while loop.
				
				
				this.timeout(500);
			}
			
			pinSleep.write(false);	// Switch off the power to stepper
			Log.i("MOTOR", "SLEEPING..");
			this.timeout(1000);
			
			Log.i("MOTOR", "z");
			this.timeout(1000);
			
			Log.i("MOTOR", "z");
			this.timeout(1000);
			
			Log.i("MOTOR", "z");
			this.timeout(1000);
			
			Log.i("MOTOR", "");
			
			pinSleep.write(true);	// Switch on the power to stepper
			Log.i("MOTOR", "AWAKE!!!");
			
			this.timeout(1000);
			
		}
		
		/**
		 * This is our custom timeout function, so we don't
		 * need to write a bunch of try/catch blocks all over.
		 * @param int ms
		 */
		private void timeout(int ms) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				// Do nothing...
			}
		}
		
		private boolean MS1_MODE(int MS1_StepMode) {
			switch(MS1_StepMode) {
			case 1:
				MS1_StepMode = 0; // Step mode: Full
				break;
			case 2:
				MS1_StepMode = 1; // Step mode: Half
				break;
			case 4:
				MS1_StepMode = 0; // Step mode: Quarter
				break;
			case 8:
				MS1_StepMode = 1; // Step mode: Eight
				break;
			}
			return MS1_StepMode == 1 ? true : false;
		}
		
		private boolean MS2_MODE(int MS2_StepMode) {
			switch (MS2_StepMode) {
			case 1:
				MS2_StepMode = 0;
				break;
			case 2:
				MS2_StepMode = 0;
				break;
			case 4:
				MS2_StepMode = 1;
				break;
			case 8:
				MS2_StepMode = 1;
				break;
			}
			
			return MS2_StepMode ==  1 ? true : false;
		}
	}
	
	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
}
