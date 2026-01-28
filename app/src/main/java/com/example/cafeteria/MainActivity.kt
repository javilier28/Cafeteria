package com.example.cafeteria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.cafeteria.ui.theme.CafeteriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CafeteriaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CafeteriaScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class Pedido(val tipo: String, val cantidad: Int, val precio: Int)

@Composable
fun CafeteriaScreen(modifier: Modifier = Modifier) {
    val azul = Color(0xFF1E4A80)
    val verde = Color(0xFF2E7D32)
    val precioUnidad = 10

    var nombreCliente by rememberSaveable { mutableStateOf("") }

    val bocadillos = listOf("Tortilla", "Jamón", "Mixto")
    var bocadilloSeleccionado by rememberSaveable { mutableStateOf(bocadillos[0]) }
    var menuAbierto by rememberSaveable { mutableStateOf(false) }

    var cantidad by rememberSaveable { mutableStateOf(1) }

    var pedidos by rememberSaveable { mutableStateOf(listOf<Pedido>()) }

    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        Image(
            painter = painterResource(R.drawable.images),
            contentDescription = "Logo cafetería",
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Cafetería Politécnico",
            color = azul,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = nombreCliente,
            onValueChange = { nombreCliente = it },
            label = { Text("Nombre cliente") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = { menuAbierto = true },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azul, contentColor = Color.White)
            ) {
                Text("Bocadillo: $bocadilloSeleccionado")
            }

            DropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { menuAbierto = false }
            ) {
                bocadillos.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            bocadilloSeleccionado = opcion
                            menuAbierto = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Cantidad:",
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFEDEFF3),
            modifier = Modifier
                .wrapContentWidth()
                .padding(top = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleMiniButton(text = "-", onClick = { if (cantidad > 1) cantidad-- })
                Text(
                    text = cantidad.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
                CircleMiniButton(text = "+", onClick = { cantidad++ })
            }
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = {
                val nuevo = Pedido(
                    tipo = bocadilloSeleccionado,
                    cantidad = cantidad,
                    precio = cantidad * precioUnidad
                )
                pedidos = pedidos + nuevo
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = azul, contentColor = Color.White)
        ) {
            Text("Añadir pedido (${cantidad * precioUnidad}€)")
        }

        Spacer(Modifier.height(14.dp))

        if (pedidos.isNotEmpty()) {
            Text(
                text = "Lista de pedidos (${pedidos.size}) para $nombreCliente",
                color = verde,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                itemsIndexed(
                    items = pedidos,
                    key = { index, item -> "$index-${item.tipo}-${item.cantidad}-${item.precio}" }
                ) { _, pedido ->
                    PedidoCard(pedido = pedido, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(10.dp))
                }
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { mostrarDialogo = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azul, contentColor = Color.White)
            ) {
                Text("Confirma pedido")
            }

            Spacer(Modifier.height(12.dp))
        } else {
            Spacer(Modifier.height(12.dp))
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogo = false },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azul, contentColor = Color.White)
                ) {
                    Text("Aceptar")
                }
            },
            title = { Text("Confirmación del pedido") },
            text = { Text("Gracias por realizar un pedido en la Cafetería del Politécnico") }
        )
    }
}

@Composable
fun CircleMiniButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(36.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PedidoCard(pedido: Pedido, modifier: Modifier = Modifier) {
    val imagen = when (pedido.tipo) {
        "Tortilla" -> R.drawable.tortilla
        "Jamón" -> R.drawable.jamon
        else -> R.drawable.mixto
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(imagen),
                contentDescription = pedido.tipo,
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = "${pedido.cantidad} x ${pedido.tipo}",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "${pedido.precio}€",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
