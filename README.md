1. Overview
Sports Day HLD
The Sports Day Event Registration System is a web application designed to facilitate the registration process for various sporting events. It allows users to view available events, register for their chosen events, and manage their selections in an intuitive and user-friendly interface. This system aims to enhance the experience of participants during sports day events by providing a seamless way to interact with event information.
2. Assumptions
● “Select” button is “Register” button. Similarly, “Unselect” button is referred to as “Un-register”.
● Each user is assumed to have a single active session at a time and will not be logged in from
multiple devices simultaneously.
● The events are pre-populated in the system and do not require dynamic creation or management
through an administrative interface.
● Event timings are assumed to be accurate and do not require real-time updates or synchronization
with an external time source.
● The system is assumed to handle a moderate number of concurrent users, with scalability
considered for future growth.
3. Functional Requirements
3.1. User Management
3.1.1 User Registration
● Users should be able to create an account by providing a unique user ID, password and an email address or phone number.
● Users should be able to go to the login page from the account creation page if the user already has one account.
3.1.2 User Login
● Users should be able to login using their unique user ID and password.
● Users should be able to go to the account creation page from the login page if the user
already does not have any account.
3.2. Event Registration
3.2.1 Register for an Event
● Users should be able to register for an event by clicking a "Select" button associated with the event on the left hand side of the screen.
● Users should not be able to register for the same event more than once.
● Users should not be able to register more than 3 events at once.
● Users should not be able to register for events with conflicting timings.
● Users should not be able to register the same event twice.
3.2.2 View Registered Events
● Users should be able to view all the events they registered on the right hand side of the screen.
● The registered events list shall include the same details as the event listing. 3.2.3 Unregister from an Event
● Users should be allowed to unregister from an event by clicking a "Deselect" button associated with the registered event.
3.3. User Interface
3.3.1 Event List Display
● The UI shall display all available events in a card format in the left hand side of the screen.
● Each card shall show the event name, category and timings, along with a button to select the event.
3.3.2 Selected Events Display
● The UI shall display a separate section for the user's registered events in a card format on the right hand side of the screen.
● Each registered event shall include the event name, category, and timings and a button to deselect the event.
3.4. Error Handling
3.4.1 User Feedback
● The system shall provide appropriate error messages for various scenarios, including:
○ Attempting to register for an already registered event.
○ Providing a duplicate user ID during registration.
○ Attempting to log in with an unrecognized user ID.
○ Attempting to create another account with the same email/phone number.
○ Selecting more than 3 events per user
○ Selecting 2 events of overlapping time range.
