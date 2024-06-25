import java.util.*

import java.util.UUID

data class Anomaly(
    val id: Int?,
    val modelId: UUID,

    val timestamp: Long,

    val status: String,

    val predicted: Double,

    val minPredicted: Double,

    val maxPredicted: Double,

    val observed: Double

) : BaseDao.BaseDaoElement<Int> {
    override fun id(): Int {
        return id!!
    }
}