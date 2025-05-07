import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.angel.barcatcher.api.Model.Cafebar
import com.angel.barcatcher.api.Model.Drinkbar
import com.angel.barcatcher.repository.barCafeRepository
import com.angel.barcatcher.repository.barDrinkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONObject

@Composable
fun BarJsonViewer(
    ID: String,
    cafeRep: barCafeRepository,
    modifier: Modifier = Modifier,
    maxHeight: Int? = 400
) {
    var bar by remember { mutableStateOf<List<Cafebar>?>(null) }
    if (ID.contains("Cafebar")) {
        LaunchedEffect(true) {
            val query = GlobalScope.async(Dispatchers.IO) { cafeRep.getCafe(ID) }
            bar = query.await().body()?.Results
        }
    }
    if (bar != null) {
        val jsonString = createBarJsonString(bar!!.first())

        val contentModifier = if (maxHeight != null) {
            Modifier
                .verticalScroll(rememberScrollState())
                .heightIn(max = maxHeight.dp)
        } else {
            Modifier.verticalScroll(rememberScrollState())
        }

        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            tonalElevation = 1.dp
        ) {
            SelectionContainer {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .then(contentModifier)
                ) {
                    Text(
                        text = formatJsonWithMarkdown(jsonString),
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
@Composable
fun BarJsonViewer(
    ID: String,
    drinkRep: barDrinkRepository,
    modifier: Modifier = Modifier,
    maxHeight: Int? = 400
) {
    var bar by remember { mutableStateOf<List<Drinkbar>?>(null) }
    if (ID.contains("Drinkbar")) {
        LaunchedEffect(true) {
            val query = GlobalScope.async(Dispatchers.IO) { drinkRep.getDrink(ID) }
            bar = query.await().body()?.Results
        }

        if (bar != null) {
            val jsonString = createBarJsonString(bar!!.first())

            val contentModifier = if (maxHeight != null) {
                Modifier
                    .verticalScroll(rememberScrollState())
                    .heightIn(max = maxHeight.dp)
            } else {
                Modifier.verticalScroll(rememberScrollState())
            }

            Surface(
                modifier = modifier,
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                tonalElevation = 1.dp
            ) {
                SelectionContainer {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .then(contentModifier)
                    ) {
                        Text(
                            text = formatJsonWithMarkdown(jsonString),
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

private fun createBarJsonString(bar: Cafebar): String {
    val jsonObj = JSONObject()

    jsonObj.put("name", bar.name)
    bar.geo_long?.let { jsonObj.put("geolong", it) }
    bar.url?.let { jsonObj.put("url", it) }
    bar.geo_lat?.let { jsonObj.put("geolat", it) }
    bar.email?.let { jsonObj.put("email", it) }
    jsonObj.put("tel", bar.tel)
    bar.capacity?.let { jsonObj.put("capacity", it) }

    val addressObj = JSONObject()
    addressObj.put("streetAddress", bar.address_streetAddress)
    addressObj.put("addressLocality", bar.address_addressLocality)
    addressObj.put("addressCountry", bar.address_addressCountry)
    addressObj.put("postalCode", bar.address_postalCode)
    jsonObj.put("address", addressObj)

    val metadataObj = JSONObject()
    metadataObj.put("ID", bar.metadata.id)
    metadataObj.put("Collection", bar.metadata.collection)
    metadataObj.put("Last Modified", bar.metadata.lastModified)
    metadataObj.put("Change Vector", bar.metadata.changeVector)
    jsonObj.put("@metadata", metadataObj)

    return jsonObj.toString(4)
}

private fun createBarJsonString(bar: Drinkbar): String {
    val jsonObj = JSONObject()

    jsonObj.put("name", bar.name)
    bar.geo_long?.let { jsonObj.put("geolong", it) }
    bar.url?.let { jsonObj.put("url", it) }
    bar.geo_lat?.let { jsonObj.put("geolat", it) }
    bar.email?.let { jsonObj.put("email", it) }
    jsonObj.put("tel", bar.tel)
    bar.capacity?.let { jsonObj.put("capacity", it) }

    val addressObj = JSONObject()
    addressObj.put("streetAddress", bar.address_streetAddress)
    addressObj.put("addressLocality", bar.address_addressLocality)
    addressObj.put("addressCountry", bar.address_addressCountry)
    addressObj.put("postalCode", bar.address_postalCode)
    jsonObj.put("address", addressObj)

    val metadataObj = JSONObject()
    metadataObj.put("ID", bar.metadata.id)
    metadataObj.put("Collection", bar.metadata.collection)
    metadataObj.put("Last Modified", bar.metadata.lastModified)
    metadataObj.put("Change Vector", bar.metadata.changeVector)
    jsonObj.put("@metadata", metadataObj)

    return jsonObj.toString(4)
}

@Composable
private fun formatJsonWithMarkdown(jsonString: String): AnnotatedString {
    val keyColor = MaterialTheme.colorScheme.primary
    val stringColor = MaterialTheme.colorScheme.tertiary
    val numberColor = MaterialTheme.colorScheme.secondary
    val booleanColor = MaterialTheme.colorScheme.secondary
    val nullColor = Color.Gray
    val punctuationColor = MaterialTheme.colorScheme.onSurfaceVariant

    return buildAnnotatedString {
        val lines = jsonString.lines()

        for ((index, line) in lines.withIndex()) {
            var position = 0
            val trimmedLine = line.trimStart()

            val indentation = line.takeWhile { it.isWhitespace() }
            append(indentation)
            position += indentation.length

            val keyValuePattern = "\"([^\"]+)\"\\s*:\\s*(.*)".toRegex()
            val keyValueMatch = keyValuePattern.find(trimmedLine)

            if (keyValueMatch != null) {
                val (key, valueStr) = keyValueMatch.destructured

                withStyle(SpanStyle(color = keyColor, fontWeight = FontWeight.Bold)) {
                    append("\"$key\"")
                }

                withStyle(SpanStyle(color = punctuationColor)) {
                    val colonPart = trimmedLine.substring(
                        keyValueMatch.groups[1]!!.range.last + 1,
                        keyValueMatch.groups[2]!!.range.first
                    )
                    append(colonPart)
                }

                colorizeValue(
                    valueStr.trim(),
                    stringColor,
                    numberColor,
                    booleanColor,
                    nullColor,
                    punctuationColor
                )
            } else {
                colorizeValue(
                    trimmedLine,
                    stringColor,
                    numberColor,
                    booleanColor,
                    nullColor,
                    punctuationColor
                )
            }

            if (index < lines.size - 1) {
                append("\n")
            }
        }
    }
}

private fun AnnotatedString.Builder.colorizeValue(
    value: String,
    stringColor: Color,
    numberColor: Color,
    booleanColor: Color,
    nullColor: Color,
    punctuationColor: Color
) {
    when {
        value.startsWith("\"") && value.endsWith("\"") -> {
            withStyle(SpanStyle(color = stringColor)) {
                append(value)
            }
        }

        value.startsWith("\"") && !value.endsWith("\"") -> {
            val stringPart = value.substringBeforeLast(',', value)
            withStyle(SpanStyle(color = stringColor)) {
                append(stringPart)
            }
            if (stringPart.length < value.length) {
                withStyle(SpanStyle(color = punctuationColor)) {
                    append(",")
                }
            }
        }

        value == "null" -> {
            withStyle(SpanStyle(color = nullColor)) {
                append(value)
            }
        }

        value == "true" || value == "false" -> {
            withStyle(SpanStyle(color = booleanColor)) {
                append(value)
            }
        }

        value.toDoubleOrNull() != null -> {
            val numberPart = value.substringBeforeLast(',', value)
            withStyle(SpanStyle(color = numberColor)) {
                append(numberPart)
            }
            if (numberPart.length < value.length) {
                withStyle(SpanStyle(color = punctuationColor)) {
                    append(",")
                }
            }
        }

        value.startsWith("{") || value.startsWith("[") || value.startsWith("}") || value.startsWith(
            "]"
        ) -> {
            withStyle(SpanStyle(color = punctuationColor)) {
                append(value)
            }
        }

        else -> {
            append(value)
        }
    }
}