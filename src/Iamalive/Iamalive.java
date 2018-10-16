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

    public static final byte MY_INSTRUCTION= 	(byte)0x46;

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

    // SMS constants
    private static final byte[] 	ALPHA_ID_MESSAGE =		{'H','e','l','l','o',' ',' ','W','o','r','l','d'};


/*
	private static final byte[]		TPDU_BASE_1 =	{	(byte)0x01,
														(byte)0xFF,
														(byte)0x0C,
														(byte)0x91,
														(byte)0x44,(byte)0x97,(byte)0x73,(byte)0x85,(byte)0x88,(byte)0x13,
														(byte)0x00,
														(byte)0x04};	
*/																							

	private static final byte[]		HARDCODED_TPDU	= {	(byte)0x01,
														(byte)0xFF,
														(byte)0x0C,
														(byte)0x91,
														(byte)0x44,(byte)0x97,(byte)0x73,(byte)0x85,(byte)0x88,(byte)0x13,
														(byte)0x00,
														(byte)0x00,
	(byte)0x51,(byte)0xC9,(byte)0xE1,(byte)0x30,(byte)0x49,(byte)0xD4,(byte)0x81,(byte)0x70,(byte)0x39,(byte)0x1A,(byte)0xAD,(byte)0x06,(byte)0x83,(byte)0xD5,(byte)0x62,
	(byte)0xB0,(byte)0xD8,(byte)0x0D,(byte)0x27,(byte)0x8B,(byte)0xC1,(byte)0x60,(byte)0xB1,(byte)0x19,(byte)0x28,(byte)0xD9,(byte)0x9C,(byte)0x26,(byte)0x75,(byte)0x20,
	(byte)0xD9,(byte)0x8C,(byte)0x56,(byte)0x83,(byte)0xC1,(byte)0x60,(byte)0x30,(byte)0x1A,(byte)0x4E,(byte)0x16,(byte)0x83,(byte)0xC1,(byte)0x62,(byte)0xA0,(byte)0xE6,
	(byte)0x70,(byte)0xD8,(byte)0x74,(byte)0x0E,(byte)0x75,(byte)0x20,(byte)0x99,(byte)0x4D,(byte)0x06,(byte)0x93,(byte)0x81,(byte)0x50,(byte)0xD6,(byte)0x37,(byte)0x39,
	(byte)0x6C,(byte)0x7E,(byte)0xBB,(byte)0xCB,(byte)0xA0,(byte)0x63,(byte)0x59,(byte)0xDE,(byte)0x0E,(byte)0xBB,(byte)0xF3,(byte)0x29};
/* 447937588831 The phone number in semi octets (449773858813). */

/*
	// Constants to manage nibbles
	public static final byte NIBBLE_SIZE  = (byte)4;
 	public static final byte UPPER_NIBBLE  = (byte)0xF0;
 	public static final byte LOWER_NIBBLE  = (byte)0x0F; 
*/


 	/**
 	*	Constructor of the applet
	*/
	private Iamalive() {
		reg 	= ToolkitRegistry.getEntry();		// register to the SIM Toolkit Framework

		// Define the applet Menu Entry and register to the EVENT_MENU_SELECTION
        itemId = reg.initMenuEntry(menuEntry, (short)0x0000, (short)menuEntry.length, 
                                   PRO_CMD_DISPLAY_TEXT, false, (byte) 0x00, (short) 0x0000);

        /* when the terminal enters the idle state with the result that either the Location status or Location
		information has been changed or updated, the terminal shall inform the UICC*/
		reg.setEvent(EVENT_EVENT_DOWNLOAD_LOCATION_STATUS);

/*
		// RAM allocated variables
		locationInfo 		= JCSystem.makeTransientByteArray ((short)3, JCSystem.CLEAR_ON_RESET);
		locationInfo_print 	= JCSystem.makeTransientByteArray ((short)6, JCSystem.CLEAR_ON_RESET);
		tpdu  				= JCSystem.makeTransientByteArray ((short)19, JCSystem.CLEAR_ON_RESET);
*/
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

/*
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


					
				    byteToASCII(locationInfo_print,locationInfo);

			        Util.arrayCopy(TPDU_BASE_1, (short)0, tpdu, (short)0,(short) TPDU_BASE_1.length);
			        tpdu[12]=(byte)(locationInfo_print.length & LOWER_NIBBLE);
			        //Util.arrayCopy(locationInfo_length,(short)0,tpdu,(short)13,(short)locationInfo_length.length);
			        Util.arrayCopy(locationInfo_print,(short)0,tpdu,(short)13,(short)locationInfo_print.length);
			        

            		//packing not required
            		proHdlr.init(PRO_CMD_SEND_SHORT_MESSAGE, (byte) 0, DEV_ID_NETWORK);
            	  	proHdlr.appendTLV(TAG_ALPHA_IDENTIFIER, ALPHA_ID_MESSAGE, (short)0,(short)ALPHA_ID_MESSAGE.length);	
					proHdlr.appendTLV((byte)TAG_SMS_TPDU, tpdu, (short)0,(short)tpdu.length);
					proHdlr.send(); 

*/

             		proHdlr.init(PRO_CMD_SEND_SHORT_MESSAGE, (byte) 0, DEV_ID_NETWORK);
            	  	proHdlr.appendTLV(TAG_ALPHA_IDENTIFIER, ALPHA_ID_MESSAGE, (short)0,(short)ALPHA_ID_MESSAGE.length);	
					proHdlr.appendTLV((byte)TAG_SMS_TPDU, HARDCODED_TPDU, (short)0,(short)HARDCODED_TPDU.length);
					proHdlr.send();


                break;
            }
        }        
	}	

/*
	private void byteToASCII(byte[] buffer, byte[] input) {

		for (short i=(short)0x00 ; (i < (short)input.length) || (i < (short)((short)buffer.length/2)); i++) {
			buffer[(short)(2*i)]= (byte)((input[i] & LOWER_NIBBLE)+0x30);	
			buffer[(short)(2*i+1)] = (byte)((((input[i] & UPPER_NIBBLE)>> NIBBLE_SIZE) & LOWER_NIBBLE)+0x30);

		}
		

	}
*/

}
