package dev.themeinerlp.attollo

import java.net.URI
import java.net.http.HttpRequest



const val USE_PERMISSION = "attollo.use"
const val NOTIFY_UPDATE_PERMISSION = "attollo.update"
const val BYTEBIN_BASE_URL = "https://paste.grim.ac/data"

val LATEST_RELEASE_VERSION_URI = URI.create("https://hangar.papermc.io/api/v1/projects/Attollo/latestrelease")
val LATEST_RELEASE_VERSION_REQUEST = HttpRequest.newBuilder().GET().uri(LATEST_RELEASE_VERSION_URI).build()