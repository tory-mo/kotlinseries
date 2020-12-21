package by.torymo.kotlinseries

import by.torymo.kotlinseries.data.db.Episode
import by.torymo.kotlinseries.data.db.Season
import by.torymo.kotlinseries.data.db.Series

class SeriesTestDataProvider {
    companion object {
        var seriesList = initSeriesList()
        var seasonList = initSeasonsList()
        var episodesList = initEpisodesList()

        private fun initSeriesList(): MutableList<Series> {
            val series = mutableListOf<Series>()
            series.add(Series(
                    1412,
                    "Arrow",
                    "Arrow",
                    "Still in prison, Oliver faces his biggest challenge yet",
                    1286668800000,
                    1286668800000,
                    1286668800000,
                    30,
                    "/mo0FP1GxOFZT4UDde7RFDz5APXF.jpg",
                    "en",
                    "/mo0FP1GxOFZT4UDde7RFDz5APXF.jpg",
                    118.797,
                    1.1,
                    1,
                    "Crime",
                    "http://www.cwtv.com/shows/arrow",
                    true,

                    "The CW",
                    7,
                    "Returning Series",
                    "TV-MA"
                    ))
            return series
        }

        private fun initSeasonsList(): MutableList<Season> {
            val series = mutableListOf<Season>()
            series.add(Season(
                    105512,
                    1412,
                    "Сезон 7",
                    1539561600000,
                    7,
                    22,
                    "",
                    "/ggkzHq0CBRcCwY5NL2MLaUzGXGW.jpg",
                    true
            ))
            return series
        }

        private fun initEpisodesList(): MutableList<Episode> {
            val episodes = mutableListOf<Episode>()
            episodes.add(Episode(
                    null,
                    "Inmate 4587",
                    1539561600000,
                    105512,
                    1412,
                    1,
                    false,
                    "Following Oliver’s shocking decision to turn himself over to the FBI and reveal his identity as the Green Arrow to the public",
                    "/wGjYh1D0lrXRsKPB0Oxno76HTa2.jpg"))
            episodes.add(Episode(
                    null,
                    "The Longbow Hunters",
                    1540166400000,
                    105512,
                    1412,
                    2,
                    false,
                    "In order to track down Diaz from inside prison, Oliver realizes that will require aligning with an old enemy",
                    "/2Ad5UlW30XE2rkABMtQIM4Uzu0C.jpg"))
            episodes.add(Episode(
                    null,
                    "Crossing Lines",
                    1540771200000,
                    105512,
                    1412,
                    3,
                    false,
                    "Still in prison, Oliver faces his biggest challenge yet",
                    "/2Ad5UlW30XE2rkABMtQIM4Uzu0C.jpg"))
            return episodes
        }
    }
}