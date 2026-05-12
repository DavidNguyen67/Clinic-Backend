package com.camel.clinic.util;

public final class RabbitMQConstants {
    public static final int DEFAULT_TTL_MS = 86_400_000;

    private RabbitMQConstants() {
    }

    public static final class Exchange {
        public static final String NOTIFICATION = "clinic.notification.exchange";
        public static final String TASK = "clinic.task.exchange";
        public static final String PUBSUB = "clinic.pubsub.exchange";
        public static final String DEAD_LETTER = "clinic.dlx.exchange";
    }

    public static final class Queue {
        public static final String EMAIL = "clinic.email.queue";
        public static final String PUSH_NOTIFICATION = "clinic.push.queue";
        public static final String TASK_BACKGROUND = "clinic.task.queue";
        public static final String PUBSUB_BROADCAST = "clinic.pubsub.queue";

        public static final String EMAIL_DLQ = "clinic.email.dlq";
        public static final String PUSH_DLQ = "clinic.push.dlq";
        public static final String TASK_DLQ = "clinic.task.dlq";
    }

    public static final class RoutingKey {
        public static final String EMAIL = "clinic.email";
        public static final String PUSH_NOTIFICATION = "clinic.push";
        public static final String TASK_BACKGROUND = "clinic.task";
        public static final String PUBSUB_BROADCAST = "clinic.pubsub";

        public static final String EMAIL_DEAD = "clinic.email.dead";
        public static final String PUSH_DEAD = "clinic.push.dead";
        public static final String TASK_DEAD = "clinic.task.dead";
    }
}