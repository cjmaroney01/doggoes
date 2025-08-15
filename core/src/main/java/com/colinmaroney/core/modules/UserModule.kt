package com.colinmaroney.core.modules

import com.colinmaroney.core.managers.UserManager
import com.colinmaroney.core.managers.UserManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UserModule {
    @Binds
    abstract fun bindUserManager(
        userManagerImpl: UserManagerImpl
    ): UserManager
}