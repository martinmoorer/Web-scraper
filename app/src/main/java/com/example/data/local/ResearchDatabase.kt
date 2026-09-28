package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TopicEntity::class,
        ScrapedPageEntity::class,
        ClassifiedItemEntity::class,
        ThesisSegmentEntity::class,
        MindMapNodeEntity::class,
        ScraperLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ResearchDatabase : RoomDatabase() {
    abstract fun researchDao(): ResearchDao

    companion object {
        @Volatile
        private var INSTANCE: ResearchDatabase? = null

        fun getInstance(context: Context): ResearchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ResearchDatabase::class.java,
                    "deep_research.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
