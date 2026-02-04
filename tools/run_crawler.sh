#!/bin/bash

# Определяем правильный хост для Kafka в зависимости от ОС
if [[ "$OSTYPE" == "darwin"* ]]; then
  # Для Mac (Docker Desktop)
  KAFKA_ADDR="host.docker.internal:19092"
else
  # Для Linux (используем host network или IP шлюза)
  KAFKA_ADDR="localhost:19092"
fi

echo "🚀 Запуск Go Crawler через Docker..."
echo "📍 Kafka Address: $KAFKA_ADDR"

# Запускаем контейнер Golang
# Ипользуем версию 1.24, так как новые библиотеки (grpc) требуют свежий тулчейн
# -v: монтируем папку crawlers внутрь контейнера
# --network host: (для Linux) или env var для Mac
docker run --rm -it \
  -v "$(pwd)/crawlers:/app" \
  -w /app \
  -e BOOTSTRAP_SERVERS=$KAFKA_ADDR \
  golang:1.24 \
  sh -c "
    echo '📦 Initializing Go module...'
    # Важно: имя модуля должно включать 'crawlers', чтобы совпадать с импортами в main.go
    [ ! -f go.mod ] && go mod init github.com/denysbohusevych/deeplink-monorepo/crawlers

    go get github.com/confluentinc/confluent-kafka-go/kafka

    go get google.golang.org/protobuf/proto

    echo '⬇️ Downloading dependencies...'
    # Принудительно обновляем зависимости, чтобы подтянуть совместимые версии
    go mod tidy

    echo '▶️ Running Crawler...'
    go run cmd/fake-crawler/main.go
  "