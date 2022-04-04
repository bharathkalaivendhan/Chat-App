package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_all_users.*
import kotlinx.android.synthetic.main.activity_user.view.*

class AllUsersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_users)

        fetchusers()
    }

    companion object{
        val USER_KEY="USER_KEY"
    }
    private fun fetchusers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        val uid = FirebaseAuth.getInstance().uid

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupieAdapter()
              snapshot.children.forEach {

                  Log.d("AllUserActivity",it.toString())
                  val user = it.getValue(User::class.java)

                  if(user != null){
                      if(user.uid!=uid) {

                          Log.d("AllUserActivity", user.username)
                          adapter.add(UserItem(user))
                      }
                  }
              }
                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem
                    val intent = Intent(view.context,ChatActivity::class.java)

                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)

                    finish()
                }
                rvAllUsers.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}


class UserItem(val user : User) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
     viewHolder.itemView.tvUserUsername.text = user.username

     Picasso.get().load(user.profilephotouri).into(viewHolder.itemView.ivUserProfile)
    }

    override fun getLayout(): Int {
        return R.layout.activity_user
    }

}