package dev.themeinerlp.attollo

import java.net.URI
import java.net.http.HttpRequest



const val USE_PERMISSION = "attollo.use"
const val NOTIFY_UPDATE_PERMISSION = "attollo.update"
const val BYTEBIN_BASE_URL = "https://paste.grim.ac/data"

const val NOTIFY_PLAYER_UPDATE_MESSAGE = "<yellow><download_url>Your version (<local_version>) is older than our latest published version (<remote_version>). Please update as soon as possible to get continued support. Or click me to get on the download page!"
const val NOTIFY_CONSOLE_UPDATE_MESSAGE = "<yellow>Your version (<local_version>) is older than our latest published version (<remote_version>). Please update as soon as possible to get continued support. Or use this link <download_url>."

val LATEST_RELEASE_VERSION_URI = URI.create("https://hangar.papermc.io/api/v1/projects/Attollo/latestrelease")
val LATEST_RELEASE_VERSION_REQUEST = HttpRequest.newBuilder().GET().uri(LATEST_RELEASE_VERSION_URI).build()