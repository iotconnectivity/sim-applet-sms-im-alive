/**
 *  I am alive applet, it sends an sms with information.
 *
 * 		Author: Diego Leñero Ramírez 
 *				diego.lenero@podgroup.com
 *		 
 */

package Iamalive;

import sim.toolkit.*;
import javacard.framework.*;

public class Iamalive extends Applet implements ToolkitInterface, ToolkitConstants {


    private ToolkitRegistry reg;
    private byte result;

    // Menu constants
    private static final byte[] menuEntry=     	{'I',' ','a','m',' ','a','l','i','v','e'};
    private byte[] item1 =        				{'S','e','n','d',' ','S','M','S'};
    private Object[] ItemList =   				{item1};
    private byte itemId;


	// locationInfo
	short len = (short)00;		
	private byte[] locationInfo;
	private byte[] locationInfo_print;

	// TPDU buffer
	private byte[] tpdu;

	// Message buffer

	private byte[] userData;
	private byte[] userData_length;

    // SMS constants
    private static final byte[] ALPHA_ID_MESSAGE =	{'S','e','n','d','i','n','g',' ','S','M','S','.','.','.'}; // optional
    private static final byte[] mcc = 				{'M','C','C',':',' '};
    private static final byte[] mnc = 				{' ','M','N','C',':',' '};


	private static final byte[]		TPDU_BASE_1 =	{	(byte)0x01,
														(byte)0xFF,
														(byte)0x0C, //length of address
														(byte)0x91,
														(byte)0x44,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98, //440123456789 The phone number in semi octets (441032547698).
														(byte)0x00,
														(byte)0x00};	
	

	// Constants to manage nibbles
	public static final byte NIBBLE_SIZE  = (byte)4;
 	public static final byte UPPER_NIBBLE  = (byte)0xF0;
 	public static final byte LOWER_NIBBLE  = (byte)0x0F; 



 	/**
 	*	Constructor of the applet
	*/
	private Iamalive() {
		reg 	= ToolkitRegistry.getEntry();		// register to the SIM Toolkit Framework

		// Define the applet Menu Entry and register to the EVENT_MENU_SELECTION
        itemId = reg.initMenuEntry(menuEntry, (short)0x0000, (short)menuEntry.length, 
                                   PRO_CMD_DISPLAY_TEXT, false, (byte) 0x00, (short) 0x0000);

		//TO DO: send sms automatically when there is an update in the location status

        /* when the terminal enters the idle state with the result that either the Location status or Location
		information has been changed or updated, the terminal shall inform the UICC*/
		//reg.setEvent(EVENT_EVENT_DOWNLOAD_LOCATION_STATUS);


		// RAM allocated variables
		locationInfo 		= JCSystem.makeTransientByteArray ((short)3, JCSystem.CLEAR_ON_RESET);
		locationInfo_print 	= JCSystem.makeTransientByteArray ((short)6, JCSystem.CLEAR_ON_RESET);
		tpdu  				= JCSystem.makeTransientByteArray ((short)28, JCSystem.CLEAR_ON_RESET);
		userData			= JCSystem.makeTransientByteArray ((short)16, JCSystem.CLEAR_ON_RESET);
		userData_length		= JCSystem.makeTransientByteArray ((short)1, JCSystem.CLEAR_ON_RESET);
	}


    /**
    * Method called by the JCRE at the installation of the applet
    */
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		Iamalive applet = new Iamalive();
		applet.register();
	}



    /**
    * Method called by the JCRE, once selected
    */
    public void process(APDU apdu) {
        // Handle the Select AID apdu
        if(selectingApplet())
            return;
    }


    /**
    * Method called by the SIM Toolkit Framework
    */
	public void processToolkit(byte event) throws ToolkitException {

		// get the handler references
        ProactiveHandler         proHdlr = ProactiveHandler.getTheHandler();
        ProactiveResponseHandler rspHdlr ;

		// Prepare the Select Item proactive command
        proHdlr.init(PRO_CMD_SELECT_ITEM,(byte)0,DEV_ID_ME);

        // add all items
        for (short i=(short) 0x0000; i<(short) ItemList.length; i++) {
            proHdlr.appendTLV((byte) (TAG_ITEM | TAG_SET_CR),(byte) (i+1),
                              (byte[])ItemList[i],(short) 0,
                              (short)((byte[])ItemList[i]).length);
        }
        // ask the SIM Toolkit Framework to send the proactive command and check the result
        if((result = proHdlr.send()) == RES_CMD_PERF){
            rspHdlr = ProactiveResponseHandler.getTheHandler();

            // SelectItem response handling
            switch (rspHdlr.getItemIdentifier()) {
                case 1:

                	// get locationInfo

					// Command Qualifier 00 = Location Information
					proHdlr.init(PRO_CMD_PROVIDE_LOCAL_INFORMATION, (byte)0x00, DEV_ID_ME);
					proHdlr.send();

					rspHdlr = ProactiveResponseHandler.getTheHandler();
					 
					// look for location information element
					if (rspHdlr.findTLV((byte)TAG_LOCATION_INFORMATION, (byte)0x01) != TLV_NOT_FOUND) {
						if ((len = rspHdlr.getValueLength()) > 1) {
						    // not empty string: to be copied
						    rspHdlr.copyValue((short)0, locationInfo, (short)0, (short)locationInfo.length);
			   
						}
					} 


					// change encoding to a readable one
				    byteToASCII(locationInfo_print,locationInfo);

					// build the message (use tpdu as buffer to safe resources)
					Util.arrayCopy(mcc, (short)0, tpdu, (short)0, (short)mcc.length);
					Util.arrayCopy(locationInfo_print,(short)0,tpdu,(short)mcc.length,(short)3);
					Util.arrayCopy(mnc, (short)0, tpdu, (short)(mcc.length+3), (short)mnc.length);
					Util.arrayCopy(locationInfo_print,(short)4,tpdu,(short)(mcc.length+3+mnc.length),(short)2);

					// change encoding to the gsm 7-bit default alphabet and pack the user data
					ASCII_to_gsmA(tpdu,(short)userData.length,userData, userData_length);

					// build the tpdu
			        Util.arrayCopy(TPDU_BASE_1, (short)0, tpdu, (short)0,(short) TPDU_BASE_1.length);
					//tpdu[12]=(byte)(userData_length[0]+1); //Length of user data
					tpdu[12]=(byte)userData.length;
			        Util.arrayCopy(userData,(short)0,tpdu,(short)(TPDU_BASE_1.length+1),(short)userData_length[0]); // user data



            		// Send SMS, packing not required
            		proHdlr.init(PRO_CMD_SEND_SHORT_MESSAGE, (byte) 0, DEV_ID_NETWORK);
					// Display ALPHA_ID_MESSAGE on screen, optional
            	  	proHdlr.appendTLV(TAG_ALPHA_IDENTIFIER, ALPHA_ID_MESSAGE, (short)0,(short)ALPHA_ID_MESSAGE.length);
					// SMS TPDU	
					proHdlr.appendTLV((byte)TAG_SMS_TPDU, tpdu, (short)0,(short)tpdu.length);
					proHdlr.send(); 

                break;
            }
        }        
	}	

	// From nibbled hexadecimal to ASCII characters
	private void byteToASCII(byte[] buffer, byte[] input) {

		for (short i=(short)0x00 ; (i < (short)input.length) || (i < (short)((short)buffer.length/2)); i++) {
			buffer[(short)(2*i)]= (byte)((input[i] & LOWER_NIBBLE)+0x30);	
			buffer[(short)(2*i+1)] = (byte)((((input[i] & UPPER_NIBBLE)>> NIBBLE_SIZE) & LOWER_NIBBLE)+0x30);

		}
		

	}

	// pack the sms user data with gsm 7-bit default alphabet			
	private void ASCII_to_gsmA(byte[] input, short input_length, byte[] output){
		short input_counter 	= 0;
		short output_counter 	= 0;
		short bit_count 		= 0;
		short bit_queue 		= 0; 
		while(input_counter<input_length){
			bit_queue |= (input[input_counter] & 0x7F) << bit_count;
			bit_count += 7;
			if (bit_count >= 8){
				output[output_counter]=(byte)(bit_queue & 0xFF);
				output_counter++;
				bit_count -= 8;
				bit_queue >>=8;
			}
			input_counter++;
		}
		if (bit_count > 0){
			output[output_counter]=(byte)(bit_queue & 0xFF);
			output_counter++;
		}
	}
	
}