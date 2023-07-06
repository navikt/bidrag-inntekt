import java.io.File
import java.time.Year
import java.time.format.DateTimeFormatter

//data class Fil(val type: String, val plussminus: Char, val sekkep: Int, val fra: LocalDate, val til: LocalDate, val beskr: String)
data class Ut(val utbeskr: String, val telles: String, val fraÅr: Year, val TomÅr: Year?, val sekkepost: String)


fun main() {
//    val inputFile = File("C:\\Users\\R153961\\Uttrekk\\input-kaps.txt")
    val inputFile = File("C:\\Users\\R153961\\Uttrekk\\input-ligs.txt")
    val outputFile = File("C:\\Users\\R153961\\Uttrekk\\output-kapsligs.txt")

    val inputLines = inputFile.readLines()

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    operator fun <T> List<T>.component6(): T {
        return get(5)
    }


    val outputList = inputLines.map { line ->
        val (_, plussminus, sekkep, fraStr, tilStr, beskr) = line.split(", ")

        val fom = Year.parse(fraStr, formatter)
        val tom = Year.parse(tilStr, formatter)

        val telles = if (plussminus == "+") "PLUSS" else "MINUS"
        val sekkepost = if (sekkep == "1") "JA" else "NEI"

        Ut(beskr, telles, fom, tom, sekkepost)
    }

    outputFile.printWriter().use { writer ->
        outputList.forEach { obj ->
            writer.print("  ${obj.utbeskr}: ")
            writer.println()
            writer.print("    telles: ${obj.telles} ")
            writer.println()
            writer.print("    sekkepost: ${obj.sekkepost} ")
            writer.println()
            writer.print("    fom: ${obj.fraÅr} ")
            writer.println()
            writer.print("    tom: ${obj.TomÅr} ")
            writer.println()
        }
    }

    println("Output file generated successfully.")
}