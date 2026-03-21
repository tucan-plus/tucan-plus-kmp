package de.selfmade4u.tucanpluskmp

import androidx.room3.TypeConverter
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
                val matchResult = regex.find(input) ?: throw IllegalArgumentException(input)
                val id = matchResult.groups["id"]!!.value
                return RESULTDETAILS(id.toLong())
            }

            @TypeConverter
            fun databaseFromLong(input: Long): RESULTDETAILS {
                return RESULTDETAILS(input)
            }

            @TypeConverter
            fun databaseToLong(input: RESULTDETAILS): Long {
                return input.id
            }
        }
    }

    /**
     * without javascript /scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=GRADEOVERVIEW&ARGUMENTS=-N556273381060863,-N000324,-AMOFF,-N394844703228539,-N0,-N,-N000000015186000,-A,-N,-A,-N,-N,-N1
     * with javascript /scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=GRADEOVERVIEW&ARGUMENTS=-N556273381060863,-N000324,-AMOFF,-N394844703228539,-N0
     */
    data class GRADEOVERVIEWModule(val id: Long) : TucanUrl() {
        companion object {
            fun fromString(input: String): GRADEOVERVIEWModule {
                val regex =
                    Regex("""^/scripts/mgrqispi\.dll\?APPNAME=CampusNet&PRGNAME=GRADEOVERVIEW&ARGUMENTS=-N(?<sessionId>\d+),-N(?<menuId>\d+),-AMOFF,-N(?<id>\d+),-N(?<p5>\d+).*$""")
                val matchResult = regex.find(input) ?: throw IllegalArgumentException(input)
                val id = matchResult.groups["id"]!!.value
                return GRADEOVERVIEWModule(id.toLong())
            }

            @TypeConverter
            fun databaseFromLong(input: Long): GRADEOVERVIEWModule {
                return GRADEOVERVIEWModule(input)
            }

            @TypeConverter
            fun databaseToLong(input: GRADEOVERVIEWModule): Long {
                return input.id
            }
        }
    }
}
