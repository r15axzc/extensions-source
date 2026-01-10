package eu.kanade.tachiyomi.extension.th.mangayipun

import eu.kanade.tachiyomi.multisrc.mangathemesia.MangaThemesia
import eu.kanade.tachiyomi.source.model.SManga
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class Mangayipun : MangaThemesia(
    "มังงะญี่ปุ่น",
    "https://xn--72cas2cj6a4hf4b5a8oc.com",
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
}
