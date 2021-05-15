import java.nio.file.Files
import java.nio.file.Path

actual fun readAllText(filePath: String): String {
    return Files.readString(Path.of(filePath))
}

actual fun writeAllText(filePath:String, text:String) {
    Files.writeString(Path.of(filePath), text)
}
