package de.selfmade4u.tucanpluskmp

import kotlin.test.Test
import kotlin.test.assertEquals

class TucanUrlTest {

    @Test
    fun testRESULTDETAILS() {
        // without javascript
        assertEquals(TucanUrl.RESULTDETAILS(395815324045539), TucanUrl.RESULTDETAILS.fromString("/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=RESULTDETAILS&ARGUMENTS=-N119367461644278,-N000363,-N395815324045539,-N000000015186000"))
        // with javascript
        assertEquals(TucanUrl.RESULTDETAILS(395815324045539), TucanUrl.RESULTDETAILS.fromString("/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=RESULTDETAILS&ARGUMENTS=-N119367461644278,-N000363,-N395815324045539"))
    }

    @Test
    fun testGRADEOVERVIEWModule() {
        // without javascript
        assertEquals(TucanUrl.GRADEOVERVIEWModule(394844703228539), TucanUrl.GRADEOVERVIEWModule.fromString("/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=GRADEOVERVIEW&ARGUMENTS=-N556273381060863,-N000324,-AMOFF,-N394844703228539,-N0,-N,-N000000015186000,-A,-N,-A,-N,-N,-N1"))
        // with javascript
        assertEquals(TucanUrl.GRADEOVERVIEWModule(394844703228539), TucanUrl.GRADEOVERVIEWModule.fromString("/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=GRADEOVERVIEW&ARGUMENTS=-N556273381060863,-N000324,-AMOFF,-N394844703228539,-N0"))
    }
}