# TaskMaster

## Lab 26

* In This Lab We Will To Create Three pages in android Studio IDE .
* First Page Is A home page that contains a text View and image , two buttons 
    1. first button is Add Task that will move us to the AddTask page
    2. second button is All Task that will move us to the AllTasl page
* Second Page is AddTask page that contain two edit text to allow to user add new task.
* Third Page is AllTask page that contain title for this page and image.

## Home Page 
![ScreenShots for home page](screenshots/homePage.png)

___

## Lab 27

* in this lab i will continuo work on TaskMaster Repo to do some features these features is :

    1. the user can be go to the setting page and enter his name thin click to save after this the user can see his name in the home page .
    2. in the home pabe we have spinner that contain all of the task . when the user click on one from this list will move to the taskDetails page that contains on the name for this task and description .
## Home Page  

![home page 2](screenshots/homePage2.png)

## Setting Page

![](screenshots/settingPage.png)

## TaskDetails Page

![](screenshots/taskDetails.png)

___

## Lab 28 

* in this lab i will to continuo work on Task Mater Tepo to add some features to the myApp :  
    1. the user can to see all task inside RecyclerView 
    2. When the user click on any item inside RecyclerView will move to the taskDetails page and will see the name and bode for this task 

## Home Page With RecyclerView for All Tasks

![Home Page](screenshots/homeRecyclerView.png)

## Task Details Page With The Title And Body For Every Task Inside The Home Page

![Task Details](screenshots/detailsPage.png)

___

## Lab 29

* in this lab i will to continuo work on Task Mater Tepo to add some features to the myApp :  
    1. the user can add the task add descreption then display it in the RecyclerView inside home page.
    2. when the user click on any item inside RecyclerView will move to the taskDetails page and will see the name and body, statu fro this task.
* in this Lab I use the Room library to store data in my local storage and retrive it from the Room database

## AddTask page

* in this page the user can add the task and description for it then click submit

![addTask Page](screenshots/AddTask_Lab29.png)

## Home Page With RecyclerView For All Tasks That The User Add It

* after the user add the task, it will add inside RecyclerView in Home Page.

![Home Page](screenshots/home_Lab29.png)

## Task Details Page

* When the user click on any item inside RecyclerView will move to another page to display the title and description, state for this item.

![taskDetails Page](screenshots/taskDetails_Lab29.png)

___

## Lab 31 

* in this lab i do some Test Function to my TaskMaster App with use Espresso Library 
* I add spinner widget to the addTask Page to allow for user to select the state for him task.

![AddTask Page](screenshots/AddTask_Lab31.png)

___
 
## Lab32 

* in this lab I'm connect my App with dynamodb
* i fitch all data inside dynamodb with API
* do sync between API and DataStore.
