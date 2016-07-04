package br.com.rezende.camel.filter;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * 
 * @author Rezende
 *
 */
public class VelocityVM {
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("direct:entrada").setHeader("data", constant("8/12/2015")).to("velocity:template.vm")
						.log("${body}");

				ProducerTemplate producer = context.createProducerTemplate();
				producer.sendBody("direct:entrada", "Apache Camel rocks!!!");
			}

		});

		context.start();
		Thread.sleep(20000);
		context.stop();
	}
}
