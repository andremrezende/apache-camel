package br.com.rezende.camel.filter;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * 
 * @author Rezende
 *
 */
public class ReceivePedidos {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {

				from("file:pedidos?noop=true&delay=5s").log("${id}").split().xpath("/pedido/itens/item").filter()
						.xpath("/item/formato[text()='PDF']").marshal().xmljson().log("${body}")
						.setHeader("CamelFileName", simple("${file:name.noext}.json")).to("file:saida");
			}

		});

		context.start();
		Thread.sleep(20000);
		context.stop();
	}
}
