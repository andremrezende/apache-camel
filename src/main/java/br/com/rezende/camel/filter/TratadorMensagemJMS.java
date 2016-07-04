package br.com.rezende.camel.filter;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 
 * @author Rezende
 *
 */
public class TratadorMensagemJMS implements MessageListener { // import
																// javax.jmx
	public void onMessage(Message jmsMessage) { // import javax.jmx
		System.out.println("Andre==> s" + jmsMessage);
	}
}
