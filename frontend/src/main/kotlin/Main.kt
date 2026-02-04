import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.deeplink.graph.v1.GraphServiceGrpcKt
import com.deeplink.graph.v1.GetPersonRequest
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "DeepLink OSINT Client") {
        MaterialTheme {
            App()
        }
    }
}

@Composable
fun App() {
    // Состояние UI
    var statusText by remember { mutableStateOf("Ready to connect...") }
    var personData by remember { mutableStateOf("") }

    // Создаем gRPC канал (соединение с бэкендом)
    val channel = remember {
        ManagedChannelBuilder.forAddress("localhost", 9090)
            .usePlaintext()
            .build()
    }

    // Создаем клиент (Stub)
    val stub = remember { GraphServiceGrpcKt.GraphServiceCoroutineStub(channel) }

    // Эффект, который сработает при нажатии кнопки (запуск корутины)
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("DeepLink Analyst Console", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            // Запускаем запрос
            statusText = "Loading..."
            // В реальном приложении это делается во ViewModel
            // Здесь для простоты используем LaunchedEffect или scope
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) { // Внимание: GlobalScope только для демо!
                try {
                    // Пробуем найти человека, которого создал Fake Crawler (ID может отличаться, пробуем наугад или используем тот, что в логах)
                    // Для теста поищем того, кого создавал ManualClientCheck (у него мы ID не фиксировали),
                    // или просто попробуем дернуть несуществующий ID, чтобы проверить связь.

                    // ВАЖНО: Чтобы это сработало, нужно знать реальный UUID из базы.
                    // Для теста просто проверим, что сервер ответит (даже ошибкой NOT_FOUND).
                    val response = stub.getPerson(
                        GetPersonRequest.newBuilder().setId("a849786d-ad00-4c5c-b898-071e3f6751ac").build()
                    )
                    withContext(Dispatchers.Main) {
                        statusText = "Success!"
                        personData = response.person.displayName
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        statusText = "Error: ${e.message}"
                        // Если ошибка "Person not found", значит соединение есть!
                    }
                }
            }
        }) {
            Text("Test Connection to Backend")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Status: $statusText")

        if (personData.isNotEmpty()) {
            Card(modifier = Modifier.padding(10.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Found Person:", style = MaterialTheme.typography.titleMedium)
                    Text(personData, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

// Хак для импорта launch без сложной настройки (для демо)
