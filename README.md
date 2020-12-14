# Student Mentoring

> An Android application built to record and monitor students' performance in a college. 

 
## Problem 

#### Context
* In most of the schools there is some sort of mentoring system that is implemented. 
* A mentor's responsibility is to offer advice, provide support and answer questions of his mentees.
* Each mentor can have a set of students' as his mentees every semester.  
* A mentor usually will have to interact with every one of his students and have to record their performance every semester. 
* This data lets future mentors understand the students' ability better and help them accordingly. 
  
#### Limitations 
* Initially this task is done on paper where each student has a file which moves from one mentor to another every semester. 
* This discourages data visualizing since the only thing a new mentor sees will be a bunch of numbers that are hard to make sense out of.
* Another problem in this system is to maintain privacy, confidentiality and transparency at the same time. 
  * A student must be able to view his performance and feedback, while this data must be hidden from other students.
  * Similarly, every mentor must be able to record and monitor the performance of only those students who are assigned to him. 
  * This is very difficult to maintain with a paper based system. 


## Addressing the problems with the app

#### _Roles and logins_
* This app has two different modes of Login
  * Student Login
  * Mentor Login
    * _Student Login and mentor login sample screens..._
* When a mentor is logged in 
  * He is welcomed with a Listview of all the students that are assigned to him. 
  * On selecting a student, 
    * the application navigates to the Student detail page, where mentor can 
      * see the student's profile
      * see previous mentors' feedback
      * Add his feedback
    * This can be done for all the students that are assigned to the mentor
* Similarly, when a student is logged in 
  * he can view his profile information and feedback given to him by his mentors.
* This addresses the problem of authorization and confidentiality since a mentor can only view/edit the data of his mentees and students' can view only their information when logged in. 

#### _Graphs and stars_
* When a mentor records students' performances in certain fields, 
  * instead of viewing and entering numbers to rate a student's performance the app chooses the following modes of input and output
    * <strong>Input - _Star view_ </strong>
      * _Image of star view..._
    * <strong> Output - _Graph View_</strong>
      * _Image of graph view..._
* This solves the problem of Data visualization. 
  


 ## Tech Stack

 The front-end of the application is an Android App while the backend(not included in the repo) was a XAMPP server with PHP and MySQL was used as the datbase. 

 ## Other Features

 * <strong>Offline usage</strong>
   * Whenever a user is logged in, all the data is fetched from the server and is persistent on the local device. 
   * Any new data that is added to the student's profile shall be temporarily stored on device and is updated to the server when online.  
 * <strong>Restrict mutliple logins</strong>
   * A mentor when logged in on one device, can not log-in on other devices. 

* <strong>Data at a glance</strong>
   * Data is represented in the form of a histogram 
   * Students/Mentors can view their progression over semesters in a single view.


## Future works
* A dashboard to 
  * appoint mentors and assign students to them.
  * Fetching (consolidated) data of past students when needed.
  * Generating reports on demand.
* Sending notifications to students whenever a new feedback is given. 
  
