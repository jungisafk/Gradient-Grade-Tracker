package com.example.gradientgradetracker.data.repository

import com.example.gradientgradetracker.data.model.User
import com.example.gradientgradetracker.ui.screens.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun signUp(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Create user in Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Failed to create user")

            // Create user document in Firestore
            val user = User(
                id = firebaseUser.uid,
                email = email
            )
            usersCollection.document(firebaseUser.uid).set(user).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Sign in with Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Failed to sign in")

            // Get user data from Firestore
            val userDoc = usersCollection.document(firebaseUser.uid).get().await()
            val user = userDoc.toObject(User::class.java) ?: throw Exception("User data not found")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        try {
            val firebaseUser = auth.currentUser ?: return@withContext null
            val userDoc = usersCollection.document(firebaseUser.uid).get().await()
            userDoc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun signUpWithStudentId(studentId: String, email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Create user in Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Failed to create user")

            // Create user document in Firestore
            val user = User(
                id = firebaseUser.uid,
                email = email,
            )
            usersCollection.document(firebaseUser.uid).set(user).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithStudentId(studentId: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Find user by studentId in Firestore
            val querySnapshot = usersCollection.whereEqualTo("studentId", studentId).get().await()
            if (querySnapshot.isEmpty) throw Exception("No user found with this Student ID")
            val userDoc = querySnapshot.documents[0]
            val user = userDoc.toObject(User::class.java) ?: throw Exception("User data not found")
            val email = user.email

            // Sign in with Firebase Auth using the found email
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Failed to sign in")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 