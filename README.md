dbpersian
=========

DBPersian - Database Persistence Annotations for Android.

Have fun dealing with SQLite database for Android.

DBPersian is a simple and elegant framework to help you to deal with a routine tasks such as database access. This is a pretty thin layer in between SQLite database and Android application, mostly designed to provide a convenient way to serialize/deserialize entities to/from SQLite database based on Java annotations. The primary goal is to keep developer as close to the database as possible while getting rid of some burden and boilerplate code. Also performance is a key consideration for DBPersian, so no reflections and other relatively slow approaches, everything is done via generating code in compile time, so hopefully your code will be as fast as if you would write it by yourself.
