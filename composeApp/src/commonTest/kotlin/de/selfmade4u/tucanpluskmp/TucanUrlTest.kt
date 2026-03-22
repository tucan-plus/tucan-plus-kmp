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
    fun testCOURSEDETAILS() {
        assertEquals(TucanUrl.COURSEDETAILS(395815324045539), TucanUrl.COURSEDETAILS.fromString("/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=COURSEDETAILS&ARGUMENTS=-N916877825818552,-N000318,-N0,-N393376109436095,-N393376109438096,-N0,-N0,-N3,-AvWRKRfKCvNw5cNH7xgldONHQOz6mQfHIeULo3SfdCvNNef5CVYwSWgLKWoHZmIoIeUpQCfnAcgREmDofRqPVVzRfxqKYOMPzVfHhQqW9RoK-YIP8WzApmzetx-W73oRqfQPP7dyZeMoefInwHBGumu5CWWUsRQPBP-o04opuWf5mQglFvYobcDZ7fumv4UU5RgP8fS7NRvZPetNAfImmYUPuxNKMvI5QVun6x-7AcURKfBDjmDwDYWcAYfwemzH7HYwbxdRC3uHhxjndcNKeOZKzeWK9rgLEVMRSHSRzvSLDejRovW59QDmKxDwXQjAd3YoM7fKy7ZPpHIHmRSLE4gHZPqUXHYmmVMpKc-ozcvZq4Q5L4SAJxZppcUKVHjAWQgoPcDLXPqKQ4fKuxUHFHDRBcZWAx-5v7-mxmSP-CQin"))
    }

    @Test
    fun testGRADEOVERVIEWModule() {
        // without javascript
        assertEquals(TucanUrl.GRADEOVERVIEWModule(394844703228539), TucanUrl.GRADEOVERVIEWModule.fromString("/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=GRADEOVERVIEW&ARGUMENTS=-N556273381060863,-N000324,-AMOFF,-N394844703228539,-N0,-N,-N000000015186000,-A,-N,-A,-N,-N,-N1"))
        // with javascript
        assertEquals(TucanUrl.GRADEOVERVIEWModule(394844703228539), TucanUrl.GRADEOVERVIEWModule.fromString("/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=GRADEOVERVIEW&ARGUMENTS=-N556273381060863,-N000324,-AMOFF,-N394844703228539,-N0"))
    }
}