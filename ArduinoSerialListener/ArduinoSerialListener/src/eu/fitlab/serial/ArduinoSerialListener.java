package eu.fitlab.serial;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class ArduinoSerialListener implements SerialPortEventListener{
	private HashMap<String, CommPortIdentifier> portMap;
	private SerialPort serialPort;
	private InputStream is;
	private OutputStream os;
	private Robot robot;
	private static final Logger logger = Logger.getLogger(ArduinoSerialListener.class);
	private HashMap<String, Integer> keyMap;
	private static final int NEW_LINE = 10;	
	private int debounceDelay = 100;
	private long lastReadTime = 0;
	private String lastKeyPressed = "";
	
	public ArduinoSerialListener() {
		portMap = getAvailablePorts();
		initialiseKeyMap();
	}
	
	public Map<String, CommPortIdentifier> getPortMap(){
		return portMap;
	}
	
	private void initialiseKeyMap() {
		keyMap = new HashMap<String, Integer>();
		//6 key
		keyMap.put("start", KeyEvent.VK_ENTER);
		keyMap.put("option", KeyEvent.VK_O);
		keyMap.put("pause", KeyEvent.VK_ESCAPE);
		keyMap.put("power", KeyEvent.VK_P);
		keyMap.put("bolus", KeyEvent.VK_B);
		keyMap.put("undo", KeyEvent.VK_U);
		
		//4X4
		keyMap.put("1", KeyEvent.VK_1);
		keyMap.put("2", KeyEvent.VK_2);
		keyMap.put("3", KeyEvent.VK_3);
		keyMap.put("4", KeyEvent.VK_4);
		keyMap.put("5", KeyEvent.VK_5);
		keyMap.put("6", KeyEvent.VK_6);
		keyMap.put("7", KeyEvent.VK_7);
		keyMap.put("8", KeyEvent.VK_8);
		keyMap.put("9", KeyEvent.VK_9);
		keyMap.put("0", KeyEvent.VK_0);
		keyMap.put(".", KeyEvent.VK_PERIOD);
		keyMap.put("c", KeyEvent.VK_C);
		
		keyMap.put("w", KeyEvent.VK_W);
		keyMap.put("x", KeyEvent.VK_X);
		keyMap.put("y", KeyEvent.VK_Y);
		keyMap.put("z", KeyEvent.VK_Z);
		
		//knob
		keyMap.put("U", KeyEvent.VK_UP);
		keyMap.put("D", KeyEvent.VK_DOWN);
		keyMap.put("knob", KeyEvent.VK_K);
	}
	
	private void initialiseRobot() {
		try {
			robot = new Robot();
		}catch(AWTException ex) {
			logger.error("Could not create Java Robot -- program will now exit", ex);
			System.exit(-1);
		}
	}
	
	private HashMap<String, CommPortIdentifier> getAvailablePorts(){
		HashMap<String, CommPortIdentifier> result = new HashMap<String, CommPortIdentifier>();
		Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		while(ports.hasMoreElements()) {
			CommPortIdentifier port = ports.nextElement();
			if(port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				result.put(port.getName(), port);
				logger.info(port.getName());
				logger.info(port.getCurrentOwner());
			}
		}
		return result;
	}
	
	public void connect(String port) throws Exception{
		initialiseRobot();

		CommPortIdentifier cpi = (CommPortIdentifier)portMap.get(port);
		CommPort cp = cpi.open(this.getClass().getName(), 2000);
		serialPort = (SerialPort)cp;
		//init is and os
		is = serialPort.getInputStream();
		os = serialPort.getOutputStream();
		
		//add listeners
		
		serialPort.addEventListener(this);
		serialPort.notifyOnDataAvailable(true);
		
	}
	
	public void disconnect()throws Exception{
		if(is !=null)
			is.close();
		if(os != null)
			os.close();
		if(serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			List<Byte> readBuffer = new ArrayList<Byte>();
			byte b;
			String keyPress, key, action;
			int keyCode;
			try {
				while ((b = (byte)is.read()) != NEW_LINE) {
					readBuffer.add(b);
				}
				
				keyPress = new String(getBytes(readBuffer)).trim();
				
				logger.info(keyPress);
				//if it is a knob turn action we will only get U or D events from the arduino serial communication
				if(keyPress.trim().length() == 1) {
					key = keyPress.trim();
					//do a press and release event
					keyCode = keyMap.get(key);
					robot.keyPress(keyCode);
					robot.keyRelease(keyCode);
					
				}else {
					key = keyPress.split(" ")[0];
					action = keyPress.split(" ")[1];

					keyCode = keyMap.get(key.toLowerCase());
					if (lastKeyPressed.equalsIgnoreCase(key)) {
						// if last key press was the same as now and its a key
						// release event, we want to hear it
						if (action.equalsIgnoreCase("released")) {
						 	robot.keyRelease(keyCode);
							lastKeyPressed = key;
						}
						else if (System.currentTimeMillis() - lastReadTime > debounceDelay) {
							// if the last key pressed is the same as the
							// current one and the debounce delay has elapsed
							// we want to hear it
							if (action.equalsIgnoreCase("pressed")) {
								robot.keyPress(keyCode);
							} else if (action.equalsIgnoreCase("released")) {
								robot.keyRelease(keyCode);
							}
							lastKeyPressed = key;
						} 
					}else {
						// if this key is not the same as the last one
						// pressed, then we want to hear it :P
						if (action.equalsIgnoreCase("pressed")) {
							robot.keyPress(keyCode);
						} else if (action.equalsIgnoreCase("released")) {
							robot.keyRelease(keyCode);
						}
						lastKeyPressed = key;
					}
				}
				lastReadTime = System.currentTimeMillis();
			} catch(Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		
	}
	
	public byte[] getBytes(List<Byte> bytes) {
		byte[] res = new byte[bytes.size()];
		for(int i=0; i< bytes.size(); i++) {
			res[i] = bytes.get(i);
		}
		return res;	
	}
	
	public static void main(String[] args) throws Exception{
		final ArduinoSerialListener a = new ArduinoSerialListener();
		JFrame f = new JFrame();
		String p = (String)JOptionPane.showInputDialog(f, "", "", 
				JOptionPane.OK_CANCEL_OPTION,null, a.getPortMap().keySet().toArray(), null);
		
		try{
			if(p != null) {
				a.connect(p);
			}else {
				//cancelled
				a.disconnect();
			}
		}
		finally {
			f.dispose();
		}		
	}
}
