package com.github.medifitprima.epcisclient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import de.unirostock.sems.cbarchive.ArchiveEntry
import de.unirostock.sems.cbarchive.CombineArchive
import java.io.File
import java.net.URI
import kotlin.io.path.*
/** Extension functions for Combine archives. */
fun ArchiveEntry.loadTextEntry(): String {
    val tempFile = createTempFile().toFile()
    return try {
        extractFile(tempFile)
        tempFile.readText()
    } finally {
        tempFile.delete()
    }
}

fun readMetadata(file: File, mapper: ObjectMapper): JsonNode {
    CombineArchive(file).use { archive ->
        val jsonUri = URI("https://www.iana.org/assignments/media-types/application/json")//FSKML.getURIS(1, 0, 12)["json"]!!

        val metadataEntry = archive.getEntriesWithFormat(jsonUri)
            .first { it.fileName == "metaData.json" }

        val tempFile = createTempFile().toFile()
        return try {
            metadataEntry.extractFile(tempFile)
            mapper.readTree(tempFile)
        } finally {
            tempFile.delete()
        }
    }
}
