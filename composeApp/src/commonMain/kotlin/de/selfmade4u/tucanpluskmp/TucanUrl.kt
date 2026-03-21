package de.selfmade4u.tucanpluskmp

import kotlin.text.get

sealed class TucanUrl {
    /**
     * without javascript /scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=RESULTDETAILS&ARGUMENTS=-N$sessionId,-N$menuId,-N$id,-N$semester
     * with javascript /scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=RESULTDETAILS&ARGUMENTS=-N$sessionId,-N$menuId,-N$id
     */
    data class RESULTDETAILS(val id: Long) : TucanUrl() {
        companion object {
            fun fromString(input: String): RESULTDETAILS {
                val regex =
                    Regex("""^/scripts/mgrqispi\.dll\?APPNAME=CampusNet&PRGNAME=RESULTDETAILS&ARGUMENTS=-N(?<sessionId>\d+),-N(?<menuId>\d+),-N(?<id>\d+)(,-N(?<semester>\d+))?$""")
                val matchResult = regex.find(input) ?: throw IllegalArgumentException()
                val id = matchResult.groups["id"]!!.value
                return RESULTDETAILS(id.toLong())
            }
        }
    }
}
