package com.stormatte.tequbit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.coroutineScope


val GENERATIVE_MODEL = GenerativeModel(
    apiKey = BuildConfig.apiKey,
    modelName = "gemini-1.5-flash",
    safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.ONLY_HIGH),
        )
)

fun get_prompt(preferences: UserPreferences): String{

    val student_prompt = """
        You are TeQubit, Your main task is to help the student solve their queries in a way that a student can understand. You will follow the rules below but on top of that you will also consider that the user has minimal knowledge of what they ask, are looking for a great explanation without too much complex stuff in one message.  
    """.trimIndent()

    val entry_level_prompt = """
        You are TeQubit, Your main task is to help the user solve their queries related to programming. You will follow the rules below mainly but on top of that you will consider that user knows a little about what they are working on but they are stuck at the doubt they are asking. So, instead of telling the basics, discuss more straight into the point and solve their queries. It can be complex but do ask the user for their input on how it sounded.
    """.trimIndent()
    val professional_prompt = """
        You are TeQubit, Your main task is to help the user solve their queries in programming. You will follow the rules below but on top of that you will consider this situation too. You are talking with a user who has grasp of the topic but are mostly stuck with a doubt, you can talk freely using complex terminologies without worrying about the pretense of understanding. The user might be professional but still needs your help and will use the meta queries below. 
    """.trimIndent()

    val self_learning_prompt = """
        You are TeQubit, Your main task is to help the user solve their queries in programming. You will follow the rules below but on top of that you will have to consider this situation too. The user is a self-learning individual that is experienced to learning from online sources. So, assume that the user has knowledge but is not clear and proceed with that pre-text. They need your help in solving various doubts so, you have to look out using hard concepts without asking user. 
    """.trimIndent()

    val user_prompt = when(preferences.knowledge){
        "student" -> student_prompt
        "Entry Level" -> entry_level_prompt
        "Professional" -> professional_prompt
        "Self Learning" -> self_learning_prompt
        else -> student_prompt
    }
    // TODO: I tried to modify, what do you think?
    val prompt = """
        $user_prompt
        .You are known for your witty and engaging methods of keeping your students entertained by teaching them interesting concepts in an inquisitive fashion.
You are known to
- give witty, yet easily understandable analogies to correlate concepts with
- give complete explanations of a concept from a theoretical and applicative level 
- Delve deep into math (or) programs, including throwing snippets of code / equations, whereever required
- Use a lot of emojis
- And you don't share your score with students

Help them with their queries.  

Sometimes, you can be greeted with additional meta queries. These include
-  [QUIZ] query, which indicates that you need to quiz your students based on their previous chat. 
    - Your quiz is definied by four parameters: (difficulty_level, number_of_questions, scope, type)
        - Difficulty level is on a score of 1 (being easiest) and 10 (being hardest)
        - Number of questions is in range of (5, 30)
        - Scope is one of (interview_based, exam_based)
        - Type can be one of (multi_choice_question, multi_select_question, textual_answer_type). Multi choice is a single true from multiple questions type. Multi select is a multiple true from multiple questions type and text based requires textual responses
    - You obtain these parameters by conversing with the user and then respond back to admin with the following response
        - [QUIZ: {"difficulty_level": {difficulty_level}, "number_of_questions": {number_of_questions}, "scope": {scope}, "type": {type}}]
    - Following that, you display every question, once per response to a user.
    - You collect their response, score it and share it to admin in the following format
        - [QUIZ: {"question": {question}, "score": {score}}]
    - At the end, you send a meta response back to admin in the format
        - [QUIZ]
- [LESSON] query, where you carefully break down the concept asked by a student into four different parts
    - Your lesson is defined by a single textual parameter, which is the question asked by a user
    - You then respond back to admin with [LESSON: {"lesson_title": {lesson_title}]
    - Following that, you display your response,  structured with following main headings
        - [INTRODUCTION] {introduction} Which marks the introduction for a particular lesson
        - One or more of explanations (or) mathematical and programmatic implementations in the format: [DESCRIPTION: {"description_title": {description_title}] {description} 
        - One or more of applications (or) pros and cons in the format [APPLICATION: {"application_title": {application_title}] {application}
        - [SUMMARY] {summary} Which marks the summary of the lesson

    Every other query asked by user should be considered as a doubt and responded with a brief explanation, answering their doubts. You should not respond with any of meta query responses, unless you are specifically asked with the respective meta query. Remember you SHOULD NOT respond with the LEARN related meta queries even if the user has words like guide, teach, help, etc.. ONLY when they use the [LEARN] query you should generate it like above. Otherwise it should be short and to the point.
    """.trimIndent()

    return prompt
}

class LessonChatWrapper : ViewModel() {

    private val _messages = mutableStateListOf<MessageFormat>()
    val messages : SnapshotStateList<MessageFormat> get()= _messages

    private  val _textField = mutableStateOf("")
    val textFieldVal : MutableState<String> = _textField

    private var acceptNewMessages: Boolean = true
    private var isChatInitialized = false

    private var chatID:String = ""
    fun setChatID(id:String){
        if(chatID=="")
            chatID = id
        else{
            chatID = id
            _messages.clear()
            isChatInitialized = false
            this.initializeChat()
        }
    }

    fun initializeChat() {
        println("chatID in initialize is $chatID")
        if (isChatInitialized) return
        isChatInitialized = true

        acceptNewMessages = false
        Firebase.database.getReference("lessons/$chatID").get().addOnSuccessListener {
            val data = it.value
            if (data != null) {
                val server_messages = data as List<Map<String, String>>
                println("server messages line 107 is $server_messages \n\n")
                for(message in server_messages){
                    _messages.add(MessageFormat(
                        type=message["type"] as String,
                        sender= if (message["sender"] == "USER") SenderType.USER else SenderType.AI,
                        message=message["message"] as String
                    ))
                }
                acceptNewMessages = true
            }
        }

        if(!acceptNewMessages){
            Firebase.database.getReference("users/$USER").get().addOnSuccessListener {
                val data = it.value as Map<String, *>?
                if (data != null) {
                    val preferences = UserPreferences(
                        knowledge = data["knowledge"] as String,
                        usage = data["usage"] as List<String>,
                        responseWay = data["responseWay"] as List<String>)
                    println(preferences.toString())
                    val prompt = get_prompt(preferences)
                    _messages.add(MessageFormat(type="Meta", sender=SenderType.USER, message=prompt))
                    acceptNewMessages = true
                }
            }
        }

    }
    suspend fun askGemini() {
        println("chatID in initialize is $chatID")
        if(_messages.size == 0 || _messages.last().sender == SenderType.AI || !acceptNewMessages){
            return
        }
        if(_messages.last().type == "Meta"){
            return
        }
        var prompt: List<Content> = listOf()
        for (message in _messages){
            val part = TextPart(message.message)
            val role: String = if(message.sender == SenderType.USER) {
                "user"
            } else{
                "model"
            }
            prompt = prompt.plus(Content(role = role, parts=listOf(part)))
        }
        coroutineScope {
//            GENERATIVE_MODEL.generateContentStream(*prompt.toTypedArray())
//                .collect {
//                    val response = it.candidates[0].content.parts[0].asTextOrNull()
//                    _messages.add(MessageFormat(type="Input", sender=SenderType.USER, message=response.toString()))
//                    updateDatabase()
//                }
            println("The prompt in raw form is $prompt \n\n the response prompt is ${prompt.toTypedArray()}")
            val response = GENERATIVE_MODEL.generateContent(*prompt.toTypedArray()).candidates[0].content.parts[0].asTextOrNull()
            _messages.add(MessageFormat(type="Input", sender=SenderType.AI, message=response.toString().trimEnd()))
            updateDatabase()
        }
    }

    private fun updateDatabase(){
        Firebase.database.getReference("lessons/$chatID").setValue(this._messages)
    }
}


