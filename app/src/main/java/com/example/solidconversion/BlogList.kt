package com.example.solidconversion

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.solidconversion.model.BlogItem

@Composable
fun BlogList(
    blogs: List<BlogItem>,
    onDeleteBlog: (BlogItem) -> Unit,
    onEditBlog: (BlogItem) -> Unit,
    onSelectBlog: (BlogItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(blogs) { blogPost ->
            BlogItem(
                blogPost = blogPost,
                onDelete = onDeleteBlog,
                onEdit = onEditBlog,
                onSelect = onSelectBlog
            )
        }
    }
}