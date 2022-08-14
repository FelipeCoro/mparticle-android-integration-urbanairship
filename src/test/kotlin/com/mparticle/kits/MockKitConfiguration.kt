package com.mparticle.kits

import android.util.SparseBooleanArray
import com.mparticle.internal.Logger
import org.json.JSONException
import org.json.JSONObject

/**
 * This class is necessary b/c SparseBooleanArray is not available while unit testing.
 */
class MockKitConfiguration : KitConfiguration() {
    @Throws(JSONException::class)
    override fun parseConfiguration(json: JSONObject): KitConfiguration {
        mTypeFilters = MockSparseBooleanArray()
        mNameFilters = MockSparseBooleanArray()
        mAttributeFilters = MockSparseBooleanArray()
        mScreenNameFilters = MockSparseBooleanArray()
        mScreenAttributeFilters = MockSparseBooleanArray()
        mUserIdentityFilters = MockSparseBooleanArray()
        mUserAttributeFilters = MockSparseBooleanArray()
        mCommerceAttributeFilters = MockSparseBooleanArray()
        mCommerceEntityFilters = MockSparseBooleanArray()
        return super.parseConfiguration(json)
    }

    override fun convertToSparseArray(json: JSONObject): SparseBooleanArray {
        val map: SparseBooleanArray = MockSparseBooleanArray()
        val iterator = json.keys()
        while (iterator.hasNext()) {
            try {
                val key = iterator.next().toString()
                map.put(key.toInt(), json.getInt(key) == 1)
            } catch (jse: JSONException) {
                Logger.error("Issue while parsing kit configuration: " + jse.message)
            }
        }
        return map
    }

    internal inner class MockSparseBooleanArray : SparseBooleanArray() {

        var map = HashMap<Int, Boolean>()

        override fun get(key: Int): Boolean = get(key, false)

        override fun get(key: Int, valueIfKeyNotFound: Boolean): Boolean {
            print("SparseArray getting: $key")
            return if (map.containsKey(key)){
                map[key]
                return true
            }
            else {
                valueIfKeyNotFound
            }
        }



        override fun put(key: Int, value: Boolean) {
            map[key] = value
        }

        override fun clear() = map.clear()


        override fun size(): Int = map.size


        override fun toString(): String = map.toString()

    }

    companion object {
        @Throws(JSONException::class)
        fun createKitConfiguration(json: JSONObject): KitConfiguration {
            return MockKitConfiguration().parseConfiguration(json)
        }

        @Throws(JSONException::class)
        fun createKitConfiguration(): KitConfiguration {
            val jsonObject = JSONObject()
            jsonObject.put("id", 42)
            return MockKitConfiguration().parseConfiguration(jsonObject)
        }
    }
}