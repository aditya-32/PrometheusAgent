package org.example.Advice;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import net.bytebuddy.asm.Advice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DemoAdvice {

    public static final Counter counter = Counter
                                        .build("http_request_total","Total number of request for labeled path")
                                        .labelNames("path")
                                        .register();

    public static final Counter failureCounter = Counter
            .build("http_request_failure_total","Total number of request failed")
            .labelNames("path", "status")
            .register();
    public static final Histogram histogram= Histogram.build("htt_request_time","Total time taken for requests")
                                                .labelNames("path")
                                                .buckets(.05, .075, .1, .25, .5, .75, 1, 2, 2.5, 5)
                                                .register();

    @Advice.OnMethodEnter
    public static Histogram.Timer enter(@Advice.Argument(0)HttpServletRequest request) {
        System.out.println("Path "+request.getServletPath());
        counter.labels(request.getServletPath()).inc();
        Histogram.Timer timer = histogram
                                .labels(request.getServletPath())
                                .startTimer();
        System.out.println("before serving the request...");
        return timer;
    }

    @Advice.OnMethodExit
    public static void exit(@Advice.Enter Histogram.Timer timer, @Advice.Argument(0)HttpServletRequest request ,@Advice.Argument(1)HttpServletResponse response) {
        System.out.println("Response status "+response.getStatus());
        timer.observeDuration();
        if(response.getStatus()!=200){
            failureCounter.labels(request.getServletPath(), String.valueOf(response.getStatus())).inc();
        }
        System.out.println("after serving the request...");
    }
}
