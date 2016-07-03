package br.com.rezende.camel.filter;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * 
 * @author Rezende
 *
 */
public class RotaPedidosLerWS {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {

				from("file:pedidos?delay=5s&noop=true").
					split().
						xpath("/pedido/itens/item").
					filter().
						xpath("/item/formato[text()='PDF']").
					marshal().xmljson().
					log("${id} - ${body}").
					setHeader(Exchange.FILE_NAME, simple("${file:name.noext}-${header.CamelSplitIndex}.json")).
				to("http4://localhost:8080/webservices/ebook/item");
			}			
		});

		context.start();
		Thread.sleep(2000);
		context.stop();
	}	
}
