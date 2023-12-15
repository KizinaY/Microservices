package com.kizina.resourceservice.producer;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQProducer {

    @Value("${queue.name.audio}")
    private String AUDIO_QUEUE_NAME;

    private final RabbitTemplate rabbitTemplate;

    private final Tracer tracer;

    @Autowired
    public RabbitMQProducer(RabbitTemplate rabbitTemplate, Tracer tracer) {
        this.rabbitTemplate = rabbitTemplate;
        this.tracer = tracer;
    }

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = Integer.MAX_VALUE,
            backoff = @Backoff(delay = 1000, maxDelay = 10000)
    )
    public void sendMessage(String message) {
        String messageWithTraceId = addTraceIdToMessage(message);
        rabbitTemplate.convertAndSend(AUDIO_QUEUE_NAME, messageWithTraceId);
    }

    private String addTraceIdToMessage(String message) {
        log.info("Start addition traceId to message. Tracer: " + tracer);
        Span currentSpan = tracer.currentSpan();
        log.info("Current span: " + currentSpan);
        String traceId = "";
        if (currentSpan != null) {
            traceId = currentSpan.context().traceId();
        }
        log.info("Stop addition traceId to message. TraceId: " + traceId);
        return message + ";" + traceId;
    }
}

