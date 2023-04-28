package org.example;

import com.sun.net.httpserver.HttpServer;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.example.Advice.DemoAdvice;

import java.io.StringWriter;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.util.Collections;

import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.named;


public class Main {
    public static void premain(String args, Instrumentation instrumentation) throws Exception{
        System.out.println("inside Premain");
        new AgentBuilder.Default()
                .type(hasSuperType(named("org.springframework.web.servlet.DispatcherServlet")))
                .transform(new AgentBuilder.Transformer.ForAdvice()
                        .include(Main.class.getClassLoader())
                        .advice(named("doService"), DemoAdvice.class.getName()))
                .installOn(instrumentation);
        runHttpServer();
    }
  
    static void runHttpServer() throws Exception {
        InetSocketAddress address = new InetSocketAddress(9300);
        HttpServer httpServer = HttpServer.create(address, 10);
        httpServer.createContext("/metrics", httpExchange -> {
            StringWriter respBodyWriter = new StringWriter();
            TextFormat.write004(respBodyWriter, CollectorRegistry.defaultRegistry.metricFamilySamples());
            byte[] respBody = respBodyWriter.toString().getBytes("UTF-8");
            httpExchange.getResponseHeaders().put("Context-Type", Collections.singletonList("text/plain; charset=UTF-8"));
            httpExchange.sendResponseHeaders(200, respBody.length);
            httpExchange.getResponseBody().write(respBody);
            httpExchange.getResponseBody().close();
        });

        httpServer.start();
    }
}
