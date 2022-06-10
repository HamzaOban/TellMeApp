package com.dogukan.tellme.view
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.node.getOrAddAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.dogukan.tellme.R
import com.dogukan.tellme.adapter.ChatLogRVAdapter
import com.dogukan.tellme.databinding.FragmentChatLogBinding
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.NotificationData
import com.dogukan.tellme.models.PushNotification
import com.dogukan.tellme.service.RetrofitObject
import com.dogukan.tellme.util.Addition
import com.dogukan.tellme.util.AppUtil
import com.dogukan.tellme.viewmodel.ChatViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_chat_log.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.collections.ArrayList


class ChatLogFragment : Fragment() , ChatLogRVAdapter.RecyclerDetails {
    private lateinit var binding : FragmentChatLogBinding
    private var  mainActivityView : MainActivity = MainActivity()
    private lateinit var adapter : ChatLogRVAdapter
    private var chatMessageList = ArrayList<ChatMessage>()
    private lateinit var ToID : String
    private var position : Int = 0
    private lateinit var myID : String
    private lateinit var myName : String
    private lateinit var hisImage : String
    private lateinit var ActiveState : String
    private lateinit var status : String
    private lateinit var email : String
    private lateinit var token : String
    private  var AppUtil = AppUtil()
    private  var addition = Addition()
    var selectedPhotoUri : Uri?=null
    private var isDeleted = false


    private val viewModel : ChatViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatLogBinding.inflate(inflater)
        val activity = activity as? MainActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.hide()
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        val activity = activity as? MainActivity
        activity?.supportActionBar?.title = myName
    }
    private fun init(){
        binding.recyclerView2.layoutManager = LinearLayoutManager(context)

        smoothRecyclerViewPosition()
        sendTextViewChanged()

        chatMessageList.clear()
        adapter = ChatLogRVAdapter(chatMessageList,this)

        //Gelen argumentler
        arguments?.let {
            ToID = ChatLogFragmentArgs.fromBundle(it).toID
            hisImage = ChatLogFragmentArgs.fromBundle(it).imageURL
            myName = ChatLogFragmentArgs.fromBundle(it).username
            position = ChatLogFragmentArgs.fromBundle(it).position
            status =ChatLogFragmentArgs.fromBundle(it).status
            ActiveState = ChatLogFragmentArgs.fromBundle(it).activeState
            email = ChatLogFragmentArgs.fromBundle(it).email
            token = ChatLogFragmentArgs.fromBundle(it).token
            ToID.let { it1 -> viewModel.getMessageFirebaseAll(it1) }
            sendMessageClick()
            topBarClick()
            viewModel.getActiveStateFirebase(ToID)
            viewModel.getActiveState()
            viewModel.getIsTyping(ToID)
            viewModel.getIstypingData()
        }
        myID = AppUtil.getUID()!!
        viewModel.getAllMessage()
        viewModel.checkIsSeenMessage(ToID)
        binding.recyclerView2.adapter = adapter


        backArrowClicked()
        cameraImageViewClick()

        observeLiveData()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                arguments?.let {
                    val usernamee = ChatLogFragmentArgs.fromBundle(it).username
                    val toIDD = ChatLogFragmentArgs.fromBundle(it).toID
                    val imageUrl = ChatLogFragmentArgs.fromBundle(it).imageURL
                    val position = ChatLogFragmentArgs.fromBundle(it).position
                    val action = ChatLogFragmentDirections.actionChatLogFragmentToLatestMessagesFragment2(
                        position,
                        toIDD,
                        usernamee,
                        imageUrl)
                    this.view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun smoothRecyclerViewPosition(){
        //Klavye açıldığında recyclerview'in yumuşak geçişli pozisyonunun değişmesi

        binding.recyclerView2.addOnLayoutChangeListener(View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom && binding.recyclerView2.adapter?.itemCount !=0 ) {
                binding.recyclerView2.postDelayed(Runnable {
                    binding.recyclerView2.smoothScrollToPosition(binding.recyclerView2.adapter?.itemCount!! - 1) }, 10)
            }
        })
    }
    private fun sendTextViewChanged(){
        //Send text view değiştiğinde olacak şeyler
        binding.sendmassegeTV.doOnTextChanged { text, _, _, count ->
            if (text.toString().trim().isEmpty() && count==0){
                viewModel.setIsTyping("no")
            }else{
                viewModel.setIsTyping("yes")

            }
        }
    }
    private fun sendMessageClick(){
        //mesajı gönderme olayı
        binding.sendmassageBtn.setOnClickListener {
            if (binding.sendmassegeTV.text.toString() == ""){
                return@setOnClickListener
            }
            viewModel.performSendMessage(binding.sendmassegeTV.text.toString(), ToID,binding)
            //Push notification
            val data = NotificationData(myName,binding.sendmassegeTV.text.toString())
            val notification = PushNotification(data,token)
            sendNotificationMessage(notification)

        }
    }
    private fun backArrowClicked(){
        binding.include.backArrow.setOnClickListener{
            val action = ChatLogFragmentDirections.actionChatLogFragmentToLatestMessagesFragment2()
            Navigation.findNavController(it).navigate(action)
        }
    }
    private fun topBarClick(){
        //FriendDetailFragment'ına geçiş
        include.setOnClickListener {
            arguments?.let {bundle->
                ToID = ChatLogFragmentArgs.fromBundle(bundle).toID
                hisImage = ChatLogFragmentArgs.fromBundle(bundle).imageURL
                myName = ChatLogFragmentArgs.fromBundle(bundle).username
                position = ChatLogFragmentArgs.fromBundle(bundle).position
                status = ChatLogFragmentArgs.fromBundle(bundle).status
                ActiveState = ChatLogFragmentArgs.fromBundle(bundle).activeState
                email = ChatLogFragmentArgs.fromBundle(bundle).email
                token = ChatLogFragmentArgs.fromBundle(bundle).token
                val action = ChatLogFragmentDirections.actionChatLogFragmentToFriendDetailFragment(
                    position,ToID,myName,hisImage,status,ActiveState,email)
                Navigation.findNavController(it).navigate(action)
            }

        }
    }
    private fun cameraImageViewClick(){
        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                selectedPhotoUri = it
                mainActivityView = (activity as MainActivity)
                val action = ChatLogFragmentDirections.actionChatLogFragmentToReViewSendImageFragment(
                    ToID,selectedPhotoUri.toString(),position,myName,status,ActiveState,email,token)
                view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }

            }
        )

        binding.cameraImageView.setOnClickListener {
            getImage.launch("image/*")
        }
    }
    override fun onStop() {
        super.onStop()
        viewModel.setIsTyping("no")
        mainActivityView.activeState("offline")
    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel.setIsTyping("no")
        mainActivityView.activeState("offline")
    }
    override fun onResume() {
        super.onResume()
        mainActivityView.activeState("online")
    }
    private fun observeLiveData(){

        viewModel.message.observe(viewLifecycleOwner, Observer { list ->

            adapter.ChatMessageUpdate(list)

            binding.sendmassageBtn.isEnabled = binding.sendmassegeTV.text != null
            binding.recyclerView2.visibility = View.VISIBLE
            binding.recyclerView2.scrollToPosition(chatMessageList.count()-1)
            viewModel.deleteMessage.observe(viewLifecycleOwner, Observer {

            })

        })

        viewModel.imageUpload.observe(viewLifecycleOwner, Observer {

            viewModel.getAllMessage()
        })
        viewModel.activeState.observe(viewLifecycleOwner, Observer {it2->
            viewModel.getActiveStateFirebase(ToID)
            viewModel.getActiveState()

            if (it2 && viewModel.getIstypingData().value==false){
                binding.include.imageActiveState.setImageResource(R.drawable.greencircle)
                addition.picassoUseIt(hisImage,binding.include.profileImage)
                binding.include.username.text = myName
                binding.include.activeState.setText(R.string.online)

            }
            else if(!it2 && viewModel.getIstypingData().value==false){
                binding.include.activeState.setText(R.string.offline)
                binding.include.imageActiveState.setImageResource(R.drawable.redcircle)
                addition.picassoUseIt(hisImage,binding.include.profileImage)
                binding.include.username.text = myName
            }
        })

        viewModel.isTyping.observe(viewLifecycleOwner, Observer {
            viewModel.getIsTyping(ToID)
            viewModel.getIstypingData()
            if (it){
                binding.include.activeState.setText(R.string.typing)
            }
        })
    }
    private fun sendNotificationMessage(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val request = RetrofitObject.api.postNotifitication(notification)
            if (request.isSuccessful){
                //Log.d("request",Gson().toJson(request))
            }else{
                //Log.d("request", request.errorBody().toString())
            }
        }catch (e : Exception){

        }
    }
    override fun onClickDeleteImageViewReciever(holder: ChatLogRVAdapter.RecieverViewHolder) {
        if (isDeleted)
            holder.itemView.visibility=View.GONE
    }
    override fun onClickDeleteImageViewSender(holder: ChatLogRVAdapter.SenderViewHolder) {
        viewModel.getAllMessage()
        viewModel.deleteMessage(ToID,"message deleted",chatMessageList,holder.position)


        holder.itemView.visibility=View.GONE
        holder.senderTrashMessage.visibility = View.GONE


    }
    override fun showIsSeenSender(holder: ChatLogRVAdapter.SenderViewHolder,chatMessage : ArrayList<ChatMessage>,p: Int) {
        viewModel.isSeen.observe(viewLifecycleOwner, Observer {
            if (it){
                if(chatMessage.isNotEmpty() && chatMessage.size!=0){
                }
            }

        })
    }
    override fun showIsSeenReciever(holder: ChatLogRVAdapter.RecieverViewHolder,chatMessage : ArrayList<ChatMessage>,p: Int) {
        viewModel.isSeen.observe(viewLifecycleOwner, Observer {
            if (it){
                if(chatMessage.isNotEmpty() && chatMessage.size!=0){
                }
            }
        })
    }
}