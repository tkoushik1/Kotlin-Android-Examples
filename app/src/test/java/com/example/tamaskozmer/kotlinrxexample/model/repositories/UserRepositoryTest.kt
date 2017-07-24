package com.example.tamaskozmer.kotlinrxexample.model.repositories

import com.example.tamaskozmer.kotlinrxexample.model.entities.User
import com.example.tamaskozmer.kotlinrxexample.model.entities.UserListModel
import com.example.tamaskozmer.kotlinrxexample.model.persistence.daos.UserDao
import com.example.tamaskozmer.kotlinrxexample.model.services.UserService
import com.example.tamaskozmer.kotlinrxexample.util.ConnectionHelper
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Response

/**
 * Created by Tamas_Kozmer on 7/24/2017.
 */
// TODO More tests
class UserRepositoryTest {

    @Mock
    lateinit var userService: UserService

    @Mock
    lateinit var userDao: UserDao

    @Mock
    lateinit var connectionHelper: ConnectionHelper

    @Mock
    lateinit var mockUserCall: Call<UserListModel>

    @Mock
    lateinit var userResponse: Response<UserListModel>

    lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        userRepository = UserRepository(userService, userDao, connectionHelper)
    }

    @Test
    fun testGetUsers_isOnlineReceivedEmptyList_emitEmptyList() {
        // Given
        val userListModel = UserListModel(emptyList())

        // When
        setUpMocks(userListModel, true)
        val testObserver = userRepository.getUsers().test()

        // Then
        testObserver.assertNoErrors()
        testObserver.assertValue { userListModelResult: UserListModel -> userListModelResult.items.isEmpty() }
        verify(userDao).insertAll(userListModel.items)
    }

    private fun setUpMocks(modelFromUserService: UserListModel, isOnline: Boolean, userListFromDb: List<User> = emptyList()) {
        `when`(connectionHelper.isOnline()).thenReturn(isOnline)
        `when`(userService.getUsers()).thenReturn(mockUserCall)
        `when`(mockUserCall.execute()).thenReturn(userResponse)
        `when`(userResponse.body()).thenReturn(modelFromUserService)
        `when`(userDao.getAllUsers()).thenReturn(userListFromDb)
    }
}