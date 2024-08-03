package com.stormatte.tequbit

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject


val GENERATIVE_MODEL = GenerativeModel(
    apiKey = BuildConfig.apiKey,
    modelName = "gemini-1.5-pro",
    safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.ONLY_HIGH),
        )
)
val PROMPTS_KNOWLEDGE = mapOf(
    "Student" to "You are TeQubit, Your task is to help the students solve their queries in a way that is easy to understand by a student.\n" +
            "Consider that the user has minimal knowledge on what they ask, and are looking for a concise and great explanations. Keep them as simplistic as possible",
    "Entry Level" to "You are TeQubit, Your task is to help the users solve their queries related to programming.\n" +
            "Consider that user knows a very little on what they are working on.\n" +
            "Instead of repeating the basics,be more straight the point and solve their queries. It can be complex, yet try to rephrase them in a way that is easiest to understand",
    "Professional" to "You are TeQubit, Your task is to help the user solve their queries in programming.\n" +
            "Consider that the user has a good grasp of the topic but need a slight refresher, you are free to use complex terminologies",
    "Self Learning" to "You are TeQubit, Your task is to help the user solve their queries in programming.\n" +
            "Consider that the user is a self-learning individual that is used to learning from online sources. It can be complex, yet try to rephrase them in a way that is easiest to understand\n" +
            "Assume that the user has significant, yet not complete knowledge"
)

val PROMPTS_RESPONSE_WAY = mapOf(
    "Witty & Fun Sentences" to "- witty and humorous analogies\n" +
            "- a bunch of emojis",
    "Casual and Easy" to "- casual, easy and practical analogies",
    "Include examples from games and pop culture" to "- analogies from games and pop cultures",
    "Let TeQubit decide based on question" to "- analogies only when required, based on queries"
)

val META_QUERY = """
    Sometimes, you can be greeted with additional meta queries. These include
    -  <[QUIZ:<|>QUESTION: {question}]> query, which indicates that you need to quiz your students based on their previous chat.
        - Your quiz is defined by four parameters: (difficulty_level, number_of_questions, scope, type)
            - Difficulty level is on a score of 1 (being easiest) and 10 (being hardest)
            - Number of questions is in range of (5, 30)
            - Scope is one of (interview_based, exam_based)
            - Type can be one of (multi_choice_question, multi_select_question, textual_answer_type). Multi choice is a single true from multiple questions type. Multi select is a multiple true from multiple questions type and text based requires textual responses
        - You obtain these parameters by conversing with the user and then respond back to admin with the following response
            - <[QUIZ:<|>{difficulty_level}<|>{number_of_questions}<|>{scope}<|>{type}}]>
        - Following that, you display every question, once per response to a user, in the format of <[QUIZ_QUERY:<|>{question}]>
        - You collect their response, score it and share it to admin in the following format
            - <[QUIZ_SCORE:<|>{score}]>
        - At the end, you send a meta response back to admin in the format
            - <[QUIZ]>
    - <[LESSON]> query, where you carefully break down the concept asked by a student into four different parts
        - Your lesson is defined by a single textual parameter, which is the question asked by a user
        - You then respond back to admin with the following:
            - <[LESSON:<|>{lesson_title}<|>{lesson_info}]> where lesson info is a greeting to start the lesson.
            - <[INTRODUCTION:<|>{introduction_title}<|>{introduction_body}]> Which marks the introduction for a particular lesson
            - One or more of explanations (or) mathematical and programmatic implementations in the format: <[DESCRIPTION:<|>{description_title}<|>{description_body}}]> 
            - One or more of applications (or) pros and cons in the format <[APPLICATION:<|>{application_title}<|>{application_body}}]> 
            - <[SUMMARY:<|>{summary_title}<|>{summary_body}}]> Which marks the summary of the lesson
            - All this has to be sent in a single response.
    For every other query, your response should be of the format
    <[RESPONSE:<|>response_title}<|>{response_body}]>
    Note:
    - You should not respond with any of meta query responses, unless you are specifically asked with the respective meta query. 
    - All responses should follow given format, as they need to be parsed further by frontend
    - All titles should be max five words long, formal and should be appropriate to full chat, rather than the particular response
    
    Here is an example lesson response with appropriate format:
    <[LESSON:<|>I am a crazy title<|>Alright! Here is your lesson that describes the stuff. Just click the button to get started]>
    <[INTRODUCTION:<|>your title here<|>INTRODUCTION_BODY: loreum ipsum blah blah fifty to hundred word body here]>
    <[DESCRIPTION:<|>your description title here<|><|> blah blah very long description no word limit here]>
    <[DESCRIPTION:<|>your description title here<|><|> blah blah very long description you can use markdown blocks to highlight text]>
    <[APPLICATION:<|>your application title here<|><|> blah blah very long list of applications maybe]>
    <[SUMMARY:<|>Your summary title here<|>Short crisp and to the point summary of lesson here]>
    
    And here is an example general response
    <[RESPONSE:<|>Response Title here<|>Your body here]>
""".trimIndent()
fun get_prompt(preferences: UserPreferences): String{
    val knowledgePrompt = PROMPTS_KNOWLEDGE[preferences.knowledge]

    var responseWayPrompt = ""
    for(responseWay in preferences.responseWay){
        responseWayPrompt += PROMPTS_RESPONSE_WAY[responseWay] + "\n"
    }
    val prompt = "$knowledgePrompt\nYour analogies are known to include:\n$responseWayPrompt\n\nHelp them with their queries.\n\n$META_QUERY"
    return prompt
}

fun parseResponse(response: String): List<Map<String, Map<String, String>>>{
    println("RESPONSE: $response")
    var responseSplit = response.split("<[")
    println("RESPONSE " + responseSplit)
    var responseMap = mutableListOf<Map<String, Map<String, String>>>()
    for(split in responseSplit){
        if(!split.contains(":")){
            continue
        }
        var parsedSplit = split
        if(parsedSplit.contains("<[")) {
            parsedSplit = parsedSplit.replace("<[", "")
        }
        if(parsedSplit.contains("]>")) {
            parsedSplit = parsedSplit.replace("]>", "")
        }
        parsedSplit = parsedSplit.trimStart().trimEnd()

        val metaStrPos = parsedSplit.indexOf(":")
        val metaStr = parsedSplit.substring(0, metaStrPos)
        val responseStr = parsedSplit.substring(metaStrPos + 1).split("<|>")
        var split_response = mutableListOf<String>()
        for(line in responseStr){
            if(line == "")
                continue
            split_response.add(line.trimStart().trimEnd())
        }
        responseMap.add(mapOf(metaStr to mapOf(split_response[0] to split_response[1])))
    }
    println("RESPONSE MAP")
    responseMap.forEach{
        for (entry in it) {
            println(entry.key)
            println(entry.value.toString())
        }
    }
    return responseMap
}

class LessonChatWrapper : ViewModel() {

    private val _messages = mutableStateListOf<MessageFormat>()
    val messages : SnapshotStateList<MessageFormat> get()= _messages

    private  val _textField = mutableStateOf("")
    val textFieldVal : MutableState<String> = _textField

    private val _chatTitle = mutableStateOf("New Chat")
    val chatTitle : MutableState<String> = _chatTitle

    fun setChatTitle(title:String){
        _chatTitle.value = title
    }

    private var acceptNewMessages: Boolean = true
    private var isChatInitialized = false

    private var chatID:String = ""
    fun setChatID(id:String){
        if(chatID=="")
            chatID = id
        else{
            chatID = id
            resetWrapper()
            this.initializeChat()
        }
    }

    fun resetWrapper(){
        _messages.clear()
        _textField.value = ""
        _chatTitle.value = "New Chat"
        isChatInitialized = false
    }

    private suspend fun userPreferences(): Pair<UserPreferences, String>{
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val res = Firebase.database.getReference("users/$userId").get().await()
        val data = res.value as Map<String, *>?
        val preferences = UserPreferences(
            knowledge = data!!["knowledge"] as String,
            usage = data["usage"] as List<String>,
            responseWay = data["responseWay"] as List<String>)
        val prompt = get_prompt(preferences)
        return Pair(preferences, prompt)
    }

    fun initializeChat() {
        if (isChatInitialized) return
        isChatInitialized = true

        acceptNewMessages = false
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            var data = Firebase.database.getReference("lessons/$userId/$chatID").get().await().value
            if (data != null) {
                val serverMessages = data as List<Map<String, String>>
                for(message in serverMessages) {
                    var parsedMessage: List<Map<String, Map<String, String>>>? = null
                    if(message["type"] == "Input" && message["sender"] == "AI"){
                        parsedMessage = parseResponse(message["message"] as String)
                    }
                    _messages.add(
                        MessageFormat(
                            type = message["type"] as String,
                            sender = if (message["sender"] == "USER") SenderType.USER else SenderType.AI,
                            message = message["message"] as String,
                            parsedMessage = parsedMessage
                        )
                    )
                }
                val (preferences, prompt) = userPreferences()
                _messages[0].message = prompt
                acceptNewMessages = true
            }
            else {
                val (preferences, prompt) = userPreferences()
                _messages.add(MessageFormat(type="Meta", sender=SenderType.USER, message=prompt, null))
                acceptNewMessages = true
            }
        }
    }
    suspend fun askGemini() {
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
        viewModelScope.launch {
            try{
                val response = GENERATIVE_MODEL.generateContent(*prompt.toTypedArray()).candidates[0].content.parts[0].asTextOrNull()
                val message = response.toString().trimEnd()
                val responseMap = parseResponse(message)
                _messages.add(MessageFormat(type="Input", sender=SenderType.AI, message=message, parsedMessage=responseMap))
                updateDatabase()
            }catch (e: Exception){
                val error_message = "Looks like something went wrong! Please try again later"
                _messages.add(MessageFormat(type="Input", sender=SenderType.AI, message=error_message, parsedMessage=listOf(mapOf("ERROR" to mapOf("Error" to error_message)))))
                Log.e(BuildConfig.APPLICATION_ID, e.stackTraceToString())
            }
        }
    }

    private suspend fun updateDatabase(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = Firebase.database.getReference("lessons/$userId/$chatID")
        val numDbMessages = dbRef.get().await().childrenCount.toInt()
        for(i in numDbMessages until _messages.size){
            val message = _messages[i]
            dbRef.child(i.toString()).setValue(mapOf(
                "type" to message.type,
                "sender" to if(message.sender == SenderType.USER) "USER" else "AI",
                "message" to message.message
            ))
        }
    }
}


