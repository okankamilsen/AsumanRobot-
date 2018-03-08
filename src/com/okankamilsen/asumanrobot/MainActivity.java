package com.okankamilsen.asumanrobot;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends ActionBarActivity {
	
	//TextView txtX, txtY, txtX1, txtY1, dutyCycleText, directionText;
	TextView dutyCycleText;
	JoystickView direction, servo;
	SeekBar dutyCycle;
	String sendText;
	int progress = 0;
	int joystickControl = 0;
	File imgFile;
	boolean isBusy = false;//this flag to indicate whether your async task completed or not
	boolean stop = false;//this flag to indicate whether your button stop clicked
	ToggleButton cameraBt,ledBt;
	Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        direction = (JoystickView)findViewById(R.id.joystickView);
        direction.setOnJostickMovedListener(_listener);
		//txtX = (TextView)findViewById(R.id.textViewX);
        //txtY = (TextView)findViewById(R.id.textViewY);
        //directionText = (TextView)findViewById(R.id.direction);
        
        
        imgFile = new  File("/storage/emulated/0/Download/2new.jpg");    
        servo = (JoystickView)findViewById(R.id.joystickView1);
        servo.setOnJostickMovedListener(_listener1);
		//txtX1 = (TextView)findViewById(R.id.textViewX1);
        //txtY1 = (TextView)findViewById(R.id.textViewY1);
        
        cameraBt = (ToggleButton) findViewById(R.id.toggleButton);
        cameraBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                	//camerayı açar
                	startHandler(0);
                } else {
                	//camerayı kapatır
                	startHandler(1);
    	            //RelativeLayout layout = (RelativeLayout) findViewById(R.id.relative_layout);
    	            //layout.setBackgroundColor(Color.WHITE);
                }
            }
        });
        ledBt = (ToggleButton) findViewById(R.id.ledToggle);
        ledBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                	//ledi açar
                	sendText = "led ";
                	Thread led = new Thread(new UdpClient(0));//.start();
	        		led.start();
                } else {
                	//ledi kapatır
                	Thread led = new Thread(new UdpClient(0));//.start();
                	sendText = "ledk ";
	        		led.start();
    	            //RelativeLayout layout = (RelativeLayout) findViewById(R.id.relative_layout);
    	            //layout.setBackgroundColor(Color.WHITE);
                }
            }
        });
        
        dutyCycleText = (TextView) findViewById(R.id.dutyCycleText);
        dutyCycle = (SeekBar) findViewById(R.id.seekBar1);
        dutyCycle.setMax(100);
        dutyCycle.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	          //progress = 0;
        	          @Override
        	          public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
        	              progress = progresValue;
        	              //Toast.makeText(getApplicationContext(),"Covered: " + progress + "/" + seekBar.getMax(), Toast.LENGTH_SHORT).show();
        	              //sendText += " "+progress;
        	              dutyCycleText.setText("Duty Cycle    "+progress + "/" + seekBar.getMax());
        	          }
        	          @Override
        	          public void onStartTrackingTouch(SeekBar seekBar) {
        	              //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
        	              //Toast.makeText(getApplicationContext(),"Covered: " + progress + "/" + seekBar.getMax(), Toast.LENGTH_SHORT).show();
        	        	  //dutyCycleText.setText(progress + "/" + seekBar.getMax());
        	          }
        	          @Override
        	          public void onStopTrackingTouch(SeekBar seekBar) {
        	        	  dutyCycleText.setText("Duty Cycle    "+progress + "/" + seekBar.getMax());
        	              //Toast.makeText(getApplicationContext(),"Covered: " + progress + "/" + seekBar.getMax(), Toast.LENGTH_SHORT).show();
        	        	  //Thread dutyCycle = new Thread(new UdpClient(progress));//.start();
        	        	  //dutyCycle.start();
        	          }
        	       });
 		
    }
    
    //motor control
    private JoystickMovedListener _listener = new JoystickMovedListener() {
    	
		@Override
		public void OnMoved(int pan, int tilt) {
			//txtX.setText(Integer.toString(pan));
			//txtY.setText(Integer.toString(tilt));
			if(pan == 10){
				if(tilt <= 10 && tilt > -10 && joystickControl == 1){
					//directionText.setText("right");
					sendText = "right ";
	        		Thread right = new Thread(new UdpClient(progress));//.start();
	        		right.start();
	        		Log.i("right","");
	        		joystickControl = 0;
				}
			}else if(pan == -10){
				if(tilt <= 10 && tilt > -10 && joystickControl == 1){
					//directionText.setText("left");
					sendText = "left ";
	        		Thread left = new Thread(new UdpClient(progress));//.start();
	        		left.start();
	        		Log.i("left","");
	        		joystickControl = 0;
				}
			}else if(tilt == 10){
				if(pan <= 10 && pan > -10 && joystickControl == 1){
					//directionText.setText("back");
					sendText = "back ";
	        		Thread back = new Thread(new UdpClient(progress));//.start();
	        		back.start();
	        		Log.i("back","");
	        		joystickControl = 0;
				}
			}else if(tilt == -10){
				if(pan <= 10 && pan > -10 && joystickControl == 1){
					//directionText.setText("straight");
					sendText = "straight ";
	        		Thread stop = new Thread(new UdpClient(progress));//.start();
	        		stop.start();
	        		Log.i("straight","");
	        		joystickControl = 0;
				}
			}
		}

		@Override
		public void OnReleased() {
			if(joystickControl == 0){
				sendText = "straight";
				Thread stop = new Thread(new UdpClient(0));//.start();
				stop.start();
				//txtX.setText("stopped");
				//txtY.setText("stopped");
				Log.i("stopped","");
			}
			joystickControl = 1;
		}
	}; 

	//servo control
	private JoystickMovedListener _listener1 = new JoystickMovedListener() {


		@Override
		public void OnMoved(int pan, int tilt) {
			//txtX1.setText(Integer.toString(pan));
			//txtY1.setText(Integer.toString(tilt));
			if(pan == 10){
				if(tilt <= 10 && tilt > -10 && joystickControl == 1){
					//directionText.setText("right");
					sendText = "sag ";
	        		Thread right = new Thread(new servoUdpClient());//.start();
	        		right.start();
	        		Log.i("sag","");
	        		joystickControl = 0;
				}
			}else if(pan == -10){
				if(tilt <= 10 && tilt > -10 && joystickControl == 1){
					//directionText.setText("left");
					sendText = "sol ";
	        		Thread left = new Thread(new servoUdpClient());//.start();
	        		left.start();
	        		Log.i("left","");
	        		joystickControl = 0;
				}
			}else if(tilt == 10){
				if(pan <= 10 && pan > -10 && joystickControl == 1){
					//directionText.setText("back");
					sendText = "asa ";
	        		Thread back = new Thread(new servoUdpClient());//.start();
	        		back.start();
	        		Log.i("back","");
	        		joystickControl = 0;
				}
			}else if(tilt == -10){
				if(pan <= 10 && pan > -10 && joystickControl == 1){
					//directionText.setText("straight");
					sendText = "yukarı ";
	        		Thread stop = new Thread(new servoUdpClient());//.start();
	        		stop.start();
	        		Log.i("straight","");
	        		joystickControl = 0;
				}
			}
		}

		@Override
		public void OnReleased() {
			if(joystickControl == 0){
				sendText = "stop";
				Thread stop = new Thread(new servoUdpClient());//.start();
				stop.start();
				//txtX1.setText("stopped");
				//txtY1.setText("stopped");
				Log.i("stopped","");
			}
			joystickControl = 1;
		}

	}; 
    
	private static final int UDP_SERVER_PORT = 8080;
    
	 public class UdpClient implements Runnable {

	        int i = 0;
	        public UdpClient(int i) {
	            this.i = i;
	        }

	        public void run() {
	        	Log.i("Responde", "icinde");
	        	Log.i("Response icinde",i+"");
	            //runUdpClient(i);
	        	final String udpMsg;

	        	udpMsg=sendText+" "+i;
	        	runOnUiThread(new Runnable() {// viewde değişiklik için ana thread kullanman lazım bu şekilde kullandık.
				     @Override
				     public void run() {
				    	 //directionText.setText(udpMsg);
				    }
				});
	        	//BtString.setText(udpMsg);
	        	DatagramSocket ds = null;
	        	try {
	    			ds = new DatagramSocket();
	    			InetAddress serverAddr = InetAddress.getByName("10.20.9.43"/*"10.104.52.73"/*ipNumber*/);
	    			DatagramPacket dp;
	    			dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), serverAddr, 8080/*Integer.parseInt(portNumber)*/);
	    			ds.send(dp);
	    		} catch (SocketException e) {
	    			e.printStackTrace();
	    		}catch (UnknownHostException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		} finally {
	    			if (ds != null) {
	    				ds.close();
	    			}
	    		}
	        }
	    }

	 public class servoUdpClient implements Runnable {

	        int i = 0;
	        

	        public void run() {
	        	Log.i("Responde", "icinde");
	        	Log.i("Response icinde",i+"");
	            //runUdpClient(i);
	        	final String udpMsg;

	        	udpMsg=sendText+" "+i;
	        	/*runOnUiThread(new Runnable() {// viewde değişiklik için ana thread kullanman lazım bu şekilde kullandık.
				     @Override
				     public void run() {
				    	 directionText.setText(udpMsg);
				    }
				});*/
	        	//BtString.setText(udpMsg);
	        	DatagramSocket ds = null;
	        	try {
	    			ds = new DatagramSocket();
	    			InetAddress serverAddr = InetAddress.getByName("10.20.9.43"/*"10.104.52.73"/*ipNumber*/);
	    			DatagramPacket dp;
	    			dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), serverAddr, 8081/*Integer.parseInt(portNumber)*/);
	    			ds.send(dp);
	    		} catch (SocketException e) {
	    			e.printStackTrace();
	    		}catch (UnknownHostException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		} finally {
	    			if (ds != null) {
	    				ds.close();
	    			}
	    		}
	        }
	    }
	    // resim gönderip alıyo alttaki 3 method
	   
	 public void startHandler(final int i)
			{
	    	Runnable r =new Runnable()
		    {

		        @Override
		        public void run()
		        {
		        	Log.i("handler ","içinde");
		            if(!isBusy) callAysncTask();

		            if(!stop) startHandler(i);
		        }
		    };
		    if(i == 0)
			    handler.postDelayed(r , 1500);
		    else{
			    handler.removeCallbacksAndMessages(null);
		    }
			}
	   
	 private void callAysncTask()
	    {
	        //TODO
	    	Log.i("Asyncask ","içinde");
	        new UdpImageClient().execute();
	    }
	 
	 private class UdpImageClient extends AsyncTask<String, Void, String> {

	        @SuppressLint("NewApi") @SuppressWarnings("resource")
			@Override
	        protected String doInBackground(String... params) {
	      
	        	Log.i("Read Button Clicked", "yipee");

				try {
					int filesize=900000; // filesize temporary hardcoded

					long start = System.currentTimeMillis();
					int bytesRead;
					int current = 0;
					
					// localhost for testing
					// TODO: server's IP address. Socket should match one above in server
					Log.i("************", "bağlanmaya çalışıyo...");

					Socket sock = new Socket("10.20.9.43",13267);///

					Log.i("************", "Connecting...");

					// receive file
					byte [] mybytearray  = new byte [filesize];
					InputStream is = sock.getInputStream();
					// TODO: Put where you want to save the file
					/* N.B.:
					 * * To view if the file transfer was successful:
					 *       * use `./adb shell` 
					 *       * use the app: File Manager
					 * 
					 * * If you downloaded to '/mnt/sdcard/download', 
					 *   your download might not show up in 'Downloads'
					 *   
					 * * You might not have '/mnt/sdcard/download' directory
					 *   if you have never downloaded anything on your iPhone
					 */
					FileOutputStream fos = new FileOutputStream("/storage/emulated/0/Download/2new.jpg");
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					bytesRead = is.read(mybytearray,0,mybytearray.length);
					current = bytesRead;
					do {
						bytesRead =
								is.read(mybytearray, current, (mybytearray.length-current));
						if(bytesRead >= 0) current += bytesRead;
					} while(bytesRead > -1);

					bos.write(mybytearray, 0 , current);
					bos.flush();
					long end = System.currentTimeMillis();
					Log.i("************ end-start = ", String.valueOf(end-start));
					bos.close();
					sock.close();
					
					
				} catch ( UnknownHostException e ) {
					Log.i("******* :( ", "UnknownHostException");
				} catch (IOException e){
					Log.i("Read has IOException", "e: " + e);
				}

				Log.i("=============== the end of read ===============", "==");

				
				runOnUiThread(new Runnable() {// viewde değişiklik için ana thread kullanman lazım bu şekilde kullandık.
				     @SuppressWarnings("deprecation")
					@Override
				     public void run() {
				    	 if(imgFile.exists()){
			 		        	Log.d("resim", "ass");
			 		            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			 		            ImageView myImage = (ImageView) findViewById(R.id.imageView1);
			 		            myImage.setImageBitmap(myBitmap);
			 		           
			 		            
			 		        }
				    }
				});
	            return "Executed";
	        }

	        @Override
	        protected void onPostExecute(String result) {
	        	
	        	
	        }

	        @Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	    }
	  
}
