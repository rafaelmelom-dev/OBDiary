package br.ufu.OBDiary.core.datasource

import androidx.compose.ui.graphics.Color
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Embedded
import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromColor(color: Color): Long = color.value.toLong()

    @TypeConverter
    fun toColor(value: Long): Color = Color(value.toULong())
}

@Database(entities = [
    VehicleEntity::class,
    RefuelingEntity::class,
    RepairEntity::class,
], version = 1)
@TypeConverters(Converters::class)
abstract class OBDiaryDatabase : RoomDatabase() {
    abstract fun vehicleDao() : VehicleDao
    abstract fun refuelingDao() : RefuelingDao
    abstract fun repairDao() : RepairDao
}

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicle")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Transaction
    @Query("SELECT * FROM vehicle")
    fun getAllVehiclesWithRefuelsAndRepairs(): Flow<List<VehicleWithRefuelsAndRepairs>>

    @Insert
    suspend fun insert(vehicle: VehicleEntity)

    @Update
    suspend fun update(vehicle: VehicleEntity)

    @Query("DELETE FROM vehicle WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Serializable
@Entity(tableName = "vehicle")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val model: String,
    val plate: String,
    val year: Int,
    val color: Color
)

data class VehicleWithRefuelsAndRepairs(
    @Embedded val vehicle: VehicleEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id_vehicle",
        entity = RefuelingEntity::class
    )
    val refuels: List<RefuelingEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id_vehicle",
        entity = RepairEntity::class
    )
    val repairs: List<RepairEntity>
)

@Dao
interface RefuelingDao {
    @Query("SELECT * FROM refuel")
    fun getAllRefuels(): Flow<List<RefuelingEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(refuel: RefuelingEntity)

    @Update
    suspend fun update(refuel: RefuelingEntity)

    @Query("DELETE FROM refuel WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Serializable
@Entity(
    tableName = "refuel",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_vehicle"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RefuelingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val id_vehicle: Int,
    val date: Date,
    val hodometer: Int,
    val liters: Double,
    val value_by_liter: Double,
    val fuel_type: String,
    val gas_station: String
)

@Dao
interface RepairDao {
    @Query("SELECT * FROM repair")
    fun getAllRepairs(): Flow<List<RepairEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(repair: RepairEntity)

    @Update
    suspend fun update(repair: RepairEntity)

    @Query("DELETE FROM repair WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Serializable
@Entity(tableName = "repair", foreignKeys = [
    ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["id_vehicle"],
        onDelete = ForeignKey.CASCADE
    )
])
data class RepairEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val id_vehicle: Int,
    val date: Date,
    val type: String,
    val description: String,
    val category: String,
    val workshop: String,
    val value: Double
)

data class Consumption(
    val id_back: Int,
    val id_front: Int,
    val consumption: Int
)
