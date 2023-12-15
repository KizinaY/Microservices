package com.kizina.resourceservice.listener;

import com.kizina.resourceservice.service.EventProcessorService;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class RabbitMQListener {

    private final EventProcessorService eventProcessorService;

    private final Tracer tracer;

    @RabbitListener(queues = "${PROCESSING_QUEUE_NAME}")
    public void processingResourceId(String message) {
        String[] messageParts = extractPartsFromMessage(message);
        String traceId = extractTraceId(messageParts);
        String resourceId = extractResourceId(messageParts);
        continueTrace(traceId, resourceId);
    }

    private void continueTrace(String traceId, String resourceId) {
        log.info("Start continue tracing. Tracer: " + tracer);
        TraceContext traceContext = tracer.traceContextBuilder()
                .traceId(traceId)
                .spanId(String.valueOf(RandomUtils.nextLong()))
                .sampled(true)
                .build();
        log.info("Trace context: " + traceContext);
        CurrentTraceContext currentTraceContext = tracer.currentTraceContext();
        try (CurrentTraceContext.Scope newScope = currentTraceContext.newScope(traceContext)) {
            Span newSpan = tracer.nextSpan().name("newSpanName");
            log.info("New span: " + newSpan);
            try (Tracer.SpanInScope ws = tracer.withSpan(newSpan.start())) {
                log.info("Span in scope: " + ws);
                processResourceId(resourceId);
            } finally {
                log.info("Stop continue tracing");
                newSpan.end();
            }
        }
    }

    private void processResourceId(String resourceId) {
        Long processedResourceId = Long.valueOf(resourceId);
        eventProcessorService.processResourceEvent(processedResourceId);
    }

    private String[] extractPartsFromMessage(String message) {
        return message.split(";");
    }

    private String extractResourceId(String[] messageParts) {
        return messageParts[0];
    }

    private String extractTraceId(String[] messageParts) {
        return messageParts[1];
    }
}

