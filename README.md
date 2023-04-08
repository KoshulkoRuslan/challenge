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

Base screen states:

Loadind state

![image](https://user-images.githubusercontent.com/37122931/230742158-0b04beaf-6bb8-488e-bee8-1edcfe5e231a.png)

Error state

![image](https://user-images.githubusercontent.com/37122931/230742162-f3c6e95c-17f7-4afe-b703-d6fc8bf9894e.png)

Progress state

![image](https://user-images.githubusercontent.com/37122931/230742172-dd040c97-f129-406e-b34c-e9b774d414c9.png)

Finish state

![image](https://user-images.githubusercontent.com/37122931/230742181-c9740fa5-d893-4af5-81b3-79aa57cc617e.png)


