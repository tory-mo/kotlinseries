package by.torymo.kotlinseries.data

import androidx.paging.PagingSource
import by.torymo.kotlinseries.data.db.Series
import retrofit2.HttpException
import java.io.IOException

class FavouritePagingSource (private val dbRepository: SeriesDbRepository): PagingSource<Int, Series>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Series> {
        return try {
            val series = dbRepository.getSeriesByType()

            LoadResult.Page(
                    data = series,
                    prevKey = null,
                    nextKey = null
            )
        }catch (exception: IOException){
            LoadResult.Error(exception)
        }catch (exception: HttpException){
            LoadResult.Error(exception)
        }
    }
}