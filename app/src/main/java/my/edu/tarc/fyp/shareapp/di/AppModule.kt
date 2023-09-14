package my.edu.tarc.fyp.shareapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import my.edu.tarc.fyp.shareapp.data.AuthRepository
import my.edu.tarc.fyp.shareapp.data.AuthRepositoryImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()


    @Provides
    @Singleton
    fun providesRepositoryImpl(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore): AuthRepository{
        return AuthRepositoryImpl(firebaseAuth, firestore)
    }
}