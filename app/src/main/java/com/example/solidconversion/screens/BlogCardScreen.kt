package com.example.solidconversion.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.solidconversion.R
import com.example.solidconversion.model.BlogItem
import com.example.solidconversion.ui.theme.NewsCycle
import com.example.solidconversion.ui.theme.NewsCycleBold
import com.example.solidconversion.ui.theme.RubikMono
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BlogCard(
    blog: BlogItem
) {
    Box (
        modifier = Modifier
            .background(Color(0xffEBDCD1))
            .fillMaxHeight()
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState())
        ){
            // IMAGE
            if (blog.mediaUri.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(model = Uri.parse(blog.mediaUri)),
                    contentDescription = "Blog photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(0.8f)
                        .height(300.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .align(Alignment.CenterHorizontally)
                        .border(.7.dp, Color.Black, RoundedCornerShape(8.dp))
                )
            }

            // TITLE
            Text(
                modifier = Modifier.padding(top = 18.dp, bottom = 2.dp),
                text = buildAnnotatedString {
                    // Doing this style allows for part of the text to be in the 'Medium' bold style while the data text is normal weight
                    withStyle(style = SpanStyle(fontSize = 22.sp, fontWeight = FontWeight.Medium, fontFamily = NewsCycleBold)) {
                        // Medium weight
                        append(blog.title)
                    }
                }
            )

            // SUBTITLE
            Text(
                modifier = Modifier.padding(bottom = 5.dp),
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 19.sp, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Italic, fontFamily = NewsCycle)) {
                        // Medium weight
                        append("\t\t\t${blog.subtitle}")
                    }
                }
            )

            // DATE
            Text(
                modifier = Modifier.padding(bottom = 5.dp),
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, fontFamily = NewsCycle)) {
                        // Medium weight
                        append(
                            SimpleDateFormat("MM/dd/yyyy: hh:mm a", Locale.getDefault()).format(
                                Date(blog.dateCreated)
                            )
                        )
                    }
                }
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                thickness = 1.dp,
                color = Color.Gray.copy(alpha = 0.5f)
            )

            // BODY
            if (blog.body != "") {
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 30.dp),
                    text = buildAnnotatedString {
                        // Doing this style allows for part of the text to be in the 'Medium' bold style while the data text is normal weight
                        withStyle(style = SpanStyle(fontSize = 19.sp, fontWeight = FontWeight.Medium, fontFamily = NewsCycle)) {
                            // Medium weight
                            append("\t\t${blog.body}")
                        }
                    },
                )
            }
        }
    }
}