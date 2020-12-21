package by.torymo.kotlinseries.data

import androidx.paging.PagingSource
import by.torymo.kotlinseries.data.db.Series
import by.torymo.kotlinseries.data.network.Requester
import by.torymo.kotlinseries.data.network.SeriesDetailsResponse
import retrofit2.HttpException
import java.io.IOException

class SearchPagingSource(private val query: String, private val requester: Requester): PagingSource<Int, Series>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Series> {
        val position = params.key ?: 1
        return try {
            val response = requester.search(query, position)
            val series = response.results
            LoadResult.Page(
                    data = SeriesDetailsResponse.toSeries(series),
                    prevKey = if(position == 1) null else position - 1,
                    nextKey = if(position == response.total_pages) null else position + 1
            )
        }catch (exception: IOException){
            LoadResult.Error(exception)
        }catch (exception: HttpException){
            LoadResult.Error(exception)
        }
    }
}