# cs0320 Term Project 2021

**Team Members:** _Fill this in!_

**Team Strengths and Weaknesses:**


Sidarth Raman:
* Strengths: Object-Oriented Programming, Data Structures & Algorithms
* Weaknesses: No React experience


Mit Ramesh
* Strengths: Backend programming, Testing, Leadership 
* Weaknesses: Front End, GUI Design, Understanding complex documentation


Henry Sowerby:
* Strengths: Java, OOP, Databases, Python, working incrementally, considering big-picture design before diving right into things
* Weaknesses: Front end design, JavaScript, API experience


Daniel Segel: 
* Strengths: Java, Object-Oriented Design, and data management 
* Weaknesses: New to Front-End HTML, CSS, and Javascript


### Idea 1: TripFinder


* Summary
   * The TripFinder application aims to create a curated trip-finding platform for users, based on various information that the user puts in. By trip-finding, the platform will design an itinerary (including flights, hotels, trips and a full cost for this) for the user to book. Depending on the ease, we may try to generate multiple itineraries (3) that the user chooses based on input. 
* Problem: 
   * The problem that this solves is the logistics of Trip Funding and planning. While there are sites to do individual things (book trips, hotels etc), they require the user knowing what they want to do. It would be really cool, I believe, to have an algorithm pick your destination based on a set of user preferences, 
* Core Features:
   * GUI/Front End:
      * A core feature of this platform would be the platform itself to allow a user to create an account as well as pre-design/load data to select an itinerary. The GUI shouldn’t be too complicated, and will mostly involve forms/creative ways to supply user information as well as a creative way to display an itinerary. 
   * Collecting/Saving User Data: 
      * Another core feature of this platform would be storing and extracting the user data. We may store data including Instagram data as well as user preferences within an account, and we will have to decide whether we pass this data through a form or store it in a database. Examples of data we will collect can include time/data the user wants to travel, the amount of money the user wants to spend, the users age etc. 
   * Itinerary Algorithm:
      * We will have to generate an itinerary algorithm that takes in the user preferences and returns the top 3 itineraries recommended to the user. Doing this will require multiple parts
         * 1. Formatting trip/hotel/item data into itineraires:
            * We will have to develop itineraries based on existing trip data and figure out how to build itineraries for a user. There are some itinerary API’s, but I am not sure how robust they are. This means we will have to pull from various travel data API’s and handle that information
         * 2. Recommending/including user preferences:
            * We will also have to figure out how to rank/weigh user preferences when considering the itineraries (ie: if the itinerary is too costly, if it’s within the range that the user wants to travel). 
      * Being able to do this, is the most important, as when we talked to people about this idea, it is clear that the algorithm being able to do the work is the core of the project. 
* Extensions:        
   * Some of the features [not core features] that we could extend include the following
      * View Popular Itineraries 
         * We can cache previous itineraries and create a page that shows popular itineraries and allows users to see it. 
      * More comprehensive matching algorithms
         * We can expand on the matching algorithm to use more data (instagram/social media data) and use other sorts of data (previous trips made, weights of user preferences) to improve our algorithm.
      * The ability to book an itinerary:
         * We can connect our application to other sites to actually book an itinerary, but this would depend on the availability of API’s.

**HTA Approval (crusch):** Approved, but make sure your algorithm is complex and interesting and not just pushing data around.

### Idea 2: Paper FanDuels Project


* Problem: I’ve always thought that sports betting is really cool, but there is no real way to engage in paper sports betting or fun prop-betting (to avoid any issues with actual online Gambling) to try to figure out common trends in sports games or to at least start sports betting with low consequences. Thus, I thought a cool idea would be to create a platform to make sports prop bets without any consequences for fun [especially for people new to Sports betting]. For really new users, I hoped to make a suggestion algorithm that takes a user’s common behavior (or if there is none, just its own decision) and potentially, uses other algorithmic techniques to suggest a bet for the user to make. 


* Summary: We hope to create a platform that can do the following: Support a user to create an account, place virtual currency into their account and engage in fake “paper bets”. The platform would allow you to search on various bets that you can make (taking into account the current time) and make a bet. When the outcome of the game is decided, your profile will be updated to reflect the win/loss of your account. The platform also, to help newbies, would have a suggestions page, which would provide a list of 
bets for the user to make. The suggestions page would be specific to the user, and be generated based on a list of user preferences. 
   * Notice: Given that online betting is currently a form of gambling, the project would not be working with actual money, just pretend paper money/virtual currency. 


* Core Features:
   * Front-End/GUI:
      * The Front-End will be fairly comprehensive, as it will support various features. The core features will be as following: 
         * Creating an Account: This must allow a user to create an account as well as update their account information (user preferences and account balance) 
         * Listing all currently available bets for a user to make, and doing this in a way that is visually appealing (allows the user to filter for specific teams and other information). 
         * A suggestions page: This will be a page for the algorithm to display the suggested bets that a user should make. 
      * Extensions to the front end :
         * Extensions within the GUI can include the following:
            * A dashboard that allows the user to view their account data, a list of their recent bets and possibly a chart that details their success (kind of like RobinHood). This will make the real-time updates obvious to the user. 
   * The difficulty of this will largely lie in the fact that there are a bunch of bets that can be made and that the number of bets changes constantly. The page will have to adapt to live-time data, meaning that the design of the page that lists all available bets will need to be designed well. 
      * However, a good GUI will be key to helping users make correct bets and having fun with the platform 


   * BackEnd:
      * Storing User Data:
         * On the backend, we will have to store user account data, as well as existing bets and their “account balance” so that our site can continue to update this information. 
      * Updating Available Bets
         * We will have to work with some of the existing Odds API’s that are online to get available bets at a given time, and remove bets when they are no longer available. This will be live time updating, so the backend will have to take this into account. 
      * Updating User’s “Bet” Portfolio and Allow them to enter/accrue fake points
         * When a user makes a bet, we will have to eventually store it and then update it when we know if it completed or not. 
      * Suggestions Algorithm: 
         * Using our user data, we will need to generate an algorithm that takes certain information (user’s favorite team, the users preference for risk etc, users sports), to determine the best bets to make.
            * Extensions: 
               * To make this stronger, we can use pre-existing bet data that the user gives us(whether the user succeeded or not) to inform future suggestions, as well as suggesting that a user. This means our suggestions for a user improve our time 
               * We can also improve the algorithm to also suggest the amount of money that a user should bet 
         * The difficulty of this algorithm will be evaluating a given bet for its characteristics (risk, etc) with some sort of numeric value and then ranking them. 
      * The main difficulty of the backend of this project will be dealing with large live-time data. 

**HTA Approval (crusch):** Rejected — algorithm seems under-specified and not complex enough for a 32 term project.

### Idea 3: Day Planner


* Summary: As busy as all of our lives are, it would be nice to have a program that could schedule our days for us, ameliorate some decision fatigue, and help us achieve our long term goals without overwhelming us. This would be a website where users could input things in their schedule that are at set times, and add other things such as homework, exercise, phone calls, meals, or any other thing they want to accomplish in that day, as well as how important each event is to the user (in order to determine priority). 
* Core (Required) Features
   * GUI
      * The GUI will consist of a calendar that is viewable either by day or week, and the calendar’s style will be personalizable based on the user’s preference (e.g. dark mode, colorful, etc.).
      * Below the calendar there will be an “add to calendar” button where the user can input things to their schedule.
      * This is so that the user can see their day in advance or as it is unfolding, and what might be challenging is the JS to create a live-updating calendar that also has things being added and taken away.
   * Inputting Events
      * The user can input events for either the day or the week, and include the scheduled time for each event (or leave that field blank if they want the algorithm to schedule it for them, and instead include the estimated time they want to spend on the event).
      * The user would also designate a priority for each unscheduled event (on a scale from 1 to 5), so that when we create the schedule, we know how important each event is in the case that there are more events than time in the day.
      * Optionally, the user could enter notes for any event.
   * Generating Suggestions
      * If users have holes in their schedules, we could analyze what the user has already inputted and make suggestions based on what else they might want to do (e.g. scan the events work exercise words like “run” or “soccer” and if none are found suggest some sort of exercise). 
      * This might be in that some events do not have broadly related keywords that we can search for, nor do we know what the user is either capable of or interested in (e.g. we wouldn’t say “want to add some meditation?”).
      * On the day of, the user could tweak things as they happen so that the schedule gets updated as the day goes on, and could remind the user of what’s supposed to happen when without overwhelming them. 
   * Live Updating
      * As the day unfolds, the user could check off events if they were successfully completed, reschedule them for either sometime specific that day/week or let us decide when. They could also adjust the time lengths of things as needed, and we would need to alter it in real time.
      * This is a critical feature of the program as this will give the user some sort of accountability/ability to check-in on their progress and adjust things as needed. It might be difficult from a JS/Front end perspective to update the calendar in real time, but rescheduling events will be essentially the same process as adding them in the first place.
* Extendable (potential) features
   * Reminders
      * When it’s time for an event to start, we could create pop-up notifications reminding the user so that they don’t forget!
      * This could be difficult to actually implement, and would likely be hard to ensure functionality on multiple browsers/platforms.

**HTA Approval (crusch):** Approved, but make sure you have a strong core algorithm.

**Mentor TA:** _Put your mentor TA's name and email here once you're assigned one!_

## Meetings
_On your first meeting with your mentor TA, you should plan dates for at least the following meetings:_

**Specs, Mockup, and Design Meeting:** _(Schedule for on or before March 15)_

**4-Way Checkpoint:** _(Schedule for on or before April 5)_

**Adversary Checkpoint:** _(Schedule for on or before April 12 once you are assigned an adversary TA)_

## How to Build and Run
_A necessary part of any README!_
