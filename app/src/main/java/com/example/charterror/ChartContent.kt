package com.example.charterror

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.component.TextComponent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun ChartContent() {
    val modelProducer = remember { CartesianChartModelProducer() }
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("MMM. dd")

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            val minDate = LocalDateTime.parse("2024-08-04T20:00:00")
            val values = (0..140).map { value ->
                minDate.plusMinutes((5 * value).toLong())
            }
            lineSeries {
                val yValues = values.mapIndexed { index, _ -> index }
                val xValues =
                    values.map { it.toEpochSecond(ZoneOffset.UTC) }
                series(xValues, yValues)
            }
        }
    }
    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                itemPlacer = remember {
                    HorizontalAxis.ItemPlacer.aligned(
                        spacing = { 1 }
                        )
                },

                label = rememberAxisLabelComponent(minWidth = TextComponent.MinWidth.fixed(111f)),
                valueFormatter = remember {
                    CartesianValueFormatter { context, value, verticalAxisPosition ->
                        val toLocalDateTime = Instant.ofEpochSecond(value.toLong())
                            .atZone(ZoneOffset.UTC)
                            .toLocalDateTime()

                        val localTime = toLocalDateTime.toLocalTime()
                        if (localTime.minute == 0 && localTime.hour == 0) {
                            toLocalDateTime.format(dateFormatter)
                        } else {
                            localTime.format(timeFormatter)
                        }
                    }
                },
            ),
        ),
        scrollState = rememberVicoScrollState(
            false,
            autoScrollCondition = AutoScrollCondition.OnModelGrowth
        ),
        zoomState = rememberVicoZoomState(
            false,
            Zoom.Content,
            Zoom.Content,
            Zoom.Content,
        ),
        modelProducer = modelProducer,
    )
}