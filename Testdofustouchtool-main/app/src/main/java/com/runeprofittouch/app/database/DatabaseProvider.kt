package com.runeprofittouch.app.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    private val migration4To5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS item_analyses (
                    itemId INTEGER NOT NULL,
                    manualCraftCost INTEGER NOT NULL,
                    baseRuneValue INTEGER NOT NULL,
                    crushingCoefficientPercent REAL NOT NULL,
                    updatedAt INTEGER NOT NULL,
                    PRIMARY KEY(itemId)
                )
                """.trimIndent()
            )
        }
    }

    private val migration5To6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS item_stats (
                    itemId INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    minimum INTEGER NOT NULL,
                    maximum INTEGER NOT NULL,
                    PRIMARY KEY(itemId, name)
                )
                """.trimIndent()
            )
        }
    }

    private val migration6To7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE item_analyses_new (
                    itemId INTEGER NOT NULL,
                    server TEXT NOT NULL,
                    manualCraftCost INTEGER NOT NULL,
                    baseRuneValue INTEGER NOT NULL,
                    crushingCoefficientPercent REAL NOT NULL,
                    updatedAt INTEGER NOT NULL,
                    PRIMARY KEY(itemId, server)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO item_analyses_new (
                    itemId, server, manualCraftCost, baseRuneValue,
                    crushingCoefficientPercent, updatedAt
                )
                SELECT
                    itemId, 'Tiliwan', manualCraftCost, baseRuneValue,
                    crushingCoefficientPercent, updatedAt
                FROM item_analyses
                """.trimIndent()
            )
            db.execSQL("DROP TABLE item_analyses")
            db.execSQL("ALTER TABLE item_analyses_new RENAME TO item_analyses")
        }
    }

    private val migration7To8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS favorites (
                    itemId INTEGER NOT NULL,
                    PRIMARY KEY(itemId)
                )
                """.trimIndent()
            )
        }
    }

    private val migration8To9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE items ADD COLUMN imageUrl TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    private val migration9To10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE resources ADD COLUMN imageUrl TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    @Volatile
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "runeprofit_touch.db"
            )
                .addMigrations(
                    migration4To5,
                    migration5To6,
                    migration6To7,
                    migration7To8,
                    migration8To9,
                    migration9To10
                )
                .fallbackToDestructiveMigration(true)
                .build()

            instance = newInstance
            newInstance
        }
    }
}
