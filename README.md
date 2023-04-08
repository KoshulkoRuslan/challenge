### Babble challenge app
## Technologies
- Dependency inhection - **Dagger2**
- Network - **OkHttp**, **Retrofit**, **Gson**
- Presentation layer - **ViewBinding**, **ViewModel**, **LiveData**, **MotionLayout**
- Asynchronous work - **RxJava**

The simple diagram that explains the base entities and ScreenStates is provided by [Miro](https://miro.com/app/board/uXjVMUgmUP8=/?share_link_id=275323464741). 

The presentation pattern in the application has been constructed with Unidirectional data flow principles but isn't wholly true MVI. 

The project separates into 3 layers - data, domain, and presentation. The data layer contains network-specific entities, the domain contains base business logic that can be reused in other projects, and the presentation layer contains ViewModel, Actions, and States.

The branch [compose-version](https://github.com/KoshulkoRuslan/babble_challenge/tree/compose-vesion) contains implementation with Jetpack Compose.

