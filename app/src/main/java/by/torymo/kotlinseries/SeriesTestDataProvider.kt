package by.torymo.kotlinseries

import by.torymo.kotlinseries.data.db.Episode
import by.torymo.kotlinseries.data.db.Series

class SeriesTestDataProvider {
    companion object {
        var seriesList = initSeriesList()
        var episodesList = initEpisodesList()

        private fun initSeriesList(): MutableList<Series> {
            var series = mutableListOf<Series>()
            series.add(Series(
                    null,
                    "Arrow",
                    "Arrow",
                    "1412",
                    "1412",
                    "/mo0FP1GxOFZT4UDde7RFDz5APXF.jpg",
                    true,
                    "Crime",
                    1286668800000,
                    7,
                    "Still in prison, Oliver faces his biggest challenge yet",
                    118.797,
                    "http://www.cwtv.com/shows/arrow",
                    "The CW",
                    7,
                    false,
                    "Returning Series"))
            series.add(Series(
                    null,
                    "Marvel's Daredevil",
                    "Marvel's Daredevil",
                    "61889",
                    "61889",
                    "/wVadC1BT2w3hDh5Vq0J0LFFTrLP.jpg",
                    true,
                    "Action",
                    1428624000000,
                    3,
                    "Lawyer-by-day Matt Murdock uses his heightened senses from being",
                    37.938,
                    "http://www.netflix.com/WiMovie/80018294",
                    "netflix",
                    3,
                    false,
                    "Returning Series"))
            series.add(Series(
                    null,
                    "The Big Bang Theory",
                    "The Big Bang Theory",
                    "1418",
                    "1418",
                    "/ooBGRQBdbGzBxAVfExiO8r7kloA.jpg",
                    true,
                    "Comedy",
                    1190592000000,
                    12,
                    "The Big Bang Theory is centered on five characters living in Pasadena, California",
                    89.812,
                    "http://www.cbs.com/shows/big_bang_theory/",
                    "CBS",
                    12,
                    false,
                    "Returning Series"))
            return series
        }

        private fun initEpisodesList(): MutableList<Episode> {
            var episodes = mutableListOf<Episode>()
            episodes.add(Episode(
                    null,
                    "Inmate 4587",
                    1539561600000,
                    "1412",
                    1,
                    7,
                    false,
                    "Following Oliver’s shocking decision to turn himself over to the FBI and reveal his identity as the Green Arrow to the public",
                    "/wGjYh1D0lrXRsKPB0Oxno76HTa2.jpg"))
            episodes.add(Episode(
                    null,
                    "The Longbow Hunters",
                    1540166400000,
                    "1412",
                    2,
                    7,
                    false,
                    "In order to track down Diaz from inside prison, Oliver realizes that will require aligning with an old enemy",
                    "/2Ad5UlW30XE2rkABMtQIM4Uzu0C.jpg"))
            episodes.add(Episode(
                    null,
                    "Crossing Lines",
                    1540771200000,
                    "1412",
                    3,
                    7,
                    false,
                    "Still in prison, Oliver faces his biggest challenge yet",
                    "/2Ad5UlW30XE2rkABMtQIM4Uzu0C.jpg"))

            episodes.add(Episode(
                    null,
                    "Resurrection",
                    1539907200000,
                    "61889",
                    1,
                    3,
                    false,
                    "Shattered physically and spiritually, Matt rethinks his purpo",
                    "/l7RZFInQVmkpqyESagGSXPlSeyL.jpg"))
            episodes.add(Episode(
                    null,
                    "Please",
                    1539907200000,
                    "61889",
                    2,
                    3,
                    false,
                    "Grieving for the life he's abandoned, Matt suffers a crisis",
                    "/hWaoa7t1Nlo5rNIy5Yq24tNDR3P.jpg"))
            episodes.add(Episode(
                    null,
                    "No Good Deed",
                    1539907200000,
                    "61889",
                    3,
                    3,
                    false,
                    "As Fisk moves into swanky new digs amid a public outcry",
                    "/2ZDMwWYx5qQd3s5ar0sq3drcmkh.jpg"))

            episodes.add(Episode(
                    null,
                    "The Conjugal Conjecture",
                    1537747200000,
                    "1418",
                    1,
                    12,
                    true,
                    "Sheldon and Amy's honeymoon runs aground in New York",
                    "/tH6udsYfheFElUCMtc10PoxzhHH.jpg"))
            episodes.add(Episode(
                    null,
                    "The Wedding Gift Wormhole",
                    1538006400000,
                    "1418",
                    2,
                    12,
                    true,
                    "Sheldon and Amy drive themselves crazy trying to figure out",
                    "/4kpMYDFKs4KGsR70UN1aPbz1tVa.jpg"))
            episodes.add(Episode(
                    null,
                    "The Procreation Calculation",
                    1538611200000,
                    "1418",
                    3,
                    12,
                    true,
                    "The Wolowitzes' life gets complicated when Stuart starts",
                    "/3wKloxLk5kSVA8yM0wYeIpoR3Pv.jpg"))
            return episodes
        }
    }
}