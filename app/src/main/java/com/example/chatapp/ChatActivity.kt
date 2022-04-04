package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat_from_row.view.*
import kotlinx.android.synthetic.main.activity_chat_to_row.view.*

class ChatActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        rvChat.adapter = adapter

        val enteredchat = findViewById<EditText>(R.id.etEnterChat)

        val user = intent.getParcelableExtra<User>(AllUsersActivity.USER_KEY)
        val usernamechat =findViewById<TextView>(R.id.tvUsernameChat)
        usernamechat.setText(user.username)

        listenForMessages()
        //setupDummyData()
        val send = findViewById<Button>(R.id.btSend)
        send.setOnClickListener {
            performSendMessage()
            enteredchat.setText(null)
        }
    }

    private fun performSendMessage()
    {
        val enteredchat = findViewById<EditText>(R.id.etEnterChat)
        val text = enteredchat.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(AllUsersActivity.USER_KEY)
        val toId = user.uid

        if (fromId == null) return

        val ref = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage = ChatMessage(ref.key!!,text,fromId,toId)
        ref.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d("ChatActivity","saved chat message: $text")
                }

    }

    private fun listenForMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        val user = intent.getParcelableExtra<User>(AllUsersActivity.USER_KEY)
        val toId = user.uid

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null)
                {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid)
                    {

                    adapter.add(ChatFromItem(chatMessage.text))
                    } else if (chatMessage.toId==user.uid)
                    {

                        adapter.add(ChatToItem(chatMessage.text))

                        snapshot.ref.removeValue()
                    }

                }
                //ref.removeValue()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
        })
    }

}

class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String)
{
    constructor() : this("","","","")
}

class ChatFromItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tvChatFromRow.text= text
    }

    override fun getLayout(): Int {
       return R.layout.activity_chat_from_row
    }
}

class ChatToItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tvChatToRow.text=text
    }

    override fun getLayout(): Int {
        return R.layout.activity_chat_to_row
    }
}