FROM apache/kafka:4.0.0

# expose
EXPOSE 9092 9093

COPY server.properties /opt/kafka/config/kraft/server.properties

# 7) Форматируем хранилище перед запуском и стартуем Kafka
ENTRYPOINT [ "sh", "-c", "\
    # форматируем storage (если уже отформатировано, при --ignore-formatted ошибок не будет) && \
    /opt/kafka/bin/kafka-storage.sh format \
      -t $KAFKA_CLUSTER_ID \
      -c /opt/kafka/config/kraft/server.properties \
      --ignore-formatted && \
    # запускаем сам сервер в KRaft-режиме \
    exec /opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/kraft/server.properties \
" ]
