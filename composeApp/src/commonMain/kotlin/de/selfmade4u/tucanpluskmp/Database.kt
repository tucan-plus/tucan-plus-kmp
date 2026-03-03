package de.selfmade4u.tucanpluskmp

import androidx.room3.ConstructedBy
import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.TypeConverter
import androidx.room3.TypeConverters
import de.selfmade4u.tucanpluskmp.database.ModuleResultDao
import de.selfmade4u.tucanpluskmp.database.ModuleResultEntity
import de.selfmade4u.tucanpluskmp.database.ModuleResultsDao
import de.selfmade4u.tucanpluskmp.database.ModuleResultsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }
}

@Database(entities = [TodoEntity::class, ModuleResultsEntity::class, ModuleResultEntity::class], version = 2)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): TodoDao
    abstract fun getModuleResultsDao(): ModuleResultsDao
    abstract fun getModuleResultDao(): ModuleResultDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(item: TodoEntity)

    @Query("SELECT count(*) FROM TodoEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM TodoEntity")
    fun getAllAsFlow(): Flow<List<TodoEntity>>
}

@Entity
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String
)