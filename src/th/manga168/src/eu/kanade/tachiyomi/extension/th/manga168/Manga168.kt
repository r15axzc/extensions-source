package eu.kanade.tachiyomi.extension.th.manga168

import android.util.Log
import eu.kanade.tachiyomi.multisrc.mangathemesia.MangaThemesia
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SManga
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class Manga168 : MangaThemesia(
    "Manga168",
    "https://manga168.net",
    "th",
    dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale("th")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Bangkok")
    },
) {
    override fun mangaDetailsParse(document: Document): SManga {
        return super.mangaDetailsParse(document).apply {
            // Add 'color' badge as a genre
            if (document.selectFirst(".thumb .colored") != null) {
                genre = genre?.plus(", Color")
            }
        }
    }

    private fun normalizeUrl(url: String): String {
        // Replace escaped slashes
        return url.replace("\\/", "/")
    }

    override fun pageListParse(document: Document): List<Page> {
        countViews(document)

        val chapterUrl = document.location()
        val htmlPages = document.select(pageSelector)
            .filterNot { it.imgAttr().isEmpty() }
            .mapIndexed { i, img -> Page(i, chapterUrl, img.imgAttr()) }
        for (page in htmlPages) {
            Log.w("Manga168", "Page: ${page.imageUrl}")
            page.imageUrl = page.imageUrl?.let { normalizeUrl(it) }
        }
        // Some sites also loads pages via javascript
        if (htmlPages.isNotEmpty()) { return htmlPages }

        val docString = document.toString()
        val imageListJson = JSON_IMAGE_LIST_REGEX.find(docString)?.destructured?.toList()?.get(0).orEmpty()
        val imageList = try {
            json.parseToJsonElement(imageListJson).jsonArray
        } catch (_: IllegalArgumentException) {
            emptyList()
        }
        val scriptPages = imageList.mapIndexed { i, jsonEl ->
            Page(i, chapterUrl, jsonEl.jsonPrimitive.content)
        }

        return scriptPages
    }
}
