package com.example.footballcompsuserv2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.footballcompsuserv2.data.local.dao.LeagueDao
import com.example.footballcompsuserv2.data.local.dao.MatchDao
import com.example.footballcompsuserv2.data.local.dao.PlayerDao
import com.example.footballcompsuserv2.data.local.dao.TeamDao
import com.example.footballcompsuserv2.data.local.dao.UserDao
import com.example.footballcompsuserv2.data.local.entities.LeagueEntity
import com.example.footballcompsuserv2.data.local.entities.MatchEntity
import com.example.footballcompsuserv2.data.local.entities.PlayerEntity
import com.example.footballcompsuserv2.data.local.entities.TeamEntity
import com.example.footballcompsuserv2.data.local.entities.UserEntity

//BASE DE DATOS EN LOCAL
@Database(entities = [LeagueEntity::class,
                      TeamEntity::class,
                      PlayerEntity::class,
                      MatchEntity::class,
                      UserEntity::class],
                      version = 1)
abstract class FootballCompsDataBase: RoomDatabase() {

    abstract fun leagueDao(): LeagueDao
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun userDao(): UserDao

    companion object{
        @Volatile
        private var _INSTANCE: FootballCompsDataBase? = null

        fun getInstance(context: Context): FootballCompsDataBase{
            return _INSTANCE ?: synchronized(this){
                _INSTANCE ?: buildDatabase(context).also{
                    database -> _INSTANCE = database
                }
            }
        }

        //CREAR LA BASE DE DATOS
        private fun buildDatabase(context: Context): FootballCompsDataBase{
            return  Room.databaseBuilder(
                    context.applicationContext,
                    FootballCompsDataBase::class.java,
                    "footballcomps_db"
                    ).build()
        }
    }
}