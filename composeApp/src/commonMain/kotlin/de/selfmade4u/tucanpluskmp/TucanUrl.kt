package de.selfmade4u.tucanpluskmp

import androidx.room3.Entity
import androidx.room3.TypeConverter
import kotlin.text.get

sealed class TucanUrl {
    /**
     * without javascript /scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=RESULTDETAILS&ARGUMENTS=-N$sessionId,-N$menuId,-N$id,-N$semester
     * with javascript /scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=RESULTDETAILS&ARGUMENTS=-N$sessionId,-N$menuId,-N$id
     */
    @Entity
    data class RESULTDETAILS(val id: Long) : TucanUrl() {
        companion object {
            fun fromString(input: String): RESULTDETAILS {
                val regex =
                    Regex("""^/scripts/mgrqispi\.dll\?APPNAME=CampusNet&PRGNAME=RESULTDETAILS&ARGUMENTS=-N(?<sessionId>\d+),-N(?<menuId>\d+),-N(?<id>\d+)(,-N(?<semester>\d+))?$""")
                val matchResult = regex.find(input) ?: throw IllegalArgumentException(input)
                val id = matchResult.groups["id"]!!.value
                return RESULTDETAILS(id.toLong())
            }
        }
    }

    @Entity
    data class COURSEDETAILS(val courseId: Long, val courseGroupId: Long) : TucanUrl() {
        companion object {
            fun fromString(input: String): COURSEDETAILS {
                val regex =
                    Regex("""^/scripts/mgrqispi\.dll\?APPNAME=CampusNet&PRGNAME=COURSEDETAILS&ARGUMENTS=-N(?<sessionId>\d+),-N(?<menuId>\d+),-N(?<courseOfStudy>\d+),-N(?<courseId>\d+),-N(?<courseGroupId>\d+),-N0,-N0(,-N3,-A.+)?$""")
                val matchResult = regex.find(input) ?: throw IllegalArgumentException(input)
                val courseId = matchResult.groups["courseId"]!!.value
                val courseGroupId = matchResult.groups["courseGroupId"]!!.value
                return COURSEDETAILS(courseId.toLong(), courseGroupId.toLong())
            }
        }
    }

    /**
     * without javascript /scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=GRADEOVERVIEW&ARGUMENTS=-N556273381060863,-N000324,-AMOFF,-N394844703228539,-N0,-N,-N000000015186000,-A,-N,-A,-N,-N,-N1
     * with javascript /scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=GRADEOVERVIEW&ARGUMENTS=-N556273381060863,-N000324,-AMOFF,-N394844703228539,-N0
     */
    @Entity
    data class GRADEOVERVIEWModule(val id: Long) : TucanUrl() {
        companion object {
            fun fromString(input: String): GRADEOVERVIEWModule {
                val regex =
                    Regex("""^/scripts/mgrqispi\.dll\?APPNAME=CampusNet&PRGNAME=GRADEOVERVIEW&ARGUMENTS=-N(?<sessionId>\d+),-N(?<menuId>\d+),-AMOFF,-N(?<id>\d+),-N0(,-N,-N\d+,-A,-N,-A,-N,-N,-N1)?$""")
                val matchResult = regex.find(input) ?: throw IllegalArgumentException(input)
                val id = matchResult.groups["id"]!!.value
                return GRADEOVERVIEWModule(id.toLong())
            }
        }
    }
}
