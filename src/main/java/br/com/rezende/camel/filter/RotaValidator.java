package br.com.rezende.camel.filter;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * 
 * @author Rezende
 *
 */
public class RotaValidator {
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				onException(Exception.class).
			    handled(true).
			        maximumRedeliveries(3).
			            redeliveryDelay(4000).
			        onRedelivery(new Processor() {
			            @Override
			            public void process(Exchange exchange) throws Exception {
			                    int counter = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER);
			                    int max = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_MAX_COUNTER);
			                    System.out.println("Redelivery - " + counter + "/" + max );;
			            }
			    }).to("file:error-parsing");
//				errorHandler(deadLetterChannel("file:erro").logExhausted(true).maximumRedeliveries(3).redeliveryDelay(2000).onRedelivery(new Processor() {
//					
//					@Override
//					public void process(Exchange exchange) throws Exception {
//						int counter = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER);
//						int max = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_MAX_COUNTER);
//						System.out.println("Redelivery " + counter + " / " + max);
//						
//					}
//				}));
				
				from("file:pedidos?delay=5s&noop=true").to("validator:pedido.xsd").
//				multicast().parallelProcessing().timeout(500). // millis
//				to("direct:http").to("direct:soap");

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
