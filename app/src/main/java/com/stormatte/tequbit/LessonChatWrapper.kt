package com.stormatte.tequbit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE),
        )
)

fun get_prompt(preferences: UserPreferences): String{

    val student_prompt = """
        You are TeQubit, A Smart Computer Science Instructor and Assistant at MatteHarmony Institute. 
    """.trimIndent()

    val entry_level_prompt = """
        You are TeQubit, A Smart Manager at MatteHarmony Industries Pvt Ltd, primarily working in computer science division. 
    """.trimIndent()
    val professional_prompt = """
        You are TeQubit, A Smart Director at MatteHarmony Industries Pvt Ltd, primarily working in computer science division. 
    """.trimIndent()

    val self_learning_prompt = """
        You are TeQubit, A Smart Youtuber who teaches computer science concepts to enthusiasts
    """.trimIndent()

    val user_prompt = when(preferences.knowledge){
        "student" -> student_prompt
        "Entry Level" -> entry_level_prompt
        "Professional" -> professional_prompt
        "Self Learning" -> self_learning_prompt
        else -> student_prompt
    }
    // TODO: You gotta modify add stuff. Choose your prompt as you wish
    val prompt = """
        $user_prompt
        You are known for your engaging methods of keeping your students entertained by teaching them interesting concepts in an inquisitive fashion.
        You are known to
        - give witty, yet easily understandable analogies to correlate concepts with
        - give complete explanations of a concept from a theoretical and applicative level 
        - Delve deep into math (or) programs, including throwing snippets of code / equations, whereever required
        - Use a lot of emojis
        - And you don't share your score with students

        Help these students with their queries.  

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

        Every other query asked by user should be considered as a doubt and responded with a brief explanation, answering their doubts. You should not respond with any of meta query responses, unless you are specifically asked with the respective meta query.
        For example, you should only respond if "[LESSON]" or "[QUIZ]" mentioned by user
    """.trimIndent()

    return prompt
}

class LessonChatWrapper(val chatID: String) {
    val messages: SnapshotStateList<MessageFormat> = mutableStateListOf()
    var acceptNewMessages: Boolean = true
    init {
        acceptNewMessages = false
        Firebase.database.getReference("lessons/$chatID").get().addOnSuccessListener {
            val data = it.getValue()
            if (data != null) {
                val server_messages = data as List<Map<String, String>>
                for(message in server_messages){
                    messages.add(MessageFormat(
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
                val data = it.getValue() as Map<String, *>?
                if (data != null) {
                    val preferences = UserPreferences(
                        knowledge = data["knowledge"] as String,
                        usage = data["usage"] as List<String>,
                        responseWay = data["responseWay"] as List<String>)
                    println(preferences.toString())
                    val prompt = get_prompt(preferences)
                    messages.add(MessageFormat(type="Meta", sender=SenderType.USER, message=prompt))
                    acceptNewMessages = true
                }
            }
        }


    }
    suspend fun askGemini() {
        if(messages.size == 0 || messages.last().sender == SenderType.AI || !acceptNewMessages){
            return
        }
//        if(messages.last().type == "Meta"){
//            return
//        }
        var prompt: List<Content> = listOf()
        for (message in messages){
            val part = TextPart(message.message)
            var role: String
            if(message.sender == SenderType.USER) {
                role = "user"
            }
            else{
                role = "model"
            }
            prompt = prompt.plus(Content(role = role, parts=listOf(part)))
        }
        coroutineScope {
//            GENERATIVE_MODEL.generateContentStream(*prompt.toTypedArray())
//                .collect {
//                    val response = it.candidates[0].content.parts[0].asTextOrNull()
//                    messages.add(MessageFormat(type="Input", sender=SenderType.USER, message=response.toString()))
//                    updateDatabase()
//                }
            val response = GENERATIVE_MODEL.generateContent(*prompt.toTypedArray()).candidates[0].content.parts[0].asTextOrNull()
            messages.add(MessageFormat(type="Input", sender=SenderType.AI, message=response.toString()))
            updateDatabase()
        }
    }

    fun updateDatabase(){
        Firebase.database.getReference("lessons/$chatID").setValue(this.messages)
    }
}


