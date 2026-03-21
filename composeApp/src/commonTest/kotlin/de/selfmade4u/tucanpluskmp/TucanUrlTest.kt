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
}