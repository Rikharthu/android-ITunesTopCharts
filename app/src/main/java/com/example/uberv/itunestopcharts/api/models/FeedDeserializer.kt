package com.example.uberv.itunestopcharts.api.models

import android.text.TextUtils
import com.google.gson.*
import java.lang.reflect.Type

class FeedDeserializer : JsonDeserializer<Feed> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Feed? {
        val feedJsonObj = json?.asJsonObject?.getAsJsonObject("feed")
        if (feedJsonObj != null) {
            val authorJsonObj: JsonObject? = feedJsonObj.getAsJsonObject("author")
            val author = authorJsonObj?.getAsJsonObject("name")?.getLabel()
            val authorUrl = authorJsonObj?.getAsJsonObject("uri")?.getLabel()
            val updated = feedJsonObj.getAsJsonObject("updated")?.getLabel()
            val rights = feedJsonObj.getAsJsonObject("rights")?.getLabel()
            val title = feedJsonObj.getAsJsonObject("title")?.getLabel()
            val icon = feedJsonObj.getAsJsonObject("icon")?.getLabel()
            val id = feedJsonObj.getAsJsonObject("id")?.getLabel()

            // Parse links
            // TODO understand it
            val links = extractLinks(feedJsonObj)

            // Parse entries
            val entries = feedJsonObj.getAsJsonArray("entry")
                    ?.filterNotNull()
                    ?.filterIsInstance<JsonObject>()
                    ?.mapNotNull {
                        parseEntry(it)
                    }

            return Feed(author = author,
                    authorUrl = authorUrl,
                    updated = updated,
                    rights = rights,
                    title = title,
                    icon = icon,
                    id = id,
                    link = links,
                    entry = entries)
        }
        return null
    }

    private fun parseLink(linkJsonObj: JsonObject?): Link? {
        val attributes = linkJsonObj?.getAsJsonObjectOrNull("attributes")
        return if (attributes != null) {
            Link(
                    rel = attributes.get("rel")?.extractString(),
                    type = attributes.get("type")?.extractString(),
                    href = attributes.get("href")?.extractString(),
                    title = attributes.get("title")?.extractString(),
                    assetType = attributes.get("assetType")?.extractString(),
                    duration = linkJsonObj.getAsJsonObjectOrNull("im:duration")?.getLabel()?.toLong()
            )
        } else {
            null
        }
    }

    private fun parseEntry(entryObj: JsonObject?): Entry? {
        if (entryObj != null) {
            val name = entryObj.getAsJsonObjectOrNull("im:name")?.getLabel()
            val rights = entryObj.getAsJsonObjectOrNull("rights")?.getLabel()
            val title = entryObj.getAsJsonObjectOrNull("title")?.getLabel()
            val priceObj = entryObj.getAsJsonObjectOrNull("im:price")
            val price = priceObj?.getLabel()
            val priceAmount = priceObj.getAsJsonObjectOrNull("attributes")
                    ?.get("amount")?.extractDouble()
            val priceCurrency = priceObj.getAsJsonObjectOrNull("attributes")
                    ?.get("currency")?.extractString()
            val links = extractLinks(entryObj)
            val id = entryObj.getAsJsonObjectOrNull("id")?.getLabel() // idUri?
            val artist = entryObj.getAsJsonObjectOrNull("im:artist")?.getLabel()
            // TODO artistUrl
            val releaseDate = entryObj.getAsJsonObjectOrNull("im:releaseDate")?.getLabel()
            val releaseDateLabel = entryObj.getAsJsonObjectOrNull("im:releaseDate")
                    ?.getAsJsonObjectOrNull("attributes")?.getLabel()
            // TODO ContentType
            // TODO Category

            return Entry(
                    name = name,
                    rights = rights,
                    title = title,
                    releaseDate = releaseDate,
                    releaseDateLabel = releaseDateLabel,
                    price = price,
                    priceAmount = priceAmount,
                    priceCurrency = priceCurrency,
                    link = links,
                    artist = artist
            )
        } else {
            return null
        }
    }

    private fun extractLinks(jsonObj: JsonObject): List<Link>? {
        return jsonObj.getAsJsonArray("link")
                ?.filterNotNull() // returns a list containing only non-null elements
                ?.filterIsInstance<JsonObject>()
                ?.mapNotNull {
                    parseLink(it)
                }
    }

    private fun JsonObject?.getAsJsonObjectOrNull(memberName: String): JsonObject? {
        val element = this?.get(memberName)
        if (element != null && element is JsonObject) {
            return element
        }
        return null
    }

    private fun JsonObject.getLabel(): String? {
        return this.get("label")?.extractString()
    }

    private fun JsonElement.extractString(): String? {
        if (this is JsonPrimitive) {
            val elementString = this.asString
            if (!TextUtils.isEmpty(elementString)) {
                return elementString
            }
        }
        return null
    }

    private fun JsonElement.extractLong(): Long? {
        return extractString()?.toLong()
    }

    private fun JsonElement.extractDouble(): Double? {
        return extractString()?.toDouble()
    }

//    private fun getLabel(jsonObj: JsonObject?): String? {
//        return getAsString(jsonObj?.get("label"))
//    }
}