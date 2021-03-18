package com.paulo.pokemon.persistence

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.squareup.moshi.Moshi
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
abstract class LocalDatabase {
  lateinit var db: AppDatabase

  @Before
  fun initDB() {
    val moshi = Moshi.Builder().build()
    db = Room.inMemoryDatabaseBuilder(getApplicationContext(), AppDatabase::class.java)
      .allowMainThreadQueries()
      .addTypeConverter(TypeResponseConverter(moshi))
      .build()
  }

  @After
  fun closeDB() {
    db.close()
  }
}
