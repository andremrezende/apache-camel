package br.com.rezende.camel.filter;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * Class to receive book orders and filter PDF books, transforming XML files to
 * JSON on output folder saida. The whole example uses Enterprise Integration
 * Patterns.
 * 
 * @author Andre Rezende
 *
 */
public class RotaSubRotas {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("file:pedidos?delay=5s&noop=true").multicast().parallelProcessing().timeout(500). // millis
						to("direct:http").to("direct:soap");

				from("direct:http").routeId("rota-pedidos").setProperty("pedidoId", xpath("/pedido/id/text()"))
						.setProperty("clienteId", xpath("/pedido/pagamento/email-titular/text()")).split()
						.xpath("/pedido/itens/item").filter().xpath("/item/formato[text()='PDF']")
						.setProperty("ebookId", xpath("/item/livro/codigo/text()")).marshal().xmljson().
						// log("${id} - ${body}").
						setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
						.setHeader(Exchange.HTTP_QUERY,
								simple("ebookId=${property.ebookId}&pedidoId=${property.pedidoId}&clienteId=${property.clienteId}"))
						.to("http4://localhost:8080/webservices/ebook/item");

				from("direct:soap").routeId("rota-soap").to("xslt:pedido-para-soap.xslt")
						.log("Resultado do Template: ${body}").setHeader(Exchange.CONTENT_TYPE, constant("text/xml"))
						.to("http4://localhost:8080/webservices/financeiro");
			}

		});

		context.start();
		Thread.sleep(20000);
		context.stop();
	}
}
