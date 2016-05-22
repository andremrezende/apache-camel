package br.com.rezende.camel.filter;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * Class to receive book orders and filter PDF books, transforming XML files to
 * JSON on output folder saida. The whole example uses Enterprise Integration Patterns.
 * 
 * @author Andre Rezende
 *
 */
public class ReceivePedidosFilter {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("file:pedidos?noop=true&delay=5s").log("${id}").split().xpath("/pedido/itens/item").filter()
						.xpath("/item/formato[text()='PDF']").marshal().xmljson().log("${body}")
						.setHeader(Exchange.FILE_NAME, simple("${file:name.noext}-${header.CamelSplitIndex}.json")).to("file:saida");
			}

		});

		context.start();
		Thread.sleep(2000);
		context.stop();
	}
}
