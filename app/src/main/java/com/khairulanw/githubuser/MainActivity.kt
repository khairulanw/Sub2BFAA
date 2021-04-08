package com.khairulanw.githubuser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.khairulanw.githubuser.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpClient.log
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import kotlin.Exception

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    private var list = ArrayList<User>()
    val listUserAdapter = UserAdapter(list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Github User's Search"

        binding.rvUser.setHasFixedSize(true)

        getData()
        searchUser()
        showRecyclerList()
    }

    private fun getData(){
        binding.progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users"
        client.addHeader("Authorization","token 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")
        client.addHeader("User-Agent","request")
        client.get(url,object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE
                val listUser = ArrayList<User>()
                val result = String(responseBody)
                log.d(TAG,result)
                try {
                    val items = JSONArray(result)
                    for (i in 0 until items.length()){
                        val item = items.getJSONObject(i)
                        val username = item.getString("login")
                        val avatar = item.getString("avatar_url")
                        val user = User()
                        user.username = username
                        user.avatar = avatar
                        listUser.add(user)

                    }
                    listUserAdapter.setData(listUser)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal
                binding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchUser() { binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            if (query.isEmpty()) {
                return true
            } else {
                list.clear()
                getDataUserFromApi(query)
            }
            return true
        }

        override fun onQueryTextChange(newText: String): Boolean {
            return false
        }
    })
    }

    private fun getDataUserFromApi(username: String) {
        binding.progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/search/users?q=$username"
        client.addHeader("Authorization", "token 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")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE
                val listUser = ArrayList<User>()
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val items = responseObject.getJSONArray("items")
                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        val username: String = item.getString("login")
                        val avatar = item.getString("avatar_url")
                        val user = User()
                        user.username = username
                        user.avatar = avatar

                        listUser.add(user)

                    }
                    listUserAdapter.setData(listUser)
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal
                binding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getDataUserDetail(username: String) {
        binding.progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$username"
        client.addHeader("Authorization", "token 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")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE
                val listUser = ArrayList<User>()
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val item = JSONObject(result)
                    val username: String? = item.getString("login")
                    val avatar: String? = item.getString("avatar_url")
                    val name: String? = item.getString("name")
                    val company: String? = item.getString("company")
                    val location: String? = item.getString("location")
                    val repository: String? = item.getString("public_repos")
                    val followers: String? = item.getString("followers")
                    val following: String? = item.getString("following")

                    val user = User()
                    user.username = username
                    user.avatar = avatar
                    user.name = name
                    user.company = company
                    user.location = location
                    user.repository = repository
                    user.followers = followers
                    user.following = following
                    listUser.add(user)


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal
                binding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showRecyclerList() {
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback{
            override fun onItemClicked(user: User) {
                val intent = Intent(this@MainActivity,DetailActivity::class.java)
                intent.putExtra(DetailActivity.DETAIL_USER,user)
                startActivity(intent)
            }
        })
    }
}