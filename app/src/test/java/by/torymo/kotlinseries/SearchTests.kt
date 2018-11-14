package by.torymo.kotlinseries

import by.torymo.kotlinseries.data.network.Requester
import org.junit.Test

class SearchTests {

    @Test
    fun searchEmpty(){
        var requester = Requester()

        requester.search(mutableMapOf(), null)
    }
}