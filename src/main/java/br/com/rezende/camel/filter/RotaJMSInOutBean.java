package br.com.rezende.camel.filter;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * 
 * @author Rezende
 *
 */
public class RotaJMSInOutBean {
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));

		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("activemq:queue:pedidos.req").bean(TratadorMensagemJMS.class).log("Pattern: ${exchange.pattern}")
						.setBody(constant("novo conteudo da mensagem"))
						.setHeader(Exchange.FILE_NAME, constant("teste.txt")).to("file:saida");
			}
		});

		context.start();
		Thread.sleep(20000);
		context.stop();
	}
}
