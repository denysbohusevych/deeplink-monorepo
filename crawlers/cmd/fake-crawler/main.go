package main

import (
	"fmt"
	"log"
	"os"
	"time"

	"github.com/confluentinc/confluent-kafka-go/kafka"
	"google.golang.org/protobuf/proto"

	// Импортируем сгенерированный код
	commonv1 "github.com/yourusername/deeplink/crawlers/gen/go/deeplink/common/v1"
	domainv1 "github.com/yourusername/deeplink/crawlers/gen/go/deeplink/common/v1"
)

func main() {
	fmt.Println("🕷️ Starting Fake Go Crawler...")

	// Читаем адрес брокера из ENV, по умолчанию localhost (для локального запуска)
	broker := os.Getenv("BOOTSTRAP_SERVERS")
	if broker == "" {
		broker = "localhost:19092"
	}
	fmt.Printf("🔌 Connecting to Kafka at: %s\n", broker)

	// 1. Подключение к Redpanda
	p, err := kafka.NewProducer(&kafka.ConfigMap{
		"bootstrap.servers": broker,
		"client.id":         "go-crawler-01",
		"acks":              "all",
	})
	if err != nil {
		log.Fatalf("Failed to create producer: %s\n", err)
	}
	defer p.Close()

	topic := "ingestion.raw.fake"

	// 2. Бесконечный цикл генерации данных
	for i := 0; i < 5; i++ {
		// Создаем фейкового человека (Protobuf)
		person := &domainv1.Person{
			Id:          fmt.Sprintf("go-generated-%d", i),
			DisplayName: fmt.Sprintf("John Doe #%d", i),
			Notes:       "Discovered by Go Crawler via Kafka",
			Gender:      domainv1.Gender_GENDER_MALE,
			BirthDate: &commonv1.FuzzyDate{
				Year: 1990 + int32(i),
			},
		}

		// Сериализация в байты
		out, err := proto.Marshal(person)
		if err != nil {
			log.Fatalln("Failed to encode address book:", err)
		}

		// Отправка в Kafka
		err = p.Produce(&kafka.Message{
			TopicPartition: kafka.TopicPartition{Topic: &topic, Partition: kafka.PartitionAny},
			Value:          out,
		}, nil)

		if err != nil {
			log.Printf("❌ Failed to produce: %v\n", err)
		} else {
			fmt.Printf("✅ Sent person: %s\n", person.DisplayName)
		}

		// Ждем подтверждения доставки
		p.Flush(1000)
		time.Sleep(2 * time.Second)
	}
}
