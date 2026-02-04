rootProject.name = "deeplink-backend"

// Include microservices
include("services:graph")
include("services:gateway") // Placeholder for future
include("services:brain")   // Placeholder for future
include("crawlers:fake-crawler")

// Include shared libraries if needed
// include("libs:common")