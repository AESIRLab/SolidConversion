package com.example.solidconversion.model

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zybooks.sksolidannotations.SolidAnnotation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import coil.compose.rememberAsyncImagePainter

@SolidAnnotation(
    "http://www.w3.org/2024/sc/core#",
    "AndroidApplication/SolidConversion"
)
data class BlogItem(
    var id: String,
    var title: String,
    var subtitle: String,
    var body: String,
    var date: Long = System.currentTimeMillis(),
    var mediaUri: String = "",
)

@Composable
fun BlogItem(
    blogPost: BlogItem,
    onDelete: (BlogItem) -> Unit,
    onEdit: (BlogItem) -> Unit,
    onSelect: (BlogItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSelect(blogPost) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 12.dp, top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                // TITLE
                Text(
                    text = blogPost.title,
                    fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 5.dp),
                    maxLines = 2,
                )
                // SUBTITLE
                Text(
                    text = blogPost.subtitle,
                    fontSize = 14.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(bottom = 5.dp),
                    maxLines = 3,
                )
                // DATE
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                            // Medium weight
                            append("Date: ")
                        }
                        // Normal weight
                        append(
                            SimpleDateFormat("MM/dd/yyyy: hh:mm a", Locale.getDefault()).format(
                            Date(blogPost.date)
                        ))
                    }
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // THUMBNAIL
                if (blogPost.mediaUri.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = Uri.parse(blogPost.mediaUri)),
                        contentDescription = "Blog photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Row(modifier = Modifier.padding(top = 6.dp)) {
                    // EDIT BUTTON
                    IconButton(onClick = { onEdit(blogPost) }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Edit blog",
                            tint = Color.Black
                        )
                    }

                    // DELETE BUTTON
                    IconButton(onClick = { onDelete(blogPost) }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete blog",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}